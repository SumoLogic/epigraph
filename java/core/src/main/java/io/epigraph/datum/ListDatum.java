/* Created by yegor on 8/3/16. */

package io.epigraph.datum;

import io.epigraph.types.ListType;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;


public interface ListDatum extends Datum {

  @Override
  @NotNull ListType type();

  @Override
  @NotNull ListDatum.Raw _raw();

  @Override
  @NotNull ListDatum.Imm toImmutable();

  int size(); // TODO isEmpty()?


  abstract class Impl extends Datum.Impl<ListType> implements ListDatum {

    protected Impl(@NotNull ListType type) { super(type); }

  }


  interface Raw extends ListDatum, Datum.Raw {

    @Override
    @NotNull ListDatum.Imm.Raw toImmutable();

    @NotNull Collection<? extends Data> _elements(); // TODO or Iterable? or List? data()?

  }


  interface Static extends ListDatum, Datum.Static {

    @Override
    @NotNull ListDatum.Imm.Static toImmutable();

  }


  interface Imm extends ListDatum, Datum.Imm {

    @Override
    @NotNull ListDatum.Imm.Raw _raw();


    final class Raw extends ListDatum.Impl implements ListDatum.Imm, ListDatum.Raw, Datum.Imm.Raw {

      private final Collection<? extends Data.Imm> elements;

      public Raw(
          @NotNull ListType type,
          @NotNull ListDatum prototype
      ) { // TODO allow only ListDatum.Mut in constructor?
        super(type); // TODO derive type from prototype - or keep allowing sub-instances to be passed?
        // TODO check prototype is compatible?
        elements = Unmodifiable.list(
            prototype._raw()._elements(),
            Data::toImmutable
        ); // TODO filter out irrelevant (subtype-only) data (pass desired type then)?
      }

      @Override
      public int size() { return elements.size(); }

      @Override
      public @NotNull Collection<? extends Data.Imm> _elements() { return elements; }

      @Override
      public @NotNull ListDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull ListDatum.Imm.Raw _raw() { return this; }

    }


    interface Static extends ListDatum.Imm, ListDatum.Static, Datum.Imm.Static {

      @Override
      @NotNull ListDatum.Imm.Static toImmutable();

      @Override
      @NotNull ListDatum.Imm.Raw _raw();


      // TODO additional sub-classes for Union and Datum element type based lists?
      abstract class Impl<MyImmDatum extends ListDatum.Imm.Static> extends ListDatum.Impl
          implements ListDatum.Imm.Static {

        private final ListDatum.Imm.Raw raw;

        protected Impl(@NotNull ListType type, @NotNull ListDatum.Imm.Raw raw) {
          super(type); // TODO check types are compatible
          this.raw = raw; // TODO validate raw internals is kosher?
        }

        @Override
        public int size() { return raw.size(); }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }  // TODO or make abstract and implement in final static impl?

        @Override
        public @NotNull ListDatum.Imm.Raw _raw() { return raw; }

      }

    }


  }


  abstract class Mut extends ListDatum.Impl implements Datum.Mut { // TODO public?

    protected Mut(@NotNull ListType type) { super(type); }

    @Override
    public abstract @NotNull ListDatum.Mut.Raw _raw();


    public static final class Raw extends ListDatum.Mut implements ListDatum.Raw, Datum.Mut.Raw {

      private final @NotNull List<Data.@NotNull Mut> elements = new ArrayList<>();

      private @Nullable List<@NotNull ? extends Data.Mut> unmodifiableViewOfElements = null;

      public Raw(ListType type) { super(type); }

      @Override
      public @NotNull Collection<@NotNull ? extends Data.Mut> _elements() { // TODO implement modifiable view of elements?
        if (unmodifiableViewOfElements == null) unmodifiableViewOfElements = Unmodifiable.list(elements);
        return unmodifiableViewOfElements;
      }

      @Override
      public int size() { return elements.size(); }

      // TODO add mut methods here

      @Override
      public @NotNull ListDatum.Imm.Raw toImmutable() { return new ListDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull ListDatum.Mut.Raw _raw() { return this; }

    }


    public static abstract class Static< // TODO MyType extends Type.Static<MyType>?
        MyImm extends ListDatum.Imm.Static
        > extends ListDatum.Mut implements ListDatum.Static, Datum.Mut.Static {

      private final @NotNull ListDatum.Mut.Raw raw;

      private final @NotNull Function<ListDatum.Imm.Raw, MyImm> immutableConstructor;

      protected Static(
          @NotNull ListType type,
          @NotNull ListDatum.Mut.Raw raw,
          @NotNull Function<ListDatum.Imm.Raw, MyImm> immutableConstructor
      ) {
        super(type); // TODO take static type separately?
        if (raw.type() != type)
          throw new IllegalArgumentException( // TODO move mut and imm checks to shared static methods
              "Incompatible raw and static types (TODO details"
          );
        this.raw = raw; // TODO validate raw data is kosher?
        this.immutableConstructor = immutableConstructor;
      }

      @Override
      public int size() { return raw.size(); }

      @Override
      public @NotNull MyImm toImmutable() { return immutableConstructor.apply(_raw().toImmutable()); }

      @Override
      public @NotNull ListDatum.Mut.Raw _raw() { return raw; }

    }


  }


}
