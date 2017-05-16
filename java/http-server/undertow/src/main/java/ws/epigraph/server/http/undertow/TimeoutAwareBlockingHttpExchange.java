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

package ws.epigraph.server.http.undertow;

import io.undertow.io.*;
import io.undertow.server.BlockingHttpExchange;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class TimeoutAwareBlockingHttpExchange implements BlockingHttpExchange {
  private final long timeout;
  private final TimeUnit timeoutTimeUnit;

  private InputStream inputStream;
  private OutputStream outputStream;
  private Sender sender;
  private final HttpServerExchange exchange;

  TimeoutAwareBlockingHttpExchange(
      final HttpServerExchange exchange,
      final long timeout,
      final TimeUnit unit) {

    this.timeout = timeout;
    timeoutTimeUnit = unit;
    this.exchange = exchange;
  }

  @Override
  public InputStream getInputStream() {
    if (inputStream == null) {
      inputStream = new UndertowTimeoutAwareInputStream(exchange, timeout, timeoutTimeUnit);
    }
    return inputStream;
  }

  @Override
  public OutputStream getOutputStream() {
    if (outputStream == null) {
      outputStream = new UndertowOutputStream(exchange);
    }
    return outputStream;
  }

  @Override
  public Sender getSender() {
    if (sender == null) {
      sender = new BlockingSenderImpl(exchange, getOutputStream());
    }
    return sender;
  }

  @Override
  public void close() throws IOException {
    try {
      getInputStream().close();
    } finally {
      getOutputStream().close();
    }
  }

  @Override
  public Receiver getReceiver() {
    return new BlockingReceiverImpl(exchange, getInputStream());
  }
}
