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
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.jdt.derived;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.DfAssistPlugin;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.nls.Messages;

/**
 * DerivedReferrer用の定数・変数・Getter/Setterの作成をアシストするプロポーサル。
 * @author schatten
 */
public class DerivedFieldPropertyProposal implements IJavaCompletionProposal {

    private static final String LINE_SEPARATER = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
    private IInvocationContext _context;
    private int _addRelevance = 0;

    private DerivedFieldInfo _fieldInfo;

    public DerivedFieldPropertyProposal(IInvocationContext context, DerivedFieldInfo fieldInfo, int relevance) {
        this(context, fieldInfo);
        this._addRelevance = relevance;
    }
    public DerivedFieldPropertyProposal(IInvocationContext context, DerivedFieldInfo fieldInfo) {
        this._context = context;
        this._fieldInfo = fieldInfo;
    }

    /**
     * 表示優先順位
     * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposal#getRelevance()
     */
    public int getRelevance() {
        // JDTのフィールド作成の前に表示されるように調整。(JDTの定数作成は 8 )
        return 10 + _addRelevance;
    }

    /**
     * 選択された場合に行う動作
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
     */
    public void apply(IDocument document) {
        // ソース生成処理
        CompilationUnit astRoot = _context.getASTRoot();
        ICompilationUnit cu = _context.getCompilationUnit();
        try {
            String typeName = getTargetTypeName(astRoot, _fieldInfo.getTargetTypeName());
            IResource resource = cu.getCorrespondingResource();
            if (resource == null || !resource.isAccessible()) {
                // ファイルの編集ができない場合何もしない。
                return;
            }
            IType findType = JavaCore.create(resource.getProject()).findType(typeName);
            // 生成先のクラスを開く
            IEditorPart editor = JavaUI.openInEditor(findType,true,false);
            // 対象クラスにメソッドを追加
            String importTypeName = this._fieldInfo.getPropertyTypeFullPackageName();
            if (!importTypeName.startsWith("java.lang")) { //$NON-NLS-1$
                findType.getCompilationUnit().createImport(importTypeName, null, Flags.AccDefault, null);
            }
            findType.createField(_fieldInfo.getConstantFieldSource(), null, true, null);
            IField propertyField = findType.createField(_fieldInfo.getPropertyFieldSource(), null, true, null);
            findType.createMethod(_fieldInfo.getGetterMethodSource(), null, true, null);
            findType.createMethod(_fieldInfo.getSetterMethodSource(), null, true, null);
            // カーソル位置制御
            if (editor instanceof AbstractTextEditor) {
                AbstractTextEditor ateditor = (AbstractTextEditor)editor;
                ISourceRange propertyFieldSourceRange = propertyField.getSourceRange();
                // 変数の型宣言前にカーソルを設定
                ateditor.selectAndReveal(propertyFieldSourceRange.getOffset() + 10, 0);
            }
        } catch (JavaModelException e) {
            DfAssistPlugin.log("Editor open error.", e); //$NON-NLS-1$
        } catch (PartInitException e) {
            DfAssistPlugin.log("Editor open error.", e); //$NON-NLS-1$
        }
    }

    /**
     * @param astRoot
     * @param selectedNode
     * @return
     */
    protected String getTargetTypeName(CompilationUnit astRoot, String selectTypeName) {
        String typeName = selectTypeName;
        @SuppressWarnings("unchecked")
        List<ImportDeclaration> imports = astRoot.imports();
        for (ImportDeclaration imp : imports) {
            if (imp.isStatic()) {
                continue;
            }
            if (imp.isOnDemand()) {
                // TODO import が * で指定されている場合の対応
            } else {
                Name name = imp.getName();
                String packageName = name.getFullyQualifiedName();
                String[] split = packageName.split("\\."); //$NON-NLS-1$
                String splitName = split.length > 1 ? split[split.length -1] : split[0];
                if (splitName.equals(typeName)) {
                    typeName = packageName;
                    break;
                }
            }
        }
        return typeName;
    }

    /* (非 Javadoc)
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
     */
    public String getAdditionalProposalInfo() {
        // 候補表示時に出力する説明文を組み立てる。
        StringBuilder code = new StringBuilder();
        code.append("public class "); //$NON-NLS-1$
        code.append(_fieldInfo.getTargetTypeName());
        code.append(" ... {").append(LINE_SEPARATER); //$NON-NLS-1$
        code.append("...").append(LINE_SEPARATER).append("<b>"); //$NON-NLS-1$ //$NON-NLS-2$
        code.append(_fieldInfo.getConstantFieldSource());
        code.append(LINE_SEPARATER);
        code.append(_fieldInfo.getPropertyFieldSource());
        code.append(LINE_SEPARATER);
        code.append(_fieldInfo.getGetterMethodSource());
        code.append(LINE_SEPARATER);
        code.append(_fieldInfo.getSetterMethodSource());
        code.append("</b>").append(LINE_SEPARATER).append("}"); //$NON-NLS-1$ //$NON-NLS-2$
        return code.toString().replaceAll(LINE_SEPARATER, "<br>" + LINE_SEPARATER); //$NON-NLS-1$
    }

    public IContextInformation getContextInformation() {
        return null;
    }

    /**
     * 候補に表示する文字列
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
     */
    public String getDisplayString() {
        return Messages.QUICK_FIX_DERIVED_FIELD_PROPERTY + " : " + this._fieldInfo.getPropertyTypeName(); //$NON-NLS-2$
    }

    /**
     * 候補に表示する際のアイコン画像
     * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
     */
    public Image getImage() {
        return null;
    }

    public Point getSelection(IDocument document) {
        return null;
    }
}
