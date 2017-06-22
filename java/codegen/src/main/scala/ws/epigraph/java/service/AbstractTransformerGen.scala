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

package ws.epigraph.java.service

import java.nio.file.Path

import ws.epigraph.compiler.CTypeKind
import ws.epigraph.java.JavaGenNames.{lqdrn2, lqn2}
import ws.epigraph.java.JavaGenUtils.up
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper
import ws.epigraph.java.service.projections.req.BaseNamespaceProvider
import ws.epigraph.java.service.projections.req.output.{ReqOutputProjectionGen, ReqOutputVarProjectionGen}
import ws.epigraph.java._
import ws.epigraph.lang.Qn
import ws.epigraph.schema.TransformerDeclaration

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
class AbstractTransformerGen(td: TransformerDeclaration, baseNamespace: Qn, val ctx: GenContext) extends JavaGen {
  protected val namespace: Qn = AbstractTransformerGen.abstractTransformerNamespace(baseNamespace, td)

  protected val shortClassName: String = AbstractTransformerGen.abstractTransformerClassName(td)

  override protected def relativeFilePath: Path = JavaGenUtils.fqnToPath(namespace).resolve(shortClassName + ".java")

  protected val outputProjectionGen: ReqOutputProjectionGen = ReqOutputVarProjectionGen.dataProjectionGen(
    new BaseNamespaceProvider {override def baseNamespace: Qn = namespace },
    td.outputProjection(),
    None,
    Qn.EMPTY,
    ctx
  )

  override def children: Iterable[JavaGen] = super.children ++ Iterable(outputProjectionGen)

  override protected def generate: String = {
    val sctx = new ObjectGenContext(ctx)

    sctx.addImport("org.jetbrains.annotations.NotNull")
    sctx.addImport("java.util.concurrent.CompletableFuture")
    sctx.addImport("ws.epigraph.invocation.InvocationResult")
    sctx.addImport("ws.epigraph.projections.req.output.ReqOutputVarProjection")
    sctx.addImport("ws.epigraph.data.Data")
    sctx.addImport(outputProjectionGen.fullClassName)

    // todo maxBatchSize
    val batching: Boolean = td.annotations().annotation(Qn.fromDotSeparated("ws.epigraph.annotations.Batching")) != null

    val cType = JavaGenUtils.toCType(td.`type`())
    val shortDataType = sctx.addImport(lqdrn2(cType, namespace.toString), namespace)
    val shortType = sctx.addImport(lqn2(cType, namespace.toString), namespace)

    val inputArg = if (cType.kind == CTypeKind.ENTITY) s"($shortType)input" else s"(($shortDataType)input).get()"
    val outputProjectionArg = if (cType.kind == CTypeKind.ENTITY) "outputProjection" else "outputProjection.singleTagProjection().projection()"
    val thenFunc = if (cType.kind == CTypeKind.ENTITY) "InvocationResult::success" else
      s"d -> InvocationResult.success($shortType.type.createDataBuilder().set(d))"

    def generateBatching(sctx: ObjectGenContext): String = {
      sctx.addImport("ws.epigraph.federator.transformers.Transformer")

      /*@formatter:off*/sn"""\
public abstract class $shortClassName<B> extends Transformer<B> {
  protected $shortClassName() {
    super(${TransformerDeclarationGen.transformerDeclarationNamespace(baseNamespace, td)}.${TransformerDeclarationGen.transformerDeclarationClassName(td)}.INSTANCE);
  }

  /**
   * Runs transformation or schedules it for batched execution
   *
   * @param input            input data
   * @param outputProjection output projection
   * @param batch            batching context
   *
   * @return output data result future
   */
  protected abstract @NotNull CompletableFuture<$shortType> transform(
      @NotNull $shortType input,
      @NotNull ${outputProjectionGen.shortClassName} outputProjection,
      @NotNull B batch);


  @Override
  public @NotNull CompletableFuture<InvocationResult<Data>> transform(
      @NotNull Data input,
      @NotNull ReqOutputVarProjection outputProjection,
      @NotNull B batch) {

    return transform($inputArg, new ${outputProjectionGen.shortClassName}($outputProjectionArg), batch)
        .thenApply($thenFunc);
  }
}"""/*@formatter:on*/
    }

    def generateNonBatching(sctx: ObjectGenContext): String ={
      sctx.addImport("ws.epigraph.federator.transformers.NonBatchingTransformer")

      /*@formatter:off*/sn"""\
public abstract class $shortClassName<B> extends NonBatchingTransformer {
  protected $shortClassName() {
    super(${TransformerDeclarationGen.transformerDeclarationNamespace(baseNamespace, td)}.${TransformerDeclarationGen.transformerDeclarationClassName(td)}.INSTANCE);
  }

  /**
   * Runs transformation
   *
   * @param input            input data
   * @param outputProjection output projection
   *
   * @return output data result future
   */
  protected abstract @NotNull CompletableFuture<$shortType> transform(
      @NotNull $shortType input,
      @NotNull ${outputProjectionGen.shortClassName} outputProjection);


  @Override
  public @NotNull CompletableFuture<InvocationResult<Data>> transform(
      @NotNull Data input,
      @NotNull ReqOutputVarProjection outputProjection) {

    return transform($inputArg, new ${outputProjectionGen.shortClassName}($outputProjectionArg))
        .thenApply($thenFunc);
  }
}"""/*@formatter:on*/
    }

    val body = if (batching) generateBatching(sctx) else generateNonBatching(sctx)

    /*@formatter:off*/sn"""\
${JavaGenUtils.topLevelComment}
package $namespace;

${ObjectGenUtils.genImports(sctx)}
/**
 * Abstract base class for {@code ${td.name()}} transformer
 */
$body
"""/*@formatter:on*/
  }

}

object AbstractTransformerGen {
  def abstractTransformerNamespace(baseNamespace: Qn, td: TransformerDeclaration): Qn =
    ServiceNames.transformerNamespace(baseNamespace, td.name())

  def abstractTransformerClassName(td: TransformerDeclaration): String = s"Abstract${ up(td.name()) }Transformer"
}
