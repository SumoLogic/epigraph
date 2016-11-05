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

package ws.epigraph.projections.req.update;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqUpdateKeysProjection {
  public static final ReqUpdateKeysProjection UPDATE_KEYS = new ReqUpdateKeysProjection(true);
  public static final ReqUpdateKeysProjection REPLACE_KEYS = new ReqUpdateKeysProjection(false);

  private final boolean update;

  public ReqUpdateKeysProjection(boolean update) {this.update = update;}

  /**
   * @return {@code true} if map entries must be updated (replaced), {@code false} if they must be patched
   */
  public boolean update() { return update; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReqUpdateKeysProjection that = (ReqUpdateKeysProjection) o;
    return update == that.update;
  }

  @Override
  public int hashCode() {
    return Objects.hash(update);
  }
}
