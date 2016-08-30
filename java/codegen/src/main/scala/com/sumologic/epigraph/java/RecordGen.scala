/* Created by yegor on 7/11/16. */

package com.sumologic.epigraph.java

import com.sumologic.epigraph.java.NewlineStringInterpolator.NewlineHelper
import com.sumologic.epigraph.schema.compiler._

class RecordGen(from: CRecordTypeDef, ctx: CContext) extends JavaTypeDefGen[CRecordTypeDef](from, ctx) {

  protected def generate: String = sn"""\
/*
 * Standard header
 */

package ${pn(t)};

//import io.epigraph.data.ListDatum;
//import io.epigraph.data.RecordDatum;
//import io.epigraph.data.Val;
//import io.epigraph.errors.ErrorValue;
//import io.epigraph.names.AnonListTypeName;
//import io.epigraph.names.NamespaceName;
//import io.epigraph.names.QualifiedTypeName;
//import io.epigraph.types.AnonListType;
//import io.epigraph.types.ListType;
//import io.epigraph.types.RecordType;
import io.epigraph.types.RecordType.Field;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//import java.util.Arrays;
//import java.util.Collections;
//import java.util.stream.Collectors;

public interface $ln extends${withParents(t)} io.epigraph.data.RecordDatum.Static {

  @NotNull $ln.Type type = new $ln.Type();
${ // for each effective field
  t.effectiveFields.map { f => sn"""\

