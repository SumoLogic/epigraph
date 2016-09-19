/* Created by yegor on 8/3/16. */

package io.epigraph.data;

import io.epigraph.types.ListType;
import io.epigraph.util.Unmodifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;
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

    @NotNull List<@NotNull ? extends Data> elements(); // TODO or Iterable? or Collection? rename to data()?

  }


  interface Static extends ListDatum, Datum.Static {

    @Override
    @NotNull ListDatum.Imm.Static toImmutable();


//    interface Tagged extends ListDatum.Static {
//
//      @Override
//      @NotNull ListDatum.Imm.Static.Tagged toImmutable();
//
//      @NotNull List<@Nullable ? extends Datum.Static> datums();
//
//    }


  }


  interface Imm extends ListDatum, Datum.Imm {

    @Override
    @NotNull ListDatum.Imm.Raw _raw();


    final class Raw extends ListDatum.Impl implements ListDatum.Imm, ListDatum.Raw, Datum.Imm.Raw {

      private final List<? extends Data.Imm> elements;

      private @NotNull Val.Imm.Raw value = new Val.Imm.Raw.DatumVal(this);

      public Raw(
          @NotNull ListType type,
          @NotNull ListDatum prototype
      ) { // TODO allow only ListDatum.Mut in constructor?
        super(type); // TODO derive type from prototype - or keep allowing sub-instances to be passed?
        // TODO check prototype is compatible?
        elements = Unmodifiable.list(
            prototype._raw().elements(),
            Data::toImmutable
        ); // TODO filter out irrelevant (subtype-only) data (pass desired type then)?
      }

      @Override
      public int size() { return elements.size(); }

      @Override
      public @NotNull List<@NotNull ? extends Data.Imm> elements() { return elements; }

      @Override
      public @NotNull ListDatum.Imm.Raw toImmutable() { return this; }

      @Override
      public @NotNull ListDatum.Imm.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Imm.Raw asValue() { return value; }

    }


    interface Static extends ListDatum.Imm, ListDatum.Static, Datum.Imm.Static {

      @Override
      @NotNull ListDatum.Imm.Static toImmutable();

      @Override
      @NotNull ListDatum.Imm.Raw _raw();


      // TODO additional sub-classes for Union and Datum element type based lists?
      abstract class Impl<MyImmDatum extends ListDatum.Imm.Static, MyImmVal extends Val.Imm.Static> extends ListDatum.Impl
          implements ListDatum.Imm.Static {

        private final @NotNull ListDatum.Imm.Raw raw;

        private final @NotNull MyImmVal value;

        protected Impl(
            @NotNull ListType type,
            @NotNull ListDatum.Imm.Raw raw,
            @NotNull Function<Val.Imm.@NotNull Raw, @NotNull MyImmVal> immValConstructor
        ) {
          super(type);
          // TODO check types are compatible
          this.raw = raw; // TODO validate raw internals is kosher?
          this.value = immValConstructor.apply(raw.asValue());
        }

        @Override
        public int size() { return raw.size(); }

        @Override
        public @NotNull MyImmDatum toImmutable() { return (MyImmDatum) this; }  // TODO or make abstract and implement in final static impl?

        @Override
        public @NotNull ListDatum.Imm.Raw _raw() { return raw; }

        @Override
        public @NotNull MyImmVal asValue() { return value; }

      }


//      interface Tagged extends ListDatum.Imm.Static {
//
//        @Override
//        @NotNull ListDatum.Imm.Static.Tagged toImmutable();
//
//        @NotNull List<@Nullable ? extends Datum.Imm.Static> datums();
//
//      }


    }


  }


  abstract class Builder extends ListDatum.Impl implements Datum.Builder {

    protected Builder(@NotNull ListType type) { super(type); }

    @Override
    public abstract @NotNull ListDatum.Builder.Raw _raw();


    public static final class Raw extends ListDatum.Builder implements ListDatum.Raw, Datum.Builder.Raw {

      private final @NotNull List<@NotNull Data> elements = new DataList<>(type());

      private final @NotNull Val.Builder.Raw value = new Val.Builder.Raw.DatumVal(this);

      public Raw(ListType type) { super(type); }

      @Override
      public @NotNull List<@NotNull Data> elements() { return elements; }

      @Override
      public int size() { return elements.size(); }

      // TODO add mut methods here

      @Override
      public @NotNull ListDatum.Imm.Raw toImmutable() { return new ListDatum.Imm.Raw(type(), this); }

      @Override
      public @NotNull ListDatum.Builder.Raw _raw() { return this; }

      @Override
      public @NotNull Val.Builder.Raw asValue() { return value; }


      private static class DataList<E extends Data> extends AbstractList<E> implements RandomAccess {

        private final @NotNull List<@NotNull E> list = new ArrayList<>();

        private final ListType listType;

        public DataList(@NotNull ListType listType) { this.listType = listType; }

        @Override
        public E get(int index) { return list.get(index); }

        @Override
        public int size() { return list.size(); }

        @Override
        public E set(int index, E element) { return list.set(index, validate(element)); }

        @Override
        public void add(int index, E element) { list.add(index, validate(element)); }

        @Override
        public E remove(int index) { return list.remove(index); }

        private E validate(E element) throws IllegalArgumentException {
          return listType.elementType.checkWrite(element);
        }

      }


    }


    public static abstract class Static<
        MyImmDatum extends ListDatum.Imm.Static,
        MyBuilderVal extends Val.Builder.Static
        > extends ListDatum.Builder implements ListDatum.Static, Datum.Builder.Static<MyImmDatum> {

      private final @NotNull ListDatum.Builder.Raw raw;

      private final @NotNull MyBuilderVal value;

      private final @NotNull Function<ListDatum.Imm.Raw, MyImmDatum> immutableConstructor;

      protected Static(
          @NotNull ListType type,
          @NotNull ListDatum.Builder.Raw raw,
          @NotNull Function<ListDatum.Imm.Raw, MyImmDatum> immutableConstructor,
          @NotNull Function<Val.Builder.@NotNull Raw, @NotNull MyBuilderVal> builderValConstructor
      ) {
        super(type); // TODO take static type separately?
        if (raw.type() != type) // TODO shared assertEqual(Type, Type): Type method
          throw new IllegalArgumentException( // TODO move mut and imm checks to shared static methods
              "Incompatible raw and static types (TODO details)"
          );
        this.raw = raw; // TODO validate raw data is kosher?
        this.value = builderValConstructor.apply(raw.asValue());
        this.immutableConstructor = immutableConstructor;
      }

      @Override
      public int size() { return raw.size(); }

      @Override
      public @NotNull MyImmDatum toImmutable() { return immutableConstructor.apply(_raw().toImmutable()); }

      @Override
      public @NotNull ListDatum.Builder.Raw _raw() { return raw; }

      @Override
      public @NotNull MyBuilderVal asValue() { return value; }

    }


  }


