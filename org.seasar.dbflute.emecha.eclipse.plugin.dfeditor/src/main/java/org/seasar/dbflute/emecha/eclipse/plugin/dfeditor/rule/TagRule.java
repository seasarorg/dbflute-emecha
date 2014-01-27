package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.rule;

import org.eclipse.jface.text.rules.*;

@Deprecated
public class TagRule extends PatternRule {

	public TagRule(IToken token) {
		super("{", "}", token, '{', false, false, true  );
	}

}
