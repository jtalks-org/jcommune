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
package org.jtalks.jcommune.web.filters.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class NoBodyResponseWrapper extends HttpServletResponseWrapper {

    private final NoBodyOutputStream noBodyOutputStream = new NoBodyOutputStream();
    private PrintWriter writer;

    public NoBodyResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return noBodyOutputStream;
    }

    public PrintWriter getWriter() throws UnsupportedEncodingException {

        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(noBodyOutputStream, getCharacterEncoding()));
        }

        return writer;
    }

    public void setContentLength() {
        super.setContentLength(noBodyOutputStream.getContentLength());
    }

    private class NoBodyOutputStream extends ServletOutputStream {

        private int contentLength = 0;

        public int getContentLength() {
            return contentLength;
        }

        public void write(int b) throws IOException {
            contentLength++;
        }

        public void write(byte buf[], int offset, int len) throws IOException {
            contentLength += len;
        }
    }
}