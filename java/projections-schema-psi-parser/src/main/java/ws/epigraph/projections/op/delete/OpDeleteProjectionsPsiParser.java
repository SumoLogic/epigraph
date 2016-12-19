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

package ws.epigraph.projections.op.delete;

import com.intellij.psi.PsiElement;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.types.TypeKind;

import java.text.MessageFormat;
import java.util.*;

import static ws.epigraph.projections.EdlProjectionPsiParserUtil.*;
import static ws.epigraph.projections.ProjectionsParsingUtil.getType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpDeleteProjectionsPsiParser {

  private OpDeleteProjectionsPsiParser() {}

  public static OpDeleteVarProjection parseVarProjection(
      @NotNull DataType dataType,
      @NotNull EdlOpDeleteVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, OpDeleteTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    boolean canDelete = psi.getPlus() != null;
    @Nullable EdlOpDeleteSingleTagProjection singleTagProjectionPsi = psi.getOpDeleteSingleTagProjection();

    if (singleTagProjectionPsi == null) {
      @Nullable EdlOpDeleteMultiTagProjection multiTagProjection = psi.getOpDeleteMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull Iterable<EdlOpDeleteMultiTagProjectionItem> tagProjectionPsiList =
          multiTagProjection.getOpDeleteMultiTagProjectionItemList();

      for (EdlOpDeleteMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
        final Type.Tag tag = getTag(type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi, errors);

        @NotNull DatumType tagType = tag.type;
        @Nullable EdlOpDeleteModelProjection modelProjection = tagProjectionPsi.getOpDeleteModelProjection();
        assert modelProjection != null; // todo when it can be null?

        @NotNull Collection<EdlOpDeleteModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpDeleteModelPropertyList();

        final OpDeleteModelProjection<?, ?> parsedModelProjection = parseModelProjection(
            tagType,
            parseModelParams(modelPropertiesPsi, typesResolver, errors),
            parseModelAnnotations(modelPropertiesPsi, errors),
            modelProjection,
            typesResolver,
            errors
        );

        tagProjections.put(
            tag.name(),
            new OpDeleteTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      }
    } else {
      Type.Tag tag = findTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi,
          errors
      );
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        if (tag == null) // will throw proper error
          tag = getTag(
              type,
              singleTagProjectionPsi.getTagName(),
              dataType.defaultTag,
              singleTagProjectionPsi,
              errors
          );

        @Nullable EdlOpDeleteModelProjection modelProjection = singleTagProjectionPsi.getOpDeleteModelProjection();
        assert modelProjection != null; // todo when it can be null?

        @NotNull Collection<EdlOpDeleteModelProperty> modelPropertiesPsi =
            singleTagProjectionPsi.getOpDeleteModelPropertyList();

        final OpDeleteModelProjection<?, ?> parsedModelProjection = parseModelProjection(
            tag.type,
            parseModelParams(modelPropertiesPsi, typesResolver, errors),
            parseModelAnnotations(modelPropertiesPsi, errors),
            modelProjection,
            typesResolver,
            errors
        );

        tagProjections.put(
            tag.name(),
            new OpDeleteTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
            )
        );
      }
    }

    // parse tails
    final List<OpDeleteVarProjection> tails;
    @Nullable EdlOpDeleteVarPolymorphicTail psiTail = psi.getOpDeleteVarPolymorphicTail();
    if (psiTail == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable EdlOpDeleteVarSingleTail singleTail = psiTail.getOpDeleteVarSingleTail();
      if (singleTail == null) {
        @Nullable EdlOpDeleteVarMultiTail multiTail = psiTail.getOpDeleteVarMultiTail();
        assert multiTail != null;
        for (EdlOpDeleteVarMultiTailItem tailItem : multiTail.getOpDeleteVarMultiTailItemList()) {
          @NotNull EdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull EdlOpDeleteVarProjection psiTailProjection = tailItem.getOpDeleteVarProjection();
          @NotNull OpDeleteVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, errors);
          tails.add(tailProjection);
        }
      } else {
        @NotNull EdlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull EdlOpDeleteVarProjection psiTailProjection = singleTail.getOpDeleteVarProjection();
        @NotNull OpDeleteVarProjection tailProjection =
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, errors);
        tails.add(tailProjection);
      }

    }

    try {
      return new OpDeleteVarProjection(
          type,
          canDelete,
          tagProjections,
          singleTagProjectionPsi == null || tagProjections.size() != 1,
          tails,
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (IllegalArgumentException e) {
      throw new PsiProcessingException(e, psi, errors);
    }
  }

  private static @NotNull OpParams parseModelParams(
      @NotNull Collection<EdlOpDeleteModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseParams(
        modelProperties.stream().map(EdlOpDeleteModelProperty::getOpParam),
        resolver,
        errors
    );

  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull Collection<EdlOpDeleteModelProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(EdlOpDeleteModelProperty::getAnnotation),
        errors
    );
  }

  private static @NotNull OpDeleteVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull EdlTypeRef tailTypeRefPsi,
      @NotNull EdlOpDeleteVarProjection psiTailProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, errors);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);
    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver,
        errors
    );
  }

  private static @NotNull OpDeleteVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      boolean canDelete,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return new OpDeleteVarProjection(
        type,
        canDelete,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpDeleteTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type,
                    OpParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    errors
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        ),
        false,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @NotNull OpDeleteVarProjection createDefaultVarProjection(
      @NotNull DatumType type,
      boolean canDelete,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, canDelete, locationPsi, errors);
  }

  private static @NotNull OpDeleteVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      boolean canDelete,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name),
          locationPsi,
          errors
      );

    return createDefaultVarProjection(type.type, defaultTag, canDelete, locationPsi, errors);
  }

  public static @NotNull OpDeleteModelProjection<?, ?> parseModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull EdlOpDeleteModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable EdlOpDeleteRecordModelProjection recordModelProjectionPsi = psi.getOpDeleteRecordModelProjection();
        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.RECORD, errors);
        return parseRecordModelProjection(
            (RecordType) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );
      case MAP:
        @Nullable EdlOpDeleteMapModelProjection mapModelProjectionPsi = psi.getOpDeleteMapModelProjection();
        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.MAP, errors);

        return parseMapModelProjection(
            (MapType) type,
            params,
            annotations,
            mapModelProjectionPsi,
            typesResolver,
            errors
        );
      case LIST:
        @Nullable EdlOpDeleteListModelProjection listModelProjectionPsi = psi.getOpDeleteListModelProjection();
        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi, errors);
        ensureModelKind(psi, TypeKind.LIST, errors);

        return parseListModelProjection(
            (ListType) type,
            params,
            annotations,
            listModelProjectionPsi,
            typesResolver,
            errors
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      case PRIMITIVE:
        return parsePrimitiveModelProjection(
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
      @NotNull EdlOpDeleteModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    // todo move to common
    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, errors);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull EdlOpDeleteModelProjection psi) {
    // todo move to common
    if (psi.getOpDeleteRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpDeleteMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpDeleteListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  private static @NotNull OpDeleteModelProjection<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpDeleteRecordModelProjection(
            (RecordType) type,
            params,
            annotations,
            Collections.emptyMap(),
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case MAP:
        MapType mapType = (MapType) type;

        final OpDeleteKeyProjection keyProjection =
            new OpDeleteKeyProjection(
                OpKeyPresence.OPTIONAL,
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

        final OpDeleteVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            false,
            locationPsi,
            errors
        );

        return new OpDeleteMapModelProjection(
            mapType,
            params,
            annotations,
            keyProjection,
            valueVarProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case LIST:
        ListType listType = (ListType) type;
        @NotNull DataType elementType = listType.elementType();
        Type.@Nullable Tag defaultElementsTag = elementType.defaultTag;

        if (defaultElementsTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for list type '%s, as it's element type '%s' doesn't have a default tag",
              type.name(),
              elementType.name
          ), locationPsi, errors);

        final OpDeleteVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultElementsTag,
            false,
            locationPsi,
            errors
        );

        return new OpDeleteListModelProjection(
            listType,
            params,
            annotations,
            itemVarProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            errors
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
      case PRIMITIVE:
        return new OpDeletePrimitiveModelProjection(
            (PrimitiveType<?>) type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  public static @NotNull OpDeleteRecordModelProjection parseRecordModelProjection(
      @NotNull RecordType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull EdlOpDeleteRecordModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, OpDeleteFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull Iterable<EdlOpDeleteFieldProjectionEntry> fieldProjectionEntriesPsi =
        psi.getOpDeleteFieldProjectionEntryList();

    for (EdlOpDeleteFieldProjectionEntry fieldProjectionEntryPsi : fieldProjectionEntriesPsi) {
      final String fieldName = fieldProjectionEntryPsi.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);

      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            fieldProjectionEntryPsi,
            errors
        );

      fieldProjections.put(
          fieldName,
          new OpDeleteFieldProjectionEntry(
              field,
              parseFieldProjection(
                  field.dataType(),
                  fieldProjectionEntryPsi.getOpDeleteFieldProjection(),
                  typesResolver,
                  errors
              ),
              EpigraphPsiUtil.getLocation(fieldProjectionEntryPsi)
          )
      );
    }

    return new OpDeleteRecordModelProjection(
        type,
        params,
        annotations,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpDeleteFieldProjection parseFieldProjection(
      @NotNull DataType fieldType,
      @NotNull EdlOpDeleteFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Collection<OpParam> fieldParamsList = null;
    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
    for (EdlOpDeleteFieldProjectionBodyPart fieldBodyPart : psi.getOpDeleteFieldProjectionBodyPartList()) {
      @Nullable EdlOpParam fieldParamPsi = fieldBodyPart.getOpParam();
      if (fieldParamPsi != null) {
        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, errors));
      }

      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), errors);
    }

    return new OpDeleteFieldProjection(
        OpParams.fromCollection(fieldParamsList),
        Annotations.fromMap(fieldAnnotationsMap),
        parseVarProjection(fieldType, psi.getOpDeleteVarProjection(), resolver, errors),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpDeleteMapModelProjection parseMapModelProjection(
      @NotNull MapType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull EdlOpDeleteMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpDeleteKeyProjection keyProjection = parseKeyProjection(psi.getOpDeleteKeyProjection(), resolver, errors);

    @Nullable EdlOpDeleteVarProjection valueProjectionPsi = psi.getOpDeleteVarProjection();
    @NotNull OpDeleteVarProjection valueProjection =
        valueProjectionPsi == null
        ? createDefaultVarProjection(
            type.valueType(),
            keyProjection.presence() == OpKeyPresence.REQUIRED,
            psi,
            errors
        )
        : parseVarProjection(type.valueType(), valueProjectionPsi, resolver, errors);

    return new OpDeleteMapModelProjection(
        type,
        params,
        annotations,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpDeleteKeyProjection parseKeyProjection(
      @NotNull EdlOpDeleteKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final OpKeyPresence presence;

    if (keyProjectionPsi.getForbidden() != null)
      presence = OpKeyPresence.FORBIDDEN;
    else if (keyProjectionPsi.getRequired() != null)
      presence = OpKeyPresence.REQUIRED;
    else
      presence = OpKeyPresence.OPTIONAL;

    List<OpParam> params = null;
    @Nullable Map<String, Annotation> annotationsMap = null;

    for (EdlOpDeleteKeyProjectionPart keyPart : keyProjectionPsi.getOpDeleteKeyProjectionPartList()) {
      @Nullable EdlOpParam paramPsi = keyPart.getOpParam();
      if (paramPsi != null) {
        if (params == null) params = new ArrayList<>(3);
        params.add(parseParameter(paramPsi, resolver, errors));
      }

      annotationsMap = parseAnnotation(annotationsMap, keyPart.getAnnotation(), errors);
    }

    return new OpDeleteKeyProjection(
        presence,
        params == null ? OpParams.EMPTY : new OpParams(params),
        annotationsMap == null ? Annotations.EMPTY : new Annotations(annotationsMap),
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  public static @NotNull OpDeleteListModelProjection parseListModelProjection(
      @NotNull ListType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull EdlOpDeleteListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    OpDeleteVarProjection itemsProjection;
    @Nullable EdlOpDeleteVarProjection opDeleteVarProjectionPsi = psi.getOpDeleteVarProjection();
    if (opDeleteVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi, errors);
    else
      itemsProjection = parseVarProjection(type.elementType(), opDeleteVarProjectionPsi, resolver, errors);


    return new OpDeleteListModelProjection(
        type,
        params,
        annotations,
        itemsProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpDeletePrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveType<?> type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) {

    return new OpDeletePrimitiveModelProjection(
        type,
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
