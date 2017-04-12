package ws.epigraph.tests;

import org.junit.Test;
import ws.epigraph.errors.ErrorValue;
import ws.epigraph.tests.codegenstress.Imm_;
import ws.epigraph.tests.codegenstress.ImmTypeDataValueBuilder;

import static ws.epigraph.tests.PersonRecord.*;

/**
 * Verifies generated setter interfaces to be usable.
 *
 * @author yegor 2017-04-11.
 */
public class FieldSettersTest {

    private static <B extends PersonRecord & SetId & SetFirstName & SetLastName> B populate(B b) {
        b.setId(PersonId.create(123));
        b.setFirstName("first" + b.getId().getVal());
        return b;
    }

    @Test
    public void testPersonRecordSetters() {
        System.out.println(populate(PersonRecord.create()));
        populate(UserRecord.create());
    }

    private static <B extends Imm_ & Imm_.SetImm > B populate(B b) {
        Imm_.Builder nestedImm = Imm_.create().setImm_Error(new ErrorValue(404, "Not found"));
        nestedImm.setShared(nestedImm);
        b.setImm(nestedImm);
        return b;
    }

    @Test
    public void testImmSetters() {
        populate(Imm_.create());
        populate(ImmTypeDataValueBuilder.create());
    }

}
