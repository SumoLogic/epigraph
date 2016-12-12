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

package ws.epigraph.url.projections.req.input;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.OpKeyPresence;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.input.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.TypeRefs;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.UrlProjectionsPsiParserUtil;

import java.util.*;

import static ws.epigraph.projections.ProjectionsParsingUtil.*;
import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqInputProjectionsPsiParser {

  private ReqInputProjectionsPsiParser() {}

  public static @NotNull ReqInputVarProjection parseVarProjection(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqInputVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    final Type type = dataType.type;
    final LinkedHashMap<String, ReqInputTagProjectionEntry> tagProjections;

    @Nullable UrlReqInputSingleTagProjection singleTagProjectionPsi = psi.getReqInputSingleTagProjection();
    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type, resolver);

    if (singleTagProjectionPsi == null) {
      @Nullable UrlReqInputMultiTagProjection multiTagProjection = psi.getReqInputMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, op, multiTagProjection, subResolver, errors);
    } else {
      // try to improve error reporting: singleTagProjectionPsi may be empty
      PsiElement tagLocation = getSingleTagLocation(singleTagProjectionPsi);
      tagProjections = new LinkedHashMap<>();
      final @Nullable UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      Type.Tag tag = findTagOrDefaultTag(type, tagNamePsi, op, tagLocation, errors);
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        if (tag == null) tag = getTagOrDefaultTag(type, null, op, tagLocation, errors); // will throw proper error

        @NotNull OpInputTagProjectionEntry opTagProjection =
            getTagProjection(tag.name(), op, tagLocation, errors);

        @NotNull OpInputModelProjection<?, ?, ?> opModelProjection = opTagProjection.projection();
        @NotNull UrlReqInputModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqInputModelProjection();

        final ReqInputModelProjection<?, ?> parsedModelProjection = parseModelProjection(
            opModelProjection,
            parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), subResolver, errors),
            parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), errors),
            modelProjectionPsi,
            subResolver,
            errors
        );

        tagProjections.put(
            tag.name(),
            new ReqInputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagLocation)
            )
        );

      }
    }

    // check that all required tags are present
    for (final Map.Entry<String, OpInputTagProjectionEntry> entry : op.tagProjections().entrySet()) {
      if (entry.getValue().projection().required() && !tagProjections.containsKey(entry.getKey()))
        errors.add(
            new PsiProcessingError(String.format("Required tag '%s' is missing", entry.getKey()), psi)
        );
    }

    final List<ReqInputVarProjection> tails =
        parseTails(dataType, op, psi.getReqInputVarPolymorphicTail(), subResolver, errors);

    try {
      return new ReqInputVarProjection(
          type,
          tagProjections,
          singleTagProjectionPsi == null || tagProjections.size() != 1,
          tails,
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (RuntimeException e) {
      throw new PsiProcessingException(e, psi, errors);
    }
  }

  private static @NotNull PsiElement getSingleTagLocation(final @NotNull UrlReqInputSingleTagProjection singleTagProjectionPsi) {
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().isEmpty()) {
      final @Nullable UrlReqInputFieldProjectionEntry fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqInputFieldProjectionEntry.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return tagLocation;
  }

  private static @NotNull LinkedHashMap<String, ReqInputTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqInputMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (!(dataType.type.equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name, op.type().name()),
          psi,
          errors
      );

    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type, typesResolver);

    final LinkedHashMap<String, ReqInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull Iterable<UrlReqInputMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqInputMultiTagProjectionItemList();

    for (UrlReqInputMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull Type.Tag tag =
            UrlProjectionsPsiParserUtil.getTag(tagProjectionPsi.getTagName(), op, tagProjectionPsi, errors);
        @NotNull OpInputTagProjectionEntry opTag = getTagProjection(tag.name(), op, tagProjectionPsi, errors);

        OpInputModelProjection<?, ?, ?> opTagProjection = opTag.projection();

        @NotNull UrlReqInputModelProjection modelProjection = tagProjectionPsi.getReqInputModelProjection();

        final ReqInputModelProjection<?, ?> parsedModelProjection = parseModelProjection(
            opTagProjection,
            parseReqParams(tagProjectionPsi.getReqParamList(), opTagProjection.params(), subResolver, errors),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList(), errors),
            modelProjection, subResolver, errors
        );

        tagProjections.put(
            tag.name(),
            new ReqInputTagProjectionEntry(
                tag,
                parsedModelProjection,
                EpigraphPsiUtil.getLocation(tagProjectionPsi)
            )
        );
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }
    }

    return tagProjections;
  }

  private static @NotNull ReqInputVarProjection getDefaultVarProjection(
      final @NotNull Type type,
      final @NotNull PsiElement psi) {
    return new ReqInputVarProjection(
        type,
        Collections.emptyMap(),
        true,
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @Nullable List<ReqInputVarProjection> parseTails(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @Nullable UrlReqInputVarPolymorphicTail tailPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<ReqInputVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type, resolver);

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable UrlReqInputVarSingleTail singleTail = tailPsi.getReqInputVarSingleTail();
      if (singleTail == null) {
        @Nullable UrlReqInputVarMultiTail multiTail = tailPsi.getReqInputVarMultiTail();
        assert multiTail != null;
        Type prevTailType = null;

        for (UrlReqInputVarMultiTailItem tailItem : multiTail.getReqInputVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, resolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqInputVarProjection psiTailProjection = tailItem.getReqInputVarProjection();
            @NotNull ReqInputVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) {
            errors.add(e.toError());
          }
        }
      } else {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqInputVarProjection psiTailProjection = singleTail.getReqInputVarProjection();
        @NotNull ReqInputVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
        tails.add(tailProjection);
      }

    }

    return tails;
  }

  private static @NotNull ReqInputVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqInputVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, errors);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);

    @Nullable OpInputVarProjection opTail = mergeOpTails(op, tailType);
    if (opTail == null)
      throw new PsiProcessingException(
          String.format("Polymorphic tail for type '%s' is not supported", tailType.name()),
          tailProjectionPsi,
          errors
      );

    return parseVarProjection(
        new DataType(tailType, dataType.defaultTag),
        opTail,
        tailProjectionPsi,
        typesResolver,
        errors
    );
  }

  private static @Nullable OpInputVarProjection mergeOpTails(@NotNull OpInputVarProjection op, @NotNull Type tailType) {
    Iterable<OpInputVarProjection> opTails = op.polymorphicTails();
    if (opTails == null) return null;
    // TODO a deep merge of op projections wrt to tailType is needed here, probably moved into a separate class
    // we simply look for the first fully matching tail for now
    // algo should be: DFS on tails, look for exact match on tailType
    // if found: merge all op tails up the stack into one mega-op-var-projection: squash all tags/fields/params together. Should be OK since they all are supertypes of tailType
    // else null

    for (OpInputVarProjection opTail : opTails) {
      if (opTail.type().equals(tailType)) return opTail;
    }

    return null;
  }

  public static @NotNull ReqInputModelProjection<?, ?> parseModelProjection(
      @NotNull OpInputModelProjection<?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqInputModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    DatumType model = op.model();
    final @NotNull TypesResolver subResolver = addTypeNamespace(model, resolver);

    switch (model.kind()) {
      case RECORD:
        final OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;

        @Nullable UrlReqInputRecordModelProjection recordModelProjectionPsi =
            psi.getReqInputRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(model, opRecord, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, errors);

        return parseRecordModelProjection(
            opRecord,
            params,
            annotations,
            recordModelProjectionPsi,
            subResolver,
            errors
        );

      case MAP:
        final OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;
        @Nullable UrlReqInputMapModelProjection mapModelProjectionPsi = psi.getReqInputMapModelProjection();

        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(model, opMap, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, errors);

        return parseMapModelProjection(
            opMap,
            params,
            annotations,
            mapModelProjectionPsi,
            subResolver,
            errors
        );

      case LIST:
        final OpInputListModelProjection opList = (OpInputListModelProjection) op;
        @Nullable UrlReqInputListModelProjection listModelProjectionPsi =
            psi.getReqInputListModelProjection();

        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(model, opList, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, errors);

        return parseListModelProjection(
            opList,
            params,
            annotations,
            listModelProjectionPsi,
            subResolver,
            errors
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (OpInputPrimitiveModelProjection) op,
            params,
            annotations,
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      default:
        throw new PsiProcessingException("Unknown type kind: " + model.kind(), psi, errors);
    }

  }

  private static @NotNull ReqInputModelProjection<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      @NotNull OpInputModelProjection<?, ?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;
        final Map<String, OpInputFieldProjectionEntry> opFields = opRecord.fieldProjections();

        final @NotNull Map<String, ReqInputFieldProjectionEntry> fields;

        fields = opFields.isEmpty() ? Collections.emptyMap() : new LinkedHashMap<>();

        return new ReqInputRecordModelProjection(
            (RecordType) type,
            params,
            annotations,
            fields,
            location
        );
      case MAP:
        OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;

        if (opMap.keyProjection().presence() == OpKeyPresence.REQUIRED)
          throw new PsiProcessingException(
              String.format("Can't build default projection for '%s': keys are required", type.name()),
              locationPsi,
              errors
          );

        MapType mapType = (MapType) type;
        final ReqInputVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            locationPsi,
            errors
        );

        return new ReqInputMapModelProjection(
            mapType,
            params,
            annotations,
            Collections.emptyList(),
            valueVarProjection,
            location
        );
      case LIST:
        OpInputListModelProjection opList = (OpInputListModelProjection) op;
        ListType listType = (ListType) type;

        final ReqInputVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            locationPsi,
            errors
        );

        return new ReqInputListModelProjection(
            listType,
            params,
            annotations,
            itemVarProjection,
            location
        );
      case UNION:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            errors
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
      case PRIMITIVE:
        return new ReqInputPrimitiveModelProjection(
            (PrimitiveType<?>) type,
            params,
            annotations,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  private static @NotNull ReqInputVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      @NotNull OpInputVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return createDefaultVarProjection(type.type, op, locationPsi, errors);
  }

  private static @NotNull ReqInputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull OpInputVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Type.@Nullable Tag defaultTag = findDefaultTag(type, op, locationPsi, errors);
    List<Type.Tag> tags = defaultTag == null ?
                          Collections.emptyList() :
                          Collections.singletonList(defaultTag);
    return createDefaultVarProjection(type, tags, op, locationPsi, errors);
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqInputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull Iterable<Type.Tag> tags,
      @NotNull OpInputVarProjection op,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, ReqInputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (Type.Tag tag : tags) {
      final OpInputTagProjectionEntry opInputTagProjection = op.tagProjections().get(tag.name());
      if (opInputTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqInputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type,
                    opInputTagProjection.projection(),
                    ReqParams.EMPTY,
                    Annotations.EMPTY,
                    locationPsi,
                    errors
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        );
      }
    }

    return new ReqInputVarProjection(
        type,
        tagProjections,
        op.parenthesized() || tagProjections.size() != 1,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqInputModelProjection psi) {
    if (psi.getReqInputRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqInputMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqInputListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull ReqInputRecordModelProjection parseRecordModelProjection(
      @NotNull OpInputRecordModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqInputRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) {

    final Map<String, ReqInputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();

    for (final UrlReqInputFieldProjectionEntry entryPsi : psi.getReqInputFieldProjectionEntryList()) {
      final @NotNull String fieldName = entryPsi.getQid().getCanonicalName();

      final @Nullable OpInputFieldProjectionEntry opFieldProjectionEntry = op.fieldProjection(fieldName);
      if (opFieldProjectionEntry == null) {
        errors.add(
            new PsiProcessingError(
                String.format(
                    "Field '%s' is not supported by the operation. Supported fields: {%s}",
                    fieldName,
                    String.join(", ", op.fieldProjections().keySet())
                ), entryPsi.getQid()
            )
        );
      } else {
        try {
          final RecordType.Field field = opFieldProjectionEntry.field();
          final @NotNull OpInputFieldProjection opFieldProjection = opFieldProjectionEntry.fieldProjection();
          final @NotNull UrlReqInputFieldProjection fieldProjectionPsi = entryPsi.getReqInputFieldProjection();
          final @NotNull DataType fieldType = field.dataType();

          final ReqInputFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  opFieldProjection,
                  fieldProjectionPsi,
                  resolver,
                  errors
              );

          fieldProjections.put(
              fieldName,
              new ReqInputFieldProjectionEntry(
                  field,
                  fieldProjection,
                  EpigraphPsiUtil.getLocation(fieldProjectionPsi)
              )
          );
        } catch (PsiProcessingException e) {
          errors.add(e.toError());
        }
      }
    }

    // check that all required fields are specified
    for (final Map.Entry<String, OpInputFieldProjectionEntry> entry : op.fieldProjections().entrySet()) {
      if (entry.getValue().fieldProjection().required() && !fieldProjections.containsKey(entry.getKey())) {
        errors.add(
            new PsiProcessingError(String.format("Required field '%s' is missing", entry.getKey()), psi)
        );
      }
    }

    return new ReqInputRecordModelProjection(
        op.model(),
        params,
        annotations,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputFieldProjection parseFieldProjection(
      final DataType fieldType,
      final @NotNull OpInputFieldProjection op,
      final @NotNull UrlReqInputFieldProjection psi,
      final @NotNull TypesResolver resolver, final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), resolver, errors);

    Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), errors);

    @NotNull UrlReqInputVarProjection psiVarProjection = psi.getReqInputVarProjection();
    @NotNull ReqInputVarProjection varProjection =
        parseVarProjection(
            fieldType,
            op.varProjection(),
            psiVarProjection,
            resolver,
            errors
        );

    return new ReqInputFieldProjection(
        fieldParams,
        fieldAnnotations,
        varProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputMapModelProjection parseMapModelProjection(
      @NotNull OpInputMapModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqInputMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final UrlReqInputKeysProjection keysProjectionPsi = psi.getReqInputKeysProjection();

    final OpInputKeyProjection opKeyProjection = op.keyProjection();
    final List<ReqInputKeyProjection> keyProjections;

    if (keysProjectionPsi.getStar() == null) {
      if (opKeyProjection.presence() == OpKeyPresence.FORBIDDEN)
        throw new PsiProcessingException("Map keys are forbidden", keysProjectionPsi, errors);

      final @NotNull Collection<UrlReqInputKeyProjection> keyProjectionsPsi =
          keysProjectionPsi.getReqInputKeyProjectionList();

      keyProjections = new ArrayList<>(keyProjectionsPsi.size());

      for (final UrlReqInputKeyProjection keyProjectionPsi : keyProjectionsPsi) {
        try {
          final @NotNull UrlDatum keyValuePsi = keyProjectionPsi.getDatum();
          final @Nullable Datum keyValue =
              getDatum(keyValuePsi, op.model().keyType(), resolver, "Error processing map key:", errors);

          if (keyValue == null) errors.add(new PsiProcessingError("Null keys are not allowed", keyValuePsi));
          else {
            keyProjections.add(
                new ReqInputKeyProjection(
                    keyValue,
                    parseReqParams(keyProjectionPsi.getReqParamList(), opKeyProjection.params(), resolver, errors),
                    parseAnnotations(keyProjectionPsi.getReqAnnotationList(), errors),
                    EpigraphPsiUtil.getLocation(keyProjectionPsi)
                )
            );
          }
        } catch (PsiProcessingException e) {
          errors.add(e.toError());
        }
      }
    } else {
      keyProjections = null;
      if (opKeyProjection.presence() == OpKeyPresence.REQUIRED)
        throw new PsiProcessingException("Map keys are required", keysProjectionPsi.getStar(), errors);
    }

    final @Nullable UrlReqInputVarProjection elementsVarProjectionPsi = psi.getReqInputVarProjection();
    final @NotNull ReqInputVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      final @NotNull Type type = op.model().valueType.type;
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.model().valueType,
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqInputMapModelProjection(
        op.model(),
        params,
        annotations,
        keyProjections,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputListModelProjection parseListModelProjection(
      @NotNull OpInputListModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqInputListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable UrlReqInputVarProjection elementsVarProjectionPsi = psi.getReqInputVarProjection();

    final @NotNull ReqInputVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      final @NotNull Type type = op.model().elementType().type;
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.model().elementType,
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqInputListModelProjection(
        op.model(),
        params,
        annotations,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqInputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull OpInputPrimitiveModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) {

    return new ReqInputPrimitiveModelProjection(
        op.model(),
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );

  }
}
