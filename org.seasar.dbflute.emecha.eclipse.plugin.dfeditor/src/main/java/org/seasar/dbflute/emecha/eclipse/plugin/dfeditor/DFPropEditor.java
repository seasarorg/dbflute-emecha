package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

public class DFPropEditor extends AbstractDecoratedTextEditor {

	private DfColorManager colorManager;

	public DFPropEditor() {
		super();
		this.colorManager = new DfColorManager();
		setSourceViewerConfiguration(new DFPropFileConfiguration(colorManager,DFEditorActivator.getDefault().getPreferenceStore()));
		setDocumentProvider(new DFPropDocumentProvider());
	}

	public void dispose() {
		super.dispose();
		colorManager.dispose();
	}

}
