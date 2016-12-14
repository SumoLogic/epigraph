/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.ideaplugin.edl.features.inspections;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class InspectionBundle {
  private static Reference<ResourceBundle> bundleRef;

  @NonNls
  private static final String BUNDLE = "ws.epigraph.ideaplugin.edl.features.inspections.InspectionBundle";

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
