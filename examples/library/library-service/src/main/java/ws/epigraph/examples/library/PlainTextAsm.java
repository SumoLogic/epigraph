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
import ws.epigraph.assembly.Asm;
import ws.epigraph.assembly.AsmContext;
import ws.epigraph.examples.library._resources.books.projections.output.bookprojection.text.plain.OutputPlainTextProjection;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class PlainTextAsm implements Asm<BooksBackend.BookData, OutputPlainTextProjection, PlainText.Value> {
  public static final PlainTextAsm INSTANCE = new PlainTextAsm();

  @Override
  public @NotNull PlainText.Value assemble(
      @NotNull BooksBackend.BookData book,
      @NotNull OutputPlainTextProjection projection,
      @NotNull AsmContext ctx) {

    String bookText = book.text;
    long textLength = bookText.length();

    // handle parameters
    Long offset = projection.getOffsetParameter();
    if (offset == null) offset = 0L;
    if (offset < 0 || offset >= bookText.length())
      offset = textLength - 1;

    Long count = projection.getCountParameter();
    if (count == null || count < 0 || offset + count > textLength - 1)
      count = textLength - offset;

    int beginIndex = Math.toIntExact(offset);
    int endIndex = Math.toIntExact(beginIndex + count);

    final PlainText.Builder plainText = PlainText.create(bookText.substring(beginIndex, endIndex));

    // provide meta if requested
    if (projection.meta() != null) {
      plainText.setMeta(
          PlainTextRange.create()
              .setOffset(offset)
              .setCount(count)
      );
    }

    return plainText.asValue();
  }
}
