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
import io.epigraph.types.ListType;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface UserRecord extends PersonRecord {

  @NotNull UserRecord.Type type = new UserRecord.Type();

  @NotNull Field bestFriend = new Field("bestFriend", UserRecord.type, true);

  @NotNull Field friends = new Field("friends", UserRecord.List.type, false);

  @Nullable UserRecord getBestFriend();

  @Nullable UserRecord.Value getBestFriend_value();

  @Nullable UserRecord.List getFriends();

  @Nullable UserRecord.List.Value getFriends_value();


  interface Value extends PersonRecord.Value {

    @Override
    @Nullable UserRecord getDatum();

    @Override
    @NotNull UserRecord.Imm.Value toImmutable();

  }


  interface Data extends PersonRecord.Data {

    @Override
    @NotNull UserRecord.Imm.Data toImmutable();

    @Nullable UserRecord.Value get(); // default tag

  }


  class Type extends RecordType.Static<
      UserRecord.Imm,
      Builder,
      UserRecord.Imm.Value,
      Builder.Value,
      UserRecord.Imm.Data,
      Builder.Data
      > {

    private Type() {
      super(
          new QualifiedTypeName(new NamespaceName(new NamespaceName(null, "com"), "example"), "UserRecord"),
          Collections.emptyList(),
          false,
          UserRecord.Builder::new,
          UserRecord.Builder.Value::new,
          UserRecord.Builder.Data::new
      );
    }

    @Override
    public @NotNull java.util.List<@NotNull Field> immediateFields() {
      return Arrays.asList(UserRecord.bestFriend);
    }


    @Override
    protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> UserRecord.List.type; }

  }


  interface Imm extends UserRecord, PersonRecord.Imm {

    @Override
    @Nullable UserRecord.Imm getBestFriend();

    @Override
    @Nullable UserRecord.Imm.Value getBestFriend_value();

    @Override
    @Nullable UserRecord.List.Imm getFriends();

    @Override
    @Nullable UserRecord.List.Imm.Value getFriends_value();


    interface Value extends UserRecord.Value, PersonRecord.Imm.Value {

      @Override
      @Nullable UserRecord.Imm getDatum();


      final class Impl extends Val.Imm.Static.Impl<UserRecord.Imm.Value, UserRecord.Imm>
          implements UserRecord.Imm.Value {

        public Impl(@NotNull Val.Imm.Raw raw) { super(UserRecord.type, raw); }

      }


    }


    interface Data extends UserRecord.Data, PersonRecord.Imm.Data {

      @Override
      @Nullable UserRecord.Imm.Value get();


      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<UserRecord.Imm.Data>
          implements UserRecord.Imm.Data {

        protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(UserRecord.type, raw); }

        @Override
        public @Nullable UserRecord.Imm.Value get() {
          return (UserRecord.Imm.Value) _raw()._getValue(UserRecord.type.self);
        }

      }


    }


    final class Impl extends RecordDatum.Imm.Static.Impl<UserRecord.Imm> implements UserRecord.Imm {

      private Impl(RecordDatum.Imm.Raw raw) { super(UserRecord.type, raw); }

      @Override
      public @Nullable PersonId.Imm getId() {
        PersonId.Imm.Value value = (PersonId.Imm.Value) _raw()._getValue(PersonRecord.id, PersonId.type.self);
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable UserRecord.Imm.Value getBestFriend_value() {
        return (UserRecord.Imm.Value) _raw()._getValue(UserRecord.bestFriend, UserRecord.type.self);
      }

      @Override
      public @Nullable UserRecord.Imm getBestFriend() {
        UserRecord.Imm.Value value = getBestFriend_value();
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable UserRecord.List.Imm.Value getFriends_value() {
        return (UserRecord.List.Imm.Value) _raw()._getValue(UserRecord.friends, UserRecord.List.type.self);
      }

      @Override
      public @Nullable UserRecord.List.Imm getFriends() {
        UserRecord.List.Imm.Value value = getFriends_value();
        return value == null ? null : value.getDatum();
      }

    }


  }


  final class Builder extends RecordDatum.Mut.Static<UserRecord.Imm> implements UserRecord {

    private Builder(@NotNull RecordDatum.Mut.Raw raw) { super(UserRecord.type, raw, UserRecord.Imm.Impl::new); }

    @Override
    public @Nullable PersonId.Builder getId() {
      PersonId.Builder.Value value = (PersonId.Builder.Value) _raw()._getValue(PersonRecord.id, PersonId.type.self);
      return value == null ? null : value.getDatum();
    }

    @Override
    public @Nullable UserRecord.Builder.Value getBestFriend_value() {
      return (UserRecord.Builder.Value) _raw()._getValue(bestFriend, UserRecord.type.self);
    }

    @Override
    public @Nullable UserRecord.Builder getBestFriend() {
      UserRecord.Builder.Value value = getBestFriend_value();
      return value == null ? null : value.getDatum();
    }

    @Override
    public @Nullable UserRecord.List.Builder.Value getFriends_value() {
      return (UserRecord.List.Builder.Value) _raw()._getValue(UserRecord.friends, UserRecord.List.type.self);
    }

    @Override
    public @Nullable UserRecord.List.Builder getFriends() {
      UserRecord.List.Builder.Value value = getFriends_value();
      return value == null ? null : value.getDatum();
    }

    public void setBestFriend(@Nullable UserRecord.Builder bestFriend) {
      _raw().getOrCreateFieldData(UserRecord.bestFriend)._raw()._setDatum(UserRecord.type.self, bestFriend);
    }

    // TODO full set of field setters


    final static class Value extends Val.Mut.Static<UserRecord.Imm.Value, UserRecord.Builder>
        implements UserRecord.Value {

      public Value(@NotNull Val.Mut.Raw raw) { super(raw, UserRecord.Imm.Value.Impl::new); }

    }


    final static class Data extends io.epigraph.data.Data.Mut.Static<UserRecord.Imm.Data>
        implements UserRecord.Data {

      protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super(UserRecord.type, raw, UserRecord.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable UserRecord.Builder.Value get() {
        return (UserRecord.Builder.Value) _raw()._getValue(UserRecord.type.self);
      }

      public void set(@Nullable UserRecord.Builder.Value value) {
        _raw()._setValue(UserRecord.type.self, value);
      } //default tag

    }


  }


  interface List extends PersonRecord.List {

    UserRecord.List.Type type = new UserRecord.List.Type();

    java.util.List<@Nullable ? extends UserRecord.Value> values();

    java.util.List<@Nullable ? extends UserRecord> datums();

    java.util.List<@Nullable ? extends ErrorValue> errors();


    interface Value extends PersonRecord.List.Value {

      @Override
      @Nullable UserRecord.List getDatum();


      @Override
      @NotNull UserRecord.List.Imm.Value toImmutable();

    }


    interface Data extends PersonRecord.List.Data {

      @Override
      @NotNull UserRecord.List.Imm.Data toImmutable();

      @Nullable UserRecord.List.Value get(); // default tag

    }


    interface Imm extends UserRecord.List, PersonRecord.List.Imm {

      java.util.List<@Nullable ? extends UserRecord.Imm.Value> values();

      java.util.List<@Nullable ? extends UserRecord.Imm> datums();

      java.util.List<@Nullable ? extends ErrorValue> errors();


      interface Value extends UserRecord.List.Value, PersonRecord.List.Imm.Value {

        @Override
        @Nullable UserRecord.List.Imm getDatum();

        final class Impl extends Val.Imm.Static.Impl<UserRecord.List.Imm.Value, UserRecord.List.Imm>
            implements UserRecord.List.Imm.Value {

          public Impl(@NotNull Val.Imm.Raw raw) { super(UserRecord.List.type, raw); }

        }


      }


      interface Data extends UserRecord.List.Data, PersonRecord.List.Imm.Data {

        @Override
        @Nullable UserRecord.List.Imm.Value get();


        final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<UserRecord.List.Imm.Data>
            implements UserRecord.List.Imm.Data {

          protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(UserRecord.List.type, raw); }

          @Override
          public @Nullable UserRecord.List.Imm.Value get() {
            return (UserRecord.List.Imm.Value) _raw()._getValue(UserRecord.List.type.self);
          }

        }


      }


      final class Impl extends ListDatum.Imm.Static.Impl<UserRecord.List.Imm> implements UserRecord.List.Imm {

        private Impl(@NotNull ListDatum.Imm.Raw raw) { super(UserRecord.List.type, raw); }

        @Override
        public java.util.List<@Nullable ? extends UserRecord.Imm.Value> values() {
          return _raw()._elements().stream().map(data ->
              (UserRecord.Imm.Value) data._raw()._getValue(UserRecord.type.self) // TODO revise cast
          ).collect(Collectors.toList());
        }

        @Override
        public java.util.List<@Nullable ? extends UserRecord.Imm> datums() {
          return _raw()._elements().stream().map(data ->
              (UserRecord.Imm) data._raw()._getValue(UserRecord.type.self).getDatum() // TODO revise nulls
          ).collect(Collectors.toList());
        }

        @Override
        public java.util.List<@Nullable ? extends ErrorValue> errors() {
          return _raw()._elements().stream().map(data ->
              data._raw()._getValue(UserRecord.type.self).getError() // TODO revise nulls
          ).collect(Collectors.toList());
        }

      }


    }


    final class Builder extends ListDatum.Mut.Static<UserRecord.List.Imm> implements UserRecord.List {

      protected Builder(@NotNull ListDatum.Mut.Raw raw) {
        super(
            UserRecord.List.type,
            raw,
            UserRecord.List.Imm.Impl::new
        );
      }

      @Override
      public java.util.List<UserRecord.Builder.Value> values() {
        return _raw()._elements().stream().map(data ->
            (UserRecord.Builder.Value) data._raw()._getValue(UserRecord.type.self) // TODO revise cast
        ).collect(Collectors.toList());
      }

      @Override
      public java.util.List<UserRecord.Builder> datums() {
        return _raw()._elements().stream().map(data ->
                (UserRecord.Builder) data._raw()._getValue(UserRecord.type.self).getDatum()
            // TODO revise nulls (define Data._getDatum(Tag))
        ).collect(Collectors.toList());
      }

      @Override
      public java.util.List<@Nullable ErrorValue> errors() {
        return _raw()._elements().stream().map(data ->
            data._raw()._getValue(UserRecord.type.self).getError() // TODO revise nulls (define Data._getError(Tag))
        ).collect(Collectors.toList());
      }


      static final class Value extends Val.Mut.Static<UserRecord.List.Imm.Value, UserRecord.List.Builder>
          implements UserRecord.List.Value {

        public Value(@NotNull Val.Mut.Raw raw) { super(raw, UserRecord.List.Imm.Value.Impl::new); }

      }


      final static class Data extends io.epigraph.data.Data.Mut.Static<UserRecord.List.Imm.Data>
          implements UserRecord.List.Data {

        protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
          super(UserRecord.List.type, raw, UserRecord.List.Imm.Data.Impl::new);
        }

        // default tag value getter
        @Override
        public @Nullable UserRecord.List.Builder.Value get() {
          return (UserRecord.List.Builder.Value) _raw()._getValue(UserRecord.List.type.self);
        }

        // default tag value setter
        public void set(@Nullable UserRecord.List.Builder.Value value) {
          _raw()._setValue(UserRecord.List.type.self, value);
        }

      }


    }


    final class Type extends AnonListType.Static<
        UserRecord.List.Imm,
        UserRecord.List.Builder,
        UserRecord.List.Imm.Value,
        UserRecord.List.Builder.Value,
        UserRecord.List.Imm.Data,
        UserRecord.List.Builder.Data
        > {

      private Type() {
        super(
            false,
            UserRecord.type,
            UserRecord.List.Builder::new,
            UserRecord.List.Builder.Value::new,
            UserRecord.List.Builder.Data::new
        );
      }

      @Override
      protected @NotNull Supplier<ListType> listTypeSupplier() {
        return () -> { // TODO or construct raw list type (make this default behavior and override in static types)?
          throw new IllegalStateException(
              "'" + AnonListTypeName.of(false, UserRecord.List.type.name()) + "' not used anywhere in the schema"
          );
        };
      }

    }


  }


}
