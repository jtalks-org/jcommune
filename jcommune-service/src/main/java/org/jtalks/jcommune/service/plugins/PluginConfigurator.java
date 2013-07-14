package org.jtalks.jcommune.service.plugins;

import org.jtalks.jcommune.model.dao.PluginDao;
import org.jtalks.jcommune.model.plugins.Plugin;

/**
 * @author Evgeny Naumenko
 */
public class PluginConfigurator {

    private PluginDao dao;

    /**
     *
     * @param dao
     */
    public PluginConfigurator(PluginDao dao) {
        this.dao = dao;
    }

    public void configure (Plugin plugin){
        //dao to be updated
    }
}
