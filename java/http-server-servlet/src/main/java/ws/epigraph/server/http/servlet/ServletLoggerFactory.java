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

import org.slf4j.Logger;
import org.slf4j.event.SubstituteLoggingEvent;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @deprecated
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ServletLoggerFactory {

  boolean postInitialization = false;

  final Map<String, ServletLogger> loggers = new HashMap<>();

  final LinkedBlockingQueue<SubstituteLoggingEvent> eventQueue = new LinkedBlockingQueue<>();

  public synchronized Logger getLogger(String name, ServletContext servletContext) {
    return loggers.computeIfAbsent(name, n -> new ServletLogger(n, eventQueue, postInitialization, servletContext));
  }

  public List<String> getLoggerNames() { return new ArrayList<>(loggers.keySet()); }

  public List<ServletLogger> getLoggers() { return new ArrayList<>(loggers.values()); }

  public LinkedBlockingQueue<SubstituteLoggingEvent> getEventQueue() { return eventQueue; }

  public void postInitialization() { postInitialization = true; }

  public void clear() {
    loggers.clear();
    eventQueue.clear();
  }
}
