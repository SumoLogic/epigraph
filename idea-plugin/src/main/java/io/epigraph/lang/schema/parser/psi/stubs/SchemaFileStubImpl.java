package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import io.epigraph.lang.schema.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileStubImpl extends PsiFileStubImpl<SchemaFile> implements SchemaFileStub {
  private final StringRef namespace;

  public SchemaFileStubImpl(SchemaFile file, StringRef namespace) {
    super(file);
    this.namespace = namespace;
  }

  @Override
  public String getNamespace() {
    return StringRef.toString(namespace);
  }

  @Override
  public IStubFileElementType getType() {
    return (IStubFileElementType) SchemaStubElementTypes.SCHEMA_FILE;
  }
}
