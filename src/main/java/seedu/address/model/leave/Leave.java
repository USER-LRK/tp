package seedu.address.model.leave;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.employee.EmployeeId;

/**
 * Represents a leave record.
 * Guarantees: immutable; the leave ID and period are present and valid.
 */
public class Leave {

    private final LeaveId leaveId;
    private final EmployeeId employeeId;
    private final LeavePeriod period;

    /**
     * Constructs a {@code Leave} with its generated ID and inclusive period.
     */
    public Leave(LeaveId leaveId, EmployeeId employeeId, LeavePeriod period) {
        requireAllNonNull(leaveId, employeeId, period);
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.period = period;
    }

    public LeaveId getLeaveId() {
        return leaveId;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public LeavePeriod getPeriod() {
        return period;
    }

    /**
     * Returns true if both leave records have the same generated leave ID.
     */
    public boolean isSameLeave(Leave otherLeave) {
        if (otherLeave == this) {
            return true;
        }

        return otherLeave != null && leaveId.equals(otherLeave.leaveId);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Leave otherLeave)) {
            return false;
        }

        return leaveId.equals(otherLeave.leaveId)
                && employeeId.equals(otherLeave.employeeId)
                && period.equals(otherLeave.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaveId, employeeId, period);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("leaveId", leaveId)
                .add("employeeId", employeeId)
                .add("period", period)
                .toString();
    }
}
