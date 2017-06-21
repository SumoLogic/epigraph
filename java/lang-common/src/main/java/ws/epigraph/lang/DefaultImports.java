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

package ws.epigraph.lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class DefaultImports {
  public static final Qn[] DEFAULT_IMPORTS = {
      new Qn("epigraph", "String"),
      new Qn("epigraph", "Integer"),
      new Qn("epigraph", "Long"),
      new Qn("epigraph", "Double"),
      new Qn("epigraph", "Boolean")
  };

  public static final List<? extends Qn> DEFAULT_IMPORTS_LIST = Collections.unmodifiableList(Arrays.asList(
      DEFAULT_IMPORTS
  ));

  private DefaultImports() {}

}
