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

package ws.epigraph.gen;

/**
 * Constants used by Java code generator.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface Constants {

  int indent = 2;

  /** Constants related to generated types index artifacts. */
  interface TypesIndex {

    /** Relative path to generated types index resource file(s). */
    String resourcePath = "epigraph/index/typesIndex.properties"; // TODO put under META-INF/?

    /** @deprecated Java index is no longer used */
    @Deprecated
    String namespace = "epigraph.index";

    /** @deprecated Java index is no longer used */
    @Deprecated
    String className = "TypesIndex";

  }

}
