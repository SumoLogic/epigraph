package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.schema.lexer.SchemaElementTypes;
import io.epigraph.schema.parser.psi.SchemaSupplementDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementDefStubImpl extends StubBase<SchemaSupplementDef> implements SchemaSupplementDefStub {
  private final SerializedFqnTypeRef sourceTypeRef;
  private final List<SerializedFqnTypeRef> supplementedTypeRefs;

  protected SchemaSupplementDefStubImpl(StubElement parent, SerializedFqnTypeRef sourceTypeRef, List<SerializedFqnTypeRef> supplementedTypeRefs) {
    super(parent, (IStubElementType) SchemaElementTypes.S_SUPPLEMENT_DEF);
    this.sourceTypeRef = sourceTypeRef;
    this.supplementedTypeRefs = supplementedTypeRefs;
  }

  @Override
  public SerializedFqnTypeRef getSourceTypeRef() {
    return sourceTypeRef;
  }

  @Nullable
  @Override
  public List<SerializedFqnTypeRef> getSupplementedTypeRefs() {
    return supplementedTypeRefs;
  }
}
