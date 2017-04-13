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
import ws.epigraph.examples.library.resources.books.operations.searchbyauthor.AbstractReadSearchByAuthorOperation;
import ws.epigraph.examples.library.resources.books.operations.searchbyauthor.output.OutputBookId_BookRecord_MapProjection;
import ws.epigraph.examples.library.resources.books.operations.searchbyauthor.output.OutputBooksFieldProjection;
import ws.epigraph.examples.library.resources.books.projections.output.bookprojection.OutputBookRecordProjection;
import ws.epigraph.schema.operations.ReadOperationDeclaration;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Search by author custom operation implementation
 */
public class SearchByAuthorOperation extends AbstractReadSearchByAuthorOperation {

  protected SearchByAuthorOperation(final @NotNull ReadOperationDeclaration declaration) {
    super(declaration);
  }

  /**
   * Runs the operation
   *
   * @param booksDataBuilder result builder to be populated, initially empty
   * @param outputProjection request projection
   *
   * @return {@code Future} of the books map data
   */
  @Override
  protected @NotNull CompletableFuture<BookId_BookRecord_Map.Data> process(
      final @NotNull BookId_BookRecord_Map.Builder.@NotNull Data booksDataBuilder,
      final @NotNull OutputBooksFieldProjection outputProjection) {


    final BookId_BookRecord_Map.Builder booksMap = BookId_BookRecord_Map.create();
    final OutputBookId_BookRecord_MapProjection booksMapProjection = outputProjection.dataProjection();
    final OutputBookRecordProjection bookRecordProjection = booksMapProjection.itemsProjection();


    for (AuthorId author : findAuthors(booksMapProjection.getAuthorParameter())) {
      for (BooksBackend.BookData book : BooksBackend.findByAuthor(author)) {
        booksMap.put_(book.id, BookBuilder.buildBook(book.id, bookRecordProjection));
      }
    }

    booksDataBuilder.set(booksMap);
    return CompletableFuture.completedFuture(booksDataBuilder);
  }

  /**
   * Find authors by pattern
   *
   * @param authorData author pattern. Absent fields are ignored
   *
   * @return collection of matching author IDs
   */
  private @NotNull Collection<AuthorId> findAuthors(@Nullable AuthorRecord authorData) {
    return authorData == null ?
           AuthorsBackend.allAuthors() :
           AuthorsBackend.findAuthors(
               authorData.getFirstName_() == null ? null : Optional.ofNullable(authorData.getFirstName()),
               authorData.getMiddleName_() == null ? null : Optional.ofNullable(authorData.getMiddleName()),
               authorData.getLastName_() == null ? null : Optional.ofNullable(authorData.getLastName())
           );
  }
}
