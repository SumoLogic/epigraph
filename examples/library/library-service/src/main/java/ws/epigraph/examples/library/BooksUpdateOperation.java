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
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.examples.library.resources.books.operations._update.AbstractUpdateOperation;
import ws.epigraph.examples.library.resources.books.operations._update.output.OutputBooksFieldProjection;
import ws.epigraph.examples.library.resources.books.operations._update.update.UpdateBooksFieldProjection;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.OutputBookRecordProjection;
import ws.epigraph.schema.operations.UpdateOperationDeclaration;
import ws.epigraph.util.HttpStatusCode;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class BooksUpdateOperation extends AbstractUpdateOperation {
  protected BooksUpdateOperation(@NotNull UpdateOperationDeclaration declaration) {
    super(declaration);
  }

  @Override
  protected @NotNull CompletableFuture<BookId_BookRecord_Map.Data> process(
      @NotNull BookId_BookRecord_Map.Builder.Data responseBuilder,
      @NotNull BookId_BookRecord_Map updateData,
      @Nullable UpdateBooksFieldProjection updateProjection,
      @NotNull OutputBooksFieldProjection outputProjection) {

    OutputBookRecordProjection bookOutputProjection = outputProjection.dataProjection().itemsProjection();

    BookId_BookRecord_Map.Builder booksMapBuilder = BookId_BookRecord_Map.create();
    responseBuilder.set(booksMapBuilder);

    for (Map.Entry<BookId.Imm, ? extends BookRecord.Value> entry : updateData.values().entrySet()) {
      BookId.Imm bookId = entry.getKey();
      BookRecord bookUpdate = entry.getValue().getDatum();

      assert bookUpdate != null; // ensured by framework

      BooksBackend.BookData bookData = BooksBackend.get(bookId);
      if (bookData == null) {
        booksMapBuilder.putError(
            bookId,
            new ErrorValue(HttpStatusCode.NOT_FOUND, "Book " + bookId.getVal() + " not found")
        );
      } else {
        // values from the update request
        Optional<String> title = Optional.ofNullable(bookUpdate.getTitle());
        Optional<AuthorId> author = Optional.ofNullable(bookUpdate.getAuthor()).map(Author::getId);
        Optional<String> text = Optional.ofNullable(bookUpdate.getText()).map(Text::getPlain).map(PlainText::getVal);

        // new fields state: either updates or existing data
        String newTitle = title.orElse(bookData.title);
        AuthorId newAuthorId = author.orElse(bookData.authorId);
        String newText = text.orElse(bookData.text);

        // check author ID
        if (AuthorsBackend.get(newAuthorId) == null) {
          booksMapBuilder.putError(
              bookId,
              new ErrorValue(HttpStatusCode.BAD_REQUEST, "Author " + newAuthorId.getVal() + " not found")
          );
        } else {
          BooksBackend.set(
              bookId,
              new BooksBackend.BookData(bookId, newTitle, newAuthorId, newText)
          );
          // return updated book record
          booksMapBuilder.put_(bookId, BookBuilder.buildBook(bookId, bookOutputProjection));
        }

      }

    }

    return CompletableFuture.completedFuture(responseBuilder);
  }
}
