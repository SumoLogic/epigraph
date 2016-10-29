package io.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import io.epigraph.data.Datum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.op.path.*;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.projections.req.path.*;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.*;
import io.epigraph.url.parser.psi.*;
import io.epigraph.url.projections.req.ReqParserUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

import static io.epigraph.url.projections.UrlProjectionsPsiParserUtil.getTag;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqPathPsiParser {

  public static ReqVarPath parseVarPath(
      @NotNull OpVarPath op,
      @NotNull DataType dataType,
      @NotNull UrlReqVarPath psi,
      @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    final Type type = dataType.type;

    @Nullable final UrlTagName tagNamePsi = psi.getTagName();

    @Nullable final OpTagPath opTagPath = op.pathTagProjection();

    if (opTagPath == null) {
      if (tagNamePsi != null)
        throw new PsiProcessingException(
            String.format("'%s' tags are not supported by the operation", dataType.name),
            tagNamePsi
        );

      return new ReqVarPath(
          type,
          null, // no tags = end of path
          EpigraphPsiUtil.getLocation(psi)
      );
    }

    @Nullable UrlReqModelPath modelPathPsi = psi.getReqModelPath();

    if (tagNamePsi == null && type.kind() == TypeKind.UNION) {
      return new ReqVarPath(
          type,
          null, // no tags = end of path
          EpigraphPsiUtil.getLocation(psi)
      );
    }

    final Type.@NotNull Tag reqTag = getTag(type, tagNamePsi, dataType.defaultTag, psi);
    final Type.Tag opTag = opTagPath.tag();

    if (!reqTag.equals(opTag)) {
      throw new PsiProcessingException(
          String.format("'%s' tag is not supported by the operation, did you mean '%s'?", reqTag.name(), opTag.name()),
          psi
      );
    }

    final OpModelPath<?, ?> opModelPath = opTagPath.projection();


    final ReqModelPath<?, ?> parsedModelProjection = parseModelPath(
        opModelPath,
        opTag.type,
        ReqParserUtil.parseReqParams(psi.getReqParamList(), opModelPath.params(), typesResolver),
        ReqParserUtil.parseAnnotations(psi.getReqAnnotationList()),
        modelPathPsi,
        typesResolver
    );

    return new ReqVarPath(
        type,
        new ReqTagPath(
            opTag, parsedModelProjection, EpigraphPsiUtil.getLocation(modelPathPsi)
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static boolean isModelPathEmpty(@Nullable UrlReqModelPath pathPsi) {
    return pathPsi == null || (
        pathPsi.getReqRecordModelPath() == null &&
        pathPsi.getReqMapModelPath() == null
    );
  }

  @NotNull
  public static ReqModelPath<?, ?> parseModelPath(
      @NotNull OpModelPath<?, ?> op,
      @NotNull DatumType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqModelPath psi,
      @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable UrlReqRecordModelPath recordModelProjectionPsi = psi.getReqRecordModelPath();
        if (recordModelProjectionPsi == null)
          throw new PsiProcessingException("Record path must be specified", psi);

        ensureModelKind(psi, TypeKind.RECORD);
        return parseRecordModelPath(
            (OpRecordModelPath) op,
            (RecordType) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver
        );
      case MAP:
        @Nullable UrlReqMapModelPath mapModelProjectionPsi = psi.getReqMapModelPath();
        if (mapModelProjectionPsi == null)
          throw new PsiProcessingException("Map path must be specified", psi);

        ensureModelKind(psi, TypeKind.MAP);

        return parseMapModelPath(
            (OpMapModelPath) op,
            (MapType) type,
            params,
            annotations,
            mapModelProjectionPsi,
            typesResolver
        );
      case LIST:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case PRIMITIVE:
        return parsePrimitiveModelPath(
            (PrimitiveType) type,
            params,
            annotations,
            psi
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull UrlReqModelPath psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull UrlReqModelPath psi) {
    if (psi.getReqRecordModelPath() != null) return TypeKind.RECORD;
    if (psi.getReqMapModelPath() != null) return TypeKind.MAP;
    return null;
  }

  /*
  @NotNull
  private static ReqModelPath<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new ReqRecordModelPath(
            (RecordType) type,
            params,
            annotations,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case MAP:
        MapType mapType = (MapType) type;

        final ReqPathKeyProjection keyProjection =
            new ReqPathKeyProjection(
                ReqParams.EMPTY,
                Annotations.EMPTY,
                EpigraphPsiUtil.getLocation(locationPsi)
            );

        @NotNull DataType valueType = mapType.valueType();
        Type.@Nullable Tag defaultValuesTag = valueType.defaultTag;

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a default tag",
              type.name(),
              valueType.name
          ), locationPsi);

        final ReqVarPath valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            locationPsi
        );

        return new ReqMapModelPath(
            mapType,
            params,
            annotations,
            keyProjection,
            valueVarProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case LIST:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi);
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), locationPsi);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi);
      case PRIMITIVE:
        return new ReqPrimitiveModelPath(
            (PrimitiveType) type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi);
    }
  }
  */

  @NotNull
  public static ReqRecordModelPath parseRecordModelPath(
      @NotNull OpRecordModelPath op,
      @NotNull RecordType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqRecordModelPath psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    @NotNull final UrlReqFieldPath fieldProjectionPsi = psi.getReqFieldPath();

    final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();

    final OpFieldPathEntry opFieldEntry = op.fieldProjections().get(fieldName);

    if (opFieldEntry == null)
      throw new PsiProcessingException(
          String.format(
              "Field '%s' is not supported by operation, supported fields: {%s}",
              fieldName,
              ProjectionUtils.listFields(op.fieldProjections().keySet())
          ),
          fieldProjectionPsi.getQid()
      );

    RecordType.Field field = opFieldEntry.field();
    @NotNull final OpFieldPath opField = opFieldEntry.projection();

    @NotNull ReqParams fieldParams =
        ReqParserUtil.parseReqParams(fieldProjectionPsi.getReqParamList(), opField.params(), typesResolver);
    @NotNull Annotations fieldAnnotations = ReqParserUtil.parseAnnotations(fieldProjectionPsi.getReqAnnotationList());

    ReqVarPath varProjection;
    @Nullable UrlReqVarPath fieldVarPathPsi = fieldProjectionPsi.getReqVarPath();
    if (fieldVarPathPsi == null) {
      @NotNull DataType fieldDataType = field.dataType();

      @Nullable Type.Tag defaultFieldTag = fieldDataType.defaultTag;
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
            fieldName,
            fieldDataType.name
        ), fieldProjectionPsi);

      varProjection = new ReqVarPath(fieldDataType.type, null, EpigraphPsiUtil.getLocation(fieldProjectionPsi));
    } else {
      varProjection = parseVarPath(opField.projection(), field.dataType(), fieldVarPathPsi, typesResolver);
    }

    @NotNull final TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldProjectionPsi);

    final ReqFieldPathEntry fieldProjection = new ReqFieldPathEntry(
        field,
        new ReqFieldPath(
            fieldParams,
            fieldAnnotations,
            varProjection,
            fieldLocation
        ),
        fieldLocation
    );

    return new ReqRecordModelPath(
        type,
        params,
        annotations,
        fieldProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqMapModelPath parseMapModelPath(
      @NotNull OpMapModelPath op,
      @NotNull MapType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqMapModelPath psi,
      @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    @NotNull ReqPathKeyProjection keyProjection = parseKeyProjection(
        op.keyProjection(),
        op.model().keyType(),
        psi,
        resolver
    );

    @Nullable UrlReqVarPath valueProjectionPsi = psi.getReqVarPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value projection not specified", psi);

    @NotNull ReqVarPath valueProjection =
        parseVarPath(op.itemsProjection(), type.valueType(), valueProjectionPsi, resolver);

    return new ReqMapModelPath(
        type,
        params,
        annotations,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static ReqPathKeyProjection parseKeyProjection(
      @NotNull OpPathKeyProjection op,
      @NotNull DatumType keyType,
      @NotNull UrlReqMapModelPath mapPathPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    @NotNull final ReqParams reqParams =
        ReqParserUtil.parseReqParams(mapPathPsi.getReqParamList(), op.params(), resolver);

    @NotNull final Annotations annotations = ReqParserUtil.parseAnnotations(mapPathPsi.getReqAnnotationList());

    @Nullable final Datum keyValue =
        ReqParserUtil.getDatum(mapPathPsi.getDatum(), keyType, resolver, "Error processing map key: ");

    if (keyValue == null) throw new PsiProcessingException("Null path keys not allowed", mapPathPsi.getDatum());

    return new ReqPathKeyProjection(
        keyValue,
        reqParams,
        annotations,
        EpigraphPsiUtil.getLocation(mapPathPsi)
    );
  }

  @NotNull
  public static ReqPrimitiveModelPath parsePrimitiveModelPath(
      @NotNull PrimitiveType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) {

    return new ReqPrimitiveModelPath(
        type,
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
