package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMPLOYEE_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;

import java.time.LocalDate;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddLeaveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.LeavePeriod;

/**
 * Parses arguments for the leave-add command.
 */
public class AddLeaveCommandParser implements Parser<AddLeaveCommand> {

    @Override
    public AddLeaveCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_EMPLOYEE_ID, PREFIX_START_DATE, PREFIX_END_DATE);

        if (!arePrefixesPresent(argMultimap, PREFIX_EMPLOYEE_ID, PREFIX_START_DATE, PREFIX_END_DATE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLeaveCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_EMPLOYEE_ID, PREFIX_START_DATE, PREFIX_END_DATE);
        EmployeeId employeeId = ParserUtil.parseEmployeeId(argMultimap.getValue(PREFIX_EMPLOYEE_ID).orElseThrow());
        LocalDate startDate = ParserUtil.parseStartDate(argMultimap.getValue(PREFIX_START_DATE).orElseThrow());
        LocalDate endDate = ParserUtil.parseEndDate(argMultimap.getValue(PREFIX_END_DATE).orElseThrow());

        try {
            return new AddLeaveCommand(employeeId, new LeavePeriod(startDate, endDate));
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
