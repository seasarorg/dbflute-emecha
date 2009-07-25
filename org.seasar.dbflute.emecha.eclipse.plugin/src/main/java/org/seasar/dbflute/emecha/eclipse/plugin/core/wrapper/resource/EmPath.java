package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.resource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmPath {

    private IPath path;

    public EmPath(IPath path) {
        this.path = path;
    }

    public EmPath(String path) {
        this.path = new Path(path);
    }

    public String[] segments() {
        return path.segments();
    }

    public IPath getPath() {
        return path;
    }

    public void setPath(IPath path) {
        this.path = path;
    }
}
