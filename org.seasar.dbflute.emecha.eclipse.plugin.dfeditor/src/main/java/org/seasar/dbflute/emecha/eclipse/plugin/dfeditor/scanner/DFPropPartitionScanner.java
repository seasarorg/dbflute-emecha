package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DFPropPartitions;

/**
 * dfpropのパーティショニングを行うクラス。
 */
public class DFPropPartitionScanner extends RuleBasedPartitionScanner implements DFPropPartitions{

	public DFPropPartitionScanner() {

		IToken dfpComment = new Token(DFP_COMMENT);
		IToken tagPartition = new Token(DFP_PARTITIONING);

		List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

        rules.add(new SingleLineRule("/*", "*/", tagPartition));
        rules.add(new SingleLineRule("\"", "\"", tagPartition));
        rules.add(new SingleLineRule("'", "'", tagPartition));

		rules.add(new EndOfLineRule("#", dfpComment));

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

}
