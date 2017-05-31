/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* Created by yegor on 9/19/16. */

package ws.epigraph.java

import ws.epigraph.compiler.CType
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

trait DatumTypeJavaGen { this: JavaTypeGen[_ >: Null <: CType] =>

  /** Generates .Value, .Value.Builder, .Value.Imm, and .Value.Imm.Impl for the datum type. */
  def datumValue: String = /*@formatter:off*/sn"""\
  /**
   * Base interface for `${t.name.name}` value (holding a datum or an error).
   */
  interface Value extends${withParents(".Value")} ws.epigraph.data.Val.Static {

    @Override
    @Nullable $ln getDatum();

    @Override
    @NotNull $ln.Value.Imm toImmutable();

    /**
     * Builder for `${t.name.name}` value (holding a builder or an error).
     */
    public static final class Builder extends ws.epigraph.data.Val.Builder.Static<$ln.Value.Imm, $ln.Builder> implements $ln.Value {

      Builder(@NotNull ws.epigraph.data.Val.Builder.Raw raw) { super(raw, $ln.Value.Imm.Impl::new); }

    }

    /**
     * Immutable interface for `${t.name.name}` value (holding an immutable datum or an error).
     */
    interface Imm extends $ln.Value,${withParents(".Value.Imm")} ws.epigraph.data.Val.Imm.Static {

      /** Returns immutable default tag datum. */
      @Override
      @Nullable $ln.Imm getDatum();

      /** Private implementation of `$ln.Value.Imm` interface. */
      final class Impl extends ws.epigraph.data.Val.Imm.Static.Impl<$ln.Value.Imm, $ln.Imm>
          implements $ln.Value.Imm {

        Impl(@NotNull ws.epigraph.data.Val.Imm.Raw raw) { super(raw); }

      }

    }

  }
"""/*@formatter:on*/

  /** Generates .Data, .Data.Builder, .Data.Imm, and .Data.Imm.Impl for the datum type. */
  def datumData: String = /*@formatter:off*/sn"""\
  /**
   * Base interface for `${t.name.name}` data (holding single default representation of the type).
   */
  interface Data extends${withParents(".Data")} ws.epigraph.data.Data.Static {

    @Override
    @NotNull $ln.Data.Imm toImmutable();

    /** Returns default tag datum. */
    @Nullable $ln get();

    /** Returns default tag value. */
    @Nullable $ln.Value get_();

    /**
     * Builder for `${t.name.name}` data (holding single default representation of the type).
     */
    public static final class Builder extends ws.epigraph.data.Data.Builder.Static<$ln.Data.Imm> implements $ln.Data {

      Builder(@NotNull ws.epigraph.data.Data.Builder.Raw raw) { super($ln.Type.instance(), raw, $ln.Data.Imm.Impl::new); }

      /** Returns default tag datum. */
      @Override
      public @Nullable $ln get() { return ws.epigraph.util.Util.apply(get_(), $ln.Value::getDatum); }

      /** Returns default tag value. */
      @Override
      public @Nullable $ln.Value get_() { return ($ln.Value) _raw().getValue($ln.Type.instance().self()); }

      /** Sets default tag datum. */
      public @NotNull $ln.Data set(@Nullable $ln datum) { _raw().setDatum($ln.Type.instance().self(), datum); return this; }

      /** Sets default tag error. */
      public @NotNull $ln.Data set_Error(@NotNull ws.epigraph.errors.ErrorValue error) { _raw().setError($ln.Type.instance().self(), error); return this; }

      /** Sets default tag value. */
      public @NotNull $ln.Data set_(@Nullable $ln.Value value) { _raw().setValue($ln.Type.instance().self(), value); return this; }

    }

    /**
     * Immutable interface for `${t.name.name}` data (holding single default representation of the type).
     */
    interface Imm extends $ln.Data,${withParents(".Data.Imm")} ws.epigraph.data.Data.Imm.Static {

      /** Returns immutable default tag datum. */
      @Override
      @Nullable $ln.Imm get();

      /** Returns immutable default tag value. */
      @Override
      @Nullable $ln.Value.Imm get_();

      /** Private implementation of `$ln.Data.Imm` interface. */
      final class Impl extends ws.epigraph.data.Data.Imm.Static.Impl<$ln.Data.Imm> implements $ln.Data.Imm {

        Impl(@NotNull ws.epigraph.data.Data.Imm.Raw raw) { super($ln.Type.instance(), raw); }

        @Override
        public @Nullable $ln.Imm get() {
          $ln.Value.Imm value = get_();
          return value == null ? null : value.getDatum();
        }

        @Override
        public @Nullable $ln.Value.Imm get_() {
          return ($ln.Value.Imm) _raw().getValue($ln.Type.instance().self());
        }

      }

    }

  }
"""/*@formatter:on*/

}
