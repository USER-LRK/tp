package seedu.address.model.employee;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an employee's annual leave entitlement in whole days.
 */
public final class LeaveEntitlement {

    public static final int DEFAULT_DAYS = 14;
    public static final int MAX_DAYS = 365;
    public static final String MESSAGE_CONSTRAINTS =
            "Annual leave entitlement must be a whole number from 0 to 365.";

    public final int value;

    /**
     * Creates an annual leave entitlement with the given number of days.
     */
    public LeaveEntitlement(int value) {
        checkArgument(isValidLeaveEntitlement(value), MESSAGE_CONSTRAINTS);
        this.value = value;
    }

    public static boolean isValidLeaveEntitlement(int value) {
        return value >= 0 && value <= MAX_DAYS;
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
        return other instanceof LeaveEntitlement otherEntitlement && value == otherEntitlement.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
