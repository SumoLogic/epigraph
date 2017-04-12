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
import ws.epigraph.examples.library.resources.books.operations._read.AbstractReadOperation;
import ws.epigraph.examples.library.resources.books.operations._read.output.OutputBookId_BookRecord_MapKeyProjection;
import ws.epigraph.examples.library.resources.books.operations._read.output.OutputBookId_BookRecord_MapProjection;
import ws.epigraph.examples.library.resources.books.operations._read.output.OutputBooksFieldProjection;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.OutputBookRecordProjection;
import ws.epigraph.schema.operations.ReadOperationDeclaration;

import java.util.concurrent.CompletableFuture;

/**
 * Default read operation implementation
 */
public class BooksReadOperation extends AbstractReadOperation {

  BooksReadOperation(final @NotNull ReadOperationDeclaration declaration) {
    super(declaration);
  }

  /**
   * Builds books collection based on specified output projection
   *
   * @param booksDataBuilder     books map data builder to be populated
   * @param booksFieldProjection books field output projection
   *
   * @return {@code Future} of the books map data
   */
  @Override
  protected @NotNull CompletableFuture<BookId_BookRecord_Map.Data> process(
      final @NotNull BookId_BookRecord_Map.Builder.Data booksDataBuilder,
      final @NotNull OutputBooksFieldProjection booksFieldProjection) {

    final BookId_BookRecord_Map.Builder booksMap = BookId_BookRecord_Map.create();
    final OutputBookId_BookRecord_MapProjection booksMapProjection = booksFieldProjection.dataProjection();
    final OutputBookRecordProjection bookRecordProjection = booksMapProjection.itemsProjection();

    for (final OutputBookId_BookRecord_MapKeyProjection keyProjection : booksMapProjection.keys()) {
      final BookId.Imm bookId = keyProjection.value();
      booksMap.put_(bookId, BookBuilder.buildBook(bookId, bookRecordProjection));
    }

    booksDataBuilder.set(booksMap);
    return CompletableFuture.completedFuture(booksDataBuilder);
  }

}
