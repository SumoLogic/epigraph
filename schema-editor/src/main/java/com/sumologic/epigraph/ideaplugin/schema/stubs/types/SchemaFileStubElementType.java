package com.sumologic.epigraph.ideaplugin.schema.stubs.types;

import com.intellij.psi.tree.IStubFileElementType;
import com.sumologic.epigraph.ideaplugin.schema.SchemaLanguage;
import com.sumologic.epigraph.ideaplugin.schema.stubs.SchemaFileStub;

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
