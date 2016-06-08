package com.sumologic.epigraph.schema.parser.psi.stubs;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReference;
import com.sumologic.epigraph.ideaplugin.schema.brains.SchemaFqnReferenceResolver;
import com.sumologic.epigraph.schema.parser.Fqn;
import com.sumologic.epigraph.schema.parser.psi.SchemaFqnTypeRef;
import com.sumologic.epigraph.schema.parser.psi.SchemaTypeDef;
import com.sumologic.epigraph.schema.parser.psi.impl.SchemaPsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public final class SerializedFqnTypeRef {
  @Nullable
  private final Fqn ref;
  @Nullable
  private final Fqn[] fqns;

  public SerializedFqnTypeRef(@Nullable Fqn ref, @Nullable Fqn[] fqns) {
    this.ref = ref;
    this.fqns = fqns;
  }

  @Nullable
  public Fqn getRef() {
    return ref;
  }

  @Nullable
  public Fqn[] getFqns() {
    return fqns;
  }

  @Nullable
  public static SerializedFqnTypeRef fromFqnTypeRef(@Nullable SchemaFqnTypeRef fqnTypeRef) {
    if (fqnTypeRef == null) return null;
    SchemaFqnReference reference = (SchemaFqnReference) SchemaPsiImplUtil.getReference(fqnTypeRef);
    if (reference == null) return null;
    SchemaFqnReferenceResolver resolver = reference.getResolver();
    return new SerializedFqnTypeRef(resolver.getSourceFqn(), resolver.getFqns());
  }

  @Nullable
  public PsiElement resolve(@NotNull Project project) {
    SchemaFqnReferenceResolver resolver = new SchemaFqnReferenceResolver(ref, fqns);
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
    stream.writeName(ref == null ? null : ref.toString());
    if (ref != null) {
      if (fqns == null) stream.writeShort(0);
      else {
        StubSerializerUtil.serializeCollection(fqns, (item, s) -> s.writeName(item.toString()), stream);
      }
    }
  }

  @NotNull
  public static SerializedFqnTypeRef deserialize(@NotNull StubInputStream stream) throws IOException {
    String refStr = StringRef.toString(stream.readName());

    Fqn ref = null;
    List<Fqn> fqns = null;

    if (refStr != null) {
      ref = Fqn.fromDotSeparated(refStr);

      fqns = StubSerializerUtil.deserializeList(s -> {
        StringRef namespaceRef = s.readName();
        String namespace = StringRef.toString(namespaceRef);
        return namespace == null ? null : Fqn.fromDotSeparated(namespace);
      }, stream, true);

    }

    return new SerializedFqnTypeRef(ref, fqns == null ? null : fqns.toArray(new Fqn[0]));
  }
}
