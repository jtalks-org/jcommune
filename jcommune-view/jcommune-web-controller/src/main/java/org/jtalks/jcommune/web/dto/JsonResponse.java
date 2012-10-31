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

/**
 * Ajax response class for sending JSON response
 * 
 * @author wedens
 */
public class JsonResponse {
	private String status;
	private Object result;

	/**
	 * Creates new instance
	 * @param status response status
	 * @param result data
	 */
	public JsonResponse(String status, Object result) {
		this.status = status;
		this.result = result;
	}
	
	/**
	 * Creates new instance
	 * @param status response status 
	 */
	public JsonResponse(String status) {
		this(status, null);
	}

	/**
	 * @return response status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status response status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return response data
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @param result response data
	 */
	public void setResult(Object result) {
		this.result = result;
	}
}
