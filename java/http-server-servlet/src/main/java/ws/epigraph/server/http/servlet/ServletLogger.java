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

package ws.epigraph.server.http.servlet;

import org.jetbrains.annotations.NotNull;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.SubstituteLogger;

import javax.servlet.ServletContext;
import java.util.Queue;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ServletLogger extends SubstituteLogger {
  private final @NotNull ServletContext servletContext;

  public ServletLogger(
      final String name,
      final Queue<SubstituteLoggingEvent> eventQueue,
      final boolean createdPostInitialization,
      final @NotNull ServletContext context) {
    super(name, eventQueue, createdPostInitialization);
    servletContext = context;
  }

  @Override
  public void error(final String msg) {
    super.error(msg);
    servletContext.log("[ERROR] " + msg);
  }

  @Override
  public void error(final String msg, final Throwable t) {
    super.error(msg, t);
    servletContext.log("[ERROR] " + msg, t);
  }

  @Override
  public void info(final String msg) {
    super.info(msg);
    servletContext.log("[INFO] " + msg);
  }

  @Override
  public void info(final String msg, final Throwable t) {
    super.info(msg, t);
    servletContext.log("[INFO] " + msg, t);
  }

  @Override
  public void warn(final String msg) {
    super.warn(msg);
    servletContext.log("[WARN] " + msg);
  }

  @Override
  public void warn(final String msg, final Throwable t) {
    super.warn(msg, t);
    servletContext.log("[WARN] " + msg, t);
  }
}
