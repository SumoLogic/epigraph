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

import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpPathPsiParser {

  private OpPathPsiParser() {}

  public static OpVarPath parseVarPath(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpVarPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final TypeApi type = dataType.type();

    @Nullable SchemaOpModelPath modelProjection = psi.getOpModelPath();

    @NotNull Collection<SchemaOpModelPathProperty> modelPropertiesPsi = psi.getOpModelPathPropertyList();
    final OpParams modelParams = parseModelParams(modelPropertiesPsi, typesResolver, errors);
    final Annotations modelAnnotations = parseModelAnnotations(modelPropertiesPsi, errors);

    if (isModelPathEmpty(modelProjection) && modelParams.isEmpty() && modelAnnotations.isEmpty()) {
      if (psi.getTagName() != null)
        throw new PsiProcessingException("Path can't end with a tag", psi.getTagName(), errors);

      return new OpVarPath(
          type,
          null, // no tags = end of path
          EpigraphPsiUtil.getLocation(psi)
      );
    }

    final TagApi tag = getTag(
        type,
        psi.getTagName(),
        dataType.defaultTag(),
        psi,
        errors
    );


    final OpModelPath<?, ?, ?> parsedModelProjection = parseModelPath(
        tag.type(),
        modelParams,
        modelAnnotations,
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
      @NotNull Collection<SchemaOpModelPathProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    return parseParams(
        modelProperties.stream().map(SchemaOpModelPathProperty::getOpParam),
        resolver,
        errors
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull Collection<SchemaOpModelPathProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(SchemaOpModelPathProperty::getAnnotation),
        errors
    );
  }

  private static @NotNull OpVarPath createDefaultVarPath(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return new OpVarPath(
        type,
        new OpTagPath(
            tag,
            createDefaultModelPath(
                tag.type(),
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
  private static boolean isModelPathEmpty(@Nullable SchemaOpModelPath pathPsi) {
    return pathPsi == null || (
        pathPsi.getOpRecordModelPath() == null &&
        pathPsi.getOpMapModelPath() == null
    );
  }

  public static @NotNull OpModelPath<?, ?, ?> parseModelPath(
      @NotNull DatumTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull SchemaOpModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable SchemaOpRecordModelPath recordModelProjectionPsi = psi.getOpRecordModelPath();
        if (recordModelProjectionPsi == null)
          return createDefaultModelPath(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.RECORD, errors);
        return parseRecordModelPath(
            (RecordTypeApi) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );
      case MAP:
        @Nullable SchemaOpMapModelPath mapModelProjectionPsi = psi.getOpMapModelPath();
        if (mapModelProjectionPsi == null)
          return createDefaultModelPath(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.MAP, errors);

        return parseMapModelPath(
            (MapTypeApi) type,
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
            (PrimitiveTypeApi) type,
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
      @NotNull SchemaOpModelPath psi,
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

  private static @Nullable TypeKind findProjectionKind(@NotNull SchemaOpModelPath psi) {
    if (psi.getOpRecordModelPath() != null) return TypeKind.RECORD;
    if (psi.getOpMapModelPath() != null) return TypeKind.MAP;
    return null;
  }

  private static @NotNull OpModelPath<?, ?, ?> createDefaultModelPath(
      @NotNull DatumTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi, @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpRecordModelPath(
            (RecordTypeApi) type,
            params,
            annotations,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case MAP:
        MapTypeApi mapType = (MapTypeApi) type;

        final OpPathKeyProjection keyProjection =
            new OpPathKeyProjection(
                OpParams.EMPTY,
                Annotations.EMPTY,
                EpigraphPsiUtil.getLocation(locationPsi)
            );

        @NotNull DataTypeApi valueType = mapType.valueType();
        @Nullable TagApi defaultValuesTag = valueType.defaultTag();

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a default tag",
              type.name(),
              valueType.name()
          ), locationPsi, errors);

        final OpVarPath valueVarProjection = createDefaultVarPath(
            valueType.type(),
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
            (PrimitiveTypeApi) type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  public static @NotNull OpRecordModelPath parseRecordModelPath(
      @NotNull RecordTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull SchemaOpRecordModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @NotNull SchemaOpFieldPathEntry fieldPathEntryPsi = psi.getOpFieldPathEntry();

    final String fieldName = fieldPathEntryPsi.getQid().getCanonicalName();
    FieldApi field = type.fieldsMap().get(fieldName);
    if (field == null)
      throw new PsiProcessingException(
          String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
          fieldPathEntryPsi,
          errors
      );

    final @NotNull SchemaOpFieldPath fieldPathPsi = fieldPathEntryPsi.getOpFieldPath();

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
      @NotNull DataTypeApi fieldType,
      @NotNull SchemaOpFieldPath psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

//    Collection<OpParam> fieldParamsList = null;
//    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
//
//    for (SchemaOpFieldPathBodyPart fieldBodyPart : psi.getOpFieldPathBodyPartList()) {
//      @Nullable SchemaOpParam fieldParamPsi = fieldBodyPart.getOpParam();
//      if (fieldParamPsi != null) {
//        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
//        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, errors));
//      }
//
//      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), errors);
//    }

    final OpVarPath varProjection;

    @Nullable SchemaOpVarPath varPathPsi = psi.getOpVarPath();

    if (varPathPsi == null) {
      @Nullable TagApi defaultFieldTag = fieldType.defaultTag();
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for type '%s' because it has no default tag",
            fieldType.name()
        ), psi, errors);

      varProjection = createDefaultVarPath(fieldType.type(), defaultFieldTag, psi, errors);
    } else {
      varProjection = parseVarPath(fieldType, varPathPsi, resolver, errors);
    }

    return new OpFieldPath(
//        OpParams.fromCollection(fieldParamsList),
//        Annotations.fromMap(fieldAnnotationsMap),
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpMapModelPath parseMapModelPath(
      @NotNull MapTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull SchemaOpMapModelPath psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpPathKeyProjection keyProjection = parseKeyProjection(psi.getOpPathKeyProjection(), resolver, errors);

    @Nullable SchemaOpVarPath valueProjectionPsi = psi.getOpVarPath();

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
      @NotNull SchemaOpPathKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Collection<OpParam> params = null;
    @Nullable Map<String, Annotation> annotationsMap = null;

    final @Nullable SchemaOpPathKeyProjectionBody body = keyProjectionPsi.getOpPathKeyProjectionBody();
    if (body != null) {
      for (SchemaOpPathKeyProjectionPart keyPart : body.getOpPathKeyProjectionPartList()) {
        @Nullable SchemaOpParam paramPsi = keyPart.getOpParam();
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
      @NotNull PrimitiveTypeApi type,
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
