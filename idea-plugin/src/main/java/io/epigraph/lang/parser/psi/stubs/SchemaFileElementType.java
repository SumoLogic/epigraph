package io.epigraph.lang.parser.psi.stubs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import io.epigraph.lang.parser.Fqn;
import io.epigraph.lang.schema.SchemaLanguage;
import io.epigraph.lang.parser.psi.SchemaFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public class SchemaFileElementType extends IStubFileElementType<EpigraphFileStub> {
  public SchemaFileElementType() {
    super("epigraph_schema.FILE", SchemaLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getExternalId() {
    return "epigraph_schema.FILE";
  }

  @Override
  public int getStubVersion() {
    return 4; // advance every time indexes or serialized stubs change
  }

  @Override
  public StubBuilder getBuilder() {
    return new DefaultStubBuilder() {
      @NotNull
      @Override
      protected StubElement createStubForFile(@NotNull PsiFile file) {
        if (file instanceof SchemaFile) {
          SchemaFile schemaFile = (SchemaFile) file;
          Fqn namespace = NamespaceManager.getNamespace(schemaFile);
          return new EpigraphFileStubImpl(schemaFile, StringRef.fromNullableString(namespace == null ? null : namespace.toString()));
        } else return super.createStubForFile(file);
      }
    };
  }

  @Override
  public void serialize(@NotNull EpigraphFileStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getNamespace());
  }

  @NotNull
  @Override
  public EpigraphFileStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    StringRef namespace = dataStream.readName();
    return new EpigraphFileStubImpl(null, namespace);
  }

  // indexStub by namespace?
}
