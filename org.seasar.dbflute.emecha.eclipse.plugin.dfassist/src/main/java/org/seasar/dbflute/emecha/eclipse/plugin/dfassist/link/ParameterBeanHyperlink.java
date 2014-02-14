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
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.link;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.StructuredSelection;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.DfAssistPlugin;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.nls.Messages;

/**
 * Hyperlink to ParameterBean.
 * @author schatten
 */
public class ParameterBeanHyperlink implements IHyperlink {
    private final IRegion fRegion;
    private final SelectionDispatchAction fOpenAction;
    private final IType fElement;
    @SuppressWarnings("unused")
    private final boolean fQualify;
    private IType pmbType = null;

    public ParameterBeanHyperlink(IRegion region, SelectionDispatchAction openAction, IType element, boolean qualify) {
        Assert.isNotNull(openAction);
        Assert.isNotNull(region);

        fRegion = region;
        fOpenAction = openAction;
        fElement = element;
        fQualify = qualify;
        try {
            pmbType = getPmbClassType();
        } catch (JavaModelException e) {
            DfAssistPlugin.log(e);
        }
    }

    public IRegion getHyperlinkRegion() {
        return fRegion;
    }

    public String getTypeLabel() {
        return null;
    }

    public String getHyperlinkText() {
        return Messages.HYPERLINK_PARAMETER_BEAN;
    }

    public void open() {
        if (existPmbType()) {
            fOpenAction.run(new StructuredSelection(pmbType));
        }
    }

    /**
     * ParameterBeanが存在するか判定する。
     * @return 対になるParameterBeanが存在する場合に<code>true</code>
     */
    public boolean existPmbType() {
        return pmbType != null && pmbType.exists();
    }

    private IType getPmbClassType() throws JavaModelException {
        String packageName = fElement.getPackageFragment().getElementName();
        String typeQualifiedName = fElement.getTypeQualifiedName();
        if (packageName.endsWith(".exentity.customize")) {
            packageName = packageName.substring(0, packageName.indexOf(".exentity.customize")) + ".exbhv.pmbean";
        }
        if (packageName.endsWith(".bsentity.customize")) {
            packageName = packageName.substring(0, packageName.indexOf(".bsentity.customize")) + ".exbhv.pmbean";
            if (typeQualifiedName.startsWith("Bs")) {
                typeQualifiedName = typeQualifiedName.substring("Bs".length());
            }
        }
        typeQualifiedName = typeQualifiedName + "Pmb";
        IType findType = fElement.getJavaProject().findType(packageName, typeQualifiedName, (IProgressMonitor) null);
        return findType;
    }

}
