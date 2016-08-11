/* Created by yegor on 8/3/16. */

package io.epigraph.data;

import io.epigraph.errors.ErrorValue;
import io.epigraph.types.Type;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public interface Data { // TODO Var? Union? Values?

  @NotNull Type type();

  @NotNull Data.Imm toImmutable();

  @NotNull Data.Raw _raw();


  abstract class Impl implements Data {

    private final @NotNull Type type;

    protected Impl(@NotNull Type type) { this.type = type; }

    @Override
    public @NotNull Type type() { return type; }

  }


  interface Raw extends Data {

    @Override
    @NotNull Data.Imm.Raw toImmutable();

    @NotNull Map<@NotNull String, @NotNull ? extends Val> _tagValues();

    @Nullable Val _getValue(@NotNull Type.Tag tag);

    @Nullable Datum _getDatum(@NotNull Type.Tag tag);

    @Nullable ErrorValue _getError(@NotNull Type.Tag tag);

  }


  interface Static extends Data {

    @Override
    @NotNull Data.Imm.Static toImmutable();

  }


  interface Imm extends Data, Immutable {

    @Override
    @NotNull Data.Imm.Raw _raw();


    final class Raw extends Data.Impl implements Data.Imm, Data.Raw {

      private final Map<@NotNull String, @NotNull ? extends Val.Imm> tagValues;

      protected Raw(@NotNull Type type, Data prototype) {
        super(type);
        // FIXME unmodifiable copy of immutable prototype data
        tagValues = new HashMap<>(); // TODO optimize for single-tag types
      }

      @Override
      public @NotNull Data.Imm.Raw toImmutable() { return this; }

      @NotNull
      @Override
      public Data.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Map<@NotNull String, @NotNull ? extends Val.Imm> _tagValues() { return tagValues; }

      @Override
      public @Nullable Val.Imm _getValue(@NotNull Type.Tag tag) {
        // TODO check tag compatibility with this.type
        return tagValues.get(tag.name);
      }

      @Override
      public @Nullable Datum.Imm _getDatum(@NotNull Type.Tag tag) {
        Val.Imm value = _getValue(tag);
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable ErrorValue _getError(@NotNull Type.Tag tag) {
        Val.Imm value = _getValue(tag);
        return value == null ? null : value.getError();
      }

    }


    interface Static extends Data.Imm, Data.Static {

      @Override
      @NotNull Data.Imm.Static toImmutable();


      abstract class Impl<MyImmData extends Data.Imm.Static> extends Data.Impl implements Data.Imm.Static {

        private final @NotNull Data.Imm.Raw raw;

        protected Impl(@NotNull Type type, @NotNull Data.Imm.Raw raw) {
          super(type);
          // TODO check raw type compatibility with static type
          this.raw = raw;
        }

        @Override
        public @NotNull MyImmData toImmutable() { return (MyImmData) this; } // TODO make abstract, override in subclasses?

        @Override
        public @NotNull Data.Imm.Raw _raw() { return raw; }

      }

    }


  }


  abstract class Mut extends Data.Impl implements Mutable {

    @Override
    public abstract @NotNull Data.Mut.Raw _raw();

    protected Mut(@NotNull Type type) { super(type); }


    public static final class Raw extends Data.Mut implements Data.Raw {

      private final @NotNull Map<String, Val.@NotNull Mut> tagValues = new HashMap<>(); // TODO optimize for single-tag types

      private @Nullable Map<String, @NotNull ? extends Val.Mut> unmodifiableViewOfTagValues = null;

      public Raw(@NotNull Type type) { super(type); }

      @Override
      public @NotNull Data.Imm.Raw toImmutable() { return new Data.Imm.Raw(type(), this); }

      @Override
      public @NotNull Data.Mut.Raw _raw() { return this; }

      @Override
      public @NotNull Map<@NotNull String, @NotNull ? extends Val.Mut> _tagValues() {
        if (unmodifiableViewOfTagValues == null) unmodifiableViewOfTagValues = Unmodifiable.map(tagValues);
        return unmodifiableViewOfTagValues;
      }

      @Override
      public @Nullable Val.Mut _getValue(@NotNull Type.Tag tag) {
        // TODO check tag compatibility with this.type
        return tagValues.get(tag.name);
      }

      @Override
      public @Nullable Datum _getDatum(@NotNull Type.Tag tag) {
        Val.Mut value = _getValue(tag);
        return value == null ? null : value.getDatum();
      }

      @Override
      public @Nullable ErrorValue _getError(@NotNull Type.Tag tag) {
        Val.Mut value = _getValue(tag);
        return value == null ? null : value.getError();
      }

      // TODO accept Value and auto-convert? convert on write (NO - someone might hold the reference already)?
      public void _setValue(@NotNull Type.Tag tag, @Nullable Val.Mut value) {
        // TODO check tag compatibility with this.type
        if (value == null) {
          tagValues.remove(tag.name);
        } else {
          // TODO check value compatibility with the tag
          tagValues.put(tag.name, value);
        }
      }

      // TODO take Datum and auto-convert?
      public void _setDatum(@NotNull Type.Tag tag, @Nullable Datum.Mut datum) {
        _getOrCreateTagValue(tag)._raw().setDatum(datum);
      }

      public void _setError(@NotNull Type.Tag tag, @NotNull ErrorValue error) {
        _getOrCreateTagValue(tag).setError(error);
      }

      public @NotNull Val.Mut _getOrCreateTagValue(@NotNull Type.Tag tag) {
        // TODO check tag compatibility with this.type
        Val.Mut value = _getValue(tag);
        // TODO this (as many other places) is not thread-safe - use ConcurrentHashMap?
        if (value == null) tagValues.put(tag.name, value = tag.createMutableValue());
        return value;
      }

    }


    public static abstract class Static<MyImmData extends Data.Imm.Static> extends Data.Mut implements Data.Static {

      private final @NotNull Data.Mut.Raw raw;

      private final @NotNull Function<Data.Imm.Raw, MyImmData> immDataConstructor;

      protected Static(
          @NotNull Type type,
          @NotNull Data.Mut.Raw raw,
          @NotNull Function<Data.Imm.Raw, MyImmData> immDataConstructor
      ) {
        super(type);
        // TODO check raw type equality to static type
        this.raw = raw;
        this.immDataConstructor = immDataConstructor;
      }

      @Override
      public final @NotNull MyImmData toImmutable() { return immDataConstructor.apply(_raw().toImmutable()); }

      @Override
      public final @NotNull Data.Mut.Raw _raw() { return raw; }

    }


  }


}
