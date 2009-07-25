package org.seasar.dbflute.emecha.eclipse.plugin.core.wrapper.runtime;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author jflute
 * @since 0.1.0 (2007/07/21 Saturday)
 */
public class EmProgressMonitor {

    private IProgressMonitor monitor;

    public EmProgressMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    public IProgressMonitor getMonitor() {
        return monitor;
    }
}
