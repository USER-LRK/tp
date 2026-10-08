package seedu.address.ui;

import java.time.Year;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeId;
import seedu.address.model.leave.Leave;

/**
 * Panel containing the list of employees.
 */
public class EmployeeListPanel extends UiPart<Region> {
    private static final String FXML = "EmployeeListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(EmployeeListPanel.class);

    @FXML
    private ListView<Employee> employeeListView;

    /**
     * Creates a {@code EmployeeListPanel} with the given {@code ObservableList}.
     */
    public EmployeeListPanel(ObservableList<Employee> employeeList) {
        super(FXML);
        employeeListView.setItems(employeeList);
        employeeListView.setCellFactory(listView -> new EmployeeListViewCell());
    }

    /**
     * Creates an employee list whose cards refresh when the central leave list changes.
     */
    public EmployeeListPanel(ObservableList<Employee> employeeList, ObservableList<Leave> leaveList,
            BiFunction<EmployeeId, Integer, Integer> remainingLeaveProvider) {
        super(FXML);
        employeeListView.setItems(employeeList);
        employeeListView.setCellFactory(listView -> new EmployeeListViewCell(remainingLeaveProvider));
        leaveList.addListener((ListChangeListener<Leave>) change -> employeeListView.refresh());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Employee} using a {@code EmployeeCard}.
     */
    class EmployeeListViewCell extends ListCell<Employee> {

        private final BiFunction<EmployeeId, Integer, Integer> remainingLeaveProvider;

        EmployeeListViewCell() {
            this(null);
        }

        EmployeeListViewCell(BiFunction<EmployeeId, Integer, Integer> remainingLeaveProvider) {
            this.remainingLeaveProvider = remainingLeaveProvider;
        }

        @Override
        protected void updateItem(Employee employee, boolean empty) {
            super.updateItem(employee, empty);

            if (empty || employee == null) {
                setGraphic(null);
                setText(null);
            } else {
                EmployeeCard card = new EmployeeCard(employee, getIndex() + 1);
                if (remainingLeaveProvider != null) {
                    int currentYear = Year.now().getValue();
                    int remainingDays = remainingLeaveProvider.apply(
                            employee.getEmployeeId().orElseThrow(), currentYear);
                    card.setLeaveRemaining(currentYear, remainingDays);
                }
                setGraphic(card.getRoot());
            }
        }
    }

}
