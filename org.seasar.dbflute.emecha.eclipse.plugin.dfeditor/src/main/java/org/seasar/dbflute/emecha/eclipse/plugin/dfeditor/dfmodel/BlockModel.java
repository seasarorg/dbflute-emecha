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
package org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel;

/**
 *
 */
public class BlockModel extends AbstractModel implements FoldingModel {

    protected static final String DEFAULT_START_BRACE = "{";
    protected static final String DEFAULT_END_BRACE = "}";
    protected String _startBrace = DEFAULT_START_BRACE;
    protected String _endBrace = DEFAULT_END_BRACE;
    protected int _length = 0;

    protected BlockModel(String stateName) {
        super(stateName);
    }

    public void addLineCount() {
        _lineFeedCount++;
    }
    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.FoldingModel#getLineFeedCount()
     */
    public int getLineFeedCount() {
        return _lineFeedCount;
    }

    public void setLength(int length) {
        _length = length;
    }
    public boolean canFolding() {
        return _lineFeedCount > 0;
    }

    public int getFoldingStart() {
        if (!canFolding()) {
            throw new UnsupportedOperationException("Can not folding.");
        }
        if (getParent() instanceof MapEntryModel) {
            return getParent().getOffset();
        }
        return getOffset();
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getLength()
     */
    public int getLength() {
        return _length;
    }
    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.FoldingModel#getFoldingLength()
     */
    public int getFoldingLength() {
        if (getParent() instanceof MapEntryModel) {
            return this.getOffset() - getParent().getOffset() + getLength();
        }
        return getLength();
    }
    public int getFoldingEnd() {
        if (!canFolding()) {
            throw new UnsupportedOperationException("Can not folding.");
        }
        return getOffset() + getLength() - getEndBrace().length();
    }

    public void setStartBrace(String startBrace) {
        _startBrace = startBrace;
    }
    public String getStartBrace() {
        return _startBrace;
    }
    public void setEndBrace(String endBrace) {
        _endBrace = endBrace;
    }
    public String getEndBrace() {
        return _endBrace;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getToStringPrefix());
        str.append(getStartBrace());
        String separator = "\n";
        for (DFPropModel child : _child) {
            str.append(separator);
            str.append(child.toString());
        }
        str.append(separator);
        str.append(getEndBrace());
        str.append(getToStringSuffix());
        return  str.toString();
    }

}
