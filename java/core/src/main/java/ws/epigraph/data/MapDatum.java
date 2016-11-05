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

/* Created by yegor on 9/20/16. */

package ws.epigraph.data;

import ws.epigraph.types.MapType;
import ws.epigraph.util.Unmodifiable;
import ws.epigraph.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;


public interface MapDatum extends Datum {

  @Override
  @NotNull MapType type();

  @Override
  @NotNull MapDatum.Raw _raw();

  @Override
  @NotNull MapDatum.Imm toImmutable();

  int size(); // TODO isEmpty()?


  abstract class Impl extends Datum.Impl<MapType> implements MapDatum {

    protected Impl(@NotNull MapType type) { super(type); }

  }


  interface Raw extends MapDatum, Datum.Raw {

    @Override
    @NotNull MapDatum.Imm.Raw toImmutable();

    @NotNull Map<Datum.@NotNull Imm, @NotNull ? extends Data> elements();

  }


  interface Static<K extends Datum.Imm> extends MapDatum, Datum.Static {

    @Override
    @NotNull MapDatum.Imm.Static toImmutable();

  }


  interface Imm extends MapDatum, Datum.Imm {

    @Override
    @NotNull MapDatum.Imm.Raw _raw();


    final class Raw extends MapDatum.Impl implements MapDatum.Imm, MapDatum.Raw, Datum.Imm.Raw {

      private final Map<Datum.@NotNull Imm, ? extends Data.Imm> elements;

      private final @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      private final int hashCode;

      public Raw(@NotNull MapDatum.Builder.Raw mutable) {
        super(mutable.type());
        elements = Unmodifiable.map(
            mutable.elements(),
            k -> k,
            Data::toImmutable,
            () -> Util.createLinkedHashMap(mutable.size())
        );
        hashCode = Objects.hash(type(), elements);
      }

      @Override
      public int size() { return elements.size(); }

      @Override
      public @NotNull Map<Datum.@NotNull Imm, @NotNull ? extends Data.Imm> elements() { return elements; }

      @Override
      public @NotNull MapDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull MapDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapDatum)) return false;
        if (o instanceof Immutable && hashCode != o.hashCode()) return false;
        MapDatum that = (MapDatum) o;
        return type().equals(that.type()) && elements.equals(that._raw().elements());
      }

      @Override
      public final int hashCode() { return hashCode; }

    }


    interface Static<K extends Datum.Imm> extends MapDatum.Imm, MapDatum.Static<K>, Datum.Imm.Static {

      @Override
      @NotNull MapDatum.Imm.Static<K> toImmutable();

      @Override
      @NotNull MapDatum.Imm.Raw _raw();


      // TODO additional sub-classes for Union and Datum value type based maps?
      abstract class Impl<K extends Datum.Imm, MyImmDatum extends MapDatum.Imm.Static, MyImmVal extends Val.Imm.Static>
          extends MapDatum.Impl implements MapDatum.Imm.Static<K> {

        private final @NotNull MapDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull MapType type, // TODO take type from raw?
            @NotNull MapDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type);
          // TODO check types are compatible
          this.raw = raw; // TODO validate raw internals is kosher (i.e. contains static datums)?
          this.value = immValConstructor.apply(new Val.Imm.Raw.DatumVal(this));
        }

        @Override
        public int size() { return raw.size(); }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }  // TODO or make abstract and implement in final static impl?

        @Override
        public final @NotNull MapDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

        @Override
        public final int hashCode() { return raw.hashCode(); }

        @Override
        public final boolean equals(Object obj) { return raw.equals(obj); }

      }


    }


  }


  abstract class Builder extends MapDatum.Impl implements Datum.Builder {

    protected Builder(@NotNull MapType type) { super(type); }

    @Override
    public abstract @NotNull MapDatum.Builder.Raw _raw();


    public static final class Raw extends MapDatum.Builder implements MapDatum.Raw, Datum.Builder.Raw {

      private final @NotNull Map<Datum.@NotNull Imm, @NotNull Data> elements = new DataMap<>(type());

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

      public Raw(MapType type) { super(type); }

      @Override
      public @NotNull Map<Datum.@NotNull Imm, @NotNull Data> elements() { return elements; }

      @Override
      public int size() { return elements.size(); }

      // TODO add mut methods here

      @Override
      public @NotNull MapDatum.Imm.Raw toImmutable() { return new MapDatum.Imm.Raw(this); }

      @Override
      public @NotNull MapDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }

      @Override
      public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapDatum)) return false;
        MapDatum that = (MapDatum) o;
        return type().equals(that.type()) && elements.equals(that._raw().elements());
      }

      @Override
      public final int hashCode() { return Objects.hash(type(), elements); }


      private static class DataMap<K extends Datum.Imm, V extends Data> extends AbstractMap<K, V> {

        private final @NotNull Map<@NotNull K, @NotNull V> map = new LinkedHashMap<>();

        private final @NotNull MapType mapType;

        public DataMap(@NotNull MapType mapType) { this.mapType = mapType; }

        @Override // TODO safeguard entry.setValue() as well
        public @NotNull Set<Map.@NotNull Entry<@NotNull K, @NotNull V>> entrySet() { return map.entrySet(); }

        @Override
        public V put(K key, V value) {
          // FIXME we might have to restrict keys to exactly declared type only to ensure clients can properly parse these
          // FIXME better, wrap keys in an object that computes equals and hashCode based on declared type properties only
          return map.put(mapType.keyType.checkAssignable(key), mapType.valueType.checkAssignable(value));
        }

        @Override
        public int size() { return map.size(); }

      }


    }


    public static abstract class Static<
        K extends Datum.Imm,
        MyImmDatum extends MapDatum.Imm.Static,
        MyBuilderVal extends Val.Builder.Static
        > extends MapDatum.Builder implements MapDatum.Static<K>, Datum.Builder.Static<MyImmDatum> {

      private final @NotNull MapDatum.Builder.Raw raw;

      private final @NotNull MyBuilderVal value;

      private final @NotNull Function<MapDatum.Imm.Raw, MyImmDatum> immutableConstructor;

      protected Static(
          @NotNull MapType type,
          @NotNull MapDatum.Builder.Raw raw,
          @NotNull Function<MapDatum.Imm.Raw, MyImmDatum> immutableConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyBuilderVal> builderValConstructor
      ) {
        super(type); // TODO take static type separately?
        if (raw.type() != type) // TODO shared assertEqual(Type, Type): Type method
          throw new IllegalArgumentException( // TODO move mut and imm checks to shared static methods
              "Incompatible raw and static types (TODO details)"
          );
        this.raw = raw; // TODO validate raw data is kosher?
        this.value = builderValConstructor.apply(new Val.Builder.Raw.DatumVal(this));
        this.immutableConstructor = immutableConstructor;
      }

      @Override
      public int size() { return raw.size(); }

      @Override
      public @NotNull MyImmDatum toImmutable() { return immutableConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull MapDatum.Builder.Raw _raw() { return raw; }

      @Override
      public @NotNull MyBuilderVal asValue() { return value; }

      @Override
      public final int hashCode() { return raw.hashCode(); }

      @Override
      public final boolean equals(Object obj) { return raw.equals(obj); }

    }


  }


}
