package seedu.address.model.leave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class LeavePeriodTest {

    private static final LocalDate MONDAY = LocalDate.of(2026, 10, 5);
    private static final LocalDate TUESDAY = LocalDate.of(2026, 10, 6);
    private static final LocalDate WEDNESDAY = LocalDate.of(2026, 10, 7);
    private static final LocalDate FRIDAY = LocalDate.of(2026, 10, 9);
    private static final LocalDate SATURDAY = LocalDate.of(2026, 10, 10);
    private static final LocalDate SUNDAY = LocalDate.of(2026, 10, 11);
    private static final LocalDate NEXT_MONDAY = LocalDate.of(2026, 10, 12);

    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new LeavePeriod(null, MONDAY));
        assertThrows(NullPointerException.class, () -> new LeavePeriod(MONDAY, null));
    }

    @Test
    public void constructor_startAfterEnd_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, LeavePeriod.MESSAGE_START_AFTER_END, () ->
                new LeavePeriod(TUESDAY, MONDAY));
    }

    @Test
    public void constructor_datesInDifferentYears_throwsIllegalArgumentException() {
        LocalDate endOfYear = LocalDate.of(2026, 12, 31);
        LocalDate startOfNextYear = LocalDate.of(2027, 1, 1);
        assertThrows(IllegalArgumentException.class, LeavePeriod.MESSAGE_DIFFERENT_YEARS, () ->
                new LeavePeriod(endOfYear, startOfNextYear));
    }

    @Test
    public void constructor_weekendOnly_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, LeavePeriod.MESSAGE_NO_WORKING_DAY, () ->
                new LeavePeriod(SATURDAY, SUNDAY));
    }

    @Test
    public void getWorkingDayCount_countsWeekdaysInclusively() {
        assertEquals(1, new LeavePeriod(MONDAY, MONDAY).getWorkingDayCount());
        assertEquals(3, new LeavePeriod(MONDAY, WEDNESDAY).getWorkingDayCount());
        assertEquals(2, new LeavePeriod(FRIDAY, NEXT_MONDAY).getWorkingDayCount());
    }

    @Test
    public void getWorkingDayCount_publicHolidayWeekday_countedAsWorkingDay() {
        LocalDate newYearsDay = LocalDate.of(2026, 1, 1);
        assertEquals(1, new LeavePeriod(newYearsDay, newYearsDay).getWorkingDayCount());
    }

    @Test
    public void getWorkingDayCount_leapDayRange_countedCorrectly() {
        LocalDate leapYearStart = LocalDate.of(2024, 2, 28);
        LocalDate leapYearEnd = LocalDate.of(2024, 3, 1);
        assertEquals(3, new LeavePeriod(leapYearStart, leapYearEnd).getWorkingDayCount());
    }

    @Test
    public void overlaps() {
        LeavePeriod mondayToWednesday = new LeavePeriod(MONDAY, WEDNESDAY);

        assertTrue(mondayToWednesday.overlaps(new LeavePeriod(MONDAY, WEDNESDAY)));
        assertTrue(mondayToWednesday.overlaps(new LeavePeriod(WEDNESDAY, FRIDAY)));
        assertFalse(mondayToWednesday.overlaps(new LeavePeriod(FRIDAY, NEXT_MONDAY)));
        assertThrows(NullPointerException.class, () -> mondayToWednesday.overlaps(null));
    }

    @Test
    public void contains() {
        LeavePeriod leavePeriod = new LeavePeriod(MONDAY, WEDNESDAY);

        assertTrue(leavePeriod.contains(MONDAY));
        assertTrue(leavePeriod.contains(TUESDAY));
        assertTrue(leavePeriod.contains(WEDNESDAY));
        assertFalse(leavePeriod.contains(FRIDAY));
        assertThrows(NullPointerException.class, () -> leavePeriod.contains(null));
    }

    @Test
    public void accessors_returnConstructedValues() {
        LeavePeriod leavePeriod = new LeavePeriod(MONDAY, WEDNESDAY);

        assertEquals(MONDAY, leavePeriod.getStartDate());
        assertEquals(WEDNESDAY, leavePeriod.getEndDate());
        assertEquals(2026, leavePeriod.getYear());
    }

    @Test
    public void equals() {
        LeavePeriod leavePeriod = new LeavePeriod(MONDAY, WEDNESDAY);

        assertTrue(leavePeriod.equals(leavePeriod));
        assertTrue(leavePeriod.equals(new LeavePeriod(MONDAY, WEDNESDAY)));
        assertFalse(leavePeriod.equals(new LeavePeriod(TUESDAY, WEDNESDAY)));
        assertFalse(leavePeriod.equals(null));
        assertFalse(leavePeriod.equals("05-10-2026 to 07-10-2026"));
    }

    @Test
    public void hashCode_sameDates_sameHashCode() {
        assertEquals(new LeavePeriod(MONDAY, WEDNESDAY).hashCode(),
                new LeavePeriod(MONDAY, WEDNESDAY).hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("05-10-2026 to 07-10-2026", new LeavePeriod(MONDAY, WEDNESDAY).toString());
    }
}
