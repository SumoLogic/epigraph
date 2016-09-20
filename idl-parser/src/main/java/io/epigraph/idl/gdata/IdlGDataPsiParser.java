package io.epigraph.idl.gdata;

import io.epigraph.gdata.*;
import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.Fqn;
import io.epigraph.psi.PsiProcessingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class IdlGDataPsiParser {
  // todo this class should belong to standalone version only

  @NotNull
  public static GDataValue parseValue(@NotNull IdlDataValue psi) throws PsiProcessingException {
    if (psi.getData() != null) return parseData(psi.getData());
    else if (psi.getDatum() != null) return parseDatum(psi.getDatum());
    else throw new PsiProcessingException("Neither data nor datum is set", psi);
  }

  @NotNull
  public static GData parseData(@NotNull IdlData psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    LinkedHashMap<String, GDatum> tags = new LinkedHashMap<>();
    for (IdlDataEntry entry : psi.getDataEntryList()) {
      @Nullable IdlDatum value = entry.getDatum();
      if (value != null)
        tags.put(entry.getQid().getCanonicalName(), parseDatum(value));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for tag '%s'", entry.getQid().getCanonicalName()), psi
        );
    }


    return new GData(getTypeRef(typeRef), tags);
  }

  @NotNull
  public static GDatum parseDatum(@NotNull IdlDatum psi) throws PsiProcessingException {
    if (psi instanceof IdlRecordDatum)
      return parseRecord((IdlRecordDatum) psi);
    else if (psi instanceof IdlMapDatum)
      return parseMap((IdlMapDatum) psi);
    else if (psi instanceof IdlListDatum)
      return parseList((IdlListDatum) psi);
    else if (psi instanceof IdlEnumDatum)
      return parseEnum((IdlEnumDatum) psi);
    else if (psi instanceof IdlPrimitiveDatum)
      return parsePrimitive((IdlPrimitiveDatum) psi);
    else if (psi instanceof IdlNullDatum)
      return parseNull((IdlNullDatum) psi);
    else throw new PsiProcessingException("Unknown value element", psi);
  }

  @NotNull
  public static GRecordDatum parseRecord(@NotNull IdlRecordDatum psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    LinkedHashMap<String, GDataValue> fields = new LinkedHashMap<>();
    for (IdlRecordDatumEntry entry : psi.getRecordDatumEntryList()) {
      @Nullable IdlDataValue value = entry.getDataValue();
      if (value != null)
        fields.put(entry.getQid().getCanonicalName(), parseValue(value));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for field '%s'", entry.getQid().getCanonicalName()), psi
        );
    }

    return new GRecordDatum(getTypeRef(typeRef), fields);
  }

  @NotNull
  public static GMapDatum parseMap(@NotNull IdlMapDatum psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    LinkedHashMap<GDatum, GDataValue> map = new LinkedHashMap<>();
    for (IdlMapDatumEntry entry : psi.getMapDatumEntryList()) {
      @Nullable IdlDataValue dataValue = entry.getDataValue();
      if (dataValue != null)
        map.put(parseDatum(entry.getDatum()), parseValue(dataValue));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for key '%s'", entry.getDataValue().getText()), psi
        );
    }

    return new GMapDatum(getTypeRef(typeRef), map);
  }

  @NotNull
  public static GListDatum parseList(@NotNull IdlListDatum psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    final List<GDataValue> items = new ArrayList<>();

    for (IdlDataValue value : psi.getDataValueList())
      items.add(parseValue(value));

    return new GListDatum(getTypeRef(typeRef), items);
  }

  @NotNull
  public static GDataEnum parseEnum(@NotNull IdlEnumDatum psi) {
    return new GDataEnum(psi.getQid().getCanonicalName());
  }

  @NotNull
  public static GPrimitiveDatum parsePrimitive(@NotNull IdlPrimitiveDatum psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    final Object value;
    if (psi.getString() != null) {
      String text = psi.getString().getText();
      value = text.substring(1, text.length() - 1);
    } else if (psi.getBoolean() != null) {
      value = Boolean.valueOf(psi.getBoolean().getText());
    } else if (psi.getNumber() != null) {
      // todo make it stable and always parse as Double?
      String text = psi.getNumber().getText();
      if (text.contains(".")) value = Double.valueOf(text);
      else value = Long.valueOf(text);
    } else
      throw new PsiProcessingException(String.format("Don't know how to handle primitive '%s'", psi.getText()), psi);

    return new GPrimitiveDatum(getTypeRef(typeRef), value);
  }

  @NotNull
  public static GNullDatum parseNull(@NotNull IdlNullDatum psi) {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();
    return new GNullDatum(getTypeRef(typeRef));
  }

  @Nullable
  private static Fqn getTypeRef(IdlFqnTypeRef typeRef) {return typeRef == null ? null : typeRef.getFqn().getFqn();}
}
