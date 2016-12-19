/*
 * Copyright 2016 Sumo Logic
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

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface Keywords {
  Keywords schema = new Schema();

  boolean isKeyword(@NotNull String s);

  default @NotNull String escape(@NotNull String s) {
    return isKeyword(s) ? "`" + s + "`" : s;
  }

  final class Schema implements Keywords {
    private static final Set<String> keywords = new HashSet<>(
        Arrays.asList(
            "namespace",
            "import",
            "map",
            "default",
            "nodefault",
            "list",
            "record",
            "extends",
            "vartype",
            "enum",
            "meta",
            "supplement",
            "supplements",
            "with",
            "abstract",
            "override",
            "integer",
            "integer",
            "long",
            "double",
            "boolean",
            "string",
            "forbidden",
            "required",
            "default",
            "resource",
            "GET",
            "POST",
            "PUT",
            "DELETE",
            "READ",
            "CREATE",
            "UPDATE",
            "CUSTOM",
            "method",
            "inputType",
            "inputProjection",
            "outputType",
            "outputProjection",
            "deleteProjection",
            "path"
        )
    );

    @Override
    public boolean isKeyword(final @NotNull String s) {
      return keywords.contains(s);
    }
  }
}
