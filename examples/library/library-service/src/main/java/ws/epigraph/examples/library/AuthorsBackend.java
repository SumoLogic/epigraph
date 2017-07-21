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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public final class AuthorsBackend {
  private static final AtomicLong nextId = new AtomicLong();
  private static final Map<AuthorId, AuthorData> authors = new HashMap<>();

  public static final AuthorId ALLAN_POE = addAuthor("Allan", null, "Poe");
  public static final AuthorId CONAN_DOYLE = addAuthor("Arthur", "Conan", "Doyle");
  public static final AuthorId MARK_TWAIN = addAuthor("Mark", null, "Twain");

  private AuthorsBackend() {}

  private static @NotNull AuthorId addAuthor(
      @Nullable String firstName,
      @Nullable String middleName,
      @Nullable String lastName) {

    AuthorId id = AuthorId.create(nextId.incrementAndGet());
    authors.put(id, new AuthorData(id.getVal(), firstName, middleName, lastName));
    return id;
  }

  public static @Nullable AuthorData get(@NotNull AuthorId id) {
    return authors.get(id);
  }

  /**
   * Gets all known author IDs
   *
   * @return collection of known author IDs
   */
  public static @NotNull Collection<@NotNull AuthorId> allAuthors() {
    return authors.keySet();
  }

  /**
   * Finds authors by name
   *
   * @param firstName  author first name or {@code null} for any
   * @param middleName author middle name or {@code null} for any
   * @param lastName   author last name or {@code null} for any
   *
   * @return collection of matching author's IDs
   */
  public static @NotNull Collection<@NotNull AuthorId> findAuthors(
      @Nullable Optional<String> firstName,
      @Nullable Optional<String> middleName,
      @Nullable Optional<String> lastName) {

    return authors.entrySet().stream()
        .filter(e ->
            (firstName != null && Objects.equals(firstName.orElse(null), e.getValue().firstName)) ||
            (middleName != null && Objects.equals(middleName.orElse(null), e.getValue().middleName)) ||
            (lastName != null && Objects.equals(lastName.orElse(null), e.getValue().lastName)) ||
            (firstName == null && middleName == null && lastName == null)
        )
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  public static class AuthorData {
    public final long id;
    public final @Nullable String firstName;
    public final @Nullable String middleName;
    public final @Nullable String lastName;

    public AuthorData(long id, @Nullable String firstName, @Nullable String middleName, @Nullable String lastName) {
      this.id = id;
      this.firstName = firstName;
      this.middleName = middleName;
      this.lastName = lastName;
    }
  }
}
