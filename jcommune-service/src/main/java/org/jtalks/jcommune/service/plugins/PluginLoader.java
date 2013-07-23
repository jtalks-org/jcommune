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

import org.apache.commons.lang.Validate;
import org.jtalks.common.service.exceptions.NotFoundException;
import org.jtalks.jcommune.model.dao.PluginConfigurationDao;
import org.jtalks.jcommune.model.entity.PluginConfiguration;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * @author Anuar_Nurmakanov
 * @author Evgeny Naumenko
 */
public class PluginLoader implements DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);
    //
    private URLClassLoader classLoader;
    private WatchKey watchKey;
    private String folder;
    private List<Plugin> plugins;
    private PluginConfigurationDao pluginConfigurationDao;

    /**
     * @param folderPath
     * @param dao
     * @throws java.io.IOException
     */
    public PluginLoader(String folderPath, PluginConfigurationDao dao) throws IOException {
        this.pluginConfigurationDao = dao;
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
        List<Plugin> filtered = new ArrayList<>(plugins.size());
        plugins:
        for (Plugin plugin : plugins) {
            for (PluginFilter filter : filters) {
                if (!filter.accept(plugin)) {
                    continue plugins;
                }
            }
            filtered.add(plugin);
        }
        LOGGER.debug("JCommune forum has {0} plugins now.", filtered.size());
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

    private synchronized void initPluginList() {
        classLoader = new PluginClassLoader(folder);
        ServiceLoader<Plugin> pluginLoader = ServiceLoader.load(Plugin.class, classLoader);
        List<Plugin> plugins = new ArrayList<>();
        for (Plugin plugin : pluginLoader) {
            String name = plugin.getName();
            PluginConfiguration configuration;
            try {
                configuration = pluginConfigurationDao.get(name);
            } catch (NotFoundException e) {
                configuration = new PluginConfiguration(name, false, plugin.getDefaultConfiguration());
            }
            plugin.configure(configuration);
            plugins.add(plugin);
        }
        this.plugins = plugins;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        this.closeClassLoader();
    }

    private void closeClassLoader() {
        try {
            classLoader.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close plugin class loader", e);
        }
    }
}
