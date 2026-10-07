package seedu.address.model.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class EmployeeIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EmployeeId(null));
    }

    @Test
    public void isValidEmployeeId_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> EmployeeId.isValidEmployeeId(null));
    }

    @Test
    public void constructor_validIds_preservesValue() {
        for (String valid : new String[] {"1", "9", "10", "1024", "100000", "999999"}) {
            assertTrue(EmployeeId.isValidEmployeeId(valid), valid);
            EmployeeId employeeId = new EmployeeId(valid);
            assertEquals(valid, employeeId.value);
            assertEquals(valid, employeeId.toString());
        }
    }

    @Test
    public void constructor_invalidIds_throwsIllegalArgumentException() {
        for (String invalid : new String[] {"", " ", "0", "00", "01", "001024", "1000000",
            "2147483648", "9".repeat(1000), "-1", "+1", "1.0", "1e3", "1a", "1 2", "1\t2",
            " 1", "1 ", "1\n", "１２", "١٢", "1\u00a02"}) {
            assertFalse(EmployeeId.isValidEmployeeId(invalid), invalid);
            assertThrows(IllegalArgumentException.class, EmployeeId.MESSAGE_CONSTRAINTS, () ->
                    new EmployeeId(invalid));
        }
    }

    @Test
    public void equals_comparesIdValue() {
        EmployeeId employeeId = new EmployeeId("1024");
        assertTrue(employeeId.equals(employeeId));
        assertTrue(employeeId.equals(new EmployeeId("1024")));
        assertFalse(employeeId.equals(new EmployeeId("1025")));
        assertFalse(employeeId.equals(null));
        assertFalse(employeeId.equals("1024"));
        assertFalse(employeeId.equals(1024));
    }

    @Test
    public void hashCode_equalIds_deduplicatedInSet() {
        EmployeeId employeeId = new EmployeeId("1024");
        EmployeeId sameId = new EmployeeId("1024");
        assertEquals(employeeId.hashCode(), sameId.hashCode());
        Set<EmployeeId> ids = new HashSet<>();
        ids.add(employeeId);
        ids.add(sameId);
        assertEquals(Set.of(employeeId), ids);
    }
}
