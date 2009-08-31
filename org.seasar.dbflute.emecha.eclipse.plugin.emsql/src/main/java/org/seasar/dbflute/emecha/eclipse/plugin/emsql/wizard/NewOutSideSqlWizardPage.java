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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
    private boolean useEntity = false;
    private boolean useParamBean = false;
    private boolean usePaging = false;
    private String lineSeparator = "\n";

    protected class DfUseEntityListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
//            ((Button)e.getSource()).setSelection(useEntity);
        }
        public void widgetSelected(SelectionEvent e) {
            useEntity = ((Button)e.getSource()).getSelection();
        }
    }
    protected class DfUsePMDListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
//            ((Button)e.getSource()).setSelection(useParamBean);
        }
        public void widgetSelected(SelectionEvent e) {
            useParamBean = ((Button)e.getSource()).getSelection();
        }
    }
    protected class DfUsePagingListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
//            ((Button)e.getSource()).setSelection(usePaging);
        }
        public void widgetSelected(SelectionEvent e) {
            usePaging = ((Button)e.getSource()).getSelection();
        }
    }
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

        // DBFlute Option
        createDBFluteControls(composite);

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

//    /**
//     * グルーピング
//     * @param parent
//     * @param label
//     * @return
//     * @deprecated
//     */
//    protected Composite createSubsection(Composite parent, String label) {
//        Group group= new Group(parent, SWT.SHADOW_NONE);
//        group.setText(label);
//        GridData data= new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
//        data.horizontalSpan = 4;
//        group.setLayoutData(data);
//        GridLayout layout= new GridLayout();
//        layout.numColumns= 4;
//        group.setLayout(layout);
//
//        return group;
//
//    }
    /**
     * Option Separator.
     * @param parent
     * @param label
     */
    protected void createOptionSeparator(Composite parent, String label) {
        Label label2 = new Label(parent,SWT.NONE);
        label2.setText(label);
        label2.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));
        createSeparator(parent, 3);

    }

    /**
     * DBFlute Options
     * @param composite
     */
    protected void createDBFluteControls(Composite composite) {
        // TODO 自動生成されたメソッド・スタブ
//        Button customizeEntityCheck = new Button(composite,SWT.CHECK);
//        customizeEntityCheck.setText("Use CustomizeEntity.");
//
//        Button parambeanCheck = new Button(composite,SWT.CHECK);
//        parambeanCheck.setText("Use ParameterBean.");
//        createSeparator(composite, 4);
//        Composite subsection = createSubsection(composite, "DBFlute Options");
        createOptionSeparator(composite, "DBFlute Options");

        createSimpleCheckBox(composite, "Use Customize Entity.", 0, new DfUseEntityListener(), useEntity);
        Button pmd = createSimpleCheckBox(composite, "Use Parameter Bean.", 0, new DfUsePMDListener(), useParamBean);
        Button paging = createSimpleCheckBox(composite, "Use Paging.", 20, new DfUsePagingListener(), usePaging);
        createSelectionDependency(pmd, paging);
    }

    protected Button createSimpleCheckBox(Composite composite, String label, int indent, SelectionListener listener, boolean defaultCheck) {
        createEmptySpace(composite, 1);
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan= 3;
        gd.horizontalIndent= indent;

        Button checkBox= new Button(composite, SWT.CHECK);
        checkBox.setFont(JFaceResources.getDialogFont());
        checkBox.setText(label);
        checkBox.setLayoutData(gd);
        if (listener != null) {
            checkBox.addSelectionListener(listener);
        }

        checkBox.setSelection(defaultCheck);

        return checkBox;
    }
    /**
     * Creates a spacer control with the given span.
     * The composite is assumed to have <code>MGridLayout</code> as
     * layout.
     * @param parent The parent composite
     */
    public Control createEmptySpace(Composite parent, int span) {
        Label label= new Label(parent, SWT.LEFT);
        GridData gd= new GridData();
        gd.horizontalAlignment= GridData.BEGINNING;
        gd.grabExcessHorizontalSpace= false;
        gd.horizontalSpan= span;
        gd.horizontalIndent= 0;
        gd.widthHint= 0;
        gd.heightHint= 0;
        label.setLayoutData(gd);
        return label;
    }
    /**
     * Creates a selection dependency between a master and a slave control.
     *
     * @param master
     *                   The master button that controls the state of the slave
     * @param slave
     *                   The slave control that is enabled only if the master is
     *                   selected
     */
    protected void createSelectionDependency(final Button master, final Control slave) {

        master.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent event) {
                // Do nothing
            }

            public void widgetSelected(SelectionEvent event) {
                slave.setEnabled(master.getSelection());
            }
        });
        slave.setEnabled(master.getSelection());
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
     * Target Class Name Label.
     * {@inheritDoc}
     */
    @Override
    protected String getSuperClassLabel() {
        return "Behavior";
    }

    /**
     * File Name Label.
     * {@inheritDoc}
     */
    @Override
    protected String getTypeNameLabel() {
        return "SQL Name";
    }

    /**
     * SQL name changed event validate.
     * {@inheritDoc}
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
