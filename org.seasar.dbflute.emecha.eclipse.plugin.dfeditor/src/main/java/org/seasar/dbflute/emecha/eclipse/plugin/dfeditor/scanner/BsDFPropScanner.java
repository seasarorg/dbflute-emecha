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

    public BsDFPropScanner(DfColorManager manager, IPreferenceStore store) {
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
        if (ruleList != null) {
            IRule[] rules = ruleList.toArray(new IRule[ruleList.size()]);
            setRules(rules);
        }
    }

    protected abstract List<IRule> createRules();

    /** The content type of the partition in which to resume scanning. */
    protected String fContentType;
    /** The offset of the partition inside which to resume. */
    protected int fPartitionOffset;

    /**
     * @see org.eclipse.jface.text.rules.IPartitionTokenScanner#setPartialRange(org.eclipse.jface.text.IDocument, int, int, java.lang.String, int)
     */
    public void setPartialRange(IDocument document, int offset, int length,
            String contentType, int partitionOffset) {
        fContentType = contentType;
        fPartitionOffset = partitionOffset;
        if (partitionOffset > -1) {
            int delta = offset - partitionOffset;
            if (delta > 0) {
                super.setRange(document, partitionOffset, length + delta);
                fOffset = offset;
                return;
            }
        }
        super.setRange(document, offset, length);

    }

    private Map<DfColor, Token> _tokenMap = new HashMap<DfColor, Token>();

    protected Token getToken(DfColor colorType) {
        Token token = _tokenMap.get(colorType);
        if (token == null) {
            token = new Token(createTextAttribute(colorType));
            _tokenMap.put(colorType, token);
        }
        return token;
    }

    private TextAttribute createTextAttribute(DfColor fontManager) {
        Color fore = colorManager.getColor(fontManager.getForeground());
        Color back = fontManager.getBackground() == null ? null : colorManager.getColor(fontManager.getBackground());
        int style = fontManager.getStyle();
        return new TextAttribute(fore, back, style);
    }

}
