package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.testutil.EmployeeBuilder;

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
    public void execute_validLeave_addsLeave() {
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(new EmployeeBuilder().build());
        ModelManager model = new ModelManager(addressBook, new UserPrefs());
        ModelManager expectedModel = new ModelManager(addressBook, new UserPrefs());
        expectedModel.addLeave(EMPLOYEE_ID, LEAVE_PERIOD);
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);

        String expectedMessage = String.format(AddLeaveCommand.MESSAGE_SUCCESS,
                1, EMPLOYEE_ID, LEAVE_PERIOD, LEAVE_PERIOD.getWorkingDayCount());
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unknownEmployee_throwsCommandException() {
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);

        assertCommandFailure(command, new ModelManager(),
                String.format(AddLeaveCommand.MESSAGE_EMPLOYEE_NOT_FOUND, EMPLOYEE_ID));
    }

    @Test
    public void execute_overlappingLeave_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        addressBook.addEmployee(new EmployeeBuilder().build());
        addressBook.addLeave(EMPLOYEE_ID, LEAVE_PERIOD);
        ModelManager model = new ModelManager(addressBook, new UserPrefs());
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);

        assertCommandFailure(command, model,
                String.format(AddLeaveCommand.MESSAGE_OVERLAPPING_LEAVE, EMPLOYEE_ID));
    }

    @Test
    public void execute_insufficientEntitlement_throwsCommandException() {
        AddressBook addressBook = new AddressBook();
        Employee employee = new EmployeeBuilder().withLeaveEntitlement(2).build();
        addressBook.addEmployee(employee);
        ModelManager model = new ModelManager(addressBook, new UserPrefs());
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);
        String expectedMessage = String.format(AddLeaveCommand.MESSAGE_INSUFFICIENT_LEAVE,
                EMPLOYEE_ID, 2, 2026, 3);

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void execute_leaveIdsExhausted_throwsCommandException() {
        Employee employee = new EmployeeBuilder().withEmployeeId("1").build();
        AddressBook addressBook = new AddressBook(List.of(employee), 2,
                List.of(), (long) Integer.MAX_VALUE + 1);
        ModelManager model = new ModelManager(addressBook, new UserPrefs());
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);

        assertCommandFailure(command, model, AddLeaveCommand.MESSAGE_LEAVE_ID_EXHAUSTED);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddLeaveCommand command = new AddLeaveCommand(EMPLOYEE_ID, LEAVE_PERIOD);

        assertThrows(NullPointerException.class, () -> command.execute(null));
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
