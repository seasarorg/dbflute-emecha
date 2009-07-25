package org.seasar.dbflute.emecha.eclipse.plugin.core.exception;

import org.eclipse.core.runtime.CoreException;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmPluginException extends RuntimeException {

    private static final long serialVersionUID = 6881734980455058322L;

    private CoreException coreException;

    public EmPluginException(String msg) {
        super(msg);
    }

    public EmPluginException(String msg, Throwable t) {
        super(msg, t);
        if (t instanceof CoreException) {
            this.coreException = (CoreException) t;
        }
    }

    public CoreException getCoreException() {
        return coreException;
    }

    public void setCoreException(CoreException coreException) {
        this.coreException = coreException;
    }
}
