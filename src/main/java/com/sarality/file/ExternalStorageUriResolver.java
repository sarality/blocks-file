package com.sarality.file;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;

import java.io.File;

/**
 * Resolves URI from the External Storage Documents folder (shows up as Downloads on Moto G4 Plus).
 *
 * @author Satya@ (Satya Puniani)
 */

class ExternalStorageUriResolver implements UriResolver {

  private static final String AUTHORITY = "com.android.externalstorage.documents";
  private static final String BASE_CONTENT_URI = "content://com.android.externalstorage.documents/document/";

  @Override
  public String getAuthority() {
    return AUTHORITY;
  }

  @Override
  public FileInfo resolve(Context context, Uri uri) {
    final String id = DocumentsContract.getDocumentId(uri);
    final String[] uriSplit = id.split(":");
    final String type = uriSplit[0];
    final String filePath = uriSplit[1];
    String path;

    if ("primary".equalsIgnoreCase(type)) {
      path = Environment.getExternalStorageDirectory() + "/" + filePath;
      File file = new File(path);
      return new FileInfo(file.getPath(), file.getName());
    } else {
      //path = Environment.getExternalStoragePublicDirectory(type) + "/" + filePath;
      //TODO (Satya) unable to read files from secondary storage. will come back to this.
      return null;
    }

  }

}
