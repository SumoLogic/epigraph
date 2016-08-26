package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.StubElement;
import io.epigraph.lang.parser.psi.SchemaSupplementDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface EpigraphSupplementDefStub extends StubElement<SchemaSupplementDef> {
  SerializedFqnTypeRef getSourceTypeRef();

  @Nullable
  List<SerializedFqnTypeRef> getSupplementedTypeRefs();
}
