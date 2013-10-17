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

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.jtalks.jcommune.model.plugins.StatefullPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom ClassLoader implementation to load classes from a specified folder.
 * It's used as a discovery class loader for JCommune plugin code jars.
 * <p>Limitations of current implementation:
 * <p> - Only .jar files are supported
 * <p> - Subfolders and their content is ignored, so scan is non-recursive
 *
 * @author Evgeny Naumenko
 */
public class PluginClassLoader  extends URLClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginClassLoader.class);

    /**
     * @param folder lookup folder. It's non-recursive, so subfolders and it's content will be ignored
     */
    public PluginClassLoader (String folder) {
        super(resolvePluginLocations(folder), StatefullPlugin.class.getClassLoader());
        LOGGER.debug("Plugin class loader created for folder {}");
    }

    private static URL[] resolvePluginLocations(String folder) {
        File file = new File(folder);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException(folder + " is not a valid plugin folder");
        }
        FilenameFilter jarFilter = new SuffixFileFilter(".jar", IOCase.INSENSITIVE);
        File[] pluginJars = file.listFiles(jarFilter);
        return createUrls(pluginJars);
    }

    private static URL[] createUrls(File[] files){
        List<URL> urls = new ArrayList<>(files.length);
        for (File jar : files){
            try {
                urls.add(jar.toURI().toURL());
            } catch (MalformedURLException e) {
                LOGGER.warn("Unable to load a plugin from JAR", e);
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }


}
