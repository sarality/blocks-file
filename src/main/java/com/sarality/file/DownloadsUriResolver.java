package com.sarality.file;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Resolves URI from the Downloads folder.
 *
 * @author abhideep@ (Abhideep Singh)
 */

class DownloadsUriResolver implements UriResolver {

  private static final String AUTHORITY = "com.android.providers.downloads.documents";
  private static final String BASE_CONTENT_URI = "content://downloads/public_downloads";

  @Override
  public String getAuthority() {
    return AUTHORITY;
  }

  @Override
  public FileInfo resolve(Context context, Uri uri) {
    final String id = DocumentsContract.getDocumentId(uri);
    final Uri contentUri = ContentUris.withAppendedId(Uri.parse(BASE_CONTENT_URI), Long.valueOf(id));

    return queryFileInfo(context, contentUri, null, null);
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
}
