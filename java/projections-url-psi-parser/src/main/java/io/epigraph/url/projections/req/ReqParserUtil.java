package io.epigraph.url.projections.req;

import com.intellij.psi.PsiElement;
import io.epigraph.data.Datum;
import io.epigraph.gdata.GDataToData;
import io.epigraph.gdata.GDatum;
import io.epigraph.lang.Qn;
import io.epigraph.names.QualifiedTypeName;
import io.epigraph.names.TypeName;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.req.ReqParam;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.ImportAwareTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
import io.epigraph.url.gdata.UrlGDataPsiParser;
import io.epigraph.url.parser.psi.UrlDatum;
import io.epigraph.url.parser.psi.UrlReqAnnotation;
import io.epigraph.url.parser.psi.UrlReqParam;
import io.epigraph.url.parser.psi.UrlRequestParam;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.epigraph.url.projections.UrlProjectionsPsiParserUtil.parseAnnotation;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqParserUtil {

  @NotNull
  public static Map<String, GDatum> parseRequestParams(
      @NotNull List<UrlRequestParam> requestParamList,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Map<String, GDatum> requestParams;

    if (!requestParamList.isEmpty()) {
      boolean first = true;
      requestParams = new HashMap<>();

      for (UrlRequestParam requestParamPsi : requestParamList) {
        if (first) {
          @Nullable final PsiElement amp = requestParamPsi.getAmp();
          if (amp != null) errors.add(new PsiProcessingError("'?' expected, got '&'", amp));
          first = false;
        } else {
          @Nullable final PsiElement qmark = requestParamPsi.getQmark();
          if (qmark != null) errors.add(new PsiProcessingError("'&' expected, got '?'", qmark));
        }

        @Nullable final PsiElement paramNamePsi = requestParamPsi.getParamName();
        if (paramNamePsi == null) {
          errors.add(new PsiProcessingError("Missing parameter name", requestParamPsi));
          continue;
        }
        String paramName = paramNamePsi.getText();

        @Nullable final UrlDatum paramValuePsi = requestParamPsi.getDatum();
        if (paramValuePsi == null) {
          errors.add(new PsiProcessingError(String.format("Missing parameter '%s' value", paramName), requestParamPsi));
          continue;
        }

        @NotNull final GDatum paramValue = UrlGDataPsiParser.parseDatum(paramValuePsi);

        requestParams.put(paramName, paramValue);
      }
    } else requestParams = Collections.emptyMap();

    return requestParams;
  }

  @NotNull
  public static Annotations parseAnnotations(@NotNull List<UrlReqAnnotation> annotationsPsi)
      throws PsiProcessingException {
    Map<String, Annotation> paramMap = null;

    for (UrlReqAnnotation annotation : annotationsPsi) {
      paramMap = parseAnnotation(paramMap, annotation.getAnnotation());
    }

    return Annotations.fromMap(paramMap);
  }

  @NotNull
  public static ReqParams parseReqParams(
      @NotNull List<UrlReqParam> reqParamsPsi,
      @Nullable OpParams opParams,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (reqParamsPsi.isEmpty()) return ReqParams.EMPTY;

    if (opParams == null) {
      errors.add(new PsiProcessingError("Parameters are not supported here", reqParamsPsi.iterator().next()));
      return ReqParams.EMPTY;
    }

    Map<String, ReqParam> paramMap = null;

    for (UrlReqParam reqParamPsi : reqParamsPsi)
      paramMap = parseReqParam(paramMap, reqParamPsi, opParams, resolver, errors);

    return ReqParams.fromMap(paramMap);
  }

  @Nullable
  public static Map<String, ReqParam> parseReqParam(
      @Nullable Map<String, ReqParam> reqParamsMap,
      @Nullable UrlReqParam reqParamPsi,
      @NotNull OpParams opParams,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (reqParamPsi != null) {
      if (reqParamsMap == null) reqParamsMap = new HashMap<>();

      String name = reqParamPsi.getQid().getCanonicalName();
      OpParam opParam = opParams.params().get(name);

      if (opParam == null) {
        errors.add(new PsiProcessingError(
            String.format(
                "Unsupported parameter '%s', supported parameters: {%s}",
                name,
                String.join(", ", opParams.params().keySet())
            ),
            reqParamPsi.getQid()
        ));
        return reqParamsMap;
      }

      final String errorMsgPrefix = String.format("Error processing parameter '%s' value: ", name);
      OpInputModelProjection<?, ?, ?> projection = opParam.projection();
      final DatumType model = projection.model();
      @NotNull final TypesResolver subResolver = addTypeNamespace(model, resolver);

      @Nullable Datum value = getDatum(reqParamPsi.getDatum(), model, subResolver, errorMsgPrefix, errors);
      if (value == null) value = projection.defaultValue();

      // todo validate value against input projection

      reqParamsMap.put(name, new ReqParam(name, value, EpigraphPsiUtil.getLocation(reqParamPsi)));
    }
    return reqParamsMap;
  }

  @Nullable
  public static Datum getDatum(
      @NotNull UrlDatum datumPsi,
      @NotNull DatumType model,
      @NotNull TypesResolver resolver,
      @NotNull String errorMessagePrefix,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    @NotNull GDatum gDatum = UrlGDataPsiParser.parseDatum(datumPsi);
    @Nullable Datum value;

    try {
      value = GDataToData.transform(model, gDatum, resolver).getDatum();
    } catch (GDataToData.ProcessingException e) {
      // try to find element by offset
      int offset = e.location().startOffset() - datumPsi.getTextRange().getStartOffset();
      PsiElement element = datumPsi.findElementAt(offset);
      if (element == null) element = datumPsi;

      errors.add(new PsiProcessingError(errorMessagePrefix + e.getMessage(), element));
      return null;
    }
    return value;
  }

  @NotNull
  public static TypesResolver addTypeNamespace(@NotNull Type type, @NotNull TypesResolver resolver) {
    @Nullable final Qn namespace = getTypeNamespace(type);

    if (namespace == null) return resolver;
    else {

      TypesResolver child = resolver;

      if (child instanceof ImportAwareTypesResolver)
        child = ((ImportAwareTypesResolver) child).childResolver();

      return new ImportAwareTypesResolver(namespace, Collections.emptyList(), child);
    }
  }

  @Nullable
  public static Qn getTypeNamespace(@NotNull Type type) {
    @NotNull final TypeName name = type.name();

    if (name instanceof QualifiedTypeName) {
      QualifiedTypeName qualifiedTypeName = (QualifiedTypeName) name;
      return qualifiedTypeName.toFqn().removeLastSegment();
    }

    return null;
  }

}
