/*
 * Copyright 2017 Sumo Logic
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

package ws.epigraph.examples.library;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class AuthorsBackend {
  private static final AtomicLong nextId = new AtomicLong();
  private static final Map<Long, AuthorData> authors = new HashMap<>();

  public static long ALLAN_POE = addAuthor("Allan", null, "Poe");
  public static long CONAN_DOYLE = addAuthor("Arthur", "Conan", "Doyle");
  public static long MARK_TWAIN = addAuthor("Mark", null, "Twain");

  private static long addAuthor(String firstName, String middleName, String lastName) {
    long id = nextId.incrementAndGet();
    authors.put(id, new AuthorData(firstName, middleName, lastName));
    return id;
  }

  public static AuthorData get(long id) {
    return authors.get(id);
  }

  public static class AuthorData {
    public final String firstName;
    public final String middleName;
    public final String lastName;

    public AuthorData(final String firstName, final String middleName, final String lastName) {
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
    }
  }
}
