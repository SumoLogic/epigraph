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
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.OutputBookRecordProjection;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.author.OutputAuthorProjection;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.author.record.OutputAuthorRecordProjection;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.text.OutputTextProjection;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.text.plain.OutputPlainTextProjection;
import ws.epigraph.util.HttpStatusCode;

/**
 * Builds {@code Book} instances
 */
public final class BookBuilder {

  private BookBuilder() {}

  /**
   * Builds book record value object
   *
   * @param bookId               book ID
   * @param bookRecordProjection book record request projection
   *
   * @return book record value (contains either a book record or an error)
   */
  public static @NotNull BookRecord.Value buildBook(
      @NotNull BookId bookId,
      @NotNull OutputBookRecordProjection bookRecordProjection) {

    final BooksBackend.BookData bookData = BooksBackend.get(bookId);

    if (bookData == null) {
      return BookRecord.type.createValue(
          new ErrorValue(HttpStatusCode.NOT_FOUND.code(), "No book with id " + bookId)
      );
    } else {
      final BookRecord.Builder book = BookRecord.create();
      book.setTitle(bookData.title);

      // only get author if requested
      final OutputAuthorProjection authorProjection = bookRecordProjection.author();
      if (authorProjection != null)
        book.setAuthor(buildAuthor(bookData.authorId, authorProjection));

      // only get text if requested
      final OutputTextProjection textProjection = bookRecordProjection.text();
      if (textProjection != null)
        book.setText(buildText(bookData, textProjection));

      return book.asValue();
    }
  }

  /**
   * Builds author object
   *
   * @param authorId         author ID
   * @param authorProjection author request projection
   *
   * @return author object
   */
  private static @NotNull Author buildAuthor(
      @NotNull AuthorId authorId,
      @NotNull OutputAuthorProjection authorProjection) {

    Author.Builder author = Author.create();
    author.setId(authorId);

    final OutputAuthorRecordProjection authorRecordProjection = authorProjection.record();
    if (authorRecordProjection != null)
      author.setRecord_(getAuthorRecord(authorId, authorRecordProjection));

    return author;
  }

  /**
   * Builds author record object
   *
   * @param authorId               author ID
   * @param authorRecordProjection author record request projection
   *
   * @return author record
   */
  private static @NotNull AuthorRecord.Value getAuthorRecord(
      @NotNull AuthorId authorId,
      @NotNull OutputAuthorRecordProjection authorRecordProjection) {

    final AuthorsBackend.AuthorData authorData = AuthorsBackend.get(authorId);

    if (authorData == null) {
      return AuthorRecord.type.createValue(
          new ErrorValue(HttpStatusCode.NOT_FOUND.code(), "No author with id " + authorId)
      );
    } else {
      AuthorRecord.Builder author = AuthorRecord.create();

      if (authorData.firstName != null)
        author.setFirstName(authorData.firstName);

      if (authorData.middleName != null)
        author.setMiddleName(authorData.middleName);

      if (authorData.lastName != null)
        author.setLastName(authorData.lastName);

      return author.asValue();
    }
  }

  /**
   * Builds book text
   *
   * @param bookData       backend book data object
   * @param textProjection text request projection
   *
   * @return book text
   */
  private static @NotNull Text buildText(
      @NotNull BooksBackend.BookData bookData,
      @NotNull OutputTextProjection textProjection) {

    Text.Builder text = Text.create();

    final OutputPlainTextProjection plainTextProjection = textProjection.plain();
    if (plainTextProjection != null) {
      String bookText = bookData.text;
      long textLength = bookText.length();

      // handle parameters
      Long offset = plainTextProjection.getOffsetParameter();
      if (offset == null) offset = 0L;
      if (offset < 0 || offset >= bookText.length())
        offset = textLength - 1;

      Long count = plainTextProjection.getCountParameter();
      if (count == null || count < 0 || offset + count > textLength - 1)
        count = textLength - offset;

      int beginIndex = Math.toIntExact(offset);
      int endIndex = Math.toIntExact(beginIndex + count);

      final PlainText.Builder plainText = PlainText.create(bookText.substring(beginIndex, endIndex));

      // provide meta if requested
      if (plainTextProjection.meta() != null) {
        plainText.setMeta(
            PlainTextRange.create()
                .setOffset(offset)
                .setCount(count)
        );
      }

      text.setPlain(plainText);
    }

    return text;
  }
}
