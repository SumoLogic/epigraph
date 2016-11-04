package ws.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import ws.epigraph.schema.parser.psi.SchemaTypeDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaTypesByNamespaceIndex extends StringStubIndexExtension<SchemaTypeDef> {
  @NotNull
  @Override
  public StubIndexKey<String, SchemaTypeDef> getKey() {
    return SchemaStubIndexKeys.TYPES_BY_NAMESPACE;
  }
}
