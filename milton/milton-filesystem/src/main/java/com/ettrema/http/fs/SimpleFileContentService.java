package com.ettrema.http.fs;

import java.io.*;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author brad
 */
public class SimpleFileContentService implements FileContentService {

    @Override
    public void setFileContent(File file, InputStream in) throws FileNotFoundException, IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public InputStream getFileContent(File file) throws FileNotFoundException {
        FileInputStream fin = new FileInputStream(file);
        return fin;
    }
}
