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

package ws.epigraph.schema.operations;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.SchemaProjectionPsiParserUtil;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.OpParams;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputFieldProjection;
import ws.epigraph.projections.op.output.OpOutputProjectionsPsiParser;
import ws.epigraph.projections.op.output.OpOutputVarProjection;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.types.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class OperationsPsiParser {
  private OperationsPsiParser() {}

  public static @NotNull OperationDeclaration parseOperation(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable SchemaReadOperationDef readOperationDef = psi.getReadOperationDef();
    if (readOperationDef != null) return parseRead(resourceType, readOperationDef, resolver, errors);

    @Nullable SchemaCreateOperationDef createOperationDef = psi.getCreateOperationDef();
    if (createOperationDef != null) return parseCreate(resourceType, createOperationDef, resolver, errors);

    @Nullable SchemaUpdateOperationDef updateOperationDef = psi.getUpdateOperationDef();
    if (updateOperationDef != null) return parseUpdate(resourceType, updateOperationDef, resolver, errors);

    @Nullable SchemaDeleteOperationDef deleteOperationDef = psi.getDeleteOperationDef();
    if (deleteOperationDef != null) return parseDelete(resourceType, deleteOperationDef, resolver, errors);

    @Nullable SchemaCustomOperationDef customOperationDef = psi.getCustomOperationDef();
    if (customOperationDef != null) return parseCustom(resourceType, customOperationDef, resolver, errors);

    throw new PsiProcessingException("Incomplete operation statement", psi, errors);
  }

  private static @NotNull ReadOperationDeclaration parseRead(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaReadOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    for (SchemaReadOperationBodyPart part : psi.getReadOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);
//    @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    final OpOutputFieldProjection outputProjection = parseOutputProjection(
        resolveOutputType(
            resourceType,
            fieldPath == null ? null : fieldPath.varProjection(),
            null,
            resolver,
            errors
        ), outputProjectionPsi, resolver, psi, errors
    );

//    if (outputProjectionPsi == null)
//      throw new PsiProcessingException("Output projection must be specified", psi, errors);
//
//    OpOutputFieldProjection outputProjection = OpOutputProjectionsPsiParser.parseFieldProjection(
//        resolveOutputType(resourceType, varPath, null, resolver, errors),
//        true,
//        outputProjectionPsi.getOpOutputFieldProjection(),
//        resolver,
//        errors
//    );

    return new ReadOperationDeclaration(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        outputProjection,
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull CreateOperationDeclaration parseCreate(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaCreateOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    for (SchemaCreateOperationBodyPart part : psi.getCreateOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);
    OpVarPath varPath = fieldPath == null ? null : fieldPath.varProjection();

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, errors);

    final @Nullable SchemaOpInputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi.getOpInputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, errors);

    return new CreateOperationDeclaration(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpInputProjectionsPsiParser.parseFieldProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, errors),
            true,
            inputFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.varProjection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull UpdateOperationDeclaration parseUpdate(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaUpdateOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    for (SchemaUpdateOperationBodyPart part : psi.getUpdateOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, errors);

    final @Nullable SchemaOpInputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi.getOpInputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, errors);

    final @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.varProjection();

    return new UpdateOperationDeclaration(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpInputProjectionsPsiParser.parseFieldProjection(
            resolveInputType(resourceType, varPath, inputTypePsi, resolver, errors),
            true,
            inputFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.varProjection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull DeleteOperationDeclaration parseDelete(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaDeleteOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationDeleteProjection deleteProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    for (SchemaDeleteOperationBodyPart part : psi.getDeleteOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      deleteProjectionPsi =
          getPsiPart(deleteProjectionPsi, part.getOperationDeleteProjection(), "delete projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    @Nullable OpFieldPath fieldPath = parsePath(resourceType, pathPsi, resolver, errors);

    if (deleteProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", psi, errors);

    final @Nullable SchemaOpDeleteFieldProjection deleteFieldProjectionPsi =
        deleteProjectionPsi.getOpDeleteFieldProjection();
    if (deleteFieldProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", deleteProjectionPsi, errors);

    return new DeleteOperationDeclaration(
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        fieldPath,
        OpDeleteProjectionsPsiParser.parseFieldProjection(
            resolveDeleteType(resourceType, fieldPath == null ? null : fieldPath.varProjection()),
            deleteFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.varProjection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull CustomOperationDeclaration parseCustom(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaCustomOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    Map<String, Annotation> annotations = null;

    SchemaOperationMethod methodPsi = null;
    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    for (SchemaCustomOperationBodyPart part : psi.getCustomOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), errors);

      methodPsi = getPsiPart(methodPsi, part.getOperationMethod(), "HTTP method", errors);
      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", errors);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", errors);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", errors);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", errors);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", errors);
    }

    final HttpMethod method;
    if (methodPsi == null)
      throw new PsiProcessingException("HTTP method must be specified", psi, errors);
    else {
      if (methodPsi.getGet() != null) method = HttpMethod.GET;
      else if (methodPsi.getPost() != null) method = HttpMethod.POST;
      else if (methodPsi.getPut() != null) method = HttpMethod.PUT;
      else if (methodPsi.getDelete() != null) method = HttpMethod.DELETE;
      else throw new PsiProcessingException("HTTP method must be specified", methodPsi, errors);
    }

    final @Nullable SchemaOpInputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi == null ? null : inputProjectionPsi.getOpInputFieldProjection();

    @Nullable OpFieldPath opPath = parsePath(resourceType, pathPsi, resolver, errors);

    return new CustomOperationDeclaration(
        method,
        parseOperationName(psi.getOperationName()),
        Annotations.fromMap(annotations),
        opPath,
        inputFieldProjectionPsi == null ? null : OpInputProjectionsPsiParser.parseFieldProjection(
            resolveInputType(
                resourceType,
                opPath == null ? null : opPath.varProjection(),
                inputTypePsi,
                resolver,
                errors
            ),
            true,
            inputFieldProjectionPsi,
            resolver,
            errors
        ),
        parseOutputProjection(
            resolveOutputType(
                resourceType,
                opPath == null ? null : opPath.varProjection(),
                outputTypePsi,
                resolver,
                errors
            ), outputProjectionPsi, resolver, psi, errors
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpOutputFieldProjection parseOutputProjection(
      final @NotNull DataTypeApi outputType,
      final @Nullable SchemaOperationOutputProjection outputProjectionPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull PsiElement location,
      final @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {

    final @Nullable SchemaOpOutputFieldProjection outputFieldProjectionPsi =
        outputProjectionPsi == null ? null : outputProjectionPsi.getOpOutputFieldProjection();

    // todo add context
    if (outputProjectionPsi == null || outputFieldProjectionPsi == null) {
      final @NotNull OpOutputVarProjection varProjection =
          OpOutputProjectionsPsiParser.createDefaultVarProjection(outputType, location, errors);

      return new OpOutputFieldProjection(
          OpParams.EMPTY,
          Annotations.EMPTY,
          varProjection,
          EpigraphPsiUtil.getLocation(location)
      );

//      throw new PsiProcessingException("Output projection must be specified", location, errors);
    }

    return OpOutputProjectionsPsiParser.parseFieldProjection(
        outputType,
        outputFieldProjectionPsi,
        resolver,
        errors
    );
  }

  @Contract("null, !null, _, _ -> !null")
  private static @Nullable <T extends PsiElement> T getPsiPart(
      @Nullable T prev,
      @Nullable T cur,
      @NotNull String name,
      @NotNull Collection<PsiProcessingError> errors) {

    if (cur != null) {
      if (prev != null) {
        errors.add(new PsiProcessingError("Operation " + name + " specified twice", cur));
      }

      return cur;
    }

    return prev;
  }

  private static @NotNull DataTypeApi resolveOutputType(
      @NotNull DataTypeApi resourceType,
      @Nullable OpVarPath opVarPath,
      @Nullable SchemaOperationOutputType declaredOutputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable SchemaValueTypeRef typeRefPsi =
        declaredOutputTypePsi == null ? null : declaredOutputTypePsi.getValueTypeRef();

    if (declaredOutputTypePsi == null || typeRefPsi == null) {
      return opVarPath == null ? resourceType : ProjectionUtils.tipType(opVarPath);
    }

    final @NotNull ValueTypeRef valueTypeRef = TypeRefs.fromPsi(typeRefPsi, errors);
    final @Nullable DataTypeApi dataType = resolver.resolve(valueTypeRef);
    if (dataType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve output type '%s'", typeRefPsi.getText()),
          typeRefPsi,
          errors
      );
    return dataType;
  }

  private static @NotNull DataTypeApi resolveInputType(
      @NotNull DataTypeApi resourceType,
      @Nullable OpVarPath path,
      @Nullable SchemaOperationInputType inputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final @Nullable SchemaTypeRef typeRefPsi = inputTypePsi == null ? null : inputTypePsi.getTypeRef();
    if (inputTypePsi == null || typeRefPsi == null) {
      if (path == null) {

        @NotNull TypeApi rtt = resourceType.type();

        if (rtt instanceof DatumTypeApi) return ((DatumTypeApi) rtt).dataType();

        return resourceType;

      } else {

        final @NotNull DataTypeApi tipType = ProjectionUtils.tipType(path);

        final @NotNull TypeApi ttt = tipType.type();

        if (ttt instanceof DatumType) return ((DatumType) ttt).dataType();

        return tipType;
      }
    }
    final @Nullable DatumTypeApi datumType = TypeRefs.fromPsi(typeRefPsi, errors).resolveDatumType(resolver);
    if (datumType == null)
      throw new PsiProcessingException("Can't resolve input type '" + typeRefPsi.getText() + "'", typeRefPsi, errors);
    return datumType.dataType();
  }

  private static @NotNull DataTypeApi resolveDeleteType(@NotNull DataTypeApi resourceType, @Nullable OpVarPath opVarPath) {
    return opVarPath == null ? resourceType : ProjectionUtils.tipType(opVarPath);
  }

  @Contract("_, null, _, _ -> null")
  private static @Nullable OpFieldPath parsePath(
      @NotNull DataTypeApi type,
      @Nullable SchemaOperationPath pathPsi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    if (pathPsi == null) return null;
    else {
      final @Nullable SchemaOpFieldPath varPathPsi = pathPsi.getOpFieldPath();
      if (varPathPsi == null) {
        errors.add(new PsiProcessingError("Path expression missing", pathPsi));
        return null;
      }
      return OpPathPsiParser.parseFieldPath(type, varPathPsi, resolver, errors);
    }
  }

  @Contract("null -> null")
  private static @Nullable String parseOperationName(@Nullable SchemaOperationName namePsi) {
    if (namePsi == null) return null;
    @Nullable SchemaQid qid = namePsi.getQid();
    if (qid == null) return null;
    return qid.getCanonicalName();
  }
}
