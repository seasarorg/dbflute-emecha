package org.seaser.dbflute.emecha.eclipse.plugin.dfeditor.rule;

import org.eclipse.jface.text.rules.*;

public class TagRule extends PatternRule {

	public TagRule(IToken token) {
		super("{", "}", token, '{', false, false, true  );
	}

}
