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

package ws.epigraph.ideaplugin.schema.formatting;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.alignment.AlignmentStrategy;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class CompositeAlignmentStrategy extends AlignmentStrategy {
  private final AlignmentStrategy[] strategies;

  public CompositeAlignmentStrategy(AlignmentStrategy... strategies) {
    this.strategies = strategies;
  }

  @Nullable
  @Override
  public Alignment getAlignment(@Nullable IElementType parentType, @Nullable IElementType childType) {
    for (AlignmentStrategy strategy : strategies) {
      Alignment a = strategy.getAlignment(parentType, childType);
      if (a != null) {
        return a;
      }
    }
    return null;
  }
}
