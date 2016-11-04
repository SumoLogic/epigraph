package ws.epigraph.projections.op;

import ws.epigraph.gdata.GDatum;
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.gdata.IdlGDataPsiParser;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.input.OpInputModelProjection;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static ws.epigraph.projections.ProjectionPsiParserUtil.parseAnnotation;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpParserUtil {
  @NotNull
  public static OpParam parseParameter(
      @NotNull IdlOpParam paramPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {
    @Nullable IdlQid qid = paramPsi.getQid();
    if (qid == null) throw new PsiProcessingException("Parameter name not specified", paramPsi);
    @NotNull String paramName = qid.getCanonicalName();

    @Nullable IdlTypeRef typeRef = paramPsi.getTypeRef();
    if (typeRef == null)
      throw new PsiProcessingException(String.format("Parameter '%s' type not specified", paramName), paramPsi);
    @NotNull TypeRef paramTypeRef = TypeRefs.fromPsi(typeRef);
    @Nullable DatumType paramType = paramTypeRef.resolveDatumType(resolver);

    if (paramType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve parameter '%s' data type '%s'", paramName, paramTypeRef), paramPsi
      );

    @Nullable IdlOpInputModelProjection paramModelProjectionPsi = paramPsi.getOpInputModelProjection();

    @Nullable Map<String, Annotation> annotationMap = null;
    for (IdlAnnotation annotationPsi : paramPsi.getAnnotationList())
      annotationMap = parseAnnotation(annotationMap, annotationPsi);
    @NotNull Annotations annotations = annotationMap == null ? Annotations.EMPTY : new Annotations(annotationMap);

    @Nullable IdlDatum defaultValuePsi = paramPsi.getDatum();
    @Nullable GDatum defaultValue = defaultValuePsi == null
                                    ? null
                                    : IdlGDataPsiParser.parseDatum(defaultValuePsi);

    final OpInputModelProjection<?, ?, ?> paramModelProjection;

    if (paramModelProjectionPsi != null)
      paramModelProjection = OpInputProjectionsPsiParser.parseModelProjection(
          paramType,
          paramPsi.getPlus() != null,
          defaultValue,
          annotations,
          null, // TODO do we want to support metadata on parameters?
          paramModelProjectionPsi,
          resolver
      ).projection();
    else
      paramModelProjection = OpInputProjectionsPsiParser.createDefaultModelProjection(
          paramType,
          paramPsi.getPlus() != null,
          defaultValue,
          annotations,
          paramPsi,
          resolver
      );

    return new OpParam(paramName, paramModelProjection, EpigraphPsiUtil.getLocation(paramPsi));
  }
}
