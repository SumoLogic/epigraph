package io.epigraph.projections.op;

import com.example.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsTest {
  @Test
  public void testRecursiveFieldProjection() {
    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        true,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null, null)
                    )
                ), true
            )
        ),
        null
    );

    OpOutputFieldProjection recursiveBestFriendProjection = new OpOutputFieldProjection(
        PersonRecord.bestFriend,
        null,
        new OpOutputVarProjection(
            Person.type,
            new OpOutputTagProjection(
                Person.record,
                false,
                recursivePersonRecordProjection
            )
        ), true
    );
    recursivePersonRecordProjection.addFieldProjection(
        recursiveBestFriendProjection
    );

    OpOutputRecordModelProjection recursivePersonRecordProjection2 = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        true,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null, null)
                    )
                ), true
            )
        ),
        null
    );
    OpOutputFieldProjection recursiveBestFriendProjection2 = new OpOutputFieldProjection(
        PersonRecord.bestFriend,
        null,
        new OpOutputVarProjection(
            Person.type,
            new OpOutputTagProjection(
                Person.record,
                false,
                recursivePersonRecordProjection2
            )
        ), true
    );
    recursivePersonRecordProjection2.addFieldProjection(
        recursiveBestFriendProjection2
    );

    assertEquals(recursiveBestFriendProjection.hashCode(), recursiveBestFriendProjection2.hashCode());
    assertEquals(recursiveBestFriendProjection, recursiveBestFriendProjection2);
  }

  @Test
  public void testRecursiveRecordProjection() {
    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        true,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null, null)
                    )
                ), true
            )
        ),
        null
    );
    recursivePersonRecordProjection.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            new OpOutputVarProjection(
                Person.type,
                new OpOutputTagProjection(
                    Person.record,
                    false,
                    recursivePersonRecordProjection
                )
            ), true
        )
    );

    OpOutputRecordModelProjection recursivePersonRecordProjection2 = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        true,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null, null)
                    )
                ), true
            )
        ),
        null
    );
    recursivePersonRecordProjection2.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            new OpOutputVarProjection(
                Person.type,
                new OpOutputTagProjection(
                    Person.record,
                    false,
                    recursivePersonRecordProjection2
                )
            ), true
        )
    );

    assertEquals(recursivePersonRecordProjection.hashCode(), recursivePersonRecordProjection2.hashCode());
    assertEquals(recursivePersonRecordProjection, recursivePersonRecordProjection2);
  }

  public static void main(String[] args) {

    OpOutputRecordModelProjection recursivePersonRecordProjection = new OpOutputRecordModelProjection(
        PersonRecord.type,
        false,
        null,
        OpOutputRecordModelProjection.fields(
            new OpOutputFieldProjection(
                PersonRecord.id,
                null,
                new OpOutputVarProjection(
                    Person.type,
                    new OpOutputTagProjection(
                        Person.id,
                        true,
                        new OpOutputPrimitiveModelProjection(PersonId.type, false, null, null)
                    )
                ), true
            )
        ),
        null
    );
    recursivePersonRecordProjection.addFieldProjection(
        new OpOutputFieldProjection(
            PersonRecord.bestFriend,
            null,
            new OpOutputVarProjection(
                Person.type,
                new OpOutputTagProjection(
                    Person.record,
                    false,
                    recursivePersonRecordProjection
                )
            ), true
        )
    );

    OpOutputVarProjection personVarProjection = new OpOutputVarProjection(
        Person.type,
        new OpOutputTagProjection(
            Person.id,
            true,
            new OpOutputPrimitiveModelProjection(
                PersonId.type,
                true,
                OpParam.params(
                    new OpParam("token", UserId.type) // todo string
                ),
                null
            )
        ),
        new OpOutputTagProjection(
            Person.record,
            true,
            new OpOutputRecordModelProjection(
                PersonRecord.type,
                false,
                null,
                OpOutputRecordModelProjection.fields(
                    new OpOutputFieldProjection(
                        PersonRecord.bestFriend,
                        null,
                        // todo make it recursive?
                        new OpOutputVarProjection(
                            Person.type,
                            new OpOutputTagProjection(
                                Person.id,
                                true,
                                new OpOutputPrimitiveModelProjection(PersonId.type, false, null, null)
                            )
                        ), true
                    )
                ),
                OpOutputRecordModelProjection.tails(
                    new OpOutputRecordModelProjection(
                        UserRecord.type,
                        false, null,
                        OpOutputRecordModelProjection.fields(
                            new OpOutputFieldProjection(
                                UserRecord.bestFriend,
                                OpParam.params(new OpParam("maxAge", PersonId.type)),
                                new OpOutputVarProjection(
                                    Person.type, // todo ??
                                    new OpOutputTagProjection(
                                        Person.record, // todo ??
                                        false,
                                        recursivePersonRecordProjection
                                    )
                                ),
                                true
                            )
                        ),
                        null
                    )
                )
            )
        )
    );

    System.out.println(personVarProjection);
  }
}
