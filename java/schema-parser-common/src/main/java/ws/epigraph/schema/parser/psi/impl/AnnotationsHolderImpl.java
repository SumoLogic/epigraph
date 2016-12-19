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

package ws.epigraph.schema.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import ws.epigraph.schema.parser.psi.AnnotationsHolder;
import ws.epigraph.schema.parser.psi.SchemaAnnotation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class AnnotationsHolderImpl extends ASTWrapperPsiElement implements AnnotationsHolder {
  public AnnotationsHolderImpl(@NotNull ASTNode node) {
    super(node);
  }

  @NotNull
  @Override
  public List<SchemaAnnotation> getAnnotationList() {
    throw new RuntimeException("Should never happen: " + getClass().getName());
  }
}
