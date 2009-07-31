/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColor;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColorManager;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.rule.CombinedWordRule;

/**
 * @author masa
 *
 */
@Deprecated
public class DfTagScanner extends BsDFPropScanner {

	public static class DfWordDetector implements IWordDetector {

		public boolean isWordPart(char c) {
			return false;
		}

		public boolean isWordStart(char c) {
			return false;
		}

	}
	/**
	 * @param manager
	 * @param store
	 */
	public DfTagScanner(DfColorManager manager, IPreferenceStore store) {
		super(manager, store);
	}

	/* (非 Javadoc)
	 * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner.BsDFPropScanner#createRules()
	 */
	@Override
	protected List<IRule> createRules() {
		List<IRule> list = new ArrayList<IRule>();
//		CombinedWordRule wordRule = new CombinedWordRule(new DfWordDetector());
//		CombinedWordRule.WordMatcher wordMatcher = new CombinedWordRule.WordMatcher();
//		wordMatcher.addWord("map:", getToken(DfColor.MAP));
//		wordMatcher.addWord("list:", getToken(DfColor.LIST));
//
//		list.add(wordRule);
		return list;
	}

}
