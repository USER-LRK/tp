package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMPLOYEE_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.model.leave.exceptions.InsufficientLeaveException;
import seedu.address.model.leave.exceptions.LeaveIdExhaustedException;
import seedu.address.model.leave.exceptions.OverlappingLeaveException;

/**
 * Represents a request to record leave for an employee.
 */
public class AddLeaveCommand extends Command {

    public static final String COMMAND_WORD = "leave";
    public static final String SUBCOMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + SUBCOMMAND_WORD
            + ": Records leave for an employee. "
            + "Parameters: "
            + PREFIX_EMPLOYEE_ID + "EMPLOYEE_ID "
            + PREFIX_START_DATE + "START_DATE "
            + PREFIX_END_DATE + "END_DATE\n"
            + "Dates must use DD-MM-YYYY format.\n"
            + "Example: " + COMMAND_WORD + " " + SUBCOMMAND_WORD + " "
            + PREFIX_EMPLOYEE_ID + "1 "
            + PREFIX_START_DATE + "05-10-2026 "
            + PREFIX_END_DATE + "07-10-2026";

    public static final String MESSAGE_SUCCESS = "Leave recorded successfully: "
            + "Leave ID %1$s; Employee ID %2$s; Period %3$s; Working days %4$d.";
    public static final String MESSAGE_EMPLOYEE_NOT_FOUND = "No employee exists with employee ID %s.";
    public static final String MESSAGE_OVERLAPPING_LEAVE =
            "This leave period overlaps an existing leave record for employee ID %s.";
    public static final String MESSAGE_INSUFFICIENT_LEAVE =
            "Employee ID %1$s has only %2$d annual leave day(s) remaining in %3$d, but this request needs %4$d.";
    public static final String MESSAGE_LEAVE_ID_EXHAUSTED =
            "No more leave IDs are available. The leave record was not added.";

    private final EmployeeId employeeId;
    private final LeavePeriod leavePeriod;

    /**
     * Creates an {@code AddLeaveCommand} for the given employee and leave period.
     */
    public AddLeaveCommand(EmployeeId employeeId, LeavePeriod leavePeriod) {
        requireAllNonNull(employeeId, leavePeriod);
        this.employeeId = employeeId;
        this.leavePeriod = leavePeriod;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        try {
            Leave addedLeave = model.addLeave(employeeId, leavePeriod);
            return new CommandResult(String.format(MESSAGE_SUCCESS, addedLeave.getLeaveId(), employeeId,
                    leavePeriod, leavePeriod.getWorkingDayCount()));
        } catch (EmployeeNotFoundException e) {
            throw new CommandException(String.format(MESSAGE_EMPLOYEE_NOT_FOUND, employeeId));
        } catch (OverlappingLeaveException e) {
            throw new CommandException(String.format(MESSAGE_OVERLAPPING_LEAVE, employeeId));
        } catch (InsufficientLeaveException e) {
            throw new CommandException(String.format(MESSAGE_INSUFFICIENT_LEAVE, employeeId,
                    e.getRemainingDays(), leavePeriod.getYear(), e.getRequestedDays()));
        } catch (LeaveIdExhaustedException e) {
            throw new CommandException(MESSAGE_LEAVE_ID_EXHAUSTED);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddLeaveCommand otherCommand)) {
            return false;
        }
        return employeeId.equals(otherCommand.employeeId)
                && leavePeriod.equals(otherCommand.leavePeriod);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("employeeId", employeeId)
                .add("leavePeriod", leavePeriod)
                .toString();
    }
}
