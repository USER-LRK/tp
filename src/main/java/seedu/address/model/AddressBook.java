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
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.model.leave.UniqueLeaveList;
import seedu.address.model.leave.exceptions.InsufficientLeaveException;
import seedu.address.model.leave.exceptions.LeaveIdExhaustedException;
import seedu.address.model.leave.exceptions.OverlappingLeaveException;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSameEmployee comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueEmployeeList employees = new UniqueEmployeeList();
    private final UniqueLeaveList leaves = new UniqueLeaveList();
    private int nextEmployeeId = 1;
    private long nextLeaveId = 1;

    public AddressBook() {}

    /**
     * Creates an address book snapshot with the given employees and next employee ID.
     */
    public AddressBook(List<Employee> employees, int nextEmployeeId) {
        this(employees, nextEmployeeId, List.of(), 1);
    }

    /**
     * Creates an address book snapshot with the given employees, leaves, and next IDs.
     */
    public AddressBook(List<Employee> employees, int nextEmployeeId, List<Leave> leaves, long nextLeaveId) {
        this.nextEmployeeId = setEmployeesAndGetNextId(employees, nextEmployeeId);
        this.nextLeaveId = setLeavesAndGetNextId(leaves, nextLeaveId);
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
        removeOrphanedLeaves();
    }

    /**
     * Replaces the contents of the leave list with {@code leaves}.
     */
    public void setLeaves(List<Leave> leaves) {
        nextLeaveId = setLeavesAndGetNextId(leaves, nextLeaveId);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        int startingEmployeeId = Math.max(nextEmployeeId, newData.getNextEmployeeId());
        long startingLeaveId = Math.max(nextLeaveId, newData.getNextLeaveId());
        AddressBook replacement = new AddressBook(newData.getEmployeeList(), startingEmployeeId,
                newData.getLeaveList(), startingLeaveId);

        employees.setEmployees(replacement.employees);
        leaves.setLeaves(replacement.leaves);
        nextEmployeeId = replacement.nextEmployeeId;
        nextLeaveId = replacement.nextLeaveId;
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
        EmployeeId employeeId = key.getEmployeeId().orElseThrow();
        employees.remove(key);
        leaves.removeAllFor(employeeId);
    }

    //// leave-level operations

    /**
     * Adds a leave record for an existing employee and assigns it the next leave ID.
     */
    public Leave addLeave(EmployeeId employeeId, LeavePeriod leavePeriod) {
        requireAllNonNull(employeeId, leavePeriod);
        Employee employee = findEmployeeById(employeeId);
        if (leaves.hasOverlappingLeave(employeeId, leavePeriod)) {
            throw new OverlappingLeaveException();
        }
        int requestedDays = leavePeriod.getWorkingDayCount();
        int usedDays = leaves.getWorkingDaysUsed(employeeId, leavePeriod.getYear());
        int remainingDays = Math.max(0, employee.getLeaveEntitlement().value - usedDays);
        if (requestedDays > remainingDays) {
            throw new InsufficientLeaveException(requestedDays, remainingDays);
        }
        if (nextLeaveId > Integer.MAX_VALUE) {
            throw new LeaveIdExhaustedException();
        }

        Leave leave = new Leave(new LeaveId((int) nextLeaveId), employeeId, leavePeriod);
        leaves.add(leave);
        nextLeaveId++;
        return leave;
    }

    /**
     * Returns true if an employee with {@code employeeId} exists.
     */
    public boolean hasEmployeeWithId(EmployeeId employeeId) {
        requireNonNull(employeeId);
        return employees.asUnmodifiableObservableList().stream()
                .anyMatch(employee -> employee.getEmployeeId().orElseThrow().equals(employeeId));
    }

    private Employee findEmployeeById(EmployeeId employeeId) {
        return employees.asUnmodifiableObservableList().stream()
                .filter(employee -> employee.getEmployeeId().orElseThrow().equals(employeeId))
                .findFirst()
                .orElseThrow(EmployeeNotFoundException::new);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("employees", employees)
                .add("nextEmployeeId", nextEmployeeId)
                .add("leaves", leaves)
                .add("nextLeaveId", nextLeaveId)
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
    public long getNextLeaveId() {
        return nextLeaveId;
    }

    @Override
    public ObservableList<Leave> getLeaveList() {
        return leaves.asUnmodifiableObservableList();
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
                && nextEmployeeId == otherAddressBook.nextEmployeeId
                && leaves.equals(otherAddressBook.leaves)
                && nextLeaveId == otherAddressBook.nextLeaveId;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(employees, nextEmployeeId, leaves, nextLeaveId);
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

    /**
     * Replaces the leave list and returns an ID greater than every leave ID in the replacement.
     */
    private long setLeavesAndGetNextId(List<Leave> leaves, long startingId) {
        requireAllNonNull(leaves);
        if (startingId < 1 || startingId > (long) Integer.MAX_VALUE + 1) {
            throw new IllegalArgumentException("Invalid next leave ID: " + startingId);
        }

        long nextId = startingId;
        UniqueLeaveList validatedLeaves = new UniqueLeaveList();
        for (Leave leave : leaves) {
            if (!hasEmployeeWithId(leave.getEmployeeId())) {
                throw new EmployeeNotFoundException();
            }
            if (validatedLeaves.hasOverlappingLeave(leave.getEmployeeId(), leave.getPeriod())) {
                throw new OverlappingLeaveException();
            }
            validatedLeaves.add(leave);
            nextId = Math.max(nextId, (long) leave.getLeaveId().value + 1);
        }

        this.leaves.setLeaves(validatedLeaves);
        return nextId;
    }

    private void removeOrphanedLeaves() {
        List<Leave> retainedLeaves = leaves.asUnmodifiableObservableList().stream()
                .filter(leave -> hasEmployeeWithId(leave.getEmployeeId()))
                .toList();
        leaves.setLeaves(retainedLeaves);
    }
}
