package io.epigraph.projections.op.output;

import com.intellij.psi.PsiElement;
import io.epigraph.gdata.GDatum;
import io.epigraph.idl.gdata.IdlGDataPsiParser;
import io.epigraph.idl.TypeRefs;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;

import static io.epigraph.projections.ProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class OpOutputProjectionsPsiParser {

  public static OpOutputVarProjection parseVarProjection(@NotNull DataType dataType,
                                                         @NotNull IdlOpOutputVarProjection psi,
                                                         @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, OpOutputTagProjection> tagProjections = new LinkedHashMap<>();

    @Nullable IdlOpOutputSingleTagProjection singleTagProjectionPsi = psi.getOpOutputSingleTagProjection();

    if (singleTagProjectionPsi != null) {
      boolean isDatum = type.kind() != TypeKind.UNION;

      final OpOutputModelProjection<?> parsedModelProjection;
      final Type.Tag tag = getTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag,
          singleTagProjectionPsi
      );

      @Nullable IdlOpOutputModelProjection modelProjection = singleTagProjectionPsi.getOpOutputModelProjection();
      assert modelProjection != null; // todo when it can be null?

      @NotNull List<IdlOpOutputModelProperty> modelPropertiesPsi =
          singleTagProjectionPsi.getOpOutputModelPropertyList();

      parsedModelProjection = parseModelProjection(
          tag.type,
          isDatum || singleTagProjectionPsi.getPlus() != null,
          parseModelParams(modelPropertiesPsi, typesResolver),
          parseModelAnnotations(modelPropertiesPsi),
          parseModelMetaProjection(tag.type, modelPropertiesPsi, typesResolver),
          modelProjection,
          typesResolver
      );

      tagProjections.put(
          tag.name(),
          new OpOutputTagProjection(
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
          )
      );
    } else {
      @Nullable IdlOpOutputMultiTagProjection multiTagProjection = psi.getOpOutputMultiTagProjection();
      assert multiTagProjection != null;
      // parse list of tags
      @NotNull List<IdlOpOutputMultiTagProjectionItem> tagProjectionPsiList =
          multiTagProjection.getOpOutputMultiTagProjectionItemList();

      for (IdlOpOutputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
        final Type.Tag tag = getTag(type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi);

        final OpOutputModelProjection<?> parsedModelProjection;

        @NotNull DatumType tagType = tag.type;
        @Nullable IdlOpOutputModelProjection modelProjection = tagProjectionPsi.getOpOutputModelProjection();
        assert modelProjection != null; // todo when it can be null?

        @NotNull List<IdlOpOutputModelProperty> modelPropertiesPsi = tagProjectionPsi.getOpOutputModelPropertyList();

        parsedModelProjection = parseModelProjection(
            tagType,
            tagProjectionPsi.getPlus() != null,
            parseModelParams(modelPropertiesPsi, typesResolver),
            parseModelAnnotations(modelPropertiesPsi),
            parseModelMetaProjection(tagType, modelPropertiesPsi, typesResolver),
            modelProjection,
            typesResolver
        );

        tagProjections.put(
            tag.name(),
            new OpOutputTagProjection(
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      }
    }

    // parse tails
    final List<OpOutputVarProjection> tails;
    @Nullable IdlOpOutputVarPolymorphicTail psiTail = psi.getOpOutputVarPolymorphicTail();
    if (psiTail != null) {
      tails = new ArrayList<>();

      @Nullable IdlOpOutputVarSingleTail singleTail = psiTail.getOpOutputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull IdlOpOutputVarProjection psiTailProjection = singleTail.getOpOutputVarProjection();
        @NotNull OpOutputVarProjection tailProjection =
            buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, singleTail);
        tails.add(tailProjection);
      } else {
        @Nullable IdlOpOutputVarMultiTail multiTail = psiTail.getOpOutputVarMultiTail();
        assert multiTail != null;
        for (IdlOpOutputVarMultiTailItem tailItem : multiTail.getOpOutputVarMultiTailItemList()) {
          @NotNull IdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull IdlOpOutputVarProjection psiTailProjection = tailItem.getOpOutputVarProjection();
          @NotNull OpOutputVarProjection tailProjection =
              buildTailProjection(dataType, tailTypeRef, psiTailProjection, typesResolver, tailItem);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return new OpOutputVarProjection(type, tagProjections, tails, EpigraphPsiUtil.getLocation(psi));
  }


  @Nullable
  private static OpParams parseModelParams(
      @NotNull List<IdlOpOutputModelProperty> modelProperties,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    List<OpParam> paramList = null;

    for (IdlOpOutputModelProperty modelProperty : modelProperties) {
      @Nullable IdlOpParam paramPsi = modelProperty.getOpParam();
      if (paramPsi != null) {
        if (paramList == null) paramList = new ArrayList<>(3);
        paramList.add(parseParameter(paramPsi, resolver));
      }
    }

    return paramList == null ? null : new OpParams(paramList);
  }

  @Nullable
  private static Annotations parseModelAnnotations(@NotNull List<IdlOpOutputModelProperty> modelProperties)
      throws PsiProcessingException {

    @Nullable Map<String, Annotation> annotationsMap = null;

    for (IdlOpOutputModelProperty modelProperty : modelProperties)
      annotationsMap = parseAnnotation(annotationsMap, modelProperty.getAnnotation());

    return annotationsMap == null ? null : new Annotations(annotationsMap);
  }

  @Nullable
  private static OpOutputModelProjection<?> parseModelMetaProjection(
      @NotNull DatumType type,
      @NotNull List<IdlOpOutputModelProperty> modelProperties,
      @NotNull TypesResolver resolver
  ) throws PsiProcessingException {

    @Nullable IdlOpOutputModelMeta modelMetaPsi = null;

    for (IdlOpOutputModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi != null)
        throw new PsiProcessingException("Metadata projection should only be specified once", modelProperty);

      modelMetaPsi = modelProperty.getOpOutputModelMeta();
    }

    if (modelMetaPsi != null) {
      @Nullable DatumType metaType = null; // TODO need a way to extract it from 'type'
      if (metaType == null) throw new PsiProcessingException(
          String.format("Type '%s' doesn't have a metadata, metadata projection can't be specified", type.name()),
          modelMetaPsi
      );

      @NotNull IdlOpOutputModelProjection metaProjectionPsi = modelMetaPsi.getOpOutputModelProjection();
      return parseModelProjection(
          metaType,
          modelMetaPsi.getPlus() != null,
          null,
          null,
          null, // TODO what if meta-type has it's own meta-type? meta-meta-type projection should go here
          metaProjectionPsi,
          resolver
      );
    } else return null;
  }

  @NotNull
  private static OpOutputVarProjection buildTailProjection(@NotNull DataType dataType,
                                                           @NotNull IdlTypeRef tailTypeRefPsi,
                                                           @NotNull IdlOpOutputVarProjection psiTailProjection,
                                                           @NotNull TypesResolver typesResolver,
                                                           @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, locationPsi);
    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        psiTailProjection,
        typesResolver
    );
  }


  @NotNull
  private static OpOutputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Type.Tag tag,
      boolean includeInDefault,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {
    return new OpOutputVarProjection(
        type,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpOutputTagProjection(
                createDefaultModelProjection(tag.type, includeInDefault, null, null, locationPsi),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        ),
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  private static OpOutputVarProjection createDefaultVarProjection(@NotNull DatumType type,
                                                                  boolean includeInDefault,
                                                                  @NotNull PsiElement locationPsi)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self, includeInDefault, locationPsi);
  }

  @NotNull
  private static OpOutputVarProjection createDefaultVarProjection(@NotNull DataType type,
                                                                  boolean includeInDefault,
                                                                  @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    @Nullable Type.Tag defaultTag = type.defaultTag;
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', default tag not specified", type.name), locationPsi
      );

    return createDefaultVarProjection(type.type, defaultTag, includeInDefault, locationPsi);
  }

  @NotNull
  public static OpOutputModelProjection<?> parseModelProjection(@NotNull DatumType type,
                                                                boolean includeInDefault,
                                                                @Nullable OpParams params,
                                                                @Nullable Annotations annotations,
                                                                @Nullable OpOutputModelProjection<?> metaProjection,
                                                                @NotNull IdlOpOutputModelProjection psi,
                                                                @NotNull TypesResolver typesResolver)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        @Nullable IdlOpOutputRecordModelProjection recordModelProjectionPsi = psi.getOpOutputRecordModelProjection();
        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(type, includeInDefault, params, annotations, psi);
        ensureModelKind(psi, TypeKind.RECORD);
        return parseRecordModelProjection(
            (RecordType) type,
            includeInDefault,
            params,
            annotations,
            metaProjection,
            recordModelProjectionPsi,
            typesResolver
        );
      case MAP:
        @Nullable IdlOpOutputMapModelProjection mapModelProjectionPsi = psi.getOpOutputMapModelProjection();
        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(type, includeInDefault, params, annotations, psi);
        ensureModelKind(psi, TypeKind.MAP);

        return parseMapModelProjection(
            (MapType) type,
            includeInDefault,
            params,
            annotations,
            metaProjection,
            mapModelProjectionPsi,
            typesResolver
        );
      case LIST:
        @Nullable IdlOpOutputListModelProjection listModelProjectionPsi = psi.getOpOutputListModelProjection();
        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(type, includeInDefault, params, annotations, psi);
        ensureModelKind(psi, TypeKind.LIST);
        return parseListModelProjection(
            (ListType) type,
            includeInDefault,
            params,
            annotations,
            metaProjection,
            listModelProjectionPsi,
            typesResolver
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      case PRIMITIVE:
        return parsePrimitiveModelProjection((PrimitiveType) type,
                                             includeInDefault,
                                             params,
                                             annotations,
                                             metaProjection,
                                             psi
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlOpOutputModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format("Unexpected projection kind ''{0}'', expected ''{1}''",
                                                            actualKind,
                                                            expectedKind
      ), psi);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull IdlOpOutputModelProjection psi) {
    if (psi.getOpOutputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpOutputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpOutputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  private static OpOutputModelProjection<?> createDefaultModelProjection(@NotNull DatumType type,
                                                                         boolean includeInDefault,
                                                                         @Nullable OpParams params,
                                                                         @Nullable Annotations annotations,
                                                                         @NotNull PsiElement locationPsi)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        return new OpOutputRecordModelProjection((RecordType) type,
                                                 includeInDefault,
                                                 params,
                                                 annotations,
                                                 null,
                                                 null,
                                                 EpigraphPsiUtil.getLocation(locationPsi)
        );
      case MAP:
        MapType mapType = (MapType) type;

        final OpOutputKeyProjection keyProjection =
            new OpOutputKeyProjection(OpOutputKeyProjection.Presence.OPTIONAL,
                                      null,
                                      null,
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

        final OpOutputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type,
            defaultValuesTag,
            includeInDefault,
            locationPsi
        );

        return new OpOutputMapModelProjection(mapType,
                                              includeInDefault,
                                              params,
                                              annotations,
                                              null,
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

        final OpOutputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type,
            defaultElementsTag,
            includeInDefault,
            locationPsi
        );

        return new OpOutputListModelProjection(listType,
                                               includeInDefault,
                                               params,
                                               annotations,
                                               null,
                                               itemVarProjection,
                                               EpigraphPsiUtil.getLocation(locationPsi)
        );
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), locationPsi);
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi);
      case PRIMITIVE:
        return new OpOutputPrimitiveModelProjection((PrimitiveType) type,
                                                    includeInDefault,
                                                    params,
                                                    annotations,
                                                    null,
                                                    EpigraphPsiUtil.getLocation(locationPsi)
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi);
    }
  }

  @NotNull
  public static OpOutputRecordModelProjection parseRecordModelProjection(
      @NotNull RecordType type,
      boolean includeInDefault,
      @Nullable OpParams params,
      @Nullable Annotations annotations,
      @Nullable OpOutputModelProjection<?> metaProjection,
      @NotNull IdlOpOutputRecordModelProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    LinkedHashMap<String, OpOutputFieldProjection> fieldProjections = new LinkedHashMap<>();
    @NotNull List<IdlOpOutputFieldProjection> psiFieldProjections = psi.getOpOutputFieldProjectionList();

    for (IdlOpOutputFieldProjection fieldProjectionPsi : psiFieldProjections) {
      final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();
      RecordType.Field field = type.fieldsMap().get(fieldName);
      if (field == null)
        throw new PsiProcessingException(
            String.format("Can't field projection for '%s', field '%s' not found", type.name(), fieldName),
            fieldProjectionPsi
        );

      final boolean includeFieldInDefault = fieldProjectionPsi.getPlus() != null;

      OpParams fieldParams;
      Annotations fieldAnnotations;

      List<OpParam> fieldParamsList = null;
      @Nullable Map<String, Annotation> fieldAnnotationsMap = null;
      for (IdlOpOutputFieldProjectionBodyPart fieldBodyPart : fieldProjectionPsi.getOpOutputFieldProjectionBodyPartList()) {
        @Nullable IdlOpParam fieldParamPsi = fieldBodyPart.getOpParam();
        if (fieldParamPsi != null) {
          if (fieldParamsList == null) fieldParamsList = new ArrayList<>(3);
          fieldParamsList.add(parseParameter(fieldParamPsi, typesResolver));
        }

        fieldAnnotationsMap = parseAnnotation(fieldAnnotationsMap, fieldBodyPart.getAnnotation());
      }

      fieldParams = fieldParamsList == null ? null : new OpParams(fieldParamsList);
      fieldAnnotations = fieldAnnotationsMap == null ? null : new Annotations(fieldAnnotationsMap);

      OpOutputVarProjection varProjection;
      @Nullable IdlOpOutputVarProjection psiVarProjection = fieldProjectionPsi.getOpOutputVarProjection();
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
            createDefaultVarProjection(fieldDataType.type, defaultFieldTag, true, fieldProjectionPsi);
      } else {
        varProjection = parseVarProjection(field.dataType(), psiVarProjection, typesResolver);
      }

      fieldProjections.put(
          fieldName,
          new OpOutputFieldProjection(
              fieldParams,
              fieldAnnotations,
              varProjection,
              includeFieldInDefault,
              EpigraphPsiUtil.getLocation(fieldProjectionPsi)
          )
      );
    }

    return new OpOutputRecordModelProjection(
        type,
        includeInDefault,
        params,
        annotations,
        metaProjection,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static OpOutputMapModelProjection parseMapModelProjection(
      @NotNull MapType type,
      boolean includeInDefault,
      @Nullable OpParams params,
      @Nullable Annotations annotations,
      @Nullable OpOutputModelProjection<?> metaProjection,
      @NotNull IdlOpOutputMapModelProjection psi,
      @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    @NotNull OpOutputKeyProjection keyProjection = parseKeyProjection(psi.getOpOutputKeyProjection(), resolver);

    @Nullable IdlOpOutputVarProjection valueProjectionPsi = psi.getOpOutputVarProjection();
    @NotNull OpOutputVarProjection valueProjection =
        valueProjectionPsi == null
        ? createDefaultVarProjection(type.valueType(),
                                     keyProjection.presence() == OpOutputKeyProjection.Presence.REQUIRED,
                                     psi
        )
        : parseVarProjection(type.valueType(), valueProjectionPsi, resolver);

    return new OpOutputMapModelProjection(
        type,
        includeInDefault,
        params,
        annotations,
        metaProjection,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  private static OpOutputKeyProjection parseKeyProjection(
      @NotNull IdlOpOutputKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    final OpOutputKeyProjection.Presence presence;

    if (keyProjectionPsi.getForbidden() != null)
      presence = OpOutputKeyProjection.Presence.FORBIDDEN;
    else if (keyProjectionPsi.getRequired() != null)
      presence = OpOutputKeyProjection.Presence.REQUIRED;
    else
      presence = OpOutputKeyProjection.Presence.OPTIONAL;

    List<OpParam> params = null;
    @Nullable Map<String, Annotation> annotationsMap = null;

    for (IdlOpOutputKeyProjectionPart keyPart : keyProjectionPsi.getOpOutputKeyProjectionPartList()) {
      @Nullable IdlOpParam paramPsi = keyPart.getOpParam();
      if (paramPsi != null) {
        if (params == null) params = new ArrayList<>(3);
        params.add(parseParameter(paramPsi, resolver));
      }

      annotationsMap = parseAnnotation(annotationsMap, keyPart.getAnnotation());
    }

    return new OpOutputKeyProjection(
        presence,
        params == null ? null : new OpParams(params),
        annotationsMap == null ? null : new Annotations(annotationsMap),
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  @NotNull
  public static OpOutputListModelProjection parseListModelProjection(
      @NotNull ListType type,
      boolean includeInDefault,
      @Nullable OpParams params,
      @Nullable Annotations annotations,
      @Nullable OpOutputModelProjection<?> metaProjection,
      @NotNull IdlOpOutputListModelProjection psi,
      @NotNull TypesResolver resolver)
      throws PsiProcessingException {

    OpOutputVarProjection itemsProjection;
    @Nullable IdlOpOutputVarProjection opOutputVarProjectionPsi = psi.getOpOutputVarProjection();
    if (opOutputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi);
    else
      itemsProjection = parseVarProjection(type.elementType(), opOutputVarProjectionPsi, resolver);


    return new OpOutputListModelProjection(
        type,
        includeInDefault,
        params,
        annotations,
        metaProjection,
        itemsProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static OpOutputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveType type,
      boolean includeInDefault,
      @Nullable OpParams params,
      @Nullable Annotations annotations,
      @Nullable OpOutputModelProjection<?> metaProjection,
      @NotNull PsiElement locationPsi) {

    return new OpOutputPrimitiveModelProjection(type,
                                                includeInDefault,
                                                params,
                                                annotations,
                                                metaProjection,
                                                EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  public static OpParam parseParameter(@NotNull IdlOpParam paramPsi,
                                       @NotNull TypesResolver resolver) throws PsiProcessingException {
    @Nullable IdlQid qid = paramPsi.getQid();
    if (qid == null) throw new PsiProcessingException("Parameter name not specified", paramPsi);
    @NotNull String paramName = qid.getCanonicalName();

    @Nullable IdlTypeRef typeRef = paramPsi.getTypeRef();
    if (typeRef == null)
      throw new PsiProcessingException(String.format("Parameter '%s' type not specified", paramName), paramPsi);
    @NotNull TypeRef paramTypeRef = TypeRefs.fromPsi(typeRef);
    @Nullable DatumType paramType = paramTypeRef.resolveDatumType(resolver);

    if (paramType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve parameter '%s' data type '%s'", paramName, paramTypeRef), paramPsi
      );

    @Nullable IdlOpInputComaModelProjection paramModelProjectionPsi = paramPsi.getOpInputComaModelProjection();
    if (paramModelProjectionPsi == null) // can this ever happen?
      throw new PsiProcessingException(String.format("Parameter '%s' projection", paramName), paramPsi);

    @Nullable Map<String, Annotation> annotationMap = null;
    for (IdlAnnotation annotationPsi : paramPsi.getAnnotationList())
      annotationMap = parseAnnotation(annotationMap, annotationPsi);
    Annotations annotations = annotationMap == null ? null : new Annotations(annotationMap);


    @Nullable IdlDatum defaultValuePsi = paramPsi.getDatum();
    @Nullable GDatum defaultValue = defaultValuePsi == null
                                    ? null
                                    : IdlGDataPsiParser.parseDatum(defaultValuePsi);

    OpInputModelProjection<?, ?> paramModelProjection = OpInputProjectionsPsiParser.parseComaModelProjection(
        paramType,
        paramPsi.getPlus() != null,
        defaultValue,
        annotations,
        null, // TODO do we want to support metadata on parameters?
        paramModelProjectionPsi,
        resolver
    ).projection();

    return new OpParam(paramName, paramModelProjection, EpigraphPsiUtil.getLocation(paramPsi));
  }
}
