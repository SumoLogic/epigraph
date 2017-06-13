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

package ws.epigraph.java;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public final class Settings {

  // make these truly configurable if needed
  private final boolean generateTextLocations = false;

  private final boolean generateSeparateMethodsForVarProjections = false;

  private final @NotNull ServerSettings serverSettings;

  private final @NotNull ClientSettings clientSettings;

  private final boolean java8Annotations;

  public Settings(
      final @NotNull ServerSettings serverSettings,
      final @NotNull ClientSettings clientSettings,
      boolean java8Annotations
  ) {
    this.serverSettings = serverSettings;
    this.clientSettings = clientSettings;
    this.java8Annotations = java8Annotations;
  }

  /** Whether Java 8 annotations should be used in generated code. */
  public boolean java8Annotations() { return java8Annotations; }

  @Contract(pure = true)
  public boolean generateTextLocations() { return generateTextLocations; }

  @Contract(pure = true)
  public boolean generateSeparateMethodsForVarProjections() { return generateSeparateMethodsForVarProjections; }

  @Contract(pure = true)
  public @NotNull ServerSettings serverSettings() { return serverSettings; }

  @Contract(pure = true)
  public @NotNull ClientSettings clientSettings() { return clientSettings; }

  public boolean debug() { return "true".equals(System.getProperty("epigraph.debug")); }


  public static final class ServerSettings {

    private final boolean generate;

    private final @Nullable List<String> services; // null means include all
    private final @Nullable List<String> transformers; // null means include all

    public ServerSettings(
        final boolean generate,
        final @Nullable List<String> services,
        final @Nullable List<String> transformers
    ) {

      this.generate = generate;
      this.services = services;
      this.transformers = transformers;
    }

    @Contract(pure = true)
    public boolean generate() { return generate; }

    @Contract(pure = true)
    public @Nullable List<String> transformers() { return transformers; }

    @Contract(pure = true)
    public @Nullable List<String> services() { return services; }
  }

  public static final class ClientSettings {

    private final boolean generate;

    private final @Nullable List<String> services; // null means include all

    public ClientSettings(
        final boolean generate,
        final @Nullable List<String> services
    ) {

      this.generate = generate;
      this.services = services;
    }

    @Contract(pure = true)
    public boolean generate() { return generate; }

    @Contract(pure = true)
    public @Nullable List<String> services() { return services; }
  }
}
