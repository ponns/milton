/*
 * Copyright (C) 2012 McEvoy Software Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.bradmcevoy.http.entity;

import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.io.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class PartialEntity implements Response.Entity {

    private static final Logger log = LoggerFactory.getLogger(PartialEntity.class);

    private List<Range> ranges;
    private File temp;

    public PartialEntity(List<Range> ranges, File temp) {
        this.ranges = ranges;
        this.temp = temp;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public File getTemp() {
        return temp;
    }

    @Override
    public void write(Response response, OutputStream outputStream) throws Exception {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(temp);
            writeRanges(fin, ranges, outputStream);
        } finally {
            StreamUtils.close(fin);
        }
    }

    public static void writeRanges(InputStream in, List<Range> ranges, OutputStream responseOut) throws IOException {
        try {
            InputStream bufIn = in; //new BufferedInputStream(in);
            long pos = 0;
            for (Range r : ranges) {
                long skip = r.getStart() - pos;
                bufIn.skip(skip);
                long length = r.getFinish() - r.getStart();
                sendBytes(bufIn, responseOut, length);
                pos = r.getFinish();
            }
        } finally {
            StreamUtils.close(in);
        }
    }

    public static void sendBytes(InputStream in, OutputStream out, long length) throws IOException {
        log.trace("sendBytes: " + length);
        long numRead = 0;
        byte[] b = new byte[1024];
        while (numRead < length) {
            long remainingBytes = length - numRead;
            int maxLength = remainingBytes > 1024 ? 1024 : (int) remainingBytes;
            int s = in.read(b, 0, maxLength);
            if (s < 0) {
                break;
            }
            numRead += s;
            out.write(b, 0, s);
        }

    }

    public static void writeRange(InputStream in, Range r, OutputStream responseOut) throws IOException {
        long skip = r.getStart();
        in.skip(skip);
        long length = r.getFinish() - r.getStart();
        sendBytes(in, responseOut, length);
    }

}
