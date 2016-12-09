package com.sarality.file;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry of UriResolver based on authority.
 *
 * @author abhideep@ (Abhideep Singh)
 */
public class UriResolverRegistry {

  private final Map<String, UriResolver> resolverMap = new HashMap<>();

  public UriResolverRegistry() {
    super();
    register(new DownloadsUriResolver());
  }

  public void register(UriResolver resolver) {
    resolverMap.put(resolver.getAuthority(), resolver);
  }

  public UriResolver getResolver(String authority) {
    return resolverMap.get(authority);
  }
}
