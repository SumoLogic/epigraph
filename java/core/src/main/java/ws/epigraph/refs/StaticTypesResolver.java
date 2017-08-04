package ws.epigraph.refs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.epigraph.gen.Constants;
import ws.epigraph.types.AnonListType;
import ws.epigraph.types.AnonMapType;
import ws.epigraph.types.DataType;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.Type;
import ws.epigraph.util.Unmodifiable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Implementation of {@link TypesResolver} for static (generated, indexed) types.
 *
 * @author yegor 2017-08-01.
 */
public final class StaticTypesResolver implements TypesResolver {

  private static final @NotNull Logger logger = LoggerFactory.getLogger(StaticTypesResolver.class);

  /**
   * Lazy holder for the resolved types map.
   *
   * Note: Type resolution is delayed to prevent circular initialization issues with current generated code (type
   * annotations).
   */
  private static class TypesHolder { // TODO this should be a standalone singleton implementation of an interface

    /** Map from type name to resolved {@link Type.Static} instance. */
    static final @NotNull Map<@NotNull String, @NotNull ? extends Type.Static<?, ?>> resolvedTypes =
        _resolveTypes_Once();

    /**
     * Resolves indexed types and returns a map from a (resolved) type name to its {@link Type.Static} instance.
     *
     * Note: This method should be invoked only once (from {@link TypesHolder#resolvedTypes} initializer).
     */
    private static @NotNull Map<@NotNull String, @NotNull ? extends Type.Static<?, ?>> _resolveTypes_Once() {
      Map<@NotNull String, @NotNull ? extends Supplier<@Nullable ? extends Type.Static<?, ?>>> suppliers =
          StaticTypesResolver.instance()._getTypeSuppliers_Once();
      Map<String, Type.Static<?, ?>> types = new HashMap<>();
      for (Map.Entry<String, @NotNull ? extends Supplier<@Nullable ? extends Type.Static<?, ?>>> entry : suppliers
          .entrySet()) {
        Type.Static<?, ?> type = entry.getValue().get();
        if (type != null) { // check whether resolved type name matches indexed type name
          String typeName = entry.getKey();
          String resolvedTypeName = type.name().toString();
          if (!resolvedTypeName.equals(typeName)) {
            logger.error("Indexed type `{}` name doesn't match resolved type `{}` name", typeName, resolvedTypeName);
          }
          types.put(resolvedTypeName, type);
        }
      }
      return Unmodifiable.map(types);
    }

  }

  /** Lazy holder for the {@link StaticTypesResolver} singleton instance. */
  private static class Holder {

    static final @NotNull StaticTypesResolver instance = new StaticTypesResolver();

  }

  public static @NotNull StaticTypesResolver instance() { return Holder.instance; }

  private @Nullable Map<@NotNull String, @NotNull ? extends Supplier<@Nullable ? extends Type.Static<?, ?>>>
      typeSuppliers = loadTypeSuppliers();

  private StaticTypesResolver() {}

