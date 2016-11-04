package ws.epigraph.projections.gen;

import ws.epigraph.types.PrimitiveType;

/**
 * @author <a href="mailto:konstantin.sobolev@gmail.com">Konstantin Sobolev</a>
 */
public interface GenPrimitiveModelProjection<
    MP extends GenPrimitiveModelProjection</*MP*/?, ?>,
    M extends PrimitiveType<?>
    > extends GenModelProjection<MP, M> {
}
