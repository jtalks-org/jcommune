/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.service.plugins;

import com.google.common.collect.Lists;
import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 *
 */
public class PluginManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

    private URLClassLoader classLoader;
    private WatchKey watchKey;
    private String folder;
    private List<Plugin> plugins;

    public PluginManager(String folderPath) throws IOException {
        Validate.notEmpty(folderPath);
        this.folder = this.resolveUserHome(folderPath);
        Path path = Paths.get(folder);
        WatchService watcher = FileSystems.getDefault().newWatchService();
        watchKey = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        this.initPluginList();
    }

    private String resolveUserHome(String path) {
        if (path.contains("~")) {
            String home = System.getProperty("user.home");
            return path.replace("~", home);
        } else {
            return path;
        }
    }

    /**
     * Returns actual list of plugins available. Client code should not cache the plugin
     * references and always use this method to obtain a plugin reference as needed.
     * Violation of this simple rule may cause memory leaks.
     *
     * @return list of plugins available at the moment
     */
    public synchronized List<Plugin> getPlugins(PluginFilter... filters) {
        this.synchronizePluginList();
        List<Plugin> filtered = new ArrayList<Plugin>(plugins.size());
        plugins:
        for (Plugin plugin : plugins) {
            for (PluginFilter filter : filters) {
                if (!filter.accept(plugin)) {
                    continue plugins;
                }
            }
            filtered.add(plugin);
        }
        return filtered;
    }

    private void synchronizePluginList() {
        List events = watchKey.pollEvents();
        if (!events.isEmpty()) {
            watchKey.reset();
            this.closeClassLoader();
            this.initPluginList();
        }
    }

    private void closeClassLoader() {
        try {
            classLoader.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close plugin class loader", e);
        }
    }

    private synchronized void initPluginList() {
        classLoader = new PluginClassLoader(folder);
        ServiceLoader<Plugin> pluginLoader = ServiceLoader.load(Plugin.class, classLoader);
        Iterator<Plugin> iterator = pluginLoader.iterator();
        plugins = Lists.newArrayList(iterator);
    }
}
