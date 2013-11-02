/*
 * Copyright 2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.dbflute.emecha.synchronizer.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dbflute.emecha.synchronizer.nls.Messages;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Refresh Handler
 * @author schatten
 */
public class RefreshHandler implements HttpHandler {
    protected static final Map<String, Integer> STRING_TO_DEPTH = new HashMap<String, Integer>();
    static {
        STRING_TO_DEPTH.put("ZERO", IResource.DEPTH_ZERO); //$NON-NLS-1$
        STRING_TO_DEPTH.put("ONE", IResource.DEPTH_ONE); //$NON-NLS-1$
        STRING_TO_DEPTH.put("INFINITE", IResource.DEPTH_INFINITE); //$NON-NLS-1$
    }

    protected int toDepth(String depth) {
        if (depth == null || depth.length() < 1) {
            return IResource.DEPTH_ONE;
        }
        Integer i = STRING_TO_DEPTH.get(depth.toUpperCase());
        return i != null ? i : IResource.DEPTH_ONE;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
        try {
            final List<RefreshTask> list = new LinkedList<RefreshTask>();
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            String query = exchange.getRequestURI().getQuery();
            if (query != null) {
                String[] params = query.split("&"); //$NON-NLS-1$
                for (String param : params) {
                    int index = param.indexOf("="); //$NON-NLS-1$
                    String name;
                    String val = null;
                    if (index > -1) {
                        name = param.substring(0, index);
                        val = param.substring(index + 1);
                    } else {
                        name = param;
                    }
                    IResource r = root.findMember(name);
                    if (r != null && r.exists()) {
                        list.add(new RefreshTask(r, toDepth(val)));
                        writer.println(r.getFullPath());
                    }
                }
            }

            if (list.isEmpty()) {
                list.add(new RefreshTask(root, IResource.DEPTH_INFINITE));
                writer.println(Messages.MSG_ALL_RESOURCE);
            }
            new WorkspaceJob(Messages.MSG_REFRESH_RESOURCE) {
                @Override
                public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                    monitor.beginTask(Messages.MSG_REFRESH_RESOURCE, list.size());
                    for (RefreshTask rt : list) {
                        rt.run(new SubProgressMonitor(monitor, 1));
                    }
                    monitor.done();
                    return Status.OK_STATUS;
                }
            }.schedule();
        } finally {
            writer.flush();
            exchange.sendResponseHeaders(200, output.size());
            OutputStream response = exchange.getResponseBody();
            response.write(output.toByteArray());
        }
    }

    private class RefreshTask {
        protected IResource r;
        protected int depth;

        public RefreshTask(IResource r, int depth) {
            this.r = r;
            this.depth = depth;
        }

        public void run(IProgressMonitor monitor) throws CoreException {
            r.refreshLocal(depth, monitor);
        }
    }
}
