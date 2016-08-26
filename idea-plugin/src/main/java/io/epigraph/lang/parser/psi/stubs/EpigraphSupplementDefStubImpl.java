package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.lexer.EpigraphElementTypes;
import io.epigraph.lang.parser.psi.EpigraphSupplementDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphSupplementDefStubImpl extends StubBase<EpigraphSupplementDef> implements EpigraphSupplementDefStub {
  private final SerializedFqnTypeRef sourceTypeRef;
  private final List<SerializedFqnTypeRef> supplementedTypeRefs;

  protected EpigraphSupplementDefStubImpl(StubElement parent, SerializedFqnTypeRef sourceTypeRef, List<SerializedFqnTypeRef> supplementedTypeRefs) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_SUPPLEMENT_DEF);
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
