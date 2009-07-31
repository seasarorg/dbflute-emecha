package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DFPropPartitions;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.rule.TagRule;
/**
 * dfpropのパーティショニングを行うクラス。
 */
public class DFPropPartitionScanner extends RuleBasedPartitionScanner implements DFPropPartitions{

	public DFPropPartitionScanner() {

		IToken dfpComment = new Token(DFP_COMMENT);
		IToken tagPartition = new Token(DFP_PARTITIONING);

		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

		rules.add(new EndOfLineRule("#", dfpComment));
		rules.add(new TagRule(tagPartition));

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

}
