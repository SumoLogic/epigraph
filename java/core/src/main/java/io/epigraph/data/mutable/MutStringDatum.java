/* Created by yegor on 7/28/16. */

package io.epigraph.data.mutable;

import io.epigraph.data.StringDatum;
import io.epigraph.data.base.ModifiableStringDatumBase;
import io.epigraph.types.StringType;

public interface MutStringDatum extends MutDatum, StringDatum {


  public static abstract class Impl extends ModifiableStringDatumBase<MutStringDatum> implements MutStringDatum {

    public Impl(StringType type) { super(type); }

  }


}
