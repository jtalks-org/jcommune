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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.ComponentDao;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Date;

/**
 * The implementation of {@link ComponentService}.
 *
 * @author Anuar_Nurmakanov
 * @author Andrei Alikov
 */
public class TransactionalComponentService extends AbstractTransactionalEntityService<Component, ComponentDao>
        implements ComponentService {

    public static final String LOGO_TOOLTIP_PROPERTY = "jcommune.logo_tooltip";
    /** this is property for the prefix that should be added to the title of every page */
    public static final String TITLE_PREFIX_PROPERTY = "jcommune.all_pages_title_prefix";
    public static final String LOGO_PROPERTY = "jcommune.logo";
    public static final String COMPONENT_FAVICON_ICO_PARAM = "jcommune.favicon.ico";
    public static final String COMPONENT_FAVICON_PNG_PARAM = "jcommune.favicon.png";

    protected static final String COMPONENT_INFO_CHANGE_DATE_PROPERTY = "jcommune.info_change_date";

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionalComponentService.class);

    private final ImageService icoFormatImageService;

    /**
     * Constructs an instance with required fields.
     *
     * @param icoFormatImageService service for converting icon to ICO format
     * @param dao                   to get component
     */
    public TransactionalComponentService(ImageService icoFormatImageService, ComponentDao dao) {
        super(dao);
        this.icoFormatImageService = icoFormatImageService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponentOfForum() {
        return getDao().getComponent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentInformation.id, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void setComponentInformation(ComponentInformation componentInformation) {
        if (componentInformation.getId() != getComponentOfForum().getId()) {
            throw new IllegalArgumentException(
                    "Service should work with the same component as the componentInformation argument.");
        }
        Component forumComponent = getDao().getComponent();
        forumComponent.setName(componentInformation.getName());
        forumComponent.setDescription(componentInformation.getDescription());
        forumComponent.setProperty(LOGO_TOOLTIP_PROPERTY, componentInformation.getLogoTooltip());
        forumComponent.setProperty(TITLE_PREFIX_PROPERTY, componentInformation.getTitlePrefix());

        if (!StringUtils.isEmpty(componentInformation.getLogo())) {
            forumComponent.setProperty(LOGO_PROPERTY, componentInformation.getLogo());
        }

        if (!StringUtils.isEmpty(componentInformation.getIcon())) {
            forumComponent.setProperty(COMPONENT_FAVICON_PNG_PARAM, componentInformation.getIcon());

            Base64Wrapper wrapper = new Base64Wrapper();
            byte[] favIcon = wrapper.decodeB64Bytes(componentInformation.getIcon());
            try {
                String iconInTheIcoFormat = icoFormatImageService.preProcessAndEncodeInString64(favIcon);
                forumComponent.setProperty(COMPONENT_FAVICON_ICO_PARAM, iconInTheIcoFormat);
            } catch (ImageProcessException e) {
                LOGGER.error("Can't convert fav icon to *.ico format", e);
            }
        }

        DateTime now = new DateTime();
        now = now.withMillisOfSecond(0);
        forumComponent.setProperty(COMPONENT_INFO_CHANGE_DATE_PROPERTY, String.valueOf(now.getMillis()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getComponentModificationTime() {
        Date modificationDate = null;

        if (getDao().getComponent() != null) {
            String dateString = getDao().getComponent().getProperty(COMPONENT_INFO_CHANGE_DATE_PROPERTY);

            if (dateString != null) {
                modificationDate = new Date(Long.parseLong(dateString));
            }
        }

        return modificationDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("hasPermission(#componentId, 'COMPONENT', 'GeneralPermission.ADMIN')")
    public void checkPermissionsForComponent(long componentId) {

    }
}
