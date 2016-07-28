/* Created by yegor on 7/22/16. */

package com.example;

import io.epigraph.data.RecordDatum;
import io.epigraph.data.builders.RecordDatumBuilder;
import io.epigraph.data.immutable.ImmRecordDatum;
import io.epigraph.data.mutable.MutRecordDatum;
import io.epigraph.names.NamespaceName;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface PersonRecord extends RecordDatum {

  public static final @NotNull PersonRecord.Type type = new PersonRecord.Type();

  public static final @NotNull Field bestFriend = new Field("bestFriend", PersonRecord.type);

  public default @Nullable PersonRecord getBestFriend() {
    return (PersonRecord) getDatum(bestFriend, PersonRecord.type.self);
  }


  public static class Type extends RecordType {

    private Type() {
      super(
          new QualifiedTypeName(new NamespaceName(new NamespaceName(null, "com"), "example"), "PersonRecord"),
          Collections.emptyList(),
          false
      );
    }

    @Override
    public @NotNull List<@NotNull Field> immediateFields() {
      return Arrays.asList(
          PersonRecord.bestFriend
      );
    }

    @Override
    public @NotNull PersonRecord.Builder builder() { return new Builder(); }

    @Override
    public @NotNull PersonRecord.Mut mutable() { return new MutImpl(); }


    private static class ImmImpl extends ImmRecordDatum.Impl implements PersonRecord.Imm {

      protected ImmImpl(@NotNull PersonRecord recordDatum) { super(recordDatum); }

    }


    private static class MutImpl extends MutRecordDatum.Impl implements PersonRecord.Mut {

      protected MutImpl() { super(PersonRecord.type); }

    }


  }


  public static interface Imm extends PersonRecord, ImmRecordDatum {}


  public static interface Mut extends PersonRecord, MutRecordDatum {

    // TODO bestFriend field in PersonRecord should be marked as abstract to allow overriding itself
    // TODO in such case this method shouldn't be generated (but should be for PersonRecord.Builder)
    default @NotNull PersonRecord.Mut setBestFriend(@Nullable PersonRecord datum) {
      return (PersonRecord.Mut) setDatum(PersonRecord.bestFriend, PersonRecord.type.self, datum);
    }

  }


  public static class Builder extends RecordDatumBuilder implements PersonRecord {

    public Builder() { super(PersonRecord.type); }

    public @NotNull PersonRecord.Builder setBestFriend(@Nullable PersonRecord datum) {
      return (PersonRecord.Builder) setDatum(PersonRecord.bestFriend, PersonRecord.type.self, datum);
    }

  }


}
