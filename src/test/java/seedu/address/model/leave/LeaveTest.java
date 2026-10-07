package seedu.address.model.leave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.employee.EmployeeId;

public class LeaveTest {

    private static final LeaveId LEAVE_ID = new LeaveId(1);
    private static final EmployeeId EMPLOYEE_ID = new EmployeeId("1024");
    private static final LeavePeriod PERIOD = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Leave(null, EMPLOYEE_ID, PERIOD));
        assertThrows(NullPointerException.class, () -> new Leave(LEAVE_ID, null, PERIOD));
        assertThrows(NullPointerException.class, () -> new Leave(LEAVE_ID, EMPLOYEE_ID, null));
    }

    @Test
    public void accessors_returnConstructedValues() {
        Leave leave = new Leave(LEAVE_ID, EMPLOYEE_ID, PERIOD);

        assertEquals(LEAVE_ID, leave.getLeaveId());
        assertEquals(EMPLOYEE_ID, leave.getEmployeeId());
        assertEquals(PERIOD, leave.getPeriod());
    }

    @Test
    public void isSameLeave() {
        Leave leave = new Leave(LEAVE_ID, EMPLOYEE_ID, PERIOD);
        Leave sameIdDifferentDetails = new Leave(LEAVE_ID, new EmployeeId("2048"), new LeavePeriod(
                LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)));

        assertTrue(leave.isSameLeave(leave));
        assertTrue(leave.isSameLeave(sameIdDifferentDetails));
        assertFalse(leave.isSameLeave(new Leave(new LeaveId(2), EMPLOYEE_ID, PERIOD)));
        assertFalse(leave.isSameLeave(null));
    }

    @Test
    public void equals() {
        Leave leave = new Leave(LEAVE_ID, EMPLOYEE_ID, PERIOD);

        assertTrue(leave.equals(leave));
        assertTrue(leave.equals(new Leave(new LeaveId(1), new EmployeeId("1024"), PERIOD)));
        assertFalse(leave.equals(new Leave(new LeaveId(2), EMPLOYEE_ID, PERIOD)));
        assertFalse(leave.equals(new Leave(LEAVE_ID, new EmployeeId("2048"), PERIOD)));
        assertFalse(leave.equals(new Leave(LEAVE_ID, EMPLOYEE_ID, new LeavePeriod(
                LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)))));
        assertFalse(leave.equals(null));
        assertFalse(leave.equals(LEAVE_ID));
    }

    @Test
    public void hashCode_sameFields_sameHashCode() {
        assertEquals(new Leave(LEAVE_ID, EMPLOYEE_ID, PERIOD).hashCode(),
                new Leave(new LeaveId(1), new EmployeeId("1024"), PERIOD).hashCode());
    }

    @Test
    public void toStringMethod() {
        Leave leave = new Leave(LEAVE_ID, EMPLOYEE_ID, PERIOD);
        String expected = Leave.class.getCanonicalName()
                + "{leaveId=1, employeeId=1024, period=05-10-2026 to 07-10-2026}";
        assertEquals(expected, leave.toString());
    }
}
