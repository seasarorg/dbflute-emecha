/*
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.emsql.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.EMSqlPlugin;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.SupportDatabase;
import org.seasar.dbflute.emecha.eclipse.plugin.emsql.dialog.SourceContainerDialog;

/**
 * EMSQLの設定画面ページ
 * @author schatten
 */
public class EMSqlPreferencesPage extends PropertyPage implements IWorkbenchPropertyPage {

    /**
     *
     */
    public EMSqlPreferencesPage() {
        super();
    }
    private String customDirectory;
    private SupportDatabase customDatabase;
    private Text directoryInput;
    private boolean changed = false;
    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        this.noDefaultAndApplyButton();
        Composite composite = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        composite.setLayout(layout);

        // directory
        Label directoryLabel = new Label(composite, SWT.NONE);
        directoryLabel.setFont(JFaceResources.getDialogFont());
        directoryLabel.setText("sqlDirectry");
        directoryLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));

        directoryLabel = new Label(composite, SWT.NONE);
        directoryLabel.setText(":");
        directoryLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));

        directoryInput = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.FILL);
        directoryInput.setFont(JFaceResources.getDialogFont());
        customDirectory = getDefaultOutputDirectory();
        directoryInput.setText(customDirectory == null ? "" : customDirectory);

        directoryInput.setEnabled(true);
        directoryInput.setEditable(true);
        directoryInput.setVisible(true);
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.grabExcessHorizontalSpace = true;

        directoryInput.setLayoutData(layoutData);
        directoryInput.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                customDirectory = ((Text) e.getSource()).getText();
                changed = true;
            }
        });

        Button button = new Button(composite, SWT.PUSH);
        button.setFont(JFaceResources.getDialogFont());
        button.setText("Browse...");//$NON-NLS-1$
        button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        button.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                selectPackageFragmentRoot();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        // database
        Label databaseLabel = new Label(composite, SWT.NONE);
        databaseLabel.setFont(JFaceResources.getDialogFont());
        databaseLabel.setText("SQL Type");
        databaseLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));

        databaseLabel = new Label(composite, SWT.NONE);
        databaseLabel.setText(":");
        databaseLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));

        Combo databaseCombo = new Combo(composite, SWT.Selection);
        databaseCombo.setFont(JFaceResources.getDialogFont());
        databaseCombo.setItems(getDatabaseItems());
        customDatabase = SupportDatabase.nameOf(getDefaultDatabase());
        if (customDatabase == null) {
            databaseCombo.select(0);
        } else {
            databaseCombo.select(customDatabase.ordinal() + 1);
        }

        databaseCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                customDatabase = SupportDatabase.nameOf(((Combo)e.getSource()).getText());
                changed = true;
            }
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        databaseLabel = new Label(composite, SWT.NONE);
        databaseLabel.setText("");
        databaseLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 1, 1));

        return composite;
    }

    private static String DEFAULT_DATABASE_LABEL = "(default)";
    private String[] getDatabaseItems() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(DEFAULT_DATABASE_LABEL); // default
        for (SupportDatabase element : SupportDatabase.values()) {
            switch (element) {
            case SQLite:
            case MSAccess:
            case Sybase:
                // non-selectable
                break;
            default:
                list.add(element.getDbName());
                break;
            }
        }
        return list.toArray(new String[list.size()]);
    }

    protected String getDefaultOutputDirectory() {
        EMSqlPreferences projectPreferences = EMSqlPlugin.getProjectPreferences(getJavaProject().getProject());
        if (getJavaElement().getElementType() == IJavaElement.JAVA_PROJECT) {
            return projectPreferences.getSqlDirectory();
        } else if (getJavaElement().getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
            IPackageFragment packageFragment = (IPackageFragment) getJavaElement().getAncestor(
                    IJavaElement.PACKAGE_FRAGMENT);
            return projectPreferences.getSqlDirectory(packageFragment.getElementName());
        }
        return projectPreferences.getDefaultSqlDirectory();
    }

    protected String getDefaultDatabase() {
        EMSqlPreferences projectPreferences = EMSqlPlugin.getProjectPreferences(getJavaProject().getProject());
        if (getJavaElement().getElementType() == IJavaElement.JAVA_PROJECT) {
            return projectPreferences.getDatabaseName();
        } else if (getJavaElement().getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
            IPackageFragment packageFragment = (IPackageFragment) getJavaElement().getAncestor(
                    IJavaElement.PACKAGE_FRAGMENT);
            return projectPreferences.getDatabaseName(packageFragment.getElementName());
        }
        return projectPreferences.getDefaultDatabaseName();
    }


    /**
     * Creates a spacer control with the given span.
     * The composite is assumed to have <code>MGridLayout</code> as
     * layout.
     * @param parent The parent composite
     */
    public Control createEmptySpace(Composite parent, int span) {
        Label label = new Label(parent, SWT.LEFT);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = false;
        gd.horizontalSpan = span;
        gd.horizontalIndent = 0;
        gd.widthHint = 0;
        gd.heightHint = 0;
        label.setLayoutData(gd);
        return label;
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        if (isChanged()) {
            EMSqlPreferences projectPreferences = EMSqlPlugin.getProjectPreferences(getJavaProject().getProject());
            String customDatabaseName = customDatabase == null ? null : customDatabase.getDbName();
            if (getJavaElement().getElementType() == IJavaElement.JAVA_PROJECT) {
                projectPreferences.setSqlDirectory(customDirectory);
                projectPreferences.setDatabaseName(customDatabaseName);
            } else if (getJavaElement().getElementType() == IJavaElement.PACKAGE_FRAGMENT) {
                IPackageFragment packageFragment = (IPackageFragment) getJavaElement().getAncestor(
                        IJavaElement.PACKAGE_FRAGMENT);
                projectPreferences.setSqlDirectory(packageFragment.getElementName(), customDirectory);
                projectPreferences.setDatabaseName(packageFragment.getElementName(), customDatabaseName);
            }
            projectPreferences.save();
        }
        return true;
    }

    /**
     * 設定内容が編集されたか判定する。
     * @return true : changed status
     */
    private boolean isChanged() {
        return changed || isDirectoryChanged() || isDatabaseChanged();
    }

    /**
     * 出力先が編集されたか判定する。
     * @return true : changed status
     */
    private boolean isDirectoryChanged() {
        return !(getDefaultOutputDirectory() == null ? customDirectory == null || customDirectory.length() == 0
                                                   : getDefaultOutputDirectory().equals(customDirectory) );
    }
    /**
     * データベースタイプが編集されたか判定する。
     * @return true : changed status
     */
    private boolean isDatabaseChanged() {
        String defaultDatabase = getDefaultDatabase();
        SupportDatabase defaultDatabaseType = SupportDatabase.nameOf(defaultDatabase);
        if (customDatabase == null) {
            return defaultDatabaseType != null;
        } else {
            return !customDatabase.equals(defaultDatabaseType);
        }
    }


    public void selectPackageFragmentRoot() {
        IPackageFragmentRoot sourceContainer = SourceContainerDialog.getSourceContainer(getShell(), getWorkspaceRoot());
        if (sourceContainer != null) {
            String root = sourceContainer.getPath().makeRelative().toString();
            customDirectory = root;
            directoryInput.setText(root);
            changed = true;
        }
    }
    protected IJavaProject getJavaProject() {
        return getJavaElement().getJavaProject();
    }
    protected IJavaElement getJavaElement() {
        return (IJavaElement)this.getElement().getAdapter(IJavaElement.class);
    }
    protected IWorkspaceRoot getWorkspaceRoot() {
        return getJavaElement().getJavaProject().getProject().getWorkspace().getRoot();
    }
}
