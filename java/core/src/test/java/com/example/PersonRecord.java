/* Created by yegor on 7/22/16. */

package com.example;

import io.epigraph.data.ListDatum;
import io.epigraph.data.RecordDatum;
import io.epigraph.data.Val;
import io.epigraph.errors.ErrorValue;
import io.epigraph.names.AnonListTypeName;
import io.epigraph.names.NamespaceName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.AnonListType;
import io.epigraph.types.DataType;
import io.epigraph.types.ListType;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface PersonRecord extends RecordDatum.Static {

  @NotNull PersonRecord.Type type = new PersonRecord.Type();

  @NotNull Field id = new Field("id", PersonId.type.dataType(false), false);

  @NotNull Field bestFriend = new Field("bestFriend", PersonRecord.type.dataType(false), false);

  @NotNull Field friends = new Field("friends", PersonRecord.List.type.dataType(false), false);

  @Nullable PersonId getId();

  @Nullable PersonRecord getBestFriend();

  @Nullable PersonRecord.Value getBestFriend_value();

  @Nullable PersonRecord.List getFriends();

  @Nullable PersonRecord.List.Value getFriends_value();


  interface Value extends Val.Static {

    @Override
    @Nullable PersonRecord getDatum();

    @Override
    @NotNull PersonRecord.Imm.Value toImmutable();

  }


  interface Data extends io.epigraph.data.Data.Static {

    @Override
    @NotNull PersonRecord.Imm.Data toImmutable();

    @Nullable PersonRecord.Value get(); // default tag

  }


  class Type extends RecordType.Static<
      PersonRecord.Imm,
      PersonRecord.Builder,
      PersonRecord.Imm.Value,
      PersonRecord.Builder.Value,
      PersonRecord.Imm.Data,
      PersonRecord.Builder.Data
      > {

    private Type() {
      super(
          new QualifiedTypeName(new NamespaceName(new NamespaceName(null, "com"), "example"), "PersonRecord"),
          Collections.emptyList(),
          PersonRecord.Builder::new,
          PersonRecord.Builder.Value::new,
          PersonRecord.Builder.Data::new
      );
    }

    @Override
    public @NotNull java.util.List<@NotNull Field> immediateFields() {
      return Arrays.asList(PersonRecord.id, PersonRecord.bestFriend, PersonRecord.friends);
    }


//    @Override
//    protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> PersonRecord.List.type; }

  }


  interface Imm extends PersonRecord, RecordDatum.Imm.Static {

    @Override
    @Nullable PersonId.Imm getId();

    @Override
    @Nullable PersonRecord.Imm getBestFriend();

    @Override
    @Nullable PersonRecord.Imm.Value getBestFriend_value();

    @Override
    @Nullable PersonRecord.List.Imm getFriends();

    @Override
    @Nullable PersonRecord.List.Imm.Value getFriends_value();


    interface Value extends PersonRecord.Value, Val.Imm.Static {

      @Override
      @Nullable PersonRecord.Imm getDatum();


      final class Impl extends Val.Imm.Static.Impl<PersonRecord.Imm.Value, PersonRecord.Imm>
          implements PersonRecord.Imm.Value {

        public Impl(@NotNull Val.Imm.Raw raw) { super(PersonRecord.type, raw); }

      }


    }


    interface Data extends PersonRecord.Data, io.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable PersonRecord.Imm.Value get();


      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<PersonRecord.Imm.Data>
          implements PersonRecord.Imm.Data {

        protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(PersonRecord.type, raw); }

        @Override
        public @Nullable PersonRecord.Imm.Value get() {
          return (PersonRecord.Imm.Value) _raw()._getValue(PersonRecord.type.self);
        }

      }


    }


    final class Impl extends RecordDatum.Imm.Static.Impl<PersonRecord.Imm> implements PersonRecord.Imm {

      private Impl(RecordDatum.Imm.Raw raw) { super(PersonRecord.type, raw); }

      @Override
      public @Nullable PersonId.Imm getId() {
        PersonId.Imm.Value value = (PersonId.Imm.Value) _raw()._getValue(PersonRecord.id, PersonId.type.self);
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable PersonRecord.Imm.Value getBestFriend_value() {
        return (PersonRecord.Imm.Value) _raw()._getValue(bestFriend, PersonRecord.type.self);
      }

      @Override
      public @Nullable PersonRecord.Imm getBestFriend() {
        PersonRecord.Imm.Value value = getBestFriend_value();
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable PersonRecord.List.Imm.Value getFriends_value() {
        return (PersonRecord.List.Imm.Value) _raw()._getValue(PersonRecord.friends, PersonRecord.List.type.self);
      }

      @Override
      public @Nullable PersonRecord.List.Imm getFriends() {
        PersonRecord.List.Imm.Value value = getFriends_value();
        return value == null ? null : value.getDatum();
      }

    }


  }


  final class Builder extends RecordDatum.Mut.Static<PersonRecord.Imm> implements PersonRecord {

    private Builder(@NotNull RecordDatum.Mut.Raw raw) { super(PersonRecord.type, raw, PersonRecord.Imm.Impl::new); }

    @Override
    public @Nullable PersonId.Builder getId() {
      return (PersonId.Builder) _raw()._getDatum(PersonRecord.id, PersonId.type.self);
    }

    @Override
    public @Nullable PersonRecord.Builder.Value getBestFriend_value() {
      return (Builder.Value) _raw()._getValue(bestFriend, PersonRecord.type.self);
    }

    @Override
    public @Nullable PersonRecord.Builder getBestFriend() {
      Builder.Value value = getBestFriend_value();
      return value == null ? null : value.getDatum();
    }

    @Override
    public @Nullable PersonRecord.List.Builder.Value getFriends_value() {
      return (List.Builder.Value) _raw()._getValue(PersonRecord.friends, PersonRecord.List.type.self);
    }

    @Override
    public @Nullable PersonRecord.List.Builder getFriends() {
      List.Builder.Value value = getFriends_value();
      return value == null ? null : value.getDatum();
    }

    public void setId(@Nullable PersonId.Builder id) {
      _raw().getOrCreateFieldData(PersonRecord.id)._raw()._setDatum(PersonId.type.self, id);
    }

    public void setBestFriend(@Nullable PersonRecord.Builder bestFriend) {
      _raw().getOrCreateFieldData(PersonRecord.bestFriend)._raw()._setDatum(PersonRecord.type.self, bestFriend);
    }

    // TODO full set of field setters

    public void setFriends(@Nullable PersonRecord.List.Builder friends) {
      _raw().getOrCreateFieldData(PersonRecord.friends)._raw()._setDatum(PersonRecord.List.type.self, friends);
    }


    final static class Value extends Val.Mut.Static<PersonRecord.Imm.Value, Builder>
        implements PersonRecord.Value {

      public Value(@NotNull Val.Mut.Raw raw) { super(raw, PersonRecord.Imm.Value.Impl::new); }

    }


    final static class Data extends io.epigraph.data.Data.Mut.Static<PersonRecord.Imm.Data>
        implements PersonRecord.Data {

      protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super(PersonRecord.type, raw, PersonRecord.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable PersonRecord.Builder.Value get() {
        return (Builder.Value) _raw()._getValue(PersonRecord.type.self);
      }

      public void set(@Nullable PersonRecord.Builder.Value value) {
        _raw()._setValue(PersonRecord.type.self, value);
      } //default tag

    }


  }


  interface List extends ListDatum.Static {

    PersonRecord.List.Type type = new PersonRecord.List.Type();

    java.util.List<@Nullable ? extends PersonRecord.Value> values();

    java.util.List<@Nullable ? extends PersonRecord> datums();

    java.util.List<@Nullable ? extends ErrorValue> errors();


    interface Value extends Val.Static {

      @Override
      @Nullable PersonRecord.List getDatum();


      @Override
      @NotNull PersonRecord.List.Imm.Value toImmutable();

    }


    interface Data extends io.epigraph.data.Data.Static {

      @Override
      @NotNull PersonRecord.List.Imm.Data toImmutable();

      @Nullable PersonRecord.List.Value get(); // default tag

    }


    interface Imm extends PersonRecord.List, ListDatum.Imm.Static {

      java.util.List<@Nullable ? extends PersonRecord.Imm.Value> values();

      java.util.List<@Nullable ? extends PersonRecord.Imm> datums();

      java.util.List<@Nullable ? extends ErrorValue> errors();


      interface Value extends PersonRecord.List.Value, Val.Imm.Static {

        @Override
        @Nullable PersonRecord.List.Imm getDatum();

        final class Impl extends Val.Imm.Static.Impl<PersonRecord.List.Imm.Value, PersonRecord.List.Imm>
            implements PersonRecord.List.Imm.Value {

          public Impl(@NotNull Val.Imm.Raw raw) { super(PersonRecord.List.type, raw); }

        }


      }


      interface Data extends PersonRecord.List.Data, io.epigraph.data.Data.Imm.Static {

        @Override
        @Nullable PersonRecord.List.Imm.Value get();


        final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<PersonRecord.List.Imm.Data>
            implements PersonRecord.List.Imm.Data {

          protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(PersonRecord.List.type, raw); }

          @Override
          public @Nullable PersonRecord.List.Imm.Value get() {
            return (PersonRecord.List.Imm.Value) _raw()._getValue(PersonRecord.List.type.self);
          }

        }


      }


      final class Impl extends ListDatum.Imm.Static.Impl<PersonRecord.List.Imm> implements PersonRecord.List.Imm {

        private Impl(@NotNull ListDatum.Imm.Raw raw) { super(PersonRecord.List.type, raw); }

        @Override
        public java.util.List<@Nullable ? extends PersonRecord.Imm.Value> values() {
          return _raw()._elements().stream().map(data ->
              (PersonRecord.Imm.Value) data._raw()._getValue(PersonRecord.type.self) // TODO revise cast
          ).collect(Collectors.toList());
        }

        @Override
        public java.util.List<@Nullable ? extends PersonRecord.Imm> datums() {
          return _raw()._elements().stream().map(data ->
              (PersonRecord.Imm) data._raw()._getValue(PersonRecord.type.self).getDatum() // TODO revise nulls
          ).collect(Collectors.toList());
        }

        @Override
        public java.util.List<@Nullable ? extends ErrorValue> errors() {
          return _raw()._elements().stream().map(data ->
              data._raw()._getValue(PersonRecord.type.self).getError() // TODO revise nulls
          ).collect(Collectors.toList());
        }

      }


    }


    final class Builder extends ListDatum.Mut.Static<PersonRecord.List.Imm> implements PersonRecord.List {

      protected Builder(@NotNull ListDatum.Mut.Raw raw) {
        super(
            PersonRecord.List.type,
            raw,
            PersonRecord.List.Imm.Impl::new
        );
      }

      @Override
      public java.util.List<PersonRecord.Builder.Value> values() {
        return _raw()._elements().stream().map(data ->
            (PersonRecord.Builder.Value) data._raw()._getValue(PersonRecord.type.self) // TODO revise cast
        ).collect(Collectors.toList());
      }

      @Override
      public java.util.List<PersonRecord.Builder> datums() {
        return _raw()._elements().stream().map(data ->
                (PersonRecord.Builder) data._raw()._getValue(PersonRecord.type.self).getDatum()
            // TODO revise nulls (define Data._getDatum(Tag))
        ).collect(Collectors.toList());
      }

      @Override
      public java.util.List<@Nullable ErrorValue> errors() {
        return _raw()._elements().stream().map(data ->
            data._raw()._getValue(PersonRecord.type.self).getError() // TODO revise nulls (define Data._getError(Tag))
        ).collect(Collectors.toList());
      }


      static final class Value extends Val.Mut.Static<PersonRecord.List.Imm.Value, PersonRecord.List.Builder>
          implements PersonRecord.List.Value {

        public Value(@NotNull Val.Mut.Raw raw) { super(raw, PersonRecord.List.Imm.Value.Impl::new); }

      }


      final static class Data extends io.epigraph.data.Data.Mut.Static<PersonRecord.List.Imm.Data>
          implements PersonRecord.List.Data {

        protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
          super(PersonRecord.List.type, raw, PersonRecord.List.Imm.Data.Impl::new);
        }

        // default tag value getter
        @Override
        public @Nullable PersonRecord.List.Builder.Value get() {
          return (PersonRecord.List.Builder.Value) _raw()._getValue(PersonRecord.List.type.self);
        }

        // default tag value setter
        public void set(@Nullable PersonRecord.List.Builder.Value value) {
          _raw()._setValue(PersonRecord.List.type.self, value);
        }

      }


    }


    final class Type extends AnonListType.Static<
        PersonRecord.List.Imm,
        PersonRecord.List.Builder,
        PersonRecord.List.Imm.Value,
        PersonRecord.List.Builder.Value,
        PersonRecord.List.Imm.Data,
        PersonRecord.List.Builder.Data
        > {

      private Type() {
        super(
            Arrays.asList(),
            new DataType(false, PersonRecord.type, PersonRecord.type.self),
            PersonRecord.List.Builder::new,
            PersonRecord.List.Builder.Value::new,
            PersonRecord.List.Builder.Data::new
        );
      }

//      @Override
//      protected @NotNull Supplier<ListType> listTypeSupplier() {
//        return () -> { // TODO or construct raw list type (make this default behavior and override in static types)?
//          throw new IllegalStateException(
//              "'" + AnonListTypeName.of(false, PersonRecord.List.type.name()) + "' not used anywhere in the schema"
//          );
//        };
//      }

    }


  }


}
