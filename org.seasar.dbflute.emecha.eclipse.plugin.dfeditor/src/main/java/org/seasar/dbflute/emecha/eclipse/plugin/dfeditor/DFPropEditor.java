/*
 * Copyright 2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.action.ToggleCommentAction;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.BlockModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.CommentModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropFileModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModelParser;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.FoldingModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.MapEntryModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.MapModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.MultiLineCommentModel;

public class DFPropEditor extends TextEditor {

    private static final String TOGGLE_COMMENT_ACTION = "ToggleCommentAction";
    private static final String PROBLEM_MARKER_KEY = DFEditorActivator.PLUGIN_ID + ".DFPropProblemMarker";
    private DfColorManager colorManager;
    private DFPropOutlinePage outlinePage;
    private ProjectionSupport projectionSupport;
    private DFPropFileModel dfPropModel;
    private ToggleCommentAction.Factory toggleCommentActionFactory;

    public DFPropEditor() {
        super();
        toggleCommentActionFactory = new ToggleCommentAction.Factory();
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.editors.text.TextEditor#dispose()
     */
    public void dispose() {
        super.dispose();
        colorManager.dispose();
        if (outlinePage != null) {
            outlinePage.dispose();
        }
        outlinePage = null;
        dfPropModel = null;
        projectionSupport = null;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        this.colorManager = new DfColorManager();
        setSourceViewerConfiguration(new DFPropFileConfiguration(colorManager, DFEditorActivator.getDefault().getPreferenceStore()));

        super.init(site, input);

    }

    /**
     * {@inheritDoc}
     * Extended to support the folding.
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite, org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    @Override
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        ISourceViewer viewer = new ProjectionViewer(parent, ruler, fOverviewRuler, true, styles);
        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        addProjectionSupport();
        addMatchingCharacterPainter();
    }

    /**
     * Extended to support the folding.
     */
    private void addProjectionSupport() {
        ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
        projectionSupport = new ProjectionSupport(viewer, getAnnotationAccess(), getSharedColors());
        projectionSupport.install();
        viewer.doOperation(ProjectionViewer.TOGGLE);
        updateFolding(getDFModel());
    }

    /**
     * Extended to support the Highlighting of matching parenthesis.
     */
    private void addMatchingCharacterPainter() {
        ITextViewerExtension2 extension = (ITextViewerExtension2) getSourceViewer();
        DefaultCharacterPairMatcher matcher = new DefaultCharacterPairMatcher(new char[] { '(', ')', '{', '}', '[', ']' });
        MatchingCharacterPainter painter = new MatchingCharacterPainter(getSourceViewer(), matcher);
        painter.setColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
        extension.addPainter(painter);
    }

    /**
     * {@inheritDoc}
     * Extended to support the folding.
     * Extended to support the Outline Page.
     * @see org.eclipse.ui.editors.text.TextEditor#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if (IContentOutlinePage.class.equals(adapter)) {
            if (outlinePage == null) {
                outlinePage = new DFPropOutlinePage(this);
            }
            return outlinePage;
        }
        if (projectionSupport != null) {
            Object obj = projectionSupport.getAdapter(getSourceViewer(), adapter);
            if (obj != null) {
                return obj;
            }
        }
        return super.getAdapter(adapter);
    }

    /**
     * {@inheritDoc}
     * Extended to support the toggle comment.
     * @see org.eclipse.ui.editors.text.TextEditor#createActions()
     */
    @Override
    protected void createActions() {
        super.createActions();
        createToggleCommentAction();
    }

    /**
     * Extended to support the toggle comment.
     */
    protected void createToggleCommentAction() {
        ResourceBundle bundle = ResourceBundle.getBundle(DFEditorActivator.class.getPackage().getName() + ".messages");
        ToggleCommentAction action = toggleCommentActionFactory.create(bundle, "ToggleComment.", this);
        action.setActionDefinitionId(DFEditorActivator.PLUGIN_ID + "." + TOGGLE_COMMENT_ACTION);
        setAction(TOGGLE_COMMENT_ACTION, action);
        markAsStateDependentAction(TOGGLE_COMMENT_ACTION, true);
        markAsSelectionDependentAction(TOGGLE_COMMENT_ACTION, true);
        configureToggleCommentAction(action);
    }

    /**
     * Configuration the toggle comment action.
     */
    protected void configureToggleCommentAction(ToggleCommentAction action) {
        ISourceViewer sourceViewer = getSourceViewer();
        SourceViewerConfiguration configuration = getSourceViewerConfiguration();
        action.configure(sourceViewer, configuration);
    }

    /**
     * Initializes the key binding scopes of this editor.
     * Extended to support the toggle comment.
     * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeKeyBindingScopes()
     */
    @Override
    protected void initializeKeyBindingScopes() {
        setKeyBindingScopes(new String[] { DFEditorActivator.PLUGIN_ID + ".DFPropEditorScope" }); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave(IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);
        updateExtensions();
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSaveAs()
     */
    @Override
    public void doSaveAs() {
        super.doSaveAs();
        updateExtensions();
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.texteditor.StatusTextEditor#doRevertToSaved()
     */
    @Override
    public void doRevertToSaved() {
        super.doRevertToSaved();
        updateExtensions();
    }

    /**
     * Update the display extension.
     * Extended to support the folding.
     * Extended to support the Outline Page.
     * Extended to support the problem marker.
     */
    protected void updateExtensions() {
        dfPropModel = createDFModel();

        updateFolding(dfPropModel);
        updateOutlinePage(dfPropModel);
        checkErrors(dfPropModel);
    }

    /**
     * Get Model of parsed DFProp file.
     * @return DFProp File Model
     */
    public DFPropFileModel getDFModel() {
        if (dfPropModel == null) {
            dfPropModel = createDFModel();
        }
        return dfPropModel;
    }

    /**
     * Create DFMapModel for Document.
     * @return Map Property Model Object.
     */
    protected DFPropFileModel createDFModel() {
        IDocument document = getDocumentProvider().getDocument(getEditorInput());
        String name = getEditorInput().getName();
        String source = document.get();
        DFPropFileModel propModel = new DFPropModelParser().parse(source);
        propModel.setFileName(name);
        return propModel;
    }

    /**
     * update the display folding
     */
    private void updateFolding(final DFPropModel dfmodel) {
        ISourceViewer viewer = getSourceViewer();
        if (viewer instanceof ProjectionViewer) {
            ProjectionAnnotationModel annotationModel = ((ProjectionViewer) viewer).getProjectionAnnotationModel();
            Iterator<?> annotations = annotationModel.getAnnotationIterator();
            Map<Integer, ProjectionAnnotation> annotationsMap = new HashMap<Integer, ProjectionAnnotation>();
            while (annotations.hasNext()) {
                Object next = annotations.next();
                if (next instanceof ProjectionAnnotation) {
                    ProjectionAnnotation annotation = (ProjectionAnnotation) next;
                    Position position = annotationModel.getPosition(annotation);
                    if (position != null) {
                        int offset = position.getOffset();
                        annotationsMap.put(offset, annotation);
                    }
                }
            }
            Map<Annotation, Position> additions = applyFolding(annotationModel, dfmodel, annotationsMap);
            annotationModel.modifyAnnotations(annotationsMap.values().toArray(new ProjectionAnnotation[annotationsMap.size()]), additions,new ProjectionAnnotation[0]);
        }
    }

    /**
     * set folding positions.
     * @param annotationModel
     * @param dfmodel
     * @param annotationsMap old annotations.
     */
    private Map<Annotation, Position> applyFolding(ProjectionAnnotationModel annotationModel, DFPropModel dfmodel, Map<Integer, ProjectionAnnotation> annotationsMap) {
        Map<Annotation, Position> retMap = new HashMap<Annotation, Position>();

        DFPropModel[] child = dfmodel.getChild();
        for (DFPropModel model : child) {
            if (model instanceof FoldingModel) {
                FoldingModel foldingModel = (FoldingModel) model;
                if (foldingModel.canFolding()) {
                    int foldingStart = foldingModel.getFoldingStart();
                    int foldingLength = foldingModel.getFoldingLength();
                    if (annotationsMap.containsKey(foldingStart)) {
                        annotationsMap.remove(foldingStart);
                    } else {
                        Position position = new Position(foldingStart, foldingLength);
                        retMap.put(new ProjectionAnnotation(), position);
                    }
                }
            }
            retMap.putAll(applyFolding(annotationModel, model, annotationsMap));
        }
        return retMap;
    }

    /**
     * update the Outline Page.
     */
    private void updateOutlinePage(final DFPropModel dfmodel) {
        if (outlinePage != null) {
            Runnable runnable = new Runnable() {
                public void run() {
                    outlinePage.update(dfmodel);
                }
            };
            runnable.run();
        }
    }

    /**
     * Check Input Error.
     * Extended to support the duplicate Map Entry's check.
     * @param dfmodel DFProp Model
     */
    private void checkErrors(DFPropModel dfmodel) {
        IEditorInput input = getEditorInput();
        if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) input).getFile();
            clearProblemMarkers(file);

            boolean isMapConfig = isMapPropFile(input.getName());
            boolean existsMap = false;
            List<DFPropModel> cache = new ArrayList<DFPropModel>();
            DFPropModel[] topChild = dfmodel.getChild();
            for (DFPropModel child : topChild) {
                if (child instanceof MapModel) {
                    if (existsMap) {
                        createExistsMapMarker(file, (MapModel)child);
                    }
                    checkDuplicateKeys(file, child);
                    existsMap = true;
                } else if (isMapConfig) {
                    if (child instanceof MultiLineCommentModel || child instanceof CommentModel) {
                        if (cache.size() > 0) {
                            DFPropModel first = cache.get(0);
                            DFPropModel last = cache.get(cache.size() - 1);
                            createStatementErrorMarker(file, first.getLineNumber(), first.getOffset(), last.getOffset() + last.getLength());
                            cache = new ArrayList<DFPropModel>();
                        }
                    } else {
                        cache.add(child);
                    }
                }
            }
            if (cache.size() > 0) {
                DFPropModel first = cache.get(0);
                DFPropModel last = cache.get(cache.size() - 1);
                createStatementErrorMarker(file, first.getLineNumber(), first.getOffset(), last.getOffset() + last.getLength());
            }
        }
    }

    /**
     * Clear Problem Markers.
     * @param resource Marker target resource.
     */
    private void clearProblemMarkers(IResource resource) {
        try {
            resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
        } catch (CoreException e) {
            // log.error(e.getMessage(), e);
        }
    }

    private boolean isMapPropFile(String name) {
        if (name == null) {
            return false;
        }
        int extensionIndex = name.lastIndexOf('.');
        if (extensionIndex < 0) {
            return false;
        }
        String configName = name.substring(0, extensionIndex);
        if (configName.endsWith("Map") || configName.endsWith("Map+")) {
            return true;
        }
        String extension = name.substring(extensionIndex + 1);
        if ("diffmap".equals(extension)) {
            return true;
        }
        return false;
    }

    /**
     * Check Map Entry duplicate keys.
     * @param resource Marker target resource.
     * @param dfmodel DFProp Models.
     */
    private void checkDuplicateKeys(IResource resource, DFPropModel dfmodel) {
        if (dfmodel instanceof BlockModel && ((BlockModel) dfmodel).isMissingEndBrace()) {
            createMissingBraceMarker(resource, (BlockModel) dfmodel);
        }
        Set<String> keyNames = new HashSet<String>();
        List<DFPropModel> cache = new ArrayList<DFPropModel>();
        for (DFPropModel element : dfmodel.getChild()) {
            if (element instanceof MapEntryModel) {
                MapEntryModel entry = (MapEntryModel) element;
                if (keyNames.contains(entry.getNameText())) {
                    createDuplicateKeyMarker(resource, entry);
                } else {
                    keyNames.add(entry.getNameText());
                }
            }
            if (dfmodel instanceof MapModel) {
                if (element instanceof MultiLineCommentModel || element instanceof CommentModel || element instanceof MapEntryModel) {
                    if (cache.size() > 0) {
                        DFPropModel first = cache.get(0);
                        DFPropModel last = cache.get(cache.size() - 1);
                        createStatementErrorMarker(resource, first.getLineNumber(), first.getOffset(), last.getOffset() + last.getLength());
                        cache = new ArrayList<DFPropModel>();
                    }
                } else {
                    cache.add(element);
                }
            }
            checkDuplicateKeys(resource, element);
        }
        if (cache.size() > 0) {
            DFPropModel first = cache.get(0);
            DFPropModel last = cache.get(cache.size() - 1);
            createStatementErrorMarker(resource, first.getLineNumber(), first.getOffset(), last.getOffset() + last.getLength());
        }
    }

    /**
     * Create marker of already exists top element  map statement.
     * @param resource Marker target resource.
     * @param model Map model.
     */
    private void createExistsMapMarker(IResource resource, MapModel model) {
        try {
            IMarker marker = resource.createMarker(PROBLEM_MARKER_KEY);
            Map<String, Object> attribute = new HashMap<String, Object>();

            attribute.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            attribute.put(IMarker.MESSAGE, "Already exists map statement.");
            attribute.put(IMarker.LINE_NUMBER, model.getLineNumber());
            attribute.put(IMarker.CHAR_START, model.getOffset());
            attribute.put(IMarker.CHAR_END, model.getOffset() + model.getStartBrace().length());

            marker.setAttributes(attribute);
        } catch (CoreException e) {
            // log.error(e.getMessage(), e);
        }
    }

    /**
     * Create marker of missing end brace.
     * @param resource Marker target resource.
     * @param model Block model.
     */
    private void createMissingBraceMarker(IResource resource, BlockModel model) {
        try {
            IMarker marker = resource.createMarker(PROBLEM_MARKER_KEY);
            Map<String, Object> attribute = new HashMap<String, Object>();

            attribute.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            attribute.put(IMarker.MESSAGE, "Missing end brace.");
            attribute.put(IMarker.LINE_NUMBER, model.getLineNumber());
            attribute.put(IMarker.CHAR_START, model.getOffset());
            attribute.put(IMarker.CHAR_END, model.getOffset() + model.getStartBrace().length());

            marker.setAttributes(attribute);
        } catch (CoreException e) {
            // log.error(e.getMessage(), e);
        }
    }

    /**
     * Create marker of statement error.
     * @param resource Marker target resource.
     * @param lineNumber start line number.
     * @param charStart marker start position.
     * @param charEnd marker end position.
     */
    private void createStatementErrorMarker(IResource resource, int lineNumber, int charStart, int charEnd) {
        try {
            IMarker marker = resource.createMarker(PROBLEM_MARKER_KEY);
            Map<String, Object> attribute = new HashMap<String, Object>();

            attribute.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            attribute.put(IMarker.MESSAGE, "Position of statement is invalid.");
            attribute.put(IMarker.LINE_NUMBER, lineNumber);
            attribute.put(IMarker.CHAR_START, charStart);
            attribute.put(IMarker.CHAR_END, charEnd);

            marker.setAttributes(attribute);
        } catch (CoreException e) {
            // log.error(e.getMessage(), e);
        }
    }

    /**
     * Create marker of duplicate Map Entry error.
     * @param resource Marker target resource.
     * @param model Map Entry model.
     */
    private void createDuplicateKeyMarker(IResource resource, MapEntryModel model) {
        try {
            IMarker marker = resource.createMarker(PROBLEM_MARKER_KEY);
            Map<String, Object> attribute = new HashMap<String, Object>();

            attribute.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            attribute.put(IMarker.MESSAGE, "The value of the key " + model.getNameText() + " is duplicated.");
            attribute.put(IMarker.LINE_NUMBER, model.getLineNumber());
            attribute.put(IMarker.CHAR_START, model.getOffset());
            attribute.put(IMarker.CHAR_END, model.getOffset() + model.getNameText().length());

            marker.setAttributes(attribute);
        } catch (CoreException e) {
            // log.error(e.getMessage(), e);
        }
    }
}
