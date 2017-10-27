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
import ws.epigraph.assembly.AsmContext;
import ws.epigraph.examples.library._resources.books.operations.read._default.AbstractReadOperation;
import ws.epigraph.examples.library._resources.books.operations.read._default.output.BookId_BookRecord_MapAsm;
import ws.epigraph.examples.library._resources.books.operations.read._default.output.OutputBooksFieldProjection;
import ws.epigraph.schema.operations.ReadOperationDeclaration;
import ws.epigraph.util.Function2;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * {@link ws.epigraph.assembly.Asm assembler}-based read operation implementation
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AsmBasedBooksReadOperation extends AbstractReadOperation {
  protected AsmBasedBooksReadOperation(@NotNull ReadOperationDeclaration declaration) { super(declaration); }

  @Override
  protected @NotNull CompletableFuture<BookId_BookRecord_Map.Data> process(
      @NotNull BookId_BookRecord_Map.Data.Builder resultBuilder,
      @NotNull OutputBooksFieldProjection projection) {

    BookId_BookRecord_Map.Value value =
        new BookId_BookRecord_MapAsm<>(
            Function.identity(), // key converter
            Function2.identity1(), // map extractor
            BookRecordAsmImpl.INSTANCE // items assembler
        ).assemble(BooksBackend.getBooks(), projection.dataProjection(), new AsmContext());

    resultBuilder.set_(value);

    return CompletableFuture.completedFuture(resultBuilder);
  }
}
