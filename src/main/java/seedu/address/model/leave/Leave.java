package seedu.address.model.leave;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a leave record.
 * Guarantees: immutable; the leave ID and period are present and valid.
 */
public class Leave {

    private final LeaveId leaveId;
    private final LeavePeriod period;

    /**
     * Constructs a {@code Leave} with its generated ID and inclusive period.
     */
    public Leave(LeaveId leaveId, LeavePeriod period) {
        requireAllNonNull(leaveId, period);
        this.leaveId = leaveId;
        this.period = period;
    }

    public LeaveId getLeaveId() {
        return leaveId;
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
                && period.equals(otherLeave.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leaveId, period);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("leaveId", leaveId)
                .add("period", period)
                .toString();
    }
}
