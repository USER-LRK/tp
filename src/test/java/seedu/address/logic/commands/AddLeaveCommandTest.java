package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.LeavePeriod;

public class AddLeaveCommandTest {

    private static final EmployeeId EMPLOYEE_ID = new EmployeeId("1");
    private static final LeavePeriod LEAVE_PERIOD = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));

    @Test
    public void constructor_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddLeaveCommand(null, LEAVE_PERIOD));
        assertThrows(NullPointerException.class, () -> new AddLeaveCommand(EMPLOYEE_ID, null));
    }

    @Test
    public void execute_notImplemented_throwsCommandException() {
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);

        assertThrows(CommandException.class, AddLeaveCommand.MESSAGE_NOT_IMPLEMENTED, () ->
                command.execute(new ModelManager()));
    }

    @Test
    public void equals() {
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);
        AddLeaveCommand sameCommand = new AddLeaveCommand(new EmployeeId("1"), new LeavePeriod(
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));
        AddLeaveCommand differentEmployee = new AddLeaveCommand(new EmployeeId("2"), LEAVE_PERIOD);
        AddLeaveCommand differentPeriod = new AddLeaveCommand(EMPLOYEE_ID, new LeavePeriod(
                LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 9)));

        assertTrue(command.equals(command));
        assertTrue(command.equals(sameCommand));
        assertFalse(command.equals(differentEmployee));
        assertFalse(command.equals(differentPeriod));
        assertFalse(command.equals(null));
        assertFalse(command.equals("leave add"));
    }

    @Test
    public void toStringMethod() {
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);
        String expected = AddLeaveCommand.class.getCanonicalName()
                + "{employeeId=1, leavePeriod=05-10-2026 to 07-10-2026}";

        assertEquals(expected, command.toString());
    }
}
