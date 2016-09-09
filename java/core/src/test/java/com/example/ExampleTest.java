/* Created by yegor on 7/25/16. */

package com.example;

//import io.epigraph.printers.RecordTypePrinter;

import org.jetbrains.annotations.NotNull;

public class ExampleTest {

  public static void main(String... args) {
    System.out.println(UserRecord.type);
    System.out.println(PersonRecord.type);
//    new RecordTypePrinter().println(System.out, PersonRecord.type);
//    new RecordTypePrinter().println(System.out, UserRecord.type);

    PersonRecord.@NotNull Builder pr = PersonRecord.type.createBuilder();
    pr.setId(PersonId.type.createBuilder(123));
    System.out.println(pr.getId().getVal());

    PersonRecord.type.createBuilder().setBestFriend((PersonRecord.Builder) null);
    PersonRecord.type.createBuilder().setBestFriend((PersonRecord.Builder) null);

    UserRecord.type.createBuilder().setBestFriend((UserRecord.Builder) null);

    //UserRecord.type.mutable().setBestFriend((PersonRecord.Builder) null); // should fail at compile-time

    UserRecord.Builder userRecord = UserRecord.type.createBuilder();
    userRecord.setBestFriend((UserRecord.Builder) null);
    UserRecord.Builder bestFriend = userRecord.getBestFriend();

    PersonId.List.Builder pids = PersonId.List.type.createBuilder();
    for (int i = 0; i < 10; ++i) {
      pids.datums().add(PersonId.type.createBuilder(i));
    }
    for (int i = 10; i < 20; ++i) {
      PersonId.Builder.Value valueBuilder = PersonId.type.createValueBuilder();
      valueBuilder.setDatum(PersonId.type.createBuilder(i));
      pids.values().add(valueBuilder);
    }
    for (PersonId.Builder pid : pids.datums()) {
      System.out.println(pid.getVal());
    }


  }

}
