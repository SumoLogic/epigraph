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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.op.*;
import ws.epigraph.projections.req.Directives;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.req.ReqParams;
import ws.epigraph.projections.req.path.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.url.projections.UrlProjectionsPsiParserUtil;

import java.text.MessageFormat;

import static ws.epigraph.url.projections.UrlProjectionsPsiParserUtil.*;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ReqPathPsiParser {

  private ReqPathPsiParser() {}

  public static ReqVarPath parseEntityPath(
      @NotNull OpEntityProjection op,
      @NotNull DataTypeApi dataType,
      @NotNull UrlReqEntityPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final TypeApi type = dataType.type();

    final @Nullable UrlTagName tagNamePsi = psi.getTagName();

    final @Nullable OpTagProjectionEntry opTagPath = op.singleTagProjection();

    if (opTagPath == null) {
      if (tagNamePsi != null)
        throw new PsiProcessingException(
            String.format("'%s' tags are not supported by the operation", dataType.name()),
            tagNamePsi,
            context
        );

      return new ReqVarPath(
          type,
          null, // no tags = end of path
          EpigraphPsiUtil.getLocation(psi)
      );
    }

    @NotNull UrlReqModelPath modelPathPsi = psi.getReqModelPath();

    final @NotNull TagApi reqTag = ProjectionsParsingUtil.getTag(
        dataType,
        UrlProjectionsPsiParserUtil.getTagName(tagNamePsi),
        op,
        psi,
        context
    );
    final TagApi opTag = opTagPath.tag();

    if (!reqTag.equals(opTag)) {
      throw new PsiProcessingException(
          String.format("'%s' tag is not supported by the operation, did you mean '%s'?", reqTag.name(), opTag.name()),
          psi,
          context
      );
    }

    final OpModelProjection<?, ?, ?, ?> opModelPath = opTagPath.projection();


    final ReqModelPath<?, ?, ?> parsedModelProjection = parseModelPath(
        opModelPath,
        opTag.type(),
        modelPathPsi,
        typesResolver,
        context
    );

    try {
      return new ReqVarPath(
          type,
          new ReqTagPath(
              opTag, parsedModelProjection, EpigraphPsiUtil.getLocation(modelPathPsi)
          ),
          EpigraphPsiUtil.getLocation(psi)
      );
    } catch (Exception e) {
      throw new PsiProcessingException(e, psi, context);
    }
  }

//  private static boolean isModelPathEmpty(@Nullable UrlReqModelPath pathPsi) {
//    return pathPsi == null || (
//        pathPsi.getReqRecordModelPath() == null &&
//        pathPsi.getReqMapModelPath() == null
//    );
//  }

  private static @NotNull ReqModelPath<?, ?, ?> parseModelPath(
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @NotNull DatumTypeApi type,
      @NotNull UrlReqModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    ReqParams params = parseReqParams(psi.getReqParamList(), op.params(), typesResolver, psi, context);
    Directives directives = parseAnnotations(psi.getReqAnnotationList(), context);

    switch (type.kind()) {
      case RECORD:
        @Nullable UrlReqRecordModelPath recordModelProjectionPsi = psi.getReqRecordModelPath();
        if (recordModelProjectionPsi == null)
          throw new PsiProcessingException("Record path must be specified", psi, context);

        ensureModelKind(psi, TypeKind.RECORD, context);
        return parseRecordModelPath(
            (OpRecordModelProjection) op,
            (RecordTypeApi) type,
            params,
            directives,
            recordModelProjectionPsi,
            typesResolver,
            context
        );
      case MAP:
        @Nullable UrlReqMapModelPath mapModelProjectionPsi = psi.getReqMapModelPath();
        if (mapModelProjectionPsi == null)
          throw new PsiProcessingException("Map path must be specified", psi, context);

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
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), psi, context);
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

  private static void ensureModelKind(
      @NotNull UrlReqModelPath psi, @NotNull TypeKind expectedKind,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    @Nullable TypeKind actualKind = findProjectionKind(psi);
    if (expectedKind != actualKind)
      throw new PsiProcessingException(MessageFormat.format(
          "Unexpected projection kind ''{0}'', expected ''{1}''",
          actualKind,
          expectedKind
      ), psi, context);
  }

  private static @Nullable TypeKind findProjectionKind(@NotNull UrlReqModelPath psi) {
    if (psi.getReqRecordModelPath() != null) return TypeKind.RECORD;
    if (psi.getReqMapModelPath() != null) return TypeKind.MAP;
    return null;
  }

  public static @NotNull ReqRecordModelPath parseRecordModelPath(
      @NotNull OpRecordModelProjection op,
      @NotNull RecordTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqRecordModelPath psi,
      @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final @NotNull UrlReqFieldPathEntry fieldPathEntryPsi = psi.getReqFieldPathEntry();

    final String fieldName = fieldPathEntryPsi.getQid().getCanonicalName();

    final OpFieldProjectionEntry opFieldEntry = op.fieldProjections().get(fieldName);

    if (opFieldEntry == null)
      throw new PsiProcessingException(
          String.format(
              "Field '%s' is not supported by operation, supported fields: {%s}",
              fieldName,
              ProjectionUtils.listFields(op.fieldProjections().keySet())
          ),
          fieldPathEntryPsi.getQid(),
          context
      );

    FieldApi field = opFieldEntry.field();

    final ReqFieldPathEntry fieldProjection = new ReqFieldPathEntry(
        field,
        parseFieldPath(
            field.dataType(),
            opFieldEntry.fieldProjection(),
            fieldPathEntryPsi.getReqFieldPath(),
            typesResolver,
            context
        ),
        EpigraphPsiUtil.getLocation(fieldPathEntryPsi)
    );

    return new ReqRecordModelPath(
        type,
        params,
        directives,
        fieldProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  public static @NotNull ReqFieldPath parseFieldPath(
      final @NotNull DataTypeApi fieldType,
      final @NotNull OpFieldProjection op,
      final @NotNull UrlReqFieldPath psi,
      final @NotNull TypesResolver typesResolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

//    @NotNull ReqParams fieldParams = parseReqParams(psi.getReqParamList(), op.params(), typesResolver, context);

//    @NotNull Annotations fieldAnnotations = parseAnnotations(psi.getReqAnnotationList(), context);

    @Nullable UrlReqEntityPath fieldVarPathPsi = psi.getReqEntityPath();

    final ReqVarPath varProjection;

    varProjection = parseEntityPath(op.entityProjection(), fieldType, fieldVarPathPsi, typesResolver, context);

//    final ReadReqPathParsingResult<ReqVarPath> fieldVarParsingResult;

    final @NotNull TextLocation fieldLocation = EpigraphPsiUtil.getLocation(psi);

    return new ReqFieldPath(
//        fieldParams,
//        fieldAnnotations,
        varProjection,
        fieldLocation
    );
  }

  public static @NotNull ReqMapModelPath parseMapModelPath(
      @NotNull OpMapModelProjection op,
      @NotNull MapTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull UrlReqMapModelPath psi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPathPsiProcessingContext context)
      throws PsiProcessingException {

    @NotNull ReqPathKeyProjection keyProjection = parseKeyProjection(
        op.keyProjection(),
        op.type().keyType(),
        psi,
        resolver,
        context
    );

    @Nullable UrlReqEntityPath valueProjectionPsi = psi.getReqEntityPath();

    if (valueProjectionPsi == null)
      throw new PsiProcessingException("Map value projection not specified", psi, context);

    @NotNull ReqVarPath valueProjection =
        parseEntityPath(op.itemsProjection(), type.valueType(), valueProjectionPsi, resolver, context);

    return new ReqMapModelPath(
        type,
        params,
        directives,
        keyProjection,
        valueProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull ReqPathKeyProjection parseKeyProjection(
      @NotNull OpKeyProjection op,
      @NotNull DatumTypeApi keyType,
      @NotNull UrlReqMapModelPath mapPathPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReqPathPsiProcessingContext context) throws PsiProcessingException {

    final @NotNull ReqParams reqParams =
        parseReqParams(mapPathPsi.getReqParamList(), op.params(), resolver, mapPathPsi, context);
    final @NotNull Directives directives = parseAnnotations(mapPathPsi.getReqAnnotationList(), context);

    final @Nullable Datum keyValue =
        getDatum(mapPathPsi.getDatum(), keyType, resolver, "Error processing map key: ", context);

    if (keyValue == null)
      throw new PsiProcessingException("Null path keys not allowed", mapPathPsi.getDatum(), context);

    return new ReqPathKeyProjection(
        keyValue,
        reqParams,
        directives,
        EpigraphPsiUtil.getLocation(mapPathPsi)
    );
  }

  private static @NotNull ReqPrimitiveModelPath parsePrimitiveModelPath(
      @NotNull PrimitiveTypeApi type,
      @NotNull ReqParams params,
      @NotNull Directives directives,
      @NotNull PsiElement locationPsi) {

    return new ReqPrimitiveModelPath(
        type,
        params,
        directives,
        EpigraphPsiUtil.getLocation(locationPsi)
    );
  }

}
