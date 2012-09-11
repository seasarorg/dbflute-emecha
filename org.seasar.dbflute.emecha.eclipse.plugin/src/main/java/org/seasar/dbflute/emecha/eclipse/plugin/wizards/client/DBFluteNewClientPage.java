package org.seasar.dbflute.emecha.eclipse.plugin.wizards.client;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;
import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.dicon.extract.EmConventionDiconExtractor;
import org.seasar.dbflute.emecha.eclipse.plugin.core.helper.dicon.extract.EmJdbcDiconExtractor;
import org.seasar.dbflute.emecha.eclipse.plugin.core.meta.website.EmMetaFromWebSite;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.io.EmFileUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.net.EmURLUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.util.zip.EmZipInputStreamUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmContainer;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmWorkspaceRoot;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.definition.DatabaseInfoDef;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.definition.TargetContainerDef;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.listener.DBFluteNewClientDefaultModifyListener;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 * @author jflute (Modification after it was generated)
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class DBFluteNewClientPage extends WizardPage {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String DEFAULT_PACKAGE_BASE = "com.xxx.yyy.dbflute";
    protected static final String TITLE_INDENT = "  ";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                            Basic Info
    //                                            ----------
    private Text outputDirectoryText;

    private Text projectText;

    private Combo databaseCombo;

    private Combo targetContainerCombo;

    private Text packageBaseText;

    // -----------------------------------------------------
    //                                         Database Info
    //                                         -------------
    private Text databaseInfoDriverText;

    private Text databaseInfoUrlText;

    private Text databaseInfoSchemaText;

    private Text databaseInfoUserText;

    private Text databaseInfoPasswordText;

    // -----------------------------------------------------
    //                                          Version Info
    //                                          ------------
    private Text versionInfoDBFluteText;

    @SuppressWarnings("unused")
    private Button versionInfoLatestVersionButton;

    @SuppressWarnings("unused")
    private Button versionInfoLatestSnapshotVersionButton;

    private Button versionInfoDownloadDBFluteButton;

    // -----------------------------------------------------
    //                                             Selection
    //                                             ---------
    private IStructuredSelection selection;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor for DBFluteNewClientPage.
     * @param selection Selection. (NotNull)
     */
    public DBFluteNewClientPage(IStructuredSelection selection) {
        super("wizardPage");
        setPageComplete(false);
        setTitle("DBFlute New Client");
        setDescription("This wizard creates a new DBFlute client.");
        this.selection = selection;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    // -----------------------------------------------------
    //                                      Control Creation
    //                                      ----------------
    /**
     * @see IDialogPage#createControl(Composite)
     * @param parent The composite of parent. (NotNull)
     */
    public void createControl(Composite parent) {
        final Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(createContainerLayout());

        // Layout
        layoutBasicInfoControl(container);

        // for Space
        createEmptyLabel(container);
        createEmptyLabel(container);

        layoutDatabaseInfoControl(container);

        // for Space
        createEmptyLabel(container);
        createEmptyLabel(container);

        layoutVersionInfoControl(container);

        layoutLatestVersionButton(container);
        createEmptyLabel(container);
        layoutLatestSnapshotVersionButton(container);
        createEmptyLabel(container);

        // for Space
        createEmptyLabel(container);
        createEmptyLabel(container);

        layoutDownloadDBFluteButton(container);

        // Initialize!
        setupFromDicon();

        // Check!
        try {
            createDialogChangedDefaultModifyListener().handle();
        } catch (RuntimeException e) {
            String msg = this.getClass().getSimpleName()
                    + "#createDialogChangedDefaultModifyListener().handle() threw the exception!";
            EmExceptionHandler.show(msg, e);
            throw e;
        }

        setControl(container);
    }

    protected GridLayout createContainerLayout() {
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 3;
        layout.horizontalSpacing = 3;
        return layout;
    }

    // -----------------------------------------------------
    //                                            Basic Info
    //                                            ----------
    protected void layoutBasicInfoControl(final Composite container) {
        setupOutputDirectory(container);
        setupProject(container, TITLE_INDENT + "Client Project (*)");
        setupDatabase(container);
        setupTargetContainer(container);
        setupPackageBase(container, TITLE_INDENT + "Package Base (*)");
    }

    protected void setupOutputDirectory(final Composite container) {
        createLabel(container, TITLE_INDENT + "Eclipse Project:");

        outputDirectoryText = new Text(container, SWT.BORDER | SWT.SINGLE);
        try {
            initializeOutputDirectory();
        } catch (RuntimeException e) {
            EmExceptionHandler.show(this.getClass().getSimpleName() + "#initialize() threw the exception!", e);
            throw e;
        }
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */
    protected void initializeOutputDirectory() {
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        outputDirectoryText.setLayoutData(gd);
        outputDirectoryText.setEditable(false);
        IProject selectProject = getInitialProject(selection);
        String projectName = selectProject == null ? null : selectProject.getName();
        if (projectName != null) {
            outputDirectoryText.setText(projectName);
        } else {
            outputDirectoryText.setText("");
        }
        outputDirectoryText.addModifyListener(createDialogChangedDefaultModifyListener());
    }

    /**
     * Get project by selected elements.
     * @param selection selected elements
     * @return project of first element.
     */
    protected IProject getInitialProject(IStructuredSelection selection) {
        if(selection != null && !selection.isEmpty()) {
            Object selectedElement = selection.getFirstElement();
            if(selectedElement instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable)selectedElement;
                IResource resource = (IResource)adaptable.getAdapter(org.eclipse.core.resources.IResource.class);
                if(resource != null && resource.getType() != IResource.ROOT) {
                    return resource.getProject();
                }
            }
        }
        return null;
    }

    protected void setupProject(final Composite container, String title) {
        createLabel(container, title);
        projectText = createText(container);
        initializeCommonProperty(projectText);
    }

    protected void setupDatabase(final Composite container) {
        // Label
        createLabel(container, TITLE_INDENT + "Database (*)");

        final List<String> databaseList = extractDatabaseList();
        databaseCombo = new Combo(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);

        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        databaseCombo.setLayoutData(gd);
        for (String databaseTypeElement : databaseList) {
            databaseCombo.add(databaseTypeElement);
        }
        databaseCombo.addModifyListener(createDialogChangedDefaultModifyListenerWithDatabaseAid());
    }

    protected void setupTargetContainer(final Composite container) {
        // Label
        createLabel(container, TITLE_INDENT + "DI Container (*)");

        final List<String> targetContainerList = extractTargetContainerList();

        targetContainerCombo = new Combo(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        targetContainerCombo.setLayoutData(gd);
        for (String targetContainerElement : targetContainerList) {
            targetContainerCombo.add(targetContainerElement);
        }
        targetContainerCombo.select(0); // as Default
        targetContainerCombo.addModifyListener(createDialogChangedDefaultModifyListener());
    }

    protected void setupPackageBase(final Composite container, String title) {
        createLabel(container, title);
        packageBaseText = createText(container, DEFAULT_PACKAGE_BASE);
        initializeCommonProperty(packageBaseText);
    }

    // -----------------------------------------------------
    //                                         Database Info
    //                                         -------------
    protected void layoutDatabaseInfoControl(final Composite container) {
        final String indent = TITLE_INDENT;
        setupDatabaseInfoDriver(container, indent + "Driver (*)");
        setupDatabaseInfoUrl(container, indent + "Url (*)");
        setupDatabaseInfoSchema(container, indent + "Schema");
        setupDatabaseInfoUser(container, indent + "User (*)");
        setupDatabaseInfoPassword(container, indent + "Password");
    }

    protected void setupDatabaseInfoDriver(final Composite container, String title) {
        createLabel(container, title);
        databaseInfoDriverText = createText(container);
        databaseInfoDriverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        databaseInfoDriverText.setEditable(false);
    }

    protected void setupDatabaseInfoUrl(final Composite container, String title) {
        createLabel(container, title);
        databaseInfoUrlText = createText(container);
        initializeCommonProperty(databaseInfoUrlText);
    }

    protected void setupDatabaseInfoSchema(final Composite container, String title) {
        createLabel(container, title);
        databaseInfoSchemaText = createText(container);
        initializeCommonProperty(databaseInfoSchemaText);
    }

    protected void setupDatabaseInfoUser(final Composite container, String title) {
        createLabel(container, title);
        databaseInfoUserText = createText(container);
        initializeCommonProperty(databaseInfoUserText);
    }

    protected void setupDatabaseInfoPassword(final Composite container, String title) {
        createLabel(container, title);
        databaseInfoPasswordText = createText(container);
        initializeCommonProperty(databaseInfoPasswordText);
    }

    protected void setupFromDicon() {
        initializePackageBaseAsDiconIfExists(getProject(), true);
        initializeDatabaseInfoUrlAsDiconIfExists();
        createDialogChangedDefaultModifyListener().handle();
    }

    protected String extractDatabaseInfoUrlFromDicon() {
        final EmJdbcDiconExtractor jdbcDiconExtractor = new EmJdbcDiconExtractor();
        return jdbcDiconExtractor.extractDatabaseUrl(getOutputDirectory());
    }

    protected String extractRootPackageFromDicon() {
        final EmConventionDiconExtractor conventionDiconExtractor = new EmConventionDiconExtractor();
        return conventionDiconExtractor.extractRootPackage(getOutputDirectory());
    }

    // -----------------------------------------------------
    //                                          Version Info
    //                                          ------------
    protected void layoutVersionInfoControl(final Composite container) {
        String indent = "  ";
        setupVersionInfoDBFlute(container, indent + "DBFlute Version (*)");
    }

    protected void setupVersionInfoDBFlute(final Composite container, String title) {
        createLabel(container, title);
        versionInfoDBFluteText = createText(container, "");
        initializeCommonProperty(versionInfoDBFluteText);
    }

    // -----------------------------------------------------
    //                                        Latest Version
    //                                        --------------
    protected void layoutLatestVersionButton(final Composite container) {
        // Button
        final Button button = new Button(container, SWT.PUSH);
        button.setText("&Latest Version");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                try {
                    setupLatestVersion();

                    // The button of down-load DBFlute is allowed to push at this timing!
                    versionInfoDownloadDBFluteButton.setEnabled(true);
                } catch (RuntimeException e) {
                    final String simpleName = e.getClass().getSimpleName();
                    String msg = "setupLatestVersion() threw the " + simpleName + "!";
                    EmExceptionHandler.show(msg, e);
                    setErrorMessage(simpleName + ": " + e.getMessage());
                }
            }
        });
        versionInfoLatestVersionButton = button;
    }

    protected void setupLatestVersion() {
        final EmMetaFromWebSite config = new EmMetaFromWebSite();
        config.loadMeta();

        final String latestVersionDBFlute = config.getLatestVersionDBFlute();
        if (latestVersionDBFlute != null) {
            versionInfoDBFluteText.setText(latestVersionDBFlute);
        }

        createDialogChangedDefaultModifyListener().handle();
    }

    protected void layoutLatestSnapshotVersionButton(final Composite container) {
        // Button
        final Button button = new Button(container, SWT.PUSH);
        button.setText("&Latest Snapshot Version");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                try {
                    setupLatestSnapshotVersion();

                    // The button of down-load DBFlute is allowed to push at this timing!
                    versionInfoDownloadDBFluteButton.setEnabled(true);
                } catch (RuntimeException e) {
                    final String simpleName = e.getClass().getSimpleName();
                    String msg = "setupLatestSnapshotVersion() threw the " + simpleName + "!";
                    EmExceptionHandler.show(msg, e);
                    setErrorMessage(simpleName + ": " + e.getMessage());
                }
            }
        });
        versionInfoLatestSnapshotVersionButton = button;
    }

    protected void setupLatestSnapshotVersion() {
        final EmMetaFromWebSite config = new EmMetaFromWebSite();
        config.loadMeta();

        final String latestVersionDBFlute = config.getLatestSnapshotVersionDBFlute();
        if (latestVersionDBFlute != null) {
            versionInfoDBFluteText.setText(latestVersionDBFlute);
        }

        createDialogChangedDefaultModifyListener().handle();
    }

    // -----------------------------------------------------
    //                                      Download DBFlute
    //                                      ----------------
    protected void layoutDownloadDBFluteButton(final Composite container) {
        // Button
        final Button button = new Button(container, SWT.PUSH);
        button.setText("&Download DBFlute");
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                try {
                    setupDownloadDBFlute();

                    // This is the pattern of non progress.
                    // downloadDBFluteWithProgress(new NullProgressMonitor());
                } catch (RuntimeException e) {
                    final String simpleName = e.getClass().getSimpleName();
                    String msg = "setupDownloadDBFlute() threw the " + simpleName + "!";
                    EmExceptionHandler.show(msg, e);
                }
            }
        });
        button.setEnabled(false);// as Default!
        versionInfoDownloadDBFluteButton = button;
        createEmptyLabel(container);
    }

    protected void setVersionInfoDownloadDBFluteButtonVisible(boolean visible) {
        versionInfoDownloadDBFluteButton.setVisible(visible);
    }

    protected void setupDownloadDBFlute() {
        final IRunnableWithProgress runnable = createDownloadDBFluteRunnableWithProgress();
        try {
            getContainer().run(false, false, runnable);
        } catch (InvocationTargetException e) {
            final Throwable targetException = e.getTargetException();
            final String simpleName = targetException.getClass().getSimpleName();
            String msg = "getContainer().run(false, false, runnable) threw the " + simpleName + "!";
            EmExceptionHandler.show(msg, targetException);
            if (targetException instanceof OutOfMemoryError) {
                setErrorMessage(simpleName + ": " + targetException.getMessage());
            }
        } catch (InterruptedException e) {
            final String simpleName = e.getClass().getSimpleName();
            String msg = "getContainer().run(false, false, runnable) threw the " + simpleName + "!";
            EmExceptionHandler.show(msg, e);
        }
    }

    protected IRunnableWithProgress createDownloadDBFluteRunnableWithProgress() {
        final IRunnableWithProgress runnable = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                downloadDBFluteWithProgress(monitor, asResult());
            }
        };
        return runnable;
    }

    protected void downloadDBFluteWithProgress(IProgressMonitor monitor, DBFluteNewClientPageResult result) {
        final String downloadVersion = result.getVersionInfoDBFlute();
        if (downloadVersion == null || downloadVersion.trim().length() == 0) {
            // Doesn't available?
            // versionInfoDownloadDBFluteButton.setEnabled(false);
            createDialogChangedDefaultModifyListener().handle();
            return;
        }
        monitor.beginTask("", 3);
        monitor.worked(1);

        final File mydbflutePureFile;
        {
            final EmWorkspaceRoot workspaceRoot = EmWorkspaceRoot.create();
            final EmContainer container = workspaceRoot.findContainer(getOutputDirectory());
            container.createDir("mydbflute");
            mydbflutePureFile = container.findContainer("mydbflute").getLocationPureFile();
        }

        final String downloadUrl;
        {
            final EmMetaFromWebSite meta = new EmMetaFromWebSite();
            meta.loadMeta();
            downloadUrl = meta.buildDownloadUrlDBFlute(downloadVersion);
        }
        final String dbfluteVersionExpression = "dbflute-" + downloadVersion;

        monitor.subTask("...Downloading DBFlute to ./mydbflute/" + dbfluteVersionExpression);
        monitor.worked(2);

        final String zipFilename;
        {
            zipFilename = mydbflutePureFile.getAbsolutePath() + "/" + dbfluteVersionExpression + ".zip";
            final URL url = EmURLUtil.createURL(downloadUrl);
            EmURLUtil.makeFileAndClose(url, zipFilename);
        }

        final ZipInputStream zipIn = EmZipInputStreamUtil.createZipInputStream(zipFilename);
        final String extractDirectoryBase = mydbflutePureFile.getAbsolutePath() + "/" + dbfluteVersionExpression;
        EmZipInputStreamUtil.extractAndClose(zipIn, extractDirectoryBase);

        EmFileUtil.deleteFile(zipFilename); // After Care!

        //createDialogChangedDefaultModifyListener().handle();

        monitor.worked(3);
        monitor.done();
    }

    // ===================================================================================
    //                                                                  Component Creation
    //                                                                  ==================
    protected Label createLabel(final Composite container, String labelText) {
        final Label label = new Label(container, SWT.NULL);
        label.setText(labelText);
        return label;
    }

    protected Label createEmptyLabel(final Composite container) {
        final Label label = new Label(container, SWT.NULL);
        label.setText("");
        return label;
    }

    protected Text createText(final Composite container) {
        final Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
        return text;
    }

    protected Text createText(final Composite container, String defaultText) {
        final Text text = new Text(container, SWT.BORDER | SWT.SINGLE);
        text.setText(defaultText);
        return text;
    }

    protected void initializeCommonProperty(Text text) {
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        text.setLayoutData(gd);
        text.addModifyListener(createDialogChangedDefaultModifyListener());
    }

    // ===================================================================================
    //                                                                Dicon Initialization
    //                                                                ====================
    protected boolean initializePackageBaseAsDiconIfExists(String dbfluetProject, boolean force) {
        final String packageBase = getPackageBase();
        if (force || !isValidString(packageBase) || packageBase.equals(DEFAULT_PACKAGE_BASE)) {
            final String rootPackageFromDicon = extractRootPackageFromDicon();
            if (rootPackageFromDicon != null && rootPackageFromDicon.trim().length() != 0) {
                packageBaseText.setText(rootPackageFromDicon + ".dbflute");
                return true;
            }
        }
        return false;
    }

    protected boolean initializeDatabaseInfoUrlAsDiconIfExists() {
        final String databaseInfoUrlFromDicon = extractDatabaseInfoUrlFromDicon();
        if (databaseInfoUrlFromDicon != null && databaseInfoUrlFromDicon.trim().length() != 0) {
            databaseInfoUrlText.setText(databaseInfoUrlFromDicon);
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                   Listener Creation
    //                                                                   =================
    protected DBFluteNewClientDefaultModifyListener createDialogChangedDefaultModifyListener() {
        return new DBFluteNewClientDefaultModifyListener(this);
    }

    protected DBFluteNewClientDefaultModifyListener createDialogChangedDefaultModifyListenerWithDatabaseAid() {
        return new DBFluteNewClientDefaultModifyListener(this).aidDatabase();
    }

    // ===================================================================================
    //                                                               Definition Extracting
    //                                                               =====================
    public java.util.List<String> extractDatabaseList() {
        return DatabaseInfoDef.extractDatabaseList();
    }

    public java.util.List<String> extractDatabaseInfoDriverList() {
        return DatabaseInfoDef.extractDriverList();
    }

    public java.util.List<String> extractTargetContainerList() {
        return TargetContainerDef.extractTargetContainerList();
    }

    public DatabaseInfoDef findDatabaseInfo(String database) {
        return DatabaseInfoDef.findDatabaseInfo(database);
    }

    public int findDatabaseInfoIndex(String database) {
        return DatabaseInfoDef.findIndex(database);
    }

    // ===================================================================================
    //                                                                  Control Delegation
    //                                                                  ==================
    public void delegateSettingDatabaseColor(int red, int green, int blue) {
        // TODO: I don't know why it is Invalid thread access!
        //final Display display = new Display();
        //final Color color = new Color(display, red, green, blue);
        //databaseCombo.setBackground(color);
    }

    public void delegateSettingDatabaseInfoDriverText(String drvier) {
        databaseInfoDriverText.setText(drvier);
    }

    public void delegateSettingDatabaseInfoUrlText(String urlTemplate) {
        String text = databaseInfoUrlText.getText();
        if (text == null || text.trim().length() == 0 || text.contains("xxx")) {
            databaseInfoUrlText.setText(urlTemplate);
        }
    }

    // ===================================================================================
    //                                                                           As Result
    //                                                                           =========
    public DBFluteNewClientPageResult asResult() {
        final String containerName = getOutputDirectory();
        final String projectName = getProject();
        final String database = getDatabase();
        final String targetContainer = getTargetContainer();
        final String packageBase = getPackageBase();
        final String databaseInfoDriver = getDatabaseInfoDriver();
        final String databaseInfoUrl = getDatabaseInfoUrl();
        final String databaseInfoSchema = getDatabaseInfoSchema();
        final String databaseInfoUser = getDatabaseInfoUser();
        final String databaseInfoPassword = getDatabaseInfoPassword();
        final String versionInfoDBFlute = getVersionInfoDBFlute();
        final DBFluteNewClientPageResult result = new DBFluteNewClientPageResult();
        result.setOutputDirectory(containerName);
        result.setProject(projectName);
        result.setDatabase(database);
        result.setTargetContainer(targetContainer);
        result.setPackageBase(packageBase);
        result.setDatabaseInfoDriver(databaseInfoDriver);
        result.setDatabaseInfoUrl(databaseInfoUrl);
        result.setDatabaseInfoSchema(databaseInfoSchema);
        result.setDatabaseInfoUser(databaseInfoUser);
        result.setDatabaseInfoPassword(databaseInfoPassword);
        result.setVersionInfoDBFlute(versionInfoDBFlute);
        return result;
    }

    // ===================================================================================
    //                                                                  Top Level Resource
    //                                                                  ==================
    public IResource findTopLevelResource(String projectName) {
        if (projectName == null) {
            return null;
        }
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        return root.findMember(projectName);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected void debug(String log) {
        if (true) {
            System.out.println(log);
        }
    }

    protected boolean isValidString(String str) {
        return str != null && str.trim().length() != 0;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                           Input Value
    //                                           -----------
    public String getOutputDirectory() {
        return outputDirectoryText.getText();
    }

    public String getProject() {
        return projectText.getText();
    }

    public String getDatabase() {
        final int selectionIndex = databaseCombo.getSelectionIndex();
        if (selectionIndex < 0) {
            return "";
        }
        final List<String> databaseList = extractDatabaseList();
        return databaseList.get(selectionIndex);
    }

    public String getTargetContainer() {
        final int selectionIndex = targetContainerCombo.getSelectionIndex();
        if (selectionIndex < 0) {
            return "";
        }
        final List<String> targetContainerList = extractTargetContainerList();
        return targetContainerList.get(selectionIndex);
    }

    public String getPackageBase() {
        return packageBaseText.getText();
    }

    public String getDatabaseInfoDriver() {
        return databaseInfoDriverText.getText();
    }

    public String getDatabaseInfoUrl() {
        return databaseInfoUrlText.getText();
    }

    public String getDatabaseInfoSchema() {
        return databaseInfoSchemaText.getText();
    }

    public String getDatabaseInfoUser() {
        return databaseInfoUserText.getText();
    }

    public String getDatabaseInfoPassword() {
        return databaseInfoPasswordText.getText();
    }

    public String getVersionInfoDBFlute() {
        return versionInfoDBFluteText.getText();
    }
}