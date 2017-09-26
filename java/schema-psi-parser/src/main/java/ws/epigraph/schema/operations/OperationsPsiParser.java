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

package ws.epigraph.schema.operations;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotation;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.OpEntityProjection;
import ws.epigraph.projections.op.OpFieldProjection;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiProcessingContext;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.schema.Namespaces;
import ws.epigraph.schema.ResourcePsiProcessingContext;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.parser.SchemaPsiParserUtil;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.TypeApi;

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
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    @Nullable SchemaReadOperationDef readOperationDef = psi.getReadOperationDef();
    if (readOperationDef != null) return parseRead(resourceType, readOperationDef, resolver, context);

    @Nullable SchemaCreateOperationDef createOperationDef = psi.getCreateOperationDef();
    if (createOperationDef != null) return parseCreate(resourceType, createOperationDef, resolver, context);

    @Nullable SchemaUpdateOperationDef updateOperationDef = psi.getUpdateOperationDef();
    if (updateOperationDef != null) return parseUpdate(resourceType, updateOperationDef, resolver, context);

    @Nullable SchemaDeleteOperationDef deleteOperationDef = psi.getDeleteOperationDef();
    if (deleteOperationDef != null) return parseDelete(resourceType, deleteOperationDef, resolver, context);

    @Nullable SchemaCustomOperationDef customOperationDef = psi.getCustomOperationDef();
    if (customOperationDef != null) return parseCustom(resourceType, customOperationDef, resolver, context);

    throw new PsiProcessingException("Incomplete operation statement", psi, context);
  }

  private static @NotNull ReadOperationDeclaration parseRead(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaReadOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    Map<DatumTypeApi, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaReadOperationBodyPart part : psi.getReadOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? ReadOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldPath fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.READ, resourceType, pathPsi, resolver, context);
//    @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    final OpFieldProjection outputProjection = parseOutputProjection(
        operationNameOrDefaultName,
        OperationKind.READ,
        resolveOutputType(
            resourceType,
            fieldPath == null ? null : fieldPath.varProjection(),
            null,
            resolver,
            context
        ), outputProjectionPsi, resolver, psi, context
    );

