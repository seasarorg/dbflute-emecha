/*
 * Copyright 2013 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.action;

import org.eclipse.core.resources.IFile;


/**
 * SQLからParameterBeanを開くアクション
 * @author schatten
 */
public class OpenParameterBeanAction extends AbstractOpenActionBase {

    /**
     *
     */
    public OpenParameterBeanAction() {
        super();
    }

    @Override
    protected String getTargetClassName(IFile file) {
        return getSqlName(file) + "Pmb";
    }

    @Override
    protected String getTargetPackageName(String packageName) {
        // TODO 自動生成されたメソッド・スタブ
        return packageName + ".exbhv.pmbean";
    }

}
