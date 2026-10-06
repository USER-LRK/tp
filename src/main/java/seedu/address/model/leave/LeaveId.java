package seedu.address.model.leave;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the unique identifier assigned to a leave record.
 * Guarantees: immutable; is valid as declared in {@link #isValidLeaveId(int)}.
 */
public class LeaveId {

    public static final String MESSAGE_CONSTRAINTS = "Leave ID must be a positive integer.";

    public final int value;

    /**
     * Constructs a {@code LeaveId}.
     *
     * @param value A positive integer assigned to a leave record.
     */
    public LeaveId(int value) {
        checkArgument(isValidLeaveId(value), MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    /**
     * Returns true if {@code value} can be used as a leave ID.
     */
    public static boolean isValidLeaveId(int value) {
        return value > 0;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof LeaveId otherLeaveId)) {
            return false;
        }

        return value == otherLeaveId.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
