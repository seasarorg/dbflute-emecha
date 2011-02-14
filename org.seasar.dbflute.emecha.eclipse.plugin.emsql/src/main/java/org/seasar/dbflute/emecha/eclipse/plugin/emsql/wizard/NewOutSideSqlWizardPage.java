/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.EMSqlPlugin;

/**
 * @author Schatten
 */
public class NewOutSideSqlWizardPage extends NewTypeWizardPage {

    /** ファイル名の規則 */
    private static final String FILE_NAME_VALIDATE = "^[a-zA-Z0-9_]+$";
    private static final String PAGE_NAME = "NewOutSideSqlPage";
    private IStructuredSelection _selection;
    private boolean initialized = false;
    private boolean useEntity = false;
    private boolean useParamBean = true;
    private boolean useAutoDetect = true;
    private boolean usePaging = false;
    private boolean useCursor = false;
    private boolean useScalar = false;
    private boolean useDomain = false;
    /** The output encoding of SQL file. (NotNull: default is same as DBFlute default) */
    private String sqlFileEncoding = "UTF-8";
    private String lineSeparator = System.getProperty("line.separator","\n");
    private boolean useComment = true;
    private String sqlCommentStr = "";

    protected class DfUseCommentListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            useComment = ((Button)e.getSource()).getSelection();
        }
    }
    protected class DfUseEntityListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            useEntity = ((Button)e.getSource()).getSelection();
        }
    }
    protected class DfUseCursorListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            useCursor = ((Button)e.getSource()).getSelection();
            if (useCursor) {
                useScalar = false;
                useDomain = false;
                usePaging = false;
            }
        }
    }
    protected class DfUseScalarListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            useScalar = ((Button)e.getSource()).getSelection();
            if (useScalar) {
                useCursor = false;
                useDomain = false;
                usePaging = false;
            }
        }
    }
    protected class DfUseDomainListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            useDomain = ((Button)e.getSource()).getSelection();
            if (useDomain) {
                useCursor = false;
                useScalar = false;
            }
        }
    }
    protected class DfUsePMDListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            useParamBean = ((Button)e.getSource()).getSelection();
        }
    }
    protected class DfUseAutoDetectListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            useAutoDetect = ((Button)e.getSource()).getSelection();
        }
    }
    protected class DfUsePagingListener implements SelectionListener {
        public void widgetDefaultSelected(SelectionEvent e) {
        }
        public void widgetSelected(SelectionEvent e) {
            usePaging = ((Button)e.getSource()).getSelection();
            if (usePaging) {
                useCursor = false;
                useScalar = false;
            }
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
        createOptionSeparator(composite, "DBFlute Options");
        Button cmt = createSimpleCheckBox(composite, "Use SQL Title and Description Comment.", 0, new DfUseCommentListener(), useComment);
        Text cmtTxt = createSQLTitleCommentTextBox(composite, 20);
        createSelectionDependency(cmt, cmtTxt);
        Button ce = createSimpleCheckBox(composite, "Use Customize Entity.", 0, new DfUseEntityListener(), useEntity);
        Button cursor = createSimpleCheckBox(composite, "Use Cursor.", 20, new DfUseCursorListener(), useCursor);
        createSelectionDependency(ce, cursor);
        Button scalar = createSimpleCheckBox(composite, "Use Scalar.", 20, new DfUseScalarListener(), useScalar);
        createSelectionDependency(ce, scalar);
        createSelectionOnece(cursor, scalar);
        Button domain = createSimpleCheckBox(composite, "Use Domain.", 20, new DfUseDomainListener(), useDomain);
        createSelectionDependency(ce, domain);
        createSelectionOnece(cursor, domain);
        createSelectionOnece(scalar, domain);

        Button pmd = createSimpleCheckBox(composite, "Use Parameter Bean.", 0, new DfUsePMDListener(), useParamBean);
        Button detect = createSimpleCheckBox(composite, "Use Auto Detect.", 20, new DfUseAutoDetectListener(), useAutoDetect);
        createSelectionDependency(pmd, detect);
        Button paging = createSimpleCheckBox(composite, "Use Paging.", 20, new DfUsePagingListener(), usePaging);
        createSelectionDependency(pmd, paging);

        createSelectionOnece(cursor, paging);
        createSelectionOnece(scalar, paging);
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
    protected Text createSQLTitleCommentTextBox(Composite composite, int indent) {
        createEmptySpace(composite, 1);
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan= 3;
        gd.horizontalIndent= indent;

        Text sqlComment = new Text(composite, SWT.SINGLE | SWT.BORDER);
        sqlComment.setFont(JFaceResources.getDialogFont());
        sqlComment.setLayoutData(gd);
        sqlComment.setText("");

        sqlComment.addModifyListener(new ModifyListener(){
            public void modifyText(ModifyEvent e) {
                sqlCommentStr = ((Text)e.getSource()).getText();
            }
        });

        return sqlComment;
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
     * @param master The master button that controls the state of the slave
     * @param slave The slave control that is enabled only if the master is selected
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
     * Creates a selection one between a master and a slave Button.
     *
     * @param master The master button that controls the state of the slave
     * @param slave The slave button that controls the state of the master
     */
    protected void createSelectionOnece(final Button master, final Button slave) {

        master.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent event) {
                // Do nothing
            }

            public void widgetSelected(SelectionEvent event) {
                if (master.getSelection() && slave.getSelection()) {
                    slave.setSelection(false);
                }
            }
        });
        slave.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent event) {
                // Do nothing
            }

            public void widgetSelected(SelectionEvent event) {
                if (master.getSelection() && slave.getSelection()) {
                    master.setSelection(false);
                }
            }
        });
    }

    /**
     * Initialize.
     */
    public void init(IStructuredSelection selection) {
        this._selection = selection;
        IJavaElement javaElement = getInitialJavaElement(selection);
        initContainerPage(javaElement);
        setPageComplete(false);
    }

    /**
     * Target Class Name Label.
     * {@inheritDoc}
     */
    @Override
    protected String getSuperClassLabel() {
        return "Behavior"; //$NON-NLS-1$
    }

    /**
     * File Name Label.
     * {@inheritDoc}
     */
    @Override
    protected String getTypeNameLabel() {
        return "SQL Name"; //$NON-NLS-1$
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
        if ( typeName == null || typeName.trim().length() == 0 ) {
            setErrorMessage("SQL Name is empty.");
            setPageComplete(false);
            return new Status(IStatus.ERROR, EMSqlPlugin.PLUGIN_ID, "SQL Name is empty.");
        }
        if ( !validateSqlFileName(typeName)) {
            setErrorMessage("SQL Name is missing.");
            setPageComplete(false);
            return new Status(IStatus.ERROR, EMSqlPlugin.PLUGIN_ID, "SQL Name is missing.");
        }

        IPath path = getFileFullPath();
        IFile file = getWorkspaceRoot().getFile(path);
        if ( file.exists() ) {
            setErrorMessage("SQL Name already exists.");
            setPageComplete(false);
            return new Status(IStatus.ERROR, EMSqlPlugin.PLUGIN_ID, 2, "SQL Name already exists.", null);
        }
        setPageComplete(true);
        return status;
    }

    /**
     * SQL Name Check.
     * @param typeName
     * @return
     */
    private boolean validateSqlFileName(String typeName) {
        if (!typeName.matches(FILE_NAME_VALIDATE)) {
            return false;
        }
        if ( typeName.startsWith("_") ) {
            return false;
        }
        if ( typeName.endsWith("_") ) {
            return false;
        }
        if ( typeName.indexOf("__") >= 0 ) {
            return false;
        }
        return true;
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
        if ( useComment  ) {
            str.append("/*");
            str.append(getLineSeparator());
            str.append(" [df:title]");
            str.append(getLineSeparator());
            str.append(getTitleComment());
            str.append(getLineSeparator());
            str.append(getLineSeparator());
            str.append(" [df:description]");
            str.append(getLineSeparator());
            str.append(getCommentDescription());
            str.append(getLineSeparator());
            str.append(getLineSeparator());
            str.append("*/");
            str.append(getLineSeparator());
            str.append(getLineSeparator());
        }
        if ( useEntity ) {
            str.append("-- #df:entity#");
            str.append(getLineSeparator());
            if ( useCursor ) {
                str.append("-- +cursor+");
                str.append(getLineSeparator());
            }
            if (useScalar) {
                str.append("-- +scalar+");
                str.append(getLineSeparator());
            }
            if (useDomain) {
                str.append("-- +domain+");
                str.append(getLineSeparator());
            }
            str.append(getLineSeparator());
        }
        if ( useParamBean ) {
            if ( usePaging ) {
                str.append("-- !df:pmb extends Paging!");
            } else {
                str.append("-- !df:pmb!");
            }
            if (useAutoDetect) {
                str.append(getLineSeparator());
                str.append("-- !!AutoDetect!!");
            } else {
                str.append(getLineSeparator());
                str.append(getParamBeanColumnsString());
            }
            str.append(getLineSeparator());
            if ( usePaging ) {
                str.append(getLineSeparator());
                str.append("/*IF pmb.isPaging()*/");
                str.append(getLineSeparator());
                str.append(getLineSeparator());
                str.append("-- ELSE select count(*)");
                str.append(getLineSeparator());
                str.append("/*END*/");
            }
            str.append(getLineSeparator());
        }
        try {
            return new ByteArrayInputStream(str.toString().getBytes(sqlFileEncoding));
        } catch (UnsupportedEncodingException e) {
            String msg = "The encoding is unsupported: " + sqlFileEncoding;
            throw new IllegalStateException(msg, e); // as system error
        }
    }

    /**
     * SQLの説明を取得する。
     * @return
     */
    private String getCommentDescription() {
        return "  SQL Description here.";
    }

    /**
     * SQLのタイトルを取得する。
     * @return
     */
    private String getTitleComment() {
        if (sqlCommentStr != null && sqlCommentStr.trim().length() > 0 ) {
            return "  " + sqlCommentStr;
        } else {
            return "  SQL title here.";
        }
    }

    /**
     * ParameterBeanで利用する項目のSQLファイル出力時文字列を取得する。
     * @return
     */
    private String getParamBeanColumnsString() {
        return "-- !!String sample!!";
    }

    public String getSQLFileExtension() {
        return "sql";
    }

}
