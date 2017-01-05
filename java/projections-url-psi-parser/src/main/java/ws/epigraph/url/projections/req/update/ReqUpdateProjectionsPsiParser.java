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

package ws.epigraph.url.projections.req.update;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.update.*;
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
public final class ReqUpdateProjectionsPsiParser {

  private ReqUpdateProjectionsPsiParser() {}

  public static @NotNull ReqUpdateVarProjection parseVarProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqUpdateVarProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {


    final TypeApi type = dataType.type();
    final LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections;

    @Nullable UrlReqUpdateSingleTagProjection singleTagProjectionPsi = psi.getReqUpdateSingleTagProjection();
    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (singleTagProjectionPsi == null) {
      @Nullable UrlReqUpdateMultiTagProjection multiTagProjection = psi.getReqUpdateMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseMultiTagProjection(dataType, op, multiTagProjection, subResolver, errors);
    } else {
      // try to improve error reporting: singleTagProjectionPsi may be empty
      PsiElement tagLocation = getSingleTagLocation(singleTagProjectionPsi);
      tagProjections = new LinkedHashMap<>();
      final @Nullable UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      TagApi tag = findTagOrDefaultTag(type, tagNamePsi, op, tagLocation, errors);
      if (tag != null || !singleTagProjectionPsi.getText().isEmpty()) {
        if (tag == null) tag = getTagOrDefaultTag(type, null, op, tagLocation, errors); // will throw proper error
        @NotNull OpInputTagProjectionEntry opTagProjection =
            getTagProjection(tag.name(), op, tagLocation, errors);

        @NotNull OpInputModelProjection<?, ?, ?> opModelProjection = opTagProjection.projection();
        @NotNull UrlReqUpdateModelProjection modelProjectionPsi = singleTagProjectionPsi.getReqUpdateModelProjection();

        final ReqUpdateModelProjection<?, ?> parsedModelProjection = parseModelProjection(
            opModelProjection,
            singleTagProjectionPsi.getPlus() != null,
            parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), subResolver, errors),
            parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), errors),
            modelProjectionPsi,
            subResolver,
            errors
        );

        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
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

    final List<ReqUpdateVarProjection> tails =
        parseTails(dataType, op, psi.getReqUpdateVarPolymorphicTail(), subResolver, errors);

    try {
      return new ReqUpdateVarProjection(
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

  private static @NotNull PsiElement getSingleTagLocation(final @NotNull UrlReqUpdateSingleTagProjection singleTagProjectionPsi) {
    PsiElement tagLocation = singleTagProjectionPsi;
    if (tagLocation.getText().isEmpty()) {
      final @Nullable UrlReqUpdateFieldProjectionEntry fieldProjectionPsi =
          PsiTreeUtil.getParentOfType(tagLocation, UrlReqUpdateFieldProjectionEntry.class);
      if (fieldProjectionPsi != null) {
        tagLocation = fieldProjectionPsi.getQid();
      }
    }
    return tagLocation;
  }

  private static @NotNull LinkedHashMap<String, ReqUpdateTagProjectionEntry> parseMultiTagProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlReqUpdateMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (!(dataType.type().equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name(), op.type().name()),
          psi,
          errors
      );

    final @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), typesResolver);

    final LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull Iterable<UrlReqUpdateMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqUpdateMultiTagProjectionItemList();

    for (UrlReqUpdateMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull TagApi tag =
            UrlProjectionsPsiParserUtil.getTag(tagProjectionPsi.getTagName(), op, tagProjectionPsi, errors);
        @NotNull OpInputTagProjectionEntry opTag = getTagProjection(tag.name(), op, tagProjectionPsi, errors);

        OpInputModelProjection<?, ?, ?> opTagProjection = opTag.projection();

        @NotNull UrlReqUpdateModelProjection modelProjection = tagProjectionPsi.getReqUpdateModelProjection();

        final ReqUpdateModelProjection<?, ?> parsedModelProjection = parseModelProjection(
            opTagProjection,
            tagProjectionPsi.getPlus() != null,
            parseReqParams(tagProjectionPsi.getReqParamList(), opTagProjection.params(), subResolver, errors),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList(), errors),
            modelProjection, subResolver, errors
        );

        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
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

  private static @NotNull ReqUpdateVarProjection getDefaultVarProjection(
      final @NotNull TypeApi type,
      final @NotNull PsiElement psi) {
    return new ReqUpdateVarProjection(
        type,
        Collections.emptyMap(),
        true,
        null,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @Nullable List<ReqUpdateVarProjection> parseTails(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @Nullable UrlReqUpdateVarPolymorphicTail tailPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<ReqUpdateVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type(), resolver);

    if (tailPsi == null) tails = null;
    else {
      tails = new ArrayList<>();

      @Nullable UrlReqUpdateVarSingleTail singleTail = tailPsi.getReqUpdateVarSingleTail();
      if (singleTail == null) {
        @Nullable UrlReqUpdateVarMultiTail multiTail = tailPsi.getReqUpdateVarMultiTail();
        assert multiTail != null;
        TypeApi prevTailType = null;

        for (UrlReqUpdateVarMultiTailItem tailItem : multiTail.getReqUpdateVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, resolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqUpdateVarProjection psiTailProjection = tailItem.getReqUpdateVarProjection();
            @NotNull ReqUpdateVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) {
            errors.add(e.toError());
          }
        }
      } else {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqUpdateVarProjection psiTailProjection = singleTail.getReqUpdateVarProjection();
        @NotNull ReqUpdateVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
        tails.add(tailProjection);
      }

    }

    return tails;
  }

  private static @NotNull ReqUpdateVarProjection buildTailProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpInputVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqUpdateVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi, errors);
    @NotNull UnionTypeApi tailType = getUnionType(tailTypeRef, typesResolver, tailTypeRefPsi, errors);

    @Nullable OpInputVarProjection opTail = mergeOpTails(op, tailType);
    if (opTail == null)
      throw new PsiProcessingException(
          String.format("Polymorphic tail for type '%s' is not supported", tailType.name()),
          tailProjectionPsi,
          errors
      );

    return parseVarProjection(
        tailType.dataType(dataType.defaultTag()),
        opTail,
        tailProjectionPsi,
        typesResolver,
        errors
    );
  }

  private static @Nullable OpInputVarProjection mergeOpTails(@NotNull OpInputVarProjection op, @NotNull TypeApi tailType) {
    Iterable<OpInputVarProjection> opTails = op.polymorphicTails();
    if (opTails == null) return null;
    // TODO a deep merge of op projections wrt to tailTypeApi is needed here, probably moved into a separate class
    // we simply look for the first fully matching tail for now
    // algo should be: DFS on tails, look for exact match on tailType
    // if found: merge all op tails up the stack into one mega-op-var-projection: squash all tags/fields/params together. Should be OK since they all are supertypes of tailType
    // else null

    for (OpInputVarProjection opTail : opTails) {
      if (opTail.type().equals(tailType)) return opTail;
    }

    return null;
  }

  public static @NotNull ReqUpdateModelProjection<?, ?> parseModelProjection(
      @NotNull OpInputModelProjection<?, ?, ?> op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    DatumTypeApi model = op.model();
    final @NotNull TypesResolver subResolver = addTypeNamespace(model, resolver);

    switch (model.kind()) {
      case RECORD:
        final OpInputRecordModelProjection opRecord = (OpInputRecordModelProjection) op;

        @Nullable UrlReqUpdateRecordModelProjection recordModelProjectionPsi =
            psi.getReqUpdateRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(model, update, opRecord, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.RECORD, psi, errors);

        return parseRecordModelProjection(
            opRecord,
            update,
            params,
            annotations,
            recordModelProjectionPsi,
            subResolver,
            errors
        );

      case MAP:
        final OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;
        @Nullable UrlReqUpdateMapModelProjection mapModelProjectionPsi = psi.getReqUpdateMapModelProjection();

        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(model, update, opMap, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.MAP, psi, errors);

        return parseMapModelProjection(
            opMap,
            update,
            params,
            annotations,
            mapModelProjectionPsi,
            subResolver,
            errors
        );

      case LIST:
        final OpInputListModelProjection opList = (OpInputListModelProjection) op;
        @Nullable UrlReqUpdateListModelProjection listModelProjectionPsi =
            psi.getReqUpdateListModelProjection();

        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(model, update, opList, params, annotations, psi, errors);

        ensureModelKind(findProjectionKind(psi), TypeKind.LIST, psi, errors);

        return parseListModelProjection(
            opList,
            update,
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

  private static @NotNull ReqUpdateModelProjection<?, ?> createDefaultModelProjection(
      @NotNull DatumTypeApi type,
      boolean update,
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

        final @NotNull Map<String, ReqUpdateFieldProjectionEntry> fields;

        fields = opFields.isEmpty() ? Collections.emptyMap() : new LinkedHashMap<>();

        return new ReqUpdateRecordModelProjection(
            (RecordTypeApi) type,
            update,
            params,
            annotations,
            fields,
            location
        );
      case MAP:
        OpInputMapModelProjection opMap = (OpInputMapModelProjection) op;

        MapTypeApi mapType = (MapTypeApi) type;
        final ReqUpdateVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            update,
            locationPsi,
            errors
        );

        return new ReqUpdateMapModelProjection(
            mapType,
            update,
            params,
            annotations,
            false,
            Collections.emptyList(),
            valueVarProjection,
            location
        );
      case LIST:
        OpInputListModelProjection opList = (OpInputListModelProjection) op;
        ListTypeApi listType = (ListTypeApi) type;

        final ReqUpdateVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            update,
            locationPsi,
            errors
        );

        return new ReqUpdateListModelProjection(
            listType,
            update,
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
        return new ReqUpdatePrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            params,
            annotations,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  private static @NotNull ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull DataTypeApi type,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return createDefaultVarProjection(type.type(), op, required, locationPsi, errors);
  }

  private static @NotNull ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable TagApi defaultTag = findDefaultTag(type, op, locationPsi, errors);
    List<TagApi> tags = defaultTag == null ?
                          Collections.emptyList() :
                          Collections.singletonList(defaultTag);
    return createDefaultVarProjection(type, tags, op, required, locationPsi, errors);
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqUpdateVarProjection createDefaultVarProjection(
      @NotNull TypeApi type,
      @NotNull Iterable<TagApi> tags,
      @NotNull OpInputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, ReqUpdateTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (TagApi tag : tags) {
      final OpInputTagProjectionEntry opInputTagProjection = op.tagProjections().get(tag.name());
      if (opInputTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqUpdateTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type(),
                    required,
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

    return new ReqUpdateVarProjection(
        type,
        tagProjections,
        op.parenthesized() || tagProjections.size() != 1,
        null,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqUpdateModelProjection psi) {
    if (psi.getReqUpdateRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqUpdateMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqUpdateListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  public static @NotNull ReqUpdateRecordModelProjection parseRecordModelProjection(
      @NotNull OpInputRecordModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) {

    final Map<String, ReqUpdateFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();

    for (final UrlReqUpdateFieldProjectionEntry entryPsi : psi.getReqUpdateFieldProjectionEntryList()) {
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
          final FieldApi field = opFieldProjectionEntry.field();
          final @NotNull OpInputFieldProjection opFieldProjection = opFieldProjectionEntry.fieldProjection();
          final @NotNull UrlReqUpdateFieldProjection fieldProjectionPsi = entryPsi.getReqUpdateFieldProjection();
          final @NotNull DataTypeApi fieldType = field.dataType();

          final ReqUpdateFieldProjection fieldProjection =
              parseFieldProjection(
                  fieldType,
                  entryPsi.getPlus() != null,
                  opFieldProjection,
                  fieldProjectionPsi,
                  resolver,
                  errors
              );

          fieldProjections.put(
              fieldName,
              new ReqUpdateFieldProjectionEntry(
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

    return new ReqUpdateRecordModelProjection(
        op.model(),
        update,
        params,
        annotations,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdateFieldProjection parseFieldProjection(
      final DataTypeApi fieldType,
      boolean update,
      final @NotNull OpInputFieldProjection op,
      final @NotNull UrlReqUpdateFieldProjection psi,
      final @NotNull TypesResolver resolver, final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), resolver, errors);

    Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), errors);

    @NotNull UrlReqUpdateVarProjection psiVarProjection = psi.getReqUpdateVarProjection();
    @NotNull ReqUpdateVarProjection varProjection =
        parseVarProjection(
            fieldType,
            op.varProjection(),
            psiVarProjection,
            resolver,
            errors
        );

    return new ReqUpdateFieldProjection(
        fieldParams,
        fieldAnnotations,
        varProjection,
        update,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdateMapModelProjection parseMapModelProjection(
      @NotNull OpInputMapModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @NotNull MapTypeApi model = op.model();

    final @NotNull Collection<UrlReqUpdateKeyProjection> keysPsi =
        psi.getReqUpdateKeysProjection().getReqUpdateKeyProjectionList();

    List<ReqUpdateKeyProjection> keys = new ArrayList<>(keysPsi.size());

    for (final UrlReqUpdateKeyProjection keyPsi : keysPsi) {
      try {
        final @Nullable Datum keyValue =
            getDatum(keyPsi.getDatum(), model.keyType(), resolver, "Error processing map key: ", errors);

        if (keyValue == null) {
          errors.add(new PsiProcessingError("Null keys are not allowed", keyPsi));
        } else {
          keys.add(
              new ReqUpdateKeyProjection(
                  keyValue,
                  parseReqParams(keyPsi.getReqParamList(), op.keyProjection().params(), resolver, errors),
                  parseAnnotations(keyPsi.getReqAnnotationList(), errors),
                  EpigraphPsiUtil.getLocation(keyPsi)
              )
          );
        }
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }
    }

    final @Nullable UrlReqUpdateVarProjection elementsVarProjectionPsi = psi.getReqUpdateVarProjection();
    final @NotNull ReqUpdateVarProjection elementsVarProjection;

    if (elementsVarProjectionPsi == null) {
      final @NotNull TypeApi type = model.valueType().type();
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          model.valueType(),
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqUpdateMapModelProjection(
        model,
        update,
        params,
        annotations,
        psi.getReqUpdateKeysProjection().getPlus() != null,
        keys,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdateListModelProjection parseListModelProjection(
      @NotNull OpInputListModelProjection op,
      boolean update,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqUpdateListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable UrlReqUpdateVarProjection elementsVarProjectionPsi = psi.getReqUpdateVarProjection();

    final @NotNull ReqUpdateVarProjection elementsVarProjection;
    if (elementsVarProjectionPsi == null) {
      final @NotNull TypeApi type = op.model().elementType().type();
      elementsVarProjection = getDefaultVarProjection(type, psi);
    } else {
      elementsVarProjection = parseVarProjection(
          op.model().elementType(),
          op.itemsProjection(),
          elementsVarProjectionPsi,
          resolver,
          errors
      );
    }

    return new ReqUpdateListModelProjection(
        op.model(),
        update,
        params,
        annotations,
        elementsVarProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqUpdatePrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull OpInputPrimitiveModelProjection op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi) {

    return new ReqUpdatePrimitiveModelProjection(
        op.model(),
        params,
        annotations,
        EpigraphPsiUtil.getLocation(locationPsi)
    );

  }
}
