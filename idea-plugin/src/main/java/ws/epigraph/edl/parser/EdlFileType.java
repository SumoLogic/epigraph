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

package ws.epigraph.edl.parser;

import com.intellij.openapi.fileTypes.LanguageFileType;
import ws.epigraph.ideaplugin.edl.presentation.EdlPresentationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class EdlFileType extends LanguageFileType {
  public static final EdlFileType INSTANCE = new EdlFileType();
  public static final String DEFAULT_EXTENSION = Common.FILE_EXTENSION;

  protected EdlFileType() {
    super(EdlLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "epigraph_edl";
  }

  @NotNull
  @Override
  public String getDescription() {
    return Common.DESCRIPTION;
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return DEFAULT_EXTENSION;
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return EdlPresentationUtil.edlFileIcon();
  }
}
