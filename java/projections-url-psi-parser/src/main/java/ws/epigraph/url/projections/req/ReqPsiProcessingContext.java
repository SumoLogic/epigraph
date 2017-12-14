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

package ws.epigraph.url.projections.req;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.projections.op.OpProjection;
import ws.epigraph.psi.DelegatingPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPsiProcessingContext extends DelegatingPsiProcessingContext {

  private final @NotNull ReqReferenceContext entityReferenceContext;
  private final @NotNull Map<String, Queue<OpProjection<?, ?>>> verifiedRefOps = new HashMap<>();
  private final @NotNull Map<String, Queue<OpProjection<?, ?>>> unverifiedRefOps = new HashMap<>();

  public ReqPsiProcessingContext(
      final @NotNull PsiProcessingContext delegate,
      final @NotNull ReqReferenceContext entityReferenceContext) {

    super(delegate);
    this.entityReferenceContext = entityReferenceContext;
  }

  public @NotNull ReqReferenceContext referenceContext() { return entityReferenceContext; }

  public @Nullable Queue<OpProjection<?, ?>> verifiedRefOps(@NotNull String name) {
    return verifiedRefOps.get(name);
  }

  public void addVerifiedRefOp(@NotNull String name, OpProjection<?, ?> op) {
    Queue<OpProjection<?, ?>> ops = verifiedRefOps(name);
    if (ops == null) {
      ops = new ArrayDeque<>();
      verifiedRefOps.put(name, ops);
    }
    ops.add(op);
  }

  public @Nullable Queue<OpProjection<?, ?>> unverifiedRefOps(@NotNull String name) {
    return unverifiedRefOps.get(name);
  }

  public void addUnverifiedRefOp(@NotNull String name, OpProjection<?, ?> op) {
    Queue<OpProjection<?, ?>> ops = unverifiedRefOps(name);
    if (ops == null) {
      ops = new ArrayDeque<>();
      unverifiedRefOps.put(name, ops);
    }
    ops.add(op);
  }
}
