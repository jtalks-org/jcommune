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

package org.jtalks.jcommune.service.transactional;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.plugins.Plugin;
import org.jtalks.jcommune.model.plugins.SimpleAuthenticationPlugin;
import org.jtalks.jcommune.model.plugins.exceptions.NoConnectionException;
import org.jtalks.jcommune.model.plugins.exceptions.UnexpectedErrorException;
import org.jtalks.jcommune.service.AuthService;
import org.jtalks.jcommune.service.plugins.PluginFilter;
import org.jtalks.jcommune.service.plugins.PluginLoader;
import org.jtalks.jcommune.service.plugins.TypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionalAuthService extends AbstractTransactionalEntityService<JCUser, UserDao>
        implements AuthService {

    private PluginLoader pluginLoader;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalUserService.class);

    /**
     * Subclass may use this constructor to store entity DAO or parent
     * entity DAO if necessary
     *
     * @param dao for operations with user data storage
     */
    TransactionalAuthService(UserDao dao, PluginLoader pluginLoader) {
        super(dao);
        this.pluginLoader = pluginLoader;
    }

    @Override
    public JCUser pluginAuthenticate(String username, String passwordHash, boolean newUser) {
        Map<String, String> authInfo = authenticateWithPlugin(username, passwordHash);
        if (authInfo.isEmpty() || !authInfo.containsKey("email") || !authInfo.containsKey("username")) {
            return null;
        }
        return saveUser(authInfo, passwordHash, newUser);
    }

    private JCUser saveUser(Map<String, String> authInfo, String passwordHash, boolean newUser) {
        JCUser user;
        if (newUser) {
            user = new JCUser(authInfo.get("username"), authInfo.get("email"), passwordHash);
        } else {
            user = getDao().getByUsername(authInfo.get("username"));
            user.setPassword(passwordHash);
            user.setEmail(authInfo.get("email"));
        }
        if (authInfo.containsKey("firstName")) {
            user.setFirstName(authInfo.get("firstName"));
        }
        if (authInfo.containsKey("lastName")) {
            user.setLastName(authInfo.get("lastName"));
        }
        getDao().saveOrUpdate(user);
        return user;
    }

    private Map<String, String> authenticateWithPlugin(String username, String passwordHash) {
        SimpleAuthenticationPlugin authPlugin = getAuthPlugin();
        Map<String, String> authInfo = new HashMap<>();
        if (authPlugin != null && authPlugin.getState() == Plugin.State.ENABLED) {
            try {
                authInfo.putAll(authPlugin.authenticate(username, passwordHash));
            } catch (UnexpectedErrorException e) {
                LOGGER.warn("Unexpected error was happened during the authentication through plugin.");
            } catch (NoConnectionException e) {
                LOGGER.warn("Can't connect to rest service.");
            }
        }
        return authInfo;
    }


    private SimpleAuthenticationPlugin getAuthPlugin() {
        Class cl = SimpleAuthenticationPlugin.class;
        PluginFilter pluginFilter = new TypeFilter(cl);
        List<Plugin> plugins = pluginLoader.getPlugins(pluginFilter);
        return !plugins.isEmpty() ? (SimpleAuthenticationPlugin) plugins.get(0) : null;
    }
}
