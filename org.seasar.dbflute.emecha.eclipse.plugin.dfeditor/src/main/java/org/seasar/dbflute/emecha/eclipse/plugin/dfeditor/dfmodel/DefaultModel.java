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
 * Default model.
 * space, blank element, ...
 */
public class DefaultModel extends AbstractModel {

    protected String _input;

    public DefaultModel() {
        this("Default");
    }

    protected DefaultModel(String stateName) {
        super(stateName);
    }

    public void setInput(String input) {
        this._input = input;
    }

    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.DFPropModel#getLength()
     */
    public int getLength() {
        return _input.length();
    }

}