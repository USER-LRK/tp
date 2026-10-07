package seedu.address.model.leave.exceptions;

/**
 * Signals that an operation would result in duplicate leave IDs.
 */
public class DuplicateLeaveException extends RuntimeException {
    public DuplicateLeaveException() {
        super("Operation would result in duplicate leave records");
    }
}
