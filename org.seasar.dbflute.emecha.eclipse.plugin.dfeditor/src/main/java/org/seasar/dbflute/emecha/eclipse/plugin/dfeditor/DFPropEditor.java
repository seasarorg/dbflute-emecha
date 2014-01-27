package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.action.ToggleCommentAction;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropFileModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModelParser;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.FoldingModel;

public class DFPropEditor extends TextEditor {

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
        ITextViewerExtension2 extension = (ITextViewerExtension2)getSourceViewer();
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
        action.setActionDefinitionId(DFEditorActivator.PLUGIN_ID + ".ToggleCommentAction");
        setAction("ToggleCommentAction", action);
        markAsStateDependentAction("ToggleCommentAction", true);
        markAsSelectionDependentAction("ToggleCommentAction", true);
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
        setKeyBindingScopes(new String[] { "org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DFPropEditorScope" });  //$NON-NLS-1$
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
     */
    protected void updateExtensions() {
        dfPropModel = createDFModel();

        updateFolding(dfPropModel);
        updateOutlinePage(dfPropModel);
    }

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
                    if(position != null) {
                        int offset = position.getOffset();
                        annotationsMap.put(offset, annotation);
                    }
                }
            }
            Map<Annotation, Position> additions = applyFolding(annotationModel, dfmodel, annotationsMap);
            annotationModel.modifyAnnotations(annotationsMap.values().toArray(new ProjectionAnnotation[annotationsMap.size()]), additions, new ProjectionAnnotation[0]);
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
}
