package seedu.address.model.leave.exceptions;

/**
 * Signals that a leave request exceeds the employee's remaining annual leave entitlement.
 */
public class InsufficientLeaveException extends RuntimeException {

    private final int requestedDays;
    private final int remainingDays;

    /**
     * Constructs an exception describing the rejected request.
     */
    public InsufficientLeaveException(int requestedDays, int remainingDays) {
        this.requestedDays = requestedDays;
        this.remainingDays = remainingDays;
    }

    public int getRequestedDays() {
        return requestedDays;
    }

    public int getRemainingDays() {
        return remainingDays;
    }
}
