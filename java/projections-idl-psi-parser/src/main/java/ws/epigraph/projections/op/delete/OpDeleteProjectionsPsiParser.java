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
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.parser.psi.*;
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

import java.text.MessageFormat;
import java.util.*;

import static ws.epigraph.projections.IdlProjectionPsiParserUtil.*;
import static ws.epigraph.projections.ProjectionsParsingUtil.getType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteProjectionsPsiParser {

  public static OpDeleteVarProjection parseVarProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpDeleteVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, OpDeleteTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    boolean canDelete = psi.getPlus() != null;
    @Nullable IdlOpDeleteSingleTagProjection singleTagProjectionPsi = psi.getOpDeleteSingleTagProjection();

    if (singleTagProjectionPsi != null) {
//      boolean isDatum = type.kind() != TypeKind.UNION;

      final OpDeleteModelProjection<?, ?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi,
          errors
      );

      @Nullable IdlOpDeleteModelProjection modelProjection = singleTagProjectionPsi.getOpDeleteModelProjection();
      assert modelProjection != null; // todo when it can be null?

      @NotNull List<IdlOpDeleteModelProperty> modelPropertiesPsi =
          singleTagProjectionPsi.getOpDeleteModelPropertyList();

      parsedModelProjection = parseModelProjection(
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
    } else {
      @Nullable IdlOpDeleteMultiTagProjection multiTagProjection = psi.getOpDeleteMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<IdlOpDeleteMultiTagProjectionItem> tagProjectionPsiList =
          multiTagProjection.getOpDeleteMultiTagProjectionItemList();

      for (IdlOpDeleteMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
        final Type.Tag tag = getTag(type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi, errors);

        final OpDeleteModelProjection<?, ?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpDeleteModelProjection modelProjection = tagProjectionPsi.getOpDeleteModelProjection();
        assert modelProjection != null; // todo when it can be null?

        @NotNull List<IdlOpDeleteModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpDeleteModelPropertyList();

        parsedModelProjection = parseModelProjection(
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
    }

    // parse tails
    final List<OpDeleteVarProjection> tails;
    @Nullable IdlOpDeleteVarPolymorphicTail psiTail = psi.getOpDeleteVarPolymorphicTail();
    if (psiTail != null) {
      tails = new ArrayList<>();

      @Nullable IdlOpDeleteVarSingleTail singleTail = psiTail.getOpDeleteVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull IdlOpDeleteVarProjection psiTailProjection = singleTail.getOpDeleteVarProjection();
        @NotNull OpDeleteVarProjection tailProjection =
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, errors);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpDeleteVarMultiTail multiTail = psiTail.getOpDeleteVarMultiTail();
        assert multiTail != null;
        for (IdlOpDeleteVarMultiTailItem tailItem : multiTail.getOpDeleteVarMultiTailItemList()) {
          @NotNull IdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull IdlOpDeleteVarProjection psiTailProjection = tailItem.getOpDeleteVarProjection();
          @NotNull OpDeleteVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, errors);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    try {
      return new OpDeleteVarProjection(type, canDelete, tagProjections, tails, EpigraphPsiUtil.getLocation(psi));
    } catch (IllegalArgumentException e) {
      throw new PsiProcessingException(e, psi, errors);
    }
  }

  @NotNull
  private static OpParams parseModelParams(
      @NotNull List<IdlOpDeleteModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseParams(
        modelProperties.stream().map(IdlOpDeleteModelProperty::getOpParam),
        resolver,
        errors
    );

  }

  @NotNull
  private static Annotations parseModelAnnotations(
      @NotNull List<IdlOpDeleteModelProperty> modelProperties,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(IdlOpDeleteModelProperty::getAnnotation),
        errors
    );
  }

  @NotNull
  private static OpDeleteVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull IdlTypeRef tailTypeRefPsi,
      @NotNull IdlOpDeleteVarProjection psiTailProjection,
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

  @NotNull
  private static OpDeleteVarProjection createDefaultVarProjection(
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
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  private static OpDeleteVarProjection createDefaultVarProjection(
      @NotNull DatumType type,
      boolean canDelete,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, canDelete, locationPsi, errors);
  }

  @NotNull
  private static OpDeleteVarProjection createDefaultVarProjection(
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

  @NotNull
  public static OpDeleteModelProjection<?, ?> parseModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpDeleteModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpDeleteRecordModelProjection recordModelProjectionPsi = psi.getOpDeleteRecordModelProjection();
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
        @Nullable IdlOpDeleteMapModelProjection mapModelProjectionPsi = psi.getOpDeleteMapModelProjection();
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
        @Nullable IdlOpDeleteListModelProjection listModelProjectionPsi = psi.getOpDeleteListModelProjection();
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
            (PrimitiveType) type,
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
      @NotNull IdlOpDeleteModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    // todo move to common
    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, errors);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull IdlOpDeleteModelProjection psi) {
    // todo move to common
    if (psi.getOpDeleteRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpDeleteMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpDeleteListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  private static OpDeleteModelProjection<?, ?> createDefaultModelProjection(
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
            (PrimitiveType) type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  @NotNull
  public static OpDeleteRecordModelProjection parseRecordModelProjection(
      @NotNull RecordType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpDeleteRecordModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, OpDeleteFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<IdlOpDeleteFieldProjectionEntry> fieldProjectionEntriesPsi =
        psi.getOpDeleteFieldProjectionEntryList();

    for (IdlOpDeleteFieldProjectionEntry fieldProjectionEntryPsi : fieldProjectionEntriesPsi) {
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

  @NotNull
  public static OpDeleteFieldProjection parseFieldProjection(
      @NotNull DataType fieldType,
      @NotNull IdlOpDeleteFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpParams fieldParams;
    @NotNull Annotations fieldAnnotations;

    List<OpParam> fieldParamsList = null;
    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
    for (IdlOpDeleteFieldProjectionBodyPart fieldBodyPart : psi.getOpDeleteFieldProjectionBodyPartList()) {
      @Nullable IdlOpParam fieldParamPsi = fieldBodyPart.getOpParam();
      if (fieldParamPsi != null) {
        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
        fieldParamsList.add(parseParameter(fieldParamPsi, resolver, errors));
      }

      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation(), errors);
    }

    fieldParams = fieldParamsList == null ? OpParams.EMPTY : new OpParams(fieldParamsList);
    fieldAnnotations = fieldAnnotationsMap == null ? Annotations.EMPTY : new Annotations(fieldAnnotationsMap);

    return new OpDeleteFieldProjection(
        fieldParams,
        fieldAnnotations,
        parseVarProjection(fieldType, psi.getOpDeleteVarProjection(), resolver, errors),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static OpDeleteMapModelProjection parseMapModelProjection(
      @NotNull MapType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpDeleteMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull OpDeleteKeyProjection keyProjection = parseKeyProjection(psi.getOpDeleteKeyProjection(), resolver, errors);

    @Nullable IdlOpDeleteVarProjection valueProjectionPsi = psi.getOpDeleteVarProjection();
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

  @NotNull
  private static OpDeleteKeyProjection parseKeyProjection(
      @NotNull IdlOpDeleteKeyProjection keyProjectionPsi,
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

    for (IdlOpDeleteKeyProjectionPart keyPart : keyProjectionPsi.getOpDeleteKeyProjectionPartList()) {
      @Nullable IdlOpParam paramPsi = keyPart.getOpParam();
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

  @NotNull
  public static OpDeleteListModelProjection parseListModelProjection(
      @NotNull ListType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpDeleteListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    OpDeleteVarProjection itemsProjection;
    @Nullable IdlOpDeleteVarProjection opDeleteVarProjectionPsi = psi.getOpDeleteVarProjection();
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

  @NotNull
  public static OpDeletePrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveType type,
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
