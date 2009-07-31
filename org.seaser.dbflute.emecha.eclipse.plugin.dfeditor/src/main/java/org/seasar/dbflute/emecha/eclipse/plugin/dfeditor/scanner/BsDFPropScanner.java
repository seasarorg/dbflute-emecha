package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColor;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColorManager;

// TODO
public abstract class BsDFPropScanner extends BufferedRuleBasedScanner implements IPartitionTokenScanner {

	private DfColorManager colorManager;
	private IPreferenceStore preferenceStore;

	public BsDFPropScanner(DfColorManager manager, IPreferenceStore store){
		super();
		this.colorManager = manager;
		this.preferenceStore = store;
		initialize();
	}

	public void initialize() {
		// TODO Editor Color & decorate Setting
		initializeRules();
	}

	private void initializeRules() {
		List<IRule> ruleList = createRules();
		if ( ruleList != null ) {
			IRule[] rules = ruleList.toArray(new IRule[ruleList.size()]);
			setRules(rules);
		}
	}

	protected abstract List<IRule> createRules();

	/** The content type of the partition in which to resume scanning. */
	protected String fContentType;
	/** The offset of the partition inside which to resume. */
	protected int fPartitionOffset;
	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		fContentType= contentType;
		fPartitionOffset= partitionOffset;
		if (partitionOffset > -1) {
			int delta= offset - partitionOffset;
			if (delta > 0) {
				super.setRange(document, partitionOffset, length + delta);
				fOffset= offset;
				return;
			}
		}
		super.setRange(document, offset, length);

	}

	private Map<DfColor,Token> _tokenMap = new HashMap<DfColor, Token>();
	protected Token getToken(DfColor colorType) {
		Token token = _tokenMap.get(colorType);
		if ( token == null ) {
			token = new Token(createTextAttribute(colorType));
			_tokenMap.put(colorType, token);
		}
		return token;
	}
	private TextAttribute createTextAttribute(DfColor fontManager) {
		Color fore = colorManager.getColor(fontManager.getForeground());
		Color back = fontManager.getBackground() == null ? null : colorManager.getColor(fontManager.getBackground());
		int style = fontManager.getStyle();
		return new TextAttribute(fore,back,style);
	}

//	protected Color getDefaultColor() {
//		return colorManager.getColor(DFPropColorConstants.DEFAULT);
//	}
//	protected Color getCommentColor() {
//		if ( preferenceStore == null ) {
//			return colorManager.getColor(DFPropColorConstants.DFP_COMMENT);
//		}
//		// TODO get prefarence
//		return colorManager.getColor(DFPropColorConstants.DFP_COMMENT);
//	}
//-----------------------------------------------------------------
// Getter/Setter
//-----------------------------------------------------------------


//	protected ColorManager getColorManager() {
//		return manager;
//	}
//
//	protected void setColorManager(ColorManager manager) {
//		this.manager = manager;
//	}
//
//	protected IPreferenceStore getPreferenceStore() {
//		return store;
//	}
//
//	protected void setPreferenceStore(IPreferenceStore store) {
//		this.store = store;
//	}

}
