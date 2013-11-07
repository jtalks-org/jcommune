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
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Load plugins from path and save configuration for them.
 * Also load plugin for class name.
 *
 * @author Anuar_Nurmakanov
 * @author Evgeny Naumenko
 */
public class PluginLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginLoader.class);

    private URLClassLoader classLoader;
    private WatchKey watchKey;
    private String folder;
    private List<Plugin> plugins;
    private WatchService watchService;
    private PluginConfigurationDao pluginConfigurationDao;

    /**
     * Constructs an instance for loading plugins from passed path to plugins directory.
     *
     * @param pluginsFolderPath      a path to a folder that contains plugins
     * @param pluginConfigurationDao to load and save configuration for loaded plugins
     * @throws java.io.IOException when it's impossible to start tracking changes in plugins folder
     */
    public PluginLoader(String pluginsFolderPath, PluginConfigurationDao pluginConfigurationDao) throws IOException {
        this.pluginConfigurationDao = pluginConfigurationDao;
        Validate.notEmpty(pluginsFolderPath);
        this.folder = this.resolveUserHome(pluginsFolderPath);
        Path path = Paths.get(folder);
        watchService = FileSystems.getDefault().newWatchService();
        watchKey = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
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
     * Will be called by container after bean creation.
     */
    public void init() {
        this.initPluginList();
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
        LOGGER.trace("JCommune forum has {} plugins now.", filtered.size());
        loadConfigurationFor(filtered);
        return filtered;
    }

    private void synchronizePluginList() {
        List events = watchKey.pollEvents();
        if (!events.isEmpty()) {
            watchKey.reset();
            try {
                classLoader.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close plugin class loader", e);
            }
            this.initPluginList();
        }
    }

    private synchronized void initPluginList() {
        classLoader = new PluginClassLoader(folder);
        ServiceLoader<Plugin> pluginLoader = ServiceLoader.load(Plugin.class, classLoader);
        List<Plugin> plugins = new ArrayList<>();
        for (Plugin plugin : pluginLoader) {
            plugins.add(plugin);
        }
        this.plugins = plugins;
    }

    /**
     * Get plugin by class name.
     *
     * @param cl class name
     * @return plugin
     */
    public Plugin getPluginByClassName(Class<? extends Plugin> cl) {
        PluginFilter pluginFilter = new TypeFilter(cl);
        List<Plugin> plugins = getPlugins(pluginFilter);
        return !plugins.isEmpty() ? plugins.get(0) : null;
    }

    private void loadConfigurationFor(List<Plugin> plugins) {
        for (Plugin plugin : plugins) {
            String name = plugin.getName();
            PluginConfiguration configuration;
            try {
                configuration = pluginConfigurationDao.get(name);
            } catch (NotFoundException e) {
                configuration = new PluginConfiguration(name, false, plugin.getDefaultConfiguration());
                pluginConfigurationDao.saveOrUpdate(configuration);
            }

            try {
                plugin.configure(configuration);
            } catch (UnexpectedErrorException e) {
                LOGGER.error("Can't configure plugin during loading. Plugin name = " + plugin.getName());
            }
        }
    }

    /**
     * Will be called by container to release resource before bean destroying.
     */
    public void destroy() {
        try {
            classLoader.close();
            watchService.close();
        } catch (IOException e1) {
            LOGGER.error("Failed to close plugin class loader", e1);
        }
    }

}
