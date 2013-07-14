package org.jtalks.jcommune.service.plugins;

import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.model.plugins.Plugin;

/**
 *
 */
public class StateFilter implements PluginFilter {

    private Plugin.State state;

    public StateFilter(Plugin.State state) {
        Validate.notNull(state);
        this.state = state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Plugin plugin) {
        return plugin.getState() == state;
    }
}
