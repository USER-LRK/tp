package seedu.address.model.leave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.exceptions.DuplicateLeaveException;
import seedu.address.model.leave.exceptions.LeaveNotFoundException;

public class UniqueLeaveListTest {

    private static final EmployeeId EMPLOYEE_1024 = new EmployeeId("1024");
    private static final EmployeeId EMPLOYEE_2048 = new EmployeeId("2048");
    private static final Leave LEAVE_ONE = new Leave(new LeaveId(1), EMPLOYEE_1024, new LeavePeriod(
            LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));
    private static final Leave LEAVE_TWO = new Leave(new LeaveId(2), EMPLOYEE_1024, new LeavePeriod(
            LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)));
    private static final Leave OTHER_EMPLOYEE_LEAVE = new Leave(new LeaveId(3), EMPLOYEE_2048, new LeavePeriod(
            LocalDate.of(2026, 10, 6), LocalDate.of(2026, 10, 8)));
    private static final Leave SAME_ID_AS_LEAVE_ONE = new Leave(new LeaveId(1), EMPLOYEE_2048, new LeavePeriod(
            LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 2)));

    private final UniqueLeaveList uniqueLeaveList = new UniqueLeaveList();

    @Test
    public void contains() {
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.contains(null));
        assertFalse(uniqueLeaveList.contains(LEAVE_ONE));

        uniqueLeaveList.add(LEAVE_ONE);

        assertTrue(uniqueLeaveList.contains(LEAVE_ONE));
        assertTrue(uniqueLeaveList.contains(SAME_ID_AS_LEAVE_ONE));
    }

    @Test
    public void findById() {
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.findById(null));
        assertTrue(uniqueLeaveList.findById(LEAVE_ONE.getLeaveId()).isEmpty());

        uniqueLeaveList.add(LEAVE_ONE);

        assertEquals(LEAVE_ONE, uniqueLeaveList.findById(new LeaveId(1)).orElseThrow());
    }

    @Test
    public void employeeQueries() {
        uniqueLeaveList.setLeaves(List.of(LEAVE_ONE, LEAVE_TWO, OTHER_EMPLOYEE_LEAVE));

        assertEquals(List.of(LEAVE_ONE, LEAVE_TWO), uniqueLeaveList.getLeavesFor(EMPLOYEE_1024));
        assertEquals(List.of(OTHER_EMPLOYEE_LEAVE), uniqueLeaveList.getLeavesFor(EMPLOYEE_2048));
        assertThrows(UnsupportedOperationException.class, () ->
                uniqueLeaveList.getLeavesFor(EMPLOYEE_1024).remove(0));

        LeavePeriod overlappingPeriod = new LeavePeriod(
                LocalDate.of(2026, 10, 7), LocalDate.of(2026, 10, 9));
        assertTrue(uniqueLeaveList.hasOverlappingLeave(EMPLOYEE_1024, overlappingPeriod));
        assertFalse(uniqueLeaveList.hasOverlappingLeave(new EmployeeId("3072"), overlappingPeriod));

        assertEquals(5, uniqueLeaveList.getWorkingDaysUsed(EMPLOYEE_1024, 2026));
        assertEquals(0, uniqueLeaveList.getWorkingDaysUsed(EMPLOYEE_1024, 2025));
        assertEquals(Set.of(EMPLOYEE_1024, EMPLOYEE_2048),
                uniqueLeaveList.getEmployeeIdsOnLeave(LocalDate.of(2026, 10, 6)));
    }

    @Test
    public void employeeQueries_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.getLeavesFor(null));
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.hasOverlappingLeave(null,
                LEAVE_ONE.getPeriod()));
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.hasOverlappingLeave(EMPLOYEE_1024, null));
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.getWorkingDaysUsed(null, 2026));
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.getEmployeeIdsOnLeave(null));
    }

    @Test
    public void add() {
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.add(null));

        uniqueLeaveList.add(LEAVE_ONE);

        assertThrows(DuplicateLeaveException.class, () -> uniqueLeaveList.add(SAME_ID_AS_LEAVE_ONE));
        assertEquals(List.of(LEAVE_ONE), uniqueLeaveList.asUnmodifiableObservableList());
    }

    @Test
    public void remove() {
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.remove(null));
        assertThrows(LeaveNotFoundException.class, () -> uniqueLeaveList.remove(LEAVE_ONE));

        uniqueLeaveList.add(LEAVE_ONE);
        uniqueLeaveList.remove(LEAVE_ONE);

        assertTrue(uniqueLeaveList.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void removeAllFor_employeeWithLeaves_removesOnlyTheirLeaves() {
        uniqueLeaveList.setLeaves(List.of(LEAVE_ONE, LEAVE_TWO, OTHER_EMPLOYEE_LEAVE));

        uniqueLeaveList.removeAllFor(EMPLOYEE_1024);

        assertEquals(List.of(OTHER_EMPLOYEE_LEAVE), uniqueLeaveList.asUnmodifiableObservableList());
    }

    @Test
    public void removeAllFor_nullEmployeeId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.removeAllFor(null));
    }

    @Test
    public void setLeaves_uniqueLeaveList_replacesContents() {
        uniqueLeaveList.add(LEAVE_ONE);
        UniqueLeaveList replacement = new UniqueLeaveList();
        replacement.add(LEAVE_TWO);

        uniqueLeaveList.setLeaves(replacement);

        assertEquals(replacement, uniqueLeaveList);
    }

    @Test
    public void setLeaves_nullUniqueLeaveList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.setLeaves((UniqueLeaveList) null));
    }

    @Test
    public void setLeaves_list_replacesContents() {
        uniqueLeaveList.add(LEAVE_ONE);

        uniqueLeaveList.setLeaves(List.of(LEAVE_TWO));

        assertEquals(List.of(LEAVE_TWO), uniqueLeaveList.asUnmodifiableObservableList());
    }

    @Test
    public void setLeaves_invalidList_doesNotChangeContents() {
        uniqueLeaveList.add(LEAVE_TWO);

        assertThrows(NullPointerException.class, () -> uniqueLeaveList.setLeaves((List<Leave>) null));
        assertThrows(NullPointerException.class, () -> uniqueLeaveList.setLeaves(Arrays.asList(LEAVE_ONE, null)));
        assertThrows(DuplicateLeaveException.class, () ->
                uniqueLeaveList.setLeaves(List.of(LEAVE_ONE, SAME_ID_AS_LEAVE_ONE)));
        assertEquals(List.of(LEAVE_TWO), uniqueLeaveList.asUnmodifiableObservableList());
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        uniqueLeaveList.add(LEAVE_ONE);
        assertThrows(UnsupportedOperationException.class, () ->
                uniqueLeaveList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        uniqueLeaveList.add(LEAVE_ONE);
        assertEquals(uniqueLeaveList.asUnmodifiableObservableList().toString(), uniqueLeaveList.toString());
    }
}
