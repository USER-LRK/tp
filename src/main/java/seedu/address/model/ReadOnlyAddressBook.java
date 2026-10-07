package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.employee.Employee;
import seedu.address.model.leave.Leave;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns the next employee ID number that may be allocated.
     */
    int getNextEmployeeId();

    /**
     * Returns the next leave ID number that may be allocated.
     */
    long getNextLeaveId();

    /**
     * Returns an unmodifiable view of the employees list.
     * This list will not contain any duplicate employees.
     */
    ObservableList<Employee> getEmployeeList();

    /**
     * Returns an unmodifiable view of the central leave list.
     */
    ObservableList<Leave> getLeaveList();

}
