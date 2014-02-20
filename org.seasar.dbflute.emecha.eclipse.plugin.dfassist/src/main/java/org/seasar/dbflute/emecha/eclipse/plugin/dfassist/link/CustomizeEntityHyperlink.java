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
 * Hyperlink to CustomizeEntity.
 * @author schatten
 */
public class CustomizeEntityHyperlink implements IHyperlink {
    private final IRegion fRegion;
    private final SelectionDispatchAction fOpenAction;
    private final IType fElement;
    @SuppressWarnings("unused")
    private final boolean fQualify;
    private IType entityType = null;

    public CustomizeEntityHyperlink(IRegion region, SelectionDispatchAction openAction, IType element, boolean qualify) {
        Assert.isNotNull(openAction);
        Assert.isNotNull(region);

        fRegion = region;
        fOpenAction = openAction;
        fElement = element;
        fQualify = qualify;
        try {
            entityType = getEntityClassType();
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
        return Messages.HYPERLINK_CUSTOMIZE_ENTITY;
    }

    public void open() {
        if (existEntityType()) {
            fOpenAction.run(new StructuredSelection(entityType));
        }
    }

    /**
     * CustomizeEntityが存在するか判定する。
     * @return 対になるCustomizeEntityが存在する場合に<code>true</code>
     */
    public boolean existEntityType() {
        return entityType != null && entityType.exists();
    }

    private IType getEntityClassType() throws JavaModelException {
        String packageName = fElement.getPackageFragment().getElementName();
        String typeQualifiedName = fElement.getTypeQualifiedName();
        if (packageName.endsWith(".exbhv.pmbean")) {
            packageName = packageName.substring(0, packageName.indexOf(".exbhv.pmbean")) + ".exentity.customize";
        }
        if (packageName.endsWith(".bsbhv.pmbean")) {
            packageName = packageName.substring(0, packageName.indexOf(".bsbhv.pmbean")) + ".exentity.customize";
            if (typeQualifiedName.startsWith("Bs")) {
                typeQualifiedName = typeQualifiedName.substring("Bs".length());
            }
        }
        if (typeQualifiedName.endsWith("Pmb")) {
            typeQualifiedName = typeQualifiedName.substring(0, typeQualifiedName.indexOf("Pmb"));
        }
        return fElement.getJavaProject().findType(packageName, typeQualifiedName, (IProgressMonitor) null);
    }

}
