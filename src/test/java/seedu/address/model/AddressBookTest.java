package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeIdExhaustedException;
import seedu.address.testutil.EmployeeBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getEmployeeList());
        assertEquals(1, addressBook.getNextEmployeeId());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicateEmployees_throwsDuplicateEmployeeException() {
        // Two employees with the same employee ID
        Employee editedAlice = new EmployeeBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Employee> newEmployees = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newEmployees);

        assertThrows(DuplicateEmployeeException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void setEmployees_legacyEmployees_assignsIds() {
        List<Employee> legacyEmployees = List.of(
                new EmployeeBuilder(ALICE).withoutEmployeeId().build(),
                new EmployeeBuilder().withoutEmployeeId().build());

        addressBook.setEmployees(legacyEmployees);

        assertEquals(new EmployeeId("1"), addressBook.getEmployeeList().get(0).getEmployeeId().orElseThrow());
        assertEquals(new EmployeeId("2"), addressBook.getEmployeeList().get(1).getEmployeeId().orElseThrow());
        assertEquals(3, addressBook.getNextEmployeeId());
    }

    @Test
    public void hasEmployee_nullEmployee_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasEmployee(null));
    }

    @Test
    public void hasEmployee_employeeNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasEmployee(ALICE));
    }

    @Test
    public void hasEmployee_employeeInAddressBook_returnsTrue() {
        addressBook.addEmployee(ALICE);
        assertTrue(addressBook.hasEmployee(ALICE));
    }

    @Test
    public void hasEmployee_employeeWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addEmployee(ALICE);
        Employee editedAlice = new EmployeeBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasEmployee(editedAlice));
    }

    @Test
    public void addEmployee_withoutId_assignsNextId() {
        Employee employee = new EmployeeBuilder().build();

        Employee addedEmployee = addressBook.addEmployee(employee);

        assertEquals(new EmployeeId("1"), addedEmployee.getEmployeeId().orElseThrow());
        assertEquals(2, addressBook.getNextEmployeeId());
    }

    @Test
    public void addEmployee_afterDeletion_doesNotReuseId() {
        Employee firstEmployee = addressBook.addEmployee(new EmployeeBuilder().build());
        addressBook.removeEmployee(firstEmployee);

        Employee secondEmployee = addressBook.addEmployee(new EmployeeBuilder().build());

        assertEquals(new EmployeeId("2"), secondEmployee.getEmployeeId().orElseThrow());
    }

    @Test
    public void addEmployee_previouslyUsedExplicitId_throwsDuplicateEmployeeException() {
        addressBook.addEmployee(ALICE);
        addressBook.removeEmployee(ALICE);

        assertThrows(DuplicateEmployeeException.class, () -> addressBook.addEmployee(ALICE));
    }

    @Test
    public void addEmployee_duplicateName_success() {
        addressBook.addEmployee(ALICE);
        Employee employeeWithDuplicateName = new EmployeeBuilder().withName(ALICE.getName().fullName).build();

        Employee addedEmployee = addressBook.addEmployee(employeeWithDuplicateName);

        assertEquals(ALICE.getName(), addedEmployee.getName());
        assertFalse(ALICE.isSameEmployee(addedEmployee));
    }

    @Test
    public void addEmployee_noAvailableId_throwsEmployeeIdExhaustedException() {
        Employee employeeWithMaximumId = new EmployeeBuilder().withEmployeeId(String.valueOf(EmployeeId.MAX_VALUE))
                .build();
        addressBook.addEmployee(employeeWithMaximumId);

        assertThrows(EmployeeIdExhaustedException.class, () ->
                addressBook.addEmployee(new EmployeeBuilder().build()));
    }

    @Test
    public void setEmployee_differentEmployeeId_throwsIllegalArgumentException() {
        addressBook.addEmployee(ALICE);
        Employee employeeWithDifferentId = new EmployeeBuilder(ALICE).withEmployeeId("2").build();

        assertThrows(IllegalArgumentException.class, () -> addressBook.setEmployee(ALICE, employeeWithDifferentId));
    }

    @Test
    public void setEmployee_editedEmployeeWithoutId_preservesId() {
        addressBook.addEmployee(ALICE);
        Employee editedEmployee = new EmployeeBuilder(ALICE).withoutEmployeeId().withAddress(VALID_ADDRESS_BOB)
                .build();

        addressBook.setEmployee(ALICE, editedEmployee);

        assertEquals(ALICE.getEmployeeId(), addressBook.getEmployeeList().get(0).getEmployeeId());
    }

    @Test
    public void constructor_invalidNextEmployeeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new AddressBook(List.of(), 0));
    }

    @Test
    public void constructor_legacyEmployeeAfterMaximumId_throwsEmployeeIdExhaustedException() {
        Employee employeeWithMaximumId = new EmployeeBuilder().withEmployeeId(String.valueOf(EmployeeId.MAX_VALUE))
                .build();
        Employee legacyEmployee = new EmployeeBuilder().build();

        assertThrows(EmployeeIdExhaustedException.class, () ->
                new AddressBook(List.of(employeeWithMaximumId, legacyEmployee), 1));
    }

    @Test
    public void copyConstructor_preservesNextEmployeeId() {
        AddressBook source = new AddressBook();
        Employee employee = source.addEmployee(new EmployeeBuilder().build());
        source.removeEmployee(employee);

        AddressBook copy = new AddressBook(source);
        Employee addedEmployee = copy.addEmployee(new EmployeeBuilder().build());

        assertEquals(new EmployeeId("2"), addedEmployee.getEmployeeId().orElseThrow());
    }

    @Test
    public void getEmployeeList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getEmployeeList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{employees=" + addressBook.getEmployeeList()
                + ", nextEmployeeId=1}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void equalsAndHashCode() {
        AddressBook addressBookCopy = new AddressBook(addressBook);

        assertTrue(addressBook.equals(addressBook));
        assertEquals(addressBook, addressBookCopy);
        assertEquals(addressBook.hashCode(), addressBookCopy.hashCode());
        assertNotEquals(addressBook, null);
        assertNotEquals(addressBook, "address book");
    }

    /**
     * A stub ReadOnlyAddressBook whose employees list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Employee> employees = FXCollections.observableArrayList();

        AddressBookStub(Collection<Employee> employees) {
            this.employees.setAll(employees);
        }

        @Override
        public int getNextEmployeeId() {
            return 1;
        }

        @Override
        public ObservableList<Employee> getEmployeeList() {
            return employees;
        }
    }

}
