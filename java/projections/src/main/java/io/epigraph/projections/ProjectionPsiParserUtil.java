package io.epigraph.projections;

import com.intellij.psi.PsiElement;
import io.epigraph.gdata.GDataValue;
import io.epigraph.idl.gdata.IdlGDataPsiParser;
import io.epigraph.idl.parser.psi.IdlCustomParam;
import io.epigraph.idl.parser.psi.IdlDataValue;
import io.epigraph.idl.parser.psi.IdlOpTagName;
import io.epigraph.idl.parser.psi.IdlQid;
import io.epigraph.lang.Fqn;
import io.epigraph.projections.op.OpCustomParam;
import io.epigraph.psi.EpigraphPsiUtil;
import io.epigraph.psi.PsiProcessingException;
import io.epigraph.types.Type;
import io.epigraph.types.TypesResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionPsiParserUtil {
  @Nullable
  public static String getTagName(@Nullable IdlOpTagName tagNamePsi) {
    if (tagNamePsi == null) return null;
    @Nullable IdlQid qid = tagNamePsi.getQid();
    if (qid == null) return null;
    return qid.getCanonicalName();
  }

  @NotNull
  public static Type.Tag getTag(
      @NotNull Type type,
      @Nullable IdlOpTagName tagName,
      @Nullable Type.Tag defaultTag,
      @NotNull PsiElement location) throws PsiProcessingException {

    final Type.Tag tag;
    final String tagNameStr = getTagName(tagName);

    if (tagNameStr == null) {
      // get default tag
      if (defaultTag == null)
        throw new PsiProcessingException(
            String.format("Can't parse default tag projection for '%s', default tag not specified", type.name()),
            location
        );

      tag = defaultTag;
      verifyTag(type, tag, location);
    } else tag = getTag(type, tagNameStr, location);
    return tag;
  }

  @NotNull
  public static Type.Tag getTag(@NotNull Type type, @NotNull String tagName, @NotNull PsiElement location)
      throws PsiProcessingException {
    Type.Tag tag = type.tagsMap().get(tagName);
    if (tag == null)
      throw new PsiProcessingException(
          String.format("Can't find tag '%s' in '%s'", tagName, type.name()),
          location
      );
    return tag;
  }

  public static void verifyTag(@NotNull Type type, @NotNull Type.Tag tag, @NotNull PsiElement location)
      throws PsiProcessingException {
    if (!type.tags().contains(tag))
      throw new PsiProcessingException(String.format("Tag '%s' doesn't belong to type '%s'",
                                                     tag.name(),
                                                     type.name()
      ), location);
  }

  @NotNull
  public static Type getType(@NotNull TypesResolver resolver, @NotNull Fqn fqn, @NotNull PsiElement location)
      throws PsiProcessingException {
    @Nullable Type type = resolver.resolve(fqn);
    if (type == null) throw new PsiProcessingException(String.format("Can't find type '%s'", fqn), location);
    return type;
  }

  @Nullable
  public static Map<String, OpCustomParam> parseCustomParam(
      @Nullable Map<String, OpCustomParam> customParamsMap,
      @Nullable IdlCustomParam customParamPsi)
      throws PsiProcessingException {

    if (customParamPsi != null) {
      if (customParamsMap == null) customParamsMap = new HashMap<>();
      @Nullable IdlDataValue customParamValuePsi = customParamPsi.getDataValue();
      if (customParamValuePsi != null) {
        @NotNull String customParamName = customParamPsi.getQid().getCanonicalName();
        @NotNull GDataValue customParamValue = IdlGDataPsiParser.parseValue(customParamValuePsi);
        customParamsMap.put(customParamName,
                            new OpCustomParam(customParamName,
                                              customParamValue,
                                              EpigraphPsiUtil.getLocation(customParamPsi)
                            )
        );
      }
    }
    return customParamsMap;
  }
}
