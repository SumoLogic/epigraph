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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.op.delete.OpDeleteFieldProjection;
import ws.epigraph.projections.op.delete.OpDeletePsiProcessingContext;
import ws.epigraph.projections.op.delete.OpDeleteVarReferenceContext;
import ws.epigraph.projections.op.input.OpInputFieldProjection;
import ws.epigraph.projections.op.input.OpInputPsiProcessingContext;
import ws.epigraph.projections.op.input.OpInputVarReferenceContext;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.projections.op.path.OpPathPsiProcessingContext;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.schema.ResourcePsiProcessingContext;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.projections.Annotation;
import ws.epigraph.projections.Annotations;
import ws.epigraph.projections.SchemaProjectionPsiParserUtil;
import ws.epigraph.projections.ProjectionUtils;
import ws.epigraph.projections.op.delete.OpDeleteProjectionsPsiParser;
import ws.epigraph.projections.op.input.OpInputProjectionsPsiParser;
import ws.epigraph.projections.op.path.OpFieldPath;
import ws.epigraph.projections.op.path.OpPathPsiParser;
import ws.epigraph.projections.op.path.OpVarPath;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.types.*;

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

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaReadOperationBodyPart part : psi.getReadOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    @Nullable OpFieldPath fieldPath =
        parsePath(OperationKind.READ, operationName, resourceType, pathPsi, resolver, context);
