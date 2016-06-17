package com.sumologic.epigraph.ideaplugin.schema.options;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
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
