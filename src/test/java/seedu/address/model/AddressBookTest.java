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

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeIdExhaustedException;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;
import seedu.address.model.leave.exceptions.InsufficientLeaveException;
import seedu.address.model.leave.exceptions.LeaveIdExhaustedException;
import seedu.address.model.leave.exceptions.OverlappingLeaveException;
import seedu.address.testutil.EmployeeBuilder;

public class AddressBookTest {

    private static final LeavePeriod MONDAY_TO_WEDNESDAY = new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));
    private static final LeavePeriod THURSDAY_TO_FRIDAY = new LeavePeriod(
            LocalDate.of(2026, 10, 8), LocalDate.of(2026, 10, 9));

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getEmployeeList());
        assertEquals(1, addressBook.getNextEmployeeId());
        assertEquals(List.of(), addressBook.getLeaveList());
        assertEquals(1, addressBook.getNextLeaveId());
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
    public void addLeave_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.addLeave(null, MONDAY_TO_WEDNESDAY));
        assertThrows(NullPointerException.class, () -> addressBook.addLeave(new EmployeeId("1"), null));
    }

    @Test
    public void addLeave_unknownEmployee_throwsEmployeeNotFoundException() {
        assertThrows(EmployeeNotFoundException.class, () ->
                addressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY));
        assertEquals(1, addressBook.getNextLeaveId());
    }

    @Test
    public void addLeave_validPeriod_assignsNextId() {
        addressBook.addEmployee(ALICE);

        Leave addedLeave = addressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY);

        assertEquals(new LeaveId(1), addedLeave.getLeaveId());
        assertEquals(List.of(addedLeave), addressBook.getLeaveList());
        assertEquals(2, addressBook.getNextLeaveId());
    }

    @Test
    public void addLeave_secondNonOverlappingPeriod_assignsFollowingId() {
        addressBook.addEmployee(ALICE);
        addressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY);

        Leave secondLeave = addressBook.addLeave(new EmployeeId("1"), THURSDAY_TO_FRIDAY);

        assertEquals(new LeaveId(2), secondLeave.getLeaveId());
    }

    @Test
    public void addLeave_overlappingPeriod_throwsOverlappingLeaveException() {
        addressBook.addEmployee(ALICE);
        addressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY);
        LeavePeriod overlappingPeriod = new LeavePeriod(
                LocalDate.of(2026, 10, 7), LocalDate.of(2026, 10, 8));

        assertThrows(OverlappingLeaveException.class, () ->
                addressBook.addLeave(new EmployeeId("1"), overlappingPeriod));
        assertEquals(2, addressBook.getNextLeaveId());
    }

    @Test
    public void addLeave_samePeriodForDifferentEmployees_success() {
        AddressBook populatedAddressBook = getTypicalAddressBook();

        Leave aliceLeave = populatedAddressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY);
        Leave bensonLeave = populatedAddressBook.addLeave(new EmployeeId("2"), MONDAY_TO_WEDNESDAY);

        assertEquals(new LeaveId(1), aliceLeave.getLeaveId());
        assertEquals(new LeaveId(2), bensonLeave.getLeaveId());
    }

    @Test
    public void addLeave_requestExceedsEntitlement_throwsInsufficientLeaveException() {
        Employee employee = new EmployeeBuilder().withLeaveEntitlement(2).build();
        Employee addedEmployee = addressBook.addEmployee(employee);

        InsufficientLeaveException exception = org.junit.jupiter.api.Assertions.assertThrows(
                InsufficientLeaveException.class, () ->
                addressBook.addLeave(addedEmployee.getEmployeeId().orElseThrow(), MONDAY_TO_WEDNESDAY));

        assertEquals(3, exception.getRequestedDays());
        assertEquals(2, exception.getRemainingDays());
        assertTrue(addressBook.getLeaveList().isEmpty());
        assertEquals(1, addressBook.getNextLeaveId());
    }

    @Test
    public void addLeave_cumulativeRequestsExceedEntitlement_throwsInsufficientLeaveException() {
        Employee employee = new EmployeeBuilder().withLeaveEntitlement(4).build();
        Employee addedEmployee = addressBook.addEmployee(employee);
        EmployeeId employeeId = addedEmployee.getEmployeeId().orElseThrow();
        addressBook.addLeave(employeeId, MONDAY_TO_WEDNESDAY);

        InsufficientLeaveException exception = org.junit.jupiter.api.Assertions.assertThrows(
                InsufficientLeaveException.class, () ->
                addressBook.addLeave(employeeId, THURSDAY_TO_FRIDAY));

        assertEquals(2, exception.getRequestedDays());
        assertEquals(1, exception.getRemainingDays());
        assertEquals(1, addressBook.getLeaveList().size());
    }

    @Test
    public void addLeave_newCalendarYear_usesNewAnnualEntitlement() {
        Employee employee = new EmployeeBuilder().withLeaveEntitlement(3).build();
        Employee addedEmployee = addressBook.addEmployee(employee);
        EmployeeId employeeId = addedEmployee.getEmployeeId().orElseThrow();
        addressBook.addLeave(employeeId, MONDAY_TO_WEDNESDAY);
        LeavePeriod followingYear = new LeavePeriod(
                LocalDate.of(2027, 10, 4), LocalDate.of(2027, 10, 6));

        Leave addedLeave = addressBook.addLeave(employeeId, followingYear);

        assertEquals(new LeaveId(2), addedLeave.getLeaveId());
    }

    @Test
    public void addLeave_noAvailableId_throwsLeaveIdExhaustedException() {
        Leave leaveWithMaximumId = new Leave(new LeaveId(Integer.MAX_VALUE), new EmployeeId("1"),
                MONDAY_TO_WEDNESDAY);
        AddressBook addressBookAtLimit = new AddressBook(List.of(ALICE), 2,
                List.of(leaveWithMaximumId), (long) Integer.MAX_VALUE + 1);

        assertThrows(LeaveIdExhaustedException.class, () ->
                addressBookAtLimit.addLeave(new EmployeeId("1"), THURSDAY_TO_FRIDAY));
    }

    @Test
    public void removeEmployee_employeeHasLeave_removesAssociatedLeaveWithoutReusingId() {
        addressBook.addEmployee(ALICE);
        addressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY);

        addressBook.removeEmployee(ALICE);

        assertTrue(addressBook.getLeaveList().isEmpty());
        assertEquals(2, addressBook.getNextLeaveId());
    }

    @Test
    public void setEmployees_employeeRemoved_removesOrphanedLeave() {
        addressBook.addEmployee(ALICE);
        addressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY);

        addressBook.setEmployees(List.of());

        assertTrue(addressBook.getLeaveList().isEmpty());
        assertEquals(2, addressBook.getNextLeaveId());
    }

    @Test
    public void setLeaves_unknownEmployee_throwsEmployeeNotFoundException() {
        Leave leave = new Leave(new LeaveId(1), new EmployeeId("1"), MONDAY_TO_WEDNESDAY);

        assertThrows(EmployeeNotFoundException.class, () -> addressBook.setLeaves(List.of(leave)));
    }

    @Test
    public void setLeaves_validLeave_replacesListAndAdvancesNextId() {
        addressBook.addEmployee(ALICE);
        Leave leave = new Leave(new LeaveId(5), new EmployeeId("1"), MONDAY_TO_WEDNESDAY);

        addressBook.setLeaves(List.of(leave));

        assertEquals(List.of(leave), addressBook.getLeaveList());
        assertEquals(6, addressBook.getNextLeaveId());
    }

    @Test
    public void setLeaves_overlappingPeriods_throwsOverlappingLeaveException() {
        addressBook.addEmployee(ALICE);
        Leave firstLeave = new Leave(new LeaveId(1), new EmployeeId("1"), MONDAY_TO_WEDNESDAY);
        Leave overlappingLeave = new Leave(new LeaveId(2), new EmployeeId("1"), new LeavePeriod(
                LocalDate.of(2026, 10, 7), LocalDate.of(2026, 10, 8)));

        assertThrows(OverlappingLeaveException.class, () ->
                addressBook.setLeaves(List.of(firstLeave, overlappingLeave)));
        assertTrue(addressBook.getLeaveList().isEmpty());
    }

    @Test
    public void constructor_invalidNextLeaveId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new AddressBook(List.of(), 1, List.of(), 0));
    }

    @Test
    public void copyConstructor_preservesLeavesAndNextLeaveId() {
        addressBook.addEmployee(ALICE);
        Leave leave = addressBook.addLeave(new EmployeeId("1"), MONDAY_TO_WEDNESDAY);

        AddressBook copy = new AddressBook(addressBook);

        assertEquals(List.of(leave), copy.getLeaveList());
        assertEquals(2, copy.getNextLeaveId());
    }

    @Test
    public void hasEmployeeWithId() {
        addressBook.addEmployee(ALICE);

        assertTrue(addressBook.hasEmployeeWithId(new EmployeeId("1")));
        assertFalse(addressBook.hasEmployeeWithId(new EmployeeId("2")));
        assertThrows(NullPointerException.class, () -> addressBook.hasEmployeeWithId(null));
    }

    @Test
    public void getEmployeeList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getEmployeeList().remove(0));
    }

    @Test
    public void getLeaveList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getLeaveList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{employees=" + addressBook.getEmployeeList()
                + ", nextEmployeeId=1, leaves=" + addressBook.getLeaveList() + ", nextLeaveId=1}";
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
        public long getNextLeaveId() {
            return 1;
        }

        @Override
        public ObservableList<Employee> getEmployeeList() {
            return employees;
        }

        @Override
        public ObservableList<Leave> getLeaveList() {
            return FXCollections.emptyObservableList();
        }
    }

}