//    @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.projection();

    final OpOutputFieldProjection outputProjection = parseOutputProjection(
        OperationKind.READ,
        operationName,
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

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    for (SchemaCreateOperationBodyPart part : psi.getCreateOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    @Nullable OpFieldPath fieldPath =
        parsePath(OperationKind.CREATE, operationName, resourceType, pathPsi, resolver, context);
    OpVarPath varPath = fieldPath == null ? null : fieldPath.varProjection();

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, context);

    final @Nullable SchemaOpInputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi.getOpInputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, context);

    OpInputVarReferenceContext referenceContext =
        createInputVarReferenceContext(OperationKind.CREATE, operationName, context);
    OpInputPsiProcessingContext opInputPsiProcessingContext = new OpInputPsiProcessingContext(
        context,
        referenceContext
    );

    final OpInputFieldProjection fieldProjection = OpInputProjectionsPsiParser.parseFieldProjection(
        resolveInputType(resourceType, varPath, inputTypePsi, resolver, context),
        true,
        inputFieldProjectionPsi,
        resolver,
        opInputPsiProcessingContext
    );
    referenceContext.ensureAllReferencesResolved(context);

    return new CreateOperationDeclaration(
        operationName,
        Annotations.fromMap(annotations),
        fieldPath,
        fieldProjection,
        parseOutputProjection(
            OperationKind.CREATE,
            operationName,
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

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaUpdateOperationBodyPart part : psi.getUpdateOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      inputTypePsi = getPsiPart(inputTypePsi, part.getOperationInputType(), "input type", context);
      inputProjectionPsi =
          getPsiPart(inputProjectionPsi, part.getOperationInputProjection(), "input projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    @Nullable OpFieldPath fieldPath =
        parsePath(OperationKind.UPDATE, operationName, resourceType, pathPsi, resolver, context);

    if (inputProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", psi, context);

    final @Nullable SchemaOpInputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi.getOpInputFieldProjection();
    if (inputFieldProjectionPsi == null)
      throw new PsiProcessingException("Input projection must be specified", inputProjectionPsi, context);

    final @Nullable OpVarPath varPath = fieldPath == null ? null : fieldPath.varProjection();

    OpInputVarReferenceContext referenceContext =
        createInputVarReferenceContext(OperationKind.UPDATE, operationName, context);
    OpInputPsiProcessingContext psiProcessingContext = new OpInputPsiProcessingContext(
        context,
        referenceContext
    );

    final OpInputFieldProjection fieldProjection = OpInputProjectionsPsiParser.parseFieldProjection(
        resolveInputType(resourceType, varPath, inputTypePsi, resolver, context),
        true,
        inputFieldProjectionPsi,
        resolver,
        psiProcessingContext
    );
    referenceContext.ensureAllReferencesResolved(context);

    return new UpdateOperationDeclaration(
        operationName,
        Annotations.fromMap(annotations),
        fieldPath,
        fieldProjection,
        parseOutputProjection(
            OperationKind.UPDATE,
            operationName,
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

    Map<String, Annotation> annotations = null;

    SchemaOperationPath pathPsi = null;
    SchemaOperationDeleteProjection deleteProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaDeleteOperationBodyPart part : psi.getDeleteOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context);

      pathPsi = getPsiPart(pathPsi, part.getOperationPath(), "path", context);
      deleteProjectionPsi =
          getPsiPart(deleteProjectionPsi, part.getOperationDeleteProjection(), "delete projection", context);
      outputTypePsi = getPsiPart(outputTypePsi, part.getOperationOutputType(), "output type", context);
      outputProjectionPsi =
          getPsiPart(outputProjectionPsi, part.getOperationOutputProjection(), "output projection", context);
    }

    @Nullable OpFieldPath fieldPath =
        parsePath(OperationKind.DELETE, operationName, resourceType, pathPsi, resolver, context);

    if (deleteProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", psi, context);

    final @Nullable SchemaOpDeleteFieldProjection deleteFieldProjectionPsi =
        deleteProjectionPsi.getOpDeleteFieldProjection();
    if (deleteFieldProjectionPsi == null)
      throw new PsiProcessingException("Delete projection must be specified", deleteProjectionPsi, context);

    OpDeleteVarReferenceContext deleteReferenceContext = new OpDeleteVarReferenceContext(
        projectionsNamespace(context, OperationKind.DELETE, operationName).append("delete"),
        context.deleteVarReferenceContext()
    );

    OpInputVarReferenceContext inputReferenceContext =
        createInputVarReferenceContext(OperationKind.DELETE, operationName, context);

    OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
        context, inputReferenceContext
    );

    OpDeletePsiProcessingContext psiProcessingContext = new OpDeletePsiProcessingContext(
        context, inputPsiProcessingContext, deleteReferenceContext
    );

    final OpDeleteFieldProjection fieldProjection = OpDeleteProjectionsPsiParser.parseFieldProjection(
        resolveDeleteType(resourceType, fieldPath == null ? null : fieldPath.varProjection()),
        deleteFieldProjectionPsi,
        resolver,
        psiProcessingContext
    );
    inputReferenceContext.ensureAllReferencesResolved(context);
    deleteReferenceContext.ensureAllReferencesResolved(context);

    return new DeleteOperationDeclaration(
        operationName,
        Annotations.fromMap(annotations),
        fieldPath,
        fieldProjection,
        parseOutputProjection(
            OperationKind.DELETE,
            operationName,
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

    Map<String, Annotation> annotations = null;

    SchemaOperationMethod methodPsi = null;
    SchemaOperationPath pathPsi = null;
    SchemaOperationInputType inputTypePsi = null;
    SchemaOperationInputProjection inputProjectionPsi = null;
    SchemaOperationOutputType outputTypePsi = null;
    SchemaOperationOutputProjection outputProjectionPsi = null;

    final String operationName = parseOperationName(psi.getOperationName());

    for (SchemaCustomOperationBodyPart part : psi.getCustomOperationBodyPartList()) {
      annotations = SchemaProjectionPsiParserUtil.parseAnnotation(annotations, part.getAnnotation(), context);

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

    final @Nullable SchemaOpInputFieldProjection inputFieldProjectionPsi =
        inputProjectionPsi == null ? null : inputProjectionPsi.getOpInputFieldProjection();

    OpInputVarReferenceContext referenceContext =
        createInputVarReferenceContext(OperationKind.CUSTOM, operationName, context);

    OpInputPsiProcessingContext psiProcessingContext = new OpInputPsiProcessingContext(
        context, referenceContext
    );

    @Nullable OpFieldPath opPath =
        parsePath(OperationKind.CUSTOM, operationName, resourceType, pathPsi, resolver, context);

    final OpInputFieldProjection fieldProjection =
        inputFieldProjectionPsi == null ? null : OpInputProjectionsPsiParser.parseFieldProjection(
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
            psiProcessingContext
        );
    referenceContext.ensureAllReferencesResolved(context);

    return new CustomOperationDeclaration(
        method,
        operationName,
        Annotations.fromMap(annotations),
        opPath,
        fieldProjection,
        parseOutputProjection(
            OperationKind.CUSTOM,
            operationName,
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

  private static @NotNull OpOutputFieldProjection parseOutputProjection(
      final @NotNull OperationKind operationKind,
      final @Nullable String operationName,
      final @NotNull DataTypeApi outputType,
      final @Nullable SchemaOperationOutputProjection outputProjectionPsi,
      final @NotNull TypesResolver resolver,
      final @NotNull PsiElement location,
      final @NotNull ResourcePsiProcessingContext context)
      throws PsiProcessingException {

    final @Nullable SchemaOpOutputFieldProjection outputFieldProjectionPsi =
        outputProjectionPsi == null ? null : outputProjectionPsi.getOpOutputFieldProjection();

    OpOutputVarReferenceContext outputVarReferenceContext = new OpOutputVarReferenceContext(
        projectionsNamespace(context, operationKind, operationName).append("output"),
        context.outputVarReferenceContext()
    );
    OpInputVarReferenceContext inputVarReferenceContext =
        createInputVarReferenceContext(operationKind, operationName, context);

    OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
        context, inputVarReferenceContext
    );
    OpOutputPsiProcessingContext psiProcessingContext = new OpOutputPsiProcessingContext(
        context, inputPsiProcessingContext, outputVarReferenceContext
    );

    final OpOutputFieldProjection fieldProjection;

    // todo add context
    if (outputProjectionPsi == null || outputFieldProjectionPsi == null) {

      final @NotNull OpOutputVarProjection varProjection =
          OpOutputProjectionsPsiParser.createDefaultVarProjection(outputType, location, psiProcessingContext);

      fieldProjection = new OpOutputFieldProjection(
//          OpParams.EMPTY,
//          Annotations.EMPTY,
          varProjection,
          EpigraphPsiUtil.getLocation(location)
      );

//      throw new PsiProcessingException("Output projection must be specified", location, context);
    } else {
      fieldProjection = OpOutputProjectionsPsiParser.parseFieldProjection(
          outputType,
          outputFieldProjectionPsi,
          resolver,
          psiProcessingContext
      );
    }
    inputVarReferenceContext.ensureAllReferencesResolved(context);
    outputVarReferenceContext.ensureAllReferencesResolved(context);

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
      @NotNull OperationKind operationKind,
      @Nullable String operationName,
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
      OpInputVarReferenceContext inputVarReferenceContext =
          createInputVarReferenceContext(operationKind, operationName, context);

      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
          context, context.inputVarReferenceContext()
      );
      OpPathPsiProcessingContext psiProcessingContext =
          new OpPathPsiProcessingContext(context, inputPsiProcessingContext);

      final OpFieldPath fieldPath = OpPathPsiParser.parseFieldPath(type, varPathPsi, resolver, psiProcessingContext);
      inputVarReferenceContext.ensureAllReferencesResolved(context);

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

  private static @NotNull Qn projectionsNamespace(
      @NotNull ResourcePsiProcessingContext context,
      @NotNull OperationKind operationKind,
      @Nullable String operationName) {

    // keep logic in sync with ServiceNames.scala
    Qn qn = context.resourceNamespace()
        .append("operations")
        .append(operationKind.toString().toLowerCase());

    if (operationName != null)
      qn = qn.append(operationName.toLowerCase());

    return qn.append("projections");
  }

  private static @NotNull OpInputVarReferenceContext createInputVarReferenceContext(
      final @NotNull OperationKind operationKind,
      final @Nullable String operationName,
      final @NotNull ResourcePsiProcessingContext context) {

    return new OpInputVarReferenceContext(
        projectionsNamespace(context, operationKind, operationName).append("input"), context.inputVarReferenceContext()
    );

  }
}
