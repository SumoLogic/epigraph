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

package ws.epigraph.url.projections.req.path;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.op.*;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.parser.psi.*;

import java.text.MessageFormat;
import java.util.Collections;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * Path parser that stops when op projection is exhausted. Returns
 * {@link ReqPartialPathParsingResult} which contains unparsed tail.
 *
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqPartialPathPsiParser {

  private ReqPartialPathPsiParser() {}

  /**
   * Parses entity path
   *
   * @param op            op path
   * @param dataType      entity data type
   * @param psi           PSI to parse
   * @param typesResolver types resolver
   * @param context       parsing context
   *
   * @return (partial) parsing result
   */
  public static ReqPartialPathParsingResult<ReqProjection<?, ?>> parsePath(
      @NotNull OpProjection<?, ?> op,
      @NotNull DataTypeApi dataType,
      @NotNull UrlReqTrunkEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    final UrlReqNamedTrunkEntityProjection namedTrunkEntityProjection = psi.getReqNamedTrunkEntityProjection();

    if (namedTrunkEntityProjection != null) {
      final UrlReqUnnamedOrRefTrunkEntityProjection unnamedOrRefTrunkEntityProjection =
          namedTrunkEntityProjection.getReqUnnamedOrRefTrunkEntityProjection();

      if (unnamedOrRefTrunkEntityProjection == null)
        throw new PsiProcessingException("Incomplete entity path definition", psi, context);

      return parseUnnamedOrRefPath(
          op,
          dataType,
          unnamedOrRefTrunkEntityProjection,
          typesResolver,
          context
      );

    }

    final UrlReqUnnamedOrRefTrunkEntityProjection unnamedOrRefTrunkEntityProjection =
        psi.getReqUnnamedOrRefTrunkEntityProjection();

    if (unnamedOrRefTrunkEntityProjection == null)
      throw new PsiProcessingException("Incomplete var projection definition", psi, context);

    return parseUnnamedOrRefPath(
        op,
        dataType,
        unnamedOrRefTrunkEntityProjection,
        typesResolver,
        context
    );
  }

  private static ReqPartialPathParsingResult<ReqProjection<?, ?>> parseUnnamedOrRefPath(
      @NotNull OpProjection<?, ?> op,
      @NotNull DataTypeApi dataType,
      @NotNull UrlReqUnnamedOrRefTrunkEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    final UrlReqTrunkEntityProjectionRef refPsi = psi.getReqTrunkEntityProjectionRef();
    if (refPsi != null)
      throw new PsiProcessingException("References are not allowed in paths", refPsi, context);

    final UrlReqUnnamedTrunkEntityProjection unnamedTrunkEntityProjection =
        psi.getReqUnnamedTrunkEntityProjection();

    if (unnamedTrunkEntityProjection == null)
      throw new PsiProcessingException("Incomplete var projection definition", psi, context);

    return parseUnnamedPath(
        op,
        dataType,
        unnamedTrunkEntityProjection,
        typesResolver,
        context
    );
  }

  private static @NotNull ReqPartialPathParsingResult<ReqProjection<?, ?>> parseUnnamedPath(
      @NotNull OpProjection<?, ?> op,
      @NotNull DataTypeApi dataType,
      @NotNull UrlReqUnnamedTrunkEntityProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    if (op.isPathEnd())
      return new ReqPartialPathParsingResult<>(
          ReqEntityProjection.pathEnd(dataType.type(), EpigraphPsiUtil.getLocation(psi)),
          PsiTreeUtil.getParentOfType(psi, UrlReqTrunkEntityProjection.class),
          null
      );

    final @Nullable OpTagProjectionEntry opTagPath = op.singleTagProjection();
    assert opTagPath != null;
    final TagApi opTag = opTagPath.tag();

    final @Nullable UrlReqComaMultiTagProjection multiTagProjectionPsi = psi.getReqComaMultiTagProjection();
    final @Nullable UrlReqTrunkSingleTagProjection singleTagProjectionPsi =
        psi.getReqTrunkSingleTagProjection();

    if (multiTagProjectionPsi != null || singleTagProjectionPsi == null)
      throw new PathNotMatchedException(
          String.format(
              "Operation path tag '%s' is not matched by the request",
              opTag.name()
          ),
          multiTagProjectionPsi == null ? psi : multiTagProjectionPsi,
          context
      );

    final TypeApi type = dataType.type();

    final @Nullable UrlTagName tagNamePsi = singleTagProjectionPsi.getTagName();
    // for unions: check that provided tag is correct
    if (type.kind() == TypeKind.ENTITY) {
      if (tagNamePsi == null || !tagNamePsi.getQid().getCanonicalName().equals(opTag.name()))
        throw new PathNotMatchedException(
            String.format(
                "Operation path tag '%s' is not matched by the request",
                opTag.name()
            ),
            tagNamePsi == null ? psi : tagNamePsi,
            context
        );
    } else if (tagNamePsi != null)
      context.addError("Tags are not supported for model types", tagNamePsi);

    final @Nullable UrlReqModelMeta metaPsi = singleTagProjectionPsi.getReqModelMeta();
    if (metaPsi != null)
      context.addError("Meta projections are not supported in paths", metaPsi);

    final OpModelProjection<?, ?, ?, ?> opModelPath = opTagPath.modelProjection();

    final @NotNull UrlReqTrunkModelProjection modelPsi =
        singleTagProjectionPsi.getReqTrunkModelProjection();

    final @Nullable ReqPartialPathParsingResult<? extends ReqModelProjection<?, ?, ?>> parsedModelResult =
        parseModelPath(
            opModelPath,
            opTag.type(),
            parseReqParams(
                singleTagProjectionPsi.getReqParamList(),
                opModelPath.params(),
                typesResolver,
                singleTagProjectionPsi,
                context
            ),
            parseAnnotations(singleTagProjectionPsi.getReqAnnotationList(), context),
            modelPsi,
            typesResolver,
            context
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
//          context
//      );
//    }

    return new ReqPartialPathParsingResult<>(
        type.kind() == TypeKind.ENTITY ?
        ReqEntityProjection.path(
            type,
            new ReqTagProjectionEntry(
                opTag,
                parsedModelResult.path(),
                EpigraphPsiUtil.getLocation(modelPsi)
            ),
            EpigraphPsiUtil.getLocation(psi)
        ) : parsedModelResult.path(),
        parsedModelResult.trunkProjectionPsi(),
        parsedModelResult.comaProjectionPsi()
    );
  }

  public static @NotNull ReqPartialPathParsingResult<? extends ReqModelProjection<?, ?, ?>> parseModelPath(
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @NotNull DatumTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqTrunkModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    switch (type.kind()) {
      case RECORD:
        final OpRecordModelProjection opRecordPath = (OpRecordModelProjection) op;

        checkModelPsi(psi, TypeKind.RECORD, context);
        if (psi.getReqComaRecordModelProjection() != null) {
          final @Nullable OpFieldProjectionEntry opFieldPath = opRecordPath.pathFieldProjection();
          assert opFieldPath != null;

          throw new PathNotMatchedException(
              String.format("Operation path not matched, field '%s' must be present", opFieldPath.field().name()),
              psi,
              context
          );
        }

        @Nullable UrlReqTrunkRecordModelProjection recordModelProjectionPsi =
            psi.getReqTrunkRecordModelProjection();

        if (recordModelProjectionPsi == null)
          throw new PsiProcessingException("Record path must be specified", psi, context);

        ensureModelKind(psi, TypeKind.RECORD, context);
        return parseRecordModelPath(
            opRecordPath,
            (RecordTypeApi) type,
            params,
            directives,
            recordModelProjectionPsi,
            typesResolver,
            context
        );
      case MAP:
        checkModelPsi(psi, TypeKind.MAP, context);

        if (psi.getReqComaMapModelProjection() != null) {
          throw new PathNotMatchedException(
              "Operation path not matched, map key must be present",
              psi,
              context
          );
        }

        @Nullable UrlReqTrunkMapModelProjection mapModelProjectionPsi = psi.getReqTrunkMapModelProjection();
        if (mapModelProjectionPsi == null)
          throw new PathNotMatchedException("Operation path not matched, map key not specified", psi, context);

        ensureModelKind(psi, TypeKind.MAP, context);

        return parseMapModelPath(
            (OpMapModelProjection) op,
            (MapTypeApi) type,
            params,
            directives,
            mapModelProjectionPsi,
            typesResolver,
            context
        );
      case LIST:
        throw new PathNotMatchedException(
            "Operation path not matched, lists are not supported in paths",
            psi,
            context
        );
      case ENUM:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      case PRIMITIVE:
        return parsePrimitiveModelPath(
            (PrimitiveTypeApi) type,
            params,
            directives,
            psi
        );
      case ENTITY:
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), psi, context);
    }
  }

  private static void checkModelPsi(
      @NotNull UrlReqComaModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull ReqPathPsiProcessingContext context) {

    TypeKind actualKind = null;

    if (psi.getReqComaRecordModelProjection() != null) actualKind = TypeKind.RECORD;
    else if (psi.getReqComaMapModelProjection() != null) actualKind = TypeKind.MAP;
    else if (psi.getReqComaListModelProjection() != null) actualKind = TypeKind.LIST;

    if (actualKind != null && actualKind != expectedKind)
      context.addError(
          String.format(
              "Expected '%s', got '%s' model kind",
              expectedKind,
              actualKind
          ), psi
      );
  }

  private static void ensureModelKind(
      @NotNull UrlReqTrunkModelProjection psi,
      @NotNull TypeKind expectedKind,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, context);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqTrunkModelProjection psi) {
    if (psi.getReqTrunkRecordModelProjection() != null) return TypeKind.RECORD;
    if (psi.getReqTrunkMapModelProjection() != null) return TypeKind.MAP;
    return null;
  }

  public static @NotNull ReqPartialPathParsingResult<ReqRecordModelProjection> parseRecordModelPath(
      @NotNull OpRecordModelProjection op,
      @NotNull RecordTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqTrunkRecordModelProjection psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final String fieldName = psi.getQid().getCanonicalName();

    final OpFieldProjectionEntry opFieldEntry = op.fieldProjections().get(fieldName);

    if (opFieldEntry == null)
      throw new PsiProcessingException(
          ProjectionsParsingUtil.unsupportedFieldMsg(fieldName, op.fieldProjections().keySet()),
          psi.getQid(),
          context
      );

    FieldApi field = opFieldEntry.field();
    final @NotNull OpFieldProjection opFieldPath = opFieldEntry.fieldProjection();
    final OpProjection<?, ?> opFieldProjection = opFieldPath.projection();

    final @Nullable UrlReqTrunkFieldProjection fieldProjectionPsi = psi.getReqTrunkFieldProjection();

    if (fieldProjectionPsi == null) {
      if (opFieldProjection.isPathEnd()) {
        final @NotNull TextLocation qidLocation = EpigraphPsiUtil.getLocation(psi.getQid());
        try {
          return new ReqPartialPathParsingResult<>(
              new ReqRecordModelProjection(
                  type,
                  false,
                  ReqParams.EMPTY,
                  Directives.EMPTY,
                  null,
                  Collections.singletonMap(
                      fieldName,
                      new ReqFieldProjectionEntry(
                          field,
                          new ReqFieldProjection(
//                          ReqParams.EMPTY,
//                          Annotations.EMPTY,
                              ReqEntityProjection.pathEnd(field.dataType().type(), qidLocation),
                              qidLocation
                          ),
                          qidLocation
                      )
                  ),
                  null,
                  EpigraphPsiUtil.getLocation(psi)
              ),
              null,
              null
          );
        } catch (Exception e) {
          throw new PsiProcessingException(e, psi, context);
        }
      } else
        throw new PathNotMatchedException(
            String.format(
                "Operation path is not matched, field '%s' must have a projection",
                fieldName
            ),
            psi,
            context
        );
    }

    final @NotNull ReqPartialPathParsingResult<ReqFieldProjection> reqFieldPathParsingResult =
        parseFieldPath(field.dataType(), opFieldPath, fieldProjectionPsi, typesResolver, context);

    final ReqFieldProjectionEntry fieldProjection = new ReqFieldProjectionEntry(
        field,
        reqFieldPathParsingResult.path(),
        reqFieldPathParsingResult.path().location()
    );

    return new ReqPartialPathParsingResult<>(
        new ReqRecordModelProjection(
            type,
            false,
            params,
            directives,
            null,
            Collections.singletonMap(fieldName, fieldProjection),
            null,
            EpigraphPsiUtil.getLocation(psi)
        ),
        reqFieldPathParsingResult.trunkProjectionPsi(),
        reqFieldPathParsingResult.comaProjectionPsi()
    );
  }

  public static @NotNull ReqPartialPathParsingResult<ReqFieldProjection> parseFieldPath(
      final @NotNull DataTypeApi fieldType,
      final @NotNull OpFieldProjection op,
      final @NotNull UrlReqTrunkFieldProjection psi,
      final @NotNull TypesResolver typesResolver,
      final @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

//    @NotNull ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), typesResolver, context);
//    @NotNull Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), context);

    @NotNull UrlReqTrunkEntityProjection fieldVarPathPsi = psi.getReqTrunkEntityProjection();

    final ReqPartialPathParsingResult<ReqProjection<?, ?>> fieldParsingResult;

    if (op.projection().isPathEnd()) {
      fieldParsingResult = new ReqPartialPathParsingResult<>(
          ReqPath.pathEnd(
              fieldType.type(),
              EpigraphPsiUtil.getLocation(fieldVarPathPsi)
          ),
          fieldVarPathPsi,
          null
      );
    } else
      fieldParsingResult =
          parsePath(op.projection(), fieldType, fieldVarPathPsi, typesResolver, context);

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(psi);

    return new ReqPartialPathParsingResult<>(

        new ReqFieldProjection(
//            fieldParams,
//            fieldAnnotations,
            fieldParsingResult.path(),
            fieldLocation
        ),
        fieldParsingResult.trunkProjectionPsi(),
        fieldParsingResult.comaProjectionPsi()
    );
  }

  public static @NotNull ReqPartialPathParsingResult<ReqMapModelProjection> parseMapModelPath(
      @NotNull OpMapModelProjection op,
      @NotNull MapTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqTrunkMapModelProjection psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    @NotNull ReqKeyProjection keyProjection = parseKeyProjection(
        op.keyProjection(),
        op.type().keyType(),
        psi,
        resolver,
        context
    );

    @NotNull UrlReqTrunkEntityProjection valueProjectionPsi = psi.getReqTrunkEntityProjection();

    ReqPartialPathParsingResult<ReqProjection<?, ?>> itemParsingResult =
        parsePath(op.itemsProjection(), type.valueType(), valueProjectionPsi, resolver, context);

    return new ReqPartialPathParsingResult<>(
        new ReqMapModelProjection(
            type,
            false,
            params,
            directives,
            null,
            Collections.singletonList(keyProjection),
            true,
            itemParsingResult.path(),
            null,
            EpigraphPsiUtil.getLocation(psi)
        ),
        itemParsingResult.trunkProjectionPsi(),
        itemParsingResult.comaProjectionPsi()
    );
  }

  private static @NotNull ReqKeyProjection parseKeyProjection(
      @NotNull OpKeyProjection op,
      @NotNull DatumTypeApi keyType,
      @NotNull UrlReqTrunkMapModelProjection mapPathPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final @NotNull ReqParams reqParams =
        parseReqParams(mapPathPsi.getReqParamList(), op.params(), resolver, mapPathPsi, context);
    final @NotNull Directives directives = parseAnnotations(mapPathPsi.getReqAnnotationList(), context);

    final @Nullable Datum keyValue =
        getDatum(mapPathPsi.getDatum(), keyType, resolver, "Error processing map key: ", context);

    if (keyValue == null)
      throw new PsiProcessingException("Null path keys not allowed", mapPathPsi.getDatum(), context);

    return new ReqKeyProjection(
        keyValue,
        reqParams,
        directives,
        EpigraphPsiUtil.getLocation(mapPathPsi)
    );
  }

  private static @NotNull ReqPartialPathParsingResult<ReqPrimitiveModelProjection> parsePrimitiveModelPath(
      @NotNull PrimitiveTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull PsiElement locationPsi) {

    return new ReqPartialPathParsingResult<>(
        new ReqPrimitiveModelProjection(
            type,
            false,
            params,
            directives,
            null,
            null,
            EpigraphPsiUtil.getLocation(locationPsi)
        ),
        null, null
    );
  }

}
