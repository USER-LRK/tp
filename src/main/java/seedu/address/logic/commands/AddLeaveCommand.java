package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMPLOYEE_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.LeavePeriod;

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

    public static final String MESSAGE_NOT_IMPLEMENTED =
            "Leave recording has been parsed successfully but is not available yet.";

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
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
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
