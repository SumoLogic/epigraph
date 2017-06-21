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

package ws.epigraph.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ws.epigraph.data.Datum;
import ws.epigraph.gdata.GDataToData;
import ws.epigraph.gdata.GDatum;
import ws.epigraph.lang.TextLocation;
import ws.epigraph.refs.TypesResolver;
import ws.epigraph.types.DatumType;
import ws.epigraph.types.DatumTypeApi;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class Annotation {
  private final @NotNull DatumTypeApi type;
  private final @NotNull GDatum gDatum;
  private @Nullable Datum datum;
  private final @NotNull TypesResolver resolver;
  private final @NotNull TextLocation location;

  public Annotation(
      @NotNull DatumTypeApi type,
      @NotNull GDatum value,
      @NotNull TextLocation location,
      @NotNull TypesResolver resolver) {

    this.type = type;
    this.gDatum = value;
    this.location = location;
    this.resolver = resolver;
  }

  /** @return annotation type */
  public @NotNull DatumTypeApi type() { return type; }

  /**
   * @return annotation value
   * @throws IllegalStateException    if called during compile-time (and data types are not available)
   * @throws IllegalArgumentException if {@code GDatum} value provided for this annotation cannot be converted to given type
   */
  public @Nullable Datum value() throws IllegalArgumentException, IllegalStateException {
    if (datum != null) return datum;

    if (type instanceof DatumType) {
      try {
        this.datum = GDataToData.transform((DatumType) type, gDatum, resolver).getDatum();
      } catch (GDataToData.ProcessingException e) {
        if (location.equals(TextLocation.UNKNOWN))
          throw new IllegalArgumentException(e);
        else
          throw new IllegalArgumentException("Error processing annotation defined at " + location, e);
      }
    } else
      throw new IllegalStateException("Can't access annotation value at compile time");

    return datum;
  }

  /**
   * @return (raw) {@code GDatum} value of this annotation
   */
  public @NotNull GDatum gDatum() { return gDatum; }

  public @NotNull TextLocation location() { return location; }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Annotation that = (Annotation) o;
    return Objects.equals(type, that.type) &&
           Objects.equals(gDatum, that.gDatum);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, gDatum);
  }
}
