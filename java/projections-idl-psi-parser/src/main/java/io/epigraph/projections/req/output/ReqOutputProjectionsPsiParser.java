package io.epigraph.projections.req.output;

import com.intellij.psi.PsiElement;
import io.epigraph.data.Datum;
import io.epigraph.gdata.GDataToData;
import io.epigraph.gdata.GDatum;
import io.epigraph.idl.gdata.IdlGDataPsiParser;
import io.epigraph.idl.TypeRefs;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.OpParam;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.input.OpInputModelProjection;
import io.epigraph.projections.op.output.*;
import io.epigraph.projections.req.ReqParam;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static io.epigraph.projections.ProjectionPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 * @deprecated Use {@code RequestUrlPsiParser} instead
 */
@Deprecated()
public class ReqOutputProjectionsPsiParser {
  // todo error messages listing supported fields/tags should take parents into account:
  // op : ..bestFriend(id,firstName)~User(profileUrl)
  // req: ..bestFriend(id,firstName)~User(x) -- supported fields are (id, firstName, profileUrl)


  public static StepsAndProjection<ReqOutputVarProjection> parseTrunkVarProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull IdlReqOutputTrunkVarProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, ReqOutputTagProjectionEntry> tagProjections;
    final int steps;
    final boolean parenthesized;

    @Nullable IdlReqOutputTrunkSingleTagProjection singleTagProjectionPsi = psi.getReqOutputTrunkSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();

      final ReqOutputModelProjection<?> parsedModelProjection;
      @Nullable final IdlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      @NotNull final Type.Tag tag;

      tag = findTagOrSingleDefaultTag(type, tagNamePsi, op, singleTagProjectionPsi);
      @NotNull OpOutputTagProjectionEntry opTagProjection = findTagProjection(tag.name(), op, singleTagProjectionPsi);

      @NotNull OpOutputModelProjection<?> opModelProjection = opTagProjection.projection();
      @NotNull IdlReqOutputTrunkModelProjection modelProjectionPsi =
          singleTagProjectionPsi.getReqOutputTrunkModelProjection();

