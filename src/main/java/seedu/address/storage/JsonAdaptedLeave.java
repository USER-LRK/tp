package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;
import seedu.address.model.leave.LeaveId;
import seedu.address.model.leave.LeavePeriod;

/**
 * Jackson-friendly version of {@link Leave}.
 */
class JsonAdaptedLeave {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Leave's %s field is missing!";
    public static final String MESSAGE_INVALID_DATE = "Leave dates must use the ISO yyyy-MM-dd format.";

    private final Integer leaveId;
    private final String employeeId;
    private final String startDate;
    private final String endDate;

    /**
     * Constructs a {@code JsonAdaptedLeave} with the given leave details.
     */
    @JsonCreator
    public JsonAdaptedLeave(@JsonProperty("leaveId") Integer leaveId,
            @JsonProperty("employeeId") String employeeId,
            @JsonProperty("startDate") String startDate,
            @JsonProperty("endDate") String endDate) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Converts a given {@code Leave} into this class for Jackson use.
     */
    public JsonAdaptedLeave(Leave source) {
        leaveId = source.getLeaveId().value;
        employeeId = source.getEmployeeId().value;
        startDate = source.getPeriod().getStartDate().toString();
        endDate = source.getPeriod().getEndDate().toString();
    }

    /**
     * Converts this Jackson-friendly adapted leave object into the model's {@code Leave} object.
     */
    public Leave toModelType() throws IllegalValueException {
        if (leaveId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    LeaveId.class.getSimpleName()));
        }
        if (!LeaveId.isValidLeaveId(leaveId)) {
            throw new IllegalValueException(LeaveId.MESSAGE_CONSTRAINTS);
        }
        if (employeeId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT,
                    EmployeeId.class.getSimpleName()));
        }
        if (!EmployeeId.isValidEmployeeId(employeeId)) {
            throw new IllegalValueException(EmployeeId.MESSAGE_CONSTRAINTS);
        }
        if (startDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "start date"));
        }
        if (endDate == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "end date"));
        }

        try {
            LeavePeriod period = new LeavePeriod(LocalDate.parse(startDate), LocalDate.parse(endDate));
            return new Leave(new LeaveId(leaveId), new EmployeeId(employeeId), period);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException(MESSAGE_INVALID_DATE);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }
}
