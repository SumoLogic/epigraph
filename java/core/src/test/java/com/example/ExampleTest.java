/* Created by yegor on 7/25/16. */

package com.example;

//import io.epigraph.printers.RecordTypePrinter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExampleTest {

  public static void main(String... args) {
    System.out.println(UserRecord.type);
    System.out.println(PersonRecord.type);
//    new RecordTypePrinter().println(System.out, PersonRecord.type);
//    new RecordTypePrinter().println(System.out, UserRecord.type);

    PersonRecord.@NotNull Mut pr = PersonRecord.type.createMutableDatum();
    pr.setId(PersonId.type.createMutableDatum(123));
    System.out.println(pr.getId().getVal());

    PersonRecord.type.createMutableDatum().setBestFriend((PersonRecord.Mut) null);
    PersonRecord.type.createMutableDatum().setBestFriend((PersonRecord.Mut) null);

    UserRecord.type.createMutableDatum().setBestFriend((UserRecord.Mut) null);

    //UserRecord.type.mutable().setBestFriend((PersonRecord.Mut) null); // should fail at compile-time

    UserRecord.Mut userRecord = UserRecord.type.createMutableDatum();
    userRecord.setBestFriend((UserRecord.Mut) null);
    @Nullable UserRecord bestFriend = userRecord.getBestFriend();
  }

}