  // Field `${f.name}`
  @NotNull Field ${jn(f.name)} = new Field("${f.name}", ${lqrn(f.typeRef, t)}.type, ${f.isAbstract});
${ // default datum and value getters
    if (f.effectiveDefaultTagName.isDefined) sn"""\

  /**
   * Returns default tag datum for `${f.name}` field.
   */
  @Nullable ${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)} get${up(f.name)}();

  /**
   * Returns default tag value for `${f.name}` field.
   */
  @Nullable ${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)}.Value get_${up(f.name)}();
"""
}${ // tags datum and value getters
    f.valueType.typeRef.resolved match {
      case vartype: CVarTypeDef =>
        vartype.effectiveTagsMap.values.map { tag => sn"""\

  /**
   * Returns `${tag.name}` tag datum for `${f.name}` field.
   */
  @Nullable ${lqrn(tag.typeRef, t)} get${up(f.name)}${up(tag.name)}();

  /**
   * Returns `${tag.name}` tag value for `${f.name}` field.
   */
  @Nullable ${lqrn(tag.typeRef, t)}.Value get_${up(f.name)}${up(tag.name)}();
"""
        }.mkString
      case _: CDatumType => ""
      case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
    }
  }
"""
  }.mkString
}\

  class Type extends io.epigraph.types.RecordType.Static<
      $ln.Imm,
      $ln.Builder,
      $ln.Imm.Value,
      $ln.Builder.Value,
      $ln.Imm.Data,
      $ln.Builder.Data
      > {

    private Type() {
      super(
          new io.epigraph.names.QualifiedTypeName(${qnameArgs(t.name.fqn).mkString("\"", "\", \"", "\"")}),
          java.util.Arrays.asList(${t.linearizedParents.map(lqn(_, t, _ + ".type")).mkString(", ")}),
          ${t.isPolymorphic},
          $ln.Builder::new,
          $ln.Builder.Value::new,
          $ln.Builder.Data::new
      );
    }

    @Override
    public @NotNull java.util.List<@NotNull Field> immediateFields() {
      return java.util.Arrays.asList(\
${
    t.declaredFields.map { f => sn"""
        ${ln + '.' + jn(f.name)}"""
    }.mkString(",")
}
      );
    }
$listSupplier\

  }

  interface Value extends${withParents(".Value")} io.epigraph.data.Val.Static {

    @Override
    @Nullable $ln getDatum();

    @Override
    @NotNull $ln.Imm.Value toImmutable();

  }

  interface Data extends${withParents(".Data")} io.epigraph.data.Data.Static {

    @Override
    @NotNull $ln.Imm.Data toImmutable();

    @Nullable $ln.Value get();

  }

  interface Imm extends $ln,${withParents(".Imm")} io.epigraph.data.RecordDatum.Imm.Static {

${ // for each effective field
    t.effectiveFields.map { f => sn"""\
${ // default getter
        if (f.effectiveDefaultTagName.isDefined) sn"""\

  /**
   * Returns immutable default tag datum for `${f.name}` field.
   */
  @Override
  @Nullable ${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)}.Imm get${up(f.name)}();

  /**
   * Returns immutable default tag value for `${f.name}` field.
   */
  @Override
  @Nullable ${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)}.Imm.Value get_${up(f.name)}();
"""
      }${ // tag getters
        f.valueType.typeRef.resolved match {
          case vartype: CVarTypeDef =>
            vartype.effectiveTagsMap.values.map { tag => sn"""\

  /**
   * Returns immutable `${tag.name}` tag datum for `${f.name}` field.
   */
  @Override
  @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(f.name)}${up(tag.name)}();

  /**
   * Returns `${tag.name}` tag value for `${f.name}` field.
   */
  @Override
  @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get_${up(f.name)}${up(tag.name)}();
"""
            }.mkString
          case _: CDatumType => ""
          case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
        }
      }
"""
    }.mkString
  }\

    interface Value extends $ln.Value,${withParents(".Imm.Value")} io.epigraph.data.Val.Imm.Static {

      @Override
      @Nullable $ln.Imm getDatum();


      final class Impl extends io.epigraph.data.Val.Imm.Static.Impl<$ln.Imm.Value, $ln.Imm>
          implements $ln.Imm.Value {

        public Impl(@NotNull io.epigraph.data.Val.Imm.Raw raw) { super($ln.type, raw); }

      }

    }

    interface Data extends $ln.Data,${withParents(".Imm.Data")} io.epigraph.data.Data.Imm.Static {

      @Override
      @Nullable $ln.Imm.Value get();

      final class Impl extends io.epigraph.data.Data.Imm.Static.Impl<$ln.Imm.Data>
          implements $ln.Imm.Data {

        protected Impl(@NotNull io.epigraph.data.Data.Imm.Raw raw) { super($ln.type, raw); }

        @Override
        public @Nullable $ln.Imm.Value get() {
          return ($ln.Imm.Value) _raw()._getValue($ln.type.self);
        }

      }

    }

    final class Impl extends io.epigraph.data.RecordDatum.Imm.Static.Impl<$ln.Imm> implements $ln.Imm {

      private Impl(@NotNull io.epigraph.data.RecordDatum.Imm.Raw raw) { super($ln.type, raw); }

${ // for each effective field
    t.effectiveFields.map { f => sn"""\
${ // default getter
      if (f.effectiveDefaultTagName.isDefined) sn"""\

        /**
         * Returns immutable default tag datum for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)}.Imm get${up(f.name)}() {
          return io.epigraph.util.Util.apply(get_${up(f.name)}(), ${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)}.Imm.Value::getDatum);
        }

        /**
         * Returns immutable default tag value for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)}.Imm.Value get_${up(f.name)}() {
          return (${lqn(dtt(f.typeRef, f.effectiveDefaultTagName.get), t)}.Imm.Value) _raw()._getValue($ln.${jn(f.name)}, FixMe.type.self);
        }
"""
}${ // tag getters
      f.valueType.typeRef.resolved match {
        case vartype: CVarTypeDef =>
          vartype.effectiveTagsMap.values.map { tag => sn"""\

        /**
         * Returns immutable `${tag.name}` tag datum for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqrn(tag.typeRef, t)}.Imm get${up(f.name)}${up(tag.name)}();

        /**
         * Returns `${tag.name}` tag value for `${f.name}` field.
         */
        @Override
        public @Nullable ${lqrn(tag.typeRef, t)}.Imm.Value get_${up(f.name)}${up(tag.name)}();
"""
          }.mkString
        case _: CDatumType => ""
        case unexpected => throw new UnsupportedOperationException(unexpected.name.name)
      }
}
"""
    }.mkString
}\

    }


  }


  final class Builder extends io.epigraph.data.RecordDatum.Mut.Static<$ln.Imm> implements $ln {

    private Builder(@NotNull io.epigraph.data.RecordDatum.Mut.Raw raw) { super($ln.type, raw, $ln.Imm.Impl::new); }

    // FIXME iterate over immediate fields
    @Override
    public @Nullable PersonId.Builder getId() {
      PersonId.Builder.Value value = (PersonId.Builder.Value) _raw()._getValue(PersonRecord.id, PersonId.type.self);
      return value == null ? null : value.getDatum();
    }

    @Override
    public @Nullable $ln.Builder.Value getBestFriend_value() {
      return ($ln.Builder.Value) _raw()._getValue(bestFriend, $ln.type.self);
    }

    @Override
    public @Nullable $ln.Builder getBestFriend() {
      $ln.Builder.Value value = getBestFriend_value();
      return value == null ? null : value.getDatum();
    }

    @Override
    public @Nullable $ln.List.Builder.Value getFriends_value() {
      return ($ln.List.Builder.Value) _raw()._getValue($ln.friends, $ln.List.type.self);
    }

    @Override
    public @Nullable $ln.List.Builder getFriends() {
      $ln.List.Builder.Value value = getFriends_value();
      return value == null ? null : value.getDatum();
    }

    public void setBestFriend(@Nullable $ln.Builder bestFriend) {
      _raw().getOrCreateFieldData($ln.bestFriend)._raw()._setDatum($ln.type.self, bestFriend);
    }

    // TODO full set of field setters


    final static class Value extends io.epigraph.data.Val.Mut.Static<$ln.Imm.Value, $ln.Builder>
        implements $ln.Value {

      public Value(@NotNull io.epigraph.data.Val.Mut.Raw raw) { super(raw, $ln.Imm.Value.Impl::new); }

    }

    final static class Data extends io.epigraph.data.Data.Mut.Static<$ln.Imm.Data>
        implements $ln.Data {

      protected Data(@NotNull io.epigraph.data.Data.Mut.Raw raw) {
        super($ln.type, raw, $ln.Imm.Data.Impl::new);
      }

      @Override
      public @Nullable $ln.Builder.Value get() {
        return ($ln.Builder.Value) _raw()._getValue($ln.type.self);
      }

      public void set(@Nullable $ln.Builder.Value value) {
        _raw()._setValue($ln.type.self, value);
      } //default tag

    }

  }

}
"""

  private def fieldName(f: CField): String = jn(f.name)

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def fieldType(f: CField, ht: CTypeDef): String = s"DatumField[${ft(f, ht)}]"

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def fieldDef(f: CField, ht: CTypeDef): String = s"""field("${f.name}", ${ft(f, ht)})"""

  // TODO val _id: DatumField[FooId] = field("id", FooId)
  private def ft(f: CField, ht: CTypeDef): String = s"${f.typeRef.resolved.name.name}"

}
