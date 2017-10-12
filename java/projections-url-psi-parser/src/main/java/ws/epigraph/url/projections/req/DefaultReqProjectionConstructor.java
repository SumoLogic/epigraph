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

package ws.epigraph.url.projections.req;

import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Val;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.gdata.GDataToData;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.projections.ProjectionsParsingUtil;
import ws.epigraph.projections.abs.AbstractModelProjection;
import ws.epigraph.projections.abs.AbstractTagProjectionEntry;
import ws.epigraph.projections.gen.ProjectionReferenceName;
import ws.epigraph.projections.op.*;
import ws.epigraph.projections.req.*;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
@NotThreadSafe
public class DefaultReqProjectionConstructor {
  public enum Mode {
    INCLUDE_NONE,
    INCLUDE_FLAGGED_ONLY,
    INCLUDE_ALL
  }

  private final @NotNull Mode mode;
  private final boolean checkForRequiredMapKeys;
  private final boolean copyFlagsFromOp;

  private final WeakHashMap<ProjectionReferenceName, ReqEntityProjection> visitedEntityRefs = new WeakHashMap<>();

  public DefaultReqProjectionConstructor(
      final @NotNull Mode mode,
      final boolean checkForRequiredMapKeys,
      final boolean copyFlagsFromOp) {

    this.mode = mode;
    this.checkForRequiredMapKeys = checkForRequiredMapKeys;
    this.copyFlagsFromOp = copyFlagsFromOp;
  }

  public static DefaultReqProjectionConstructor outputProjectionDefaultConstructor() {
    return new DefaultReqProjectionConstructor(Mode.INCLUDE_FLAGGED_ONLY, true, false);
  }

  public static DefaultReqProjectionConstructor inputProjectionDefaultConstructor(boolean includeAll) {
    return new DefaultReqProjectionConstructor(includeAll ? Mode.INCLUDE_ALL : Mode.INCLUDE_NONE, false, true);
  }

  public static DefaultReqProjectionConstructor updateProjectionDefaultConstructor(boolean includeAll) {
    return new DefaultReqProjectionConstructor(includeAll ? Mode.INCLUDE_ALL : Mode.INCLUDE_NONE, false, false);
  }

//  public boolean checkForRequiredMapKeys() { return checkForRequiredMapKeys; }

