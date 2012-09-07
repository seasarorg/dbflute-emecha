package org.seasar.dbflute.emecha.eclipse.plugin.wizards.version;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;

/**
 * This is a sample new wizard. Its role is to create a new file
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "mpe". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 *
 * @author jflute (Modification after it was generated)
 * @since 0.2.3 (2009/01/31 Saturday)
 */
public class DBFluteUpgrade extends Wizard implements INewWizard {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private DBFluteUpgradePage page;
    private IStructuredSelection selection;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor for DBFluteNewWizard.
     */
    public DBFluteUpgrade() {
        super();
        setNeedsProgressMonitor(true);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * We will accept the selection in the workbench to see if
     * we can initialize from it.
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }

    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new DBFluteUpgradePage(selection);
        addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     * @return Determination.
     */
    public boolean performFinish() {
        final DBFluteUpgradePageResult result = page.asResult();
        final IResource res = page.findTopLevelResource(result.getOutputDirectory());
        final IRunnableWithProgress runnableWithProgress = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    DBFluteUpgradePageFinishHandler handler = new DBFluteUpgradePageFinishHandler(result, res);
                    handler.handleFinish(monitor);
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

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    protected void debug(String log) {
        if (true) {
            System.out.println(log);
        }
    }
}