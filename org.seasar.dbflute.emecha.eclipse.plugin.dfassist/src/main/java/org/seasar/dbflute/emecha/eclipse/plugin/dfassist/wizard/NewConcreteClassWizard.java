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
package org.seasar.dbflute.emecha.eclipse.plugin.dfassist.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.seasar.dbflute.emecha.eclipse.plugin.dfassist.DfAssistPlugin;

/**
 * Wizard to create a new concrete class.
 * @author schatten
 */
public class NewConcreteClassWizard extends Wizard implements INewWizard {

	private IFile resource;
	private NewConcreteClassWizardPage wizardPage;
	/**
	 *
	 */
	public NewConcreteClassWizard() {
	    super();
	    setNeedsProgressMonitor(true);
	}

	/**
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IRunnableWithProgress runnable = new IRunnableWithProgress(){

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try{
					wizardPage.createType(monitor);
				}catch (CoreException e) {
				    DfAssistPlugin.log(e);
				}finally{
					monitor.done();
				}
			}
		};
		if ( finishPage(runnable) ) {
			try {
				JavaUI.openInEditor(this.wizardPage.getCreatedType());
			} catch (Exception e) {
			    DfAssistPlugin.log(e);
				return false;
			}
		}
		return true;
	}

	/**
	 * finishPage
	 * @param runnable
	 */
	private boolean finishPage(IRunnableWithProgress runnable) {
		try{
			IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(runnable);
			PlatformUI.getWorkbench().getProgressService().runInUI(
                    getContainer(), op,
                    ResourcesPlugin.getWorkspace().getRoot());
		} catch ( InterruptedException e ){
		    DfAssistPlugin.log(e);
			return false;
		} catch (InvocationTargetException e) {
		    DfAssistPlugin.log(e);
			return false;
		}
		return true;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object o = selection.getFirstElement();
		if (o instanceof IFile) {
			IFile f = (IFile) o;
			init(f);
		}
		this.wizardPage = new NewConcreteClassWizardPage();
		addPage(this.wizardPage);
		this.wizardPage.init(selection);
		this.wizardPage.setResource(resource);
	}

	protected void init(IFile file) {
		IProject p = file.getProject();
		IJavaProject javap = JavaCore.create(p);
		if (javap.exists() && javap.isOpen()) {
			this.resource = file;
		}

	}

}
