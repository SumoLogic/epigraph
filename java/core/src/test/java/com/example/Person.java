/* Created by yegor on 8/11/16. */

package com.example;

import io.epigraph.data.Data;
import io.epigraph.data.ListDatum;
import io.epigraph.data.Val;
import io.epigraph.names.AnonListTypeName;
import io.epigraph.names.NamespaceName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.AnonListType;
import io.epigraph.types.DataType;
import io.epigraph.types.ListType;
import io.epigraph.types.Type.Tag;
import io.epigraph.types.UnionType;
import io.epigraph.util.ListView;
import io.epigraph.util.Unmodifiable;
import io.epigraph.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;


public interface Person extends Data.Static {

  Person.Type type = new Person.Type();

  @Override
  @NotNull Person.Imm toImmutable();

  // default tag
  @Nullable PersonId get();

  @Nullable PersonId.Value get_value();

  // `id` tag
  Tag id = new Tag("id", PersonId.type);

  @Nullable PersonId getId();

  @Nullable PersonId.Value getId_value(); // idValue()?

  // `record` tag
  Tag record = new Tag("record", PersonRecord.type);

  @Nullable PersonRecord getRecord();

  @Nullable PersonRecord.Value getRecord_value();


  final class Type extends UnionType.Static<Person.Imm, Person.Builder> {

    private Type() {
      super(
          new QualifiedTypeName(new NamespaceName(new NamespaceName(null, "com"), "example"), "Person"),
          Collections.emptyList(),
          Person.Builder::new
      );
    }

//    @Override
//    protected @NotNull Supplier<ListType> listTypeSupplier() { return () -> Person.List.type; }

    @Override
    public @NotNull Collection<@NotNull ? extends Tag> immediateTags() {
      return Arrays.asList(Person.id, Person.record); // TODO need to identify default tag, too?
    }

  }


  interface Imm extends Person, Data.Imm.Static {

    @Override
    @Nullable PersonId.Imm get();

    @Override
    @Nullable PersonId.Imm.Value get_value();

    @Override
    @Nullable PersonId.Imm getId();

    @Override
    @Nullable PersonId.Imm.Value getId_value();

    @Override
    @Nullable PersonRecord.Imm getRecord();

    @Override
    @Nullable PersonRecord.Imm.Value getRecord_value();

    final class Impl extends Data.Imm.Static.Impl<Person.Imm> implements Person.Imm {

      protected Impl(@NotNull Data.Imm.Raw raw) { super(Person.type, raw); }

      @Override
      public @Nullable PersonId.Imm get() { return getId(); }

      @Override
      public @Nullable PersonId.Imm.Value get_value() { return getId_value(); }

      @Override
      public @Nullable PersonId.Imm getId() {
        PersonId.Imm.Value value = getId_value();
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable PersonId.Imm.Value getId_value() {
        return (PersonId.Imm.Value) _raw()._getValue(Person.id);
      }

      @Override
      public @Nullable PersonRecord.Imm getRecord() {
        PersonRecord.Imm.Value value = getRecord_value();
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable PersonRecord.Imm.Value getRecord_value() {
        return (PersonRecord.Imm.Value) _raw()._getValue(Person.id);
      }

    }

  }


  final class Builder extends Data.Mut.Static<Person.Imm> implements Person {

    protected Builder(@NotNull Data.Mut.Raw raw) { super(Person.type, raw, Person.Imm.Impl::new); }

    @Override
    public @Nullable PersonId.Builder get() { return getId(); }

    @Override
    public @Nullable PersonId.Builder.Value get_value() { return getId_value(); }

    public Person.Builder set(@Nullable PersonId.Builder id) {
      setId(id);
      return this;
    }

    public Person.Builder set_value(@Nullable PersonId.Builder.Value idValue) {
      setId_value(idValue);
      return this;
    }

    @Override
    public @Nullable PersonId.Builder getId() {
      PersonId.Builder.Value value = getId_value();
      return value == null ? null : value.getDatum();
    }

    @Override
    public @Nullable PersonId.Builder.Value getId_value() {
      return (PersonId.Builder.Value) _raw()._getValue(Person.id);
    }

    public Person.Builder setId(@Nullable PersonId.Builder id) {
      _raw()._getOrCreateTagValue(Person.id)._raw().setDatum(id);
      return this;
    }

    public Person.Builder setId_value(@Nullable PersonId.Builder.Value idValue) {
      _raw()._setValue(Person.id, idValue);
      return this;
    }

