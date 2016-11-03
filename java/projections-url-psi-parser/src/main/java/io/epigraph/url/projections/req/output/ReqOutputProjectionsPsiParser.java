package io.epigraph.url.projections.req.output;

import com.intellij.psi.PsiElement;
import io.epigraph.data.Datum;
import io.epigraph.lang.TextLocation;
import io.epigraph.projections.Annotations;
import io.epigraph.projections.ProjectionUtils;
import io.epigraph.projections.StepsAndProjection;
import io.epigraph.projections.op.OpParams;
import io.epigraph.projections.op.output.*;
import io.epigraph.projections.req.ReqParams;
import io.epigraph.projections.req.output.*;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import io.epigraph.refs.TypesResolver;
import io.epigraph.types.*;
import io.epigraph.url.TypeRefs;
import io.epigraph.url.parser.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static io.epigraph.url.projections.UrlProjectionsPsiParserUtil.getTag;
import static io.epigraph.url.projections.UrlProjectionsPsiParserUtil.getType;
import static io.epigraph.url.projections.req.ReqParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReqOutputProjectionsPsiParser {
  // todo error messages listing supported fields/tags should take parents into account:
  // op : ..bestFriend(id,firstName)~User(profileUrl)
  // req: ..bestFriend(id,firstName)~User(x) -- supported fields are (id, firstName, profileUrl)


  @NotNull
  public static StepsAndProjection<ReqOutputVarProjection> parseTrunkVarProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull UrlReqOutputTrunkVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, ReqOutputTagProjectionEntry> tagProjections;
    final int steps;
    final boolean parenthesized;

    @NotNull final TypesResolver subResolver = addTypeNamespace(dataType.type, typesResolver);

    @Nullable UrlReqOutputTrunkSingleTagProjection singleTagProjectionPsi = psi.getReqOutputTrunkSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();

      final ReqOutputModelProjection<?, ?> parsedModelProjection;
      @Nullable final UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();

      @NotNull final Type.Tag tag;

      tag = findTagOrSingleDefaultTag(type, tagNamePsi, op, singleTagProjectionPsi, errors);
      @NotNull OpOutputTagProjectionEntry opTagProjection =
          findTagProjection(tag.name(), op, singleTagProjectionPsi, errors);

      @NotNull OpOutputModelProjection<?, ?> opModelProjection = opTagProjection.projection();
      @NotNull UrlReqOutputTrunkModelProjection modelProjectionPsi =
          singleTagProjectionPsi.getReqOutputTrunkModelProjection();

      StepsAndProjection<? extends ReqOutputModelProjection<?, ?>> stepsAndProjection = parseTrunkModelProjection(
          opModelProjection,
          singleTagProjectionPsi.getPlus() != null,
          parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), subResolver, errors),
          parseAnnotations(singleTagProjectionPsi.getReqAnnotationList()),
          parseModelMetaProjection(
              opModelProjection,
              singleTagProjectionPsi.getReqOutputModelMeta(),
              subResolver,
              errors
          ),
          modelProjectionPsi,
          subResolver,
          errors
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
      @Nullable UrlReqOutputComaMultiTagProjection multiTagProjection = psi.getReqOutputComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, op, multiTagProjection, subResolver, errors);
      steps = 0;
      parenthesized = true;
    }

    final List<ReqOutputVarProjection> tails =
        parseTails(dataType, op, psi.getReqOutputVarPolymorphicTail(), subResolver, errors);

    return new StepsAndProjection<>(
        steps,
        new ReqOutputVarProjection(type, tagProjections, tails, parenthesized, EpigraphPsiUtil.getLocation(psi))
    );
  }

  /**
   * Finds supported tag with a given name in type {@code type} if {@code idlTagName} is not null.
   * <p>
   * Otherwise gets all {@link #findDefaultTags(Type, OpOutputVarProjection, PsiElement, List)}  default tags} and, if
   * this collection contains only one element, returns it; otherwise fails.
   */
  @NotNull
  private static Type.Tag findTagOrSingleDefaultTag(
      @NotNull Type type,
      @Nullable UrlTagName idlTagName,
      @NotNull OpOutputVarProjection opOutputVarProjection,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (idlTagName != null) return findTag(type, idlTagName, opOutputVarProjection, location, errors);
    else {
      @NotNull final List<Type.Tag> defaultTags = findDefaultTags(type, opOutputVarProjection, location, errors);
      switch (defaultTags.size()) {
        case 0:
          throw new PsiProcessingException(
              String.format("Can't build projection for type '%s': no tags supported", type.name()),
              location,
              errors
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
              location,
              errors
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
      @NotNull UrlTagName idlTagName,
      @NotNull OpOutputVarProjection opOutputVarProjection,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    final Type.Tag tag;

    @Nullable final UrlQid qid = idlTagName.getQid();

    String tagName = qid.getCanonicalName();
    tag = getTag(type, tagName, location);

    if (!opOutputVarProjection.tagProjections().containsKey(tag.name()))
      throw new PsiProcessingException(
          String.format("Tag '%s' is not supported by the operation. Supported tags: {%s}",
                        tagName, listTags(opOutputVarProjection)
          ),
          location,
          errors
      );

    return tag;
  }

  @NotNull
  private static OpOutputTagProjectionEntry findTagProjection(
      @NotNull String tagName,
      @NotNull OpOutputVarProjection op,
      @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    @Nullable final OpOutputTagProjectionEntry tagProjection = op.tagProjection(tagName);
    if (tagProjection == null) {
      throw new PsiProcessingException(
          String.format("Tag '%s' is unsupported, supported tags: {%s}", tagName, listTags(op)), location, errors);
    }
    return tagProjection;
  }

  private static String listTags(@NotNull OpOutputVarProjection op) {
    return op.tagProjections().keySet().stream().collect(Collectors.joining(", "));
  }

  @NotNull
  private static Type.Tag findTag(
      @NotNull Type type, @NotNull String tagName, @NotNull PsiElement location,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Map<@NotNull String, @NotNull ? extends Type.Tag> tagsMap = type.tagsMap();
    final Type.Tag tag = tagsMap.get(tagName);
    if (tag == null)
      throw new PsiProcessingException(
          String.format("Can't find tag '%s' in type '%s'; known tags: {%s}",
                        tagName, type.name(), String.join(", ", tagsMap.keySet())
          ),
          location,
          errors
      );

    return tag;
  }

  public static StepsAndProjection<ReqOutputVarProjection> parseComaVarProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull UrlReqOutputComaVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final Type type = dataType.type;
    final LinkedHashMap<String, ReqOutputTagProjectionEntry> tagProjections;
    final boolean parenthesized;

    @NotNull final TypesResolver subResolver = addTypeNamespace(dataType.type, typesResolver);

    @Nullable UrlReqOutputComaSingleTagProjection singleTagProjectionPsi = psi.getReqOutputComaSingleTagProjection();
    if (singleTagProjectionPsi != null) {
      tagProjections = new LinkedHashMap<>();
      final ReqOutputModelProjection<?, ?> parsedModelProjection;

      @NotNull Type.Tag tag =
          findTagOrSingleDefaultTag(type, singleTagProjectionPsi.getTagName(), op, singleTagProjectionPsi, errors);
      @NotNull OpOutputTagProjectionEntry opTagProjection =
          findTagProjection(tag.name(), op, singleTagProjectionPsi, errors);

      @NotNull OpOutputModelProjection<?, ?> opModelProjection = opTagProjection.projection();

      @NotNull UrlReqOutputComaModelProjection modelProjectionPsi =
          singleTagProjectionPsi.getReqOutputComaModelProjection();

      parsedModelProjection = parseComaModelProjection(
          opModelProjection,
          singleTagProjectionPsi.getPlus() != null,
          parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelProjection.params(), subResolver, errors),
          parseAnnotations(singleTagProjectionPsi.getReqAnnotationList()),
          parseModelMetaProjection(
              opModelProjection,
              singleTagProjectionPsi.getReqOutputModelMeta(),
              subResolver,
              errors
          ),
          modelProjectionPsi, subResolver, errors
      );

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
      @Nullable UrlReqOutputComaMultiTagProjection multiTagProjection = psi.getReqOutputComaMultiTagProjection();
      assert multiTagProjection != null;
      tagProjections = parseComaMultiTagProjection(dataType, op, multiTagProjection, subResolver, errors);
      parenthesized = true;
    }

    final List<ReqOutputVarProjection> tails =
        parseTails(dataType, op, psi.getReqOutputVarPolymorphicTail(), subResolver, errors);

    return new StepsAndProjection<>(
        0,
        new ReqOutputVarProjection(type, tagProjections, tails, parenthesized, EpigraphPsiUtil.getLocation(psi))
    );
  }

  @NotNull
  private static LinkedHashMap<String, ReqOutputTagProjectionEntry> parseComaMultiTagProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull UrlReqOutputComaMultiTagProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (!(dataType.type.equals(op.type())))
      throw new PsiProcessingException(
          String.format("Inconsistent arguments. data type: '%s', op type: '%s'", dataType.name, op.type().name()),
          psi,
          errors
      );

    @NotNull final TypesResolver subResolver = addTypeNamespace(dataType.type, typesResolver);

    final LinkedHashMap<String, ReqOutputTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    // parse list of tags
    @NotNull List<UrlReqOutputComaMultiTagProjectionItem> tagProjectionPsiList =
        psi.getReqOutputComaMultiTagProjectionItemList();

    for (UrlReqOutputComaMultiTagProjectionItem tagProjectionPsi : tagProjectionPsiList) {
      try {
        @NotNull Type.Tag tag = findTag(dataType.type, tagProjectionPsi.getTagName(), op, tagProjectionPsi, errors);
        @NotNull OpOutputTagProjectionEntry opTag = findTagProjection(tag.name(), op, tagProjectionPsi, errors);

        OpOutputModelProjection<?, ?> opTagProjection = opTag.projection();

        final ReqOutputModelProjection<?, ?> parsedModelProjection;

        @NotNull UrlReqOutputComaModelProjection modelProjection = tagProjectionPsi.getReqOutputComaModelProjection();

        parsedModelProjection = parseComaModelProjection(
            opTagProjection,
            tagProjectionPsi.getPlus() != null,
            parseReqParams(tagProjectionPsi.getReqParamList(), opTagProjection.params(), subResolver, errors),
            parseAnnotations(tagProjectionPsi.getReqAnnotationList()),
            parseModelMetaProjection(opTagProjection, tagProjectionPsi.getReqOutputModelMeta(), subResolver, errors),
            modelProjection, subResolver, errors
        );

        tagProjections.put(
            tag.name(),
            new ReqOutputTagProjectionEntry(
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

  @Nullable
  private static List<ReqOutputVarProjection> parseTails(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @Nullable UrlReqOutputVarPolymorphicTail tailPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<ReqOutputVarProjection> tails;

    @NotNull TypesResolver subResolver = addTypeNamespace(dataType.type, typesResolver);

    if (tailPsi != null) {

      tails = new ArrayList<>();

      @Nullable UrlReqOutputVarSingleTail singleTail = tailPsi.getReqOutputVarSingleTail();
      if (singleTail != null) {
        @NotNull UrlTypeRef tailTypeRef = singleTail.getTypeRef();
        @NotNull UrlReqOutputComaVarProjection psiTailProjection = singleTail.getReqOutputComaVarProjection();
        @NotNull ReqOutputVarProjection tailProjection =
            buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
        tails.add(tailProjection);
      } else {
        @Nullable UrlReqOutputVarMultiTail multiTail = tailPsi.getReqOutputVarMultiTail();
        assert multiTail != null;
        Type prevTailType = null;

        for (UrlReqOutputVarMultiTailItem tailItem : multiTail.getReqOutputVarMultiTailItemList()) {
          try {
            if (prevTailType != null)
              subResolver = addTypeNamespace(prevTailType, typesResolver);

            @NotNull UrlTypeRef tailTypeRef = tailItem.getTypeRef();
            @NotNull UrlReqOutputComaVarProjection psiTailProjection = tailItem.getReqOutputComaVarProjection();
            @NotNull ReqOutputVarProjection tailProjection =
                buildTailProjection(dataType, op, tailTypeRef, psiTailProjection, subResolver, errors);
            tails.add(tailProjection);

            prevTailType = tailProjection.type();
          } catch (PsiProcessingException e) {
            errors.add(e.toError());
          }
        }
      }

    } else tails = null;

    return tails;
  }


  @Nullable
  private static ReqOutputModelProjection<?, ?> parseModelMetaProjection(
      @NotNull OpOutputModelProjection<?, ?> op,
      @Nullable UrlReqOutputModelMeta modelMetaPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (modelMetaPsi == null) return null;

    OpOutputModelProjection<?, ?> metaOp = op.metaProjection();

    if (metaOp == null) {
      errors.add(
          new PsiProcessingError(
              String.format("Meta projection not supported on type '%s'", op.model().name()),
              modelMetaPsi
          )
      );
      return null;
    }

    // no params/annotations/meta on meta for now

    return parseComaModelProjection(
        metaOp,
        modelMetaPsi.getPlus() != null,
        ReqParams.EMPTY,
        Annotations.EMPTY,
        null,
        modelMetaPsi.getReqOutputComaModelProjection(),
        addTypeNamespace(metaOp.model(), resolver),
        errors
    );
  }

  @NotNull
  private static ReqOutputVarProjection buildTailProjection(
      @NotNull DataType dataType,
      @NotNull OpOutputVarProjection op,
      @NotNull UrlTypeRef tailTypeRefPsi,
      @NotNull UrlReqOutputComaVarProjection tailProjectionPsi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TypeRef tailTypeRef = TypeRefs.fromPsi(tailTypeRefPsi);
    @NotNull Type tailType = getType(tailTypeRef, typesResolver, tailTypeRefPsi);

    @Nullable OpOutputVarProjection opTail = mergeOpTails(op, tailType);
    if (opTail == null)
      throw new PsiProcessingException(
          String.format("Polymorphic tail for type '%s' is not supported", tailType.name()),
          tailProjectionPsi,
          errors
      );

    return parseComaVarProjection(
        new DataType(tailType, dataType.defaultTag),
        opTail,
        tailProjectionPsi,
        typesResolver,
        errors
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
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

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
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    List<Type.Tag> defaultTags = findDefaultTags(type, op, locationPsi, errors);
    return createDefaultVarProjection(type, defaultTags, op, required, locationPsi, errors);
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
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (type.kind() != TypeKind.UNION) {
      DatumType datumType = (DatumType) type;
      final Type.@NotNull Tag self = datumType.self;
      findTagProjection(self.name(), op, locationPsi, errors); // check that op contains it
      return Collections.singletonList(self);
    }

    final Map<String, OpOutputTagProjectionEntry> opTagProjections = op.tagProjections();

    List<Type.Tag> defaultTags = new ArrayList<>(opTagProjections.size());
    for (Map.Entry<String, OpOutputTagProjectionEntry> entry : opTagProjections.entrySet()) {
      final OpOutputModelProjection<?, ?> opTagProjection = entry.getValue().projection();
      if (opTagProjection.includeInDefault()) {
        String tagName = entry.getKey();
        defaultTags.add(findTag(type, tagName, locationPsi, errors));
      }
    }

    return defaultTags;
  }

  @NotNull
  private static ReqOutputVarProjection createDefaultVarProjection(
      @NotNull DataType type,
      @NotNull OpOutputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return createDefaultVarProjection(type.type, op, required, locationPsi, errors);
  }

  @NotNull
  private static ReqOutputVarProjection createDefaultVarProjection(
      @NotNull DatumType type,
      @NotNull OpOutputVarProjection op,
      boolean required,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    return createDefaultVarProjection(type, Collections.singletonList(type.self), op, required, locationPsi, errors);
  }

  @NotNull
  public static StepsAndProjection<? extends ReqOutputModelProjection<?, ?>> parseTrunkModelProjection(
      @NotNull OpOutputModelProjection<?, ?> op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputModelProjection<?, ?> metaProjection,
      @NotNull UrlReqOutputTrunkModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull final TypesResolver subResolver = addTypeNamespace(op.model(), typesResolver);

    switch (op.model().kind()) {
      case RECORD:
        @Nullable
        UrlReqOutputTrunkRecordModelProjection trunkRecordProjectionPsi = psi.getReqOutputTrunkRecordModelProjection();

        if (trunkRecordProjectionPsi != null) {
          return parseTrunkRecordModelProjection(
              (OpOutputRecordModelProjection) op,
              required,
              params,
              annotations,
              (ReqOutputRecordModelProjection) metaProjection,
              trunkRecordProjectionPsi,
              subResolver,
              errors
          );
        } else break;

      case MAP:
        @Nullable
        UrlReqOutputTrunkMapModelProjection trunkMapProjectionPsi = psi.getReqOutputTrunkMapModelProjection();

        if (trunkMapProjectionPsi != null) {
          return parseTrunkMapModelProjection(
              (OpOutputMapModelProjection) op,
              required,
              params,
              annotations,
              (ReqOutputMapModelProjection) metaProjection,
              trunkMapProjectionPsi,
              subResolver,
              errors
          );
        } else break;
    }

    // end of path
    return
        new StepsAndProjection<>(
            0,
            parseComaModelProjection(op, required, params, annotations, metaProjection, psi, subResolver, errors)
        );

  }

  @NotNull
  public static ReqOutputModelProjection<?, ?> parseComaModelProjection(
      @NotNull OpOutputModelProjection<?, ?> op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputModelProjection<?, ?> metaProjection,
      @NotNull UrlReqOutputComaModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    DatumType model = op.model();
    @NotNull final TypesResolver subResolver = addTypeNamespace(model, typesResolver);

    switch (model.kind()) {
      case RECORD:
        final OpOutputRecordModelProjection opRecord = (OpOutputRecordModelProjection) op;

        @Nullable UrlReqOutputComaRecordModelProjection recordModelProjectionPsi =
            psi.getReqOutputComaRecordModelProjection();

        if (recordModelProjectionPsi == null)
          return createDefaultModelProjection(model, required, opRecord, params, annotations, psi, errors);

        ensureModelKind(psi, TypeKind.RECORD, errors);

        return parseComaRecordModelProjection(
            opRecord,
            required,
            params,
            annotations,
            (ReqOutputRecordModelProjection) metaProjection,
            recordModelProjectionPsi,
            subResolver,
            errors
        );

      case MAP:
        final OpOutputMapModelProjection opMap = (OpOutputMapModelProjection) op;
        @Nullable UrlReqOutputComaMapModelProjection mapModelProjectionPsi = psi.getReqOutputComaMapModelProjection();

        if (mapModelProjectionPsi == null)
          return createDefaultModelProjection(model, required, opMap, params, annotations, psi, errors);

        ensureModelKind(psi, TypeKind.MAP, errors);

        return parseComaMapModelProjection(
            opMap,
            required,
            params,
            annotations,
            (ReqOutputMapModelProjection) metaProjection,
            mapModelProjectionPsi,
            subResolver,
            errors
        );

      case LIST:
        final OpOutputListModelProjection opList = (OpOutputListModelProjection) op;
        @Nullable UrlReqOutputComaListModelProjection listModelProjectionPsi =
            psi.getReqOutputComaListModelProjection();

        if (listModelProjectionPsi == null)
          return createDefaultModelProjection(model, required, opList, params, annotations, psi, errors);

        ensureModelKind(psi, TypeKind.LIST, errors);

        return parseListModelProjection(
            opList,
            required,
            params,
            annotations,
            (ReqOutputListModelProjection) metaProjection,
            listModelProjectionPsi,
            subResolver,
            errors
        );

      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      case PRIMITIVE:
        return parsePrimitiveModelProjection(
            (PrimitiveType) model,
            required,
            params,
            annotations,
            (ReqOutputPrimitiveModelProjection) metaProjection,
            psi
        );

      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + model.kind(), psi, errors);

      default:
        throw new PsiProcessingException("Unknown type kind: " + model.kind(), psi, errors);
    }
  }

  private static void ensureModelKind(
      @NotNull UrlReqOutputComaModelProjection psi, @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(
          String.format("Unexpected projection kind '%s', expected '%s'", actualKind, expectedKind),
          psi,
          errors
      );
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull UrlReqOutputComaModelProjection psi) {
    if (psi.getReqOutputComaRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqOutputComaMapModelProjection() != null) return TypeKind.MAP;
    if (psi.getReqOutputComaListModelProjection() != null) return TypeKind.LIST;
    return null;
  }

  @NotNull
  private static ReqOutputModelProjection<?, ?> createDefaultModelProjection(
      @NotNull DatumType type,
      boolean required,
      @NotNull OpOutputModelProjection<?, ?> op,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull TextLocation location = EpigraphPsiUtil.getLocation(locationPsi);

    switch (type.kind()) {
      case RECORD:
        OpOutputRecordModelProjection opRecord = (OpOutputRecordModelProjection) op;
        final Map<String, OpOutputFieldProjectionEntry> opFields = opRecord.fieldProjections();

        @NotNull final Map<String, ReqOutputFieldProjectionEntry> fields;

        if (opFields.isEmpty()) {
          fields = Collections.emptyMap();
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
                          ReqParams.EMPTY,
                          Annotations.EMPTY,
                          createDefaultVarProjection(
                              field.dataType().type,
                              opFieldProjection.projection(),
                              false,
                              locationPsi,
                              errors
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
              locationPsi,
              errors
          );

        MapType mapType = (MapType) type;
        final ReqOutputVarProjection valueVarProjection = createDefaultVarProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            required,
            locationPsi,
            errors
        );

        return new ReqOutputMapModelProjection(
            mapType,
            required,
            params,
            annotations,
            null,
            null,
            valueVarProjection,
            location
        );
      case LIST:
        OpOutputListModelProjection opList = (OpOutputListModelProjection) op;
        ListType listType = (ListType) type;

        final ReqOutputVarProjection itemVarProjection = createDefaultVarProjection(
            listType.elementType(),
            opList.itemsProjection(),
            required,
            locationPsi,
            errors
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
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            locationPsi,
            errors
        );
      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), locationPsi, errors);
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
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), locationPsi, errors);
    }
  }

  @NotNull
  public static StepsAndProjection<ReqOutputRecordModelProjection> parseTrunkRecordModelProjection(
      @NotNull OpOutputRecordModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputRecordModelProjection metaProjection,
      @NotNull UrlReqOutputTrunkRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, OpOutputFieldProjectionEntry> opFields = op.fieldProjections();
    final String fieldName = psi.getQid().getCanonicalName();

    if (opFields.isEmpty())
      throw new PsiProcessingException("No fields are supported by the operation", psi.getQid(), errors);

    OpOutputFieldProjectionEntry opFieldProjectionEntry = opFields.get(fieldName);
    if (opFieldProjectionEntry == null) {
      throw new PsiProcessingException(
          String.format(
              "Unsupported field '%s', supported fields: (%s)",
              fieldName,
              ProjectionUtils.listFields(opFields.keySet())
          ),
          psi.getQid(),
          errors
      );
    }

    @NotNull final RecordType.Field field = opFieldProjectionEntry.field();
    @NotNull final OpOutputFieldProjection opFieldProjection = opFieldProjectionEntry.projection();

    @NotNull DataType fieldType = field.dataType();

    @Nullable UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();
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
        ), psi, errors);

      @NotNull ReqOutputVarProjection varProjection = createDefaultVarProjection(
          fieldType.type,
          opFieldProjection.projection(),
          required,
          psi,
          errors
      );

      fieldProjections.put(
          fieldName,
          new ReqOutputFieldProjectionEntry(
              field,
              new ReqOutputFieldProjection(
                  ReqParams.EMPTY,
                  Annotations.EMPTY,
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
              resolver,
              errors
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
      @NotNull UrlReqOutputTrunkFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {

    return parseTrunkFieldProjection(required, fieldType, op.params(), op.projection(), psi, resolver, errors);
  }

  @NotNull
  public static StepsAndProjection<ReqOutputFieldProjection> parseTrunkFieldProjection(
      boolean required,
      @NotNull DataType fieldType,
      @Nullable OpParams opParams,
      @NotNull OpOutputVarProjection opVarProjection,
      @NotNull UrlReqOutputTrunkFieldProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors
  ) throws PsiProcessingException {

    final int steps;
    final ReqOutputVarProjection varProjection;

    @Nullable UrlReqOutputTrunkVarProjection psiVarProjection = psi.getReqOutputTrunkVarProjection();
    StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
        parseTrunkVarProjection(fieldType, opVarProjection, psiVarProjection, resolver, errors);

    varProjection = stepsAndProjection.projection();
    steps = stepsAndProjection.pathSteps() + 1;

    return new StepsAndProjection<>(
        steps,
        new ReqOutputFieldProjection(
            parseReqParams(psi.getReqParamList(), opParams, resolver, errors),
            parseAnnotations(psi.getReqAnnotationList()),
            varProjection,
            required,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static ReqOutputRecordModelProjection parseComaRecordModelProjection(
      @NotNull OpOutputRecordModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputRecordModelProjection metaProjection,
      @NotNull UrlReqOutputComaRecordModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    LinkedHashMap<String, ReqOutputFieldProjectionEntry> fieldProjections = new LinkedHashMap<>();
    @NotNull List<UrlReqOutputComaFieldProjection> psiFieldProjections = psi.getReqOutputComaFieldProjectionList();

    Map<String, OpOutputFieldProjectionEntry> opFields = op.fieldProjections();

    for (UrlReqOutputComaFieldProjection fieldProjectionPsi : psiFieldProjections) {
      try {
        final String fieldName = fieldProjectionPsi.getQid().getCanonicalName();

        @Nullable OpOutputFieldProjectionEntry opFieldProjectionEntry = opFields.get(fieldName);

        if (opFieldProjectionEntry == null)
          throw new PsiProcessingException(
              String.format(
                  "Unsupported field '%s', supported fields: (%s)",
                  fieldName,
                  ProjectionUtils.listFields(opFields.keySet())
              ),
              fieldProjectionPsi,
              errors
          );

        @NotNull final RecordType.Field field = opFieldProjectionEntry.field();
        @NotNull final OpOutputFieldProjection opFieldProjection = opFieldProjectionEntry.projection();
        final boolean fieldRequired = fieldProjectionPsi.getPlus() != null;

        ReqParams fieldParams =
            parseReqParams(fieldProjectionPsi.getReqParamList(), opFieldProjection.params(), resolver, errors);

        Annotations fieldAnnotations = parseAnnotations(fieldProjectionPsi.getReqAnnotationList());

        @Nullable UrlReqOutputComaVarProjection psiVarProjection = fieldProjectionPsi.getReqOutputComaVarProjection();
        @NotNull ReqOutputVarProjection varProjection =
            parseComaVarProjection(
                field.dataType(),
                opFieldProjection.projection(),
                psiVarProjection,
                resolver,
                errors
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
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }
    }

    return new ReqOutputRecordModelProjection(
        op.model(),
        required,
        params,
        annotations,
        metaProjection,
        fieldProjections,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static StepsAndProjection<ReqOutputMapModelProjection> parseTrunkMapModelProjection(
      @NotNull OpOutputMapModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputMapModelProjection metaProjection,
      @NotNull UrlReqOutputTrunkMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final List<ReqOutputKeyProjection> keyProjections;

    if (op.keyProjection().presence() == OpOutputKeyProjection.Presence.FORBIDDEN)
      throw new PsiProcessingException("Map keys are forbidden", psi.getDatum(), errors);

    @NotNull UrlDatum valuePsi = psi.getDatum();
    @Nullable Datum keyValue = getDatum(valuePsi, op.model().keyType(), resolver, "Error processing map key: ", errors);
    if (keyValue == null) throw new PsiProcessingException("Null keys are not allowed", valuePsi, errors);

    ReqOutputKeyProjection keyProjection = new ReqOutputKeyProjection(
        keyValue,
        parseReqParams(psi.getReqParamList(), op.keyProjection().params(), resolver, errors),
        parseAnnotations(psi.getReqAnnotationList()),
        EpigraphPsiUtil.getLocation(psi)
    );

    final int steps;
    final ReqOutputVarProjection valueProjection;

    @Nullable UrlReqOutputTrunkVarProjection valueProjectionPsi = psi.getReqOutputTrunkVarProjection();
    StepsAndProjection<ReqOutputVarProjection> stepsAndProjection =
        parseTrunkVarProjection(op.model().valueType(), op.itemsProjection(), valueProjectionPsi, resolver, errors);

    valueProjection = stepsAndProjection.projection();
    steps = stepsAndProjection.pathSteps() + 1;

    return new StepsAndProjection<>(
        steps,
        new ReqOutputMapModelProjection(
            op.model(),
            required,
            params,
            annotations,
            metaProjection,
            Collections.singletonList(keyProjection),
            valueProjection,
            EpigraphPsiUtil.getLocation(psi)
        )
    );
  }

  @NotNull
  public static ReqOutputMapModelProjection parseComaMapModelProjection(
      @NotNull OpOutputMapModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputMapModelProjection metaProjection,
      @NotNull UrlReqOutputComaMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull UrlReqOutputComaKeysProjection keysProjectionPsi = psi.getReqOutputComaKeysProjection();

    @NotNull final OpOutputKeyProjection opKeyProjection = op.keyProjection();
    final List<ReqOutputKeyProjection> keyProjections;

    if (keysProjectionPsi.getStar() != null) {
      keyProjections = null;
      if (opKeyProjection.presence() == OpOutputKeyProjection.Presence.REQUIRED)
        throw new PsiProcessingException("Map keys are required", keysProjectionPsi.getStar(), errors);
    } else {
      if (opKeyProjection.presence() == OpOutputKeyProjection.Presence.FORBIDDEN)
        throw new PsiProcessingException("Map keys are forbidden", keysProjectionPsi, errors);

      final int keysSize = keysProjectionPsi.getReqOutputComaKeyProjectionList().size();
      keyProjections = new ArrayList<>(keysSize);
      for (UrlReqOutputComaKeyProjection keyProjectionPsi : keysProjectionPsi.getReqOutputComaKeyProjectionList()) {

        try {
          @NotNull UrlDatum valuePsi = keyProjectionPsi.getDatum();
          @Nullable Datum keyValue =
              getDatum(valuePsi, op.model().keyType(), resolver, "Error processing map key: ", errors);
          
          if (keyValue == null) throw new PsiProcessingException("Null keys are not allowed", valuePsi, errors);

          keyProjections.add(
              new ReqOutputKeyProjection(
                  keyValue,
                  parseReqParams(keyProjectionPsi.getReqParamList(), opKeyProjection.params(), resolver, errors),
                  parseAnnotations(keyProjectionPsi.getReqAnnotationList()),
                  EpigraphPsiUtil.getLocation(keyProjectionPsi)
              )
          );
        } catch (PsiProcessingException e) {
          errors.add(e.toError());
        }
      }
    }

    @Nullable UrlReqOutputComaVarProjection valueProjectionPsi = psi.getReqOutputComaVarProjection();
    @NotNull final ReqOutputVarProjection valueProjection;
    if (valueProjectionPsi == null) {
      valueProjection = createDefaultVarProjection(
          op.model().valueType().type,
          op.itemsProjection(),
          false,
          psi,
          errors
      );
    } else {
      valueProjection = parseComaVarProjection(
          op.model().valueType(),
          op.itemsProjection(),
          valueProjectionPsi,
          resolver,
          errors
      ).projection();
    }


    return new ReqOutputMapModelProjection(
        op.model(),
        required,
        params,
        annotations,
        metaProjection,
        keyProjections,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqOutputListModelProjection parseListModelProjection(
      @NotNull OpOutputListModelProjection op,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputListModelProjection metaProjection,
      @NotNull UrlReqOutputComaListModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    ReqOutputVarProjection itemsProjection;
    @Nullable UrlReqOutputComaVarProjection reqOutputVarProjectionPsi = psi.getReqOutputComaVarProjection();
    if (reqOutputVarProjectionPsi == null)
      itemsProjection = createDefaultVarProjection(op.model().elementType(), op.itemsProjection(), true, psi, errors);
    else
      itemsProjection =
          parseComaVarProjection(
              op.model().elementType(),
              op.itemsProjection(),
              reqOutputVarProjectionPsi,
              resolver,
              errors
          ).projection();


    return new ReqOutputListModelProjection(
        op.model(),
        required,
        params,
        annotations,
        metaProjection,
        itemsProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  @NotNull
  public static ReqOutputPrimitiveModelProjection parsePrimitiveModelProjection(
      @NotNull PrimitiveType type,
      boolean required,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @Nullable ReqOutputPrimitiveModelProjection metaProjection,
      @NotNull PsiElement locationPsi) throws PsiProcessingException {

    return new ReqOutputPrimitiveModelProjection(
        type,
        required,
        params,
        annotations,
        metaProjection,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