//  abstract class Mut extends ListDatum.Impl implements Datum.Mut { // TODO public?
//
//    protected Mut(@NotNull ListType type) { super(type); }
//
//    @Override
//    public abstract @NotNull ListDatum.Mut.Raw _raw();
//
//
//    public static final class Raw extends ListDatum.Mut implements ListDatum.Raw, Datum.Mut.Raw {
//
//      private final @NotNull List<Data.@NotNull Mut> elements = new DataList<>(type());
//
////      private @Nullable List<@NotNull ? extends Data.Mut> unmodifiableViewOfElements = null;
//
//      public Raw(ListType type) { super(type); }
//
//      @Override
//      public @NotNull List<Data.@NotNull Mut> elements() { return elements; }
//
////      @Override
////      public @NotNull List<@NotNull ? extends Data.Mut> elements() { // FIXME implement modifiable view of elements (YES!)?
////        if (unmodifiableViewOfElements == null) unmodifiableViewOfElements = /*Unmodifiable.list(*/elements/*)*/;
////        return unmodifiableViewOfElements;
////      }
//
//      @Override
//      public int size() { return elements.size(); }
//
//      // TODO add mut methods here
//
//      @Override
//      public @NotNull ListDatum.Imm.Raw toImmutable() { return new ListDatum.Imm.Raw(type(), this); }
//
//      @Override
//      public @NotNull ListDatum.Mut.Raw _raw() { return this; }
//
//
//      private static class DataList<E extends Data.@NotNull Mut> extends AbstractList<E> implements RandomAccess {
//
//        private final @NotNull List<@NotNull E> list = new ArrayList<>();
//
//        private final ListType listType;
//
//        public DataList(@NotNull ListType listType) { this.listType = listType; }
//
//        @Override
//        public E get(int index) { return list.get(index); }
//
//        @Override
//        public int size() { return list.size(); }
//
//        @Override
//        public E set(int index, E element) { return list.set(index, validate(element)); }
//
//        @Override
//        public void add(int index, E element) { list.add(index, validate(element)); }
//
//        @Override
//        public E remove(int index) { return list.remove(index); }
//
//        private E validate(E element) throws IllegalArgumentException {
//          return listType.elementType.checkWrite(element);
//        }
//
//      }
//
//
//    }
//
//
//    public static abstract class Static<MyImmDatum extends ListDatum.Imm.Static> extends ListDatum.Mut
//        implements ListDatum.Static, Datum.Mut.Static<MyImmDatum> {
//
//      private final @NotNull ListDatum.Mut.Raw raw;
//
//      private final @NotNull Function<ListDatum.Imm.Raw, MyImmDatum> immutableConstructor;
//
//      protected Static(
//          @NotNull ListType type,
//          @NotNull ListDatum.Mut.Raw raw,
//          @NotNull Function<ListDatum.Imm.Raw, MyImmDatum> immutableConstructor
//      ) {
//        super(type); // TODO take static type separately?
//        if (raw.type() != type) // TODO shared assertEqual(Type, Type): Type method
//          throw new IllegalArgumentException( // TODO move mut and imm checks to shared static methods
//              "Incompatible raw and static types (TODO details)"
//          );
//        this.raw = raw; // TODO validate raw data is kosher?
//        this.immutableConstructor = immutableConstructor;
//      }
//
//      @Override
//      public int size() { return raw.size(); }
//
//      @Override
//      public @NotNull MyImmDatum toImmutable() { return immutableConstructor.apply(_raw().toImmutable()); }
//
//      @Override
//      public @NotNull ListDatum.Mut.Raw _raw() { return raw; }
//
//
//      public static abstract class Tagged<MyImm extends ListDatum.Imm.Static.Tagged, MyDefault extends Datum.Mut.Static>
//          extends ListDatum.Mut.Static<MyImm> implements ListDatum.Static.Tagged {
//
//        protected Tagged(
//            @NotNull ListType type,
//            @NotNull ListDatum.Mut.Raw raw,
//            @NotNull Function<ListDatum.Imm.Raw, MyImm> immutableConstructor
//        ) { super(type, raw, immutableConstructor); }
//
//      }
//
//
//    }
//
//
//  }


}
