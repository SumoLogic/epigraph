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
import ws.epigraph.examples.library.resources.books.operations.read.AbstractReadOperation;
import ws.epigraph.examples.library.resources.books.operations.read.output.ReqOutputBookId_BookRecord_MapKeyProjection;
import ws.epigraph.examples.library.resources.books.operations.read.output.ReqOutputBookId_BookRecord_MapProjection;
import ws.epigraph.examples.library.resources.books.operations.read.output.ReqOutputBooksFieldProjection;
import ws.epigraph.examples.library.resources.books.operations.read.output.elements.ReqOutputBookRecordProjection;
import ws.epigraph.examples.library.resources.books.operations.read.output.elements.author.ReqOutputAuthorProjection;
import ws.epigraph.examples.library.resources.books.operations.read.output.elements.author.record.ReqOutputAuthorRecordProjection;
import ws.epigraph.examples.library.resources.books.operations.read.output.elements.text.ReqOutputTextProjection;
import ws.epigraph.examples.library.resources.books.operations.read.output.elements.text.plain.ReqOutputPlainTextProjection;
import ws.epigraph.schema.operations.ReadOperationDeclaration;

import java.util.concurrent.CompletableFuture;

public class BooksReadOperation extends AbstractReadOperation{

  BooksReadOperation(@NotNull final ReadOperationDeclaration declaration) {
    super(declaration);
  }

  @Override
  protected @NotNull CompletableFuture<BookId_BookRecord_Map.Data> process(
      @NotNull final BookId_BookRecord_Map.Builder.Data booksDataBuilder,
      @NotNull final ReqOutputBooksFieldProjection booksFieldProjection) {

    final BookId_BookRecord_Map.Builder booksMap = BookId_BookRecord_Map.create();
    final ReqOutputBookId_BookRecord_MapProjection booksMapProjection = booksFieldProjection.dataProjection();
    final ReqOutputBookRecordProjection bookRecordProjection = booksMapProjection.itemsProjection();

    for (final ReqOutputBookId_BookRecord_MapKeyProjection keyProjection : booksMapProjection.keys()) {
      final BookId.Imm bookId = keyProjection.value();
      booksMap.put_(bookId, getBook(bookId.getVal(), bookRecordProjection));
    }

    booksDataBuilder.set(booksMap);
    return CompletableFuture.completedFuture(booksDataBuilder);
  }

  private BookRecord.Value getBook(long bookId, final ReqOutputBookRecordProjection bookRecordProjection) {
    final BooksBackend.BookData bookData = BooksBackend.get(bookId);

    if (bookData == null) {
      return BookRecord.type.createValue(
          new ErrorValue(404, "No book with id " + bookId)
      );
    } else {
      final BookRecord.Builder book = BookRecord.create();
      book.setTitle(bookData.title);

      // only get author if requested
      final ReqOutputAuthorProjection authorProjection = bookRecordProjection.author();
      if (authorProjection != null)
        book.setAuthor$(getAuthor(bookData.authorId, authorProjection));

      // only get text if requested
      final ReqOutputTextProjection textProjection = bookRecordProjection.text();
      if (textProjection != null)
        book.setText$(getText(bookData, textProjection));

      return book.asValue();
    }
  }

  private Author getAuthor(long authorId, ReqOutputAuthorProjection authorProjection) {
    Author.Builder author = Author.create();
    author.setId(AuthorId.create(authorId));

    final ReqOutputAuthorRecordProjection authorRecordProjection = authorProjection.record();
    if (authorRecordProjection !=null)
      author.setRecord_(getAuthorRecord(authorId, authorRecordProjection));

    return author;
  }

  private AuthorRecord.Value getAuthorRecord(long authorId, ReqOutputAuthorRecordProjection authorRecordProjection) {
    final AuthorsBackend.AuthorData authorData = AuthorsBackend.get(authorId);

    if (authorData == null) {
      return AuthorRecord.type.createValue(
          new ErrorValue(404, "No author with id " + authorId)
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

  private Text getText(BooksBackend.BookData bookData, ReqOutputTextProjection textProjection) {
    Text.Builder text = Text.create();

    final ReqOutputPlainTextProjection plainTextProjection = textProjection.plain();
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
        count = textLength - offset - 1;

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
