package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeIdExhaustedException;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.exceptions.DuplicateLeaveException;
import seedu.address.model.leave.exceptions.OverlappingLeaveException;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_EMPLOYEE = "Employees list contains duplicate employee(s).";
    public static final String MESSAGE_INVALID_NEXT_EMPLOYEE_ID = "Address book contains an invalid next employee ID.";
    public static final String MESSAGE_DUPLICATE_LEAVE = "Leave list contains duplicate leave record(s).";
    public static final String MESSAGE_ORPHANED_LEAVE = "Leave list contains a record for an unknown employee.";
    public static final String MESSAGE_OVERLAPPING_LEAVE = "Leave list contains overlapping records for an employee.";
    public static final String MESSAGE_INVALID_NEXT_LEAVE_ID = "Address book contains an invalid next leave ID.";

    // Retain the existing JSON key so saved address books remain compatible.
    @JsonProperty("persons")
    private final List<JsonAdaptedEmployee> employees = new ArrayList<>();
    @JsonProperty("nextEmployeeId")
    private final Integer nextEmployeeId;
    @JsonProperty("leaves")
    private final List<JsonAdaptedLeave> leaves = new ArrayList<>();
    @JsonProperty("nextLeaveId")
    private final Long nextLeaveId;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given employees.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedEmployee> employees,
            @JsonProperty("nextEmployeeId") Integer nextEmployeeId,
            @JsonProperty("leaves") List<JsonAdaptedLeave> leaves,
            @JsonProperty("nextLeaveId") Long nextLeaveId) {
        this.employees.addAll(employees);
        this.nextEmployeeId = nextEmployeeId;
        if (leaves != null) {
            this.leaves.addAll(leaves);
        }
        this.nextLeaveId = nextLeaveId;
    }

    /**
     * Constructs a legacy-compatible address book containing only employees.
     */
    public JsonSerializableAddressBook(List<JsonAdaptedEmployee> employees, Integer nextEmployeeId) {
        this(employees, nextEmployeeId, List.of(), null);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        employees.addAll(source.getEmployeeList().stream().map(JsonAdaptedEmployee::new).collect(Collectors.toList()));
        nextEmployeeId = source.getNextEmployeeId();
        leaves.addAll(source.getLeaveList().stream().map(JsonAdaptedLeave::new).collect(Collectors.toList()));
        nextLeaveId = source.getNextLeaveId();
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        List<Employee> modelEmployees = new ArrayList<>();
        for (JsonAdaptedEmployee jsonAdaptedEmployee : employees) {
            modelEmployees.add(jsonAdaptedEmployee.toModelType());
        }
        List<Leave> modelLeaves = new ArrayList<>();
        for (JsonAdaptedLeave jsonAdaptedLeave : leaves) {
            modelLeaves.add(jsonAdaptedLeave.toModelType());
        }

        int storedNextEmployeeId = nextEmployeeId == null ? 1 : nextEmployeeId;
        long storedNextLeaveId = nextLeaveId == null ? 1 : nextLeaveId;
        if (storedNextEmployeeId < 1 || storedNextEmployeeId > EmployeeId.MAX_VALUE + 1) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_EMPLOYEE_ID);
        }
        if (storedNextLeaveId < 1 || storedNextLeaveId > (long) Integer.MAX_VALUE + 1) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_LEAVE_ID);
        }
        try {
            return new AddressBook(modelEmployees, storedNextEmployeeId, modelLeaves, storedNextLeaveId);
        } catch (DuplicateEmployeeException e) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_EMPLOYEE);
        } catch (EmployeeIdExhaustedException e) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_EMPLOYEE_ID);
        } catch (DuplicateLeaveException e) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_LEAVE);
        } catch (EmployeeNotFoundException e) {
            throw new IllegalValueException(MESSAGE_ORPHANED_LEAVE);
        } catch (OverlappingLeaveException e) {
            throw new IllegalValueException(MESSAGE_OVERLAPPING_LEAVE);
        }
    }

}
