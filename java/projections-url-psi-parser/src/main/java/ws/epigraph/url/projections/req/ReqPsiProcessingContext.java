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
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.psi.DelegatingPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;

import java.util.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public abstract class ReqPsiProcessingContext<
    OVP extends GenVarProjection<OVP, ?, ?>,
    VRC extends ReferenceContext<?, ?>
    > extends DelegatingPsiProcessingContext {

  private final @NotNull VRC varReferenceContext;
  private final @NotNull Map<String, Queue<OVP>> verifiedRefOps = new HashMap<>();
  private final @NotNull Map<String, Queue<OVP>> unverifiedRefOps = new HashMap<>();

  protected ReqPsiProcessingContext(
      final @NotNull PsiProcessingContext delegate,
      final @NotNull VRC context) {
    super(delegate);
    varReferenceContext = context;
  }

  public @NotNull VRC varReferenceContext() { return varReferenceContext; }

  public @Nullable Queue<OVP> verifiedRefOps(@NotNull String name) {
    return verifiedRefOps.get(name);
  }

  public void addVerifiedRefOp(@NotNull String name, OVP op) {
    Queue<OVP> ops = verifiedRefOps(name);
    if (ops == null) {
      ops = new ArrayDeque<>();
      verifiedRefOps.put(name, ops);
    }
    ops.add(op);
  }

  public @Nullable Queue<OVP> unverifiedRefOps(@NotNull String name) {
    return unverifiedRefOps.get(name);
  }
  
  public void addUnverifiedRefOp(@NotNull String name, OVP op) {
    Queue<OVP> ops = unverifiedRefOps(name);
    if (ops == null) {
      ops = new ArrayDeque<>();
      unverifiedRefOps.put(name, ops);
    }
    ops.add(op);
  }
}
