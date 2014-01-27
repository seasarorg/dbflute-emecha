package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.scanner;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColor;
import org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.DfColorManager;

public class DFPropCommentScanner extends BsDFPropScanner {

    public DFPropCommentScanner(DfColorManager manager, IPreferenceStore store ){
        super(manager,store);
    }

    @Override
    protected List<IRule> createRules() {
        ArrayList<IRule> rules = new ArrayList<IRule>();

        rules.add(new EndOfLineRule("#",getToken(DfColor.LINE_COMMENT)));

        return rules;
    }

}
