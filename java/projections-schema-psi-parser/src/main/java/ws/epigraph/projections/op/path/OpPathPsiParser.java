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
import ws.epigraph.projections.op.*;
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
import java.util.Collections;
import java.util.Map;

import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.*;
import static ws.epigraph.schema.parser.SchemaPsiParserUtil.parseAnnotation;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpPathPsiParser {

  private OpPathPsiParser() {}

  public static @NotNull OpProjection<?, ?> parsePath(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpEntityPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpPathPsiProcessingContext context)
      throws PsiProcessingException {

    final TypeApi type = dataType.type();

    @Nullable SchemaOpModelPath modelPathPsi = psi.getOpModelPath();

    if (isModelPathEmpty(modelPathPsi)) {
      if (psi.getTagName() != null)
        throw new PsiProcessingException(
            "Path can't end with a tag (path tip type must be a data type)",
            psi.getTagName(),
            context
        );

      return OpPath.pathEnd(type, EpigraphPsiUtil.getLocation(psi));
    }

    final TagApi tag = getTag(
        dataType,
        psi.getTagName(),
        EpigraphPsiUtil.getLocation(psi),
        context
    );

    final OpModelProjection<?, ?, ?, ?> modelPath = parseModelPath(
        tag.type(),
        modelPathPsi,
        typesResolver,
        context
    );

    return
        type.kind() == TypeKind.ENTITY ?
        OpEntityProjection.path(
            type,
            new OpTagProjectionEntry(
                tag,
                modelPath,
                EpigraphPsiUtil.getLocation(modelPathPsi)
            ),
            EpigraphPsiUtil.getLocation(psi)
        ) :
        modelPath;
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

  private static @NotNull OpEntityProjection createDefaultEntityPath(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
      @NotNull PsiElement locationPsi,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    return OpEntityProjection.path(
        type,
        new OpTagProjectionEntry(
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

  private static @NotNull OpModelProjection<?, ?, ?, ?> parseModelPath(
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

  private static @NotNull OpModelProjection<?, ?, ?, ?> createDefaultModelPath(
      @NotNull DatumTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi, @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);
    switch (type.kind()) {
      case RECORD:
        return new OpRecordModelProjection(
            (RecordTypeApi) type,
            false,
            null,
            params,
            annotations,
            null,
            Collections.emptyMap(),
            null,
            location
        );
      case MAP:
        MapTypeApi mapType = (MapTypeApi) type;

        final OpKeyProjection keyProjection =
            new OpKeyProjection(
                OpKeyPresence.OPTIONAL,
                location,
                OpParams.EMPTY,
                Annotations.EMPTY,
                null,
                location
            );

        @NotNull DataTypeApi valueType = mapType.valueType();
        @Nullable TagApi tag = valueType.retroTag();

        if (tag == null && valueType.type().kind() != TypeKind.ENTITY)
          tag = ((DatumTypeApi) (valueType.type())).self();

        if (tag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default path for map type '%s, as it's value type '%s' doesn't have a retro tag",
              type.name(),
              valueType.name()
          ), locationPsi, context);

        final OpEntityProjection valueVarProjection = createDefaultEntityPath(
            valueType.type(),
            tag,
            locationPsi,
            context
        );

        return new OpMapModelProjection(
            mapType,
            false,
            null,
            params,
            annotations,
            null,
            keyProjection,
            valueVarProjection,
            null,
            location
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
        return new OpPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            false,
            null,
            params,
            annotations,
            null,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, context);
    }
  }

  public static @NotNull OpRecordModelProjection parseRecordModelPath(
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
          String.format("Can't field path for '%s', field '%s' not found", type.name(), fieldName),
          fieldPathEntryPsi,
          context
      );

    final @Nullable SchemaOpFieldPath fieldPathPsi = fieldPathEntryPsi.getOpFieldPath();
    if (fieldPathPsi == null)
      throw new PsiProcessingException(String.format("Incomplete field '%s' path", fieldName), psi, context);

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldPathEntryPsi);

    final OpFieldProjectionEntry fieldProjection = new OpFieldProjectionEntry(
        field,
        parseFieldPath(
            field.dataType(),
            fieldPathPsi,
            typesResolver,
            context
        ),
        fieldLocation
    );

    return new OpRecordModelProjection(
        type,
        false,
        null,
        params,
        annotations,
        null,
        Collections.singletonMap(fieldName, fieldProjection),
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpFieldProjection parseFieldPath(
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

    final OpProjection<?, ?> fieldProjection;

    @Nullable SchemaOpEntityPath varPathPsi = psi.getOpEntityPath();

    fieldProjection = parsePath(fieldType, varPathPsi, resolver, context);

    return new OpFieldProjection(
//        OpParams.fromCollection(fieldParamsList),
//        Annotations.fromMap(fieldAnnotationsMap),
        fieldProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpMapModelProjection parseMapModelPath(
      @NotNull MapTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull SchemaOpMapModelPath psi,
      @NotNull TypesResolver resolver,
      @NotNull OpPathPsiProcessingContext context) throws PsiProcessingException {

    @NotNull OpKeyProjection keyProjection =
        parseKeyProjection(type.keyType(), psi.getOpPathKeyProjection(), resolver, context);

    @Nullable SchemaOpEntityPath valueProjectionPsi = psi.getOpEntityPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value path not specified", psi, context);

    @NotNull OpProjection<?, ?> valueProjection =
        parsePath(type.valueType(), valueProjectionPsi, resolver, context);

    return new OpMapModelProjection(
        type,
        false,
        null,
        params,
        annotations,
        null,
        keyProjection,
        valueProjection,
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpKeyProjection parseKeyProjection(
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

    return new OpKeyProjection(
        OpKeyPresence.REQUIRED,
        EpigraphPsiUtil.getLocation(keyProjectionPsi),
        OpParams.fromCollection(params),
        Annotations.fromMap(annotationsMap),
        projection,
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  private static @NotNull OpPrimitiveModelProjection parsePrimitiveModelPath(
      @NotNull PrimitiveTypeApi type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) {

    return new OpPrimitiveModelProjection(
        type,
        false,
        null,
        params,
        annotations,
        null,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
