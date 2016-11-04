package ws.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import io.epigraph.schema.parser.psi.SchemaNamespaceDecl;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev.com">Konstantin Sobolev</a>
 */
public class SchemaNamespaceByNameIndex extends StringStubIndexExtension<SchemaNamespaceDecl> {
  @NotNull
  @Override
  public StubIndexKey<String, SchemaNamespaceDecl> getKey() {
    return SchemaStubIndexKeys.NAMESPACE_BY_NAME;
  }
}
