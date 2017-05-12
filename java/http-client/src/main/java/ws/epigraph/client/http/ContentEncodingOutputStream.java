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

package ws.epigraph.client.http;

import org.apache.http.nio.ContentEncoder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Output stream that sends all the output to provided content encoder
 * <p>
 * It is highly encouraged wrap this instance in a {@code BufferedOutputStream} for efficiency.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ContentEncodingOutputStream extends OutputStream {
  private final @NotNull ContentEncoder encoder;
  private final ByteBuffer singleByteBuffer = ByteBuffer.allocate(1);

  public ContentEncodingOutputStream(final @NotNull ContentEncoder encoder) {this.encoder = encoder;}

  @Override
  public void write(final int b) throws IOException {
    singleByteBuffer.clear();
    singleByteBuffer.put((byte) b);
    encoder.write(singleByteBuffer);
  }

  @Override
  public void write(final @NotNull byte[] b) throws IOException {
    ByteBuffer buf = ByteBuffer.wrap(b);
    encoder.write(buf);
  }

  @Override
  public void write(final @NotNull byte[] b, final int off, final int len) throws IOException {
    ByteBuffer buf = ByteBuffer.wrap(b, off, len);
    encoder.write(buf);
  }

  @Override
  public void close() throws IOException {
    encoder.complete();
  }
}
