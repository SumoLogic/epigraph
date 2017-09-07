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

package ws.epigraph.schema.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.delete.*;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.ImportAwareTypesResolver;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.schema.*;
import ws.epigraph.schema.operations.OperationDeclaration;
import ws.epigraph.schema.operations.OperationsPsiParser;
import ws.epigraph.schema.parser.psi.*;
import ws.epigraph.types.DataTypeApi;
import ws.epigraph.types.TypeApi;
import ws.epigraph.types.TypeKind;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ResourcesSchemaPsiParser {
  // todo transform projections after all references are resolved
  private ResourcesSchemaPsiParser() {}

  // currently we can get multiple ResourcesSchema instances for the same namespace (if there are multiple files for it)
  // not sure if there's any reason to merge them into one instance

  public static @NotNull ResourcesSchema parseResourcesSchema(
      @NotNull SchemaFile psi,
      @NotNull TypesResolver basicResolver,
      @NotNull SchemasPsiProcessingContext context) throws PsiProcessingException {

    @Nullable SchemaNamespaceDecl namespaceDeclPsi = PsiTreeUtil.getChildOfType(psi, SchemaNamespaceDecl.class);
    if (namespaceDeclPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, context);
    @Nullable SchemaQn namespaceFqnPsi = namespaceDeclPsi.getQn();
    if (namespaceFqnPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, context);

    Qn namespace = namespaceFqnPsi.getQn();

    SchemaPsiProcessingContext schemaProcessingContext = context.getSchemaPsiProcessingContext(namespace);

    TypesResolver resolver = new ImportAwareTypesResolver(namespace, parseImports(psi), basicResolver);

    SchemaDefs defs = psi.getDefs();

    parseGlobalProjections(namespace, defs, schemaProcessingContext, resolver);

    Map<String, ResourceDeclaration> resources = parseResources(namespace, defs, schemaProcessingContext, resolver);
    Map<String, TransformerDeclaration> transformers =
        parseTransformers(defs, schemaProcessingContext, resolver);

    return new ResourcesSchema(namespace, resources, transformers);
  }

  private static void parseGlobalProjections(
      final @NotNull Qn namespace,
      final @Nullable SchemaDefs defs,
      final @NotNull ReferenceAwarePsiProcessingContext schemaProcessingContext,
      final @NotNull TypesResolver resolver) {

    if (defs != null) {
      for (final SchemaProjectionDef projectionDefPsi : defs.getProjectionDefList()) {
        try {
          parseProjectionDef(namespace, null, projectionDefPsi, resolver, schemaProcessingContext);
        } catch (PsiProcessingException e) {
          schemaProcessingContext.addException(e);
        }
      }
    }
  }

  private static @NotNull Map<String, TransformerDeclaration> parseTransformers(
      final @Nullable SchemaDefs defs,
      final @NotNull SchemaPsiProcessingContext schemaProcessingContext,
      final @NotNull TypesResolver resolver) {

    final Map<String, TransformerDeclaration> transformers;

    if (defs == null) transformers = Collections.emptyMap();
    else {
      transformers = new LinkedHashMap<>();
      for (final SchemaTransformerDef transformerDefPsi : defs.getTransformerDefList()) {
        if (transformerDefPsi != null) {
          try {
            TransformerDeclaration transformer =
                parseTransformer(transformerDefPsi, resolver, schemaProcessingContext);
            transformers.put(transformer.name(), transformer);
          } catch (PsiProcessingException e) {
            schemaProcessingContext.addException(e);
          }
        }
      }
    }

    return transformers;
  }

  private static @NotNull Map<String, ResourceDeclaration> parseResources(
      final @NotNull Qn namespace,
      final @Nullable SchemaDefs defs,
      final @NotNull SchemaPsiProcessingContext schemaProcessingContext,
      final @NotNull TypesResolver resolver) {

    final Map<String, ResourceDeclaration> resources;

    if (defs == null) resources = Collections.emptyMap();
    else {
      resources = new LinkedHashMap<>();
      for (SchemaResourceDef resourceDefPsi : defs.getResourceDefList()) {
        if (resourceDefPsi != null) {
          try {
            ResourceDeclaration resource = parseResource(namespace, resourceDefPsi, resolver, schemaProcessingContext);
            resources.put(resource.fieldName(), resource);
          } catch (PsiProcessingException e) {
            schemaProcessingContext.addException(e);
          }
        }
      }
    }

    return resources;
  }

  private static @NotNull List<Qn> parseImports(@NotNull PsiElement psi) {
    final @Nullable SchemaImports importsPsi = PsiTreeUtil.getChildOfType(psi, SchemaImports.class);
    if (importsPsi == null) return Collections.emptyList();

    final @NotNull Collection<SchemaImportStatement> importStatementsPsi = importsPsi.getImportStatementList();

    if (importStatementsPsi.isEmpty()) return Collections.emptyList();

    return importStatementsPsi
        .stream()
        .filter(Objects::nonNull)
        .map(SchemaImportStatement::getQn)
        .filter(Objects::nonNull)
        .map(SchemaQn::getQn)
        .collect(Collectors.toList());
  }

  private static TransformerDeclaration parseTransformer(
      @NotNull SchemaTransformerDef psi,
      @NotNull TypesResolver resolver,
      @NotNull SchemaPsiProcessingContext context) throws PsiProcessingException {

    SchemaTransformerName transformerNamePsi = psi.getTransformerName();
    if (transformerNamePsi == null)
      throw new PsiProcessingException("Resource name not specified", psi, context);

    String transformerName = transformerNamePsi.getQid().getCanonicalName();

    @Nullable TransformerDeclaration existingDeclaration = context.transformer(transformerName);
    if (existingDeclaration != null) {
      throw new PsiProcessingException(
          String.format("Transformer '%s' is already declared at %s", transformerName, existingDeclaration.location()),
          psi,
          context
      );
    }

    @Nullable SchemaTransformerType typePsi = psi.getTransformerType();
    if (typePsi == null)
      throw new PsiProcessingException(
          String.format("Transformer '%s' type not specified", transformerName),
          psi, context
      );

    SchemaTypeRef typeRefPsi = typePsi.getTypeRef();
    TypeRef typeRef = TypeRefs.fromPsi(typeRefPsi, context);
    TypeApi transformerType = typeRef.resolve(resolver);

    if (transformerType == null)
      throw new PsiProcessingException(
          String.format("Can't resolve transformer '%s' type '%s'", transformerName, typeRef),
          typeRefPsi,
          context
      );

    final Annotations annotations = SchemaPsiParserUtil.parseAnnotations(
        psi.getTransformerBodyPartList()
            .stream()
            .map(SchemaTransformerBodyPart::getAnnotation)
            .filter(Objects::nonNull),
        context,
        resolver
    );

    OpInputVarProjection inputProjection = null;
    OpOutputVarProjection outputProjection = null;

    for (final SchemaTransformerBodyPart bodyPart : psi.getTransformerBodyPartList()) {

      SchemaTransformerInputProjection transformerInputProjectionPsi = bodyPart.getTransformerInputProjection();
      if (transformerInputProjectionPsi != null) {

        if (inputProjection == null) {
          SchemaOpInputFieldProjection fieldProjectionPsi = transformerInputProjectionPsi.getOpInputFieldProjection();
          SchemaOpInputVarProjection varProjectionPsi =
              fieldProjectionPsi == null ? null : fieldProjectionPsi.getOpInputVarProjection();

          if (varProjectionPsi == null) {
            context.addError(
                String.format("Input projection for transformer '%s' is not defined", transformerName),
                transformerInputProjectionPsi
            );
          } else {
            inputProjection = OpInputProjectionsPsiParser.parseVarProjection(
                transformerType.dataType(),
                varProjectionPsi,
                resolver,
                new OpInputPsiProcessingContext(context, context.inputReferenceContext())
            );
          }
        } else {
          context.addError(
              String.format("Input projection is already defined for transformer '%s' at %s",
                  transformerName, inputProjection.location()
              ),
              transformerInputProjectionPsi
          );
        }
      }

      SchemaTransformerOutputProjection transformerOutputProjectionPsi = bodyPart.getTransformerOutputProjection();
      if (transformerOutputProjectionPsi != null) {

        if (outputProjection == null) {
          SchemaOpOutputFieldProjection fieldProjectionPsi =
              transformerOutputProjectionPsi.getOpOutputFieldProjection();
          SchemaOpOutputVarProjection varProjectionPsi =
              fieldProjectionPsi == null ? null : fieldProjectionPsi.getOpOutputVarProjection();

          if (varProjectionPsi == null) {
            context.addError(
                String.format("Output projection for transformer '%s' is not defined", transformerName),
                transformerOutputProjectionPsi
            );
          } else {
            outputProjection = OpProjectionsPsiParser.parseVarProjection(
                transformerType.dataType(),
                false,
                varProjectionPsi,
                resolver,
                new OpOutputPsiProcessingContext(
                    context,
                    new OpInputPsiProcessingContext(context, context.inputReferenceContext()),
                    context.outputReferenceContext()
                )
            );
          }
        } else {
          context.addError(
              String.format("Output projection is already defined for transformer '%s' at %s",
                  transformerName, outputProjection.location()
              ),
              transformerOutputProjectionPsi
          );
        }
      }

    }

    if (inputProjection == null)
      throw new PsiProcessingException(
          String.format("Input projection for transformer '%s' is not specified", transformerName),
          psi,
          context
      );

    if (outputProjection == null)
      throw new PsiProcessingException(
          String.format("Output projection for transformer '%s' is not specified", transformerName),
          psi,
          context
      );

    TransformerDeclaration transformerDeclaration = new TransformerDeclaration(
        transformerName,
        transformerType,
        annotations,
        inputProjection,
        outputProjection,
        EpigraphPsiUtil.getLocation(psi)
    );

    context.addTransformer(transformerDeclaration);

    return transformerDeclaration;
  }

  private static ResourceDeclaration parseResource(
      @NotNull Qn namespace,
      @NotNull SchemaResourceDef psi,
      @NotNull TypesResolver resolver,
      @NotNull SchemaPsiProcessingContext context) throws PsiProcessingException {

    final SchemaResourceName resourceName = psi.getResourceName();
    if (resourceName == null)
      throw new PsiProcessingException("Resource name not specified", psi, context);

    final String fieldName = resourceName.getQid().getCanonicalName();

    ResourceDeclaration existingDeclaration = context.resource(fieldName);
    if (existingDeclaration != null) {
      throw new PsiProcessingException(
          String.format("Resource '%s' is already declared at %s", fieldName, existingDeclaration.location()),
          psi,
          context
      );
    }

    SchemaResourceType resourceTypePsi = psi.getResourceType();
    if (resourceTypePsi == null)
      throw new PsiProcessingException(
          String.format("Resource '%s' type not specified", fieldName),
          psi, context
      );

    @NotNull SchemaValueTypeRef valueTypeRefPsi = resourceTypePsi.getValueTypeRef();
    @NotNull ValueTypeRef valueTypeRef = TypeRefs.fromPsi(valueTypeRefPsi, context);
    @Nullable DataTypeApi resourceType = resolver.resolve(valueTypeRef);

    if (resourceType == null) throw new PsiProcessingException(
        String.format("Can't resolve resource '%s' type '%s'", fieldName, valueTypeRef),
        resourceTypePsi,
        context
    );

    // convert datum kind to samovar
    @NotNull TypeApi type = resourceType.type();
    if (resourceType.defaultTag() == null && valueTypeRef.defaultOverride() == null && type.kind() != TypeKind.ENTITY) {
      resourceType = type.dataType();
    }

    ResourcePsiProcessingContext resourcePsiProcessingContext = new ResourcePsiProcessingContext(
        context, namespace, fieldName
    );

    final Iterable<SchemaProjectionDef> projectionDefsPsi = psi.getProjectionDefList();
    for (final SchemaProjectionDef projectionDefPsi : projectionDefsPsi) {
      try {
        parseProjectionDef(namespace, fieldName, projectionDefPsi, resolver, resourcePsiProcessingContext);
      } catch (PsiProcessingException e) {
        context.addException(e);
      }
    }

    @NotNull Collection<SchemaOperationDef> operationDefsPsi = psi.getOperationDefList();

    final List<OperationDeclaration> operations = new ArrayList<>(operationDefsPsi.size());
    for (SchemaOperationDef defPsi : operationDefsPsi)
      try {
        operations.add(
            OperationsPsiParser.parseOperation(
                resourceType,
                defPsi,
                resolver,
                resourcePsiProcessingContext
            )
        );
      } catch (PsiProcessingException e) {
        context.addException(e);
      }

    resourcePsiProcessingContext.inputReferenceContext().ensureAllReferencesResolved();
    resourcePsiProcessingContext.outputReferenceContext().ensureAllReferencesResolved();
    resourcePsiProcessingContext.deleteReferenceContext().ensureAllReferencesResolved();

    // do we allow to multiple declarations for the same resource, merging them into one here?

    final ResourceDeclaration resourceDeclaration = new ResourceDeclaration(
        fieldName, resourceType, operations, EpigraphPsiUtil.getLocation(psi)
    );

    final List<ResourceDeclarationError> resourceDeclarationErrors = new ArrayList<>();
    resourceDeclaration.validate(resourceDeclarationErrors);
    for (ResourceDeclarationError resourceDeclarationError : resourceDeclarationErrors) {
      context.addError(
          resourceDeclarationError.message(),
          resourceDeclarationError.location()
      );
    }

    context.addResource(resourceDeclaration);

    return resourceDeclaration;
  }

  private static void parseProjectionDef(
      @NotNull Qn namespace,
      @Nullable String resourceName,
      @NotNull SchemaProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReferenceAwarePsiProcessingContext context) throws PsiProcessingException {

    final SchemaOutputProjectionDef outputProjectionDef = projectionDefPsi.getOutputProjectionDef();
    final SchemaInputProjectionDef inputProjectionDef = projectionDefPsi.getInputProjectionDef();
    final SchemaDeleteProjectionDef deleteProjectionDef = projectionDefPsi.getDeleteProjectionDef();

    if (outputProjectionDef != null) {
      parseOutputProjectionDef(namespace, resourceName, outputProjectionDef, resolver, context);
    } else if (inputProjectionDef != null) {
      parseInputProjectionDef(namespace, resourceName, inputProjectionDef, resolver, context);
    } else if (deleteProjectionDef != null) {
      parseDeleteProjectionDef(namespace, resourceName, deleteProjectionDef, resolver, context);
    } else {
      context.addError("Incomplete projection definition", projectionDefPsi);
    }
  }

  private static void parseOutputProjectionDef(
      @NotNull Qn namespace,
      @Nullable String resourceName,
      @NotNull SchemaOutputProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReferenceAwarePsiProcessingContext context) throws PsiProcessingException {

    parseGenProjectionDef(
        "output",
        projectionDefPsi,
        projectionDefPsi.getQid(),
        projectionDefPsi.getTypeRef(),
        projectionDefPsi.getOpOutputUnnamedOrRefVarProjection(),
        resolver,
        context.outputReferenceContext(),
        projectionName -> new OpOutputReferenceContext(
            ProjectionReferenceName.fromQn(
                resourceName == null ?
                new Namespaces(namespace).outputProjectionNamespace(projectionName) :
                new Namespaces(namespace).outputProjectionNamespace(resourceName, projectionName)
            ),
            context.outputReferenceContext(),
            context
        ),
        context,
        OutputUnnamedVarReferenceParser.INSTANCE
    );

  }

  private static void parseInputProjectionDef(
      @NotNull Qn namespace,
      @Nullable String resourceName,
      @NotNull SchemaInputProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReferenceAwarePsiProcessingContext context) throws PsiProcessingException {

    parseGenProjectionDef(
        "input",
        projectionDefPsi,
        projectionDefPsi.getQid(),
        projectionDefPsi.getTypeRef(),
        projectionDefPsi.getOpInputUnnamedOrRefVarProjection(),
        resolver,
        context.inputReferenceContext(),
        projectionName -> new OpInputReferenceContext(
            ProjectionReferenceName.fromQn(
                resourceName == null ?
                new Namespaces(namespace).inputProjectionNamespace(projectionName) :
                new Namespaces(namespace).inputProjectionNamespace(resourceName, projectionName)
            ),
            context.inputReferenceContext(),
            context
        ),
        context,
        InputUnnamedVarReferenceParser.INSTANCE
    );

  }

  private static void parseDeleteProjectionDef(
      @NotNull Qn namespace,
      @Nullable String resourceName,
      @NotNull SchemaDeleteProjectionDef projectionDefPsi,
      @NotNull TypesResolver resolver,
      @NotNull ReferenceAwarePsiProcessingContext context) throws PsiProcessingException {

    parseGenProjectionDef(
        "delete",
        projectionDefPsi,
        projectionDefPsi.getQid(),
        projectionDefPsi.getTypeRef(),
        projectionDefPsi,
        resolver,
        context.deleteReferenceContext(),
        projectionName -> new OpDeleteReferenceContext(
            ProjectionReferenceName.fromQn(
                resourceName == null ?
                new Namespaces(namespace).deleteProjectionNamespace(projectionName) :
                new Namespaces(namespace).deleteProjectionNamespace(resourceName, projectionName)
            ),
            context.deleteReferenceContext(),
            context
        ),
        context,
        DeleteUnnamedVarReferenceParser.INSTANCE
    );

  }

  private static <
      VP extends GenVarProjection<VP, ?, MP>,
      MP extends GenModelProjection<?, ?, ?, ?>,
      RC extends ReferenceContext<VP, MP>,
      UP extends PsiElement>
  void parseGenProjectionDef(
      @NotNull String projectionKind,
      @NotNull PsiElement projectionDefPsi,
      @Nullable SchemaQid projectionNamePsi,
      @Nullable SchemaTypeRef typeRefPsi,
      @Nullable UP unnamedPsi,
      @NotNull TypesResolver resolver,
      @NotNull RC referenceContext,
      @NotNull Function<String, RC> innerReferenceContextFactory,
      @NotNull ReferenceAwarePsiProcessingContext context,
      @NotNull UnnamedVarParser<VP, MP, RC, UP> psiParser) throws PsiProcessingException {

    if (projectionNamePsi == null)
      context.addError("Incomplete " + projectionKind + " projection definition: name not specified", projectionDefPsi);
    else {
      final String projectionName = projectionNamePsi.getCanonicalName();

      String projectionKindUp = projectionKind.substring(0, 1).toUpperCase() + projectionKind.substring(1);
      if (referenceContext.isResolved(projectionName))
        context.addError(
            String.format(projectionKindUp + " projection '%s' is already defined", projectionName),
            projectionDefPsi
        );
      else {
        if (typeRefPsi == null || unnamedPsi == null)
          context.addError(
              String.format("Incomplete " + projectionKind + " projection '%s' definition", projectionName),
              projectionDefPsi
          );
        else {
          final TypeRef typeRef = TypeRefs.fromPsi(typeRefPsi, context);
          final TypeApi type = typeRef.resolve(resolver);
          if (type == null)
            context.addError(
                String.format(
                    projectionKindUp + " projection '%s' type '%s' not defined",
                    projectionName,
                    typeRef.toString()
                ),
                projectionDefPsi
            );
          else {
            //final VP reference =
            referenceContext.entityReference(type, projectionName, false, EpigraphPsiUtil.getLocation(projectionDefPsi));

            final RC innerReferenceContext = innerReferenceContextFactory.apply(projectionName);
            final VP value = psiParser.parse(
                type.dataType(),
                unnamedPsi,
                resolver,
                innerReferenceContext,
                context
            );
            innerReferenceContext.ensureAllReferencesResolved();

            referenceContext.resolveEntityRef(projectionName, value, EpigraphPsiUtil.getLocation(unnamedPsi));
          }
        }
      }
    }
  }

  private interface UnnamedVarParser<
      VP extends GenVarProjection<VP, ?, MP>,
      MP extends GenModelProjection<?, ?, ?, ?>,
      RC extends ReferenceContext<VP, MP>,
      UP extends PsiElement
      > {
    VP parse(
        @NotNull DataTypeApi type,
        @NotNull UP psi,
        @NotNull TypesResolver resolver,
        @NotNull RC referenceContext,
        @NotNull ReferenceAwarePsiProcessingContext context
    ) throws PsiProcessingException;
  }

  private static class OutputUnnamedVarReferenceParser implements UnnamedVarParser<
      OpOutputVarProjection,
      OpOutputModelProjection<?, ?, ?, ?>,
      OpOutputReferenceContext,
      SchemaOpOutputUnnamedOrRefVarProjection> {

    static final OutputUnnamedVarReferenceParser INSTANCE = new OutputUnnamedVarReferenceParser();

    @Override
    public OpOutputVarProjection parse(
        final @NotNull DataTypeApi type,
        final @NotNull SchemaOpOutputUnnamedOrRefVarProjection psi,
        final @NotNull TypesResolver resolver,
        final @NotNull OpOutputReferenceContext referenceContext,
        final @NotNull ReferenceAwarePsiProcessingContext context) throws PsiProcessingException {

      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
          context,
          context.inputReferenceContext()
      );
      OpOutputPsiProcessingContext outputPsiProcessingContext = new OpOutputPsiProcessingContext(
          context,
          inputPsiProcessingContext,
          referenceContext
      );

      return OpProjectionsPsiParser.parseUnnamedOrRefVarProjection(
          type,
          false,
          psi,
          resolver,
          outputPsiProcessingContext
      );
    }
  }

  private static class InputUnnamedVarReferenceParser implements UnnamedVarParser<
      OpInputVarProjection,
      OpInputModelProjection<?, ?, ?, ?>,
      OpInputReferenceContext,
      SchemaOpInputUnnamedOrRefVarProjection> {

    static final InputUnnamedVarReferenceParser INSTANCE = new InputUnnamedVarReferenceParser();

    @Override
    public OpInputVarProjection parse(
        final @NotNull DataTypeApi type,
        final @NotNull SchemaOpInputUnnamedOrRefVarProjection psi,
        final @NotNull TypesResolver resolver,
        final @NotNull OpInputReferenceContext referenceContext,
        final @NotNull ReferenceAwarePsiProcessingContext context) throws PsiProcessingException {

      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
          context,
          referenceContext
      );

      return OpInputProjectionsPsiParser.parseUnnamedOrRefVarProjection(
          type,
          psi,
          resolver,
          inputPsiProcessingContext
      );
    }
  }

  private static class DeleteUnnamedVarReferenceParser implements UnnamedVarParser<
      OpDeleteVarProjection,
      OpDeleteModelProjection<?, ?, ?>,
      OpDeleteReferenceContext,
      SchemaDeleteProjectionDef> {

    static final DeleteUnnamedVarReferenceParser INSTANCE = new DeleteUnnamedVarReferenceParser();

    @Override
    public OpDeleteVarProjection parse(
        final @NotNull DataTypeApi type,
        final @NotNull SchemaDeleteProjectionDef psi,
        final @NotNull TypesResolver resolver,
        final @NotNull OpDeleteReferenceContext referenceContext,
        final @NotNull ReferenceAwarePsiProcessingContext context) throws PsiProcessingException {

      OpInputPsiProcessingContext inputPsiProcessingContext = new OpInputPsiProcessingContext(
          context,
          context.inputReferenceContext()
      );
      OpDeletePsiProcessingContext deletePsiProcessingContext = new OpDeletePsiProcessingContext(
          context,
          inputPsiProcessingContext,
          referenceContext
      );

      final SchemaOpDeleteUnnamedOrRefVarProjection varProjection = psi.getOpDeleteUnnamedOrRefVarProjection();
      assert varProjection != null;

      return OpDeleteProjectionsPsiParser.parseUnnamedOrRefVarProjection(
          type,
          psi.getPlus() != null,
          varProjection,
          resolver,
          deletePsiProcessingContext
      );
    }
  }

}
