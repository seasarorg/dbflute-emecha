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

import java.util.ArrayList;
import java.util.List;

/**
 * DFProp model base implementation.
 */
public abstract class AbstractModel implements DFPropModel {

    protected final String _stateName;
    protected DFPropModel _parent;
    protected List<DFPropModel> _child = new ArrayList<DFPropModel>();
    protected int _offset = 0;
    protected int _lineNumber;

    protected AbstractModel(String stateName) {
        _stateName = stateName;
    }

    public void setParent(DFPropModel parent) {
        _parent = parent;
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getParent()
     */
    public DFPropModel getParent() {
        return _parent;
    }

    public void addChild(AbstractModel child) {
        _child.add(child);
        child.setParent(this);
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getChild()
     */
    public DFPropModel[] getChild() {
        if (_child == null || _child.size() == 0) {
            return new DFPropModel[] {};
        }
        return _child.toArray(new DFPropModel[_child.size()]);
    }

    public void setOffset(int offset) {
        _offset = offset;
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getOffset()
     */
    public int getOffset() {
        return _offset;
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getEndPosition()
     */
    public int getEndPosition() {
        return getOffset() + getLength();
    }

    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getLineNumber()
     */
    public int getLineNumber() {
        return _lineNumber;
    }

    protected String getToStringPrefix() {
        return "[" + _stateName + ":" + getOffset() + "]";
    }

    protected String getToStringSuffix() {
        return "[/" + _stateName + ":" + getLength() + "]";
    }
}
