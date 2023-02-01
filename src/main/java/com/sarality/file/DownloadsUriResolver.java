package com.sarality.file;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;

/**
 * Resolves URI from the Downloads folder.
 *
 * @author abhideep@ (Abhideep Singh)
 */

class DownloadsUriResolver implements UriResolver {

  private static final String AUTHORITY = "com.android.providers.downloads.documents";

  @Override
  public String getAuthority() {
    return AUTHORITY;
  }

  @Override
  public FileInfo resolve(Context context, Uri uri) {
    return UriUtils.getFileInfo(context, uri, AUTHORITY);
  }

  private FileInfo queryFileInfo(Context context, Uri uri, String selection, String[] selectionArgs) {
    Cursor cursor = null;
    String[] projection = { MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE };

    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        int filePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        int fileNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
        String path = cursor.getString(filePathIndex);
        String filename = cursor.getString(fileNameIndex);
        return new FileInfo(path, filename);
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }

  private String getFileName(Context context, Uri uri) {
    Cursor cursor = null;
    try {
      cursor = context.getContentResolver().query(uri, null, null, null, null);
      String fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
      if (fileName != null && !fileName.isEmpty()) {
        return fileName;
      }
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }

}
