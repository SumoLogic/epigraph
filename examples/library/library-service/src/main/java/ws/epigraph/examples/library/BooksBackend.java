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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Simple books backend implementation
 */
public final class BooksBackend {
  private static final AtomicLong nextId = new AtomicLong();
  private static final Map<BookId, BookData> books = new HashMap<>();

  private BooksBackend() {}

  /**
   * Creates new book
   *
   * @param title    book title
   * @param authorId book author ID
   * @param text     book text
   *
   * @return created book ID
   */
  public static @NotNull BookId addBook(@NotNull String title, @NotNull AuthorId authorId, @NotNull String text) {
    BookId id = BookId.create(nextId.incrementAndGet());
    books.put(id, new BookData(id, title, authorId, text));
    return id;
  }

  /**
   * Gets book by ID
   *
   * @param id book ID
   *
   * @return book data or {@code null} if not found
   */
  public static @Nullable BookData get(@NotNull BookId id) { return books.get(id); }

  /**
   * Finds books by author
   *
   * @param authorId author ID
   *
   * @return collection of books by this author
   */
  public static Collection<BookData> findByAuthor(AuthorId authorId) {
    return books.values().stream()
        .filter(e -> e.authorId.equals(authorId))
        .collect(Collectors.toList());
  }

  public static class BookData {
    public final @NotNull BookId id;
    public final @NotNull String title;
    public final @NotNull AuthorId authorId;
    public final @NotNull String text;

    public BookData(@NotNull BookId id, @NotNull String title, @NotNull AuthorId authorId, @NotNull String text) {
      this.id = id;
      this.title = title;
      this.authorId = authorId;
      this.text = text;
    }
  }

  static {
    addBook(
        "The Gold Bug",
        AuthorsBackend.ALLAN_POE,
        "MANY years ago, I contracted an intimacy with a Mr. William Legrand. " +
        "He was of an ancient Huguenot family, and had once been wealthy; but a series of " +
        "misfortunes had reduced him to want. To avoid the mortification consequent upon " +
        "his disasters, he left New Orleans, the city of his forefathers, and took up his " +
        "residence at Sullivan's Island, near Charleston, South Carolina. \n" +
        "\n" +
        "This Island is a very singular one. It consists of little else than the sea sand, " +
        "and is about three miles long. Its breadth at no point exceeds a quarter of a mile. " +
        "It is separated from the main land by a scarcely perceptible creek, oozing its way " +
        "through a wilderness of reeds and slime, a favorite resort of the marsh-hen. The " +
        "vegetation, as might be supposed, is scant, or at least dwarfish. No trees of any " +
        "magnitude are to be seen. Near the western extremity, where Fort Moultrie stands, and " +
        "where are some miserable frame buildings, tenanted, during summer, by the fugitives " +
        "from Charleston dust and fever, may be found, indeed, the bristly palmetto; but the " +
        "whole island, with the exception of this western point, and a line of hard, white " +
        "beach on the seacoast, is covered with a dense undergrowth of the sweet myrtle, so " +
        "much prized by the horticulturists of England. The shrub here often attains the height " +
        "of fifteen or twenty feet, and forms an almost impenetrable coppice, burthening the " +
        "air with its fragrance."
    );

    addBook(
        "A Study In Scarlet",
        AuthorsBackend.CONAN_DOYLE,
        "IN the year 1878 I took my degree of Doctor of Medicine of the University of London, and " +
        "proceeded to Netley to go through the course prescribed for surgeons in the army. Having " +
        "completed my studies there, I was duly attached to the Fifth Northumberland Fusiliers as " +
        "Assistant Surgeon. The regiment was stationed in India at the time, and before I could join " +
        "it, the second Afghan war had broken out. On landing at Bombay, I learned that my corps had " +
        "advanced through the passes, and was already deep in the enemy's country. I followed, however, " +
        "with many other officers who were in the same situation as myself, and succeeded in reaching " +
        "Candahar in safety, where I found my regiment, and at once entered upon my new duties.\n" +
        "\n" +
        "The campaign brought honours and promotion to many, but for me it had nothing but misfortune " +
        "and disaster. I was removed from my brigade and attached to the Berkshires, with whom I served " +
        "at the fatal battle of Maiwand. There I was struck on the shoulder by a Jezail bullet, which " +
        "shattered the bone and grazed the subclavian artery. I should have fallen into the hands of the " +
        "murderous Ghazis had it not been for the devotion and courage shown by Murray, my orderly, who " +
        "threw me across a pack-horse, and succeeded in bringing me safely to the British lines.\n" +
        "\n" +
        "Worn with pain, and weak from the prolonged hardships which I had undergone, I was removed, with " +
        "a great train of wounded sufferers, to the base hospital at Peshawar. Here I rallied, and had " +
        "already improved so far as to be able to walk about the wards, and even to bask a little upon the " +
        "verandah, when I was struck down by enteric fever, that curse of our Indian possessions. For " +
        "months my life was despaired of, and when at last I came to myself and became convalescent, I was " +
        "so weak and emaciated that a medical board determined that not a day should be lost in sending me " +
        "back to England. I was dispatched, accordingly, in the troopship \"Orontes,\" and landed a month " +
        "later on Portsmouth jetty, with my health irretrievably ruined, but with permission from a " +
        "paternal government to spend the next nine months in attempting to improve it."
    );

    addBook(
        "The Adventures of Tom Sawyer",
        AuthorsBackend.MARK_TWAIN,
        "\"TOM!\"\n" +
        "No answer.\n" +
        "\"TOM!\"\n" +
        "No answer.\n" +
        "\"What's gone with that boy, I wonder? You TOM!\"\n" +
        "No answer.\n" +
        "The old lady pulled her spectacles down and looked over them about the room; then she put them up " +
        "and looked out under them. She seldom or never looked THROUGH them for so small a thing as a boy; " +
        "they were her state pair, the pride of her heart, and were built for \"style,\" not service—she " +
        "could have seen through a pair of stove–lids just as well. She looked perplexed for a moment, and " +
        "then said, not fiercely, but still loud enough for the furniture to hear:\n" +
        "\"Well, I lay if I get hold of you I'll—\"\n" +
        "She did not finish, for by this time she was bending down and punching under the bed with the broom, " +
        "and so she needed breath to punctuate the punches with. She resurrected nothing but the cat."
    );
  }
}
