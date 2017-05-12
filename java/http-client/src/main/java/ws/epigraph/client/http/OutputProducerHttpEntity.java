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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OutputProducerHttpEntity implements HttpEntity {
  public static final Logger LOG = LoggerFactory.getLogger(OutputProducerHttpEntity.class);

  private final @NotNull String contentType;
  private final @NotNull OutputProducer outputProducer;

  public OutputProducerHttpEntity(
      final @NotNull String contentType,
      final @NotNull OutputProducer outputProducer) {

    this.contentType = contentType;
    this.outputProducer = outputProducer;
  }

  @Override
  public boolean isRepeatable() { return true; }

  @Override
  public boolean isChunked() { return false; }

  @Override
  public long getContentLength() { return -1; }

  @Override
  public Header getContentType() { return new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType); }

  @Override
  public Header getContentEncoding() { return null; }

  @Override
  public InputStream getContent() throws IOException, UnsupportedOperationException {
    // could fiddle with piped streams and extra threads here too but seems like an overkill

    LOG.warn("Memory-inefficient mode!"); //todo remove

    DirectBufferAccessByteArrayOutputStream baos = new DirectBufferAccessByteArrayOutputStream();
    outputProducer.produce(baos);
    return new ByteArrayInputStream(baos.getBuffer());
  }

  @Override
  public void writeTo(final OutputStream os) throws IOException {
    outputProducer.produce(os);
  }

  @Override
  public boolean isStreaming() { return false; } //?

  @Override
  @SuppressWarnings("deprecation")
  public void consumeContent() throws IOException { }

  private static class DirectBufferAccessByteArrayOutputStream extends ByteArrayOutputStream {
    public byte[] getBuffer() { return buf; } // toByteArray incurs extra memory copy, don't need it
  }
}
