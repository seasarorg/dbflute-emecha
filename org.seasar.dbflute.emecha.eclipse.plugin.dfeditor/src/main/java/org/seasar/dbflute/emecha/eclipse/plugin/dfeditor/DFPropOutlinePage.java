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
import java.util.List;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropFileModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.ListModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.MapModel;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.NamedModel;

/**
 * DFProp Outline Page
 */
public class DFPropOutlinePage extends ContentOutlinePage {

    private DFPropInput input = new DFPropInput();
    private DFPropEditor editor;

    private static class DFPropInput {
        DFPropModel dfmodel;
    }

    public DFPropOutlinePage(DFPropEditor dfPropEditor) {
        this.editor = dfPropEditor;
        this.input.dfmodel = dfPropEditor.getDFModel();
    }

    public void update(DFPropModel dfmodel) {
        input.dfmodel = dfmodel;
        TreeViewer viewer = getTreeViewer();
        viewer.refresh();
        viewer.expandToLevel(viewer.getInput(), 2);
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        TreeViewer viewer = getTreeViewer();

        viewer.setContentProvider(getContentProvider());
        viewer.setLabelProvider(getLabelProvider());
        viewer.addSelectionChangedListener(getSelectionChangedListener());
        viewer.setAutoExpandLevel(2);
        viewer.setInput(input);
    }

    private ISelectionChangedListener getSelectionChangedListener() {
        return new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                Object firstElement = ((TreeSelection) event.getSelection()).getFirstElement();
                if (firstElement instanceof DFPropFileModel) {
                    // nothing
                } else if (firstElement instanceof MapModel) {
                    MapModel mapModel = (MapModel) firstElement;
                    editor.selectAndReveal(mapModel.getOffset(), mapModel.getStartBrace().length());
                } else if (firstElement instanceof NamedModel) {
                    NamedModel namedModel = (NamedModel) firstElement;
                    editor.selectAndReveal(namedModel.getOffset(), namedModel.getNameText().length());
                }
            }
        };
    }

    private IContentProvider getContentProvider() {
        return new ITreeContentProvider() {

            /**
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
             */
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                // Do nothing
            }

            /**
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            public void dispose() {
                // Do nothing
            }

            /**
             * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
             */
            public boolean hasChildren(Object element) {
                if (element instanceof DFPropModel) {
                    DFPropModel model = (DFPropModel) element;
                    return hasLabelModel(model);
                }
                return false;
            }

            private boolean hasLabelModel(DFPropModel element) {
                DFPropModel[] child = element.getChild();
                for (DFPropModel model : child) {
                    if (model instanceof NamedModel) {
                        return true;
                    }
                    if (model instanceof ListModel) {
                        DFPropModel[] listChild = model.getChild();
                        for (DFPropModel listElement : listChild) {
                            if (listElement instanceof MapModel) {
                                return true;
                            }
                        }
                    }
                    if (model instanceof MapModel && model.getParent() instanceof ListModel) {
                        return true;
                    }
                    if (hasLabelModel(model)) {
                        return true;
                    }
                }
                return false;
            }

            /**
             * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
             */
            public Object getParent(Object element) {
                if (element instanceof DFPropModel) {
                    return getParentLabelModel((DFPropModel) element);
                }
                return null;
            }

            private DFPropModel getParentLabelModel(DFPropModel element) {
                if (element == null) {
                    return null;
                }
                DFPropModel parent = element.getParent();
                if (parent == null) {
                    return null;
                }
                if (parent instanceof MapModel && parent.getParent() instanceof ListModel) {
                    return parent;
                }
                if (parent instanceof NamedModel) {
                    return parent;
                }
                return getParentLabelModel(parent);
            }

            /**
             * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(java.lang.Object)
             */
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof DFPropInput) {
                    return new DFPropModel[] { ((DFPropInput) inputElement).dfmodel };
                }
                if (inputElement instanceof DFPropModel) {
                    return new DFPropModel[] { (DFPropModel) inputElement };
                }
                return new Object[0];
            }

            /**
             * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
             */
            public Object[] getChildren(Object parentElement) {
                if (parentElement instanceof DFPropModel) {
                    return getLabelChildren((DFPropModel) parentElement).toArray();
                }
                return new Object[0];
            }

            private List<DFPropModel> getLabelChildren(DFPropModel element) {
                List<DFPropModel> list = new ArrayList<DFPropModel>();
                DFPropModel[] child = element.getChild();
                for (DFPropModel model : child) {
                    if (model instanceof NamedModel) {
                        list.add(model);
                        continue;
                    }
                    if (model instanceof ListModel) {
                        DFPropModel[] listChild = model.getChild();
                        for (DFPropModel listElement : listChild) {
                            if (listElement instanceof MapModel) {
                                list.add(listElement);
                            }
                        }
                        continue;
                    }
                    list.addAll(getLabelChildren(model));
                }
                return list;
            }

        };
    }

    private IBaseLabelProvider getLabelProvider() {
        return new LabelProvider() {
            /**
             * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
             */
            @Override
            public Image getImage(Object element) {
                if (element instanceof DFPropFileModel) {
                    return DFEditorActivator.getImageDescriptor("icons/dfeditor2.gif").createImage();
                }
                if (element instanceof MapModel) {
                    return DFEditorActivator.getImageDescriptor("icons/list_map.gif").createImage();
                }
                if (element instanceof NamedModel) {
                    return DFEditorActivator.getImageDescriptor("icons/property.gif").createImage();
                }
                return null;
            }

            /**
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            @Override
            public String getText(Object element) {
                if (element instanceof MapModel && ((MapModel) element).getParent() instanceof ListModel) {
                    return "map:";
                }
                if (element instanceof NamedModel) {
                    return ((NamedModel) element).getNameText();
                }
                return super.getText(element);
            }
        };
    }
}
