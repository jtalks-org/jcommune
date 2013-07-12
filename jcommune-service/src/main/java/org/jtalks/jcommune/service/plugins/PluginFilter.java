package org.jtalks.jcommune.service.plugins;

import org.jtalks.jcommune.model.plugins.Plugin;

/**
 *
 */
public interface PluginFilter {

    public boolean accept(Plugin plugin);
}
