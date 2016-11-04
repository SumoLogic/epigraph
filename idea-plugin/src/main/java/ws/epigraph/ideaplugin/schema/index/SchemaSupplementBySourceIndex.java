package ws.epigraph.ideaplugin.schema.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import ws.epigraph.schema.parser.psi.SchemaSupplementDef;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class SchemaSupplementBySourceIndex extends StringStubIndexExtension<SchemaSupplementDef> {
  @NotNull
  @Override
  public StubIndexKey<String, SchemaSupplementDef> getKey() {
    return SchemaStubIndexKeys.SUPPLEMENTS_BY_SOURCE;
  }
}
