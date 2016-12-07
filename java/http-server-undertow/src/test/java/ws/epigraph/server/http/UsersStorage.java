/*
 * Copyright 2016 Sumo Logic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ws.epigraph.server.http;

import epigraph.String;
import org.jetbrains.annotations.NotNull;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.tests.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample service backend. Not thread safe, so one request at a time please.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UsersStorage {
  private final PersonId_Person_Map.Builder storage;
  private final AtomicInteger nextId = new AtomicInteger();

  public UsersStorage() {
    storage = PersonId_Person_Map.create();

    // create initial storage state
    for (int id = 1; id <= 10; ++id) {
      PersonId pid = PersonId.create(id);
      storage.put$(pid, createPerson(pid));
    }

    nextId.set(11);
  }

  public @NotNull PersonId_Person_Map.Builder users() {
    return storage;
  }

  public @NotNull PersonId insertPerson(@NotNull PersonRecord.Builder personRecord) {
    PersonId id = PersonId.create(nextId.incrementAndGet());
    personRecord.setId(id);
    storage.put$(id, Person.create().setId(id).setRecord(personRecord));
    return id;
  }

  private static @NotNull Person createPerson(@NotNull PersonId pid) {
    Integer id = pid.getVal();

    final Person bestFriend;
    final UserRecord bestFriendRecord = UserRecord.create()
        .setId(PersonId.create(id + 1))
        .setFirstName(String.create("First" + (id + 1)))
        .setLastName(String.create("Last" + (id + 1)))
        .setProfile(Url.create("http://google.com/" + (id + 1)));

    if (id % 5 == 0)
      bestFriend = Person.create().setId(PersonId.create(id + 1)).setRecord(bestFriendRecord);
    else
      bestFriend = User.create().setId(UserId.create(id + 1)).setRecord(bestFriendRecord);

    return User
        .create()
        .setId(UserId.create(pid.getVal()))
        .setRecord(UserRecord
            .create()
            .setId(pid)
            .setFirstName(epigraph.String.create("First" + id))
            .setLastName(epigraph.String.create("Last" + id))
            .setProfile_Error(new ErrorValue(404, "Not Found", null))
            .setBestFriend$(bestFriend
            )
            .setWorstEnemy(UserRecord.create()
                .setId(PersonId.create(id + 2))
                .setFirstName(epigraph.String.create("First" + (id + 2)))
                .setLastName(epigraph.String.create("Last" + (id + 2)))
                .setProfile(Url.create("http://bing.com/" + (id + 2)))
            )
            .setFriends(Person_List.create()
                .add(User.create()
                    .setId(UserId.create(id + 1))
                    .setRecord(UserRecord.create()
                        .setId(PersonId.create(id + 1))
                        .setFirstName(epigraph.String.create("First" + (id + 1)))
                        .setLastName(epigraph.String.create("Last" + (id + 1)))
                        .setProfile(Url.create("http://google.com/" + (id + 1)))
                    )
                )
                .add(Person.create()
                    .setId(PersonId.create(id + 3))
                    .setRecord(PersonRecord.create()
                        .setId(PersonId.create(id + 3))
                        .setFirstName(epigraph.String.create("First" + (id + 3)))
                        .setLastName(epigraph.String.create("Last" + (id + 3)))
                    )
                )
            )
        );
  }

}