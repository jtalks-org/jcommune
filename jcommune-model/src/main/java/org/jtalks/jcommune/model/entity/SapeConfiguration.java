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
package org.jtalks.jcommune.model.entity;

/**
 * Entity for SAPE configuration of the forum
 *
 * @author Vyacheslav Mishcheryakov
 */
public class SapeConfiguration {
    private String accountId;
    private int timeout;
    private String hostUrl;
    private int numberOfLinks;
    private boolean showOnMainPage;
    private boolean showDummyLinks;
    private boolean enableSape;

    /** @return the accoundId */
    public String getAccountId() {
        return accountId;
    }

    /** @param accountId the accoundId to set */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /** @return the timeout */
    public int getTimeout() {
        return timeout;
    }

    /** @param timeout the timeout to set */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /** @return the hostUrl */
    public String getHostUrl() {
        return hostUrl;
    }

    /** @param hostUrl the hostUrl to set */
    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    /** @return the numberOfLinks */
    public int getNumberOfLinks() {
        return numberOfLinks;
    }

    /** @param numberOfLinks the numberOfLinks to set */
    public void setNumberOfLinks(int numberOfLinks) {
        this.numberOfLinks = numberOfLinks;
    }

    /** @return the showOnMainPage */
    public boolean isShowOnMainPage() {
        return showOnMainPage;
    }

    /** @param showOnMainPage the showOnMainPage to set */
    public void setShowOnMainPage(boolean showOnMainPage) {
        this.showOnMainPage = showOnMainPage;
    }

    /** @return the showDummyLinks */
    public boolean isShowDummyLinks() {
        return showDummyLinks;
    }

    /** @param showDummyLinks the showDummyLinks to set */
    public void setShowDummyLinks(boolean showDummyLinks) {
        this.showDummyLinks = showDummyLinks;
    }

    /** @return the enableSape flag */
    public boolean isEnableSape() {
        return enableSape;
    }

    /** @param enableSape the enableSape to set */
    public void setEnableSape(boolean enableSape) {
        this.enableSape = enableSape;
    }
}
