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

/* Created by yegor on 8/3/16. */

package ws.epigraph.data;

import ws.epigraph.types.DatumType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Datum {

  @NotNull DatumType type();

  @NotNull Datum.Raw _raw();

  @NotNull Datum.Imm toImmutable();

  @NotNull Val asValue();


  abstract class Impl<MyType extends DatumType> implements Datum {

    private final @NotNull MyType type;

    protected Impl(@NotNull MyType type) { this.type = type; }

    @Override
    public final @NotNull MyType type() { return type; }

  }


  interface Raw extends Datum {

    @Nullable Datum meta();

    @Override
    @NotNull Datum.Imm.Raw toImmutable();

    @Override
    @NotNull Val.Raw asValue();

  }


  interface Static extends Datum {

    @Override
    @NotNull Datum.Imm.Static toImmutable();

    @Override
    @NotNull Val.Static asValue();

  }


  interface Imm extends Datum, Immutable {

    @Override
    @NotNull Val.Imm asValue();


    interface Raw extends Datum.Imm, Datum.Raw {

      @Override
      @NotNull Val.Imm.Raw asValue();

    }


    interface Static extends Datum.Imm, Datum.Static {

      @Override
      @NotNull Val.Imm.Static asValue();

    }


  }


  interface Builder extends Datum, Mutable {

    @Override
    @NotNull Datum.Builder.Raw _raw();

    interface Raw extends Datum.Builder, Datum.Raw {

      @NotNull Builder setMeta(@Nullable Datum meta);

    }


    interface Static<MyImmDatum extends Datum.Imm.Static> extends Datum.Builder, Datum.Static {

      @Override
      @NotNull MyImmDatum toImmutable();

    }


  }


}
