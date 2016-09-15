package io.epigraph.idl.gdata;

import io.epigraph.idl.parser.psi.*;
import io.epigraph.lang.Fqn;
import io.epigraph.lang.gdata.*;
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
    if (psi.getDataVar() != null) return parseVar(psi.getDataVar());
    else if (psi.getVarValue() != null) return parseVarValue(psi.getVarValue());
    else throw new PsiProcessingException("Neither dataVar nor varValue is set", psi);
  }

  @NotNull
  public static GDataVar parseVar(@NotNull IdlDataVar psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    LinkedHashMap<String, GDataVarValue> tags = new LinkedHashMap<>();
    for (IdlDataVarEntry entry : psi.getDataVarEntryList()) {
      @Nullable IdlVarValue value = entry.getVarValue();
      if (value != null)
        tags.put(entry.getQid().getCanonicalName(), parseVarValue(value));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for tag '%s'", entry.getQid().getCanonicalName()), psi
        );
    }


    return new GDataVar(getTypeRef(typeRef), tags);
  }

  @NotNull
  public static GDataVarValue parseVarValue(@NotNull IdlVarValue psi) throws PsiProcessingException {
    if (psi instanceof IdlDataRecord)
      return parseRecord((IdlDataRecord) psi);
    else if (psi instanceof IdlDataMap)
      return parseMap((IdlDataMap) psi);
    else if (psi instanceof IdlDataList)
      return parseList((IdlDataList) psi);
    else if (psi instanceof IdlDataEnum)
      return parseEnum((IdlDataEnum) psi);
    else if (psi instanceof IdlDataPrimitive)
      return parsePrimitive((IdlDataPrimitive) psi);
    else if (psi instanceof IdlDataNull)
      return parseNull((IdlDataNull) psi);
    else throw new PsiProcessingException("Unknown value element", psi);
  }

  @NotNull
  public static GDataRecord parseRecord(@NotNull IdlDataRecord psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    LinkedHashMap<String, GDataValue> fields = new LinkedHashMap<>();
    for (IdlDataRecordEntry entry : psi.getDataRecordEntryList()) {
      @Nullable IdlDataValue value = entry.getDataValue();
      if (value != null)
        fields.put(entry.getQid().getCanonicalName(), parseValue(value));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for field '%s'", entry.getQid().getCanonicalName()), psi
        );
    }

    return new GDataRecord(getTypeRef(typeRef), fields);
  }

  @NotNull
  public static GDataMap parseMap(@NotNull IdlDataMap psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    LinkedHashMap<GDataVarValue, GDataValue> map = new LinkedHashMap<>();
    for (IdlDataMapEntry entry : psi.getDataMapEntryList()) {
      @Nullable IdlDataValue dataValue = entry.getDataValue();
      if (dataValue != null)
        map.put(parseVarValue(entry.getVarValue()), parseValue(dataValue));
      else
        throw new PsiProcessingException(
            String.format("Got 'null' value for key '%s'", entry.getVarValue().getText()), psi
        );
    }

    return new GDataMap(getTypeRef(typeRef), map);
  }

  @NotNull
  public static GDataList parseList(@NotNull IdlDataList psi) throws PsiProcessingException {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();

    final List<GDataValue> items = new ArrayList<>();

    for (IdlDataValue value : psi.getDataValueList())
      items.add(parseValue(value));

    return new GDataList(getTypeRef(typeRef), items);
  }

  @NotNull
  public static GDataEnum parseEnum(@NotNull IdlDataEnum psi) {
    return new GDataEnum(psi.getQid().getCanonicalName());
  }

  @NotNull
  public static GDataPrimitive parsePrimitive(@NotNull IdlDataPrimitive psi) throws PsiProcessingException {
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
    } else throw new PsiProcessingException(String.format("Don't know how to handle primitive '%s'", psi.getText()), psi);

    return new GDataPrimitive(getTypeRef(typeRef), value);
  }

  @NotNull
  public static GDataNull parseNull(@NotNull IdlDataNull psi) {
    @Nullable IdlFqnTypeRef typeRef = psi.getFqnTypeRef();
    return new GDataNull(getTypeRef(typeRef));
  }

  @Nullable
  private static Fqn getTypeRef(IdlFqnTypeRef typeRef) {return typeRef == null ? null : typeRef.getFqn().getFqn();}
}
