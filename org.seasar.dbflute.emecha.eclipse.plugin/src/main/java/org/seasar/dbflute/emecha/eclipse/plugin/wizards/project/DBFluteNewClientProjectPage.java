/**
 *
 */
package org.seasar.dbflute.emecha.eclipse.plugin.wizards.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.DBFluteNewClientPage;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.DBFluteNewClientPageResult;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.client.listener.DBFluteNewClientDefaultModifyListener;
import org.seasar.dbflute.emecha.eclipse.plugin.wizards.project.listener.DBFluteNewProjectDefaultModifyListener;

/**
 *
 */
public class DBFluteNewClientProjectPage extends DBFluteNewClientPage {

    public DBFluteNewClientProjectPage(IStructuredSelection selection) {
        super(selection);
    }

    private GetValueDelegate<String> outputDirectoryDelegate;
    protected void setOutputDirectoryDelegate(GetValueDelegate<String> delegate) {
        outputDirectoryDelegate = delegate;
    }
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        setVersionInfoDownloadDBFluteButtonVisible(false);
    }
    @Override
    protected void setupOutputDirectory(Composite container) {
        // not visible
    }

    @Override
    public String getOutputDirectory() {
        return outputDirectoryDelegate.delegate();
    }
    @Override
    protected void setupFromDicon() {
        // don't exists
    }

    @Override
    protected DBFluteNewClientDefaultModifyListener createDialogChangedDefaultModifyListener() {
        return new DBFluteNewProjectDefaultModifyListener(this);
    }

    @Override
    protected DBFluteNewClientDefaultModifyListener createDialogChangedDefaultModifyListenerWithDatabaseAid() {
        return new DBFluteNewProjectDefaultModifyListener(this).aidDatabase();
    }

    public void handleDownloadDBFluteWithProgress(IProgressMonitor monitor, DBFluteNewClientPageResult result) {
        this.downloadDBFluteWithProgress(monitor, result);
    }

    public boolean isJavaProject() {
        return true;
    }

    protected void createJavaProject(IProgressMonitor monitor, IProject project) throws CoreException, IOException {
        createFolders(project, monitor);
        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[]{"org.eclipse.jdt.core.javanature"});
        ICommand command = description.newCommand();
        command.setBuilderName("org.eclipse.jdt.core.javabuilder");
        ICommand[] buildSpec = new ICommand[]{command};
        description.setBuildSpec(buildSpec);
        project.setDescription(description, monitor);
        createClasspathFile(project, monitor);
    }
    protected void createFolders(IProject project, IProgressMonitor monitor) throws CoreException {
        IFolder srcFolder = project.getFolder("src");
        srcFolder.create(true, true, new SubProgressMonitor(monitor, 1));

        IFolder mainFolder = srcFolder.getFolder("main");
        mainFolder.create(true, true, new SubProgressMonitor(monitor, 1));

        IFolder mainJavaFolder = mainFolder.getFolder("java");
        mainJavaFolder.create(true, true, new SubProgressMonitor(monitor, 1));

        IFolder mainResourceFolder = mainFolder.getFolder("resources");
        mainResourceFolder.create(true, true, new SubProgressMonitor(monitor, 1));

        IFolder testFolder = srcFolder.getFolder("test");
        testFolder.create(true, true, new SubProgressMonitor(monitor, 1));

        IFolder testJavaFolder = testFolder.getFolder("java");
        testJavaFolder.create(true, true, new SubProgressMonitor(monitor, 1));

        IFolder testResourceFolder = testFolder.getFolder("resources");
        testResourceFolder.create(true, true, new SubProgressMonitor(monitor, 1));
    }
    protected void createClasspathFile(IProject project, IProgressMonitor monitor) throws CoreException, IOException {
        IFile classpathFile = project.getFile(".classpath");
        InputStream source = getInitialSource();
        classpathFile.create(source, false, new SubProgressMonitor(monitor, 1));
    }
    private InputStream getInitialSource() throws UnsupportedEncodingException {
        StringBuilder str = new StringBuilder();
        String separator = System.getProperty("line.separator", "\n");
        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(separator);
        str.append("<classpath>").append(separator);
        str.append("\t<classpathentry kind=\"src\" path=\"src/main/java\"/>").append(separator);
        str.append("\t<classpathentry kind=\"src\" path=\"src/main/resources\"/>").append(separator);
        str.append("\t<classpathentry kind=\"src\" output=\"target/test-classes\" path=\"src/test/java\"/>").append(separator);
        str.append("\t<classpathentry kind=\"src\" output=\"target/test-classes\" path=\"src/test/resources\"/>").append(separator);
        str.append("\t<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>").append(separator);
        str.append("\t<classpathentry kind=\"output\" path=\"target/classes\"/>").append(separator);
        str.append("</classpath>").append(separator);
        return new ByteArrayInputStream(str.toString().getBytes("UTF-8"));
    }
}
