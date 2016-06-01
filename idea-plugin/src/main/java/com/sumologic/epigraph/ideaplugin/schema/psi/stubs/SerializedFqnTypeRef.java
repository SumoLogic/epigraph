package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.brains.Fqn;
import com.sumologic.epigraph.ideaplugin.schema.brains.NamespaceManager;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReference;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReferenceResolver;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaFqnTypeRef;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDef;
import com.sumologic.epigraph.ideaplugin.schema.psi.impl.SchemaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public final class SerializedFqnTypeRef {
  @Nullable
  private Fqn shortName;
  @Nullable
  private Set<Fqn> namespacesToSearch;

  public SerializedFqnTypeRef(@Nullable Fqn shortName, @Nullable Set<Fqn> namespacesToSearch) {
    this.namespacesToSearch = namespacesToSearch;
    this.shortName = shortName;
  }

  @Nullable
  public static SerializedFqnTypeRef fromFqnTypeRef(@Nullable SchemaFqnTypeRef fqnTypeRef) {
    if (fqnTypeRef == null) return null;
    SchemaFqnReference ref = (SchemaFqnReference) SchemaPsiImplUtil.getReference(fqnTypeRef);
    if (ref == null) return null;
    SchemaFqnReferenceResolver resolver = ref.getResolver();
    return new SerializedFqnTypeRef(resolver.getShortName(), resolver.getNamespacesToSearch());
  }

  @Nullable
  public Fqn getShortName() {
    return shortName;
  }

  @Nullable
  public Set<Fqn> getNamespacesToSearch() {
    return namespacesToSearch;
  }

  @Nullable
  public PsiElement resolve(@NotNull Project project) {
    SchemaFqnReferenceResolver resolver = new SchemaFqnReferenceResolver(getNamespacesToSearch(), getShortName());
    return resolver.resolve(project);
  }

  @Nullable
  public SchemaTypeDef resolveTypeDef(@NotNull Project project) {
    PsiElement element = resolve(project);
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
    stream.writeName(shortName == null ? null : shortName.toString());
    if (shortName != null) {
      if (namespacesToSearch == null) stream.writeShort(0);
      else {

        // filter out default namespaces
        Set<String> namespaces = namespacesToSearch.stream()
            .map(Fqn::toString)
            .filter(n -> !NamespaceManager.isDefaultNamespace(n))
            .collect(Collectors.toSet());


        // NB we're writing all namespaces to search for every type ref, but it won't take much space
        // thanks to names de-duplication done by IDEA. However this can be optimized further.
        // TODO(low) remove star imports from this list, write them at the file level
        // Estimated overhead is a few KBs per file at most.

        StubSerializerUtil.serializeCollection(namespaces, (item, s) -> s.writeName(item), stream);
      }
    }
  }

  @NotNull
  public static SerializedFqnTypeRef deserialize(@NotNull StubInputStream stream) throws IOException {
    String shortNameStr = StringRef.toString(stream.readName());

    Fqn shortName = null;
    Set<Fqn> namespacesToSearch = null;

    if (shortNameStr != null) {
      shortName = Fqn.fromDotSeparated(shortNameStr);

      namespacesToSearch = StubSerializerUtil.deserializeSet(s -> {
        StringRef namespaceRef = s.readName();
        String namespace = StringRef.toString(namespaceRef);
        return namespace == null ? null : Fqn.fromDotSeparated(namespace);
      }, stream, true);

      Collections.addAll(namespacesToSearch, NamespaceManager.DEFAULT_NAMESPACES);
    }

    return new SerializedFqnTypeRef(shortName, namespacesToSearch);
  }
}