      StepsAndProjection<? extends ReqOutputModelProjection<?>> stepsAndProjection = parseTrunkModelProjection(
          opModelProjection,
          singleTagProjectionPsi.getPlus() != null,
          parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), typesResolver),
          parseAnnotations(singleTagProjectionPsi.getReqAnnotationList()),
          parseModelMetaProjection(opModelProjection, singleTagProjectionPsi.getReqOutputModelMeta(), typesResolver),
          modelProjectionPsi, typesResolver
      );

      parsedModelProjection = stepsAndProjection.projection();
      steps = stepsAndProjection.pathSteps() + 1;

      tagProjections.put(
          tag.name(),
          new ReqOutputTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
          )
      );
      parenthesized = false;

    } else {
      @Nullable IdlReqOutputComaMultiTagProjection multiTagProjection = psi.getReqOutputComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, op, multiTagProjection, typesResolver);
      steps = 0;
      parenthesized = true;
    }

    final List<ReqOutputVarProjection> tails =
        parseTails(dataType, op, psi.getReqOutputVarPolymorphicTail(), typesResolver);

    return new StepsAndProjection<>(
        steps,
        new ReqOutputVarProjection(type, tagProjections, tails, parenthesized, EpigraphPsiUtil.getLocation(psi))
    );
  }

  /**
   * Finds supported tag with a given name in type {@code type} if {@code idlTagName} is not null.
   * <p>
   * Otherwise gets all {@link #findDefaultTags(Type, OpOutputVarProjection, PsiElement) default tags} and, if
   * this collection contains only one element, returns it; otherwise fails.
   */
  @NotNull
  private static Type.Tag findTagOrSingleDefaultTag(
      @NotNull Type type,
      @Nullable IdlTagName idlTagName,
      @NotNull OpOutputVarProjection opOutputVarProjection,
      @NotNull PsiElement location) throws PsiProcessingException {

    if (idlTagName != null) return findTag(type, idlTagName, opOutputVarProjection, location);
    else {
      @NotNull final List<Type.Tag> defaultTags = findDefaultTags(type, opOutputVarProjection, location);
      switch (defaultTags.size()) {
        case 0:
          throw new PsiProcessingException(
              String.format("Can't build projection for type '%s': no tags supported", type.name()),
              location
          );

        case 1:
          return defaultTags.get(0);

        default:
          throw new PsiProcessingException(
              String.format(
                  "Can't build projection for type '%s': more than one default tag supported by the " +
                  "operation: {%s}. Please specify explicitly which one(s) to build",
                  type.name(),
                  defaultTags.stream().map(Type.Tag::name).collect(Collectors.joining(", "))
              ),
              location
          );
      }
    }
  }

  /**
   * Finds supported tag with a given name in type {@code type}
   */
  @NotNull
  private static Type.Tag findTag(
      @NotNull Type type,
      @NotNull IdlTagName idlTagName,
      @NotNull OpOutputVarProjection opOutputVarProjection,
      @NotNull PsiElement location) throws PsiProcessingException {
    final Type.Tag tag;

    @Nullable final IdlQid qid = idlTagName.getQid();

    if (qid == null)
      throw new PsiProcessingException("Tag name is missing", idlTagName);
    else {
      String tagName = qid.getCanonicalName();
      tag = getTag(type, tagName, location);

      if (!opOutputVarProjection.tagProjections().containsKey(tag.name()))
        throw new PsiProcessingException(
            String.format("Tag '%s' is not supported by the operation. Supported tags: {%s}",
                          tagName, listTags(opOutputVarProjection)
            ),
            location
        );

      return tag;
    }
  }

  @NotNull
  private static OpOutputTagProjectionEntry findTagProjection(
      @NotNull String tagName,
      @NotNull OpOutputVarProjection op,
      @NotNull PsiElement location) throws PsiProcessingException {
    @Nullable final OpOutputTagProjectionEntry tagProjection = op.tagProjection(tagName);
    if (tagProjection == null) {
      throw new PsiProcessingException(
          String.format("Tag '%s' is unsupported, supported tags: {%s}", tagName, listTags(op)), location);
    }
    return tagProjection;
  }

  private static String listTags(@NotNull OpOutputVarProjection op) {
    return op.tagProjections().keySet().stream().collect(Collectors.joining(", "));
  }

  @NotNull
  private static Type.Tag findTag(@NotNull Type type, @NotNull String tagName, @NotNull PsiElement location)
      throws PsiProcessingException {

    final Map<@NotNull String, @NotNull ? extends Type.Tag> tagsMap = type.tagsMap();
    final Type.Tag tag = tagsMap.get(tagName);
    if (tag == null)
      throw new PsiProcessingException(
          String.format("Can't find tag '%s' in type '%s'; known tags: {%s}",
                        tagName, type.name(), String.join(", ", tagsMap.keySet())
          ),
          location
      );

    return tag;
  }

  public static StepsAndProjection<ReqOutputVarProjection> parseComaVarProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull IdlReqOutputComaVarProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, ReqOutputTagProjectionEntry> tagProjections;
    final boolean parenthesized;

    @Nullable IdlReqOutputComaSingleTagProjection singleTagProjectionPsi = psi.getReqOutputComaSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();
      final ReqOutputModelProjection<?> parsedModelProjection;

      @NotNull Type.Tag tag =
          findTagOrSingleDefaultTag(type, singleTagProjectionPsi.getTagName(), op, singleTagProjectionPsi);
      @NotNull OpOutputTagProjectionEntry opTagProjection = findTagProjection(tag.name(), op, singleTagProjectionPsi);

      @NotNull OpOutputModelProjection<?> opModelProjection = opTagProjection.projection();

      @NotNull IdlReqOutputComaModelProjection modelProjectionPsi =
          singleTagProjectionPsi.getReqOutputComaModelProjection();

      parsedModelProjection = parseComaModelProjection(
          opModelProjection,
          singleTagProjectionPsi.getPlus() != null,
          parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), typesResolver),
          parseAnnotations(singleTagProjectionPsi.getReqAnnotationList()),
          parseModelMetaProjection(opModelProjection, singleTagProjectionPsi.getReqOutputModelMeta(), typesResolver),
          modelProjectionPsi, typesResolver
      ).projection();

      tagProjections.put(
          tag.name(),
          new ReqOutputTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(singleTagProjectionPsi)
          )
      );
      parenthesized = false;

    } else {
      @Nullable IdlReqOutputComaMultiTagProjection multiTagProjection = psi.getReqOutputComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, op, multiTagProjection, typesResolver);
      parenthesized = true;
    }

    final List<ReqOutputVarProjection> tails =
        parseTails(dataType, op, psi.getReqOutputVarPolymorphicTail(), typesResolver);

    return new StepsAndProjection<>(
        0,
        new ReqOutputVarProjection(type, tagProjections, tails, parenthesized, EpigraphPsiUtil.getLocation(psi))
    );
  }

  @NotNull
  private static LinkedHashMap<String, ReqOutputTagProjectionEntry> parseComaMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull IdlReqOutputComaMultiTagProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    if (!(dataType.type.equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name, op.type().name()),
          psi
      );

    final LinkedHashMap<String, ReqOutputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<IdlReqOutputComaMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqOutputComaMultiTagProjectionItemList();

    for (IdlReqOutputComaMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
//      final Type.Tag tag = tag(dataType.type, tagProjectionPsi.getTagName(), dataType.defaultTag, tagProjectionPsi);
      @NotNull Type.Tag tag = findTag(dataType.type, tagProjectionPsi.getTagName(), op, tagProjectionPsi);
      @NotNull OpOutputTagProjectionEntry opTag = findTagProjection(tag.name(), op, tagProjectionPsi);

      OpOutputModelProjection<?> opTagProjection = opTag.projection();

      final ReqOutputModelProjection<?> parsedModelProjection;

      @NotNull IdlReqOutputComaModelProjection modelProjection = tagProjectionPsi.getReqOutputComaModelProjection();

      parsedModelProjection = parseComaModelProjection(
          opTagProjection,
          tagProjectionPsi.getPlus() != null,
          parseReqParams(tagProjectionPsi.getReqParamList(), opTagProjection.params(), typesResolver),
          parseAnnotations(tagProjectionPsi.getReqAnnotationList()),
          parseModelMetaProjection(opTagProjection, tagProjectionPsi.getReqOutputModelMeta(), typesResolver),
          modelProjection, typesResolver
      ).projection();

      tagProjections.put(
          tag.name(),
          new ReqOutputTagProjectionEntry(
              tag,
              parsedModelProjection,
              EpigraphPsiUtil.getLocation(tagProjectionPsi)
          )
      );
    }

    return tagProjections;
  }

  @Nullable
  private static List<ReqOutputVarProjection> parseTails(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @Nullable IdlReqOutputVarPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    final List<ReqOutputVarProjection> tails;

    if (tailPsi != null) {

      tails = new ArrayList<>();

      @Nullable IdlReqOutputVarSingleTail singleTail = tailPsi.getReqOutputVarSingleTail();
      if (singleTail != null) {
        @NotNull IdlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull IdlReqOutputComaVarProjection psiTailProjection = singleTail.getReqOutputComaVarProjection();
        @NotNull ReqOutputVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, typesResolver);
        tails.add(tailProjection);
      } else {
        @Nullable IdlReqOutputVarMultiTail multiTail = tailPsi.getReqOutputVarMultiTail();
        assert multiTail != null;
        for (IdlReqOutputVarMultiTailItem tailItem : multiTail.getReqOutputVarMultiTailItemList()) {
          @NotNull IdlTypeRef tailTypeRef = tailItem.getTypeRef();
          @NotNull IdlReqOutputComaVarProjection psiTailProjection = tailItem.getReqOutputComaVarProjection();
          @NotNull ReqOutputVarProjection tailProjection =
              buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, typesResolver);
          tails.add(tailProjection);
        }
      }

    } else tails = null;

    return tails;
  }


  @Nullable
  private static ReqOutputModelProjection<?> parseModelMetaProjection(
      @NotNull OpOutputModelProjection<?> op,
      @Nullable IdlReqOutputModelMeta modelMetaPsi,
      @NotNull TypesResolver resolver
  ) throws PsiProcessingException {

    if (modelMetaPsi == null) return null;

    OpOutputModelProjection<?> metaOp = op.metaProjection();

    if (metaOp == null) throw new PsiProcessingException(
        String.format("Meta projection not supported on type '%s'", op.model().name()),
        modelMetaPsi
    );

    // no params/annotations/meta on meta for now

    return parseComaModelProjection(
        metaOp,
        modelMetaPsi.getPlus() != null,
        null,
        null,
        null,
        modelMetaPsi.getReqOutputComaModelProjection(),
        resolver
    ).projection();
  }

  @NotNull
  private static ReqOutputVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull IdlTypeRef tailTypeRefPsi,
      @NotNull IdlReqOutputComaVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi);

    @Nullable OpOutputVarProjection opTail = mergeOpTails(op, tailType);
    if (opTail == null)
      throw new PsiProcessingException(
          String.format("Polymorphic tail for type '%s' is not supported", tailType.name()),
          tailProjectionPsi
      );

    return parseComaVarProjection(
        new DataType(tailType, dataType.defaultTag),
        opTail,
        tailProjectionPsi,
        typesResolver
    ).projection();
  }

  @Nullable
  private static OpOutputVarProjection mergeOpTails(@NotNull OpOutputVarProjection op, @NotNull Type tailType) {
    List<OpOutputVarProjection> opTails = op.polymorphicTails();
    if (opTails == null) return null;
    // TODO a deep merge of op projections wrt to tailType is needed here, probably moved into a separate class
    // we simply look for the first fully matching tail for now
    // algo should be: DFS on tails, look for exact match on tailType
    // if found: merge all op tails up the stack into one mega-op-var-projection: squash all tags/fields/params together. Should be OK since they all are supertypes of tailType
    // else null

    for (OpOutputVarProjection opTail : opTails) {
      if (opTail.type().equals(tailType)) return opTail;
    }

    return null;
  }

  /**
   * Creates default var projection with explicitly specified list of tags
   */
  private static ReqOutputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull List<Type.Tag> tags,
      @NotNull OpOutputVarProjection op, boolean required,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    LinkedHashMap<String, ReqOutputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (Type.Tag tag : tags) {
      final OpOutputTagProjectionEntry opOutputTagProjection = op.tagProjections().get(tag.name());
      if (opOutputTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqOutputTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    tag.type,
                    required,
                    opOutputTagProjection.projection(),
                    null,
                    null,
                    locationPsi
                ),
                EpigraphPsiUtil.getLocation(locationPsi)
            )
        );
      }
    }

    return new ReqOutputVarProjection(
        type,
        tagProjections,
        null,
        tagProjections.size() > 1,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

  @NotNull
  private static ReqOutputVarProjection createDefaultVarProjection(
      @NotNull Type type,
      @NotNull OpOutputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    List<Type.Tag> defaultTags = findDefaultTags(type, op, locationPsi);
    return createDefaultVarProjection(type, defaultTags, op, required, locationPsi);
  }

  /**
   * Finds default tags for a given {@code type}
   * <p>
   * If it's a {@code DatumType}, then default tag is {@code self}, provided that {@code op} contains it.
   * If it's a {@code UnionType}, then all default tags from {@code op} are included.
   */
  @NotNull
  private static List<Type.Tag> findDefaultTags(
      @NotNull Type type,
      @NotNull OpOutputVarProjection op,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    if (type.kind() != TypeKind.UNION) {
      DatumType datumType = (DatumType) type;
      final Type.@NotNull Tag self = datumType.self;
      findTagProjection(self.name(), op, locationPsi); // check that op contains it
      return Collections.singletonList(self);
    }

    final LinkedHashMap<String, OpOutputTagProjectionEntry> opTagProjections = op.tagProjections();

    List<Type.Tag> defaultTags = new ArrayList<>(opTagProjections.size());
    for (Map.Entry<String, OpOutputTagProjectionEntry> entry : opTagProjections.entrySet()) {
      final OpOutputModelProjection<?> opTagProjection = entry.getValue().projection();
      if (opTagProjection.includeInDefault()) {
        String tagName = entry.getKey();
        defaultTags.add(findTag(type, tagName, locationPsi));
      }
    }

    return defaultTags;
  }

  @NotNull
  private static ReqOutputVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      @NotNull OpOutputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    return createDefaultVarProjection(type.type, op, required, locationPsi);
  }

  @NotNull
  private static ReqOutputVarProjection createDefaultVarProjection(
      @NotNull DatumType type,
      @NotNull OpOutputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    return createDefaultVarProjection(type, Collections.singletonList(type.self), op, required, locationPsi);
  }

  @NotNull
  public static StepsAndProjection<? extends ReqOutputModelProjection<?>> parseTrunkModelProjection(
      @NotNull OpOutputModelProjection<?> op,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull IdlReqOutputTrunkModelProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    switch (op.model().kind()) {
      case RECORD:
        @Nullable
        IdlReqOutputTrunkRecordModelProjection trunkRecordProjectionPsi = psi.getReqOutputTrunkRecordModelProjection();

        if (trunkRecordProjectionPsi != null) {
          return parseTrunkRecordModelProjection(
              (OpOutputRecordModelProjection) op,
              required,
              params,
              annotations,
              metaProjection,
              trunkRecordProjectionPsi,
              typesResolver
          );
        } else break;

      case MAP:
        @Nullable
        IdlReqOutputTrunkMapModelProjection trunkMapProjectionPsi = psi.getReqOutputTrunkMapModelProjection();

        if (trunkMapProjectionPsi != null) {
          return parseTrunkMapModelProjection(
              (OpOutputMapModelProjection) op,
              required,
              params,
              annotations,
              metaProjection,
              trunkMapProjectionPsi,
              typesResolver
          );
        } else break;
    }

    // end of path
    return parseComaModelProjection(op, required, params, annotations, metaProjection, psi, typesResolver);

  }

  @NotNull
  public static StepsAndProjection<? extends ReqOutputModelProjection<?>> parseComaModelProjection(
      @NotNull OpOutputModelProjection<?> op,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull IdlReqOutputComaModelProjection psi,
      @NotNull TypesResolver typesResolver) throws PsiProcessingException {

    DatumType model = op.model();
    switch (model.kind()) {
      case RECORD:
        final OpOutputRecordModelProjection opRecord = (OpOutputRecordModelProjection) op;

        @Nullable IdlReqOutputComaRecordModelProjection recordModelProjectionPsi =
            psi.getReqOutputComaRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(model, required, opRecord, params, annotations, psi)
          );

        ensureModelKind(psi, TypeKind.RECORD);

        return parseComaRecordModelProjection(
            opRecord,
            required,
            params,
            annotations,
            metaProjection,
            recordModelProjectionPsi,
            typesResolver
        );

      case MAP:
        final OpOutputMapModelProjection opMap = (OpOutputMapModelProjection) op;
        @Nullable IdlReqOutputComaMapModelProjection mapModelProjectionPsi = psi.getReqOutputComaMapModelProjection();

        if (mapModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(model, required, opMap, params, annotations, psi)
          );

        ensureModelKind(psi, TypeKind.MAP);

        return parseComaMapModelProjection(
            opMap,
            required,
            params,
            annotations,
            metaProjection,
            mapModelProjectionPsi,
            typesResolver
        );

      case LIST:
        final OpOutputListModelProjection opList = (OpOutputListModelProjection) op;
        @Nullable IdlReqOutputComaListModelProjection listModelProjectionPsi =
            psi.getReqOutputComaListModelProjection();

        if (listModelProjectionPsi == null)
          return new StepsAndProjection<>(
              0,
              createDefaultModelProjection(model, required, opList, params, annotations, psi)
          );

        ensureModelKind(psi, TypeKind.LIST);

        return parseListModelProjection(
            opList,
            required,
            params,
            annotations,
            metaProjection,
            listModelProjectionPsi,
            typesResolver
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi);

      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (PrimitiveType) model,
            required,
            params,
            annotations,
            metaProjection,
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi);

      default:
        throw new PsiProcessingException("Unknown type kind: " + model.kind(), psi);
    }
  }

  private static void ensureModelKind(@NotNull IdlReqOutputComaModelProjection psi, @NotNull TypeKind expectedKind)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(
          String.format("Unexpected projection kind '%s', expected '%s'", actualKind, expectedKind),
          psi
      );
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull IdlReqOutputComaModelProjection psi) {
    if (psi.getReqOutputComaRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqOutputComaMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqOutputComaListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  private static ReqOutputModelProjection<?> createDefaultModelProjection(
      @NotNull DatumType type,
      boolean required,
      @NotNull OpOutputModelProjection<?> op,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpOutputRecordModelProjection opRecord = (OpOutputRecordModelProjection) op;
        final @Nullable LinkedHashMap<String, OpOutputFieldProjectionEntry> opFields = opRecord.fieldProjections();

        final LinkedHashMap<String, ReqOutputFieldProjectionEntry> fields;
        if (opFields == null) {
          fields = null;
        } else {
          fields = new LinkedHashMap<>();

          for (Map.Entry<String, OpOutputFieldProjectionEntry> entry : opFields.entrySet()) {
            final OpOutputFieldProjectionEntry opFieldProjectionEntry = entry.getValue();
            @NotNull final OpOutputFieldProjection opFieldProjection = opFieldProjectionEntry.projection();
            if (opFieldProjection.includeInDefault()) {
              final String fieldName = entry.getKey();
              final RecordType.Field field = opFieldProjectionEntry.field();

              fields.put(
                  fieldName,
                  new ReqOutputFieldProjectionEntry(
                      field,
                      new ReqOutputFieldProjection(
                          null,
                          null,
                          createDefaultVarProjection(
                              field.dataType().type,
                              opFieldProjection.projection(),
                              false,
                              locationPsi
                          ),
                          false,
                          TextLocation.UNKNOWN
                      ),
                      TextLocation.UNKNOWN
                  )
              );
            }
          }
        }

        return new ReqOutputRecordModelProjection(
            (RecordType) type,
            required,
            params,
            annotations,
            null,
            fields,
            location
        );
      case MAP:
        OpOutputMapModelProjection opMap = (OpOutputMapModelProjection) op;

        if (opMap.keyProjection().presence() == OpOutputKeyProjection.Presence.REQUIRED)
          throw new PsiProcessingException(
              String.format("Can't build default projection for '%s': keys are required", type.name()),
              locationPsi
          );

        MapType mapType = (MapType) type;
        final ReqOutputVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(), required,
            locationPsi
        );

        return new ReqOutputMapModelProjection(
            mapType,
            required,
            params,
            annotations,
            null,
            null,
            false,
            valueVarProjection,
            location
        );
      case LIST:
        OpOutputListModelProjection opList = (OpOutputListModelProjection) op;
        ListType listType = (ListType) type;

        final ReqOutputVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(), required,
            locationPsi
        );

        return new ReqOutputListModelProjection(
            listType,
            required,
            params,
            annotations,
            null,
            itemVarProjection,
            location
        );
      case UNION:
        throw new PsiProcessingException("Was expecting to get datum model kind, got: " + type.kind(), locationPsi);
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi);
      case PRIMITIVE:
        return new ReqOutputPrimitiveModelProjection(
            (PrimitiveType) type,
            required,
            params,
            annotations,
            null,
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi);
    }
  }

  @NotNull
  public static StepsAndProjection<ReqOutputRecordModelProjection> parseTrunkRecordModelProjection(
      @NotNull OpOutputRecordModelProjection op,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull IdlReqOutputTrunkRecordModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    @Nullable LinkedHashMap<String, OpOutputFieldProjectionEntry> opFields = op.fieldProjections();
    final String fieldName = psi.getQid().getCanonicalName();

    if (opFields == null)
      throw new PsiProcessingException("Fields are not supported by the operation", psi.getQid());

    final OpOutputFieldProjectionEntry opFieldProjectionEntry = opFields.get(fieldName);
    if (opFieldProjectionEntry == null) {
      throw new PsiProcessingException(
          String.format(
              "Unsupported field '%s', supported fields: (%s)",
              fieldName,
              ProjectionUtils.listFields(opFields.keySet())
          ),
          psi.getQid()
      );
    }

    @NotNull final RecordType.Field field = opFieldProjectionEntry.field();
    @NotNull final OpOutputFieldProjection opFieldProjection = opFieldProjectionEntry.projection();
    @NotNull final DataType fieldType = field.dataType();

    @Nullable IdlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
    boolean fieldRequired = psi.getPlus() != null;

    @Nullable LinkedHashMap<String, ReqOutputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    final int steps;

    @NotNull final TextLocation fieldLocation = EpigraphPsiUtil.getLocation(psi);

    if (fieldProjectionPsi == null) {
      @Nullable Type.Tag defaultFieldTag = fieldType.defaultTag;
      if (defaultFieldTag == null)
        throw new PsiProcessingException(String.format(
            "Can't construct default projection for field '%s', as it's type '%s' has no default tag",
            fieldName,
            fieldType.name
        ), psi);

      @NotNull ReqOutputVarProjection varProjection = createDefaultVarProjection(
          fieldType.type,
          opFieldProjection.projection(),
          required,
          psi
      );

      fieldProjections.put(
          fieldName,
          new ReqOutputFieldProjectionEntry(
              field,
              new ReqOutputFieldProjection(
                  null,
                  null,
                  varProjection,
                  fieldRequired,
                  fieldLocation
              ),
              fieldLocation
          )
      );

      // first step = our field, second step = default var. default var projection is a trunk projection, default model projection is a coma projection
      steps = 2;
    } else {
      @NotNull StepsAndProjection<ReqOutputFieldProjection> fieldStepsAndProjection =
          parseTrunkFieldProjection(
              fieldRequired,
              fieldType,
              opFieldProjection,
              fieldProjectionPsi,
              resolver
          );

      fieldProjections.put(
          fieldName,
          new ReqOutputFieldProjectionEntry(
              field,
              fieldStepsAndProjection.projection(),
              EpigraphPsiUtil.getLocation(fieldProjectionPsi)
          )
      );

      steps = fieldStepsAndProjection.pathSteps();
    }

    return new StepsAndProjection<>(
        steps,
        new ReqOutputRecordModelProjection(
            op.model(),
            required,
            params,
            annotations,
            metaProjection,
            fieldProjections,
            fieldLocation
        )
    );
  }

  @NotNull
  public static StepsAndProjection<ReqOutputFieldProjection> parseTrunkFieldProjection(
      boolean required,
      @NotNull DataType fieldType,
      @NotNull OpOutputFieldProjection op,
      @NotNull IdlReqOutputTrunkFieldProjection psi,
      @NotNull TypesResolver resolver
  ) throws PsiProcessingException {

    return parseTrunkFieldProjection(required, fieldType, op.params(), op.projection(), psi, resolver);
  }

  @NotNull
  public static StepsAndProjection<ReqOutputFieldProjection> parseTrunkFieldProjection(
      boolean required,
      @NotNull DataType fieldType,
      @Nullable OpParams opParams,
      @NotNull OpOutputVarProjection opVarProjection,
      @NotNull IdlReqOutputTrunkFieldProjection psi,
      @NotNull TypesResolver resolver
  ) throws PsiProcessingException {

    final int steps;
    final ReqOutputVarProjection varProjection;

    @Nullable IdlReqOutputTrunkVarProjection psiVarProjection = psi.getReqOutputTrunkVarProjection();
    StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
        parseTrunkVarProjection(fieldType, opVarProjection, psiVarProjection, resolver);

    varProjection = stepsAndProjection.projection();
    steps = stepsAndProjection.pathSteps() + 1;

    return new StepsAndProjection<>(
        steps,
        new ReqOutputFieldProjection(
            parseReqParams(psi.getReqParamList(), opParams, resolver),
            parseAnnotations(psi.getReqAnnotationList()),
            varProjection,
            required,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<ReqOutputRecordModelProjection> parseComaRecordModelProjection(
      @NotNull OpOutputRecordModelProjection op,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull IdlReqOutputComaRecordModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    LinkedHashMap<String, ReqOutputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<IdlReqOutputComaFieldProjection> psiFieldProjections = psi.getReqOutputComaFieldProjectionList();

    @Nullable LinkedHashMap<String, OpOutputFieldProjectionEntry> opFieldEntries = op.fieldProjections();

    for (IdlReqOutputComaFieldProjection fieldProjectionPsi : psiFieldProjections) {
      final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();

//      @NotNull RecordType.Field field = findField(op.model(), fieldName, fieldProjectionPsi);
      @Nullable OpOutputFieldProjectionEntry opFieldProjectionEntry =
          opFieldEntries == null ? null : opFieldEntries.get(fieldName);

      if (opFieldProjectionEntry == null)
        throw new PsiProcessingException(
            String.format(
                "Unsupported field '%s', supported fields: (%s)",
                fieldName,
                ProjectionUtils.listFields(opFieldEntries == null ? null : opFieldEntries.keySet())
            ),
            fieldProjectionPsi
        );

      @NotNull final RecordType.Field field = opFieldProjectionEntry.field();
      @NotNull final OpOutputFieldProjection opFieldProjection = opFieldProjectionEntry.projection();

      final boolean fieldRequired = fieldProjectionPsi.getPlus() != null;

      ReqParams fieldParams =
          parseReqParams(fieldProjectionPsi.getReqParamList(), opFieldProjection.params(), resolver);

      Annotations fieldAnnotations = parseAnnotations(fieldProjectionPsi.getReqAnnotationList());

      @Nullable IdlReqOutputComaVarProjection psiVarProjection = fieldProjectionPsi.getReqOutputComaVarProjection();
      @NotNull ReqOutputVarProjection varProjection =
          parseComaVarProjection(
              field.dataType(),
              opFieldProjection.projection(),
              psiVarProjection,
              resolver
          ).projection();

      @NotNull final TextLocation fieldLocation = EpigraphPsiUtil.getLocation(fieldProjectionPsi);
      fieldProjections.put(
          fieldName,
          new ReqOutputFieldProjectionEntry(
              field,
              new ReqOutputFieldProjection(
                  fieldParams,
                  fieldAnnotations,
                  varProjection,
                  fieldRequired,
                  fieldLocation
              ),
              fieldLocation
          )
      );
    }

    return new StepsAndProjection<>(
        0,
        new ReqOutputRecordModelProjection(
            op.model(),
            required,
            params,
            annotations,
            metaProjection,
            fieldProjections,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<ReqOutputMapModelProjection> parseTrunkMapModelProjection(
      @NotNull OpOutputMapModelProjection op,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull IdlReqOutputTrunkMapModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    final List<ReqOutputKeyProjection> keyProjections;

    if (op.keyProjection().presence() == OpOutputKeyProjection.Presence.FORBIDDEN)
      throw new PsiProcessingException("Map keys are forbidden", psi.getDatum());

    @NotNull IdlDatum valuePsi = psi.getDatum();
    @Nullable Datum keyValue = getDatum(valuePsi, op.model().keyType(), resolver, "Error processing map key: ");
    if (keyValue == null) throw new PsiProcessingException("Null keys are not allowed", valuePsi);

    ReqOutputKeyProjection keyProjection = new ReqOutputKeyProjection(
        keyValue,
        parseReqParams(psi.getReqParamList(), op.keyProjection().params(), resolver),
        parseAnnotations(psi.getReqAnnotationList()),
        EpigraphPsiUtil.getLocation(psi)
    );

    final int steps;
    final ReqOutputVarProjection valueProjection;

    @Nullable IdlReqOutputTrunkVarProjection valueProjectionPsi = psi.getReqOutputTrunkVarProjection();
    if (valueProjectionPsi == null) {
      valueProjection = createDefaultVarProjection(op.model().valueType().type, op.itemsProjection(), required, psi);
      // first step = our field, second step = default var. default var projection is a trunk projection, default model projection is a coma projection
      steps = 2;
    } else {
      StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
          parseTrunkVarProjection(op.model().valueType(), op.itemsProjection(), valueProjectionPsi, resolver);

      valueProjection = stepsAndProjection.projection();
      steps = stepsAndProjection.pathSteps() + 1;
    }

    return new StepsAndProjection<>(
        steps,
        new ReqOutputMapModelProjection(
            op.model(),
            required,
            params,
            annotations,
            metaProjection,
            Collections.singletonList(keyProjection),
            psi.getPlus() != null,
            valueProjection,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<ReqOutputMapModelProjection> parseComaMapModelProjection(
      @NotNull OpOutputMapModelProjection op,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull IdlReqOutputComaMapModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    @NotNull IdlReqOutputComaKeysProjection keysProjectionPsi = psi.getReqOutputComaKeysProjection();

    @NotNull final OpOutputKeyProjection opKeyProjection = op.keyProjection();
    final List<ReqOutputKeyProjection> keyProjections;

    if (keysProjectionPsi.getStar() != null) {
      keyProjections = null;
      if (opKeyProjection.presence() == OpOutputKeyProjection.Presence.REQUIRED)
        throw new PsiProcessingException("Map keys are required", keysProjectionPsi.getStar());
    } else {
      if (opKeyProjection.presence() == OpOutputKeyProjection.Presence.FORBIDDEN)
        throw new PsiProcessingException("Map keys are forbidden", keysProjectionPsi);

      final int keysSize = keysProjectionPsi.getReqOutputComaKeyProjectionList().size();
      keyProjections = new ArrayList<>(keysSize);
      for (IdlReqOutputComaKeyProjection keyProjectionPsi : keysProjectionPsi.getReqOutputComaKeyProjectionList()) {

        @NotNull IdlDatum valuePsi = keyProjectionPsi.getDatum();
        @Nullable Datum keyValue = getDatum(valuePsi, op.model().keyType(), resolver, "Error processing map key: ");
        if (keyValue == null) throw new PsiProcessingException("Null keys are not allowed", valuePsi);

        keyProjections.add(
            new ReqOutputKeyProjection(
                keyValue,
                parseReqParams(keyProjectionPsi.getReqParamList(), opKeyProjection.params(), resolver),
                parseAnnotations(keyProjectionPsi.getReqAnnotationList()),
                EpigraphPsiUtil.getLocation(keyProjectionPsi)
            )
        );
      }
    }

    @Nullable IdlReqOutputComaVarProjection valueProjectionPsi = psi.getReqOutputComaVarProjection();
    @NotNull final ReqOutputVarProjection valueProjection;
    if (valueProjectionPsi == null) {
      valueProjection = createDefaultVarProjection(
          op.model().valueType().type,
          op.itemsProjection(),
          false,
          psi
      );
    } else {
      valueProjection = parseComaVarProjection(
          op.model().valueType(),
          op.itemsProjection(),
          valueProjectionPsi,
          resolver
      ).projection();
    }


    return new StepsAndProjection<>(
        0,
        new ReqOutputMapModelProjection(
            op.model(),
            required,
            params,
            annotations,
            metaProjection,
            keyProjections,
            keysProjectionPsi.getPlus() != null,
            valueProjection,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<ReqOutputListModelProjection> parseListModelProjection(
      @NotNull OpOutputListModelProjection op,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull IdlReqOutputComaListModelProjection psi,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    ReqOutputVarProjection itemsProjection;
    @Nullable IdlReqOutputComaVarProjection reqOutputVarProjectionPsi = psi.getReqOutputComaVarProjection();
    if (reqOutputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(op.model().elementType(), op.itemsProjection(), true, psi);
    else
      itemsProjection =
          parseComaVarProjection(
              op.model().elementType(),
              op.itemsProjection(),
              reqOutputVarProjectionPsi,
              resolver
          ).projection();


    return new StepsAndProjection<>(
        0,
        new ReqOutputListModelProjection(
            op.model(),
            required,
            params,
            annotations,
            metaProjection,
            itemsProjection,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static StepsAndProjection<ReqOutputPrimitiveModelProjection> parsePrimitiveModelProjection(
      @NotNull PrimitiveType type,
      boolean required,
      @Nullable ReqParams params,
      @Nullable Annotations annotations,
      @Nullable ReqOutputModelProjection<?> metaProjection,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    return new StepsAndProjection<>(
        0,
        new ReqOutputPrimitiveModelProjection(
            type,
            required,
            params,
            annotations,
            metaProjection,
            EpigraphPsiUtil.getLocation(locationPsi)
        )
    );
  }

  @Nullable
  private static Annotations parseAnnotations(@NotNull List<IdlReqAnnotation> annotationsPsi)
      throws PsiProcessingException {
    Map<String, Annotation> paramMap = null;

    for (IdlReqAnnotation annotation : annotationsPsi) {
      paramMap = parseAnnotation(paramMap, annotation.getAnnotation());
    }

    return paramMap == null ? null : new Annotations(paramMap);
  }

  @Nullable
  private static ReqParams parseReqParams(
      @NotNull List<IdlReqParam> reqParamsPsi,
      @Nullable OpParams opParams,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    if (reqParamsPsi.isEmpty()) return null;

    if (opParams == null)
      throw new PsiProcessingException("Parameters are not supported here", reqParamsPsi.iterator().next());

    Map<String, ReqParam> paramMap = null;

    for (IdlReqParam reqParamPsi : reqParamsPsi)
      paramMap = parseReqParam(paramMap, reqParamPsi, opParams, resolver);

    return paramMap == null ? null : new ReqParams(paramMap);
  }

  @Nullable
  private static Map<String, ReqParam> parseReqParam(
      @Nullable Map<String, ReqParam> reqParamsMap,
      @Nullable IdlReqParam reqParamPsi,
      @NotNull OpParams opParams,
      @NotNull TypesResolver resolver) throws PsiProcessingException {

    if (reqParamPsi != null) {
      if (reqParamsMap == null) reqParamsMap = new HashMap<>();

      String name = reqParamPsi.getQid().getCanonicalName();
      OpParam opParam = opParams.params().get(name);

      if (opParam == null)
        throw new PsiProcessingException(
            String.format(
                "Unsupported parameter '%s', supported parameters: {%s}",
                name,
                String.join(", ", opParams.params().keySet())
            ),
            reqParamPsi.getQid()
        );

      final String errorMsgPrefix = String.format("Error processing parameter '%s' value: ", name);
      OpInputModelProjection<?, ?> projection = opParam.projection();
      @Nullable Datum value = getDatum(reqParamPsi.getDatum(), projection.model(), resolver, errorMsgPrefix);
      if (value == null) value = projection.defaultValue();

      // todo validate value against input projection

      reqParamsMap.put(name, new ReqParam(name, value, EpigraphPsiUtil.getLocation(reqParamPsi)));
    }
    return reqParamsMap;
  }

  @Nullable
  private static Datum getDatum(
      @NotNull IdlDatum datumPsi,
      @NotNull DatumType model,
      @NotNull TypesResolver resolver,
      @NotNull String errorMessagePrefix) throws PsiProcessingException {
    @NotNull GDatum gDatum = IdlGDataPsiParser.parseDatum(datumPsi);
    @Nullable Datum value;

    try {
      value = GDataToData.transform(model, gDatum, resolver).getDatum();
    } catch (GDataToData.ProcessingException e) {
      // try to find element by offset
      int offset = e.location().startOffset() - datumPsi.getTextRange().getStartOffset();
      PsiElement element = datumPsi.findElementAt(offset);
      if (element == null) element = datumPsi;

      throw new PsiProcessingException(
          errorMessagePrefix + e.getMessage(),
          element
      );
    }
    return value;
  }

}
