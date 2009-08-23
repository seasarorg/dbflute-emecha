/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.LogUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.EMSqlPlugin;

/**
 * @author masa
 *
 */
public class NewOutSideSqlWizardPage extends NewTypeWizardPage {


    private static final String PAGE_NAME = "NewOutSideSqlPage";
    private IStructuredSelection _selection;
    private boolean initialized = false;
    private boolean useEntity = true;
    private boolean useParamBean = true;
    private boolean usePaging = false;
    private String lineSeparator = "\n";
    /**
     * @return lineSeparator
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * @param lineSeparator セットする lineSeparator
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * @param name
     */
    public NewOutSideSqlWizardPage() {
        super(false,PAGE_NAME);
        setPageComplete(false);
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite composite= new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        // TODO 画面の作成
        int nColumns= 4;
        GridLayout layout= new GridLayout();
        layout.numColumns= nColumns;
        composite.setLayout(layout);

        // pick & choose the wanted UI components

        createContainerControls(composite, nColumns);
        createPackageControls(composite, nColumns);
        createSuperClassControls(composite, nColumns);
        createTypeNameControls(composite, nColumns);

        IJavaElement javaElement = getInitialJavaElement(_selection);
        // Initial Behavior Package Setting
        IPackageFragment packageFragment = (IPackageFragment)javaElement.getAncestor(IJavaElement.PACKAGE_FRAGMENT);
        setPackageFragment(packageFragment, true);

        // Initial Behavior Setting
        String elementName = javaElement.getElementName();
        if (elementName != null && elementName.indexOf('.') > 0) {
            elementName = elementName.substring(0, elementName.indexOf('.'));
        }
        setSuperClass(packageFragment.getElementName() + "." + elementName, false);

        // Initial Source Root Setting
        IJavaProject javaProject = javaElement.getJavaProject();
        IPackageFragmentRoot resourcePackageFragmentRoot = javaProject.getPackageFragmentRoot(javaProject.getProject().getName() + "/src/main/resources");
        setPackageFragmentRoot(resourcePackageFragmentRoot,true);

        // 描画する画面を設定
        setControl(composite);
        Dialog.applyDialogFont(composite);

        setFocus();
        initialized = true;
    }

    /**
     * Initialize.
     */
    public void init(IStructuredSelection selection) {
        this._selection = selection;
        IJavaElement javaElement = getInitialJavaElement(selection);
        initContainerPage(javaElement);
    }

    /**
     * Returns the label that is used for the super class input field.
     *
     * @return the label that is used for the super class input field.
     * @since 3.2
     */
    @Override
    protected String getSuperClassLabel() {
        return "Behavior";
    }

    /**
     * Returns the label that is used for the type name input field.
     *
     * @return the label that is used for the type name input field.
     * @since 3.2
     */
    @Override
    protected String getTypeNameLabel() {
        return "SQL Name";
    }

    /**
     * Hook method that gets called when the type name has changed. The method validates the
     * type name and returns the status of the validation.
     * <p>
     * Subclasses may extend this method to perform their own validation.
     * </p>
     *
     * @return the status of the validation
     */
    @Override
    protected IStatus typeNameChanged() {
        setErrorMessage(null);
        if ( !initialized ) {
            setPageComplete(false);
            return Status.OK_STATUS;
        }
        IStatus status = super.typeNameChanged();
        String typeName = getTypeName();
        LogUtil.log(EMSqlPlugin.getDefault(), "CheckName:" + typeName);
        if ( typeName == null || typeName.trim().length() == 0 ) {
            setErrorMessage("SQL Name is empty.");
            setPageComplete(false);
            return new Status(IStatus.ERROR, EMSqlPlugin.PLUGIN_ID, "SQL Name is empty.");
        }
        if ( !typeName.matches("^[a-zA-Z0-9]+$")) {
            setErrorMessage("SQL Name is missing.");
            setPageComplete(false);
            return new Status(IStatus.ERROR, EMSqlPlugin.PLUGIN_ID, "SQL Name is missing.");
        }

        // TODO File exists check!!
        IPath path = getFileFullPath();
        IFile file = getWorkspaceRoot().getFile(path);
        LogUtil.log(EMSqlPlugin.getDefault(), "TargetFile:" + file.getFullPath().toString());
        if ( file.exists() ) {
            setErrorMessage("SQL Name already exists.");
            setPageComplete(false);
            return new Status(IStatus.ERROR, EMSqlPlugin.PLUGIN_ID, 2, "SQL Name already exists.", null);
        }
        setPageComplete(true);
        return status;
    }

    /**
     * @return
     */
    private IPath getFileFullPath() {
        IPath path = getSQLFolderPath();
        String fileName = getSQLFileName();
        return path.append(fileName + "." + getSQLFileExtension());
    }

    /**
     * @return
     */
    protected IPath getSQLFolderPath() {
        IPath path = getPackageFragmentRoot().getPackageFragment(getPackageText()).getPath();
        return path;
    }

    /**
     * Get SQL file name.
     * (Not file path)
     * @return SQL file name.
     */
    public String getSQLFileName() {
        return getSuperClass().substring(getSuperClass().lastIndexOf('.') + 1) + "_" + getTypeName();
    }

    /**
     * Create SQL file.
     */
    public IFile createSQLFile(IProgressMonitor monitor) throws CoreException, InterruptedException {
        // TODO Create File with directories.
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        monitor.beginTask("Create SQL file. File name is " + getSQLFileName() + ".", 2);
        try {
            IPackageFragmentRoot root= getPackageFragmentRoot();
            IPackageFragment pack= root.getPackageFragment(getPackageText());
            if (pack == null) {
                pack= root.getPackageFragment(""); //$NON-NLS-1$
            }
            if (!pack.exists()) {
                String packName= pack.getElementName();
                pack= root.createPackageFragment(packName, true, new SubProgressMonitor(monitor, 1));
            } else {
                monitor.worked(1);
            }
            IFile file = getWorkspaceRoot().getFile(getFileFullPath());

            InputStream source = getInitialSource();
            file.create(source, false, new SubProgressMonitor(monitor, 2));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            return file;
       } finally {
            monitor.done();
        }
    }

    /**
     * @return
     */
    private InputStream getInitialSource() {
        StringBuilder str = new StringBuilder();
        if ( useEntity ) {
            str.append("-- #df:entity#");
            str.append(getLineSeparator());
            str.append(getLineSeparator());
        }
        if ( useParamBean ) {
            if ( usePaging ) {
                str.append("-- !df:pmb extends SPB!");
            } else {
                str.append("-- !df:pmb!");
            }
            str.append(getLineSeparator());
        }
        return new ByteArrayInputStream(str.toString().getBytes());
    }

    public String getSQLFileExtension() {
        return "sql";
    }

}
