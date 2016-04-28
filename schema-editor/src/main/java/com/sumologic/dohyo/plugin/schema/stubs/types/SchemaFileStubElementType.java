package com.sumologic.dohyo.plugin.schema.stubs.types;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IStubFileElementType;
import com.sumologic.dohyo.plugin.schema.SchemaLanguage;
import com.sumologic.dohyo.plugin.schema.stubs.SchemaFileStub;

/**
 * Todo add doc
 *
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileStubElementType extends IStubFileElementType<SchemaFileStub> {
  public static final SchemaFileStubElementType INSTANCE = new SchemaFileStubElementType();

  public SchemaFileStubElementType() {
    super("FILE", SchemaLanguage.INSTANCE);
  }

  // todo: builder, serialize?
}
