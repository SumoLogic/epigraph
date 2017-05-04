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
import ws.epigraph.examples.library.resources.books.operations._delete.AbstractDeleteOperation;
import ws.epigraph.examples.library.resources.books.operations._delete.delete.DeleteBookId_BookRecord_MapKeyProjection;
import ws.epigraph.examples.library.resources.books.operations._delete.delete.DeleteBooksFieldProjection;
import ws.epigraph.examples.library.resources.books.operations._delete.output.OutputBooksFieldProjection;
import ws.epigraph.schema.operations.DeleteOperationDeclaration;
import ws.epigraph.util.HttpStatusCode;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class BooksDeleteOperation extends AbstractDeleteOperation {
  BooksDeleteOperation(@NotNull DeleteOperationDeclaration declaration) {
    super(declaration);
  }

  @Override
  protected @NotNull CompletableFuture<BookId_BookRecord_Map.Data> process(
      @NotNull BookId_BookRecord_Map.Builder.Data responseBuilder,
      @NotNull DeleteBooksFieldProjection deleteProjection,
      @NotNull OutputBooksFieldProjection outputProjection) {

    BookId_BookRecord_Map.Builder booksMapBuilder = BookId_BookRecord_Map.create();
    responseBuilder.set(booksMapBuilder);

    for (DeleteBookId_BookRecord_MapKeyProjection keyProjection : deleteProjection.dataProjection().keys()) {
      BookId bookId = keyProjection.value();

      if (!BooksBackend.delete(bookId)) {
        booksMapBuilder.putError(
            bookId,
            new ErrorValue(HttpStatusCode.NOT_FOUND, "Book " + bookId.getVal() + " not found")
        );
      }
    }

    return CompletableFuture.completedFuture(responseBuilder);
  }
}
