package ws.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.path.*;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.path.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.req.ReqParserUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ReadReqPathPsiParser {

  public static ReadReqPathParsingResult<ReqVarPath> parseVarPath(
      @NotNull OpVarPath op,
      @NotNull DataType dataType,
      @NotNull UrlReqOutputTrunkVarProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    if (OpVarPath.isEnd(op))
      throw new IllegalArgumentException("This method should not be called for empty op paths");

    @Nullable final OpTagPath opTagPath = op.pathTagProjection();
    assert opTagPath != null;
    final Type.Tag opTag = opTagPath.tag();

    @Nullable final UrlReqOutputComaMultiTagProjection multiTagProjectionPsi = psi.getReqOutputComaMultiTagProjection();
    @Nullable final UrlReqOutputTrunkSingleTagProjection singleTagProjectionPsi =
        psi.getReqOutputTrunkSingleTagProjection();

    if (multiTagProjectionPsi != null || singleTagProjectionPsi == null)
      throw new PathNotMatchedException(
          String.format(
              "Operation path tag '%s' is not matched by the request",
              opTag.name()
          ),
          multiTagProjectionPsi != null ? multiTagProjectionPsi : psi,
          errors
      );

    final Type type = dataType.type;

    @Nullable final UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();
    // for unions: check that provided tag is correct
    if (type.kind().equals(TypeKind.UNION)) {
      if (tagNamePsi == null || !tagNamePsi.getQid().getCanonicalName().equals(opTag.name()))
        throw new PathNotMatchedException(
            String.format(
                "Operation path tag '%s' is not matched by the request",
                opTag.name()
            ),
            tagNamePsi == null ? psi : tagNamePsi,
            errors
        );
    } else if (tagNamePsi != null)
      errors.add(new PsiProcessingError("Tags are not supported for non-var types", tagNamePsi));

    @Nullable final UrlReqOutputModelMeta metaPsi = singleTagProjectionPsi.getReqOutputModelMeta();
    if (metaPsi != null)
      errors.add(new PsiProcessingError("Meta projections are not supported in paths", metaPsi));

    final OpModelPath<?, ?> opModelPath = opTagPath.projection();

    @NotNull final UrlReqOutputTrunkModelProjection modelPsi =
        singleTagProjectionPsi.getReqOutputTrunkModelProjection();

    final @Nullable ReadReqPathParsingResult<? extends ReqModelPath<?, ?>> parsedModelResult = parseModelPath(
        opModelPath,
        opTag.type,
        ReqParserUtil.parseReqParams(singleTagProjectionPsi.getReqParamList(), opModelPath.params(), typesResolver, errors),
        ReqParserUtil.parseAnnotations(singleTagProjectionPsi.getReqAnnotationList()),
        modelPsi,
        typesResolver,
        errors
    );

//    if (parsedModelResult == null) { // spotted a coma model, path ends here
//      return new ReadReqPathParsingResult<>(
//          new ReqVarPath(
//              type,
//              null,
//              EpigraphPsiUtil.getLocation(psi)
//          ),
//          psi,
//          null,
//          errors
//      );
//    }

    return new ReadReqPathParsingResult<>(
        new ReqVarPath(
            type,
            new ReqTagPath(
                opTag,
                parsedModelResult.path(),
                EpigraphPsiUtil.getLocation(modelPsi)
            ),
            EpigraphPsiUtil.getLocation(psi)
        ),
        parsedModelResult.trunkProjectionPsi(),
        parsedModelResult.comaProjectionPsi(),
        errors
    );
  }

  @NotNull
  public static ReadReqPathParsingResult<? extends ReqModelPath<?, ?>> parseModelPath(
      @NotNull OpModelPath<?, ?> op,
      @NotNull DatumType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqOutputTrunkModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        final OpRecordModelPath opRecordPath = (OpRecordModelPath) op;

        if (psi.getReqOutputComaRecordModelProjection() != null) {
          @Nullable final OpFieldPathEntry opFieldPath = opRecordPath.pathFieldProjection();
          assert opFieldPath != null;

          throw new PathNotMatchedException(
              String.format("Operation path not matched, field '%s' must be present", opFieldPath.field().name()),
              psi,
              errors
          );
        }

        @Nullable UrlReqOutputTrunkRecordModelProjection recordModelProjectionPsi =
            psi.getReqOutputTrunkRecordModelProjection();

        if (recordModelProjectionPsi == null)
          throw new PsiProcessingException("Record path must be specified", psi, errors);

        ensureModelKind(psi, TypeKind.RECORD, errors);
        return parseRecordModelPath(
            opRecordPath,
            (RecordType) type,
            params,
            annotations,
            recordModelProjectionPsi,
            typesResolver,
            errors
        );
      case MAP:
        if (psi.getReqOutputComaMapModelProjection() != null) {
          throw new PathNotMatchedException(
              "Operation path not matched, map key must be present",
              psi,
              errors
          );
        }

        @Nullable UrlReqOutputTrunkMapModelProjection mapModelProjectionPsi = psi.getReqOutputTrunkMapModelProjection();
        if (mapModelProjectionPsi == null)
          throw new PathNotMatchedException("Operation path not matched, map key not specified", psi, errors);

        ensureModelKind(psi, TypeKind.MAP, errors);

        return parseMapModelPath(
            (OpMapModelPath) op,
            (MapType) type,
            params,
            annotations,
            mapModelProjectionPsi,
            typesResolver,
            errors
        );
      case LIST:
        throw new PathNotMatchedException(
            "Operation path not matched, lists are not supported in paths",
            psi,
            errors
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      case PRIMITIVE:
        return parsePrimitiveModelPath(
            (PrimitiveType) type,
            params,
            annotations,
            psi,
            errors
        );
      case UNION:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, errors);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, errors);
    }
  }

  private static void ensureModelKind(
      @NotNull UrlReqOutputTrunkModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (!expectedKind.equals(actualKind))
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, errors);
  }

  @Nullable
  private static TypeKind findProjectionKind(@NotNull UrlReqOutputTrunkModelProjection psi) {
    if (psi.getReqOutputTrunkRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqOutputTrunkMapModelProjection() != null) return TypeKind.MAP;
    return null;
  }

  @NotNull
  public static ReadReqPathParsingResult<ReqRecordModelPath> parseRecordModelPath(
      @NotNull OpRecordModelPath op,
      @NotNull RecordType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqOutputTrunkRecordModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final String fieldName = psi.getQid().getCanonicalName();

    final OpFieldPathEntry opFieldEntry = op.fieldProjections().get(fieldName);

    if (opFieldEntry == null)
      throw new PsiProcessingException(
          String.format(
              "Field '%s' is not supported by operation, supported fields: {%s}",
              fieldName,
              ProjectionUtils.listFields(op.fieldProjections().keySet())
          ),
          psi.getQid(),
          errors
      );

    RecordType.Field field = opFieldEntry.field();
    @NotNull final OpFieldPath opFieldPath = opFieldEntry.projection();
    @NotNull final OpVarPath opFieldVarProjection = opFieldPath.projection();

    @Nullable final UrlReqOutputTrunkFieldProjection fieldProjectionPsi = psi.getReqOutputTrunkFieldProjection();

    if (fieldProjectionPsi == null) {
      if (OpVarPath.isEnd(opFieldVarProjection)) {
        return new ReadReqPathParsingResult<>(
            new ReqRecordModelPath(
                type,
                ReqParams.EMPTY,
                Annotations.EMPTY,
                new ReqFieldPathEntry(
                    field,
                    new ReqFieldPath(
                        ReqParams.EMPTY,
                        Annotations.EMPTY,
                        new ReqVarPath(
                            field.dataType().type,
                            null,
                            EpigraphPsiUtil.getLocation(psi.getQid())
                        ),
                        EpigraphPsiUtil.getLocation(psi.getQid())
                    ),
                    EpigraphPsiUtil.getLocation(psi.getQid())
                ),
                EpigraphPsiUtil.getLocation(psi)
            ),
            null,
            null,
            errors
        );
      } else
        throw new PathNotMatchedException(
            String.format(
                "Operation path is not matched, field '%s' must have a projection",
                fieldName
            ),
            psi,
            errors
        );
    }

    final @NotNull ReadReqPathParsingResult<ReqFieldPath> reqFieldPathParsingResult =
        parseFieldPath(field.dataType(), opFieldPath, fieldProjectionPsi, typesResolver, errors);

    final ReqFieldPathEntry fieldProjection = new ReqFieldPathEntry(
        field,
        reqFieldPathParsingResult.path(),
        reqFieldPathParsingResult.path().location()
    );

    return new ReadReqPathParsingResult<>(
        new ReqRecordModelPath(
            type,
            params,
            annotations,
            fieldProjection,
            EpigraphPsiUtil.getLocation(psi)
        ),
        reqFieldPathParsingResult.trunkProjectionPsi(),
        reqFieldPathParsingResult.comaProjectionPsi(),
        errors
    );
  }

  @NotNull
  public static ReadReqPathParsingResult<ReqFieldPath> parseFieldPath(
      final @NotNull DataType fieldType,
      final @NotNull OpFieldPath op,
      final @NotNull UrlReqOutputTrunkFieldProjection psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull ReqParams fieldParams =
        ReqParserUtil.parseReqParams(psi.getReqParamList(), op.params(), typesResolver, errors);

    @NotNull Annotations fieldAnnotations = ReqParserUtil.parseAnnotations(psi.getReqAnnotationList());

    @NotNull UrlReqOutputTrunkVarProjection fieldVarPathPsi = psi.getReqOutputTrunkVarProjection();

    final ReadReqPathParsingResult<ReqVarPath> fieldVarParsingResult;

    if (OpVarPath.isEnd(op.projection())) {
      fieldVarParsingResult = new ReadReqPathParsingResult<>(
          new ReqVarPath(
              fieldType.type,
              null,
              EpigraphPsiUtil.getLocation(fieldVarPathPsi)
          ),
          fieldVarPathPsi,
          null,
          errors
      );
    } else
      fieldVarParsingResult =
          parseVarPath(op.projection(), fieldType, fieldVarPathPsi, typesResolver, errors);

    @NotNull final TextLocation fieldLocation = EpigraphPsiUtil.getLocation(psi);

    return new ReadReqPathParsingResult<>(

        new ReqFieldPath(
            fieldParams,
            fieldAnnotations,
            fieldVarParsingResult.path(),
            fieldLocation
        ),
        fieldVarParsingResult.trunkProjectionPsi(),
        fieldVarParsingResult.comaProjectionPsi(),
        errors
    );
  }

  @NotNull
  public static ReadReqPathParsingResult<ReqMapModelPath> parseMapModelPath(
      @NotNull OpMapModelPath op,
      @NotNull MapType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull UrlReqOutputTrunkMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    @NotNull ReqPathKeyProjection keyProjection = parseKeyProjection(
        op.keyProjection(),
        op.model().keyType(),
        psi,
        resolver,
        errors
    );

    @NotNull UrlReqOutputTrunkVarProjection valueProjectionPsi = psi.getReqOutputTrunkVarProjection();

    ReadReqPathParsingResult<ReqVarPath> varParsingResult =
        parseVarPath(op.itemsProjection(), type.valueType(), valueProjectionPsi, resolver, errors);

    return new ReadReqPathParsingResult<>(
        new ReqMapModelPath(
            type,
            params,
            annotations,
            keyProjection,
            varParsingResult.path(),
            EpigraphPsiUtil.getLocation(psi)
        ),
        varParsingResult.trunkProjectionPsi(),
        varParsingResult.comaProjectionPsi(),
        errors
    );
  }

  @NotNull
  private static ReqPathKeyProjection parseKeyProjection(
      @NotNull OpPathKeyProjection op,
      @NotNull DatumType keyType,
      @NotNull UrlReqOutputTrunkMapModelProjection mapPathPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @NotNull final ReqParams reqParams =
        ReqParserUtil.parseReqParams(mapPathPsi.getReqParamList(), op.params(), resolver, errors);

    @NotNull final Annotations annotations = ReqParserUtil.parseAnnotations(mapPathPsi.getReqAnnotationList());

    @Nullable final Datum keyValue =
        ReqParserUtil.getDatum(mapPathPsi.getDatum(), keyType, resolver, "Error processing map key: ", errors);

    if (keyValue == null) throw new PsiProcessingException("Null path keys not allowed", mapPathPsi.getDatum(), errors);

    return new ReqPathKeyProjection(
        keyValue,
        reqParams,
        annotations,
        EpigraphPsiUtil.getLocation(mapPathPsi)
    );
  }

  @NotNull
  public static ReadReqPathParsingResult<ReqPrimitiveModelPath> parsePrimitiveModelPath(
      @NotNull PrimitiveType type,
      @NotNull ReqParams params,
      @NotNull Annotations annotations,
      @NotNull PsiElement locationPsi,
      @NotNull List<PsiProcessingError> errors) {

    return new ReadReqPathParsingResult<>(
        new ReqPrimitiveModelPath(
            type,
            params,
            annotations,
            EpigraphPsiUtil.getLocation(locationPsi)
        ),
        null, null,
        errors
    );
  }

}
