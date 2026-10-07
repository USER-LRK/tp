package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.storage.JsonAdaptedLeave.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;

public class JsonAdaptedLeaveTest {

    private static final Leave VALID_LEAVE = new Leave(new LeaveId(1), new EmployeeId("2"),
            new LeavePeriod(LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7)));

    @Test
    public void toModelType_validLeaveDetails_returnsLeave() throws Exception {
        assertEquals(VALID_LEAVE, new JsonAdaptedLeave(VALID_LEAVE).toModelType());
    }

    @Test
    public void toModelType_nullLeaveId_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(null, "2", "2026-10-05", "2026-10-07");
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, LeaveId.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, leave::toModelType);
    }

    @Test
    public void toModelType_invalidLeaveId_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(0, "2", "2026-10-05", "2026-10-07");
        assertThrows(IllegalValueException.class, LeaveId.MESSAGE_CONSTRAINTS, leave::toModelType);
    }

    @Test
    public void toModelType_nullEmployeeId_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, null, "2026-10-05", "2026-10-07");
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, EmployeeId.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, leave::toModelType);
    }

    @Test
    public void toModelType_invalidEmployeeId_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, "02", "2026-10-05", "2026-10-07");
        assertThrows(IllegalValueException.class, EmployeeId.MESSAGE_CONSTRAINTS, leave::toModelType);
    }

    @Test
    public void toModelType_nullStartDate_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, "2", null, "2026-10-07");
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "start date");
        assertThrows(IllegalValueException.class, expectedMessage, leave::toModelType);
    }

    @Test
    public void toModelType_nullEndDate_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, "2", "2026-10-05", null);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, "end date");
        assertThrows(IllegalValueException.class, expectedMessage, leave::toModelType);
    }

    @Test
    public void toModelType_invalidDateFormat_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, "2", "05-10-2026", "2026-10-07");
        assertThrows(IllegalValueException.class, JsonAdaptedLeave.MESSAGE_INVALID_DATE, leave::toModelType);
    }

    @Test
    public void toModelType_invalidPeriod_throwsIllegalValueException() {
        JsonAdaptedLeave leave = new JsonAdaptedLeave(1, "2", "2026-10-07", "2026-10-05");
        assertThrows(IllegalValueException.class, LeavePeriod.MESSAGE_START_AFTER_END, leave::toModelType);
    }
}
