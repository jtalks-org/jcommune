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

package org.jtalks.jcommune.web.dto;

import org.jtalks.jcommune.model.entity.PollOption;

/**
 * Data transfer object for transferring poll option to the client side.
 *
 * @author Anuar Nurmakanov
 */
public class PollOptionDto {
    private long id;
    private int pollCount;

    /**
     * Default constructor.
     * It is also required for JSON.
     */
    public PollOptionDto() {
    }

    /**
     * Creates data transfer object, that represents info about the poll option.
     *
     * @param option the poll option
     */
    public PollOptionDto(PollOption option) {
        this.id = option.getId();
        this.pollCount = option.getPollCount();
    }

    /**
     * Get the poll option id.
     *
     * @return the poll option id
     */
    public long getId() {
        return id;
    }

    /**
     * Set the poll option id.
     * It is also required for JSON.
     *
     * @param id the poll option id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the poll count.
     *
     * @return the poll count
     */
    public int getPollCount() {
        return pollCount;
    }

    /**
     * Set the poll count.
     * It is also required for JSON.
     *
     * @param pollCount the poll count
     */
    public void setPollCount(int pollCount) {
        this.pollCount = pollCount;
    }
}
