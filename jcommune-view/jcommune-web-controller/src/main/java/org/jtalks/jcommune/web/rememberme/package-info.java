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
/**
 * 
 * Provides additional components to work with "Remember Me" functionality. For instance we need a custom logic to
 * handle remember me exceptions, it's kept here. <br/>
 * Note, that since it's Spring Security and Web related logic, it can be described in different config files like
 * {@code web.xml, xxx-security.xml}.
 *
 */
package org.jtalks.jcommune.web.rememberme;