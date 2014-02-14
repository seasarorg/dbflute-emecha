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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.DfAssistPlugin;

/**
 * SQLからJavaクラスを開くアクションのベースクラス。
 * @author schatten
 */
public abstract class AbstractOpenActionBase implements IObjectActionDelegate {


    protected IStructuredSelection _selection;
    protected IAction _action;
    protected IWorkbenchPart _targetPart;
    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection iss = (IStructuredSelection) selection;
            this._selection = iss;
        }
    }
    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this._action = action;
        this._targetPart = targetPart;
    }
    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        Object obj = this._selection.getFirstElement();
        if (obj instanceof IFile) {
            IFile file = (IFile) obj;
            IProject project = file.getProject();
            IJavaProject javap = JavaCore.create(project);
            if (javap.exists() && javap.isOpen()) {
                String packageName = getTargetPackageName(getBasePackageName(file, javap));
                String typeQualifiedName = getTargetClassName(file);
                try {
                    IType findType = javap.findType(packageName, typeQualifiedName, (IProgressMonitor)null);
                    if (findType == null || !findType.exists()) return;
                    if (findType.isBinary()) {
                        IClassFile classFile = findType.getClassFile();
                        classFile.open((IProgressMonitor)null);
                    } else {
                        openFileInEditor(file, findType);
                    }
                } catch (JavaModelException e) {
                    DfAssistPlugin.log(e);
                } catch (PartInitException e) {
                    DfAssistPlugin.log(e);
                }
            }
        }
    }

    /**
     * 対象のパッケージ名を取得する。
     * @param packageName ベースパッケージ
     * @return ターゲットパッケージ名
     */
    protected abstract String getTargetPackageName(String packageName);
    /**
     * 対象のファイルに関連するクラス名を取得する。
     * @param file 選択されたファイル
     * @return ターゲットクラス名
     */
    protected abstract String getTargetClassName(IFile file);
    /**
     * 対象のファイルのパッケージを取得する。
     * @param file 選択されたファイル
     * @return ターゲットクラス名
     */
    protected String getBasePackageName(IFile file, IJavaProject javap) {
        String filePath = file.getParent().getFullPath().toString();
        String rootPath = null;
        try {
            for (IPackageFragmentRoot root : javap.getPackageFragmentRoots()) {
                if (filePath.startsWith(root.getPath().toString())) {
                    rootPath = root.getPath().toString();
                    break;
                }
            }
        } catch (JavaModelException e) {
            DfAssistPlugin.log(e);
        }
        if (rootPath == null) return null;
        String dir = filePath.substring(rootPath.length() + 1);
        int index = dir.indexOf("/exbhv");
        if (index > 0) {
            return dir.substring(0, index).replace('/', '.');
        }
        return null;
    }
    protected String getSqlName(IFile file) {
        String fileName = file.getName();
        int separateIndex = fileName.lastIndexOf('_');
        int extensionIndex = fileName.lastIndexOf('.');
        if (separateIndex > 0 && extensionIndex > 0) {
            String sqlName = fileName.substring(separateIndex + 1, extensionIndex);
            Pattern pattern = Pattern.compile("^((?=select|update|insert|delete)[a-z]{6})[A-Za-z0-9]*");
            Matcher matcher = pattern.matcher(sqlName);
            if (matcher.matches()) {
                sqlName = sqlName.substring(matcher.group(1).length());
            }
            return sqlName;
        }
        return null;
    }

    protected void openFileInEditor(IFile selected, IType findType) throws PartInitException {
        IFile file = selected.getProject().getFile(findType.getPath().removeFirstSegments(1));
        IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (dw != null) {
            IWorkbenchPage page = dw.getActivePage();
            if (page != null) {
                IEditorPart editor = IDE.openEditor(page, file, true);
                editor.setFocus();
            }
        }
    }

}
