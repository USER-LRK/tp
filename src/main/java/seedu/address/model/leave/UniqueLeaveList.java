package seedu.address.model.leave;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.leave.exceptions.DuplicateLeaveException;
import seedu.address.model.leave.exceptions.LeaveNotFoundException;

/**
 * A list of leave records that enforces unique leave IDs and does not allow nulls.
 */
public class UniqueLeaveList implements Iterable<Leave> {

    private final ObservableList<Leave> internalList = FXCollections.observableArrayList();
    private final ObservableList<Leave> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains a leave record with the same ID as {@code toCheck}.
     */
    public boolean contains(Leave toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameLeave);
    }

    /**
     * Returns the leave record with {@code leaveId}, or an empty value if none exists.
     */
    public Optional<Leave> findById(LeaveId leaveId) {
        requireNonNull(leaveId);
        return internalList.stream()
                .filter(leave -> leave.getLeaveId().equals(leaveId))
                .findFirst();
    }

    /**
     * Adds a leave record. Its leave ID must not already exist in the list.
     */
    public void add(Leave toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateLeaveException();
        }
        internalList.add(toAdd);
    }

    /**
     * Removes {@code toRemove}. The exact leave record must exist in the list.
     */
    public void remove(Leave toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new LeaveNotFoundException();
        }
    }

    public void setLeaves(UniqueLeaveList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list. The replacement must not contain duplicate leave IDs.
     */
    public void setLeaves(List<Leave> leaves) {
        requireAllNonNull(leaves);
        if (!leavesAreUnique(leaves)) {
            throw new DuplicateLeaveException();
        }
        internalList.setAll(leaves);
    }

    /**
     * Returns the backing list as an unmodifiable observable list.
     */
    public ObservableList<Leave> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Leave> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueLeaveList otherUniqueLeaveList)) {
            return false;
        }

        return internalList.equals(otherUniqueLeaveList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    private boolean leavesAreUnique(List<Leave> leaves) {
        for (int i = 0; i < leaves.size() - 1; i++) {
            for (int j = i + 1; j < leaves.size(); j++) {
                if (leaves.get(i).isSameLeave(leaves.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
