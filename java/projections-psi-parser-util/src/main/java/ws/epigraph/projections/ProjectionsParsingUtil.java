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

package ws.epigraph.projections;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.gen.GenModelProjection;
import ws.epigraph.projections.gen.GenTagProjectionEntry;
import ws.epigraph.projections.gen.GenEntityProjection;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;
import ws.epigraph.util.WordUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class ProjectionsParsingUtil {

  // todo rename to ProjectionsUtil
  // todo take MessagesContext instead of PsiProcessingContext

  private ProjectionsParsingUtil() {}

  /**
   * Gets tag instance looking for it in the following order:
   * <ul>
   * <li>if {@code tagName} is not null: look up by tag name</li>
   * <li>else if retro tag is present: use it</li>
   * <li>else if this is a datum type: use self tag</li>
   * <li>else raise an error</li>
   * </ul>
   *
   * @param dataType data type with optional retro tag
   * @param tagName  optional tag name
   * @param op       optional operation projection for extra checks
   * @param location location for error message
   * @param context  psi processing context
   *
   * @return tag instance
   * @throws PsiProcessingException in case tag can't be found
   */
  public static <
      MP extends GenModelProjection<?, ?, ?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenEntityProjection<VP, TP, MP>
      >
  @NotNull TagApi getTag(
      @NotNull DataTypeApi dataType,
      @Nullable String tagName,
      @Nullable VP op,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final TagApi tag = findTag(dataType, tagName, op, location, context);

    if (tag == null)
      throw noRetroTagError(dataType, location, context);
    return tag;
  }

  public static PsiProcessingException noRetroTagError(
      final @NotNull DataTypeApi dataType,
      final @NotNull TextLocation location,
      final @NotNull PsiProcessingContext context) {

    return new PsiProcessingException(
        String.format("Can't parse projection for '%s', retro tag not specified", dataType.name()),
        location,
        context
    );
  }

  /**
   * Finds tag instance looking for it in the following order:
   * <ul>
   * <li>if {@code tagName} is not null: look up by tag name</li>
   * <li>else if retro tag is present: use it</li>
   * <li>else if this is a datum type: use self tag</li>
   * <li>else return null</li>
   * </ul>
   *
   * @param dataType data type with optional retro tag
   * @param tagName  optional tag name
   * @param op       optional operation projection for extra checks
   * @param location location for error message
   * @param context  psi processing context
   *
   * @return tag instance or null if not found
   */
  public static @Nullable <
      MP extends GenModelProjection<?, ?, ?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenEntityProjection<VP, TP, MP>
      >
  TagApi findTag(
      @NotNull DataTypeApi dataType,
      @Nullable String tagName,
      @Nullable VP op,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    final TagApi tag;
    final TypeApi type = dataType.type();

    if (tagName == null) {
      // get self tag
      TagApi retroTag = dataType.retroTag();
      if (retroTag == null) {
        if (type.kind() == TypeKind.ENTITY) return null;
        else tag = ((DatumTypeApi) type).self();
      } else {
        if (op == null || op.tagProjections().containsKey(retroTag.name())) {
          tag = retroTag;
        } else {
          tag = null;
        }
      }

    } else {
      tag = type.tagsMap().get(tagName);
      if (tag == null)
        throw new PsiProcessingException(
            String.format("Unknown tag '%s' in type '%s', known tags: (%s)", tagName, type.name(), listTags(type)),
            location, context
        );
    }

    if (tag != null)
      verifyTag(type, tag, op, location, context);

    return tag;
  }

  public static <
      MP extends GenModelProjection<?, ?, ?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenEntityProjection<VP, TP, MP>
      >
  void verifyTag(
      @NotNull TypeApi type,
      @NotNull TagApi tag,
      @Nullable VP op,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (!type.tags().contains(tag))
      throw new PsiProcessingException(
          String.format("Unknown tag '%s' in type '%s', known tags: (%s)", tag.name(), type.name(), listTags(type)),
          location,
          context
      );

    if (op != null)
      getTagProjection(tag.name(), op, location, context);
  }

  public static <VP extends GenEntityProjection<VP, ?, ?>> void verifyData(
      @NotNull DataTypeApi dataType,
      @NotNull VP varProjection,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (!varProjection.type().isAssignableFrom(dataType.type())) {
      final ProjectionReferenceName projectionName = varProjection.referenceName();
      final String message;

      if (projectionName == null)
        message = String.format(
            "Projection type '%s' is not compatible with type '%s'",
            varProjection.type().name(), dataType.type().name()
        );
      else
        message = String.format(
            "Projection '%s' type '%s' is not compatible with type '%s'",
            projectionName, varProjection.type().name(), dataType.type().name()
        );

      //context.addError(message, location);
      throw new PsiProcessingException(message, location, context);
    }
  }

  public static String listTags(@NotNull TypeApi type) {
    return type.tags().stream().map(TagApi::name).collect(Collectors.joining(","));
  }

  public static <VP extends GenEntityProjection<?, ?, ?>> String listTags(@NotNull VP op) {
    return String.join(", ", op.tagProjections().keySet());
  }

  public static TypeApi getType(
      @NotNull TypeRef typeRef,
      boolean allowNull,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    @Nullable TypeApi type = typeRef.resolve(resolver);
    if (type == null && !allowNull)
      throw new PsiProcessingException(String.format("Unknown type '%s'", typeRef.toString()), location, context);

    return type;
  }

  public static EntityTypeApi getEntityType(
      @NotNull TypeRef typeRef,
      boolean allowNull,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    TypeApi type = getType(typeRef, allowNull, resolver, location, context);
    if (type instanceof EntityTypeApi)
      return (EntityTypeApi) type;

    if (type == null && allowNull)
      return null;

    assert type != null; // otherwise `getType` would blow up

    throw new PsiProcessingException(
        String.format("Expected '%s' to be an entity type, but actual kind is '%s'",
            type.name().toString(), type.kind().toString()
        ), location, context);
  }

  public static DatumTypeApi getDatumType(
      @NotNull TypeRef typeRef,
      boolean allowNull,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    TypeApi type = getType(typeRef, allowNull, resolver, location, context);
    if (type instanceof DatumTypeApi)
      return (DatumTypeApi) type;

    if (type == null && allowNull)
      return null;

    assert type != null; // otherwise `getType` would blow up

    throw new PsiProcessingException(
        String.format("Expected '%s' to be a model type, but actual kind is '%s'",
            type.name().toString(), type.kind().toString()
        ), location, context);
  }

  /**
   * Finds tag projection by tag name
   */
  public static @NotNull <
      MP extends GenModelProjection<?, ?, ?, ?>,
      TP extends GenTagProjectionEntry<TP, MP>,
      VP extends GenEntityProjection<VP, TP, MP>
      >
  TP getTagProjection(
      @NotNull String tagName,
      @NotNull VP op,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {
    final TP tagProjection = op.tagProjections().get(tagName);
    if (tagProjection == null) {
      throw new PsiProcessingException(
          unsupportedTagMsg(tagName, op.tagProjections().keySet()),
          location,
          context
      );
    }
    return tagProjection;
  }

//  /**
//   * Finds default tags for a given {@code type}
//   * <p>
//   * If it's a {@code DatumType}, then default tag is {@code self}, provided that {@code op} contains it.
//   */
//  public static @Nullable <
//      MP extends GenModelProjection<?, ?, ?, ?>,
//      TP extends GenTagProjectionEntry<TP, MP>,
//      VP extends GenVarProjection<VP, TP, MP>
//      >
//  TagApi findSelfOrRetroTag(
//      @NotNull DataTypeApi dataType,
//      @Nullable VP op,
//      @NotNull TextLocation location,
//      @NotNull PsiProcessingContext context) throws PsiProcessingException {
//
//    TagApi retroTag = dataType.retroTag();
//    if (retroTag != null)
//      return retroTag;
//
//    TypeApi type = dataType.type();
//    if (type.kind() != TypeKind.ENTITY) {
//      DatumTypeApi datumType = (DatumTypeApi) type;
//      final @NotNull TagApi self = datumType.self();
//      if (op != null) getTagProjection(self.name(), op, location, context); // check that op contains it
//      return self;
//    }
//
//    return null;
//  }

  public static <VP extends GenEntityProjection<VP, ?, ?>> @NotNull VP getEntityTail(
      @NotNull VP vp,
      @NotNull TypeRef tailTypeRef,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext ctx) throws PsiProcessingException {

    @Nullable EntityTypeApi targetType = getEntityType(tailTypeRef, true, resolver, location, ctx);

    if (targetType == null) {
      throw new PsiProcessingException(
          unsupportedTagMsg(tailTypeRef.toString(), supportedEntityTailTypes(vp)), location, ctx
      );
    }

    if (targetType.equals(vp.type())) return vp;

    if (!hasTail(vp, targetType)) {
      throw new PsiProcessingException(
          unsupportedTagMsg(tailTypeRef.toString(), supportedEntityTailTypes(vp)), location, ctx
      );
    }

    return vp.normalizedForType(targetType);
  }

  @SuppressWarnings("unchecked")
  public static <VP extends GenEntityProjection<VP, ?, ?>> boolean hasTail(
      @NotNull VP vp,
      @NotNull EntityTypeApi tailType) {

    if (vp.tailByType(tailType) != null) return true;
    final List<?> tails = vp.polymorphicTails();
    return tails != null && tails.stream().anyMatch(t -> hasTail((VP) t, tailType));
  }

  public static @NotNull List<String> supportedEntityTailTypes(@NotNull GenEntityProjection<?, ?, ?> vp) {
    if (vp.polymorphicTails() == null) return Collections.emptyList();
    Set<String> acc = new HashSet<>();
    supportedEntityTailTypes(vp, acc);
    List<String> res = new ArrayList<>(acc);
    Collections.sort(res);
    return res;
  }

  @SuppressWarnings("unchecked")
  private static void supportedEntityTailTypes(@NotNull GenEntityProjection<?, ?, ?> vp, Set<String> acc) {
    final List<GenEntityProjection<?, ?, ?>> tails = (List<GenEntityProjection<?, ?, ?>>) vp.polymorphicTails();
    if (tails != null)
      tails.stream().map(t -> t.type().name().toString()).forEach(acc::add);
  }

  @SuppressWarnings("unchecked")
  public static <MP extends GenModelProjection<?, ?, ?, ?>> @NotNull MP getModelTail(
      @NotNull MP mp,
      @NotNull TypeRef tailTypeRef,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext ctx) throws PsiProcessingException {

    @Nullable DatumTypeApi targetType = getModelTailType(mp, tailTypeRef, resolver, location, ctx);

    if (targetType.equals(mp.type())) return mp;

    if (!hasModelTail(mp, targetType)) {
      throw new PsiProcessingException(
          String.format(
              "Polymorphic tail for type '%s' is not supported. Supported tail types: {%s}",
              targetType.name(),
              String.join(", ", supportedModelTailTypes(mp))
          ),
          location, ctx
      );
    }

    return (MP) mp.normalizedForType(targetType);
  }

  public static @NotNull <MP extends GenModelProjection<?, ?, ?, ?>> DatumTypeApi getModelTailType(
      final @NotNull MP mp,
      final @NotNull TypeRef tailTypeRef,
      final @NotNull TypesResolver resolver,
      final @NotNull TextLocation location,
      final @NotNull PsiProcessingContext ctx) throws PsiProcessingException {

    @Nullable DatumTypeApi targetType = getDatumType(tailTypeRef, true, resolver, location, ctx);

    if (targetType == null) {
      throw new PsiProcessingException(
          unsupportedTailMsg(tailTypeRef.toString(), supportedModelTailTypes(mp)), location, ctx
      );
    }
    return targetType;
  }

  @SuppressWarnings("unchecked")
  public static <MP extends GenModelProjection<?, ?, ?, ?>> boolean hasModelTail(
      @NotNull MP mp,
      @NotNull DatumTypeApi tailType) {

    if (mp.tailByType(tailType) != null) return true;
    final List<?> tails = mp.polymorphicTails();
    return tails != null && tails.stream().anyMatch(t -> hasModelTail((MP) t, tailType));
  }

  public static @NotNull List<String> supportedModelTailTypes(@NotNull GenModelProjection<?, ?, ?, ?> mp) {
    if (mp.polymorphicTails() == null) return Collections.emptyList();
    Set<String> acc = new HashSet<>();
    supportedModelTailTypes(mp, acc);
    List<String> res = new ArrayList<>(acc);
    Collections.sort(res);
    return res;
  }

  @SuppressWarnings("unchecked")
  private static void supportedModelTailTypes(@NotNull GenModelProjection<?, ?, ?, ?> vp, Set<String> acc) {
    final List<GenModelProjection<?, ?, ?, ?>> tails = (List<GenModelProjection<?, ?, ?, ?>>) vp.polymorphicTails();
    if (tails != null)
      tails.stream().map(t -> t.type().name().toString()).forEach(acc::add);
  }

  public static void checkEntityTailType(
      @NotNull EntityTypeApi tailType,
      @NotNull DataTypeApi dataType,
      @NotNull PsiElement tailTypeRefPsi,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (!dataType.type().isAssignableFrom(tailType))
      throw new PsiProcessingException(
          String.format(
              "Tail type '%s' is not compatible with type '%s'",
              tailType.name(), dataType.type().name()
          ),
          tailTypeRefPsi,
          context
      );
  }

  public static void checkEntityTailType(
      @NotNull TypeApi rootProjectionType,
      @NotNull EntityTypeApi tailType,
      @NotNull TypeApi tailProjectionType,
      @NotNull PsiElement location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (!rootProjectionType.isAssignableFrom(tailType))
      throw new PsiProcessingException(
          String.format(
              "Tail type '%s' is not a subtype of '%s'", tailType.name(), rootProjectionType.name()
          ),
          location,
          context
      );

    if (!tailType.isAssignableFrom(tailProjectionType))
      throw new PsiProcessingException(
          String.format(
              "Tail projection type '%s' is not a subtype of tail type '%s'", tailProjectionType.name(), tailType.name()
          ),
          location,
          context
      );
  }

  public static void checkModelTailType(
      @NotNull DatumTypeApi rootProjectionType,
      @NotNull DatumTypeApi tailType,
      @NotNull DatumTypeApi tailProjectionType,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (!rootProjectionType.isAssignableFrom(tailType))
      throw new PsiProcessingException(
          String.format(
              "Tail type '%s' is not a subtype of '%s'", tailType.name(), rootProjectionType.name()
          ),
          location,
          context
      );

    if (!tailType.isAssignableFrom(tailProjectionType))
      throw new PsiProcessingException(
          String.format(
              "Tail projection type '%s' is not a subtype of tail type '%s'", tailProjectionType.name(), tailType.name()
          ),
          location,
          context
      );
  }

  public static String unsupportedObjMsg(String objName, String objNamePl, String name, Collection<String> supported) {
    return String.format(
        "%s '%s' is not supported%supported %s: (%s)",
        objName,
        name,
        WordUtil.suggest(name, supported, ", did you mean '%s'? S", ", s"),
        objNamePl.toLowerCase(),
        ProjectionUtils.listStrings(supported)
    );
  }

  public static String unsupportedFieldMsg(String name, Collection<String> supported) {
    return unsupportedObjMsg("Field", "fields", name, supported);
  }

  public static String unsupportedTagMsg(String name, Collection<String> supported) {
    return unsupportedObjMsg("Tag", "tags", name, supported);
  }

  public static String unsupportedTailMsg(String name, Collection<String> supported) {
    return unsupportedObjMsg("Tail type", "tail types", name, supported);
  }
}
