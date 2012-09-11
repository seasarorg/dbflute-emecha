/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.wizards.project;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.DBFluteNewClientPageFinishHandler;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.DBFluteNewClientPageResult;

/**
 *
 */
public class DBFluteNewProject extends BasicNewProjectResourceWizard implements INewWizard {

    private DBFluteNewClientProjectPage dbfluteClientPage;
    /**
     *
     */
    public DBFluteNewProject() {
        super();
    }

    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        super.addPages();
        dbfluteClientPage = new DBFluteNewClientProjectPage(selection);
        dbfluteClientPage.setOutputDirectoryDelegate(new GetValueDelegate<String>() {
            public String delegate() {
                return getNewProject() == null ? null : getNewProject().getName();
            }
        });
        addPage(dbfluteClientPage);
    }

    @Override
    public boolean canFinish() {
        return super.canFinish() && dbfluteClientPage.isPageComplete();
    }
    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     * @return Determination.
     */
    public boolean performFinish() {
        super.performFinish();
        final DBFluteNewClientPageResult result = dbfluteClientPage.asResult();
        final boolean javaProject = dbfluteClientPage.isJavaProject();
        final IResource res = getNewProject();
        final IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    SubProgressMonitor downloadMonitor = new SubProgressMonitor(monitor, 3);
                    dbfluteClientPage.handleDownloadDBFluteWithProgress(downloadMonitor, result);
                    SubProgressMonitor clientMonitor = new SubProgressMonitor(monitor, 1);
                    DBFluteNewClientPageFinishHandler handler = new DBFluteNewClientPageFinishHandler(result, res);
                    handler.handleFinish(clientMonitor);
                    if (javaProject) {
                        IProject project = res.getProject();
                        SubProgressMonitor natureMonitor = new SubProgressMonitor(monitor, 1);
                        dbfluteClientPage.createJavaProject(natureMonitor, project);
                    }
                } catch (CoreException e) {
                    String msg = "DBFluteNewWizard#handleFinish() threw the core exception!";
                    EmExceptionHandler.show(msg, e);
                    throw new InvocationTargetException(e);
                } catch (Throwable t) {
                    String msg = "DBFluteNewWizard#handleFinish() threw the throwable!";
                    EmExceptionHandler.show(msg, t);
                    throw new InvocationTargetException(t);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, runnableWithProgress);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }

}
