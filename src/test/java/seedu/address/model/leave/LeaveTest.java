package seedu.address.model.leave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class LeaveTest {

    private static final LeaveId LEAVE_ID = new LeaveId(1);
    private static final LeavePeriod PERIOD = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Leave(null, PERIOD));
        assertThrows(NullPointerException.class, () -> new Leave(LEAVE_ID, null));
    }

    @Test
    public void accessors_returnConstructedValues() {
        Leave leave = new Leave(LEAVE_ID, PERIOD);

        assertEquals(LEAVE_ID, leave.getLeaveId());
        assertEquals(PERIOD, leave.getPeriod());
    }

    @Test
    public void isSameLeave() {
        Leave leave = new Leave(LEAVE_ID, PERIOD);
        Leave sameIdDifferentPeriod = new Leave(LEAVE_ID, new LeavePeriod(
                LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)));

        assertTrue(leave.isSameLeave(leave));
        assertTrue(leave.isSameLeave(sameIdDifferentPeriod));
        assertFalse(leave.isSameLeave(new Leave(new LeaveId(2), PERIOD)));
        assertFalse(leave.isSameLeave(null));
    }

    @Test
    public void equals() {
        Leave leave = new Leave(LEAVE_ID, PERIOD);

        assertTrue(leave.equals(leave));
        assertTrue(leave.equals(new Leave(new LeaveId(1), PERIOD)));
        assertFalse(leave.equals(new Leave(new LeaveId(2), PERIOD)));
        assertFalse(leave.equals(new Leave(LEAVE_ID, new LeavePeriod(
                LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)))));
        assertFalse(leave.equals(null));
        assertFalse(leave.equals(LEAVE_ID));
    }

    @Test
    public void hashCode_sameFields_sameHashCode() {
        assertEquals(new Leave(LEAVE_ID, PERIOD).hashCode(), new Leave(new LeaveId(1), PERIOD).hashCode());
    }

    @Test
    public void toStringMethod() {
        Leave leave = new Leave(LEAVE_ID, PERIOD);
        String expected = Leave.class.getCanonicalName()
                + "{leaveId=1, period=05-10-2026 to 07-10-2026}";
        assertEquals(expected, leave.toString());
    }
}
