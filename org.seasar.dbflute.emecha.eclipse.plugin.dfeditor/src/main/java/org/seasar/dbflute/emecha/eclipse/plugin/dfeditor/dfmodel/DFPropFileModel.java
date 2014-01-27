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
public class DFPropFileModel extends AbstractModel implements NamedModel {

    protected String _fileName;

    public DFPropFileModel() {
        super("DFPropFile");
    }

    public void setFileName(String name) {
        _fileName = name;
    }
    /**
     * @see org.seasar.dbflute.emecha.eclipse.plugin.dfeditor.dfmodel.NamedModel#getNameText()
     */
    public String getNameText() {
        return _fileName;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[").append(_stateName).append("]");
        String separator = "\n";
        for (DFPropModel child : _child) {
            str.append(separator);
            str.append(child.toString());
        }
        str.append(separator);
        str.append("[/").append(_stateName).append("]");
        return  str.toString();
    }
}
