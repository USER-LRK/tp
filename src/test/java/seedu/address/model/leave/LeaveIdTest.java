package seedu.address.model.leave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class LeaveIdTest {

    @Test
    public void constructor_nonPositiveValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, LeaveId.MESSAGE_CONSTRAINTS, () -> new LeaveId(0));
        assertThrows(IllegalArgumentException.class, LeaveId.MESSAGE_CONSTRAINTS, () -> new LeaveId(-1));
    }

    @Test
    public void isValidLeaveId() {
        assertFalse(LeaveId.isValidLeaveId(Integer.MIN_VALUE));
        assertFalse(LeaveId.isValidLeaveId(0));
        assertTrue(LeaveId.isValidLeaveId(1));
        assertTrue(LeaveId.isValidLeaveId(Integer.MAX_VALUE));
    }

    @Test
    public void equals() {
        LeaveId leaveId = new LeaveId(12);

        assertTrue(leaveId.equals(leaveId));
        assertTrue(leaveId.equals(new LeaveId(12)));
        assertFalse(leaveId.equals(new LeaveId(13)));
        assertFalse(leaveId.equals(null));
        assertFalse(leaveId.equals(12));
    }

    @Test
    public void hashCode_sameValue_sameHashCode() {
        assertEquals(new LeaveId(12).hashCode(), new LeaveId(12).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("12", new LeaveId(12).toString());
    }
}
