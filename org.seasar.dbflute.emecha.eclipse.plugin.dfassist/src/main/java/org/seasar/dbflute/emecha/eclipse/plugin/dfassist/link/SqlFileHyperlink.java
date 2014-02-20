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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJarEntryResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.StructuredSelection;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.DfAssistPlugin;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.nls.Messages;

/**
 * Java code Hyperlink to SQL.
 * @author schatten
 */
public class SqlFileHyperlink implements IHyperlink {
    private final IRegion fRegion;
    private final SelectionDispatchAction fOpenAction;
    private final boolean fQualify;
    private IStorage sqlFile;

    public SqlFileHyperlink(IRegion region, SelectionDispatchAction openAction, IType element, boolean qualify) {
        Assert.isNotNull(openAction);
        Assert.isNotNull(region);

        fRegion = region;
        fOpenAction = openAction;
        fQualify = qualify;
        try {
            sqlFile = findSqlResource(element);
            if (sqlFile == null) {
                String packageName = element.getPackageFragment().getElementName();
                String typeQualifiedName = element.getTypeQualifiedName();
                if (packageName.endsWith(".exbhv.pmbean")) {
                    packageName = packageName.substring(0, packageName.indexOf(".pmbean"));
                    if (typeQualifiedName.endsWith("Pmb")) {
                        typeQualifiedName = typeQualifiedName.substring(0, typeQualifiedName.indexOf("Pmb"));
                    }
                } else if (packageName.endsWith(".bsbhv.pmbean")) {
                    packageName = packageName.substring(0, packageName.indexOf(".bsbhv.pmbean")) + ".exbhv";
                    if (typeQualifiedName.startsWith("Bs")) {
                        typeQualifiedName = typeQualifiedName.substring("Bs".length());
                    }
                    if (typeQualifiedName.endsWith("Pmb")) {
                        typeQualifiedName = typeQualifiedName.substring(0, typeQualifiedName.indexOf("Pmb"));
                    }
                } else if (packageName.endsWith(".exentity.customize")) {
                    packageName = packageName.substring(0, packageName.indexOf(".exentity.customize")) + ".exbhv";
                } else if (packageName.endsWith(".bsentity.customize")) {
                    packageName = packageName.substring(0, packageName.indexOf(".bsentity.customize")) + ".exbhv";
                    if (typeQualifiedName.startsWith("Bs")) {
                        typeQualifiedName = typeQualifiedName.substring("Bs".length());
                    }
                }
                sqlFile = getSqlResource(element, packageName, typeQualifiedName);
            }
        } catch (CoreException e) {
            DfAssistPlugin.log(e);
        }
    }

    public SqlFileHyperlink(IRegion region, SelectionDispatchAction openAction, IField element, boolean qualify) {
        Assert.isNotNull(openAction);
        Assert.isNotNull(region);

        fRegion = region;
        fOpenAction = openAction;
        fQualify = qualify;
        try {
            IType parentElement = (IType) element.getParent();
            String packageName = parentElement.getPackageFragment().getElementName();
            if (packageName.endsWith("bsbhv")) {
                packageName = packageName.substring(0, packageName.lastIndexOf('.')) + ".exbhv";
            }
            String source = (String) parentElement.getField(element.getElementName()).getConstant();
            source = source.substring(source.indexOf('"') + 1, source.lastIndexOf('"')); //$NON-NLS-1$ //$NON-NLS-2$
            String[] splitPath = source.split(":");
            int length = splitPath.length - 1;
            for (int i = 0; i < length; i++) {
                packageName += "." + splitPath[i]; //$NON-NLS-1$
            }
            String typeQualifiedName = splitPath[length];

            sqlFile = getSqlResource(parentElement, packageName, typeQualifiedName);
        } catch (CoreException e) {
            DfAssistPlugin.log(e);
        }
    }

    public boolean isQualify() {
        return fQualify;
    }

    public IRegion getHyperlinkRegion() {
        return fRegion;
    }

    public String getTypeLabel() {
        return null;
    }

    public String getHyperlinkText() {
        return Messages.HYPERLINK_SQL_FILE;
    }

    public void open() {
        if (existSqlFile()) {
            fOpenAction.run(new StructuredSelection(sqlFile));
        }
    }

    public boolean existSqlFile() {
        return sqlFile != null;
    }

