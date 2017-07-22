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

/* Created by yegor on 7/27/16. */

package ws.epigraph.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Self<Self> { // TODO This?

  // This can, of course, be intentionally broken (`class Foo implements Self<Bar>`); good job then.
  @Contract(pure = true)
  @SuppressWarnings("unchecked")
  default @NotNull Self self() { return (Self) this; }

}
