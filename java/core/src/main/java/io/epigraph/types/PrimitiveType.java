/* Created by yegor on 7/22/16. */

package io.epigraph.types;

import io.epigraph.data.Data;
import io.epigraph.data.PrimitiveDatum;
import io.epigraph.data.Val;
import io.epigraph.names.QualifiedTypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class PrimitiveType<Native> extends DatumType {

  protected PrimitiveType(
      @NotNull QualifiedTypeName name,
      @NotNull List<@NotNull ? extends PrimitiveType<Native>> immediateSupertypes
  ) { super(name, immediateSupertypes); }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.PRIMITIVE; }

  @Override
  public @NotNull QualifiedTypeName name() { return (QualifiedTypeName) super.name(); }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends PrimitiveType<Native>> immediateSupertypes() {
    return (List<? extends PrimitiveType<Native>>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends PrimitiveType<Native>> supertypes() {
    return (Collection<? extends PrimitiveType<Native>>) super.supertypes();
  }

  public abstract @NotNull PrimitiveDatum.Builder<Native> createBuilder(@NotNull Native val);


  public interface Raw extends DatumType.Raw {}


  public interface Static<Native,
      MyImmDatum extends PrimitiveDatum.Imm.Static<Native>,
      MyBuilderDatum extends PrimitiveDatum.Builder.Static<Native, MyImmDatum>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyBuilderDatum>,
      MyImmData extends Data.Imm.Static,
      MyBuilderData extends Data.Builder.Static<MyImmData>
      > extends DatumType.Static<MyImmDatum, MyBuilderDatum, MyImmVal, MyBuilderVal, MyImmData, MyBuilderData> {}

}
