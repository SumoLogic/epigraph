package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.NamedStub;
import io.epigraph.lang.parser.psi.EpigraphTypeDef;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface EpigraphTypeDefStubBase<T extends EpigraphTypeDef> extends NamedStub<T> {
  @Nullable
  String getNamespace();

  @Nullable
  List<SerializedFqnTypeRef> getExtendsTypeRefs();
}
