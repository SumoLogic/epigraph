package io.epigraph.schema.parser.psi.stubs;

import com.intellij.psi.stubs.NamedStub;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaTypeDefStubBase<T extends SchemaTypeDef> extends NamedStub<T> {
  @Nullable
  String getNamespace();

  @Nullable
  List<SerializedFqnTypeRef> getExtendsTypeRefs();
}