    @Override
    public @Nullable PersonRecord.Builder getRecord() {
      PersonRecord.Builder.Value value = getRecord_value();
      return value == null ? null : value.getDatum();
    }

    @Override
    public @Nullable PersonRecord.Builder.Value getRecord_value() {
      return (PersonRecord.Builder.Value) _raw()._getValue(Person.id);
    }

    public Person.Builder setRecord(@Nullable PersonRecord.Builder record) {
      _raw()._getOrCreateTagValue(Person.record)._raw().setDatum(record);
      return this;
    }

    public Person.Builder setRecord_value(@Nullable PersonRecord.Builder.Value idValue) {
      _raw()._setValue(Person.id, idValue);
      return this;
    }

  }


  interface List extends ListDatum.Static {

    Person.List.Type type = new Person.List.Type();

    java.util.List<@NotNull ? extends Person> datas(); // union type (if declared) data list


    java.util.List<@Nullable ? extends PersonId.Value> values(); // default tag (if declared or implied) values list

    java.util.List<@Nullable ? extends PersonId> datums(); // default tag (if declared or implied) datum list


    java.util.List<@Nullable ? extends PersonId.Value> idValues(); // union `id` value list

    java.util.List<@Nullable ? extends PersonId> idDatums(); // union `id` datum list


    java.util.List<@Nullable ? extends PersonRecord.Value> recordValues(); // union `record` value list

    java.util.List<@Nullable ? extends PersonRecord> recordDatums(); // union `record` datum list


    interface Value extends Val.Static {

      @Override
      @Nullable Person.List getDatum();


      @Override
      @NotNull Person.List.Imm.Value toImmutable();

    }


    interface Data extends io.epigraph.data.Data.Static {

      @Override
      @NotNull Person.List.Imm.Data toImmutable();

      @Nullable Person.List.Value get(); // default tag

    }


    interface Imm extends Person.List, ListDatum.Imm.Static {

      java.util.List<@NotNull ? extends Person.Imm> datas(); // union type (if declared) data list


      java.util.List<@Nullable ? extends PersonId.Imm.Value> values(); // default tag (if declared or implied) values list

      java.util.List<@Nullable ? extends PersonId.Imm> datums(); // default tag (if declared or implied) datum list


      java.util.List<@Nullable ? extends PersonId.Imm.Value> idValues(); // union `id` value list

      java.util.List<@Nullable ? extends PersonId.Imm> idDatums(); // union `id` datum list


      java.util.List<@Nullable ? extends PersonRecord.Imm.Value> recordValues(); // union `record` value list

      java.util.List<@Nullable ? extends PersonRecord.Imm> recordDatums(); // union `record` datum list


      interface Value extends Person.List.Value, Val.Imm.Static {

        @Override
        @Nullable Person.List.Imm getDatum();

        final class Impl extends Val.Imm.Static.Impl<Person.List.Imm.Value, Person.List.Imm>
            implements Person.List.Imm.Value {

          public Impl(@NotNull Val.Imm.Raw raw) { super(Person.List.type, raw); }

        }


      }


      interface Data extends Person.List.Data, io.epigraph.data.Data.Imm.Static {

        @Override
        @Nullable Person.List.Imm.Value get();


        final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<Person.List.Imm.Data>
            implements Person.List.Imm.Data {

          protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super(Person.List.type, raw); }

          @Override
          public @Nullable Person.List.Imm.Value get() {
            return (Person.List.Imm.Value) _raw()._getValue(Person.List.type.self);
          }

        }


      }


      final class Impl extends ListDatum.Imm.Static.Impl<Person.List.Imm> implements Person.List.Imm {

        private Impl(@NotNull ListDatum.Imm.Raw raw) { super(Person.List.type, raw); }

        @Override
        public java.util.List<@NotNull ? extends Person.Imm> datas() {
          return Util.castEx(_raw()._elements());
        }

        @Override
        public java.util.List<@Nullable ? extends PersonId.Imm.Value> values() {
          return new Unmodifiable.ListView<Person.Imm, PersonId.Imm.Value>(datas(), Person.Imm::get_value);
        }

        @Override
        public java.util.List<@Nullable ? extends PersonId.Imm> datums() {
          return new Unmodifiable.ListView<Person.Imm, PersonId.Imm>(datas(), Person.Imm::get);
        }

