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
 * Multi line comment model.
 * <p>
 *  // Comment line
 *  // Comment line
 *  // Comment line
 * </p>
 */
public class MultiLineCommentModel extends DefaultModel implements FoldingModel {
    protected int _length = 0;

    public MultiLineCommentModel() {
        super("MultiLineComment");
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.AbstractModel#addChild(org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.AbstractModel)
     */
    public void addChild(CommentModel child) {
        super.addChild(child);
    }

    /**
     * Set element length.
     * @param length element length.
     */
    public void setLength(int length) {
        _length = length;
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getLength()
     */
    public int getLength() {
        return _length;
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.FoldingModel#canFolding()
     */
    public boolean canFolding() {
        return getChild().length > 1;
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.FoldingModel#getFoldingStart()
     */
    public int getFoldingStart() {
        if (!canFolding()) {
            throw new UnsupportedOperationException("Can not folding.");
        }
        return getOffset();
    }

    /**
     * {@inheritDoc}
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.FoldingModel#getFoldingLength()
     */
    public int getFoldingLength() {
        return getLength();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getToStringPrefix());
        String separator = "\n";
        for (DFPropModel child : getChild()) {
            str.append(separator);
            str.append(child.toString());
        }
        str.append(separator);
        str.append(getToStringSuffix());
        return str.toString();
    }
}
