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

package ws.epigraph.java

import ws.epigraph.lang.Qn

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
trait Fragments extends JavaGen {

  protected val frag: Fragments.type = Fragments

  protected def interpolate(ns: Qn, f: Fragment): String = f.interpolate(
    Fragment.javaNamespacesInScope + ns,
    Fragment.javaImportsGenerator
  )

  object Fragments {
    val notNull: Fragment =
      if (ctx.java8Annotations) Fragment("@") + Fragment.imp("org.jetbrains.annotations.NotNull") + Fragment(" ")
      else Fragment.empty

    val nullable: Fragment =
      if (ctx.java8Annotations) Fragment("@") + Fragment.imp("org.jetbrains.annotations.NotNull") + Fragment(" ")
      else Fragment.empty

    val func: Fragment = Fragment.imp("java.util.function.Function")
    val asm: Fragment = Fragment.imp("ws.epigraph.assembly.Asm")
    val asmCtx: Fragment = Fragment.imp("ws.epigraph.assembly.AsmContext")
    val _type: Fragment = Fragment.imp("ws.epigraph.types.Type")
    val errValue: Fragment = Fragment.imp("ws.epigraph.errors.ErrorValue")
    val assemblerContext: Fragment = Fragment.imp("ws.epigraph.assembly.AsmContext")
  }

}
