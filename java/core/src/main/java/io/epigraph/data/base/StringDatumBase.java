/* Created by yegor on 7/28/16. */

package io.epigraph.data.base;

import io.epigraph.data.StringDatum;
import io.epigraph.types.StringType;

public abstract class StringDatumBase extends PrimitiveDatumBase<StringType> implements StringDatum {

  public StringDatumBase(StringType type) { super(type); }

}
