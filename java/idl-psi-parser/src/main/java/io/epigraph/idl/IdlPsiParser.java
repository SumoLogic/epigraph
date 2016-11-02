package io.epigraph.idl;

import com.intellij.psi.util.PsiTreeUtil;
import io.epigraph.idl.operations.OperationIdl;
import io.epigraph.idl.operations.OperationsPsiParser;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.Qn;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingError;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.ImportAwareTypesResolver;
import io.epigraph.refs.TypesResolver;
import io.epigraph.refs.ValueTypeRef;
import io.epigraph.types.DataType;
import io.epigraph.types.DatumType;
import io.epigraph.types.Type;
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
    @NotNull ValueTypeRef valueTypeRef = TypeRefs.fromPsi(valueTypeRefPsi);
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