        @Override
        public java.util.List<@Nullable ? extends PersonId.Imm.Value> idValues() {
          return new Unmodifiable.ListView<Person.Imm, PersonId.Imm.Value>(datas(), Person.Imm::getId_value);
        }

        @Override
        public java.util.List<@Nullable ? extends PersonId.Imm> idDatums() {
          return new Unmodifiable.ListView<Person.Imm, PersonId.Imm>(datas(), Person.Imm::getId);
        }

        @Override
        public java.util.List<@Nullable ? extends PersonRecord.Imm.Value> recordValues() {
          return new Unmodifiable.ListView<Person.Imm, PersonRecord.Imm.Value>(datas(), Person.Imm::getRecord_value);
        }

        @Override
        public java.util.List<@Nullable ? extends PersonRecord.Imm> recordDatums() {
          return new Unmodifiable.ListView<Person.Imm, PersonRecord.Imm>(datas(), Person.Imm::getRecord);
        }

      }


    }


    final class Builder extends ListDatum.Mut.Static<Person.List.Imm> implements Person.List {

      protected Builder(@NotNull ListDatum.Mut.Raw raw) {
        super(Person.List.type, raw, Person.List.Imm.Impl::new);
      }

      @Override
      public java.util.List<Person.@NotNull Builder> datas() {
        return Util.cast(_raw()._elements());
      }

      @Override
      public java.util.List<PersonId.Builder.@Nullable Value> values() {
        return new ListView<>(
            datas(),
            Person.Builder::get_value,
            Person.Builder::set_value,
            Person.type::createMutableData
        );
      }

      @Override
      public java.util.List<PersonId.@Nullable Builder> datums() {
        return new ListView<>(
            datas(),
            Person.Builder::get,
            Person.Builder::set,
            Person.type::createMutableData
        );
      }

      @Override
      public java.util.List<PersonId.Builder.@Nullable Value> idValues() {
        return new ListView<>(
            datas(),
            Person.Builder::getId_value,
            Person.Builder::setId_value,
            Person.type::createMutableData
        );
      }

      @Override
      public java.util.List<PersonId.@Nullable Builder> idDatums() {
        return new ListView<>(
            datas(),
            Person.Builder::getId,
            Person.Builder::setId,
            Person.type::createMutableData
        );
      }

      @Override
      public java.util.List<PersonRecord.Builder.@Nullable Value> recordValues() {
        return new ListView<>(
            datas(),
            Person.Builder::getRecord_value,
            Person.Builder::setRecord_value,
            Person.type::createMutableData
        );
      }

      @Override
      public java.util.List<PersonRecord.@Nullable Builder> recordDatums() {
        return new ListView<>(
            datas(),
            Person.Builder::getRecord,
            Person.Builder::setRecord,
            Person.type::createMutableData
        );
      }


      static final class Value extends Val.Mut.Static<Person.List.Imm.Value, Person.List.Builder>
          implements Person.List.Value {

        public Value(@NotNull Val.Mut.Raw raw) { super(raw, Person.List.Imm.Value.Impl::new); }

      }


      final static class Data extends io.epigraph.data.Data.Mut.Static<Person.List.Imm.Data>
          implements Person.List.Data {

        protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
          super(Person.List.type, raw, Person.List.Imm.Data.Impl::new);
        }

        // default tag value getter
        @Override
        public @Nullable Person.List.Builder.Value get() {
          return (Person.List.Builder.Value) _raw()._getValue(Person.List.type.self);
        }

        // default tag value setter
        public void set(@Nullable Person.List.Builder.Value value) {
          _raw()._setValue(Person.List.type.self, value);
        }

      }


    }


    final class Type extends AnonListType.Static<
        Person.List.Imm,
        Person.List.Builder,
        Person.List.Imm.Value,
        Person.List.Builder.Value,
        Person.List.Imm.Data,
        Person.List.Builder.Data
        > {

      private Type() {
        super(
            Arrays.asList(),
            new DataType(false, Person.type, null),
            Person.List.Builder::new,
            Person.List.Builder.Value::new,
            Person.List.Builder.Data::new
        );
      }

//      @Override
//      protected @NotNull Supplier<ListType> listTypeSupplier() {
//        return () -> { // TODO or construct raw list type (make this default behavior and override in static types)?
//          throw new IllegalStateException(
//              "'" + AnonListTypeName.of(false, Person.List.type.name()) + "' not used anywhere in the schema"
//          );
//        };
//      }

    }


  }

}
