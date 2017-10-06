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
import ws.epigraph.projections.op.*;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpPathPsiProcessingContext;
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
    SchemaOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaReadOperationBodyPart part : psi.getReadOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? ReadOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldProjection fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.READ, resourceType, pathPsi, resolver, context);

    final OpFieldProjection outputProjection = parseOutputProjection(
        operationNameOrDefaultName,
        OperationKind.READ,
        resolveOutputType(
            resourceType,
            fieldPath == null ? null : fieldPath.entityProjection(),
            null,
            resolver,
            context
        ), outputProjectionPsi, resolver, psi, context
    );

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
    SchemaInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOutputProjection outputProjectionPsi = null;

    for (SchemaCreateOperationBodyPart part : psi.getCreateOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? CreateOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldProjection fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.CREATE, resourceType, pathPsi, resolver, context);
    OpEntityProjection entityProjection = fieldPath == null ? null : fieldPath.entityProjection();

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, context);

    final @Nullable SchemaOpFieldProjection inputFieldProjectionPsi = inputProjectionPsi.getOpFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, context);

    OpReferenceContext referenceContext =
        createReferenceContext(
            operationNameOrDefaultName,
            context.inputReferenceContext(),
            OperationKind.CREATE,
            Namespaces.INPUT_SEGMENT,
            context
        );

    final OpFieldProjection fieldProjection =
        context.schemaPsiProcessingContext().inputProjectionsParser().parseFieldProjection(
            resolveInputType(resourceType, entityProjection, inputTypePsi, resolver, context),
            true,
            inputFieldProjectionPsi,
            resolver,
            new OpPsiProcessingContext(context, referenceContext)
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
                fieldPath == null ? null : fieldPath.entityProjection(),
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
    SchemaInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaUpdateOperationBodyPart part : psi.getUpdateOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? UpdateOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldProjection fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.UPDATE, resourceType, pathPsi, resolver, context);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, context);

    final @Nullable SchemaOpFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi.getOpFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, context);

    final @Nullable OpEntityProjection entityPath = fieldPath == null ? null : fieldPath.entityProjection();

    OpReferenceContext referenceContext =
        createReferenceContext(
            operationNameOrDefaultName,
            context.inputReferenceContext(),
            OperationKind.UPDATE,
            Namespaces.INPUT_SEGMENT,
            context
        );

    final @NotNull OpFieldProjection fieldProjection =
        context.schemaPsiProcessingContext().inputProjectionsParser().parseFieldProjection(
            resolveInputType(resourceType, entityPath, inputTypePsi, resolver, context),
            true,
            inputFieldProjectionPsi,
            resolver,
            new OpPsiProcessingContext(context, referenceContext)
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
                fieldPath == null ? null : fieldPath.entityProjection(),
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
    SchemaDeleteProjection deleteProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaDeleteOperationBodyPart part : psi.getDeleteOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      deleteProjectionPsi =
          getPsiPart(deleteProjectionPsi, part.getDeleteProjection(), "delete projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOutputProjection(), "output projection", context);
    }

    final String operationNameOrDefaultName =
        operationName == null ? DeleteOperationDeclaration.DEFAULT_NAME : operationName;

    @Nullable OpFieldProjection fieldPath =
        parsePath(operationNameOrDefaultName, OperationKind.DELETE, resourceType, pathPsi, resolver, context);

    if (deleteProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", psi, context);

    final @Nullable SchemaOpFieldProjection deleteFieldProjectionPsi =
        deleteProjectionPsi.getOpFieldProjection();
    if (deleteFieldProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", deleteProjectionPsi, context);

    OpReferenceContext referenceContext =
        createReferenceContext(
            operationNameOrDefaultName,
            context.deleteReferenceContext(),
            OperationKind.DELETE,
            Namespaces.DELETE_SEGMENT,
            context
        );

    final OpFieldProjection fieldProjection =
        context.schemaPsiProcessingContext().deleteProjectionsParser().parseFieldProjection(
            resolveDeleteType(resourceType, fieldPath == null ? null : fieldPath.entityProjection()),
            deleteProjectionPsi.getPlus() != null,
            deleteFieldProjectionPsi,
            resolver,
            new OpPsiProcessingContext(context, referenceContext)
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
                fieldPath == null ? null : fieldPath.entityProjection(),
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
    SchemaInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOutputProjection outputProjectionPsi = null;

    final @Nullable String operationName = parseOperationName(psi.getOperationName());
    if (operationName == null)
      throw new PsiProcessingException("Custom operation must have a name", psi, context);

    for (SchemaCustomOperationBodyPart part : psi.getCustomOperationBodyPartList()) {
      annotations = SchemaPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context, resolver);

      methodPsi = getPsiPart(methodPsi, part.getOperationMethod(), "HTTP method", context);
      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOutputProjection(), "output projection", context);
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

    final @Nullable SchemaOpFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi == null ? null : inputProjectionPsi.getOpFieldProjection();

    OpReferenceContext inputReferenceContext =
        createReferenceContext(
            operationName,
            context.inputReferenceContext(),
            OperationKind.CUSTOM,
            Namespaces.INPUT_SEGMENT,
            context
        );

    @Nullable OpFieldProjection opPath =
        parsePath(operationName, OperationKind.CUSTOM, resourceType, pathPsi, resolver, context);

    final OpFieldProjection fieldProjection =
        inputFieldProjectionPsi == null ? null :
        context.schemaPsiProcessingContext().inputProjectionsParser().parseFieldProjection(
            resolveInputType(
                resourceType,
                opPath == null ? null : opPath.entityProjection(),
                inputTypePsi,
                resolver,
                context
            ),
            true,
            inputFieldProjectionPsi,
            resolver,
            new OpPsiProcessingContext(context, inputReferenceContext)
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
                opPath == null ? null : opPath.entityProjection(),
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
      final @Nullable SchemaOutputProjection outputProjectionPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull PsiElement location,
      final @NotNull ResourcePsiProcessingContext resourcePsiProcessingContext)
      throws PsiProcessingException {

    final @Nullable SchemaOpFieldProjection outputFieldProjectionPsi =
        outputProjectionPsi == null ? null : outputProjectionPsi.getOpFieldProjection();

    OpReferenceContext outputReferenceContext =
        createReferenceContext(
            operationNameOrDefaultName,
            resourcePsiProcessingContext.outputReferenceContext(),
            operationKind,
            Namespaces.OUTPUT_SEGMENT,
            resourcePsiProcessingContext
        );

    OpPsiProcessingContext opPsiProcessingContext = new OpPsiProcessingContext(
        resourcePsiProcessingContext, outputReferenceContext
    );

    final OpFieldProjection fieldProjection;

    // todo add context
    if (outputProjectionPsi == null || outputFieldProjectionPsi == null) {

      final @NotNull OpEntityProjection varProjection =
          OpBasicProjectionPsiParser.createDefaultEntityProjection(outputType, location, opPsiProcessingContext);

      fieldProjection = new OpFieldProjection(
//          OpParams.EMPTY,
//          Annotations.EMPTY,
          varProjection,
          EpigraphPsiUtil.getLocation(location)
      );

//      throw new PsiProcessingException("Output projection must be specified", location, context);
    } else {
      fieldProjection =
          resourcePsiProcessingContext.schemaPsiProcessingContext().outputProjectionsParser().parseFieldProjection(
              outputType,
              false,
              outputFieldProjectionPsi,
              resolver,
              opPsiProcessingContext
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
        context.addError("Operation '" + name + "' specified twice", cur);
      }

      return cur;
    }

    return prev;
  }

  private static @NotNull DataTypeApi resolveOutputType(
      @NotNull DataTypeApi resourceType,
      @Nullable OpEntityProjection opEntityPath,
      @Nullable SchemaOperationOutputType declaredOutputTypePsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {


    // if type specified explicitly: use it
    // else use path tip type, if present
    // else use resource type

    final @Nullable SchemaValueTypeRef typeRefPsi =
        declaredOutputTypePsi == null ? null : declaredOutputTypePsi.getValueTypeRef();

    if (declaredOutputTypePsi == null || typeRefPsi == null) {
      return opEntityPath == null ? resourceType : ProjectionUtils.tipType(opEntityPath);
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
      @Nullable OpEntityProjection path,
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

        if (rtt instanceof DatumTypeApi)
          //noinspection RedundantCast
          return ((DatumTypeApi) rtt).dataType();

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
      @Nullable OpEntityProjection opEntityPath) {

    return opEntityPath == null ? resourceType : ProjectionUtils.tipType(opEntityPath);
  }

  private static @Nullable OpFieldProjection parsePath(
      @NotNull String operationNameOrDefaultName,
      @NotNull OperationKind operationKind,
      @NotNull DataTypeApi type,
      @Nullable SchemaOperationPath pathPsi,
      @NotNull TypesResolver resolver,
      @NotNull ResourcePsiProcessingContext context) throws PsiProcessingException {

    if (pathPsi == null) return null;
    else {
      final @Nullable SchemaOpFieldPath fieldPathPsi = pathPsi.getOpFieldPath();
      if (fieldPathPsi == null) {
        context.addError("Path expression missing", pathPsi);
        return null;
      }

      OpReferenceContext referenceContext =
          createReferenceContext(
              operationNameOrDefaultName,
              context.inputReferenceContext(),
              operationKind,
              Namespaces.PATH_SEGMENT,
              context
          );

      OpPsiProcessingContext outputPsiProcessingContext = new OpPsiProcessingContext(
          context,
          referenceContext
      );

      OpPathPsiProcessingContext psiProcessingContext =
          new OpPathPsiProcessingContext(context, outputPsiProcessingContext);

      final OpFieldProjection fieldPath = OpPathPsiParser.parseFieldPath(type,
          fieldPathPsi, resolver, psiProcessingContext
      );

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

  private static @NotNull OpReferenceContext createReferenceContext(
      final @NotNull String operationNameOrDefaultName,
      final @Nullable OpReferenceContext parentReferenceContext,
      final @NotNull OperationKind operationKind,
      final @NotNull String projectionsNamespaceSegment,
      final @NotNull ResourcePsiProcessingContext context) {

    return new OpReferenceContext(
        ProjectionReferenceName.fromQn(
            new Namespaces(context.namespace())
                .operationProjectionsNamespace(
                    context.resourceName(),
                    operationKind,
                    operationNameOrDefaultName
                ).append(projectionsNamespaceSegment)
        ),
        parentReferenceContext,
        context
    );

  }

}
