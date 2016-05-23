package com.sumologic.epigraph.ideaplugin.schema.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaRecordTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.TypeKind;
import com.sumologic.epigraph.ideaplugin.schema.psi.stubs.SchemaRecordTypeDefStub;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
abstract public class SchemaRecordTypeDefImplBase extends SchemaTypeDefImplBase<SchemaRecordTypeDefStub, SchemaRecordTypeDef> implements SchemaTypeDef {
  public SchemaRecordTypeDefImplBase(@NotNull ASTNode node) {
    super(node);
  }

  SchemaRecordTypeDefImplBase(@NotNull SchemaRecordTypeDefStub stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @NotNull
  @Override
  public TypeKind getKind() {
    return TypeKind.RECORD;
  }

}
