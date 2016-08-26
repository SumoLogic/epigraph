package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import io.epigraph.lang.parser.psi.SchemaFile;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class EpigraphFileStubImpl extends PsiFileStubImpl<SchemaFile> implements EpigraphFileStub {
  private final StringRef namespace;

  public EpigraphFileStubImpl(SchemaFile file, StringRef namespace) {
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
