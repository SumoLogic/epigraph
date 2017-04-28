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
import ws.epigraph.examples.library.resources.books.operations._create.AbstractCreateOperation;
import ws.epigraph.examples.library.resources.books.operations._create.input.InputBooksFieldProjection;
import ws.epigraph.examples.library.resources.books.operations._create.output.OutputBooksFieldProjection;
import ws.epigraph.schema.operations.CreateOperationDeclaration;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class BooksCreateOperation extends AbstractCreateOperation {

  BooksCreateOperation(@NotNull CreateOperationDeclaration declaration) {
    super(declaration);
  }

  @Override
  protected @NotNull CompletableFuture<BookId_List.Data> process(
      @NotNull BookId_List.Builder.Data responseBuilder,
      @NotNull BookRecord_List inputData,
      @Nullable InputBooksFieldProjection inputProjection,
      @NotNull OutputBooksFieldProjection outputProjection) {

    BookId_List.Builder bookIdList = BookId_List.create();
    responseBuilder.set(bookIdList);

    for (BookRecord book : inputData.datums()) {
      String title = book.getTitle();
      assert title != null; // guaranteed by input projection

      Text text = book.getText();
      assert text != null;
      PlainText plainText = text.getPlain();
      assert plainText != null;

      Author author = book.getAuthor();
      assert author != null;

      AuthorId authorId = author.getId();
      assert authorId != null;

      AuthorsBackend.AuthorData authorData = AuthorsBackend.get(authorId);
      if (authorData == null)
        bookIdList.addError(new ErrorValue(400, "Author with id " + authorId.getVal() + " not found"));
      else {
        BookId bookId = BooksBackend.addBook(title, authorId, plainText.getVal());
        bookIdList.add(bookId);
      }
    }

    return CompletableFuture.completedFuture(responseBuilder);
  }
}
