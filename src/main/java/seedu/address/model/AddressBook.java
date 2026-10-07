package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.UniqueEmployeeList;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeIdExhaustedException;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameEmployee comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueEmployeeList employees = new UniqueEmployeeList();
    private int nextEmployeeId = 1;

    public AddressBook() {}

    /**
     * Creates an address book snapshot with the given employees and next employee ID.
     */
    public AddressBook(List<Employee> employees, int nextEmployeeId) {
        this.nextEmployeeId = setEmployeesAndGetNextId(employees, nextEmployeeId);
    }

    /**
     * Creates an AddressBook using the Employees in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the employee list with {@code employees}.
     * {@code employees} must not contain duplicate employees.
     */
    public void setEmployees(List<Employee> employees) {
        nextEmployeeId = setEmployeesAndGetNextId(employees, nextEmployeeId);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        int startingId = Math.max(nextEmployeeId, newData.getNextEmployeeId());
        nextEmployeeId = setEmployeesAndGetNextId(newData.getEmployeeList(), startingId);
    }

    //// employee-level operations

    /**
     * Returns true if an employee with the same identity as {@code employee} exists in the address book.
     */
    public boolean hasEmployee(Employee employee) {
        requireNonNull(employee);
        return employees.contains(employee);
    }

    /**
     * Adds an employee to the address book.
     * The employee must not already exist in the address book.
     */
    public Employee addEmployee(Employee employee) {
        requireNonNull(employee);

        Employee employeeWithId = employee;
        if (employee.getEmployeeId().isPresent()) {
            int employeeId = Integer.parseInt(employee.getEmployeeId().orElseThrow().value);
            if (employeeId < nextEmployeeId) {
                throw new DuplicateEmployeeException();
            }
        } else {
            if (nextEmployeeId > EmployeeId.MAX_VALUE) {
                throw new EmployeeIdExhaustedException();
            }
            employeeWithId = employee.withEmployeeId(new EmployeeId(String.valueOf(nextEmployeeId)));
        }

        employees.add(employeeWithId);
        int employeeId = Integer.parseInt(employeeWithId.getEmployeeId().orElseThrow().value);
        nextEmployeeId = Math.max(nextEmployeeId, employeeId + 1);
        return employeeWithId;
    }

    /**
     * Replaces the given employee {@code target} in the list with {@code editedEmployee}.
     * {@code target} must exist in the address book.
     * The employee identity of {@code editedEmployee} must not be the same as another existing employee in
     * the address book.
     */
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireAllNonNull(target, editedEmployee);

        EmployeeId targetId = target.getEmployeeId().orElseThrow();
        Employee employeeWithPreservedId = editedEmployee.getEmployeeId()
                .map(id -> {
                    if (!id.equals(targetId)) {
                        throw new IllegalArgumentException("An employee's ID cannot be changed");
                    }
                    return editedEmployee;
                })
                .orElseGet(() -> editedEmployee.withEmployeeId(targetId));

        employees.setEmployee(target, employeeWithPreservedId);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeEmployee(Employee key) {
        employees.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("employees", employees)
                .add("nextEmployeeId", nextEmployeeId)
                .toString();
    }

    @Override
    public ObservableList<Employee> getEmployeeList() {
        return employees.asUnmodifiableObservableList();
    }

    @Override
    public int getNextEmployeeId() {
        return nextEmployeeId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return employees.equals(otherAddressBook.employees)
                && nextEmployeeId == otherAddressBook.nextEmployeeId;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(employees, nextEmployeeId);
    }

    /**
     * Replaces the employee list after assigning IDs to any legacy or draft employees. The returned ID is
     * strictly greater than every assigned ID and never lower than {@code startingId}.
     */
    private int setEmployeesAndGetNextId(List<Employee> employees, int startingId) {
        requireAllNonNull(employees);
        if (startingId < 1 || startingId > EmployeeId.MAX_VALUE + 1) {
            throw new IllegalArgumentException("Invalid next employee ID: " + startingId);
        }

        int nextId = startingId;
        for (Employee employee : employees) {
            if (employee.getEmployeeId().isPresent()) {
                int employeeId = Integer.parseInt(employee.getEmployeeId().orElseThrow().value);
                nextId = Math.max(nextId, employeeId + 1);
            }
        }

        List<Employee> employeesWithIds = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.getEmployeeId().isPresent()) {
                employeesWithIds.add(employee);
                continue;
            }
            if (nextId > EmployeeId.MAX_VALUE) {
                throw new EmployeeIdExhaustedException();
            }
            employeesWithIds.add(employee.withEmployeeId(new EmployeeId(String.valueOf(nextId))));
            nextId++;
        }

        this.employees.setEmployees(employeesWithIds);
        return nextId;
    }
}
