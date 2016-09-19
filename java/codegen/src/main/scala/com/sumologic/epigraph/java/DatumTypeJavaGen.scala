/* Created by yegor on 9/19/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler.CType

trait DatumTypeJavaGen {this: JavaTypeGen[_ >: Null <: CType] =>

  def builderValueAndDataBuilder: String = /*@formatter:off*/sn"""\
    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    public static final class Value extends io.epigraph.data.Val.Builder.Static<$ln.Imm.Value, $ln.Builder> implements $ln.Value {

      Value(@NotNull io.epigraph.data.Val.Builder.Raw raw) { super(raw, $ln.Imm.Value.Impl::new); }

    }

    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    public static final class Data extends io.epigraph.data.Data.Builder.Static<$ln.Imm.Data> implements $ln.Data {

      Data(@NotNull io.epigraph.data.Data.Builder.Raw raw) { super($ln.type, raw, $ln.Imm.Data.Impl::new); }

      /** Returns default tag datum. */
      @Override
      public @Nullable $ln get() { return io.epigraph.util.Util.apply(get$$(), $ln.Value::getDatum); }

      /** Returns default tag value. */
      @Override
      public @Nullable $ln.Value get$$() { return ($ln.Value) _raw().getValue($ln.type.self); }

      /** Sets default tag datum. */
      public @NotNull $ln.Data set(@Nullable $ln datum) { _raw().setDatum($ln.type.self, datum); return this; }

      /** Sets default tag error. */
      public @NotNull $ln.Data set(@NotNull io.epigraph.errors.ErrorValue error) { _raw().setError($ln.type.self, error); return this; }

      /** Sets default tag value. */
      public @NotNull $ln.Data set(@Nullable $ln.Value value) { _raw().setValue($ln.type.self, value); return this; }

    }
"""/*@formatter:on*/

}
