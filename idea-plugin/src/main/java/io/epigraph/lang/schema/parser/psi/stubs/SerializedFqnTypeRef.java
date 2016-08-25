package io.epigraph.lang.schema.parser.psi.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReference;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReferenceResolver;
import io.epigraph.lang.schema.parser.Fqn;
import io.epigraph.lang.schema.parser.psi.SchemaFqnTypeRef;
import io.epigraph.lang.schema.parser.psi.SchemaTypeDef;
import io.epigraph.lang.schema.parser.psi.impl.SchemaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public final class SerializedFqnTypeRef {
  @Nullable
  private Fqn shortName;
  @Nullable
  private List<Fqn> namespacesToSearch;
  // OR
  @Nullable
  private SchemaFqnTypeRef typeRef;

  public SerializedFqnTypeRef(@Nullable Fqn shortName, @Nullable List<Fqn> namespacesToSearch) {
    this.namespacesToSearch = namespacesToSearch;
    this.shortName = shortName;
  }

  public SerializedFqnTypeRef(@Nullable SchemaFqnTypeRef typeRef) {
    this.typeRef = typeRef; // do the rest lazily
  }

  private void initFromTypeRef() {
    if ((shortName == null || namespacesToSearch == null) && typeRef != null) {
      SchemaFqnReference ref = (SchemaFqnReference) SchemaPsiImplUtil.getReference(typeRef);
      if (ref != null) {
        SchemaFqnReferenceResolver resolver = ref.getResolver();
        shortName = resolver.getSuffix();
        namespacesToSearch = resolver.getPrefixes();
      }

      typeRef = null;
    }
  }

  @Nullable
  public Fqn getShortName() {
    initFromTypeRef();
    return shortName;
  }

  @Nullable
  public List<Fqn> getNamespacesToSearch() {
    initFromTypeRef();
    return namespacesToSearch;
  }

  @Nullable
  private PsiElement resolve(@NotNull Project project, @NotNull GlobalSearchScope searchScope) {
    initFromTypeRef();
    List<Fqn> namespacesToSearch = getNamespacesToSearch();
    Fqn shortName = getShortName();
    if (namespacesToSearch == null || shortName == null) return null;
    SchemaFqnReferenceResolver resolver = new SchemaFqnReferenceResolver(namespacesToSearch, shortName, searchScope);
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

    Fqn shortName = null;
    List<Fqn> namespacesToSearch = null;

    if (shortNameStr != null) {
      shortName = Fqn.fromDotSeparated(shortNameStr);

      namespacesToSearch = StubSerializerUtil.deserializeList(s -> {
        StringRef namespaceRef = s.readName();
        String namespace = StringRef.toString(namespaceRef);
        return namespace == null ? null : Fqn.fromDotSeparated(namespace);
      }, stream, true);
    }

    return new SerializedFqnTypeRef(shortName, namespacesToSearch);
  }
}