//    if (outputProjectionPsi == null)
//      throw new PsiProcessingException("Output projection must be specified", psi, context);
//
//    OpOutputFieldProjection outputProjection = OpOutputProjectionsPsiParser.parseFieldProjection(
//        resolveOutputType(resourceType, varPath, null, resolver, context),
//        true,
//        outputProjectionPsi.getOpOutputFieldProjection(),
//        resolver,
//        context
//    );

    return new ReadOperationDeclaration(
        operationName,
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
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    final String operationName = parseOperationName(psi.getOperationName());

    Map<DatumTypeApi, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    for (SchemaCreateOperationBodyPart part : psi.getCreateOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? CreateOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldPath fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.CREATE, resourceType, pathPsi, resolver, context);
    OpVarPath varPath = fieldPath == null ? null : fieldPath.varProjection();

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, context);

    final @Nullable SchemaOpOutputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi.getOpOutputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, context);

    OpReferenceContext referenceContext =
        createInputReferenceContext(
            operationNameOrDefaultName,
            context.inputReferenceContext(),
            OperationKind.CREATE,
            context
        );

    OpPsiProcessingContext outputPsiProcessingContext = new OpPsiProcessingContext(
        context,
        referenceContext
    );

    final OpFieldProjection fieldProjection = OpInputProjectionsPsiParser.INSTANCE.parseFieldProjection(
        resolveInputType(resourceType, varPath, inputTypePsi, resolver, context),
        true,
        inputFieldProjectionPsi,
        resolver,
        outputPsiProcessingContext
    );
    referenceContext.ensureAllReferencesResolved();

    return new CreateOperationDeclaration(
        operationName,
        Annotations.fromMap(annotations),
        fieldPath,
        fieldProjection,
        parseOutputProjection(
            operationNameOrDefaultName,
            OperationKind.CREATE,
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.varProjection(),
                outputTypePsi,
                resolver,
                context
            ), outputProjectionPsi, resolver, psi, context
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull UpdateOperationDeclaration parseUpdate(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaUpdateOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    Map<DatumTypeApi, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaUpdateOperationBodyPart part : psi.getUpdateOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? UpdateOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldPath fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.UPDATE, resourceType, pathPsi, resolver, context);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, context);

    final @Nullable SchemaOpOutputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi.getOpOutputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, context);

    final @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.varProjection();

    OpReferenceContext referenceContext =
        createInputReferenceContext(
            operationNameOrDefaultName,
            context.inputReferenceContext(),
            OperationKind.UPDATE,
            context
        );

    OpPsiProcessingContext outputPsiProcessingContext = new OpPsiProcessingContext(
        context,
        referenceContext
    );

    final @NotNull OpFieldProjection fieldProjection = OpInputProjectionsPsiParser.INSTANCE.parseFieldProjection(
        resolveInputType(resourceType, varPath, inputTypePsi, resolver, context),
        true,
        inputFieldProjectionPsi,
        resolver,
        outputPsiProcessingContext
    );
    referenceContext.ensureAllReferencesResolved();

    return new UpdateOperationDeclaration(
        operationName,
        Annotations.fromMap(annotations),
        fieldPath,
        fieldProjection,
        parseOutputProjection(
            operationNameOrDefaultName,
            OperationKind.UPDATE,
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.varProjection(),
                outputTypePsi,
                resolver,
                context
            ), outputProjectionPsi, resolver, psi, context
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull DeleteOperationDeclaration parseDelete(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaDeleteOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    Map<DatumTypeApi, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationDeleteProjection deleteProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaDeleteOperationBodyPart part : psi.getDeleteOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      deleteProjectionPsi =
          getPsiPart(deleteProjectionPsi, part.getOperationDeleteProjection(), "delete projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? DeleteOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldPath fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.DELETE, resourceType, pathPsi, resolver, context);

    if (deleteProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", psi, context);

    final @Nullable SchemaOpOutputFieldProjection deleteFieldProjectionPsi =
        deleteProjectionPsi.getOpOutputFieldProjection();
    if (deleteFieldProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", deleteProjectionPsi, context);

    OpReferenceContext referenceContext =
        createDeleteReferenceContext(
            operationNameOrDefaultName,
            context.deleteReferenceContext(),
            OperationKind.DELETE,
            context
        );

    OpPsiProcessingContext psiProcessingContext = new OpPsiProcessingContext(
        context,
        referenceContext
    );

    final OpFieldProjection fieldProjection = OpDeleteProjectionsPsiParser.INSTANCE.parseFieldProjection(
        resolveDeleteType(resourceType, fieldPath == null ? null : fieldPath.varProjection()),
        deleteProjectionPsi.getPlus() != null,
        deleteFieldProjectionPsi,
        resolver,
        psiProcessingContext
    );
    referenceContext.ensureAllReferencesResolved();

    return new DeleteOperationDeclaration(
        operationName,
        Annotations.fromMap(annotations),
        fieldPath,
        fieldProjection,
        parseOutputProjection(
            operationNameOrDefaultName,
            OperationKind.DELETE,
            resolveOutputType(
                resourceType,
                fieldPath == null ? null : fieldPath.varProjection(),
                outputTypePsi,
                resolver,
                context
            ), outputProjectionPsi, resolver, psi, context
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull CustomOperationDeclaration parseCustom(
      @NotNull DataTypeApi resourceType,
      @NotNull SchemaCustomOperationDef psi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    Map<DatumTypeApi, Annotation> annotations = null;

    SchemaOperationMethod methodPsi = null;
    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final @Nullable String operationName = parseOperationName(psi.getOperationName());
    if (operationName == null)
      throw new PsiProcessingException("Custom operation must have a name", psi, context);

    for (SchemaCustomOperationBodyPart part : psi.getCustomOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      methodPsi = getPsiPart(methodPsi, part.getOperationMethod(), "HTTP method", context);
      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    final HttpMethod method;
    if (methodPsi == null)
      throw new PsiProcessingException("HTTP method must be specified", psi, context);
    else {
      if (methodPsi.getGet() != null) method = HttpMethod.GET;
      else if (methodPsi.getPost() != null) method = HttpMethod.POST;
      else if (methodPsi.getPut() != null) method = HttpMethod.PUT;
      else if (methodPsi.getDelete() != null) method = HttpMethod.DELETE;
      else throw new PsiProcessingException("HTTP method must be specified", methodPsi, context);
    }

    final @Nullable SchemaOpOutputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi == null ? null : inputProjectionPsi.getOpOutputFieldProjection();

    OpReferenceContext inputReferenceContext =
        createInputReferenceContext(operationName, context.inputReferenceContext(), OperationKind.CUSTOM, context);

    OpPsiProcessingContext outputPsiProcessingContext = new OpPsiProcessingContext(
        context,
        inputReferenceContext
    );

    @Nullable OpFieldPath opPath =
        parsePath(operationName, OperationKind.CUSTOM, resourceType, pathPsi, resolver, context);

    final OpFieldProjection fieldProjection =
        inputFieldProjectionPsi == null ? null : OpInputProjectionsPsiParser.INSTANCE.parseFieldProjection(
            resolveInputType(
                resourceType,
                opPath == null ? null : opPath.varProjection(),
                inputTypePsi,
                resolver,
                context
            ),
            true,
            inputFieldProjectionPsi,
            resolver,
            outputPsiProcessingContext
        );

    inputReferenceContext.ensureAllReferencesResolved();

    return new CustomOperationDeclaration(
        method,
        operationName,
        Annotations.fromMap(annotations),
        opPath,
        fieldProjection,
        parseOutputProjection(
            operationName,
            OperationKind.CUSTOM,
            resolveOutputType(
                resourceType,
                opPath == null ? null : opPath.varProjection(),
                outputTypePsi,
                resolver,
                context
            ), outputProjectionPsi, resolver, psi, context
        ),
        EpigraphPsiUtil.getLocation(psi)
    );
  }

  private static @NotNull OpFieldProjection parseOutputProjection(
      final @NotNull String operationNameOrDefaultName,
      final @NotNull OperationKind operationKind,
      final @NotNull DataTypeApi outputType,
      final @Nullable SchemaOperationOutputProjection outputProjectionPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull PsiElement location,
      final @NotNull ResourcePsiProcessingContext context)
      throws PsiProcessingException {

    final @Nullable SchemaOpOutputFieldProjection outputFieldProjectionPsi =
        outputProjectionPsi == null ? null : outputProjectionPsi.getOpOutputFieldProjection();

    OpReferenceContext outputReferenceContext =
        createOutputReferenceContext(
            operationNameOrDefaultName,
            context.outputReferenceContext(),
            operationKind,
            context
        );

    OpPsiProcessingContext psiProcessingContext = new OpPsiProcessingContext(
        context, outputReferenceContext // todo (null)
    );

    final OpFieldProjection fieldProjection;

    // todo add context
    if (outputProjectionPsi == null || outputFieldProjectionPsi == null) {

      final @NotNull OpEntityProjection varProjection =
          OpBasicProjectionPsiParser.createDefaultVarProjection(outputType, location, psiProcessingContext);

      fieldProjection = new OpFieldProjection(
//          OpParams.EMPTY,
//          Annotations.EMPTY,
          varProjection,
          EpigraphPsiUtil.getLocation(location)
      );

//      throw new PsiProcessingException("Output projection must be specified", location, context);
    } else {
      fieldProjection = OpOutputProjectionsPsiParser.INSTANCE.parseFieldProjection(
          outputType,
          false,
          outputFieldProjectionPsi,
          resolver,
          psiProcessingContext
      );
    }
    outputReferenceContext.ensureAllReferencesResolved();

    return fieldProjection;
  }

  @Contract("null, !null, _, _ -> !null")
  private static @Nullable <T extends PsiElement> T getPsiPart(
      @Nullable T prev,
      @Nullable T cur,
      @NotNull String name,
      @NotNull PsiProcessingContext context) {

    if (cur != null) {
      if (prev != null) {
        context.addError("Operation " + name + " specified twice", cur);
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
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {


    // if type specified explicitly: use it
    // else use path tip type, if present
    // else use resource type

    final @Nullable SchemaValueTypeRef typeRefPsi =
        declaredOutputTypePsi == null ? null : declaredOutputTypePsi.getValueTypeRef();

    if (declaredOutputTypePsi == null || typeRefPsi == null) {
      return opVarPath == null ? resourceType : ProjectionUtils.tipType(opVarPath);
    }

    final @NotNull ValueTypeRef valueTypeRef = TypeRefs.fromPsi(typeRefPsi, context);
    final @Nullable DataTypeApi dataType = resolver.resolve(valueTypeRef);
    if (dataType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve output type '%s'", typeRefPsi.getText()),
          typeRefPsi,
          context
      );
    return dataType;
  }

  private static @NotNull DataTypeApi resolveInputType(
      @NotNull DataTypeApi resourceType,
      @Nullable OpVarPath path,
      @Nullable SchemaOperationInputType inputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    // if type specified explicitly: use it
    // else use path tip type, if present
    // else use resource type

    final @Nullable SchemaTypeRef typeRefPsi = inputTypePsi == null ? null : inputTypePsi.getTypeRef();
    if (inputTypePsi == null || typeRefPsi == null) {
      if (path == null) {
        // no path and no explicit type: use resource type

        @NotNull TypeApi rtt = resourceType.type();

        if (rtt instanceof DatumTypeApi) return ((DatumTypeApi) rtt).dataType();

        return resourceType;

      } else {
        // path provided but no explicit type: use path tip type

        final @NotNull DataTypeApi tipType = ProjectionUtils.tipType(path);

        final @NotNull TypeApi ttt = tipType.type();

        if (ttt instanceof DatumType) return ((DatumType) ttt).dataType();

        return tipType;
      }
    }
    // explicit type given: use it
    final @Nullable DatumTypeApi datumType = TypeRefs.fromPsi(typeRefPsi, context).resolveDatumType(resolver);
    if (datumType == null)
      throw new PsiProcessingException("Can't resolve input type '" + typeRefPsi.getText() + "'", typeRefPsi, context);
    return datumType.dataType();
  }

  private static @NotNull DataTypeApi resolveDeleteType(
      @NotNull DataTypeApi resourceType,
      @Nullable OpVarPath opVarPath) {
    return opVarPath == null ? resourceType : ProjectionUtils.tipType(opVarPath);
  }

  private static @Nullable OpFieldPath parsePath(
      @NotNull String operationNameOrDefaultName,
      @NotNull OperationKind operationKind,
      @NotNull DataTypeApi type,
      @Nullable SchemaOperationPath pathPsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    if (pathPsi == null) return null;
    else {
      final @Nullable SchemaOpFieldPath varPathPsi = pathPsi.getOpFieldPath();
      if (varPathPsi == null) {
        context.addError("Path expression missing", pathPsi);
        return null;
      }

      // todo which reference context should be used here?

      OpReferenceContext referenceContext =
          createOutputReferenceContext(operationNameOrDefaultName, null/*?*/, operationKind, context);

      OpPsiProcessingContext outputPsiProcessingContext = new OpPsiProcessingContext(
          context,
          referenceContext
      );

      OpPathPsiProcessingContext psiProcessingContext =
          new OpPathPsiProcessingContext(context, outputPsiProcessingContext);

      final OpFieldPath fieldPath = OpPathPsiParser.parseFieldPath(type, varPathPsi, resolver, psiProcessingContext);

      referenceContext.ensureAllReferencesResolved();

      return fieldPath;
    }
  }

  @Contract("null -> null")
  private static @Nullable String parseOperationName(@Nullable SchemaOperationName namePsi) {
    if (namePsi == null) return null;
    @Nullable SchemaQid qid = namePsi.getQid();
    if (qid == null) return null;
    return qid.getCanonicalName();
  }

  private static @NotNull OpReferenceContext createOutputReferenceContext(
      final @NotNull String operationNameOrDefaultName,
      final @Nullable OpReferenceContext parentReferenceContext,
      final @NotNull OperationKind operationKind,
      final @NotNull ResourcePsiProcessingContext context) {

    return new OpReferenceContext(
        ProjectionReferenceName.fromQn(
            new Namespaces(context.namespace())
                .operationOutputProjectionsNamespace(
                    context.resourceName(),
                    operationKind,
                    operationNameOrDefaultName
                )
        ),
        parentReferenceContext,
        context
    );

  }

  private static @NotNull OpReferenceContext createInputReferenceContext(
      final @NotNull String operationNameOrDefaultName,
      final @Nullable OpReferenceContext parentReferenceContext,
      final @NotNull OperationKind operationKind,
      final @NotNull ResourcePsiProcessingContext context) {

    return new OpReferenceContext(
        ProjectionReferenceName.fromQn(
            new Namespaces(context.namespace())
                .operationInputProjectionsNamespace(
                    context.resourceName(),
                    operationKind,
                    operationNameOrDefaultName
                )
        ),
        parentReferenceContext,
        context
    );

  }

  private static @NotNull OpReferenceContext createDeleteReferenceContext(
      final @NotNull String operationNameOrDefaultName,
      final @Nullable OpReferenceContext parentReferenceContext,
      final @NotNull OperationKind operationKind,
      final @NotNull ResourcePsiProcessingContext context) {

    return new OpReferenceContext(
        ProjectionReferenceName.fromQn(
            new Namespaces(context.namespace())
                .operationDeleteProjectionsNamespace(
                    context.resourceName(),
                    operationKind,
                    operationNameOrDefaultName
                )
        ),
        parentReferenceContext,
        context
    );

  }

}
