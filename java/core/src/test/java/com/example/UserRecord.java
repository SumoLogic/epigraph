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
import java.util.List;

public interface UserRecord extends PersonRecord, RecordDatum {

  @NotNull UserRecord.Type type = new UserRecord.Type(); // TODO potential clash with user-defined "type" field

  @NotNull Field bestFriend = new RecordType.Field("bestFriend", UserRecord.type, false);

  public default @Nullable UserRecord getBestFriend() {
    return (UserRecord) getDatum(bestFriend, PersonRecord.type.self);
  }

  class Type extends RecordType {

    private Type() {
      super(
          new QualifiedTypeName(new NamespaceName(new NamespaceName(null, "com"), "example"), "UserRecord"),
          Arrays.asList(PersonRecord.type),
          false
      );
    }

    @Override
    public @NotNull List<@NotNull Field> immediateFields() {
      return Arrays.asList(
          UserRecord.bestFriend
      );
    }

    @Override
    public @NotNull UserRecord.Builder builder() {
      return new Builder();
    }

    @Override
    public @NotNull UserRecord.Mut mutable() {
      return new MutImpl();
    }


    private static class MutImpl extends MutRecordDatum.Impl implements UserRecord.Mut {

      // TODO field setter methods (share some with Builder?)

      private MutImpl() {
        super(UserRecord.type);
      }

    }

  }

  public static interface Imm extends UserRecord, PersonRecord.Imm, ImmRecordDatum {}


  public static interface Mut extends UserRecord, PersonRecord.Mut, MutRecordDatum {

    default @NotNull UserRecord.Mut setBestFriend(@Nullable UserRecord datum) {
      return (UserRecord.Mut) setDatum(UserRecord.bestFriend, UserRecord.type.self, datum);
    }

  }


  public static class Builder extends RecordDatumBuilder implements UserRecord {

    private Builder() { super(UserRecord.type); }

    public @NotNull UserRecord.Builder setBestFriend(@Nullable UserRecord datum) {
      return (UserRecord.Builder) setDatum(UserRecord.bestFriend, UserRecord.type.self, datum);
    }

  }


}
