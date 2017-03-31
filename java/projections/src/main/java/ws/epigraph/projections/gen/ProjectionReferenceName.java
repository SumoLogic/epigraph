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

package ws.epigraph.projections.gen;

import org.jetbrains.annotations.NotNull;
import ws.epigraph.lang.GenQn;
import ws.epigraph.lang.NamingConventions;
import ws.epigraph.lang.Qn;
import ws.epigraph.names.QualifiedTypeName;
import ws.epigraph.names.TypeName;
import ws.epigraph.types.TypeApi;

import java.util.Objects;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public class ProjectionReferenceName extends GenQn<ProjectionReferenceName.RefNameSegment, ProjectionReferenceName> {
  public static final ProjectionReferenceName EMPTY = new ProjectionReferenceName();

  public static @NotNull ProjectionReferenceName fromQn(@NotNull Qn qn) {
    RefNameSegment[] segments = new RefNameSegment[qn.size()];
    for (int i = 0; i < qn.size(); i++) {
      segments[i] = new StringRefNameSegment(qn.segments[i]);
    }
    return new ProjectionReferenceName(segments);
  }

  public ProjectionReferenceName(final @NotNull RefNameSegment... segments) {
    super(RefNameSegment.class, segments);
  }

  protected ProjectionReferenceName(final @NotNull RefNameSegment[] segments, final boolean copy) {
    super(RefNameSegment.class, segments, copy);
  }

  @Override
  protected ProjectionReferenceName newInstance(final @NotNull RefNameSegment[] segments, final boolean copy) {
    return new ProjectionReferenceName(segments, copy);
  }

  @Override
  protected ProjectionReferenceName emptyInstance() {
    return EMPTY;
  }

  public interface RefNameSegment {}

  public static final class StringRefNameSegment implements RefNameSegment {
    public final String string;

    public StringRefNameSegment(final String string) {
      this.string = NamingConventions.unquote(string);
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final StringRefNameSegment segment = (StringRefNameSegment) o;
      return Objects.equals(string, segment.string);
    }

    @Override
    public int hashCode() {
      return Objects.hash(string);
    }

    @Override
    public String toString() { return string; }
  }

  public static final class TypeRefNameSegment implements RefNameSegment {
    public final @NotNull TypeApi type;
    public final boolean shortName;

    public TypeRefNameSegment(final @NotNull TypeApi type, final boolean shortName) {
      this.type = type;
      this.shortName = shortName;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      final TypeRefNameSegment segment = (TypeRefNameSegment) o;
      return shortName == segment.shortName &&
             Objects.equals(type, segment.type);
    }

    @Override
    public int hashCode() {
      return Objects.hash(type, shortName);
    }

    @Override
    public String toString() {
      final TypeName typeName = type.name();

      if (typeName instanceof QualifiedTypeName) {
        @NotNull Qn fqn = ((QualifiedTypeName) typeName).toFqn();
        return shortName ? fqn.last() : fqn.toString();
      }

      return typeName.toString(); // do we need anything better here?
    }
  }


}
