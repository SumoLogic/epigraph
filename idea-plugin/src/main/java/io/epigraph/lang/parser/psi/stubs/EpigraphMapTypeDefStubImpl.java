package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.lexer.EpigraphElementTypes;
import io.epigraph.lang.parser.psi.EpigraphMapTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphMapTypeDefStubImpl extends SchemaTypeDefStubBaseImpl<EpigraphMapTypeDef> implements EpigraphMapTypeDefStub {
  EpigraphMapTypeDefStubImpl(StubElement parent,
                             String name,
                             String namespace,
                             @Nullable final List<SerializedFqnTypeRef> extendsTypeRefs) {
    super(parent, (IStubElementType) EpigraphElementTypes.E_MAP_TYPE_DEF, name, namespace, extendsTypeRefs);
  }
}
