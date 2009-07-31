package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.action.XMLDoubleClickStrategy;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.BsDFPropScanner;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.DFPropCommentScanner;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.DefaultTokenScanner;
// TODO
public class DFPropFileConfiguration extends TextSourceViewerConfiguration implements DFPropPartitions {

	private XMLDoubleClickStrategy doubleClickStrategy;

	private BsDFPropScanner commentScanner;
	private BsDFPropScanner defaultScanner;
//	private BsDFPropScanner tagScanner;
	private DfColorManager colorManager;

	public DFPropFileConfiguration(DfColorManager colorManager, IPreferenceStore preferenceStore) {
		super(preferenceStore);
		this.colorManager = colorManager;
		if ( preferenceStore != null ) {
		    // TODO TAG WHIDTH SETTING
		    preferenceStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 4);
		}
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			DFP_PARTITIONING,
			DFP_COMMENT
			};
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new XMLDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected BsDFPropScanner getCommentScanner() {
		if ( commentScanner == null ) {
			commentScanner = new DFPropCommentScanner(colorManager,fPreferenceStore);
		}
		return commentScanner;
	}

	protected BsDFPropScanner getDefaultScanner() {
		if ( defaultScanner == null )
			defaultScanner = new DefaultTokenScanner(colorManager,fPreferenceStore);
		return defaultScanner;
	}

//	protected BsDFPropScanner getTagScanner() {
//		if ( tagScanner == null ) {
//			tagScanner = new DfTagScanner(colorManager,fPreferenceStore);
//		}
//		return tagScanner;
//	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, DFP_COMMENT);
		reconciler.setRepairer(dr, DFP_COMMENT);

		dr = new DefaultDamagerRepairer(getCommentScanner());
		reconciler.setDamager(dr, DFP_PARTITIONING);
		reconciler.setRepairer(dr, DFP_PARTITIONING);

		dr = new DefaultDamagerRepairer(getDefaultScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

//		dr = new DefaultDamagerRepairer(getTagScanner());
//		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
//		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

//		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
//				new TextAttribute(colorManager.getColor(DfColor.COMMENT.getForeground())));
//		reconciler.setDamager(ndr, DFP_COMMENT);
//		reconciler.setRepairer(ndr, DFP_COMMENT);

		return reconciler;
	}

}