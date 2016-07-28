/* Created by yegor on 7/26/16. */

package io.epigraph.data.builders;

import io.epigraph.data.StringDatum;
import io.epigraph.data.base.ModifiableStringDatumBase;
import io.epigraph.types.StringType;
import org.jetbrains.annotations.NotNull;

public abstract class StringDatumBuilder extends ModifiableStringDatumBase<StringDatumBuilder>
    implements PrimitiveDatumBuilder, StringDatum {

  public StringDatumBuilder(@NotNull StringType type) { super(type); }

}
