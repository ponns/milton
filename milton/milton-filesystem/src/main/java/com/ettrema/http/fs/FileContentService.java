package com.ettrema.http.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction for storing and retrieving 
 *
 * @author brad
 */
public interface FileContentService {
	void setFileContent(File file, InputStream in) throws FileNotFoundException, IOException;
	InputStream getFileContent(File file) throws FileNotFoundException;
}
