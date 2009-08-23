/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.ide.IDE;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.LogUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.EMSqlPlugin;

/**
 * @author masa
 *
 */
public class NewOutSideSqlWizard extends Wizard implements INewWizard {

//    private IFile _resource;
//    private IProject _project;
//    private IJavaProject _javaProject;
//    private IWorkbench _workbench;
    private IStructuredSelection _selection;

    private NewOutSideSqlWizardPage mainPage;
    private IFile createFile = null;
    /**
     * Construct
     */
    public NewOutSideSqlWizard() {
        super();
        setWindowTitle("Create new outside sql.");
        setNeedsProgressMonitor(true);
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
        if ( !mainPage.isPageComplete() ) {
            return false;
        }
        IRunnableWithProgress progress = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException {
                try {
                    if ( monitor == null ) {
                        monitor = new NullProgressMonitor();
                    }
                    monitor.beginTask("Create SQL file. File name is " + mainPage.getSQLFileName() + ".", 5);
                    createFile = mainPage.createSQLFile(new SubProgressMonitor(monitor, 5));
                } catch (Exception e) {
                    LogUtil.log(EMSqlPlugin.getDefault(),e);
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            if (finishPage(progress)) {
                if ( createFile != null) {
                    IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                    try {
                        if (dw != null) {
                            IWorkbenchPage page = dw.getActivePage();
                            if (page != null) {
                                IDE.openEditor(page, createFile, true);
                            }
                        }
                    } catch (PartInitException e) {
//                        DialogUtil.openError(dw.getShell(), ResourceMessages.FileResource_errorMessage,
//                                e.getMessage(), e);
                        LogUtil.log(EMSqlPlugin.getDefault(),e);
                    }

                }
                return true;
            }
        } catch (Exception e) {
            LogUtil.log(EMSqlPlugin.getDefault(), e);
        }

        return false;
    }

    /**
     * finishPage.
     * @param runnable execute task.
     * @return true:complite false:error
     */
    protected boolean finishPage(IRunnableWithProgress runnable) {
        IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(runnable);
        try {
            PlatformUI.getWorkbench().getProgressService().runInUI(
                    getContainer(), op,
                    ResourcesPlugin.getWorkspace().getRoot());
        } catch (InvocationTargetException e) {
            LogUtil.log(EMSqlPlugin.getDefault(), e);
            return false;
        } catch (InterruptedException e) {
            LogUtil.log(EMSqlPlugin.getDefault(), e);
            return false;
        }
        return true;
    }


    /**
     * Initialize Wizard.
     * @param workbench
     * @param selection2
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO 自動生成されたメソッド・スタブ
//        this._workbench = workbench;
        this._selection = selection;
        Object obj = _selection.getFirstElement();
        if (obj instanceof IFile) {
            IFile file = (IFile) obj;
            init(file);
        }
    }

    /**
     * Initialize Wizard.
     * @param file
     */
    public void init(IFile file) {
        // TODO 自動生成されたメソッド・スタブ
//        _project = file.getProject();
//        IJavaProject javap = JavaCore.create(prj);
//        if (javap.exists() && javap.isOpen()) {
//            this._resource = file;
//            this._project = javap;
//        }

    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        // TODO 自動生成されたメソッド・スタブ
        mainPage = new NewOutSideSqlWizardPage();
        mainPage.setTitle("CreateNewOutSideSql");
        mainPage.setDescription("Create new OutSideSql file.");
        addPage(mainPage);
        mainPage.init(_selection);

    }

}
