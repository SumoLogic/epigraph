package com.sumologic.epigraph.ideaplugin.schema.psi.stubs;

import com.intellij.psi.stubs.NamedStub;
import com.sumologic.epigraph.ideaplugin.schema.psi.SchemaTypeDefElement;

/**
 * @author <a href="mailto:konstantin@sumologic.com">Konstantin Sobolev</a>
 */
public interface SchemaTypeDefStubBase<T extends SchemaTypeDefElement> extends NamedStub<T> {
}
