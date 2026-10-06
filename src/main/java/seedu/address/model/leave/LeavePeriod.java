package seedu.address.model.leave;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents the inclusive date range of a leave record.
 * Guarantees: immutable; dates are ordered, belong to the same calendar year, and contain a working day.
 */
public class LeavePeriod {

    public static final String MESSAGE_START_AFTER_END =
            "The start date must be on or before the end date.";
    public static final String MESSAGE_DIFFERENT_YEARS =
            "The leave period must start and end in the same calendar year.";
    public static final String MESSAGE_NO_WORKING_DAY =
            "The leave period must include at least one working day.";

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MM-uuuu");

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int workingDayCount;

    /**
     * Constructs a {@code LeavePeriod} covering both boundary dates.
     */
    public LeavePeriod(LocalDate startDate, LocalDate endDate) {
        requireNonNull(startDate);
        requireNonNull(endDate);
        checkArgument(!startDate.isAfter(endDate), MESSAGE_START_AFTER_END);
        checkArgument(startDate.getYear() == endDate.getYear(), MESSAGE_DIFFERENT_YEARS);

        int calculatedWorkingDayCount = calculateWorkingDayCount(startDate, endDate);
        checkArgument(calculatedWorkingDayCount > 0, MESSAGE_NO_WORKING_DAY);

        this.startDate = startDate;
        this.endDate = endDate;
        this.workingDayCount = calculatedWorkingDayCount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getYear() {
        return startDate.getYear();
    }

    public int getWorkingDayCount() {
        return workingDayCount;
    }

    /**
     * Returns true if this period and {@code other} contain at least one common date.
     */
    public boolean overlaps(LeavePeriod other) {
        requireNonNull(other);
        return !startDate.isAfter(other.endDate) && !endDate.isBefore(other.startDate);
    }

    /**
     * Returns true if {@code date} falls within this inclusive period.
     */
    public boolean contains(LocalDate date) {
        requireNonNull(date);
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    private static int calculateWorkingDayCount(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate currentDate = startDate;

        while (true) {
            if (isWorkingDay(currentDate)) {
                count++;
            }
            if (currentDate.equals(endDate)) {
                return count;
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    private static boolean isWorkingDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    @Override
    public String toString() {
        return startDate.format(DISPLAY_FORMAT) + " to " + endDate.format(DISPLAY_FORMAT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof LeavePeriod otherLeavePeriod)) {
            return false;
        }

        return startDate.equals(otherLeavePeriod.startDate)
                && endDate.equals(otherLeavePeriod.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
}
