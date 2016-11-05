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

package ws.epigraph.ideaplugin.schema.options;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@State(
    name = "EpiSchema",
    storages = @Storage("editor.codeinsight.epigraph.schema")
)
public class SchemaSettings implements PersistentStateComponent<SchemaSettings> {
  public boolean SHOW_EPIGRAPH_SCHEMA_ADD_IMPORT_HINTS = true;

  public static SchemaSettings getInstance() {
    return ServiceManager.getService(SchemaSettings.class);
  }

  @Nullable
  @Override
  public SchemaSettings getState() {
    return this;
  }

  @Override
  public void loadState(SchemaSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }
}
