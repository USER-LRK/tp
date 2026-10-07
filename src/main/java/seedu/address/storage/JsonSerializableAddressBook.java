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
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeIdExhaustedException;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_EMPLOYEE = "Employees list contains duplicate employee(s).";
    public static final String MESSAGE_INVALID_NEXT_EMPLOYEE_ID = "Address book contains an invalid next employee ID.";

    // Retain the existing JSON key so saved address books remain compatible.
    @JsonProperty("persons")
    private final List<JsonAdaptedEmployee> employees = new ArrayList<>();
    @JsonProperty("nextEmployeeId")
    private final Integer nextEmployeeId;

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given employees.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedEmployee> employees,
            @JsonProperty("nextEmployeeId") Integer nextEmployeeId) {
        this.employees.addAll(employees);
        this.nextEmployeeId = nextEmployeeId;
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        employees.addAll(source.getEmployeeList().stream().map(JsonAdaptedEmployee::new).collect(Collectors.toList()));
        nextEmployeeId = source.getNextEmployeeId();
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

        int storedNextEmployeeId = nextEmployeeId == null ? 1 : nextEmployeeId;
        try {
            return new AddressBook(modelEmployees, storedNextEmployeeId);
        } catch (DuplicateEmployeeException e) {
            throw new IllegalValueException(MESSAGE_DUPLICATE_EMPLOYEE);
        } catch (IllegalArgumentException | EmployeeIdExhaustedException e) {
            throw new IllegalValueException(MESSAGE_INVALID_NEXT_EMPLOYEE_ID);
        }
    }

}
