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
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.wizard;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.DfAssistPlugin;

/**
 * Wizard page to create a new concrete class.
 * @author schatten
 */
public class NewConcreteClassWizardPage extends NewClassWizardPage {

	private IFile resource;

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jdt.ui.wizards.NewClassWizardPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		String pkgName = getPackageFragment().getElementName();
		String implPackageName = pkgName;
		if (implPackageName == null || implPackageName.trim().length() == 0) {
		    implPackageName = "impl";
	    } else if (!implPackageName.endsWith(".impl")) {
		    implPackageName = pkgName + ".impl";
		}
        setPackageFragment( getPackageFragmentRoot().getPackageFragment( implPackageName) ,true);
        if (this.resource != null) {
            String typeName = this.resource.getFullPath().removeFileExtension().lastSegment();

            if ( typeName.lastIndexOf("Abstract") > -1 ) {
                setTypeName(typeName.replace("Abstract", "") + "Impl", true);
            } else {
                setTypeName(typeName + "Impl", true);
            }

            String superClassName = pkgName + "." + typeName;
            try {
                if ( this.isInterface(this.resource) ) {
                    List<String> list = new ArrayList<String>();
                    list.add(superClassName);
                    setSuperInterfaces(list, true);
                } else {
                    setSuperClass(superClassName, true);
                }
            } catch (Exception e) {
                DfAssistPlugin.log(e);
            }
        }
        setAddComments(true, true);
		enableCommentControl(true);
		setMethodStubSelection(false, false, true, true);

	}

	/**
	 * @param resource セットする resource
	 */
	public void setResource(IFile resource) {
		this.resource = resource;
	}

	/**
	 * 対象がInterfaceクラスか判定する。
	 * @param file 対象のファイル
	 * @return 対象がInterfaceクラスの場合に<code>true</code>
	 * @throws CoreException
	 * @throws IOException
	 */
	protected boolean isInterface(IFile file) throws CoreException, IOException {
		ASTParser parser = ASTParser.newParser(getAstLevel(file));
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(this.getSourceString(file).toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(null);
		TypeDeclaration type = (TypeDeclaration)unit.types().get(0);
		return type.isInterface();

	}

    /**
     * ソース解析レベルを取得する。
     * @param file 対象のファイル
     * @return AST API Level
     * @see org.eclipse.jdt.core.dom.AST
     */
    private int getAstLevel(IFile file) {
        IProject project = file.getProject();
        IJavaProject javaProject = JavaCore.create(project);
        String option = javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);

        if ("1.7".equals(option)) {
            // Java 7 compliance level.
            return 4; // AST.JLS4;
        }
        // Default return is Java 5 compliance level.
        return 3; //AST.JLS3;
    }

	/**
	 * 対象のファイルの内容を取得する。
	 * @param file 対象のファイル
	 * @return ソース文字列
	 * @throws CoreException
	 * @throws IOException
	 */
	protected String getSourceString(IFile file) throws CoreException ,IOException {
		StringBuilder builder = new StringBuilder();
		InputStream stream = null;
		BufferedReader reader = null;
		try {
			stream = file.getContents();
			reader = new BufferedReader(new InputStreamReader(stream));
			for (String str = reader.readLine(); str != null; str = reader.readLine() ){
				builder.append(str);
				builder.append('\n');
			}
			return builder.toString();
		} finally {
			closeStream(stream);
			closeStream(reader);
		}
	}
	/**
	 * ストリームを閉じる。
	 * @param closeable target stream
	 */
	protected void closeStream(Closeable closeable) {
		try {
			if ( closeable != null )
					closeable.close();
		}catch (IOException e) {
		    DfAssistPlugin.log(e);
		}
	}

}
