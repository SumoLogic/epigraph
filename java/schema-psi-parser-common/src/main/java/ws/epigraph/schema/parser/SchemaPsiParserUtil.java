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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.annotations.Annotation;
import ws.epigraph.annotations.Annotations;
import ws.epigraph.gdata.*;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypeReferenceFactory;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.schema.TypeRefs;
import ws.epigraph.schema.gdata.SchemaGDataPsiParser;
import ws.epigraph.schema.parser.psi.SchemaAnnotation;
import ws.epigraph.schema.parser.psi.SchemaDatum;
import ws.epigraph.schema.parser.psi.SchemaQnTypeRef;
import ws.epigraph.types.DatumTypeApi;
import ws.epigraph.types.PrimitiveTypeApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class SchemaPsiParserUtil {
  private SchemaPsiParserUtil() {}

  public static @Nullable Map<DatumTypeApi, Annotation> parseAnnotation(
      @Nullable Map<DatumTypeApi, Annotation> annotationsMap,
      @Nullable SchemaAnnotation annotationPsi,
      @NotNull PsiProcessingContext context,
      @NotNull TypesResolver typeResolver) throws PsiProcessingException {

    if (annotationPsi != null) {
      if (annotationsMap == null) annotationsMap = new HashMap<>();
      @Nullable SchemaDatum annotationValuePsi = annotationPsi.getDatum();
      SchemaQnTypeRef typeRefPsi = annotationPsi.getQnTypeRef();
      if (typeRefPsi == null)
        context.addError("Malformed annotation", annotationPsi);
      else {
        TypeRef typeRef = TypeRefs.fromPsi(typeRefPsi, context);
        DatumTypeApi datumType = typeRef.resolveDatumType(typeResolver);
        if (datumType == null)
          context.addError(String.format("Can't resolve annotation type '%s'", typeRef.toString()), typeRefPsi);
        else {
          annotationsMap.put(
              datumType,
              new Annotation(
                  datumType,
                  annotationValuePsi == null
                  ? createDefaultAnnotation(datumType, EpigraphPsiUtil.getLocation(annotationPsi))
                  : SchemaGDataPsiParser.parseDatum(annotationValuePsi, context),
                  EpigraphPsiUtil.getLocation(annotationPsi),
                  typeResolver
              )
          );
        }
      }
    }
    return annotationsMap;
  }

  private static @NotNull GDatum createDefaultAnnotation(@NotNull DatumTypeApi type, @NotNull TextLocation location) {
    switch (type.kind()) {
      case RECORD:
        return new GRecordDatum(TypeReferenceFactory.createReference(type), new LinkedHashMap<>(), location);
      case MAP:
        return new GMapDatum(TypeReferenceFactory.createReference(type), new LinkedHashMap<>(), location);
      case LIST:
        return new GListDatum(TypeReferenceFactory.createReference(type), Collections.emptyList(), location);
      case PRIMITIVE:
        PrimitiveTypeApi primitiveType = (PrimitiveTypeApi) type;
        final Object value;
        switch (primitiveType.primitiveKind()) {
          case BOOLEAN:
            value = true;
            break;
          case DOUBLE:
            value = 0d;
            break;
          case INTEGER:
            value = 0;
            break;
          case LONG:
            value = 0L;
            break;
          case STRING:
            value = "";
            break;
          default:
            throw new RuntimeException("Unknown primitive kind: " + primitiveType.primitiveKind());
        }
        return new GPrimitiveDatum(TypeReferenceFactory.createReference(type), value, location);

      default:
        throw new RuntimeException("Unknown type kind: " + type.kind());
    }
  }

  public static @NotNull Annotations parseAnnotations(
      @NotNull Stream<SchemaAnnotation> annotationsPsi,
      @NotNull PsiProcessingContext context,
      @NotNull TypesResolver typesResolver
  ) throws PsiProcessingException {

    return parseAnnotations(annotationsPsi.collect(Collectors.toList()), context, typesResolver);
  }

  public static @NotNull Annotations parseAnnotations(
      @NotNull Iterable<SchemaAnnotation> annotationsPsi,
      @NotNull PsiProcessingContext context,
      @NotNull TypesResolver typesResolver
  ) throws PsiProcessingException {

    @Nullable Map<DatumTypeApi, Annotation> annotationMap = null;
    for (final SchemaAnnotation annotationPsi : annotationsPsi) {
      annotationMap = parseAnnotation(annotationMap, annotationPsi, context, typesResolver);
    }

    return Annotations.fromMap(annotationMap);
  }
}
