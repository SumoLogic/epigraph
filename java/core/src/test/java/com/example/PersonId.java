/* Created by yegor on 8/2/16. */

package com.example;

import io.epigraph.data.IntegerDatum;
import io.epigraph.data.ListDatum;
import io.epigraph.data.Val;
import io.epigraph.names.AnonListTypeName;
import io.epigraph.names.NamespaceName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.AnonListType;
import io.epigraph.types.IntegerType;
import io.epigraph.types.ListType;
import io.epigraph.util.ListView;
import io.epigraph.util.Unmodifiable;
import io.epigraph.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.Supplier;


public interface PersonId extends IntegerDatum.Static {

  @NotNull PersonId.Type type = new PersonId.Type();


  interface Value extends Val.Static {

    @Override
    @Nullable PersonId getDatum();

    @Override
    @NotNull PersonId.Imm.Value toImmutable();

  }


  interface Data extends io.epigraph.data.Data.Static {

    @Override
    @NotNull PersonId.Imm.Data toImmutable();

    @Nullable PersonId.Value get_value(); // default tag value

    @Nullable PersonId get(); // default tag datum

  }


  interface Imm extends PersonId, IntegerDatum.Imm.Static {


    final class Impl extends IntegerDatum.Imm.Static.Impl implements PersonId.Imm {

      private Impl(@NotNull IntegerDatum.Imm.Raw raw) { super(PersonId.type, raw); }

    }


    interface Value extends PersonId.Value, Val.Imm.Static {

      @Override
      @Nullable PersonId.Imm getDatum();


      final class Impl extends Val.Imm.Static.Impl<PersonId.Imm.Value, PersonId.Imm> implements PersonId.Imm.Value {

        public Impl(@NotNull Val.Imm.Raw raw) { super(PersonId.type, raw); }

      }


    }


    interface Data extends PersonId.Data, io.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable PersonId.Imm.Value get_value(); // implied default self-tag value

      @Override
      @Nullable PersonId.Imm get(); // implied default self-tag datum

      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<PersonId.Imm.Data> implements PersonId.Imm.Data {

        protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(PersonId.type, raw); }

        @Override
        public @Nullable PersonId.Imm.Value get_value() {
          return (PersonId.Imm.Value) _raw()._getValue(PersonId.type.self);
        }

