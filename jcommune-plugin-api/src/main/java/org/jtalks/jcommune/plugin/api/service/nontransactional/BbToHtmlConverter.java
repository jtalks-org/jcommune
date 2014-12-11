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
package org.jtalks.jcommune.plugin.api.service.nontransactional;

import org.jtalks.jcommune.plugin.api.service.PluginBbCodeService;

/**
 * Service for processing bb-codes in plugins.
 * For processing bb-codes in jcommune use classes from service module
 *
 * This class is  singleton because we can't use spring dependency injection mechanism in plugins due plugins can be
 * added or removed in runtime.
 *
 * @author Mikhail Stryzhonok
 */
public class BbToHtmlConverter implements PluginBbCodeService {
    private static final BbToHtmlConverter INSTANCE = new BbToHtmlConverter();

    private PluginBbCodeService bbCodeService;

    /** Use {@link #getInstance()}, this class is singleton. */
    private BbToHtmlConverter(){

    }

    /**
     * Gets instance of this class
     *
     * @return instance of {@link BbToHtmlConverter}
     */
    public static PluginBbCodeService getInstance() {
        return INSTANCE;
    }

    /**
     *  {@inheritDoc}
     */
    @Override
    public String stripBBCodes(String bbCode) {
        return bbCodeService.stripBBCodes(bbCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertBbToHtml(String post) {
        return bbCodeService.convertBbToHtml(post);
    }

    /**
     * Sets bb-code service. Should be used once, during initialization
     *
     * @param bbCodeService bb-code service to set
     */
    public void setBbCodeService(PluginBbCodeService bbCodeService) {
        this.bbCodeService = bbCodeService;
    }
}