  /**
   * Reads and merges all `/epigraph/index/typesIndex.properties` resources from the classpath into a type name to
   * (validated) {@link Supplier} of its {@link Type.Static} instance.
   */
  private static @NotNull Map<
      @NotNull String, @NotNull ? extends Supplier<@Nullable ? extends Type.Static<?, ?>>
  > loadTypeSuppliers() {
    Map<String, @NotNull Supplier<@Nullable ? extends Type.Static<?, ?>>> typeSuppliers = new HashMap<>();
    try {
      // TODO utilize multiple providers (.properties, .json) and merge all?
      Enumeration<URL> urls = // TODO check if Thread.currentThread().getContextClassLoader() is more appropriate?
          StaticTypesResolver.class.getClassLoader().getResources(Constants.TypesIndex.resourcePath);
      Map<String, String> validatedTypeClassNames = new HashMap<>();
      int indexResourceConut;
      for (indexResourceConut = 0; urls.hasMoreElements(); ++indexResourceConut) {
        URL url = urls.nextElement();
        Properties typesIndexProperties = new Properties();
        try (InputStream is = url.openStream()) {
          if (is == null) { // this is unlikely to happen...
            logger.error("Couldn't find types index resource {}", url);
          } else try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            typesIndexProperties.load(reader);
          }
        } catch (IOException e) {
          logger.error("Failed to load types index resource {}", url, e);
        }
        for (String typeName : typesIndexProperties.stringPropertyNames()) {
          String typeClassName = typesIndexProperties.getProperty(typeName);
          String validatedTypeClassName = validatedTypeClassNames.get(typeName);
          if (validatedTypeClassName == null) {
            Supplier<@Nullable ? extends Type.Static<?, ?>> typeSupplier = getTypeSupplier(typeName, typeClassName);
            if (typeSupplier != null) {
              typeSuppliers.put(typeName, typeSupplier);
              validatedTypeClassNames.put(typeName, typeClassName);
            }
          } else if (!validatedTypeClassName.equals(typeClassName)) {
            logger.error(
                "Type `{}` already mapped to class `{}`, ignoring mapping to `{}`", // TODO print resource urls, too?
                typeName, validatedTypeClassName, typeClassName
            );
          }
        }
      }
      logger.info(
          "Loaded {} static type name to type class name mappings from {} index resource(s)",
          validatedTypeClassNames.size(), indexResourceConut
      );
    } catch (IOException e) {
      logger.error("Failed to enumerate types index resources", e);
      throw new RuntimeException("Failed to enumerate types index resources", e); // TODO better exception?
    }
    return Unmodifiable.map(typeSuppliers);
  }

  /**
   * Loads, initializes, and validates specified type class and returns a {@link Supplier} for the instance of the type
   * or null (in case something goes wrong).
   */
  private static @Nullable Supplier<@Nullable ? extends Type.Static<?, ?>> getTypeSupplier(
      @NotNull String typeName, @NotNull String typeClassName
  ) {
    try {
      Class<?> typeClass = Class.forName(typeClassName, true, StaticTypesResolver.class.getClassLoader());
      if (Type.Static.class.isAssignableFrom(typeClass)) {
        Supplier<@Nullable ? extends Type.Static<?, ?>> typeSupplier = null;
        for (Method method : typeClass.getDeclaredMethods()) {
          int mod = method.getModifiers();
          boolean isPublicStaticZeroArgSelfReturningInstanceMethod =
              Modifier.isPublic(mod) && Modifier.isStatic(mod) && "instance".equals(method.getName()) &&
                  method.getParameterCount() == 0 && typeClass.equals(method.getReturnType());
          if (isPublicStaticZeroArgSelfReturningInstanceMethod) {
            typeSupplier = () -> {
              try {
                return (Type.Static<?, ?>) method.invoke(null);
              } catch (Throwable e) { // TODO handle InterruptedException properly?
                logger.error("Failed to instantiate class `{}` for type `{}`", typeClass, typeName, e);
                return null;
              }
            };
          }
        }
        if (typeSupplier == null) {
          logger.error(
              "Class `{}` for type `{}` doesn't declare a `public static {} instance()` method",
              typeClass, typeName, typeClassName
          );
        }
        return typeSupplier;
      } else {
        logger.error("Class `{}` for type `{}` doesn't extend `{}`", typeClass, typeName, Type.Static.class);
        return null;
      }
    } catch (ClassNotFoundException e) {
      logger.error("Couldn't load class `{}` for type `{}`", typeClassName, typeName, e);
      return null;
    }
  }

  /** Returns type suppliers (and clears the instance field). */
  private Map<@NotNull String, @NotNull ? extends Supplier<@Nullable ? extends Type.Static<?, ?>>> _getTypeSuppliers_Once() {
    try { return typeSuppliers; } finally { typeSuppliers = null; }
  }

  /**
   * Returns an unmodifiable view of map from a (canonical, fully-qualified) epigraph type name to its {@link
   * Type.Static} instance.
   *
   * Note: In general, the contents of the map may change over time. However, this implementation provides a stable
   * non-changing one.
   */
  public @NotNull Map<@NotNull String, @NotNull ? extends Type.Static<?, ?>> types() {
    return TypesHolder.resolvedTypes;
  }

  @Override
  public @Nullable Type.Static<?, ?> resolve(@NotNull QnTypeRef typeRef) { return types().get(typeName(typeRef)); }

  @Override
  public @Nullable AnonListType.Static<?, ?, ?, ?, ?, ?> resolve(@NotNull AnonListRef typeRef) {
    Type type = types().get(typeName(typeRef));
    assert type == null || type instanceof AnonListType.Static;
    return (AnonListType.Static<?, ?, ?, ?, ?, ?>) type;
  }

  @Override
  public @Nullable AnonMapType.Static<?, ?, ?, ?, ?, ?, ?> resolve(@NotNull AnonMapRef typeRef) {
    Type type = types().get(typeName(typeRef));
    assert type == null || type instanceof AnonMapType.Static;
    return (AnonMapType.Static<?, ?, ?, ?, ?, ?, ?>) type;
  }

  @Override
  public @Nullable DataType resolve(@NotNull ValueTypeRef valueTypeRef) {
    return (DataType) TypesResolver.super.resolve(valueTypeRef);
  }

  private @NotNull String typeName(@NotNull TypeRef typeRef) {
    // this code reverses the logic of CType.toString (including subclasses) and CDataType.name
    if (typeRef instanceof QnTypeRef) {
      return ((QnTypeRef) typeRef).qn().toString();
    } else if (typeRef instanceof AnonMapRef) {
      AnonMapRef amr = (AnonMapRef) typeRef;
      String ktn = typeName(amr.keysType());
      String vtn = valueTypeName(amr.itemsType());
      return String.format("map[%s,%s]", ktn, vtn);
    } else if (typeRef instanceof AnonListRef) {
      return String.format("list[%s]", valueTypeName(((AnonListRef) typeRef).itemsType()));
    } else throw new IllegalArgumentException("Unknown type ref: " + typeRef.getClass().getName());
  }

  private @NotNull String valueTypeName(@NotNull ValueTypeRef ref) {
    // this code reverses the logic of CDataType.name
    String typeName = typeName(ref.typeRef());
    String defaultOverride = ref.defaultOverride();
    return defaultOverride == null || DatumType.MONO_TAG_NAME.equals(defaultOverride)
        ? typeName
        : typeName + " default " + defaultOverride; // FIXME " retro "?
  }

}
