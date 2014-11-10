package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * @author Mikhail Stryzhonok
 */
public interface PluginBranchService {
    Branch get(Long id) throws NotFoundException;
}
