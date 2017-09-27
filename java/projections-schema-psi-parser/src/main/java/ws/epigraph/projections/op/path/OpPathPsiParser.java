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

package ws.epigraph.projections.op.path;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotation;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.SchemaProjectionPsiParserUtil;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.OpModelProjection;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.parser.SchemaPsiParserUtil;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.*;
import ws.epigraph.types.TypeKind;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.*;
import static ws.epigraph.schema.parser.SchemaPsiParserUtil.parseAnnotation;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpPathPsiParser {

  private OpPathPsiParser() {}

  public static @NotNull OpVarPath parseVarPath(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpEntityPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPathPsiProcessingContext context)
      throws PsiProcessingException {

    final TypeApi type = dataType.type();

    @Nullable SchemaOpModelPath modelProjection = psi.getOpModelPath();


    if (isModelPathEmpty(modelProjection) ) {
      if (psi.getTagName() != null)
        throw new PsiProcessingException(
            "Path can't end with a tag (path tip type must be a data type)",
            psi.getTagName(),
            context
        );

      return new OpVarPath(
          type,
          null, // no tags = end of path
          EpigraphPsiUtil.getLocation(psi)
      );
    }

    final TagApi tag = getTag(
        dataType,
        psi.getTagName(),
        psi,
        context
    );


    final OpModelPath<?, ?, ?> parsedModelProjection = parseModelPath(
        tag.type(),
        modelProjection,
        typesResolver,
        context
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
      throw new PsiProcessingException(e, psi, context);
    }
  }


  private static @NotNull OpParams parseModelParams(
      @NotNull Collection<SchemaOpModelPathProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {


    return parseParams(
        modelProperties.stream().map(SchemaOpModelPathProperty::getOpParam),
        resolver,
        context.inputPsiProcessingContext()
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull Collection<SchemaOpModelPathProperty> modelProperties,
      @NotNull OpPathPsiProcessingContext context,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    return SchemaPsiParserUtil.parseAnnotations(
        modelProperties.stream().map(SchemaOpModelPathProperty::getAnnotation),
        context,
        typesResolver
    );
  }

  private static @NotNull OpVarPath createDefaultVarPath(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
      @NotNull PsiElement locationPsi,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    return new OpVarPath(
        type,
        new OpTagPath(
            tag,
            createDefaultModelPath(
                tag.type(),
                OpParams.EMPTY,
                Annotations.EMPTY,
                locationPsi,
                context
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
        pathPsi.getOpMapModelPath() == null &&
        pathPsi.getOpModelPathPropertyList().isEmpty()
    );
  }

  private static @NotNull OpModelPath<?, ?, ?> parseModelPath(
      @NotNull DatumTypeApi type,
      @NotNull SchemaOpModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    @NotNull Collection<SchemaOpModelPathProperty> modelPropertiesPsi = psi.getOpModelPathPropertyList();
    final OpParams params = parseModelParams(modelPropertiesPsi, typesResolver, context);
    final Annotations annotations = parseModelAnnotations(modelPropertiesPsi, context, typesResolver);

    switch (type.kind()) {
      case RECORD:
        @Nullable SchemaOpRecordModelPath recordModelProjectionPsi = psi.getOpRecordModelPath();
        if (recordModelProjectionPsi == null)
          return createDefaultModelPath(type, params, annotations, psi, context);
        ensureModelKind(psi, TypeKind.RECORD, context);
        return parseRecordModelPath(
            (RecordTypeApi) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver,
            context
        );
      case MAP:
        @Nullable SchemaOpMapModelPath mapModelProjectionPsi = psi.getOpMapModelPath();
        if (mapModelProjectionPsi == null)
          return createDefaultModelPath(type, params, annotations, psi, context);
        ensureModelKind(psi, TypeKind.MAP, context);

        return parseMapModelPath(
            (MapTypeApi) type,
            params,
            annotations,
            mapModelProjectionPsi,
            typesResolver,
            context
        );
      case LIST:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      case PRIMITIVE:
        return parsePrimitiveModelPath(
            (PrimitiveTypeApi) type,
            params,
            annotations,
            psi
        );
      case ENTITY:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, context);
    }
  }

  private static void ensureModelKind(
      @NotNull SchemaOpModelPath psi,
      @NotNull TypeKind expectedKind,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, context);
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
      @NotNull PsiElement locationPsi, @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

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
                null,
                EpigraphPsiUtil.getLocation(locationPsi)
            );

        @NotNull DataTypeApi valueType = mapType.valueType();
        @Nullable TagApi defaultValuesTag = valueType.retroTag();

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a default tag",
              type.name(),
              valueType.name()
          ), locationPsi, context);

        final OpVarPath valueVarProjection = createDefaultVarPath(
            valueType.type(),
            defaultValuesTag,
            locationPsi,
            context
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
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, context);
      case ENTITY:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            context
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, context);
      case PRIMITIVE:
        return new OpPrimitiveModelPath(
            (PrimitiveTypeApi) type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, context);
    }
  }

  public static @NotNull OpRecordModelPath parseRecordModelPath(
      @NotNull RecordTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull SchemaOpRecordModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    final @NotNull SchemaOpFieldPathEntry fieldPathEntryPsi = psi.getOpFieldPathEntry();

    final String fieldName = fieldPathEntryPsi.getQid().getCanonicalName();
    FieldApi field = type.fieldsMap().get(fieldName);
    if (field == null)
      throw new PsiProcessingException(
          String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
          fieldPathEntryPsi,
          context
      );

    final @NotNull SchemaOpFieldPath fieldPathPsi = fieldPathEntryPsi.getOpFieldPath();

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldPathEntryPsi);

    final OpFieldPathEntry fieldProjection = new OpFieldPathEntry(
        field,
        parseFieldPath(
            field.dataType(),
            fieldPathPsi,
            typesResolver,
            context
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
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

//    Collection<OpParam> fieldParamsList = null;
//    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
//
//    for (SchemaOpFieldPathBodyPart fieldBodyPart : psi.getOpFieldPathBodyPartList()) {
//      @Nullable SchemaOpParam fieldParamPsi = fieldBodyPart.getOpParam();
//      if (fieldParamPsi != null) {
//        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
//        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, context));
//      }
//
//      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), context);
//    }

    final OpVarPath varProjection;

    @Nullable SchemaOpEntityPath varPathPsi = psi.getOpEntityPath();

    varProjection = parseVarPath(fieldType, varPathPsi, resolver, context);

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
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    @NotNull OpPathKeyProjection keyProjection =
        parseKeyProjection(type.keyType(), psi.getOpPathKeyProjection(), resolver, context);

    @Nullable SchemaOpEntityPath valueProjectionPsi = psi.getOpEntityPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value projection not specified", psi, context);

    @NotNull OpVarPath valueProjection = parseVarPath(type.valueType(), valueProjectionPsi, resolver, context);

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
      @NotNull DatumTypeApi keyType,
      @NotNull SchemaOpPathKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    Collection<OpParam> params = null;
    @Nullable Map<DatumTypeApi, Annotation> annotationsMap = null;
    @Nullable OpModelProjection<?, ?, ?, ?> projection = null;

    final @Nullable SchemaOpPathKeyProjectionBody body = keyProjectionPsi.getOpPathKeyProjectionBody();
    if (body != null) {
      for (SchemaOpPathKeyProjectionPart keyPart : body.getOpPathKeyProjectionPartList()) {
        @Nullable SchemaOpParam paramPsi = keyPart.getOpParam();
        if (paramPsi != null) {
          if (params == null) params = new ArrayList<>(3);
          params.add(parseParameter(paramPsi, resolver, context.inputPsiProcessingContext()));
        }

        annotationsMap = parseAnnotation(annotationsMap, keyPart.getAnnotation(), context, resolver);

        projection = SchemaProjectionPsiParserUtil.parseKeyProjection(
            keyType,
            projection,
            keyPart.getOpKeyProjection(),
            resolver,
            context.inputPsiProcessingContext()
        );
      }
    }

    return new OpPathKeyProjection(
        OpParams.fromCollection(params),
        Annotations.fromMap(annotationsMap),
        projection,
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
