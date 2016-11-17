/*
 * Copyright 2016 Sumo Logic
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

import ws.epigraph.gdata.*;
import ws.epigraph.psi.PsiProcessingError;
import ws.epigraph.url.TypeRefs;
import ws.epigraph.url.parser.psi.*;
import ws.epigraph.psi.EpigraphPsiUtil;
import ws.epigraph.psi.PsiProcessingException;
import ws.epigraph.refs.TypeRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class UrlGDataPsiParser {

  @NotNull
  public static GDataValue parseValue(@NotNull UrlDataValue psi, @NotNull List<PsiProcessingError> errors) throws PsiProcessingException {
    if (psi.getData() != null) return parseData(psi.getData(), errors);
    else if (psi.getDatum() != null) return parseDatum(psi.getDatum(), errors);
    else throw new PsiProcessingException("Neither data nor datum is set", psi, errors);
  }

  @NotNull
  public static GData parseData(@NotNull UrlData psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDatum> tags = new LinkedHashMap<>();
    for (UrlDataEntry entry : psi.getDataEntryList()) {
      @Nullable UrlDatum value = entry.getDatum();
      if (value != null)
        tags.put(entry.getQid().getCanonicalName(), parseDatum(value, errors));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for tag '%s'", entry.getQid().getCanonicalName()), psi, errors
        );
    }


    return new GData(getTypeRef(typeRef, errors), tags, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GDatum parseDatum(@NotNull UrlDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    if (psi instanceof UrlRecordDatum)
      return parseRecord((UrlRecordDatum) psi, errors);
    else if (psi instanceof UrlMapDatum)
      return parseMap((UrlMapDatum) psi, errors);
    else if (psi instanceof UrlListDatum)
      return parseList((UrlListDatum) psi, errors);
    else if (psi instanceof UrlEnumDatum)
      return parseEnum((UrlEnumDatum) psi);
    else if (psi instanceof UrlPrimitiveDatum)
      return parsePrimitive((UrlPrimitiveDatum) psi, errors);
    else if (psi instanceof UrlNullDatum)
      return parseNull((UrlNullDatum) psi, errors);
    else throw new PsiProcessingException("Unknown value element", psi, errors);
  }

  @NotNull
  public static GRecordDatum parseRecord(@NotNull UrlRecordDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<String, GDataValue> fields = new LinkedHashMap<>();
    for (UrlRecordDatumEntry entry : psi.getRecordDatumEntryList()) {
      @Nullable UrlDataValue value = entry.getDataValue();
      if (value != null)
        fields.put(entry.getQid().getCanonicalName(), parseValue(value, errors));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for field '%s'", entry.getQid().getCanonicalName()), psi, errors
        );
    }

    return new GRecordDatum(getTypeRef(typeRef, errors), fields, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GMapDatum parseMap(@NotNull UrlMapDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    LinkedHashMap<GDatum, GDataValue> map = new LinkedHashMap<>();
    for (UrlMapDatumEntry entry : psi.getMapDatumEntryList()) {
      @Nullable UrlDataValue dataValue = entry.getDataValue();
      if (dataValue != null)
        map.put(parseDatum(entry.getDatum(), errors), parseValue(dataValue, errors));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for key '%s'", entry.getDataValue().getText()), psi, errors
        );
    }

    return new GMapDatum(getTypeRef(typeRef, errors), map, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GListDatum parseList(@NotNull UrlListDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();

    final List<GDataValue> items = new ArrayList<>();

    for (UrlDataValue value : psi.getDataValueList())
      items.add(parseValue(value, errors));

    return new GListDatum(getTypeRef(typeRef, errors), items, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GDataEnum parseEnum(@NotNull UrlEnumDatum psi) {
    return new GDataEnum(psi.getQid().getCanonicalName(), EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GPrimitiveDatum parsePrimitive(@NotNull UrlPrimitiveDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
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
      throw new PsiProcessingException(
          String.format("Don't know how to handle primitive '%s'", psi.getText()),
          psi,
          errors
      );

    return new GPrimitiveDatum(getTypeRef(typeRef, errors), value, EpigraphPsiUtil.getLocation(psi));
  }

  @NotNull
  public static GNullDatum parseNull(@NotNull UrlNullDatum psi, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    @Nullable UrlTypeRef typeRef = psi.getTypeRef();
    return new GNullDatum(getTypeRef(typeRef, errors), EpigraphPsiUtil.getLocation(psi));
  }

  @Nullable
  private static TypeRef getTypeRef(UrlTypeRef typeRef, @NotNull List<PsiProcessingError> errors)
      throws PsiProcessingException {
    return typeRef == null ? null : TypeRefs.fromPsi(typeRef, errors);
  }
}