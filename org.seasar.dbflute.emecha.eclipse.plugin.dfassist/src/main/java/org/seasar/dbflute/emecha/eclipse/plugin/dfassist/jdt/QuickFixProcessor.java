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
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.jdt;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.DfAssistPlugin;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.jdt.derived.DerivedFieldInfo;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.jdt.derived.DerivedFieldPropertyProposal;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.jdt.derived.DerivedType;

/**
 * 拡張プロパティ生成プロセッサ
 * @author schatten
 */
public class QuickFixProcessor implements IQuickFixProcessor {

    /* (非 Javadoc)
     * @see org.eclipse.jdt.ui.text.java.IQuickFixProcessor#hasCorrections(org.eclipse.jdt.core.ICompilationUnit, int)
     */
    public boolean hasCorrections(ICompilationUnit unit, int problemId) {
        // このプロセッサが候補を提示するかを判定する。
        switch (problemId) {
        case IProblem.UndefinedField:
        case IProblem.UndefinedName:
            // フィールドが定義されていない場合に補完対象とする。
            return true;
        default:
            return false;
        }
    }

    /* (非 Javadoc)
     * @see org.eclipse.jdt.ui.text.java.IQuickFixProcessor#getCorrections(org.eclipse.jdt.ui.text.java.IInvocationContext, org.eclipse.jdt.ui.text.java.IProblemLocation[])
     */
    public IJavaCompletionProposal[] getCorrections(IInvocationContext context, IProblemLocation[] locations)
            throws CoreException {
        if (locations == null || locations.length == 0) {
            return null;
        }
        Set<Integer> handledProblems = new HashSet<Integer>(locations.length);
        Collection<IJavaCompletionProposal> result = new ArrayList<IJavaCompletionProposal>();
        for (int i = 0; i < locations.length; i++) {
            IProblemLocation problem = locations[i];
            Integer id = new Integer(problem.getProblemId());
            if (handledProblems.add(id)) {
                result.addAll(process(context, problem));
            }
        }
        return (IJavaCompletionProposal[]) result.toArray(new IJavaCompletionProposal[result.size()]);
    }
    protected Collection<IJavaCompletionProposal> process(IInvocationContext context, IProblemLocation problem) {
        int problemId = problem.getProblemId();
        if (problemId == 0) {// no proposals for none-problem locations
            return new ArrayList<IJavaCompletionProposal>();
        }

        List<IJavaCompletionProposal> list = new ArrayList<IJavaCompletionProposal>();
        switch (problemId) {
        case IProblem.UndefinedName:
        case IProblem.UndefinedField:
            list.addAll(getDerivedFieldProposal(context, problem));
            break;
        default:
            break;
        }
        return list;
    }

    /**
     * DerivedFieldを補完するための補完候補を取得する。
     * @param context
     * @param problem
     * @return 補完候補
     */
    protected Collection<IJavaCompletionProposal> getDerivedFieldProposal(IInvocationContext context, IProblemLocation problem) {

        CompilationUnit astRoot = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveredNode(astRoot);
        if (selectedNode == null) {
            return new ArrayList<IJavaCompletionProposal>();
        }
        List<IJavaCompletionProposal> proposals = new ArrayList<IJavaCompletionProposal>();
        switch (selectedNode.getNodeType()) {
        case ASTNode.SIMPLE_NAME:
            ASTNode simpleNameParent = selectedNode.getParent();
            if (simpleNameParent.getNodeType() == ASTNode.QUALIFIED_NAME) {
                selectedNode = simpleNameParent;
            }
        case ASTNode.QUALIFIED_NAME:
            DerivedFieldInfo fieldInfo = createDerivedFieldInfo(context, problem, (Name)selectedNode);
            ASTNode parent = selectedNode.getParent();
//            DfAssistPlugin.log("Problem:" + problem.getProblemId() + ", selectNode:" + selectedNode.getClass() + ", Parent-NodeType:" + parent.getNodeType() + ", Parent-Class:" + parent.getClass());
            if (parent instanceof MethodInvocation) {
                MethodInvocation parentMethod = (MethodInvocation)parent;
                String methodName = parentMethod.getName().getFullyQualifiedName();
                DerivedType derivedType = DerivedType.nameOf(methodName);
                if (derivedType == null) {
                    return proposals;
                }
                switch (derivedType) {
                case COUNT:
                case COUNT_DISTINCT:
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Integer.class)));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Long.class), -1));
                    break;
                case MAX:
                case MIN:
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Integer.class), 2));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Date.class), 1));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Timestamp.class), 1));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(BigDecimal.class)));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Long.class), -1));
                    break;
                case AVG:
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(BigDecimal.class), 1));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Integer.class)));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Long.class), -1));
                    break;
                case SUM:
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Integer.class), 1));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(BigDecimal.class)));
                    proposals.add(new DerivedFieldPropertyProposal(context, fieldInfo.copy(Long.class), -1));
                    break;
                default:
                    break;
                }
            }
            break;
        default:
            break;
        }
        return proposals;
    }

    protected DerivedFieldInfo createDerivedFieldInfo(IInvocationContext _context, IProblemLocation _problem, Name selectedNode) {
        Name nameNode = selectedNode;
        if (selectedNode instanceof SimpleName) {
            ASTNode simpleNameParent = selectedNode.getParent();
            if (simpleNameParent.getNodeType() == ASTNode.QUALIFIED_NAME) {
                nameNode = (QualifiedName)simpleNameParent;
            }
        }

       DerivedFieldInfo fieldInfo = new DerivedFieldInfo();
        fieldInfo.setConstantFieldName(getPropertyName(nameNode));
        fieldInfo.setTargetTypeName(getSelectedTypeName(nameNode));
        return fieldInfo;
    }

    protected String getPropertyName(Name selectedNode) {
        if (selectedNode instanceof QualifiedName) {
            return ((QualifiedName)selectedNode).getName().getFullyQualifiedName();
        } else {
            String fullyQualifiedName = ((Name)selectedNode).getFullyQualifiedName();
            return fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.') + 1);
        }
    }
    /**
     * @param selectedNode
     * @return
     */
    protected String getSelectedTypeName(Name selectedNode) {
        if (selectedNode instanceof QualifiedName) {
            return ((QualifiedName)selectedNode).getQualifier().getFullyQualifiedName();
        }
        String fullyQualifiedName = ((Name) selectedNode).getFullyQualifiedName();
        int sep = fullyQualifiedName.lastIndexOf('.');
        if (sep > 0) {
            return fullyQualifiedName.substring(0, sep);
        }
        // TODO クラス名取得方法検討
        ASTNode parent = selectedNode.getParent();
        if (parent instanceof Name) {
            return getSelectedTypeName((Name)parent);
        }
        if (parent instanceof FieldAccess) {
            return ((FieldAccess)parent).getExpression().toString();
        }
        return null;
    }


}
