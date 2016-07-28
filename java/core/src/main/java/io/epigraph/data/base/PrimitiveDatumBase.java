/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.PrimitiveDatum;
import io.epigraph.types.PrimitiveType;
import org.jetbrains.annotations.NotNull;

public abstract class PrimitiveDatumBase<MyDatumType extends PrimitiveType>
    extends DatumBase<MyDatumType> implements PrimitiveDatum {

  protected PrimitiveDatumBase(@NotNull MyDatumType type) { super(type); }

}
