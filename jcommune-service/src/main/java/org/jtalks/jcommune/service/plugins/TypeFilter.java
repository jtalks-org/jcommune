package org.jtalks.jcommune.service.plugins;

import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.model.plugins.Plugin;

/**
 *
 * @author Evgeny Naumenko
 */
public class TypeFilter implements PluginFilter {

    private Class<Plugin> type;

    public TypeFilter(Class<Plugin> type) {
        Validate.notNull(type);
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Plugin plugin) {
        return type.isInstance(plugin);
    }
}
