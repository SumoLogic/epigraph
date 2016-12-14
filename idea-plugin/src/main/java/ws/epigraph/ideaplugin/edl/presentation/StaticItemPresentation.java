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

package ws.epigraph.ideaplugin.edl.presentation;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class StaticItemPresentation implements ItemPresentation {
  @Nullable
  private final String presentableText;
  @Nullable
  private final String locationString;
  @Nullable
  private final Icon icon;

  public StaticItemPresentation(@Nullable String presentableText, @Nullable String locationString, @Nullable Icon icon) {
    this.presentableText = presentableText;
    this.locationString = locationString;
    this.icon = icon;
  }

  @Nullable
  @Override
  public String getPresentableText() {
    return presentableText;
  }

  @Nullable
  @Override
  public String getLocationString() {
    return locationString;
  }

  @Nullable
  @Override
  public Icon getIcon(boolean unused) {
    return icon;
  }
}
