package org.seasar.dbflute.emecha.eclipse.plugin.wizards.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;
import org.seasar.dbflute.emecha.eclipse.plugin.core.prototype.EmPrototypeEntry;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.io.EmFileUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.core.util.net.EmURLUtil;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmContainer;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmFile;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource.EmWorkspaceRoot;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.runtime.EmProgressMonitor;

/**
 * @author jflute
 * @since 0.1.0 (2007/08/11 Saturday)
 */
public class DBFluteNewClientPageFinishHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBFluteNewClientPageResult newClientPageResult;
    protected IResource topLevelResource;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public DBFluteNewClientPageFinishHandler(DBFluteNewClientPageResult newWizardPageResult, IResource topLevelResource) {
        this.newClientPageResult = newWizardPageResult;
        this.topLevelResource = topLevelResource;
    }

    // ===================================================================================
    //                                                                     Finish Handling
    //                                                                     ===============
    public void handleFinish(IProgressMonitor monitor) throws CoreException {
        // top level definition
        final EmProgressMonitor progressMonitor = new EmProgressMonitor(monitor);
        final EmWorkspaceRoot workspaceRoot = EmWorkspaceRoot.create();
        final EmContainer container = workspaceRoot.findContainer(newClientPageResult.getOutputDirectory());

        // create directory of dbflute_[project].
        container.createDir(buildDBFluteClientDirectoryName(newClientPageResult));

        final EmPrototypeEntry prototypeEntry = EmPrototypeEntry.create();
        final List<URL> entryList = prototypeEntry.findDBFluteClientEntries();
        for (URL url : entryList) {
            final String path = buildOutputPath(url.getPath());

            if (isDirectory(path)) { // for directory
                container.createDir(extractDirectoryPath(path));
                continue;
            }

            // - - - - - - - - -
            // Here is for File!
            // - - - - - - - - -

            boolean filterFileText = false;
            if (isAvailableFilteringFileText(path)) {
                filterFileText = true;
            }

            final InputStream openStream = EmURLUtil.openStream(url);
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(openStream, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                String msg = "The encoding is unsupported: UTF-8";
                EmExceptionHandler.throwAsPluginException(msg, e);
                return;
            }
            final StringBuilder sb = new StringBuilder();
            while (true) {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    if (filterFileText) {
                        line = filterFileText(line);
                    }
                    sb.append(line).append(ln());
                } catch (IOException e) {
                    String msg = "";
                    EmExceptionHandler.throwAsPluginException(msg, e);
                    return;
                }
            }
            final EmFile outputFile = container.createFile(path);
            try {
                final byte[] bytes = sb.toString().getBytes("UTF-8");
                outputFile.create(new ByteArrayInputStream(bytes), true, progressMonitor);
            } catch (UnsupportedEncodingException e) {
                String msg = "The encoding is unsupported: UTF-8";
                EmExceptionHandler.throwAsPluginException(msg, e);
                return;
            }
        }

        refreshResources(monitor);
        monitor.worked(1);
    }

    protected void refreshResources(IProgressMonitor monitor) {
        if (topLevelResource != null) {
            try {
                topLevelResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            } catch (CoreException ignored) {
                String msg = "IResource.refreshLocal() threw the exception!";
                EmExceptionHandler.show(msg, ignored);
            }
        }
    }

    protected boolean isAvailableFilteringFileText(final String path) {
        if (path.contains(buildBuildPropertiesName(newClientPageResult))) {
            return true;
        }
        if (path.contains(".dfprop")) {
            return true;
        }
        if (path.contains("_project.bat") || path.contains("_project.sh")) {
            return true;
        }
        return false;
    }

    protected boolean isDirectory(String path) {
        return path.endsWith("/");
    }

    protected String extractDirectoryPath(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.lastIndexOf("/"));
        }
        return path;
    }

    protected String ln() {
        return "\n";
    }

    // ===================================================================================
    //                                                                     Building Helper
    //                                                                     ===============
    protected String buildOutputPath(String path) {
        final String fileSeparator = EmPrototypeEntry.FILE_SEPARATOR;
        path = EmFileUtil.removeFirstFileSeparatorIfNeeds(path, fileSeparator);

        final String dbfluteClientDirectoryName = buildDBFluteClientDirectoryName(newClientPageResult);
        final String dbfluteClientPath = EmPrototypeEntry.buildDBFluteClientPath();
        if (path.contains(dbfluteClientPath)) {
            path = replace(path, dbfluteClientPath, dbfluteClientDirectoryName);
        }
        path = EmFileUtil.removeFirstFileSeparatorIfNeeds(path, fileSeparator);

        final String projectNameMark = EmPrototypeEntry.PROJECT_NAME_MARK;
        if (path.contains(projectNameMark)) {
            path = replace(path, projectNameMark, newClientPageResult.getProject());
        }

        return path;
    }

    protected String buildDBFluteClientDirectoryName(DBFluteNewClientPageResult result) {
        return "dbflute_" + result.getProject();
    }

    protected String buildBuildPropertiesName(DBFluteNewClientPageResult result) {
        return "build.properties";
    }

    // ===================================================================================
    //                                                                    Filtering Helper
    //                                                                    ================
    protected String filterFileText(String line) {
        if (line.startsWith("#")) { // exclude comment line.
            return line;
        }
        return newClientPageResult.filterLineByResult(line, "${", "}");
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String str, String fromStr, String toStr) {
        if (str == null || fromStr == null || toStr == null) {
            return null;
        }
        StringBuilder sb = null; // lazy load
        int pos = 0;
        int pos2 = 0;
        do {
            pos = str.indexOf(fromStr, pos2);
            if (pos2 == 0 && pos < 0) { // first loop and not found
                return str; // without creating StringBuilder 
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            if (pos == 0) {
                sb.append(toStr);
                pos2 = fromStr.length();
            } else if (pos > 0) {
                sb.append(str.substring(pos2, pos));
                sb.append(toStr);
                pos2 = pos + fromStr.length();
            } else { // (pos < 0) second or after loop only
                sb.append(str.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    protected void debug(String log) {
        if (true) {
            System.out.println(log);
        }
    }
}
