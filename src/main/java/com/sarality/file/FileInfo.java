package com.sarality.file;

/**
 * Information about a File like file name and path
 *
 * @author abhideep@ (Abhideep Singh)
 */
public class FileInfo {
  private final String path;
  private final String filename;

  public FileInfo(String path, String filename) {
    this.path = path;
    this.filename = filename;
  }

  public String getPath() {
    return path;
  }

  public String getFilename() {
    return filename;
  }
}
