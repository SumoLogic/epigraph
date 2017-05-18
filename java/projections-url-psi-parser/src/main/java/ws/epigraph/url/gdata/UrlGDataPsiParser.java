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

package ws.epigraph.url.gdata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.gdata.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingContext;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import ws.epigraph.url.TypeRefs;
import ws.epigraph.url.parser.psi.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class UrlGDataPsiParser {

  private UrlGDataPsiParser() {}

  public static @NotNull GDataValue parseValue(@NotNull UrlDataValue psi, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    if (psi.getData() != null) return parseData(psi.getData(), context);
    else if (psi.getDatum() != null) return parseDatum(psi.getDatum(), context);
    else throw new PsiProcessingException("Neither data nor datum is set", psi, context);
  }

  public static @NotNull GData parseData(@NotNull UrlData psi, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDatum> tags = new LinkedHashMap<>();
    for (UrlDataEntry entry : psi.getDataEntryList()) {
      @Nullable UrlDatum value = entry.getDatum();
      if (value == null) throw new PsiProcessingException(
          String.format("Got 'null' value for tag '%s'", entry.getQid().getCanonicalName()), psi, context
      );
      else tags.put(entry.getQid().getCanonicalName(), parseDatum(value, context));
    }


    return new GData(getTypeRef(typeRef, context), tags, EpigraphPsiUtil.getLocation(psi));
  }

  public static @NotNull GDatum parseDatum(@NotNull UrlDatum psi, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    if (psi instanceof UrlRecordDatum)
      return parseRecord((UrlRecordDatum) psi, context);
    else if (psi instanceof UrlMapDatum)
      return parseMap((UrlMapDatum) psi, context);
    else if (psi instanceof UrlListDatum)
      return parseList((UrlListDatum) psi, context);
    else if (psi instanceof UrlEnumDatum)
      return parseEnum((UrlEnumDatum) psi, context);
    else if (psi instanceof UrlPrimitiveDatum)
      return parsePrimitive((UrlPrimitiveDatum) psi, context);
    else if (psi instanceof UrlNullDatum)
      return parseNull((UrlNullDatum) psi, context);
    else throw new PsiProcessingException("Unknown value element", psi, context);
  }

  public static @NotNull GRecordDatum parseRecord(@NotNull UrlRecordDatum psi, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDataValue> fields = new LinkedHashMap<>();
    for (UrlRecordDatumEntry entry : psi.getRecordDatumEntryList()) {
      @Nullable UrlDataValue value = entry.getDataValue();
      if (value == null) throw new PsiProcessingException(
          String.format("Got 'null' value for field '%s'", entry.getQid().getCanonicalName()), psi, context
      );
      else fields.put(entry.getQid().getCanonicalName(), parseValue(value, context));
    }

    return new GRecordDatum(getTypeRef(typeRef, context), fields, EpigraphPsiUtil.getLocation(psi));
  }

  public static @NotNull GMapDatum parseMap(@NotNull UrlMapDatum psi, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<GDatum, GDataValue> map = new LinkedHashMap<>();
    for (UrlMapDatumEntry entry : psi.getMapDatumEntryList()) {
      @Nullable UrlDataValue dataValue = entry.getDataValue();
      if (dataValue == null) throw new PsiProcessingException(
          String.format("Got 'null' value for key '%s'", entry.getDataValue().getText()), psi, context
      );
      else map.put(parseDatum(entry.getDatum(), context), parseValue(dataValue, context));
    }

    return new GMapDatum(getTypeRef(typeRef, context), map, EpigraphPsiUtil.getLocation(psi));
  }

  public static @NotNull GListDatum parseList(@NotNull UrlListDatum psi, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    final List<GDataValue> items = new ArrayList<>();

    for (UrlDataValue value : psi.getDataValueList())
      items.add(parseValue(value, context));

    return new GListDatum(getTypeRef(typeRef, context), items, EpigraphPsiUtil.getLocation(psi));
  }

  public static @NotNull GEnumDatum parseEnum(@NotNull UrlEnumDatum psi, PsiProcessingContext context)
      throws PsiProcessingException {
    final UrlQid qid = psi.getQid();
    if (qid == null)
      throw new PsiProcessingException("Enum value not specified", psi, context);
    else
      return new GEnumDatum(qid.getCanonicalName(), EpigraphPsiUtil.getLocation(psi));
  }

  public static @NotNull GPrimitiveDatum parsePrimitive(
      @NotNull UrlPrimitiveDatum psi,
      @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    final Object value;
    if (psi.getString() != null) {
      String text = psi.getString().getText();
      value = text.substring(1, text.length() - 1).replace("\\'", "'");
    } else if (psi.getBoolean() != null) {
      value = Boolean.valueOf(psi.getBoolean().getText());
    } else if (psi.getNumber() != null) {
      // todo make it stable and always parse as Double?
      String text = psi.getNumber().getText();
      if (text.contains(".")) value = Double.valueOf(text);
      else value = Long.valueOf(text);
    } else
      throw new PsiProcessingException(
          String.format("Don't know how to handle primitive '%s'", psi.getText()),
          psi,
          context
      );

    return new GPrimitiveDatum(getTypeRef(typeRef, context), value, EpigraphPsiUtil.getLocation(psi));
  }

  public static @NotNull GNullDatum parseNull(@NotNull UrlNullDatum psi, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();
    return new GNullDatum(getTypeRef(typeRef, context), EpigraphPsiUtil.getLocation(psi));
  }

  private static @Nullable TypeRef getTypeRef(UrlTypeRef typeRef, @NotNull PsiProcessingContext context)
      throws PsiProcessingException {
    return typeRef == null ? null : TypeRefs.fromPsi(typeRef, context);
  }
}
