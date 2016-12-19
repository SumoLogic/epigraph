/*
 * Copyright 2016 Sumo Logic
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

package ws.epigraph.projections.op.path;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.TypeKind;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ws.epigraph.projections.EdlProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpPathPsiParser {

  private OpPathPsiParser() {}

  public static OpVarPath parseVarPath(
      @NotNull DataType dataType,
      @NotNull EdlOpVarPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final Type type = dataType.type;

    @Nullable EdlOpModelPath modelProjection = psi.getOpModelPath();

    if (isModelPathEmpty(modelProjection)) {
      if (psi.getTagName() != null)
        throw new PsiProcessingException("Path can't end with a tag", psi.getTagName(), errors);

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
        psi,
        errors
    );

    @NotNull Collection<EdlOpModelPathProperty> modelPropertiesPsi = psi.getOpModelPathPropertyList();

    final OpModelPath<?, ?> parsedModelProjection = parseModelPath(
        tag.type,
        parseModelParams(modelPropertiesPsi, typesResolver, errors),
        parseModelAnnotations(modelPropertiesPsi, errors),
        modelProjection,
        typesResolver,
        errors
    );

    try {
      return new OpVarPath(
          type,
          new OpTagPath(
              tag, parsedModelProjection, EpigraphPsiUtil.getLocation(modelProjection)
          ),
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (RuntimeException e) {
      throw new PsiProcessingException(e, psi, errors);
    }
  }


  private static @NotNull OpParams parseModelParams(
      @NotNull Collection<EdlOpModelPathProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    return parseParams(
        modelProperties.stream().map(EdlOpModelPathProperty::getOpParam),
        resolver,
        errors
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull Collection<EdlOpModelPathProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(EdlOpModelPathProperty::getAnnotation),
        errors
    );
  }

  private static @NotNull OpVarPath createDefaultVarPath(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return new OpVarPath(
        type,
        new OpTagPath(
            tag,
            createDefaultModelPath(
                tag.type,
                OpParams.EMPTY,
                Annotations.EMPTY,
                locationPsi,
                errors
            ),
            EpigraphPsiUtil.getLocation(locationPsi)
        ),
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @Contract("null -> true")
  private static boolean isModelPathEmpty(@Nullable EdlOpModelPath pathPsi) {
    return pathPsi == null || (
        pathPsi.getOpRecordModelPath() == null &&
        pathPsi.getOpMapModelPath() == null
    );
  }

  public static @NotNull OpModelPath<?, ?> parseModelPath(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull EdlOpModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable EdlOpRecordModelPath recordModelProjectionPsi = psi.getOpRecordModelPath();
        if (recordModelProjectionPsi == null)
          return createDefaultModelPath(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.RECORD, errors);
        return parseRecordModelPath(
            (RecordType) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );
      case MAP:
        @Nullable EdlOpMapModelPath mapModelProjectionPsi = psi.getOpMapModelPath();
        if (mapModelProjectionPsi == null)
          return createDefaultModelPath(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.MAP, errors);

        return parseMapModelPath(
            (MapType) type,
            params,
            annotations,
            mapModelProjectionPsi,
            typesResolver,
            errors
        );
      case LIST:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      case PRIMITIVE:
        return parsePrimitiveModelPath(
            (PrimitiveType<?>) type,
            params,
            annotations,
            psi
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, errors);
    }
  }

  private static void ensureModelKind(
      @NotNull EdlOpModelPath psi,
      @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, errors);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull EdlOpModelPath psi) {
    if (psi.getOpRecordModelPath() != null) return TypeKind.RECORD;
    if (psi.getOpMapModelPath() != null) return TypeKind.MAP;
    return null;
  }

  private static @NotNull OpModelPath<?, ?> createDefaultModelPath(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi, @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

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
          ), locationPsi, errors);

        final OpVarPath valueVarProjection = createDefaultVarPath(
            valueType.type,
            defaultValuesTag,
            locationPsi,
            errors
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
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            errors
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
      case PRIMITIVE:
        return new OpPrimitiveModelPath(
            (PrimitiveType<?>) type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  public static @NotNull OpRecordModelPath parseRecordModelPath(
      @NotNull RecordType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull EdlOpRecordModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @NotNull EdlOpFieldPathEntry fieldPathEntryPsi = psi.getOpFieldPathEntry();

    final String fieldName = fieldPathEntryPsi.getQid().getCanonicalName();
    RecordType.Field field = type.fieldsMap().get(fieldName);
    if (field == null)
      throw new PsiProcessingException(
          String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
          fieldPathEntryPsi,
          errors
      );

    final @NotNull EdlOpFieldPath fieldPathPsi = fieldPathEntryPsi.getOpFieldPath();

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldPathEntryPsi);

    final OpFieldPathEntry fieldProjection = new OpFieldPathEntry(
        field,
        parseFieldPath(
            field.dataType(),
            fieldPathPsi,
            typesResolver,
            errors
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

  public static @NotNull OpFieldPath parseFieldPath(
      @NotNull DataType fieldType,
      @NotNull EdlOpFieldPath psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Collection<OpParam> fieldParamsList = null;
    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;

    for (EdlOpFieldPathBodyPart fieldBodyPart : psi.getOpFieldPathBodyPartList()) {
      @Nullable EdlOpParam fieldParamPsi = fieldBodyPart.getOpParam();
      if (fieldParamPsi != null) {
        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, errors));
      }

      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), errors);
    }

    final OpVarPath varProjection;

    @Nullable EdlOpVarPath varPathPsi = psi.getOpVarPath();

    if (varPathPsi == null) {
      @Nullable Type.Tag defaultFieldTag = fieldType.defaultTag;
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for type '%s' because it has no default tag",
            fieldType.name
        ), psi, errors);

      varProjection = createDefaultVarPath(fieldType.type, defaultFieldTag, psi, errors);
    } else {
      varProjection = parseVarPath(fieldType, varPathPsi, resolver, errors);
    }

    return new OpFieldPath(
        OpParams.fromCollection(fieldParamsList),
        Annotations.fromMap(fieldAnnotationsMap),
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpMapModelPath parseMapModelPath(
      @NotNull MapType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull EdlOpMapModelPath psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpPathKeyProjection keyProjection = parseKeyProjection(psi.getOpPathKeyProjection(), resolver, errors);

    @Nullable EdlOpVarPath valueProjectionPsi = psi.getOpVarPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value projection not specified", psi, errors);

    @NotNull OpVarPath valueProjection = parseVarPath(type.valueType(), valueProjectionPsi, resolver, errors);

    return new OpMapModelPath(
        type,
        params,
        annotations,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpPathKeyProjection parseKeyProjection(
      @NotNull EdlOpPathKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Collection<OpParam> params = null;
    @Nullable Map<String, Annotation> annotationsMap = null;

    final @Nullable EdlOpPathKeyProjectionBody body = keyProjectionPsi.getOpPathKeyProjectionBody();
    if (body != null) {
      for (EdlOpPathKeyProjectionPart keyPart : body.getOpPathKeyProjectionPartList()) {
        @Nullable EdlOpParam paramPsi = keyPart.getOpParam();
        if (paramPsi != null) {
          if (params == null) params = new ArrayList<>(3);
          params.add(parseParameter(paramPsi, resolver, errors));
        }

        annotationsMap = parseAnnotation(annotationsMap, keyPart.getAnnotation(), errors);
      }
    }

    return new OpPathKeyProjection(
        OpParams.fromCollection(params),
        Annotations.fromMap(annotationsMap),
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  public static @NotNull OpPrimitiveModelPath parsePrimitiveModelPath(
      @NotNull PrimitiveType<?> type,
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