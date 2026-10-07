package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * An immutable employee identifier from 1 to 999999, without leading zeros.
 * Validates the identifier's format, not its uniqueness or whether an employee exists.
 * An employee ID is independent of an employee's position in a displayed list.
 */
public final class EmployeeId {

    public static final int MAX_VALUE = 999999;

    public static final String MESSAGE_CONSTRAINTS =
            "Employee ID must be an integer from 1 to 999999 without leading zeros.";
    public static final String VALIDATION_REGEX = "[1-9][0-9]{0,5}";

    public final String value;

    /**
     * Constructs an employee ID from its canonical representation. Whitespace is not trimmed.
     *
     * @param employeeId A valid employee ID.
     * @throws NullPointerException if {@code employeeId} is null.
     * @throws IllegalArgumentException if {@code employeeId} is invalid.
     */
    public EmployeeId(String employeeId) {
        requireNonNull(employeeId);
        checkArgument(isValidEmployeeId(employeeId), MESSAGE_CONSTRAINTS);
        value = employeeId;
    }

    /**
     * Returns whether {@code test} contains one to six ASCII digits, starting with 1 to 9.
     * Checks the format without numeric conversion, so oversized input cannot overflow.
     *
     * @throws NullPointerException if {@code test} is null.
     */
    public static boolean isValidEmployeeId(String test) {
        requireNonNull(test);
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof EmployeeId otherId && value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
