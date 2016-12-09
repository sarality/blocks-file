package com.sarality.file;

import android.content.Context;
import android.net.Uri;

/**
 * Interface for class that resolves a URI for a given File Authority
 *
 * @author abhideep@ (Abhideep Singh)
 */
public interface UriResolver {

  String getAuthority();

  FileInfo resolve(Context context, Uri uri);
}
