package com.sarality.file;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Resolves a URI into a {@link FileInfo}
 *
 * @author abhideep@ (Abhideep Singh)
 */
public class FileUriResolver {

  public static final Logger logger = LoggerFactory.getLogger(FileUriResolver.class);

  private final static UriResolverRegistry REGISTRY = new UriResolverRegistry();

  private final Activity activity;

  public FileUriResolver(Activity activity) {
    this.activity = activity;
  }

  public FileInfo resolve(Uri uri) {
    String authority = uri.getAuthority();
    logger.info("The uri {} has the authority {}", uri.getPath(), authority);

    if (TextUtils.isEmpty(authority)) {
      File file = new File(uri.getPath());
      return new FileInfo(file.getPath(), file.getName());
    }

    UriResolver resolver = REGISTRY.getResolver(authority);
    return resolver.resolve(activity, uri);
  }
}
