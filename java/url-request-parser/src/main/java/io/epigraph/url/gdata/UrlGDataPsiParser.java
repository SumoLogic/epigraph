package io.epigraph.url.gdata;

import io.epigraph.gdata.*;
import io.epigraph.url.TypeRefs;
import io.epigraph.url.parser.psi.*;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlGDataPsiParser {
  // todo this class should belong to standalone version only (standalone idl parser, once extracted)

  @NotNull
  public static GDataValue parseValue(@NotNull UrlDataValue psi) throws PsiProcessingException {
    if (psi.getData() != null) return parseData(psi.getData());
    else if (psi.getDatum() != null) return parseDatum(psi.getDatum());
    else throw new PsiProcessingException("Neither data nor datum is set", psi);
  }

  @NotNull
  public static GData parseData(@NotNull UrlData psi) throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDatum> tags = new LinkedHashMap<>();
    for (UrlDataEntry entry : psi.getDataEntryList()) {
      @Nullable UrlDatum value = entry.getDatum();
      if (value != null)
        tags.put(entry.getQid().getCanonicalName(), parseDatum(value));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for tag '%s'", entry.getQid().getCanonicalName()), psi
        );
    }


    return new GData(getTypeRef(typeRef), tags, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GDatum parseDatum(@NotNull UrlDatum psi) throws PsiProcessingException {
    if (psi instanceof UrlRecordDatum)
      return parseRecord((UrlRecordDatum) psi);
    else if (psi instanceof UrlMapDatum)
      return parseMap((UrlMapDatum) psi);
    else if (psi instanceof UrlListDatum)
      return parseList((UrlListDatum) psi);
    else if (psi instanceof UrlEnumDatum)
      return parseEnum((UrlEnumDatum) psi);
    else if (psi instanceof UrlPrimitiveDatum)
      return parsePrimitive((UrlPrimitiveDatum) psi);
    else if (psi instanceof UrlNullDatum)
      return parseNull((UrlNullDatum) psi);
    else throw new PsiProcessingException("Unknown value element", psi);
  }

  @NotNull
  public static GRecordDatum parseRecord(@NotNull UrlRecordDatum psi) throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDataValue> fields = new LinkedHashMap<>();
    for (UrlRecordDatumEntry entry : psi.getRecordDatumEntryList()) {
      @Nullable UrlDataValue value = entry.getDataValue();
      if (value != null)
        fields.put(entry.getQid().getCanonicalName(), parseValue(value));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for field '%s'", entry.getQid().getCanonicalName()), psi
        );
    }

    return new GRecordDatum(getTypeRef(typeRef), fields, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GMapDatum parseMap(@NotNull UrlMapDatum psi) throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<GDatum, GDataValue> map = new LinkedHashMap<>();
    for (UrlMapDatumEntry entry : psi.getMapDatumEntryList()) {
      @Nullable UrlDataValue dataValue = entry.getDataValue();
      if (dataValue != null)
        map.put(parseDatum(entry.getDatum()), parseValue(dataValue));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for key '%s'", entry.getDataValue().getText()), psi
        );
    }

    return new GMapDatum(getTypeRef(typeRef), map, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GListDatum parseList(@NotNull UrlListDatum psi) throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    final List<GDataValue> items = new ArrayList<>();

    for (UrlDataValue value : psi.getDataValueList())
      items.add(parseValue(value));

    return new GListDatum(getTypeRef(typeRef), items, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GDataEnum parseEnum(@NotNull UrlEnumDatum psi) {
    return new GDataEnum(psi.getQid().getCanonicalName(), EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GPrimitiveDatum parsePrimitive(@NotNull UrlPrimitiveDatum psi) throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

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

    return new GPrimitiveDatum(getTypeRef(typeRef), value, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GNullDatum parseNull(@NotNull UrlNullDatum psi) throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();
    return new GNullDatum(getTypeRef(typeRef), EpigraphPsiUtil.getLocation(psi));
  }

  @Nullable
  private static TypeRef getTypeRef(UrlTypeRef typeRef) throws PsiProcessingException {
    return typeRef == null ? null : TypeRefs.fromPsi(typeRef);
  }
}
