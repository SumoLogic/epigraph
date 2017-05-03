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

package ws.epigraph.projections.op.input;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.gdata.SchemaGDataPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.*;
import ws.epigraph.types.TypeKind;
import ws.epigraph.gdata.validation.OpInputGDataValidator;

import java.text.MessageFormat;
import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.findTag;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.getTag;
import static ws.epigraph.projections.SchemaProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OpInputProjectionsPsiParser {

  private OpInputProjectionsPsiParser() {}

  public static OpInputVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpInputVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    final SchemaOpInputNamedVarProjection namedVarProjection = psi.getOpInputNamedVarProjection();
    if (namedVarProjection == null) {
      final SchemaOpInputUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          psi.getOpInputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition",
            psi,
            context.errors()
        );

      return parseUnnamedOrRefVarProjection(
          dataType,
          unnamedOrRefVarProjection,
          typesResolver,
          context
      );
    } else {
      // named var projection
      final String projectionName = namedVarProjection.getQid().getCanonicalName();

      final @Nullable SchemaOpInputUnnamedOrRefVarProjection unnamedOrRefVarProjection =
          namedVarProjection.getOpInputUnnamedOrRefVarProjection();

      if (unnamedOrRefVarProjection == null)
        throw new PsiProcessingException(
            String.format("Incomplete var projection '%s' definition", projectionName),
            psi,
            context.errors()
        );

      final OpInputVarProjection reference = context.referenceContext()
          .varReference(dataType.type(), projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final OpInputVarProjection value = parseUnnamedOrRefVarProjection(
          dataType,
          unnamedOrRefVarProjection,
          typesResolver,
          context
      );

      context.referenceContext()
          .resolve(projectionName, value, EpigraphPsiUtil.getLocation(unnamedOrRefVarProjection), context);

      return reference;
    }
  }

  public static OpInputVarProjection parseUnnamedOrRefVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpInputUnnamedOrRefVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context)
      throws PsiProcessingException {

    final SchemaOpInputVarProjectionRef varProjectionRef = psi.getOpInputVarProjectionRef();
    if (varProjectionRef == null) {
      // usual var projection
      final SchemaOpInputUnnamedVarProjection unnamedVarProjection = psi.getOpInputUnnamedVarProjection();
      if (unnamedVarProjection == null)
        throw new PsiProcessingException("Incomplete var projection definition", psi, context.errors());
      else return parseUnnamedVarProjection(
          dataType,
          unnamedVarProjection,
          typesResolver,
          context
      );
    } else {
      // var projection reference
      final SchemaQid varProjectionRefPsi = varProjectionRef.getQid();
      if (varProjectionRefPsi == null)
        throw new PsiProcessingException(
            "Incomplete var projection definition: name not specified",
            psi,
            context.errors()
        );

      final String projectionName = varProjectionRefPsi.getCanonicalName();
      return context.referenceContext()
          .varReference(dataType.type(), projectionName, true, EpigraphPsiUtil.getLocation(psi));

    }
  }

  public static OpInputVarProjection parseUnnamedVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpInputUnnamedVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    final TypeApi type = dataType.type();
    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections;

    boolean isDatumType = type.kind() != TypeKind.UNION;

    @Nullable SchemaOpInputSingleTagProjection singleTagProjectionPsi = psi.getOpInputSingleTagProjection();
    if (singleTagProjectionPsi == null) {
      @Nullable SchemaOpInputMultiTagProjection multiTagProjection = psi.getOpInputMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, multiTagProjection, typesResolver, context);
    } else {
      tagProjections = new LinkedHashMap<>();
      TagApi tag = findTag(
          type,
          singleTagProjectionPsi.getTagName(),
          dataType.defaultTag(),
          singleTagProjectionPsi,
          context
      );
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        final OpInputModelProjection<?, ?, ?, ?> parsedModelProjection;
        if (tag == null) // will throw proper error
          tag = getTag(
              type,
              singleTagProjectionPsi.getTagName(),
              dataType.defaultTag(),
              singleTagProjectionPsi,
              context
          );

        parsedModelProjection = parseModelProjection(
            tag.type(),
            singleTagProjectionPsi.getPlus() != null || isDatumType, // 'self 'tags on datum projections are required
            singleTagProjectionPsi.getOpInputModelProjection(),
            typesResolver,
            context
        );

        tagProjections.put(
            tag.name(),
            new OpInputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
            )
        );
      }
    }

    final List<OpInputVarProjection> tails =
        parseTails(dataType, psi.getOpInputVarPolymorphicTail(), typesResolver, context);

    try {
      return new OpInputVarProjection(
          type,
          tagProjections,
          singleTagProjectionPsi == null || tagProjections.size() != 1,
          tails,
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (Exception e) {
      throw new PsiProcessingException(e, psi, context);
    }
  }

  private static @NotNull LinkedHashMap<String, OpInputTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaOpInputMultiTagProjection multiTagProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    final LinkedHashMap<String, OpInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<SchemaOpInputMultiTagProjectionItem> tagProjectionPsiList =
        multiTagProjection.getOpInputMultiTagProjectionItemList();

    for (SchemaOpInputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      final TagApi tag =
          getTag(dataType.type(), tagProjectionPsi.getTagName(), dataType.defaultTag(), tagProjectionPsi, context);

      final OpInputModelProjection<?, ?, ?, ?> parsedModelProjection;

      @NotNull DatumTypeApi tagType = tag.type();

      parsedModelProjection = parseModelProjection(
          tagType,
          tagProjectionPsi.getPlus() != null,
          tagProjectionPsi.getOpInputModelProjection(),
          typesResolver,
          context
      );

      tagProjections.put(
          tag.name(),
          new OpInputTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(tagProjectionPsi)
          )
      );
    }

    return tagProjections;
  }

  private static @Nullable List<OpInputVarProjection> parseTails(
      @NotNull DataTypeApi dataType,
      @Nullable SchemaOpInputVarPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    final List<OpInputVarProjection> tails;

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable SchemaOpInputVarSingleTail singleTail = tailPsi.getOpInputVarSingleTail();
      if (singleTail == null) {
        @Nullable SchemaOpInputVarMultiTail multiTail = tailPsi.getOpInputVarMultiTail();
        assert multiTail != null;
        for (SchemaOpInputVarMultiTailItem tailItem : multiTail.getOpInputVarMultiTailItemList()) {
          @NotNull SchemaTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull SchemaOpInputVarProjection psiTailProjection = tailItem.getOpInputVarProjection();
          @NotNull OpInputVarProjection tailProjection =
              buildTailProjection(
                  dataType,
                  tailTypeRef,
                  psiTailProjection,
                  typesResolver,
                  context
              );
          tails.add(tailProjection);
        }
      } else {
        @NotNull SchemaTypeRef typeRefPsi = singleTail.getTypeRef();
        @NotNull SchemaOpInputVarProjection psiTailProjection = singleTail.getOpInputVarProjection();
        @NotNull OpInputVarProjection tailProjection =
            buildTailProjection(dataType, typeRefPsi, psiTailProjection, typesResolver, context);
        tails.add(tailProjection);
      }

    }

    return tails;
  }

  private static @Nullable GDatum getModelDefaultValue(
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    GDatum result = null;
    for (SchemaOpInputModelProperty property : modelProperties) {
      @Nullable SchemaOpInputDefaultValue defaultValuePsi = property.getOpInputDefaultValue();
      if (defaultValuePsi != null) {
        if (result == null) {
          @Nullable SchemaDatum varValuePsi = defaultValuePsi.getDatum();
          if (varValuePsi != null)
            result = SchemaGDataPsiParser.parseDatum(varValuePsi, context);
        } else {
          context.addError("Default value should only be specified once", defaultValuePsi);
        }
      }
    }

    return result;
  }

  private static @NotNull OpParams parseModelParams(
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    return parseParams(
        modelProperties.stream().map(SchemaOpInputModelProperty::getOpParam),
        resolver,
        context
    );
  }

  private static @NotNull Annotations parseModelAnnotations(
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    return parseAnnotations(
        modelProperties.stream().map(SchemaOpInputModelProperty::getAnnotation),
        context
    );
  }

  private static @Nullable OpInputModelProjection<?, ?, ?, ?> parseModelMetaProjection(
      @NotNull DatumTypeApi type,
      @NotNull List<SchemaOpInputModelProperty> modelProperties,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context
  ) throws PsiProcessingException {

    @Nullable SchemaOpInputModelMeta modelMetaPsi = null;

    for (SchemaOpInputModelProperty modelProperty : modelProperties) {
      if (modelMetaPsi == null) {
        modelMetaPsi = modelProperty.getOpInputModelMeta();
      } else {
        context.addError("Metadata projection should only be specified once", modelProperty);
      }
    }

    if (modelMetaPsi != null) {
      @Nullable DatumTypeApi metaType = type.metaType();
      if (metaType == null) {
        context.addError(
            String.format("Type '%s' doesn't have a metadata, metadata projection can't be specified", type.name()),
            modelMetaPsi
        );
      } else {
        @NotNull SchemaOpInputModelProjection metaProjectionPsi = modelMetaPsi.getOpInputModelProjection();
        return parseModelProjection(
            metaType,
            modelMetaPsi.getPlus() != null,
            metaProjectionPsi,
            resolver,
            context
        );
      }
    }

    return null;
  }

  private static @NotNull OpInputVarProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      @NotNull SchemaTypeRef tailTypeRefPsi,
      SchemaOpInputVarProjection psiTailProjection,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull UnionTypeApi tailType = getUnionType(tailTypeRef, typesResolver, tailTypeRefPsi, context);
    checkTailType(tailType, dataType, tailTypeRefPsi, context);
    return parseVarProjection(
        tailType.dataType(dataType.defaultTag()),
        psiTailProjection,
        typesResolver,
        context
    );
  }

  private static @NotNull OpInputVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {
    return new OpInputVarProjection(
        type,
        ProjectionUtils.singletonLinkedHashMap(
            tag.name(),
            new OpInputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    required,
                    null,
                    OpParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    resolver,
                    context
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        ),
        false,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @NotNull OpInputVarProjection createDefaultVarProjection(
      @NotNull DatumTypeApi type,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context)
      throws PsiProcessingException {
    return createDefaultVarProjection(type, type.self(), required, locationPsi, resolver, context);
  }

  private static @NotNull OpInputVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context)
      throws PsiProcessingException {

    @Nullable TagApi defaultTag = type.defaultTag();
    if (defaultTag == null)
      throw new PsiProcessingException(
          String.format("Can't build default projection for '%s', retro tag not specified", type.name()),
          locationPsi,
          context
      );

    return createDefaultVarProjection(type.type(), defaultTag, required, locationPsi, resolver, context);
  }

  public static @NotNull OpInputModelProjection<?, ?, ?, ?> parseModelProjection(
      @NotNull DatumTypeApi type,
      boolean required,
      @NotNull SchemaOpInputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    return parseModelProjection(
        OpInputModelProjection.class,
        type,
        required,
        psi,
        typesResolver,
        context
    );
  }
//
//  public static @NotNull OpInputModelProjection<?, ?, ?, ?> parseModelProjection(
//      @NotNull DatumTypeApi type,
//      boolean required,
//      @Nullable GDatum defaultValue,
//      @NotNull OpParams params,
//      @NotNull Annotations annotations,
//      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
//      @NotNull SchemaOpInputModelProjection psi,
//      @NotNull TypesResolver typesResolver,
//      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {
//
//    return parseModelProjection(
//        OpInputModelProjection.class,
//        type,
//        required,
//        defaultValue,
//        params,
//        annotations,
//        metaProjection,
//        psi.getOpInputUnnamedModelProjection(),
//        typesResolver,
//        context
//    );
//  }
//

// ----------------

  @SuppressWarnings("unchecked")
  private static @NotNull <MP extends OpInputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP parseModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean required,
      @NotNull SchemaOpInputModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context)
      throws PsiProcessingException {

    // this follows `parseVarProjection` logic

    final SchemaOpInputNamedModelProjection namedModelProjection = psi.getOpInputNamedModelProjection();
    if (namedModelProjection == null) {
      final SchemaOpInputUnnamedOrRefModelProjection unnamedOrRefModelProjection =
          psi.getOpInputUnnamedOrRefModelProjection();

      if (unnamedOrRefModelProjection == null)
        throw new PsiProcessingException(
            "Incomplete model projection definition",
            psi,
            context.errors()
        );

      return parseUnnamedOrRefModelProjection(
          modelClass,
          type,
          required,
          unnamedOrRefModelProjection,
          typesResolver,
          context
      );
    } else {
      // named model projection
      final String projectionName = namedModelProjection.getQid().getCanonicalName();

      final @Nullable SchemaOpInputUnnamedOrRefModelProjection unnamedOrRefModelProjection =
          namedModelProjection.getOpInputUnnamedOrRefModelProjection();

      if (unnamedOrRefModelProjection == null)
        throw new PsiProcessingException(
            String.format("Incomplete model projection '%s' definition", projectionName),
            psi,
            context.errors()
        );

      final MP reference = (MP) context.referenceContext()
          .modelReference(type, projectionName, false, EpigraphPsiUtil.getLocation(psi));

      final MP value = parseUnnamedOrRefModelProjection(
          modelClass,
          type,
          required,
          unnamedOrRefModelProjection,
          typesResolver,
          context
      );

      //noinspection rawtypes
      context.referenceContext().<OpInputModelProjection>resolve(
          projectionName,
          value,
          EpigraphPsiUtil.getLocation(unnamedOrRefModelProjection),
          context
      );

      return reference;
    }
  }

  @SuppressWarnings("unchecked")
  private static @NotNull <MP extends OpInputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP parseUnnamedOrRefModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean required,
      @NotNull SchemaOpInputUnnamedOrRefModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context)
      throws PsiProcessingException {

    // this follows `parseUnnamedOrRefVarProjection` logic

    final SchemaOpInputModelProjectionRef modelProjectionRef = psi.getOpInputModelProjectionRef();
    if (modelProjectionRef == null) {
      // usual model projection
      final SchemaOpInputUnnamedModelProjection unnamedModelProjection = psi.getOpInputUnnamedModelProjection();
      if (unnamedModelProjection == null)
        throw new PsiProcessingException("Incomplete model projection definition", psi, context.errors());
      else return parseUnnamedModelProjection(
          modelClass,
          type,
          required,
          unnamedModelProjection,
          typesResolver,
          context
      );
    } else {
      // model projection reference
      final SchemaQid modelProjectionRefPsi = modelProjectionRef.getQid();
      if (modelProjectionRefPsi == null)
        throw new PsiProcessingException(
            "Incomplete model projection definition: name not specified",
            psi,
            context.errors()
        );

      final String projectionName = modelProjectionRefPsi.getCanonicalName();
      return (MP) context.referenceContext().modelReference(
          type,
          projectionName,
          true,
          EpigraphPsiUtil.getLocation(psi)
      );

    }
  }

  @SuppressWarnings("unchecked")
  public static @NotNull <MP extends OpInputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP parseUnnamedModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean required,
      @NotNull SchemaOpInputUnnamedModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    final List<SchemaOpInputModelProperty> modelProperties = psi.getOpInputModelPropertyList();

    final GDatum defaultValue = getModelDefaultValue(modelProperties, context);
    final OpParams params = parseModelParams(modelProperties, typesResolver, context);
    final Annotations annotations = parseModelAnnotations(modelProperties, context);
    final OpInputModelProjection<?, ?, ?, ?> metaProjection =
        parseModelMetaProjection(type, modelProperties, typesResolver, context);

    MP mp = parseUnnamedModelProjection(
        modelClass,
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        psi,
        typesResolver,
        context
    );

    if (defaultValue != null) {
      // todo warn if default value is present and required = true

      final OpInputGDataValidator validator = new OpInputGDataValidator(typesResolver);
      validator.validateDatum(defaultValue, mp);
      validator.errors().forEach(validationError ->
          context.addError(
              validationError.toStringNoTextLocation(),
              validationError.textLocation()
          )
      );
    }

    return mp;
  }


  @SuppressWarnings("unchecked")
  private static @NotNull <MP extends OpInputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP parseUnnamedModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean required,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @NotNull SchemaOpInputUnnamedModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        assert modelClass.isAssignableFrom(OpInputRecordModelProjection.class);
        ensureModelKind(psi, TypeKind.RECORD, context);

        @Nullable SchemaOpInputRecordModelProjection recordModelProjectionPsi =
            psi.getOpInputRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return (MP) createDefaultModelProjection(
              type,
              required,
              defaultValue,
              params,
              annotations,
              psi,
              typesResolver,
              context
          );

        GRecordDatum defaultRecordData = coerceDefault(defaultValue, GRecordDatum.class, psi, context);

        return (MP) parseRecordModelProjection(
            (RecordTypeApi) type,
            required,
            defaultRecordData,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpInputRecordModelProjection.class,
                psi.getOpInputModelPolymorphicTail(),
                typesResolver,
                context
            ),
            recordModelProjectionPsi,
            typesResolver,
            context
        );

      case MAP:
        assert modelClass.isAssignableFrom(OpInputMapModelProjection.class);
        ensureModelKind(psi, TypeKind.MAP, context);

        @Nullable SchemaOpInputMapModelProjection mapModelProjectionPsi = psi.getOpInputMapModelProjection();

        if (mapModelProjectionPsi == null)
          return (MP) createDefaultModelProjection(
              type,
              required,
              defaultValue,
              params,
              annotations,
              psi,
              typesResolver,
              context
          );

        GMapDatum defaultMapData = coerceDefault(defaultValue, GMapDatum.class, psi, context);

        return (MP) parseMapModelProjection(
            (MapTypeApi) type,
            required,
            defaultMapData,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpInputMapModelProjection.class,
                psi.getOpInputModelPolymorphicTail(),
                typesResolver,
                context
            ),
            mapModelProjectionPsi,
            typesResolver,
            context
        );

      case LIST:
        assert modelClass.isAssignableFrom(OpInputListModelProjection.class);
        ensureModelKind(psi, TypeKind.LIST, context);

        @Nullable SchemaOpInputListModelProjection listModelProjectionPsi =
            psi.getOpInputListModelProjection();

        if (listModelProjectionPsi == null)
          return (MP) createDefaultModelProjection(
              type,
              required,
              defaultValue,
              params,
              annotations,
              psi,
              typesResolver,
              context
          );

        GListDatum defaultListData = coerceDefault(defaultValue, GListDatum.class, psi, context);

        return (MP) parseListModelProjection(
            (ListTypeApi) type,
            required,
            defaultListData,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpInputListModelProjection.class,
                psi.getOpInputModelPolymorphicTail(),
                typesResolver,
                context
            ),
            listModelProjectionPsi,
            typesResolver,
            context
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);

      case PRIMITIVE:
        assert modelClass.isAssignableFrom(OpInputPrimitiveModelProjection.class);

        GPrimitiveDatum defaultPrimitiveData =
            coerceDefault(defaultValue, GPrimitiveDatum.class, psi, context);

        return (MP) parsePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            required,
            defaultPrimitiveData,
            params,
            annotations,
            metaProjection,
            parseModelTails(
                OpInputPrimitiveModelProjection.class,
                psi.getOpInputModelPolymorphicTail(),
                typesResolver,
                context
            ),
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);

      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, context);
    }
  }

  @Contract("_, null, _, _ -> null")
  private static @Nullable <MP extends OpInputModelProjection<?, ?, ?, ?>>
  /*@Nullable*/ List<MP> parseModelTails(
      @NotNull Class<MP> modelClass,
      @Nullable SchemaOpInputModelPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    if (tailPsi == null) return null;
    else {
      List<MP> tails = new ArrayList<>();

      final SchemaOpInputModelSingleTail singleTailPsi = tailPsi.getOpInputModelSingleTail();
      if (singleTailPsi == null) {
        final SchemaOpInputModelMultiTail multiTailPsi = tailPsi.getOpInputModelMultiTail();
        assert multiTailPsi != null;
        for (SchemaOpInputModelMultiTailItem tailItemPsi : multiTailPsi.getOpInputModelMultiTailItemList()) {
          tails.add(
              buildModelTailProjection(
                  modelClass,
                  tailItemPsi.getTypeRef(),
                  tailItemPsi.getOpInputModelProjection(),
                  typesResolver,
                  context
              )
          );
        }
      } else {
        tails.add(
            buildModelTailProjection(
                modelClass,
                singleTailPsi.getTypeRef(),
                singleTailPsi.getOpInputModelProjection(),
                typesResolver,
                context
            )
        );
      }
      return tails;
    }
  }

  private static @NotNull <MP extends OpInputModelProjection<?, ?, ?, ?>>
  /*@NotNull*/ MP buildModelTailProjection(
      @NotNull Class<MP> modelClass,
      @NotNull SchemaTypeRef tailTypeRefPsi,
      @NotNull SchemaOpInputModelProjection modelProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, context);
    @NotNull DatumTypeApi tailType = getDatumType(tailTypeRef, typesResolver, tailTypeRefPsi, context);

    return parseModelProjection(
        modelClass,
        tailType,
        false,
        modelProjectionPsi,
        typesResolver,
        context
    );
  }

  private static void ensureModelKind(
      @NotNull SchemaOpInputUnnamedModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (actualKind != null && expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, context);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull SchemaOpInputUnnamedModelProjection psi) {
    if (psi.getOpInputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getOpInputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getOpInputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull OpInputModelProjection<?, ?, ?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      boolean required,
      @Nullable GDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        return new OpInputRecordModelProjection(
            (RecordTypeApi) type,
            required,
            (GRecordDatum) defaultValue,
            params,
            annotations,
            null,
            Collections.emptyMap(),
            null,
            location
        );
      case MAP:
        MapTypeApi mapType = (MapTypeApi) type;

        @NotNull DataTypeApi valueType = mapType.valueType();
        @Nullable TagApi defaultValuesTag = valueType.defaultTag();

        if (defaultValuesTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for map type '%s, as it's value type '%s' doesn't have a retro tag",
              type.name(),
              valueType.name()
          ), locationPsi, context);

        final OpInputVarProjection valueVarProjection = createDefaultVarProjection(
            valueType.type(),
            defaultValuesTag,
            required,
            locationPsi,
            resolver,
            context
        );

        return new OpInputMapModelProjection(
            mapType,
            required,
            (GMapDatum) defaultValue,
            params,
            annotations,
            null,
            new OpInputKeyProjection(
                OpKeyPresence.OPTIONAL,
                OpParams.EMPTY,
                Annotations.EMPTY,
                location
            ),
            valueVarProjection,
            null,
            location
        );
      case LIST:
        ListTypeApi listType = (ListTypeApi) type;
        @NotNull DataTypeApi elementType = listType.elementType();
        @Nullable TagApi defaultElementsTag = elementType.defaultTag();

        if (defaultElementsTag == null)
          throw new PsiProcessingException(String.format(
              "Can't create default projection for list type '%s, as it's element type '%s' doesn't have a retro tag",
              type.name(),
              elementType.name()
          ), locationPsi, context);

        final OpInputVarProjection itemVarProjection = createDefaultVarProjection(
            elementType.type(),
            defaultElementsTag,
            required,
            locationPsi,
            resolver,
            context
        );

        return new OpInputListModelProjection(
            listType,
            required,
            (GListDatum) defaultValue,
            params,
            annotations,
            null,
            itemVarProjection,
            null,
            location
        );
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            context
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, context);
      case PRIMITIVE:
        return new OpInputPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            required,
            (GPrimitiveDatum) defaultValue,
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

  public static @NotNull OpInputRecordModelProjection parseRecordModelProjection(
      @NotNull RecordTypeApi type,
      boolean required,
      @Nullable GRecordDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpInputRecordModelProjection> tails,
      @NotNull SchemaOpInputRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) {

    LinkedHashMap<String, OpInputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<SchemaOpInputFieldProjectionEntry> psiFieldProjections = psi.getOpInputFieldProjectionEntryList();

    for (SchemaOpInputFieldProjectionEntry fieldProjectionEntryPsi : psiFieldProjections) {
      try {
        final String fieldName = fieldProjectionEntryPsi.getQid().getCanonicalName();
        FieldApi field = type.fieldsMap().get(fieldName);
        if (field == null) {
          context.addError(
              String.format("Can't build field projection for '%s', field '%s' not found", type.name(), fieldName),
              fieldProjectionEntryPsi

          );
          continue;
        }

        final @NotNull DataTypeApi fieldType = field.dataType();
        final boolean fieldRequired = fieldProjectionEntryPsi.getPlus() != null;

        final SchemaOpInputFieldProjection fieldProjectionPsi = fieldProjectionEntryPsi.getOpInputFieldProjection();
        if (fieldProjectionPsi == null)
          context.addError("Incomplete field '" + fieldName + "' projection", fieldProjectionEntryPsi);
        else {
          final OpInputFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  fieldRequired,
                  fieldProjectionPsi,
                  resolver,
                  context
              );

          fieldProjections.put(
              fieldName,
              new OpInputFieldProjectionEntry(
                  field,
                  fieldProjection
                  ,
                  EpigraphPsiUtil.getLocation(fieldProjectionEntryPsi)
              )
          );
        }
      } catch (PsiProcessingException e) {
        context.errors().add(e.toError());
      }
    }

    return new OpInputRecordModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        fieldProjections,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpInputFieldProjection parseFieldProjection(
      final DataTypeApi fieldType,
      final boolean required,
      final @NotNull SchemaOpInputFieldProjection psi,
      final @NotNull TypesResolver resolver,
      final @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

//    @NotNull OpParams fieldParams = parseParams(
//        psi.getOpInputFieldProjectionBodyPartList()
//            .stream()
//            .map(SchemaOpInputFieldProjectionBodyPart::getOpParam),
//        resolver,
//        context
//    );
//
//    @NotNull Annotations fieldAnnotations = parseAnnotations(
//        psi.getOpInputFieldProjectionBodyPartList()
//            .stream()
//            .map(SchemaOpInputFieldProjectionBodyPart::getAnnotation),
//        context
//    );

    @NotNull SchemaOpInputVarProjection psiVarProjection = psi.getOpInputVarProjection();
    OpInputVarProjection varProjection = parseVarProjection(fieldType, psiVarProjection, resolver, context);

    return new OpInputFieldProjection(
//        fieldParams,
//        fieldAnnotations,
        varProjection,
        required,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpInputMapModelProjection parseMapModelProjection(
      @NotNull MapTypeApi type,
      boolean required,
      @Nullable GMapDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpInputMapModelProjection> tails,
      @NotNull SchemaOpInputMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    @NotNull OpInputKeyProjection keyProjection = parseKeyProjection(psi.getOpInputKeyProjection(), resolver, context);

    @Nullable SchemaOpInputVarProjection valueProjectionPsi = psi.getOpInputVarProjection();
    @NotNull OpInputVarProjection valueProjection =
        valueProjectionPsi == null ?
        createDefaultVarProjection(type.valueType(), false, psi, resolver, context) :
        parseVarProjection(type.valueType(), valueProjectionPsi, resolver, context);

    return new OpInputMapModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        keyProjection,
        valueProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpInputKeyProjection parseKeyProjection(
      @NotNull SchemaOpInputKeyProjection keyProjectionPsi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    final OpKeyPresence presence;

    if (keyProjectionPsi.getForbidden() != null)
      presence = OpKeyPresence.FORBIDDEN;
    else if (keyProjectionPsi.getRequired() != null)
      presence = OpKeyPresence.REQUIRED;
    else
      presence = OpKeyPresence.OPTIONAL;

    final @NotNull List<SchemaOpInputKeyProjectionPart> keyPartsPsi =
        keyProjectionPsi.getOpInputKeyProjectionPartList();

    final @NotNull OpParams keyParams =
        parseParams(keyPartsPsi.stream().map(SchemaOpInputKeyProjectionPart::getOpParam), resolver, context);
    final @NotNull Annotations keyAnnotations =
        parseAnnotations(keyPartsPsi.stream().map(SchemaOpInputKeyProjectionPart::getAnnotation), context);

    return new OpInputKeyProjection(
        presence,
        keyParams,
        keyAnnotations,
        EpigraphPsiUtil.getLocation(keyProjectionPsi)
    );
  }

  public static @NotNull OpInputListModelProjection parseListModelProjection(
      @NotNull ListTypeApi type,
      boolean required,
      @Nullable GListDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpInputListModelProjection> tails,
      @NotNull SchemaOpInputListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    OpInputVarProjection itemsProjection;
    @Nullable SchemaOpInputVarProjection opInputVarProjectionPsi = psi.getOpInputVarProjection();
    if (opInputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(type, true, psi, resolver, context);
    else
      itemsProjection = parseVarProjection(type.elementType(), opInputVarProjectionPsi, resolver, context);

    return new OpInputListModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        itemsProjection,
        tails,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull OpInputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveTypeApi type,
      boolean required,
      @Nullable GPrimitiveDatum defaultValue,
      @NotNull OpParams params,
      @NotNull Annotations annotations,
      @Nullable OpInputModelProjection<?, ?, ?, ?> metaProjection,
      @Nullable List<OpInputPrimitiveModelProjection> tails,
      @NotNull PsiElement locationPsi) {

    return new OpInputPrimitiveModelProjection(
        type,
        required,
        defaultValue,
        params,
        annotations,
        metaProjection,
        tails,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @SuppressWarnings("unchecked")
  private static @Nullable <D extends GDatum> D coerceDefault(
      @Nullable GDatum defaultValue,
      Class<D> cls,
      @NotNull PsiElement location,
      @NotNull OpInputPsiProcessingContext context) throws PsiProcessingException {

    if (defaultValue == null) return null;
    if (defaultValue instanceof GNullDatum) return null;
    if (defaultValue.getClass().equals(cls))
      return (D) defaultValue;
    throw new PsiProcessingException(
        String.format("Invalid default value '%s', expected to get '%s'", defaultValue, cls.getName()),
        location,
        context
    );
  }
}
