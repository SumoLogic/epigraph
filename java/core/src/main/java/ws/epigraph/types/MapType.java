/* Created by yegor on 9/20/16. */

package ws.epigraph.types;

import ws.epigraph.data.Data;
import ws.epigraph.data.Datum;
import ws.epigraph.data.MapDatum;
import ws.epigraph.data.Val;
import ws.epigraph.names.TypeName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class MapType extends DatumType {

  public final @NotNull DatumType keyType;
  public final @NotNull DataType valueType;

  protected MapType(
      @NotNull TypeName name,
      @NotNull List<@NotNull ? extends MapType> immediateSupertypes,
      @NotNull DatumType keyType,
      @NotNull DataType valueType
  ) {
    super(name, immediateSupertypes);
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public final @NotNull TypeKind kind() { return TypeKind.MAP; }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull List<@NotNull ? extends MapType> immediateSupertypes() {
    return (List<? extends MapType>) super.immediateSupertypes();
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull Collection<@NotNull ? extends MapType> supertypes() {
    return (Collection<? extends MapType>) super.supertypes();
  }

  public @NotNull DatumType keyType() { return keyType; }

  public @NotNull DataType valueType() { return valueType; }

  public abstract @NotNull MapDatum.Builder createBuilder();


  public interface Static<
      K extends Datum.Imm.Static,
      MyImmDatum extends MapDatum.Imm.Static,
      MyBuilderDatum extends MapDatum.Builder.Static<K, MyImmDatum, MyBuilderVal>,
      MyImmVal extends Val.Imm.Static,
      MyBuilderVal extends Val.Builder.Static<MyImmVal, MyBuilderDatum>,
      MyImmData extends Data.Imm.Static,
      MyBuilderData extends Data.Builder.Static<MyImmData>
      > extends DatumType.Static<MyImmDatum, MyBuilderDatum, MyImmVal, MyBuilderVal, MyImmData, MyBuilderData> {}


}
