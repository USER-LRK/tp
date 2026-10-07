package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddLeaveCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses subcommands under the {@code leave} command group.
 */
public class LeaveCommandParser implements Parser<Command> {

    private static final Pattern SUBCOMMAND_FORMAT = Pattern.compile("(?<subcommandWord>\\S+)(?<arguments>.*)");

    @Override
    public Command parse(String args) throws ParseException {
        Matcher matcher = SUBCOMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLeaveCommand.MESSAGE_USAGE));
        }

        String subcommandWord = matcher.group("subcommandWord");
        String arguments = matcher.group("arguments");
        if (AddLeaveCommand.SUBCOMMAND_WORD.equals(subcommandWord)) {
            return new AddLeaveCommandParser().parse(arguments);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLeaveCommand.MESSAGE_USAGE));
    }
}
