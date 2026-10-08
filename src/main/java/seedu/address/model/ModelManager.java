package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeavePeriod;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Employee> filteredEmployees;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredEmployees = new FilteredList<>(this.addressBook.getEmployeeList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasEmployee(Employee employee) {
        requireNonNull(employee);
        return addressBook.hasEmployee(employee);
    }

    @Override
    public void deleteEmployee(Employee target) {
        addressBook.removeEmployee(target);
    }

    @Override
    public Employee addEmployee(Employee employee) {
        Employee addedEmployee = addressBook.addEmployee(employee);
        updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);
        return addedEmployee;
    }

    @Override
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireAllNonNull(target, editedEmployee);

        addressBook.setEmployee(target, editedEmployee);
    }

    @Override
    public Leave addLeave(EmployeeId employeeId, LeavePeriod leavePeriod) {
        requireAllNonNull(employeeId, leavePeriod);
        return addressBook.addLeave(employeeId, leavePeriod);
    }

    @Override
    public int getRemainingLeave(EmployeeId employeeId, int year) {
        requireNonNull(employeeId);
        return addressBook.getRemainingLeave(employeeId, year);
    }

    @Override
    public ObservableList<Leave> getLeaveList() {
        return addressBook.getLeaveList();
    }

    //=========== Filtered Employee List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Employee} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Employee> getFilteredEmployeeList() {
        return filteredEmployees;
    }

    @Override
    public void updateFilteredEmployeeList(Predicate<Employee> predicate) {
        requireNonNull(predicate);
        filteredEmployees.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredEmployees.equals(otherModelManager.filteredEmployees);
    }

}
