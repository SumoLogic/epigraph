package io.epigraph.projections.gen;

import io.epigraph.types.PrimitiveType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenPrimitiveModelProjection<
    MP extends GenPrimitiveModelProjection</*MP*/?, ?>,
    M extends PrimitiveType<?>
    > extends GenModelProjection<MP, M> {
}
