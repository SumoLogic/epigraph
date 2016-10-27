package io.epigraph.projections.op;

import io.epigraph.gdata.GDatum;
import io.epigraph.idl.TypeRefs;
import io.epigraph.idl.gdata.IdlGDataPsiParser;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static io.epigraph.projections.ProjectionPsiParserUtil.parseAnnotation;

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
    if (paramModelProjectionPsi == null) // can this ever happen?
      throw new PsiProcessingException(String.format("Parameter '%s' projection", paramName), paramPsi);

    @Nullable Map<String, Annotation> annotationMap = null;
    for (IdlAnnotation annotationPsi : paramPsi.getAnnotationList())
      annotationMap = parseAnnotation(annotationMap, annotationPsi);
    @NotNull Annotations annotations = annotationMap == null ? Annotations.EMPTY : new Annotations(annotationMap);

    @Nullable IdlDatum defaultValuePsi = paramPsi.getDatum();
    @Nullable GDatum defaultValue = defaultValuePsi == null
                                    ? null
                                    : IdlGDataPsiParser.parseDatum(defaultValuePsi);

    OpInputModelProjection<?, ?, ?> paramModelProjection = OpInputProjectionsPsiParser.parseModelProjection(
        paramType,
        paramPsi.getPlus() != null,
        defaultValue,
        annotations,
        null, // TODO do we want to support metadata on parameters?
        paramModelProjectionPsi,
        resolver
    ).projection();

    return new OpParam(paramName, paramModelProjection, EpigraphPsiUtil.getLocation(paramPsi));
  }
}
