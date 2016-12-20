package ws.epigraph.java

import java.nio.file.{Path, Paths}

import ws.epigraph.compiler.CType
import ws.epigraph.gen.Constants
import ws.epigraph.java.NewlineStringInterpolator.NewlineHelper

import scala.collection.JavaConverters._

/**
 * @author yegor 2016-12-15.
 */
class IndexGen(ctx: GenContext) extends JavaGen[Iterable[CType]](ctx) {

    private val IndexClassName: String = Constants.TypesIndex.className

    override protected def relativeFilePath: Path =
      Paths.get(s"${Constants.TypesIndex.namespace.replaceAll("\\.", "/")}/$IndexClassName.java")

    override protected def generate: String = /*@formatter:off*/sn"""\
package epigraph.java;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.Type;
import ws.epigraph.util.Unmodifiable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class $IndexClassName {

  public static final @NotNull Map<@NotNull String, @NotNull ? extends Type> types = types();

  private $IndexClassName() {}

  private static @NotNull Map<@NotNull String, @NotNull ? extends Type> types() {
    Map<@NotNull String, @NotNull Type> types = new LinkedHashMap<>();

${ctx.generatedTypes.asScala.toSeq./*TODO better*/sortWith((a, b) => a._1.name < b._1.name).map { entry => sn"""\
    types.put("${entry._1.name}", ${entry._2});
"""
  }.mkString
}\

    return Unmodifiable.map(types);
  }

}
"""/*@formatter:on*/

}
