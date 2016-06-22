package com.sumologic.epigraph.ideaplugin.schema;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaBundle {
  private static Reference<ResourceBundle> bundleRef;

  @NonNls
  private static final String BUNDLE = "com.sumologic.epigraph.ideaplugin.schema.SchemaBundle";

  public static String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
    return CommonBundle.message(getBundle(), key, params);
  }

  private static ResourceBundle getBundle() {
    ResourceBundle bundle = null;

    if (bundleRef != null) bundle = bundleRef.get();

    if (bundle == null) {
      bundle = ResourceBundle.getBundle(BUNDLE);
      bundleRef = new SoftReference<>(bundle);
    }

    return bundle;
  }
}
