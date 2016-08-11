/* Created by yegor on 8/2/16. */

package com.example;

import io.epigraph.data.IntegerDatum;
import io.epigraph.data.ListDatum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.names.AnonListTypeName;
import io.epigraph.names.NamespaceName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.AnonListType;
import io.epigraph.types.IntegerType;
import io.epigraph.types.ListType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public interface UserId extends PersonId {

  @NotNull UserId.Type type = new UserId.Type();


  interface Value extends PersonId.Value, Val.Static {

    @Override
    @Nullable UserId getDatum();

    @Override
    @NotNull UserId.Imm.Value toImmutable();

  }


  interface Data extends PersonId.Data {

    @Override
    @NotNull UserId.Imm.Data toImmutable();

    @Nullable UserId.Value get(); // default tag

  }


  interface Imm extends UserId, PersonId.Imm {


    final class Impl extends IntegerDatum.Imm.Static.Impl implements UserId.Imm {

      private Impl(@NotNull IntegerDatum.Imm.Raw raw) { super(UserId.type, raw); }

    }


    interface Value extends UserId.Value, PersonId.Imm.Value {

      @Override
      @Nullable UserId.Imm getDatum();


      final class Impl extends Val.Imm.Static.Impl<UserId.Imm.Value, UserId.Imm> implements UserId.Imm.Value {

        public Impl(@NotNull Val.Imm.Raw raw) { super(UserId.type, raw); }

      }


    }


    interface Data extends UserId.Data, PersonId.Imm.Data {

      @Override
      @Nullable UserId.Imm.Value get();


      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<UserId.Imm.Data> implements UserId.Imm.Data {

        protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(UserId.type, raw); }

        @Override
        public @Nullable UserId.Imm.Value get() { return (UserId.Imm.Value) _raw()._getValue(UserId.type.self); }

      }


    }


  }


  final class Builder extends IntegerDatum.Mut.Static<UserId.Imm> implements UserId {

    private Builder(Raw raw) { super(UserId.type, raw, UserId.Imm.Impl::new); }


    final static class Value extends Val.Mut.Static<UserId.Imm.Value, UserId.Builder> implements UserId.Value {

      public Value(@NotNull Val.Mut.Raw raw) { super(raw, UserId.Imm.Value.Impl::new); }

    }


    final static class Data extends io.epigraph.data.Data.Mut.Static<UserId.Imm.Data> implements UserId.Data {

      protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super(UserId.type, raw, UserId.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable UserId.Builder.Value get() { return (UserId.Builder.Value) _raw()._getValue(UserId.type.self); }

      public void set(@Nullable UserId.Builder.Value value) { _raw()._setValue(UserId.type.self, value); } //default tag

    }


  }


  final class Type extends IntegerType.Static<
      UserId.Imm, UserId.Builder, UserId.Imm.Value, UserId.Builder.Value, UserId.Imm.Data, UserId.Builder.Data
      > {

    protected Type() {
      super(
          new QualifiedTypeName(new NamespaceName(new NamespaceName(null, "com"), "example"), "UserId"),
          Arrays.asList(PersonId.type),
          false,
          UserId.Builder::new,
          UserId.Builder.Value::new,
          UserId.Builder.Data::new
      );
    }

    @Override // TODO pass as super constructor argument
    protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> UserId.List.type; }

  }


  interface List extends PersonId.List {

    UserId.List.Type type = new UserId.List.Type();

    java.util.List<@Nullable ? extends UserId.Value> values();

    java.util.List<@Nullable ? extends UserId> datums();

    java.util.List<@Nullable ? extends ErrorValue> errors();


    interface Value extends PersonId.List.Value {

      @Override
      @Nullable UserId.List getDatum();


      @Override
      @NotNull UserId.List.Imm.Value toImmutable();

    }


    interface Data extends PersonId.List.Data {

      @Override
      @NotNull UserId.List.Imm.Data toImmutable();

      @Nullable UserId.List.Value get(); // default tag

    }


    interface Imm extends UserId.List, PersonId.List.Imm {

      java.util.List<@Nullable ? extends UserId.Imm.Value> values();

      java.util.List<@Nullable ? extends UserId.Imm> datums();

      java.util.List<@Nullable ? extends ErrorValue> errors();


      interface Value extends UserId.List.Value, PersonId.List.Imm.Value {

        @Override
        @Nullable UserId.List.Imm getDatum();

        final class Impl extends Val.Imm.Static.Impl<UserId.List.Imm.Value, UserId.List.Imm>
            implements UserId.List.Imm.Value {

          public Impl(@NotNull Val.Imm.Raw raw) { super(UserId.List.type, raw); }

        }


      }


      interface Data extends UserId.List.Data, PersonId.List.Imm.Data {

        @Override
        @Nullable UserId.List.Imm.Value get();


        final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<UserId.List.Imm.Data>
            implements UserId.List.Imm.Data {

          protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(UserId.List.type, raw); }

          @Override
          public @Nullable UserId.List.Imm.Value get() {
            return (UserId.List.Imm.Value) _raw()._getValue(UserId.List.type.self);
          }

        }


      }


      final class Impl extends ListDatum.Imm.Static.Impl<UserId.List.Imm> implements UserId.List.Imm {

        private Impl(@NotNull ListDatum.Imm.Raw raw) { super(UserId.List.type, raw); }

        @Override
        public java.util.List<@Nullable ? extends UserId.Imm.Value> values() {
          return _raw()._elements().stream().map(data ->
              (UserId.Imm.Value) data._raw()._getValue(UserId.type.self) // TODO revise cast
          ).collect(Collectors.toList());
        }

        @Override
        public java.util.List<@Nullable ? extends UserId.Imm> datums() {
          return _raw()._elements().stream().map(data ->
              (UserId.Imm) data._raw()._getValue(UserId.type.self).getDatum() // TODO revise nulls
          ).collect(Collectors.toList());
        }

        @Override
        public java.util.List<@Nullable ? extends ErrorValue> errors() {
          return _raw()._elements().stream().map(data ->
              data._raw()._getValue(UserId.type.self).getError() // TODO revise nulls
          ).collect(Collectors.toList());
        }

      }


    }


    final class Builder extends ListDatum.Mut.Static<UserId.List.Imm> implements UserId.List {

      protected Builder(@NotNull ListDatum.Mut.Raw raw) { super(UserId.List.type, raw, UserId.List.Imm.Impl::new); }

      @Override
      public java.util.List<UserId.Builder.Value> values() {
        return _raw()._elements().stream().map(data ->
            (UserId.Builder.Value) data._raw()._getValue(UserId.type.self) // TODO revise cast
        ).collect(Collectors.toList());
      }

      @Override
      public java.util.List<UserId.@Nullable Builder> datums() {
        return _raw()._elements().stream().map(data ->
                (UserId.Builder) data._raw()._getValue(UserId.type.self).getDatum()
            // TODO revise nulls (define Data._getDatum(Tag))
        ).collect(Collectors.toList());
      }

      @Override
      public java.util.List<@Nullable ErrorValue> errors() {
        return _raw()._elements().stream().map(data ->
            data._raw()._getValue(UserId.type.self).getError() // TODO revise nulls (define Data._getError(Tag))
        ).collect(Collectors.toList());
      }


      static final class Value extends Val.Mut.Static<UserId.List.Imm.Value, UserId.List.Builder>
          implements UserId.List.Value {

        public Value(@NotNull Val.Mut.Raw raw) { super(raw, UserId.List.Imm.Value.Impl::new); }

      }


      final static class Data extends io.epigraph.data.Data.Mut.Static<UserId.List.Imm.Data>
          implements UserId.List.Data {

        protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
          super(UserId.List.type, raw, UserId.List.Imm.Data.Impl::new);
        }

        // default tag value getter
        @Override
        public @Nullable UserId.List.Builder.Value get() {
          return (UserId.List.Builder.Value) _raw()._getValue(UserId.List.type.self);
        }

        // default tag value setter
        public void set(@Nullable UserId.List.Builder.Value value) { _raw()._setValue(UserId.List.type.self, value); }

      }


    }


    final class Type extends AnonListType.Static<
        UserId.List.Imm,
        UserId.List.Builder,
        UserId.List.Imm.Value,
        UserId.List.Builder.Value,
        UserId.List.Imm.Data,
        UserId.List.Builder.Data
        > {

      private Type() {
        super(
            false,
            UserId.type,
            UserId.List.Builder::new,
            UserId.List.Builder.Value::new,
            UserId.List.Builder.Data::new
        );
      }

      @Override
      protected @NotNull Supplier<ListType> listTypeSupplier() {
        return () -> { // TODO or construct raw list type (make this default behavior and override in static types)?
          throw new IllegalStateException(
              "'" + AnonListTypeName.of(false, UserId.List.type.name()) + "' not used anywhere in the schema"
          );
        };
      }

    }


  }


}
