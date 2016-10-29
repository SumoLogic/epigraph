package io.epigraph.projections.op.path;

import com.intellij.psi.PsiElement;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.epigraph.projections.ProjectionPsiParserUtil.getTag;
import static io.epigraph.projections.ProjectionPsiParserUtil.parseAnnotation;
import static io.epigraph.projections.op.OpParserUtil.parseParameter;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpPathPsiParser {

  public static OpVarPath parseVarProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpVarPath psi,
      @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    final Type type = dataType.type;

    @Nullable IdlOpModelPath modelProjection = psi.getOpModelPath();

    if (isModelPathEmpty(modelProjection)) {
      return new OpVarPath(
          type,
          null, // no tags = end of path
          EpigraphPsiUtil.getLocation(psi)
      );
    }

    final Type.Tag tag = getTag(
        type,
        psi.getTagName(),
        dataType.defaultTag,
        psi
    );

    @NotNull List<IdlOpModelPathProperty> modelPropertiesPsi =
        psi.getOpModelPathPropertyList();

    final OpModelPath<?, ?> parsedModelProjection = parseModelProjection(
        tag.type,
        parseModelParams(modelPropertiesPsi, typesResolver),
        parseModelAnnotations(modelPropertiesPsi),
        modelProjection,
        typesResolver
    );

    return new OpVarPath(
        type,
        new OpTagPath(
            tag, parsedModelProjection, EpigraphPsiUtil.getLocation(modelProjection)
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static OpParams parseModelParams(
      @NotNull List<IdlOpModelPathProperty> modelProperties,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> paramList = null;

    for (IdlOpModelPathProperty modelProperty : modelProperties) {
      @Nullable IdlOpParam paramPsi = modelProperty.getOpParam();
      if (paramPsi != null) {
        if (paramList == null) paramList = new ArrayList<>(3);
        paramList.add(parseParameter(paramPsi, resolver));
      }
    }

    return paramList == null ? OpParams.EMPTY : new OpParams(paramList);
  }

  @NotNull
  private static Annotations parseModelAnnotations(@NotNull List<IdlOpModelPathProperty> modelProperties)
      throws PsiProcessingException {

    @Nullable Map<String, Annotation> annotationsMap = null;

    for (IdlOpModelPathProperty modelProperty : modelProperties)
      annotationsMap = parseAnnotation(annotationsMap, modelProperty.getAnnotation());

    return annotationsMap == null ? Annotations.EMPTY : new Annotations(annotationsMap);
  }

  @NotNull
  private static OpVarPath createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {
    return new OpVarPath(
        type,
        new OpTagPath(
            tag,
            createDefaultModelProjection(
                tag.type,
                OpParams.EMPTY,
                Annotations.EMPTY,
                locationPsi
            ),
            EpigraphPsiUtil.getLocation(locationPsi)
        ),
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  private static OpVarPath createDefaultVarProjection(
      @NotNull DatumType type,
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, locationPsi);
  }

  @NotNull
  private static OpVarPath createDefaultVarProjection(
      @NotNull DataType type,
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name), locationPsi
      );

    return createDefaultVarProjection(type.type, defaultTag, locationPsi);
  }

  private static boolean isModelPathEmpty(@Nullable IdlOpModelPath pathPsi) {
    return pathPsi == null || (
        pathPsi.getOpRecordModelPath() == null &&
        pathPsi.getOpMapModelPath() == null
    );
  }

  @NotNull
  public static OpModelPath<?, ?> parseModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpModelPath psi,
      @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpRecordModelPath recordModelProjectionPsi = psi.getOpRecordModelPath();
        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi);
        ensureModelKind(psi, TypeKind.RECORD);
        return parseRecordModelProjection(
            (RecordType) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver
        );
      case MAP:
        @Nullable IdlOpMapModelPath mapModelProjectionPsi = psi.getOpMapModelPath();
        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi);
        ensureModelKind(psi, TypeKind.MAP);

        return parseMapModelProjection(
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
        return parsePrimitiveModelProjection(
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

  private static void ensureModelKind(@NotNull IdlOpModelPath psi, @NotNull TypeKind expectedKind)
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
  private static TypeKind findProjectionKind(@NotNull IdlOpModelPath psi) {
    if (psi.getOpRecordModelPath() != null) return TypeKind.RECORD;
    if (psi.getOpMapModelPath() != null) return TypeKind.MAP;
    return null;
  }

  @NotNull
  private static OpModelPath<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpRecordModelPath(
            (RecordType) type,
            params,
            annotations,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case MAP:
        MapType mapType = (MapType) type;

        final OpPathKeyProjection keyProjection =
            new OpPathKeyProjection(
                OpParams.EMPTY,
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

        final OpVarPath valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            locationPsi
        );

        return new OpMapModelPath(
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
        return new OpPrimitiveModelPath(
            (PrimitiveType) type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi);
    }
  }

  @NotNull
  public static OpRecordModelPath parseRecordModelProjection(
      @NotNull RecordType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpRecordModelPath psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    @NotNull final IdlOpFieldPath fieldProjectionPsi = psi.getOpFieldPath();

    final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
    RecordType.Field field = type.fieldsMap().get(fieldName);
    if (field == null)
      throw new PsiProcessingException(
          String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
          fieldProjectionPsi
      );

    @NotNull OpParams fieldParams;
    @NotNull Annotations fieldAnnotations;

    List<OpParam> fieldParamsList = null;
    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
    for (IdlOpFieldPathBodyPart fieldBodyPart : fieldProjectionPsi.getOpFieldPathBodyPartList()) {
      @Nullable IdlOpParam fieldParamPsi = fieldBodyPart.getOpParam();
      if (fieldParamPsi != null) {
        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
        fieldParamsList.add(parseParameter(fieldParamPsi, typesResolver));
      }

      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation());
    }

    fieldParams = fieldParamsList == null ? OpParams.EMPTY : new OpParams(fieldParamsList);
    fieldAnnotations = fieldAnnotationsMap == null ? Annotations.EMPTY : new Annotations(fieldAnnotationsMap);

    OpVarPath varProjection;
    @Nullable IdlOpVarPath psiVarProjection = fieldProjectionPsi.getOpVarPath();
    if (psiVarProjection == null) {
      @NotNull DataType fieldDataType = field.dataType();
      @Nullable Type.Tag defaultFieldTag = fieldDataType.defaultTag;
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
            fieldName,
            fieldDataType.name
        ), fieldProjectionPsi);

      varProjection =
          createDefaultVarProjection(fieldDataType.type, defaultFieldTag, fieldProjectionPsi);
    } else {
      varProjection = parseVarProjection(field.dataType(), psiVarProjection, typesResolver);
    }

    @NotNull final TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldProjectionPsi);

    final OpFieldPathEntry fieldProjection = new OpFieldPathEntry(
        field,
        new OpFieldPath(
            fieldParams,
            fieldAnnotations,
            varProjection,
            fieldLocation
        ),
        fieldLocation
    );

    return new OpRecordModelPath(
        type,
        params,
        annotations,
        fieldProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static OpMapModelPath parseMapModelProjection(
      @NotNull MapType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpMapModelPath psi,
      @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    @NotNull OpPathKeyProjection keyProjection = parseKeyProjection(psi.getOpPathKeyProjection(), resolver);

    @Nullable IdlOpVarPath valueProjectionPsi = psi.getOpVarPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value projection not specified", psi);

    @NotNull OpVarPath valueProjection = parseVarProjection(type.valueType(), valueProjectionPsi, resolver);

    return new OpMapModelPath(
        type,
        params,
        annotations,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static OpPathKeyProjection parseKeyProjection(
      @NotNull IdlOpPathKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> params = null;
    @Nullable Map<String, Annotation> annotationsMap = null;

    @Nullable final IdlOpPathKeyProjectionBody body = keyProjectionPsi.getOpPathKeyProjectionBody();
    if (body != null) {
      for (IdlOpPathKeyProjectionPart keyPart : body.getOpPathKeyProjectionPartList()) {
        @Nullable IdlOpParam paramPsi = keyPart.getOpParam();
        if (paramPsi != null) {
          if (params == null) params = new ArrayList<>(3);
          params.add(parseParameter(paramPsi, resolver));
        }

        annotationsMap = parseAnnotation(annotationsMap, keyPart.getAnnotation());
      }
    }

    return new OpPathKeyProjection(
        OpParams.fromCollection(params),
        Annotations.fromMap(annotationsMap),
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  @NotNull
  public static OpPrimitiveModelPath parsePrimitiveModelProjection(
      @NotNull PrimitiveType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) {

    return new OpPrimitiveModelPath(
        type,
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}