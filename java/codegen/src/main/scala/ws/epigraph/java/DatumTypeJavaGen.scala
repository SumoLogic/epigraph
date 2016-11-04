/* Created by yegor on 9/19/16. */

package ws.epigraph.java

import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.schema.compiler.CType

trait DatumTypeJavaGen {this: JavaTypeGen[_ >: Null <: CType] =>

  def builderValueAndDataBuilder: String = /*@formatter:off*/sn"""\
$builderValue\

$dataBuilder\
"""/*@formatter:on*/

  def builderValue: String = /*@formatter:off*/sn"""\
    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    public static final class Value extends ws.epigraph.data.Val.Builder.Static<$ln.Imm.Value, $ln.Builder> implements $ln.Value {

      Value(@NotNull ws.epigraph.data.Val.Builder.Raw raw) { super(raw, $ln.Imm.Value.Impl::new); }

    }
"""/*@formatter:on*/

  def dataBuilder: String = /*@formatter:off*/sn"""\
    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    public static final class Data extends ws.epigraph.data.Data.Builder.Static<$ln.Imm.Data> implements $ln.Data {

      Data(@NotNull ws.epigraph.data.Data.Builder.Raw raw) { super($ln.Type.instance(), raw, $ln.Imm.Data.Impl::new); }

      /** Returns default tag datum. */
      @Override
      public @Nullable $ln get() { return ws.epigraph.util.Util.apply(get_(), $ln.Value::getDatum); }

      /** Returns default tag value. */
      @Override
      public @Nullable $ln.Value get_() { return ($ln.Value) _raw().getValue($ln.Type.instance().self); }

      /** Sets default tag datum. */
      public @NotNull $ln.Data set(@Nullable $ln datum) { _raw().setDatum($ln.Type.instance().self, datum); return this; }

      /** Sets default tag error. */
      public @NotNull $ln.Data set_Error(@NotNull ws.epigraph.errors.ErrorValue error) { _raw().setError($ln.Type.instance().self, error); return this; }

      /** Sets default tag value. */
      public @NotNull $ln.Data set_(@Nullable $ln.Value value) { _raw().setValue($ln.Type.instance().self, value); return this; }

    }
"""/*@formatter:on*/

}
