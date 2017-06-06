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
import ws.epigraph.lang.Qn;
import ws.epigraph.projections.ReferenceContext;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenVarProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.delete.*;
import ws.epigraph.projections.op.input.*;
import ws.epigraph.projections.op.output.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
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
public final class ResourcesSchemaPsiParser { // todo this must be ported to scala/ctypes
  private ResourcesSchemaPsiParser() {}

  public static @NotNull ResourcesSchema parseResourcesSchema(
      @NotNull SchemaFile psi,
      @NotNull TypesResolver basicResolver,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    @Nullable SchemaNamespaceDecl namespaceDeclPsi = PsiTreeUtil.getChildOfType(psi, SchemaNamespaceDecl.class);
    if (namespaceDeclPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, context);
    @Nullable SchemaQn namespaceFqnPsi = namespaceDeclPsi.getQn();
    if (namespaceFqnPsi == null)
      throw new PsiProcessingException("namespace not specified", psi, context);

    Qn namespace = namespaceFqnPsi.getQn();

    SchemaPsiProcessingContext schemaProcessingContext = new SchemaPsiProcessingContext(context, namespace);

    TypesResolver resolver = new ImportAwareTypesResolver(namespace, parseImports(psi), basicResolver);

    SchemaDefs defs = psi.getDefs();

    parseGlobalProjections(namespace, defs, schemaProcessingContext, resolver);

    Map<String, ResourceDeclaration> resources = parseResources(namespace, defs, schemaProcessingContext, resolver);

    schemaProcessingContext.inputReferenceContext().ensureAllReferencesResolved();
    schemaProcessingContext.outputReferenceContext().ensureAllReferencesResolved();
    schemaProcessingContext.deleteReferenceContext().ensureAllReferencesResolved();

    return new ResourcesSchema(namespace, resources, Collections.emptyMap());
  }

  private static void parseGlobalProjections(
      final @NotNull Qn namespace,
      final @Nullable SchemaDefs defs,
      final @NotNull SchemaPsiProcessingContext schemaProcessingContext,
      final @NotNull TypesResolver resolver) {

    // todo should work across multiple files

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

  private static @NotNull Map<String, ResourceDeclaration> parseResources(
      final @NotNull Qn namespace,
      final @Nullable SchemaDefs defs,
      final @NotNull SchemaPsiProcessingContext schemaProcessingContext,
      final @NotNull TypesResolver resolver) {


    final Map<String, ResourceDeclaration> resources;

    if (defs == null) resources = Collections.emptyMap();
    else {
      resources = new HashMap<>();
      for (SchemaResourceDef resourceDefPsi : defs.getResourceDefList()) {
        if (resourceDefPsi != null) {
          try {
            ResourceDeclaration resource = parseResource(namespace, resourceDefPsi, resolver, schemaProcessingContext);
            String fieldName = resource.fieldName();
            if (resources.containsKey(fieldName))
              schemaProcessingContext.addError("Resource '" + fieldName + "' is already defined", resourceDefPsi);
            else
              resources.put(fieldName, resource);
          } catch (PsiProcessingException e) {
            schemaProcessingContext.addException(e);
          }
        }
      }
    }

    return resources;
  }

  private static @NotNull List<Qn> parseImports(@NotNull SchemaFile idlPsi) {
    final @Nullable SchemaImports importsPsi = PsiTreeUtil.getChildOfType(idlPsi, SchemaImports.class);
    if (importsPsi == null) return Collections.emptyList();

    final @NotNull List<SchemaImportStatement> importStatementsPsi = importsPsi.getImportStatementList();

    if (importStatementsPsi.isEmpty()) return Collections.emptyList();

    return importStatementsPsi
        .stream()
        .filter(Objects::nonNull)
        .map(SchemaImportStatement::getQn)
        .filter(Objects::nonNull)
        .map(SchemaQn::getQn)
        .collect(Collectors.toList());
  }

  public static ResourceDeclaration parseResource(
      @NotNull Qn namespace,
      @NotNull SchemaResourceDef psi,
      @NotNull TypesResolver resolver,
      @NotNull SchemaPsiProcessingContext context) throws PsiProcessingException {

    final SchemaResourceName resourceName = psi.getResourceName();
    if (resourceName == null) throw new PsiProcessingException(
        "Resource name not specified", psi, context
    );

    final String fieldName = resourceName.getQid().getCanonicalName();

    SchemaResourceType resourceTypePsi = psi.getResourceType();
    if (resourceTypePsi == null) throw new PsiProcessingException(
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
    if (resourceType.defaultTag() == null && valueTypeRef.defaultOverride() == null && type.kind() != TypeKind.UNION) {
      resourceType = type.dataType();
    }

    ResourcePsiProcessingContext resourcePsiProcessingContext = new ResourcePsiProcessingContext(
        context, namespace, fieldName
    );

    final List<SchemaProjectionDef> projectionDefsPsi = psi.getProjectionDefList();
    for (final SchemaProjectionDef projectionDefPsi : projectionDefsPsi) {
      try {
        parseProjectionDef(namespace, fieldName, projectionDefPsi, resolver, resourcePsiProcessingContext);
      } catch (PsiProcessingException e) {
        context.addException(e);
      }
    }

    @NotNull List<SchemaOperationDef> operationDefsPsi = psi.getOperationDefList();

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

    final ResourceDeclaration resourceDeclaration = new ResourceDeclaration(
        fieldName, resourceType, operations, EpigraphPsiUtil.getLocation(psi)
    );

    final ArrayList<ResourceDeclarationError> resourceDeclarationErrors = new ArrayList<>();
    resourceDeclaration.validate(resourceDeclarationErrors);
    for (ResourceDeclarationError resourceDeclarationError : resourceDeclarationErrors) {
      context.addError(
          resourceDeclarationError.message(),
          resourceDeclarationError.location()
      );
    }

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

    String projectionKindUp = projectionKind.substring(0, 1).toUpperCase() + projectionKind.substring(1);

    if (projectionNamePsi == null)
      context.addError("Incomplete " + projectionKind + " projection definition: name not specified", projectionDefPsi);
    else {
      final String projectionName = projectionNamePsi.getCanonicalName();

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
            referenceContext.varReference(type, projectionName, false, EpigraphPsiUtil.getLocation(projectionDefPsi));

            final RC innerReferenceContext = innerReferenceContextFactory.apply(projectionName);
            final VP value = psiParser.parse(
                type.dataType(),
                unnamedPsi,
                resolver,
                innerReferenceContext,
                context
            );
            innerReferenceContext.ensureAllReferencesResolved();

            referenceContext.resolve(projectionName, value, EpigraphPsiUtil.getLocation(unnamedPsi), context);
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
      OpOutputModelProjection<?, ?, ?>,
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

      return OpOutputProjectionsPsiParser.parseUnnamedOrRefVarProjection(
          type,
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
