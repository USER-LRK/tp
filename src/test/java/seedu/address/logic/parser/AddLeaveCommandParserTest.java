package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMPLOYEE_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddLeaveCommand;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.LeavePeriod;

public class AddLeaveCommandParserTest {

    private static final String VALID_EMPLOYEE_ID = "1";
    private static final String VALID_START_DATE = "05-10-2026";
    private static final String VALID_END_DATE = "07-10-2026";
    private static final String EMPLOYEE_ID_DESC = " " + PREFIX_EMPLOYEE_ID + VALID_EMPLOYEE_ID;
    private static final String START_DATE_DESC = " " + PREFIX_START_DATE + VALID_START_DATE;
    private static final String END_DATE_DESC = " " + PREFIX_END_DATE + VALID_END_DATE;

    private final AddLeaveCommandParser parser = new AddLeaveCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        AddLeaveCommand expectedCommand = new AddLeaveCommand(new EmployeeId(VALID_EMPLOYEE_ID), new LeavePeriod(
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));

        assertParseSuccess(parser, EMPLOYEE_ID_DESC + START_DATE_DESC + END_DATE_DESC, expectedCommand);
        assertParseSuccess(parser, END_DATE_DESC + EMPLOYEE_ID_DESC + START_DATE_DESC, expectedCommand);
    }

    @Test
    public void parse_missingField_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLeaveCommand.MESSAGE_USAGE);

        assertParseFailure(parser, START_DATE_DESC + END_DATE_DESC, expectedMessage);
        assertParseFailure(parser, EMPLOYEE_ID_DESC + END_DATE_DESC, expectedMessage);
        assertParseFailure(parser, EMPLOYEE_ID_DESC + START_DATE_DESC, expectedMessage);
        assertParseFailure(parser, "", expectedMessage);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLeaveCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "unexpected" + EMPLOYEE_ID_DESC + START_DATE_DESC + END_DATE_DESC,
                expectedMessage);
    }

    @Test
    public void parse_duplicateField_failure() {
        String validArguments = EMPLOYEE_ID_DESC + START_DATE_DESC + END_DATE_DESC;

        assertParseFailure(parser, EMPLOYEE_ID_DESC + validArguments,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMPLOYEE_ID));
        assertParseFailure(parser, START_DATE_DESC + validArguments,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START_DATE));
        assertParseFailure(parser, END_DATE_DESC + validArguments,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END_DATE));
    }

    @Test
    public void parse_invalidEmployeeId_failure() {
        assertParseFailure(parser, " " + PREFIX_EMPLOYEE_ID + "01" + START_DATE_DESC + END_DATE_DESC,
                EmployeeId.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidStartDate_failure() {
        assertParseFailure(parser, EMPLOYEE_ID_DESC + " " + PREFIX_START_DATE + "31-02-2026" + END_DATE_DESC,
                ParserUtil.MESSAGE_INVALID_START_DATE);
    }

    @Test
    public void parse_invalidEndDate_failure() {
        assertParseFailure(parser, EMPLOYEE_ID_DESC + START_DATE_DESC + " " + PREFIX_END_DATE + "31-02-2026",
                ParserUtil.MESSAGE_INVALID_END_DATE);
    }

    @Test
    public void parse_startAfterEnd_failure() {
        assertParseFailure(parser, EMPLOYEE_ID_DESC + " " + PREFIX_START_DATE + "08-10-2026" + END_DATE_DESC,
                LeavePeriod.MESSAGE_START_AFTER_END);
    }

    @Test
    public void parse_datesInDifferentYears_failure() {
        assertParseFailure(parser, EMPLOYEE_ID_DESC + " " + PREFIX_START_DATE + "31-12-2026"
                        + " " + PREFIX_END_DATE + "01-01-2027",
                LeavePeriod.MESSAGE_DIFFERENT_YEARS);
    }

    @Test
    public void parse_weekendOnlyPeriod_failure() {
        assertParseFailure(parser, EMPLOYEE_ID_DESC + " " + PREFIX_START_DATE + "10-10-2026"
                        + " " + PREFIX_END_DATE + "11-10-2026",
                LeavePeriod.MESSAGE_NO_WORKING_DAY);
    }
}
