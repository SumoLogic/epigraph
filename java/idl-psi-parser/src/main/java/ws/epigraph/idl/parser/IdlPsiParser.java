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

package ws.epigraph.idl.parser;

import com.intellij.psi.util.PsiTreeUtil;
import ws.epigraph.idl.Idl;
import ws.epigraph.idl.ResourceIdl;
import ws.epigraph.idl.TypeRefs;
import ws.epigraph.idl.operations.OperationIdl;
import ws.epigraph.idl.operations.OperationsPsiParser;
import ws.epigraph.idl.parser.psi.*;
import ws.epigraph.lang.Qn;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.ImportAwareTypesResolver;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.refs.ValueTypeRef;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlPsiParser {
  @NotNull
  public static Idl parseIdl(
      @NotNull IdlFile idlPsi,
      @NotNull TypesResolver basicResolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    @Nullable IdlNamespaceDecl namespaceDeclPsi = PsiTreeUtil.getChildOfType(idlPsi, IdlNamespaceDecl.class);
    if (namespaceDeclPsi == null)
      throw new PsiProcessingException("namespace not specified", idlPsi, errors);
    @Nullable IdlQn namespaceFqnPsi = namespaceDeclPsi.getQn();
    if (namespaceFqnPsi == null)
      throw new PsiProcessingException("namespace not specified", idlPsi, errors);

    Qn namespace = namespaceFqnPsi.getQn();

    TypesResolver resolver = new ImportAwareTypesResolver(namespace, parseImports(idlPsi), basicResolver);

    @Nullable IdlResourceDef[] resourceDefsPsi = PsiTreeUtil.getChildrenOfType(idlPsi, IdlResourceDef.class);

    final Map<String, ResourceIdl> resources;

    if (resourceDefsPsi != null) {
      resources = new HashMap<>();
      for (IdlResourceDef resourceDefPsi : resourceDefsPsi) {
        if (resourceDefPsi != null) {
          try {
            ResourceIdl resource = parseResource(resourceDefPsi, resolver, errors);
            String fieldName = resource.fieldName();
            if (resources.containsKey(fieldName))
              errors.add(new PsiProcessingError("Resource '" + fieldName + "' is already defined", resourceDefPsi));
            else
              resources.put(fieldName, resource);
          } catch (PsiProcessingException e) {
            errors.add(e.toError());
          }
        }
      }
    } else
      resources = Collections.emptyMap();

    return new Idl(namespace, resources);
  }

  @NotNull
  private static List<Qn> parseImports(@NotNull IdlFile idlPsi) {
    @Nullable final IdlImports importsPsi = PsiTreeUtil.getChildOfType(idlPsi, IdlImports.class);
    if (importsPsi == null) return Collections.emptyList();

    @NotNull final List<IdlImportStatement> importStatementsPsi = importsPsi.getImportStatementList();

    if (importStatementsPsi.isEmpty()) return Collections.emptyList();

    return importStatementsPsi
        .stream()
        .filter(Objects::nonNull)
        .map(IdlImportStatement::getQn)
        .filter(Objects::nonNull)
        .map(IdlQn::getQn)
        .collect(Collectors.toList());
  }

  public static ResourceIdl parseResource(
      @NotNull IdlResourceDef psi,
      @NotNull TypesResolver resolver,
      @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {

    final String fieldName = psi.getResourceName().getQid().getCanonicalName();

    @NotNull IdlResourceType resourceTypePsi = psi.getResourceType();

    @NotNull IdlValueTypeRef valueTypeRefPsi = resourceTypePsi.getValueTypeRef();
    @NotNull ValueTypeRef valueTypeRef = TypeRefs.fromPsi(valueTypeRefPsi, errors);
    @Nullable DataType resourceType = resolver.resolve(valueTypeRef);

    if (resourceType == null) throw new PsiProcessingException(
        String.format("Can't resolve resource '%s' kind '%s'", fieldName, valueTypeRef),
        resourceTypePsi,
        errors
    );

    // convert datum kind to samovar
    @NotNull Type type = resourceType.type;
    if (resourceType.defaultTag == null && valueTypeRef.defaultOverride() == null && type instanceof DatumType) {
      resourceType = new DataType(type, ((DatumType) type).self);
    }

    @NotNull List<IdlOperationDef> defsPsi = psi.getOperationDefList();

    final List<OperationIdl> operations = new ArrayList<>(defsPsi.size());
    for (IdlOperationDef defPsi : defsPsi)
      try {
        operations.add(OperationsPsiParser.parseOperation(resourceType, defPsi, resolver, errors));
      } catch (PsiProcessingException e) {
        errors.add(e.toError());
      }

    return new ResourceIdl(
        fieldName, resourceType, operations, EpigraphPsiUtil.getLocation(psi)
    );
  }
}
