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
import ws.epigraph.projections.op.OpParam;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

import static ws.epigraph.projections.ProjectionPsiParserUtil.*;
import static ws.epigraph.projections.op.OpParserUtil.parseParameter;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpDeleteProjectionsPsiParser {

  public static OpDeleteVarProjection parseVarProjection(
      @NotNull DataType dataType,
      @NotNull IdlOpDeleteVarProjection psi,
      @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, OpDeleteTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    boolean canDelete = psi.getPlus() != null;
    @Nullable IdlOpDeleteSingleTagProjection singleTagProjectionPsi = psi.getOpDeleteSingleTagProjection();

    if (singleTagProjectionPsi != null) {
      boolean isDatum = type.kind() != TypeKind.UNION;

      final OpDeleteModelProjection<?, ?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi
      );

      @Nullable IdlOpDeleteModelProjection modelProjection = singleTagProjectionPsi.getOpDeleteModelProjection();
      assert modelProjection != null; // todo when it can be null?

      @NotNull List<IdlOpDeleteModelProperty> modelPropertiesPsi =
          singleTagProjectionPsi.getOpDeleteModelPropertyList();

      parsedModelProjection = parseModelProjection(
          tag.type,
          parseModelParams(modelPropertiesPsi, typesResolver),
          parseModelAnnotations(modelPropertiesPsi),
          modelProjection,
          typesResolver
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
        final Type.Tag tag = getTag(type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi);

        final OpDeleteModelProjection<?, ?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpDeleteModelProjection modelProjection = tagProjectionPsi.getOpDeleteModelProjection();
        assert modelProjection != null; // todo when it can be null?

        @NotNull List<IdlOpDeleteModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpDeleteModelPropertyList();

        parsedModelProjection = parseModelProjection(
            tagType,
            parseModelParams(modelPropertiesPsi, typesResolver),
            parseModelAnnotations(modelPropertiesPsi),
            modelProjection,
            typesResolver
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
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpDeleteVarMultiTail multiTail = psiTail.getOpDeleteVarMultiTail();
        assert multiTail != null;
        for (IdlOpDeleteVarMultiTailItem tailItem : multiTail.getOpDeleteVarMultiTailItemList()) {
          @NotNull IdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull IdlOpDeleteVarProjection psiTailProjection = tailItem.getOpDeleteVarProjection();
          @NotNull OpDeleteVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return new OpDeleteVarProjection(type, canDelete, tagProjections, tails, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  private static OpParams parseModelParams(
      @NotNull List<IdlOpDeleteModelProperty> modelProperties,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> paramList = null;

    for (IdlOpDeleteModelProperty modelProperty : modelProperties) {
      @Nullable IdlOpParam paramPsi = modelProperty.getOpParam();
      if (paramPsi != null) {
        if (paramList == null) paramList = new ArrayList<>(3);
        paramList.add(parseParameter(paramPsi, resolver));
      }
    }

    return paramList == null ? OpParams.EMPTY : new OpParams(paramList);
  }

  @NotNull
  private static Annotations parseModelAnnotations(@NotNull List<IdlOpDeleteModelProperty> modelProperties)
      throws PsiProcessingException {

    @Nullable Map<String, Annotation> annotationsMap = null;

    for (IdlOpDeleteModelProperty modelProperty : modelProperties)
      annotationsMap = parseAnnotation(annotationsMap, modelProperty.getAnnotation());

    return annotationsMap == null ? Annotations.EMPTY : new Annotations(annotationsMap);
  }

  @NotNull
  private static OpDeleteVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull IdlTypeRef tailTypeRefPsi,
      @NotNull IdlOpDeleteVarProjection psiTailProjection,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi);
    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver
    );
  }

  @NotNull
  private static OpDeleteVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      boolean canDelete,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {
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
                    locationPsi
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
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, canDelete, locationPsi);
  }

  @NotNull
  private static OpDeleteVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      boolean canDelete,
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name), locationPsi
      );

    return createDefaultVarProjection(type.type, defaultTag, canDelete, locationPsi);
  }

  @NotNull
  public static OpDeleteModelProjection<?, ?> parseModelProjection(
      @NotNull DatumType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpDeleteModelProjection psi,
      @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpDeleteRecordModelProjection recordModelProjectionPsi = psi.getOpDeleteRecordModelProjection();
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
        @Nullable IdlOpDeleteMapModelProjection mapModelProjectionPsi = psi.getOpDeleteMapModelProjection();
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
        @Nullable IdlOpDeleteListModelProjection listModelProjectionPsi = psi.getOpDeleteListModelProjection();
        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(type, params, annotations, psi);
        ensureModelKind(psi, TypeKind.LIST);

        return parseListModelProjection(
            (ListType) type,
            params,
            annotations,
            listModelProjectionPsi,
            typesResolver
        );
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

  private static void ensureModelKind(@NotNull IdlOpDeleteModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    // todo move to common
    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi);
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
      @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

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
                OpDeleteKeyProjection.Presence.OPTIONAL,
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

        final OpDeleteVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            false,
            locationPsi
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
          ), locationPsi);

        final OpDeleteVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultElementsTag,
            false,
            locationPsi
        );

        return new OpDeleteListModelProjection(
            listType,
            params,
            annotations,
            itemVarProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        );
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), locationPsi);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi);
      case PRIMITIVE:
        return new OpDeletePrimitiveModelProjection(
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
  public static OpDeleteRecordModelProjection parseRecordModelProjection(
      @NotNull RecordType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpDeleteRecordModelProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    LinkedHashMap<String, OpDeleteFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<IdlOpDeleteFieldProjectionEntry> fieldProjectionEntriesPsi =
        psi.getOpDeleteFieldProjectionEntryList();

    for (IdlOpDeleteFieldProjectionEntry fieldProjectionEntryPsi : fieldProjectionEntriesPsi) {
      final String fieldName = fieldProjectionEntryPsi.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);

      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            fieldProjectionEntryPsi
        );

      fieldProjections.put(
          fieldName,
          new OpDeleteFieldProjectionEntry(
              field,
              parseFieldProjection(
                  field.dataType(),
                  fieldProjectionEntryPsi.getOpDeleteFieldProjection(),
                  typesResolver
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
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    @NotNull OpParams fieldParams;
    @NotNull Annotations fieldAnnotations;

    List<OpParam> fieldParamsList = null;
    @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
    for (IdlOpDeleteFieldProjectionBodyPart fieldBodyPart : psi.getOpDeleteFieldProjectionBodyPartList()) {
      @Nullable IdlOpParam fieldParamPsi = fieldBodyPart.getOpParam();
      if (fieldParamPsi != null) {
        if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
        fieldParamsList.add(parseParameter(fieldParamPsi, resolver));
      }

      fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation());
    }

    fieldParams = fieldParamsList == null ? OpParams.EMPTY : new OpParams(fieldParamsList);
    fieldAnnotations = fieldAnnotationsMap == null ? Annotations.EMPTY : new Annotations(fieldAnnotationsMap);

    final OpDeleteVarProjection varProjection;

    @Nullable IdlOpDeleteVarProjection varProjectionPsi = psi.getOpDeleteVarProjection();

    if (varProjectionPsi == null) {
      @Nullable Type.Tag defaultFieldTag = fieldType.defaultTag;
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for type '%s' because it has no default tag",
            fieldType.name
        ), psi);

      varProjection =
          createDefaultVarProjection(fieldType, true, psi);
    } else {
      varProjection = parseVarProjection(fieldType, varProjectionPsi, resolver);
    }

    return new OpDeleteFieldProjection(
        fieldParams,
        fieldAnnotations,
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static OpDeleteMapModelProjection parseMapModelProjection(
      @NotNull MapType type,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull IdlOpDeleteMapModelProjection psi,
      @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    @NotNull OpDeleteKeyProjection keyProjection = parseKeyProjection(psi.getOpDeleteKeyProjection(), resolver);

    @Nullable IdlOpDeleteVarProjection valueProjectionPsi = psi.getOpDeleteVarProjection();
    @NotNull OpDeleteVarProjection valueProjection =
        valueProjectionPsi == null
        ? createDefaultVarProjection(
            type.valueType(),
            keyProjection.presence() == OpDeleteKeyProjection.Presence.REQUIRED,
            psi
        )
        : parseVarProjection(type.valueType(), valueProjectionPsi, resolver);

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
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    final OpDeleteKeyProjection.Presence presence;

    if (keyProjectionPsi.getForbidden() != null)
      presence = OpDeleteKeyProjection.Presence.FORBIDDEN;
    else if (keyProjectionPsi.getRequired() != null)
      presence = OpDeleteKeyProjection.Presence.REQUIRED;
    else
      presence = OpDeleteKeyProjection.Presence.OPTIONAL;

    List<OpParam> params = null;
    @Nullable Map<String, Annotation> annotationsMap = null;

    for (IdlOpDeleteKeyProjectionPart keyPart : keyProjectionPsi.getOpDeleteKeyProjectionPartList()) {
      @Nullable IdlOpParam paramPsi = keyPart.getOpParam();
      if (paramPsi != null) {
        if (params == null) params = new ArrayList<>(3);
        params.add(parseParameter(paramPsi, resolver));
      }

      annotationsMap = parseAnnotation(annotationsMap, keyPart.getAnnotation());
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
      @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    OpDeleteVarProjection itemsProjection;
    @Nullable IdlOpDeleteVarProjection opDeleteVarProjectionPsi = psi.getOpDeleteVarProjection();
    if (opDeleteVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi);
    else
      itemsProjection = parseVarProjection(type.elementType(), opDeleteVarProjectionPsi, resolver);


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