  public @NotNull ReqEntityProjection createDefaultEntityProjection(
      @NotNull DataTypeApi dataType,
      @NotNull OpEntityProjection op,
      boolean flag,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    TypeApi type = dataType.type();
    ProjectionReferenceName referenceName = op.referenceName();

    ReqEntityProjection ref = null;

    if (referenceName != null) {
      ref = visitedEntityRefs.get(referenceName);
      if (ref == null) {
        ref = new ReqEntityProjection(type, location);
        visitedEntityRefs.put(referenceName, ref);
      } else
        return ref;
    }

    final Iterable<TagApi> tags;

    Collection<OpTagProjectionEntry> opTagEntries = op.tagProjections().values();

    switch (mode) {
      case INCLUDE_NONE:
        @Nullable TagApi defaultTag = ProjectionsParsingUtil.findTag(dataType, null, op, location, context);
        tags = defaultTag == null ?
               Collections.emptyList() :
               Collections.singletonList(defaultTag);
        break;

      case INCLUDE_FLAGGED_ONLY:
        if (type.kind() == TypeKind.ENTITY) {
          tags = opTagEntries
              .stream()
              .filter(tpe -> tpe.projection().flag())
              .map(AbstractTagProjectionEntry::tag)
              .collect(Collectors.toList());
        } else {
          OpTagProjectionEntry opSingleTag = op.singleTagProjection();
          assert opSingleTag != null;
          tags = Collections.singletonList(opSingleTag.tag());
        }
        break;

      case INCLUDE_ALL:
        tags = opTagEntries.stream().map(AbstractTagProjectionEntry::tag).collect(Collectors.toList());
        break;

      default:
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    ReqEntityProjection res = createDefaultEntityProjection(type, tags, op, flag, resolver, location, context);
    if (ref == null) {
      return res;
    } else {
      ref.resolve(referenceName, res);
      return ref;
    }

  }

  /**
   * Creates default entity projection with explicitly specified list of tags
   */
  private ReqEntityProjection createDefaultEntityProjection(
      @NotNull TypeApi type,
      @NotNull Iterable<TagApi> tags,
      @NotNull OpEntityProjection op,
      boolean flag,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    LinkedHashMap<String, ReqTagProjectionEntry> tagProjections = new LinkedHashMap<>();

    for (TagApi tag : tags) {
      final OpTagProjectionEntry opTagProjection = op.tagProjections().get(tag.name());
      if (opTagProjection != null) {
        tagProjections.put(
            tag.name(),
            new ReqTagProjectionEntry(
                tag,
                createDefaultModelProjection(
                    ReqRecordModelProjection.class,
                    tag.type(),
                    copyFlagsFromOp && opTagProjection.projection().flag(),
                    opTagProjection.projection(),
                    null,
                    Directives.EMPTY,
                    resolver,
                    location,
                    context
                ),
                location
            )
        );
      }
    }

    final List<ReqEntityProjection> tails;

    List<OpEntityProjection> opTails = op.polymorphicTails();
    if (mode == Mode.INCLUDE_NONE || opTails == null)
      tails = null;
    else {
      tails = new ArrayList<>(opTails.size());
      for (final OpEntityProjection opTail : opTails) {
        if (mode == Mode.INCLUDE_ALL || opTail.flag()) {
          tails.add(
              createDefaultEntityProjection(
                  opTail.type().dataType(),
                  opTail,
                  copyFlagsFromOp && opTail.flag(),
                  resolver,
                  location,
                  context
              )
          );
        }
      }
    }

    return new ReqEntityProjection(
        type,
        flag,
        tagProjections,
        op.parenthesized() || tagProjections.size() != 1,
        tails != null && tails.isEmpty() ? null : tails,
        location
    );
  }


  @SuppressWarnings("unchecked")
  public <MP extends ReqModelProjection<?, ?, ?>>
  @NotNull MP createDefaultModelProjection(
      @NotNull Class<MP> modelClass,
      @NotNull DatumTypeApi type,
      boolean flag,
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @Nullable ReqParams params,
      @NotNull Directives directives,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    if (params == null)
      params = getDefaultParams(op, resolver, location, context);

    switch (type.kind()) {
      case RECORD:
        OpRecordModelProjection opRecord = (OpRecordModelProjection) op;
        final Map<String, OpFieldProjectionEntry> opFields = opRecord.fieldProjections();

        final @NotNull Map<String, ReqFieldProjectionEntry> fields;

        if (opFields.isEmpty() || mode == Mode.INCLUDE_NONE) {
          fields = Collections.emptyMap();
        } else {
          fields = new LinkedHashMap<>();

          for (final Map.Entry<String, OpFieldProjectionEntry> entry : opFields.entrySet()) {
            OpFieldProjectionEntry fpe = entry.getValue();
            OpFieldProjection fieldProjection = fpe.fieldProjection();

            if (mode == Mode.INCLUDE_ALL || fieldProjection.flag()) {
              fields.put(
                  entry.getKey(),
                  new ReqFieldProjectionEntry(
                      fpe.field(),
                      new ReqFieldProjection(
                          createDefaultEntityProjection(
                              fpe.field().dataType(),
                              fieldProjection.entityProjection(),
                              copyFlagsFromOp && fieldProjection.flag(),
                              resolver,
                              location,
                              context
                          ),
                          location
                      ),
                      location
                  )
              );
            }
          }
        }


        return (MP) new ReqRecordModelProjection(
            (RecordTypeApi) type,
            flag,
            params,
            directives,
            createDefaultMetaProjection(ReqModelProjection.class, op, resolver, location, context),
            fields,
            createDefaultModelTails(ReqRecordModelProjection.class, op, resolver, location, context),
            location
        );

      case MAP:
        OpMapModelProjection opMap = (OpMapModelProjection) op;

        if (opMap.keyProjection().presence() == AbstractOpKeyPresence.REQUIRED && checkForRequiredMapKeys)
          throw new PsiProcessingException(
              String.format("Can't build default projection for '%s': keys are required", type.name()),
              location,
              context
          );

        MapTypeApi mapType = (MapTypeApi) type;
        final ReqEntityProjection valueEntityProjection = createDefaultEntityProjection(
            mapType.valueType(),
            opMap.itemsProjection(),
            flag,
            resolver,
            location,
            context
        );

        return (MP) new ReqMapModelProjection(
            mapType,
            flag,
            params,
            directives,
            createDefaultMetaProjection(ReqModelProjection.class, op, resolver, location, context),
            null,
            false,
            valueEntityProjection,
            createDefaultModelTails(ReqMapModelProjection.class, op, resolver, location, context),
            location
        );

      case LIST:
        OpListModelProjection opList = (OpListModelProjection) op;
        ListTypeApi listType = (ListTypeApi) type;

        final ReqEntityProjection itemEntityProjection = createDefaultEntityProjection(
            listType.elementType(),
            opList.itemsProjection(),
            flag,
            resolver,
            location,
            context
        );

        return (MP) new ReqListModelProjection(
            listType,
            flag,
            params,
            directives,
            createDefaultMetaProjection(ReqModelProjection.class, op, resolver, location, context),
            itemEntityProjection,
            createDefaultModelTails(ReqListModelProjection.class, op, resolver, location, context),
            location
        );

      case ENTITY:
        throw new PsiProcessingException(
            "Was expecting to get datum model kind, got: " + type.kind(),
            location,
            context
        );

      case ENUM:
        // todo
        throw new PsiProcessingException("Unsupported type kind: " + type.kind(), location, context);

      case PRIMITIVE:
        return (MP) new ReqPrimitiveModelProjection(
            (PrimitiveTypeApi) type,
            flag,
            params,
            directives,
            createDefaultMetaProjection(ReqModelProjection.class, op, resolver, location, context),
            createDefaultModelTails(ReqPrimitiveModelProjection.class, op, resolver, location, context),
            location
        );
      default:
        throw new PsiProcessingException("Unknown type kind: " + type.kind(), location, context);
    }
  }

  private <MP extends ReqModelProjection<?, ?, ?>> @Nullable MP createDefaultMetaProjection(
      @NotNull Class<MP> modelClass,
      final @NotNull OpModelProjection<?, ?, ?, ?> op,
      @NotNull TypesResolver resolver,
      final @NotNull TextLocation location,
      final @NotNull PsiProcessingContext context) {

    if (mode == Mode.INCLUDE_NONE)
      return null;

    return Optional.ofNullable(op.metaProjection()).map(mp -> {
      if (mode == Mode.INCLUDE_FLAGGED_ONLY && !mp.flag())
        return null;
      try {
        return createDefaultModelProjection(
            modelClass,
            mp.type(),
            copyFlagsFromOp && mp.flag(),
            mp,
            null,
            Directives.EMPTY,
            resolver,
            location,
            context
        );
      } catch (PsiProcessingException e) {
        context.addException(e);
        return null;
      }
    }).filter(Objects::nonNull).orElse(null);
  }

  @SuppressWarnings("unchecked")
  public <MP extends ReqModelProjection<?, ?, ?>>
  @Nullable List<MP> createDefaultModelTails(
      @NotNull Class<MP> modelClass,
      @NotNull OpModelProjection<?, ?, ?, ?> op,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context) throws PsiProcessingException {

    List<OpModelProjection<?, ?, ?, ?>> opTailsToInclude = (List<OpModelProjection<?, ?, ?, ?>>) op.polymorphicTails();
    if (opTailsToInclude != null) {
      switch (mode) {
        case INCLUDE_NONE:
          opTailsToInclude = null;
          break;
        case INCLUDE_FLAGGED_ONLY:
          opTailsToInclude =
              opTailsToInclude.stream().filter(AbstractModelProjection::flag).collect(Collectors.toList());
          break;
        case INCLUDE_ALL: // keep all
      }
    }

    return Optional.ofNullable(opTailsToInclude).map(otti -> otti.stream().map(ot -> {
      try {
        return createDefaultModelProjection(
            modelClass,
            ot.type(),
            copyFlagsFromOp && ot.flag(),
            ot,
            null,
            Directives.EMPTY,
            resolver,
            location,
            context
        );
      } catch (PsiProcessingException e) {
        context.addException(e);
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toList())).filter(t -> !t.isEmpty()).orElse(null);

  }

  private @NotNull ReqParams getDefaultParams(
      @NotNull OpModelProjection<?, ?, ?, ?> mp,
      @NotNull TypesResolver resolver,
      @NotNull TextLocation location,
      @NotNull PsiProcessingContext context
  ) {
    OpParams opParams = mp.params();
    if (opParams.isEmpty())
      return ReqParams.EMPTY;

    // check that there are no required parameters without defaults
    List<String> opRequiredParamsWithoutDefaults = opParams.asMap()
        .values()
        .stream()
        .filter(opParam -> opParam.projection().flag() && opParam.projection().defaultValue() == null)
        .map(OpParam::name)
        .collect(Collectors.toList());

    if (!opRequiredParamsWithoutDefaults.isEmpty()) {
      context.addError(String.format(
          "Can't build default projection for '%s': required parameter(s) have no default value: {%s}",
          mp.type().name(),
          String.join(", ", opRequiredParamsWithoutDefaults)
      ), location);
    }

    List<OpParam> opParamsWithDefaults = opParams.asMap()
        .values()
        .stream()
        .filter(opParam -> opParam.projection().defaultValue() != null)
        .collect(Collectors.toList());

    if (opParamsWithDefaults.isEmpty())
      return ReqParams.EMPTY;

    return new ReqParams(
        opParamsWithDefaults.stream().map(p -> {
          String name = p.name();

          GDatum d = p.projection().defaultValue();
          assert d != null;

          try {
            Val val = GDataToData.transform((DatumType) p.projection().type(), d, resolver);
            ErrorValue error = val.getError();

            if (error != null) {
              context.addError("Malformed default value in op projection for parameter '" + name + "'", p.location());
              return null;
            }

            return new ReqParam(name, val.getDatum(), TextLocation.UNKNOWN);
          } catch (GDataToData.ProcessingException e) {
            context.addError(
                "Malformed default value in op projection for parameter '" + name + "': " + e,
                p.location()
            );
            return null;
          }
        }).filter(Objects::nonNull).collect(Collectors.toList())
    );
  }
}
