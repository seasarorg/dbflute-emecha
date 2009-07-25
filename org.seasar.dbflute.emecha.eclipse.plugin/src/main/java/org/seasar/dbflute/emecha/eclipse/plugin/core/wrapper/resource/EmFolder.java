package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.dbflute.emecha.eclipse.plugin.core.exception.EmExceptionHandler;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmFolder {

    private IFolder folder;

    public EmFolder(IFolder folder) {
        this.folder = folder;
    }

    public void create(boolean force, boolean local, IProgressMonitor monitor) {
        try {
            folder.create(force, local, monitor);
        } catch (CoreException e) {
            String msg = this.getClass().getName() + "#create(): force=" + force + " local=" + local;
            msg = msg + " monitor=" + monitor;
            EmExceptionHandler.throwAsPluginException(msg, e);
        }
    }

    public IFolder getFolder() {
        return folder;
    }

    public void setFolder(IFolder folder) {
        this.folder = folder;
    }
}
