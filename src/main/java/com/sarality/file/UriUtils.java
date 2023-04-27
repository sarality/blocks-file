package com.sarality.file;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UriUtils {

  /**
   * Get a file path from a Uri. This will get the the path for Storage Access
   * Framework Documents, as well as the _data field for the MediaStore and
   * other file-based ContentProviders.<br>
   * <br>
   * Callers should check whether the path is local before assuming it
   * represents a local file.
   *
   * @param context The context.
   * @param uri     The Uri to query.
   */
  public static FileInfo getFileInfo(final Context context, final Uri uri, final String authority) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      if (!DocumentsContract.isDocumentUri(context, uri)) {
        return getFileInfoForScheme(context, uri);
      }

      final String docId = DocumentsContract.getDocumentId(uri);
      // ExternalStorageProvider
      if (isExternalStorageDocument(uri)) {
        final String[] split = docId.split(":");
        final String type = split[0];

        if ("primary".equalsIgnoreCase(type)) {
          String filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
          return new FileInfo(filePath, uri.getUserInfo());
        }
      } else if (isDownloadsDocument(uri)) {

        if (docId != null && docId.startsWith("raw:")) {
          String filePath = docId.substring(4);
          String fileName = uri.getLastPathSegment();
          return new FileInfo(filePath, fileName);
        }

        String[] contentUriPrefixesToTry = new String[]{
            "content://downloads/public_downloads",
            "content://downloads/my_downloads",
            "content://downloads/all_downloads",
        };

        for (String contentUriPrefix : contentUriPrefixesToTry) {
          Uri contentUri;
          if (docId != null && docId.startsWith("msf")) {
            contentUri = DocumentsContract.buildDocumentUri(authority, docId);
          } else {
            contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.parseLong(docId));
          }
          try {
            FileInfo fileInfo = queryFileInfoColumns(context, contentUri, null, null);
            if (fileInfo != null && fileInfo.getPath() != null) {
              return fileInfo;
            }
          } catch (Exception e) {
            // Ignore Exception
            e.printStackTrace();
          }
        }

        return getFileInfoForCache(context, uri);
      } else if (isMediaDocument(uri)) {
        final String[] split = docId.split(":");
        final String type = split[0];

        Uri contentUri = null;
        if ("image".equals(type)) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if ("video".equals(type)) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else if ("audio".equals(type)) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        final String selection = "_id = ?";
        final String[] selectionArgs = new String[] {
            split[1]
        };

        return queryFileInfoColumns(context, contentUri, selection, selectionArgs);
      }
    }
    return null;
  }

  public static FileInfo getFileInfoForCache(Context context, Uri uri) {
    String fileName = getFileName(context, uri);
    File cacheDir = getDocumentCacheDir(context);
    File file = generateFileName(fileName, cacheDir);
    String destinationPath = null;
    if (file != null) {
      destinationPath = file.getAbsolutePath();
      saveFileFromUri(context, uri, destinationPath);
    }
    return new FileInfo(destinationPath, fileName);
  }

  public static FileInfo getFileInfoForScheme(Context context, Uri uri) {
    if ("content".equalsIgnoreCase(uri.getScheme())) {
      // Return the remote address
      if (isGooglePhotosUri(uri)) {
        String path = uri.getLastPathSegment();
        String fileName = getFileName(context, uri);
        return new FileInfo(path, fileName);
      }
      return queryFileInfoColumns(context, uri, null, null);
    } else if ("file".equalsIgnoreCase(uri.getScheme())) {
      String path = uri.getPath();
      String fileName = getFileName(context, uri);
      return new FileInfo(path, fileName);
    }
    return null;
  }

  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }

  public static FileInfo queryFileInfoColumns(Context context, Uri uri, String selection, String[] selectionArgs) {
    Cursor cursor = null;
    final String pathColumn = MediaStore.Files.FileColumns.DATA;
    final String nameColumn = MediaStore.Files.FileColumns.TITLE;
    final String[] projection = { pathColumn, nameColumn };
    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        int filePathIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        int fileNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
        String path = cursor.getString(filePathIndex);
        String filename = cursor.getString(fileNameIndex);
        return new FileInfo(path, filename);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (cursor != null)
        cursor.close();
    }
    return null;
  }

  public static String getFileName(Context context, Uri uri) {
    String mimeType = context.getContentResolver().getType(uri);
    String filename = null;
    Cursor returnCursor = context.getContentResolver().query(uri, null, null, null,
        null);
    if (returnCursor != null) {
      int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
      returnCursor.moveToFirst();
      filename = returnCursor.getString(nameIndex);
      returnCursor.close();
    }
    return filename;
  }

  public static File getDocumentCacheDir(Context context) {
    File dir = new File(context.getCacheDir(), "document");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    return dir;
  }

  public static File generateFileName(String name, File directory) {
    if (name == null) {
      return null;
    }
    File file = new File(directory, name);
    if (file.exists()) {
      String fileName = name;
      String extension = "";
      int dotIndex = name.lastIndexOf('.');
      if (dotIndex > 0) {
        fileName = name.substring(0, dotIndex);
        extension = name.substring(dotIndex);
      }
      int index = 0;
      while (file.exists()) {
        index++;
        name = fileName + '(' + index + ')' + extension;
        file = new File(directory, name);
      }
    }
    try {
      if (!file.createNewFile()) {
        return null;
      }
    } catch (IOException e) {
      return null;
    }
    return file;
  }

  private static void saveFileFromUri(Context context, Uri uri, String destinationPath) {
    InputStream input = null;
    BufferedOutputStream output = null;
    try {
      input = context.getContentResolver().openInputStream(uri);
      output = new BufferedOutputStream(new FileOutputStream(destinationPath, false));
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = input.read(buffer)) != -1) {
        output.write(buffer, 0, bytesRead);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (input != null) {
          input.close();
        }
        if (output != null) {
          output.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
