/* Created by yegor on 8/11/16. */

package com.example;

import io.epigraph.data.Data;
import io.epigraph.names.NamespaceName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.ListType;
import io.epigraph.types.Type.Tag;
import io.epigraph.types.UnionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
          false,
          Person.Builder::new
      );
    }

    @Override
    protected @NotNull Supplier<ListType> listTypeSupplier() {
      return null; // TODO
    }

    @Override
    public @NotNull List<Tag> immediateTags() {
      return Arrays.asList(Person.id, Person.record); // TODO need to specify default tag, too?
    }

    @Override
    public @NotNull List<Tag> tags() {
      return null; // FIXME parent should deal with this (similar to fields() in record type)
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

    // TODO setters

    @Override
    public @Nullable PersonId.Builder get() { return getId(); }

    @Override
    public @Nullable PersonId.Builder.Value get_value() { return getId_value(); }

    public void set(@Nullable PersonId.Builder id) {
      setId(id);
    }

    public void set_value(@Nullable PersonId.Builder.Value idValue) {
      setId_value(idValue);
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
    
    public void setId(@Nullable PersonId.Builder id) {
      _raw()._getOrCreateTagValue(Person.id)._raw().setDatum(id);
    }

    public void setId_value(@Nullable PersonId.Builder.Value idValue) {
      _raw()._setValue(Person.id, idValue);
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

    public void setRecord(@Nullable PersonRecord.Builder record) {
      _raw()._getOrCreateTagValue(Person.record)._raw().setDatum(record);
    }

    public void setRecord_value(@Nullable PersonRecord.Builder.Value idValue) {
      _raw()._setValue(Person.id, idValue);
    }

  }


}
