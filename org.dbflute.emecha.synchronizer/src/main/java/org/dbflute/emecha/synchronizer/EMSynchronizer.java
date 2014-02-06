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
package org.dbflute.emecha.synchronizer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.dbflute.emecha.synchronizer.handler.RefreshHandler;
import org.dbflute.emecha.synchronizer.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.sun.net.httpserver.HttpServer;

/**
 * The activator class controls the plug-in life cycle
 */
public class EMSynchronizer extends AbstractUIPlugin implements IStartup {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.dbflute.emecha.synchronizer"; //$NON-NLS-1$

    // The shared instance
    private static EMSynchronizer plugin;

    private HttpServer server;

    private ExecutorService threadPool;

    private int port;
    /**
     * The constructor
     */
    public EMSynchronizer() {
    }

    /**
     * {@inheritDoc}
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    @Override
    public void earlyStartup() {
        // Startup plugin with Server
        serverStart();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        serverStop();
        super.stop(context);
    }

    public void serverStart() {
        try {
            plugin.port = plugin.getPreferenceStore().getInt(PreferenceConstants.P_LISTEN_PORT);
            String hostname = "localhost";
            HttpServer server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
            threadPool = Executors.newFixedThreadPool(1);
            server.setExecutor(threadPool);
            server.createContext("/", new RefreshHandler());
            server.createContext("/refresh", new RefreshHandler());
            server.start();
            getLog().log(new Status(IStatus.INFO, PLUGIN_ID, "Synchronizer server is started at port"+ port +"."));
            this.server = server;
        } catch (IOException e) {
            getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, "Synchronizer server is not started by error. (Port:" + port + ")", e));
            server = null;
        }
    }

    public void serverStop() throws InterruptedException {
        if (server != null) {
            server.stop(10);
        }
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
            threadPool.awaitTermination(60, TimeUnit.SECONDS);
        }
        threadPool = null;
        server = null;
    }

    public static void serverRestart() {
        EMSynchronizer synchronizer = getDefault();
        try {
            synchronizer.serverStop();
            synchronizer.serverStart();
        } catch (InterruptedException e) {
            synchronizer.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, "Synchronizer server stop error.", e));
        }
    }

    public static int getServerPort() {
        return plugin.port;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static EMSynchronizer getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
