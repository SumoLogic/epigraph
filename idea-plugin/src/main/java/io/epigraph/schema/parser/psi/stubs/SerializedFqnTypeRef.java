package io.epigraph.schema.parser.psi.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaQnReference;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaQnReferenceResolver;
import io.epigraph.lang.Qn;
import io.epigraph.schema.parser.psi.SchemaQnTypeRef;
import io.epigraph.schema.parser.psi.SchemaTypeDef;
import io.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public final class SerializedFqnTypeRef {
  @Nullable
  private Qn shortName;
  @Nullable
  private List<Qn> namespacesToSearch;
  // OR
  @Nullable
  private SchemaQnTypeRef typeRef;

  public SerializedFqnTypeRef(@Nullable Qn shortName, @Nullable List<Qn> namespacesToSearch) {
    this.namespacesToSearch = namespacesToSearch;
    this.shortName = shortName;
  }

  public SerializedFqnTypeRef(@Nullable SchemaQnTypeRef typeRef) {
    this.typeRef = typeRef; // do the rest lazily
  }

  private void initFromTypeRef() {
    if ((shortName == null || namespacesToSearch == null) && typeRef != null) {
      SchemaQnReference ref = (SchemaQnReference) SchemaPsiImplUtil.getReference(typeRef);
      if (ref != null) {
        SchemaQnReferenceResolver resolver = ref.getResolver();
        shortName = resolver.getSuffix();
        namespacesToSearch = resolver.getPrefixes();
      }

      typeRef = null;
    }
  }

  @Nullable
  public Qn getShortName() {
    initFromTypeRef();
    return shortName;
  }

  @Nullable
  public List<Qn> getNamespacesToSearch() {
    initFromTypeRef();
    return namespacesToSearch;
  }

  @Nullable
  private PsiElement resolve(@NotNull Project project, @NotNull GlobalSearchScope searchScope) {
    initFromTypeRef();
    List<Qn> namespacesToSearch = getNamespacesToSearch();
    Qn shortName = getShortName();
    if (namespacesToSearch == null || shortName == null) return null;
    SchemaQnReferenceResolver resolver = new SchemaQnReferenceResolver(namespacesToSearch, shortName, searchScope);
    return resolver.resolve(project);
  }

  @Nullable
  public SchemaTypeDef resolveTypeDef(@NotNull Project project, @NotNull GlobalSearchScope searchScope) {
    PsiElement element = resolve(project, searchScope);
    if (element instanceof SchemaTypeDef) return (SchemaTypeDef) element;
    return null;
  }

  public static void serializeNullable(@Nullable SerializedFqnTypeRef ref, @NotNull StubOutputStream stream) throws IOException {
    stream.writeBoolean(ref != null);
    if (ref != null) ref.serialize(stream);
  }

  @Nullable
  public static SerializedFqnTypeRef deserializeNullable(@NotNull StubInputStream stream) throws IOException {
    boolean b = stream.readBoolean();
    if (!b) return null;
    return deserialize(stream);
  }

  public void serialize(@NotNull StubOutputStream stream) throws IOException {
    initFromTypeRef();
    stream.writeName(shortName == null ? null : shortName.toString());
    if (shortName != null) {
      if (namespacesToSearch == null) stream.writeShort(0);
      else {
        // NB we're writing all namespaces to search for every type ref, but it won't take much space
        // thanks to names de-duplication done by IDEA. However this can be optimized further.
        // TODO(low) remove star imports from this list, write them at the file level
        // Estimated overhead is a few KBs per file at most.

        StubSerializerUtil.serializeCollection(namespacesToSearch, (item, s) -> s.writeName(item.toString()), stream);
      }
    }
  }

  @NotNull
  public static SerializedFqnTypeRef deserialize(@NotNull StubInputStream stream) throws IOException {
    String shortNameStr = StringRef.toString(stream.readName());

    Qn shortName = null;
    List<Qn> namespacesToSearch = null;

    if (shortNameStr != null) {
      shortName = Qn.fromDotSeparated(shortNameStr);

      namespacesToSearch = StubSerializerUtil.deserializeList(s -> {
        StringRef namespaceRef = s.readName();
        String namespace = StringRef.toString(namespaceRef);
        return namespace == null ? null : Qn.fromDotSeparated(namespace);
      }, stream, true);
    }

    return new SerializedFqnTypeRef(shortName, namespacesToSearch);
  }
}
