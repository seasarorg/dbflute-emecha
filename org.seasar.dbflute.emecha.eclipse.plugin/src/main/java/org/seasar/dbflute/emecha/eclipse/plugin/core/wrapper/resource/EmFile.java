package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;
import org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.runtime.EmProgressMonitor;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmFile {

    private IFile file;

    public EmFile(IFile file) {
        this.file = file;
    }

    public void create(InputStream source, boolean force, EmProgressMonitor monitor) {
        try {
            file.create(source, force, monitor.getMonitor());
        } catch (CoreException e) {
            String msg = this.getClass().getName() + "#create(): source=" + source + " force=" + force;
            msg = msg + " monitor=" + monitor;
            EmExceptionHandler.throwAsPluginException(msg, e);
        }
    }

    public void delete(boolean force, EmProgressMonitor monitor) {
        try {
            file.delete(force, monitor.getMonitor());
        } catch (CoreException e) {
            String msg = this.getClass().getName() + "#create(): force=" + force;
            msg = msg + " monitor=" + monitor;
            EmExceptionHandler.throwAsPluginException(msg, e);
        }
    }

    public boolean exists() {
        return file.exists();
    }

    public void setContents(InputStream source, boolean force, boolean keepHistory, EmProgressMonitor monitor) {
        try {
            file.setContents(source, force, keepHistory, monitor.getMonitor());
        } catch (CoreException e) {
            String msg = this.getClass().getName() + "#setContents(): source=" + source + " force=" + force;
            msg = msg + " keepHistory=" + keepHistory + " monitor=" + monitor;
            EmExceptionHandler.throwAsPluginException(msg, e);
        }
    }

    public IFile getFile() {
        return file;
    }

    public void setFile(IFile file) {
        this.file = file;
    }
}
