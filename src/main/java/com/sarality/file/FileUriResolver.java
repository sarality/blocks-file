package com.sarality.file;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Resolves a URI into a {@link FileInfo}
 *
 * @author abhideep@ (Abhideep Singh)
 */
public class FileUriResolver {

  private final static UriResolverRegistry REGISTRY = new UriResolverRegistry();

  private final Activity activity;

  public FileUriResolver(Activity activity) {
    this.activity = activity;
  }

  public FileInfo resolve(Uri uri) {
    String authority = uri.getAuthority();
    UriResolver resolver = REGISTRY.getResolver(authority);
    return resolver.resolve(activity, uri);
  }
}