        @Override
        public @Nullable PersonId.Imm get() {
          PersonId.Imm.Value value = get_value();
          return value == null ? null : value.getDatum();
        }

      }


    }


  }


  final class Builder extends IntegerDatum.Mut.Static<PersonId.Imm> implements PersonId {

    private Builder(@NotNull IntegerDatum.Mut.Raw raw) { super(PersonId.type, raw, PersonId.Imm.Impl::new); }


    final static class Value extends Val.Mut.Static<PersonId.Imm.Value, PersonId.Builder> implements PersonId.Value {

      public Value(@NotNull Val.Mut.Raw raw) { super(raw, PersonId.Imm.Value.Impl::new); }

    }


    final static class Data extends io.epigraph.data.Data.Mut.Static<PersonId.Imm.Data> implements PersonId.Data {

      protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super(PersonId.type, raw, PersonId.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable PersonId.Builder.Value get_value() {
        return (PersonId.Builder.Value) _raw()._getValue(PersonId.type.self);
      }

      @Override
      public @Nullable PersonId.Builder get() {
        return Util.apply(PersonId.Builder.Value::getDatum, get_value());
      }

      // implied default tag value
      public void set_value(@Nullable PersonId.Builder.Value value) { _raw()._setValue(PersonId.type.self, value); }

      // implied default tag datum
      public void set(@Nullable PersonId.Builder datum) {
        _raw()._getOrCreateTagValue(PersonId.type.self)._raw().setDatum(datum);
      }

    }


  }


  final class Type extends IntegerType.Static<
      PersonId.Imm, PersonId.Builder, PersonId.Imm.Value, PersonId.Builder.Value, PersonId.Imm.Data, PersonId.Builder.Data
      > {

    protected Type() {
      super(
          new QualifiedTypeName(new NamespaceName(new NamespaceName(null, "com"), "example"), "PersonId"),
          Collections.emptyList(),
          false,
          PersonId.Builder::new,
          PersonId.Builder.Value::new,
          PersonId.Builder.Data::new
      );
    }

    @Override
    protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> PersonId.List.type; }


  }


  interface List extends ListDatum.Static {

    PersonId.List.Type type = new PersonId.List.Type();

    java.util.List<@Nullable ? extends PersonId.Value> values();

    java.util.List<@Nullable ? extends PersonId> datums();


    interface Value extends Val.Static {

      @Override
      @Nullable PersonId.List getDatum();


      @Override
      @NotNull PersonId.List.Imm.Value toImmutable();

    }


    interface Data extends io.epigraph.data.Data.Static {

      @Override
      @NotNull PersonId.List.Imm.Data toImmutable();

      @Nullable PersonId.List.Value get(); // default tag

    }


    interface Imm extends PersonId.List, ListDatum.Imm.Static {

      @Override
      java.util.List<@Nullable ? extends PersonId.Imm.Value> values();

      @Override
      java.util.List<@Nullable ? extends PersonId.Imm> datums();


      interface Value extends PersonId.List.Value, Val.Imm.Static {

        @Override
        @Nullable PersonId.List.Imm getDatum();

        final class Impl extends Val.Imm.Static.Impl<PersonId.List.Imm.Value, PersonId.List.Imm>
            implements PersonId.List.Imm.Value {

          public Impl(@NotNull Val.Imm.Raw raw) { super(PersonId.List.type, raw); }

        }


      }


      interface Data extends PersonId.List.Data, io.epigraph.data.Data.Imm.Static {

        @Override
        @Nullable PersonId.List.Imm.Value get();


        final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<PersonId.List.Imm.Data>
            implements PersonId.List.Imm.Data {

          protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(PersonId.List.type, raw); }

          @Override
          public @Nullable PersonId.List.Imm.Value get() {
            return (PersonId.List.Imm.Value) _raw()._getValue(PersonId.List.type.self);
          }

        }


      }


      final class Impl extends ListDatum.Imm.Static.Impl<PersonId.List.Imm> implements PersonId.List.Imm {

        private Impl(@NotNull ListDatum.Imm.Raw raw) { super(PersonId.List.type, raw); }

        // method is private to not expose datas() for non-union types (so simple type can be replaced with union type without breaking backwards-compatibility)
        private java.util.List<@NotNull ? extends PersonId.Imm.Data> datas() {
          return (java.util.List<? extends PersonId.Imm.Data>) _raw()._elements();
        }

        @Override // implied default tag values
        public java.util.List<@Nullable ? extends PersonId.Imm.Value> values() {
          return new Unmodifiable.ListView<PersonId.Imm.Data, PersonId.Imm.Value>(
              datas(),
              PersonId.Imm.Data::get_value
          );
        }

        @Override // implied default tag datums
        public java.util.List<@Nullable ? extends PersonId.Imm> datums() {
          return new Unmodifiable.ListView<PersonId.Imm.Data, PersonId.Imm>(datas(), PersonId.Imm.Data::get);
        }

      }


    }


    final class Builder extends ListDatum.Mut.Static<PersonId.List.Imm> implements PersonId.List {

      protected Builder(@NotNull ListDatum.Mut.Raw raw) { super(PersonId.List.type, raw, PersonId.List.Imm.Impl::new); }

      // method is private to not expose datas() for non-union types (so simple type can be replaced with union type without breaking backwards-compatibility)
      private java.util.List<PersonId.Builder.@NotNull Data> datas() {
        return (java.util.List<PersonId.Builder.Data>) _raw()._elements();
      }

      @Override
      public java.util.List<PersonId.Builder.Value> values() {
        return new ListView<>(
            datas(),
            PersonId.Builder.Data::get_value,
            PersonId.Builder.Data::set_value,
            PersonId.type::createMutableData
        );
      }

      @Override
      public java.util.List<PersonId.@Nullable Builder> datums() {
        return new ListView<>(
            datas(),
            PersonId.Builder.Data::get,
            PersonId.Builder.Data::set,
            PersonId.type::createMutableData
        );
      }


      static final class Value extends Val.Mut.Static<PersonId.List.Imm.Value, PersonId.List.Builder>
          implements PersonId.List.Value {

        public Value(@NotNull Val.Mut.Raw raw) { super(raw, PersonId.List.Imm.Value.Impl::new); }

      }


      final static class Data extends io.epigraph.data.Data.Mut.Static<PersonId.List.Imm.Data>
          implements PersonId.List.Data {

        protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
          super(PersonId.List.type, raw, PersonId.List.Imm.Data.Impl::new);
        }

        // default tag value getter
        @Override
        public @Nullable PersonId.List.Builder.Value get() {
          return (PersonId.List.Builder.Value) _raw()._getValue(PersonId.List.type.self);
        }

        // default tag value setter
        public void set(@Nullable PersonId.List.Builder.Value value) {
          _raw()._setValue(PersonId.List.type.self, value);
        }

      }


    }


    final class Type extends AnonListType.Static<
        PersonId.List.Imm,
        PersonId.List.Builder,
        PersonId.List.Imm.Value,
        PersonId.List.Builder.Value,
        PersonId.List.Imm.Data,
        PersonId.List.Builder.Data
        > {

      private Type() {
        super(
            false,
            PersonId.type,
            PersonId.List.Builder::new,
            PersonId.List.Builder.Value::new,
            PersonId.List.Builder.Data::new
        );
      }

      @Override
      protected @NotNull Supplier<ListType> listTypeSupplier() {
        return () -> { // TODO or construct raw list type (make this default behavior and override in static types)?
          throw new IllegalStateException(
              "'" + AnonListTypeName.of(false, PersonId.List.type.name()) + "' not used anywhere in the schema"
          );
        };
      }

    }


  }


}