    private IStorage findSqlResource(IType type) throws CoreException {
        IType bsPmb = null;
        String packageName = type.getPackageFragment().getElementName();
        String typeQualifiedName = type.getTypeQualifiedName();
        if (packageName.endsWith(".exbhv.pmbean")) {
            String bsPackageName = packageName.substring(0, packageName.indexOf(".exbhv.pmbean")) + ".bsbhv.pmbean";
            packageName = packageName.substring(0, packageName.indexOf(".pmbean"));
            String superclassTypeSignature = type.getSuperclassName();
            bsPmb = type.getJavaProject().findType(bsPackageName, superclassTypeSignature, (IProgressMonitor) null);
        } else if (packageName.endsWith(".bsbhv.pmbean")) {
            packageName = packageName.substring(0, packageName.indexOf(".bsbhv.pmbean")) + ".exbhv";
            bsPmb = type;
        } else if (packageName.endsWith(".exentity.customize")) {
            String bsPackageName = packageName.substring(0, packageName.indexOf(".exentity.customize")) + ".bsbhv.pmbean";
            packageName = packageName.substring(0, packageName.indexOf(".exentity.customize")) + ".exbhv";
            String superclassTypeName = "Bs" + typeQualifiedName + "Pmb";
            bsPmb = type.getJavaProject().findType(bsPackageName, superclassTypeName, (IProgressMonitor) null);
        } else if (packageName.endsWith(".bsentity.customize")) {
            String bsPackageName = packageName.substring(0, packageName.indexOf(".bsentity.customize")) + ".bsbhv.pmbean";
            packageName = packageName.substring(0, packageName.indexOf(".bsentity.customize")) + ".exbhv";
            String superclassTypeName = typeQualifiedName + "Pmb";
            bsPmb = type.getJavaProject().findType(bsPackageName, superclassTypeName, (IProgressMonitor) null);
        }
        if (bsPmb == null) {
            return null;
        }
        IMethod method = bsPmb.getMethod("getOutsideSqlPath", (String[]) null); //$NON-NLS-1$
        if (method == null) {
            return null;
        }
        String source = method.getSource();
        String path = source.substring(source.indexOf('"') + 1, source.lastIndexOf('"')); //$NON-NLS-1$ //$NON-NLS-2$
        if (path == null || path.trim().length() == 0) {
            return null;
        }
        String[] splitPath = path.split(":"); //$NON-NLS-1$
        int length = splitPath.length - 1;
        for (int i = 0; i < length; i++) {
            packageName += "." + splitPath[i]; //$NON-NLS-1$
        }

        return getSqlResource(type, packageName, "_" + splitPath[length]); //$NON-NLS-1$
    }

    private IStorage getSqlResource(IType type, String packageName, String typeQualifiedName) throws CoreException {
        IPackageFragmentRoot[] allPackageFragmentRoots = type.getJavaProject().getPackageFragmentRoots();
        for (IPackageFragmentRoot root : allPackageFragmentRoots) {
            IPackageFragment packageFragment = root.getPackageFragment(packageName);
            if (!packageFragment.exists()) {
                continue;
            }
            IStorage sqlResource = searchPackageResource(packageFragment, typeQualifiedName);
            if (sqlResource != null) {
                return sqlResource;
            }
        }
        return null;
    }

    private IStorage searchPackageResource(IPackageFragment packageFragment, String typeQualifiedName) throws CoreException {
        Object[] nonJavaResources = packageFragment.getNonJavaResources();
        IStorage sqlResource = searchSqlResource(typeQualifiedName, nonJavaResources);
        if (sqlResource != null) {
            return sqlResource;
        }
        IJavaElement[] children = packageFragment.getChildren();
        for (IJavaElement element : children) {
            if (element instanceof IPackageFragment) {
                sqlResource = searchPackageResource((IPackageFragment) element, typeQualifiedName);
                if (sqlResource != null)
                    return sqlResource;
            }
        }
        return null;
    }

    private IStorage searchSqlResource(String typeQualifiedName, Object[] nonJavaResources) throws CoreException {
        for (Object object : nonJavaResources) {
            if (object instanceof IFile) {
                String fileName = ((IFile) object).getName();
                if (isSqlFileName(typeQualifiedName, fileName)) {
                    return (IFile) object;
                }
            }
            if (object instanceof IJarEntryResource) {
                IJarEntryResource jarEntry = (IJarEntryResource) object;
                if (jarEntry.isFile()) {
                    String fileName = jarEntry.getName();
                    if (isSqlFileName(typeQualifiedName, fileName)) {
                        return jarEntry;
                    }
                }
            }
        }
        return null;
    }

    private boolean isSqlFileName(String typeQualifiedName, String fileName) {
        if (fileName == null || fileName.trim().length() == 0) {
            return false;
        }
        if (!fileName.endsWith(".sql")) { //$NON-NLS-1$
            return false;
        }
        if (fileName.endsWith((typeQualifiedName + ".sql"))) { //$NON-NLS-1$
            return true;
        }
        return fileName.endsWith(String.valueOf(typeQualifiedName.charAt(0)).toLowerCase() + typeQualifiedName.substring(1) + ".sql"); //$NON-NLS-1$
    }

}
