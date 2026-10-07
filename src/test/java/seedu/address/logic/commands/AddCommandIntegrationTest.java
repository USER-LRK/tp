package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalEmployees.getTypicalAddressBook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EmployeeBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_newEmployee_success() {
        Employee validEmployee = new EmployeeBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Employee expectedEmployee = expectedModel.addEmployee(validEmployee);

        assertCommandSuccess(new AddCommand(validEmployee), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedEmployee)),
                expectedModel);
    }

    @Test
    public void execute_employeeWithDuplicateName_success() {
        Employee employeeInList = model.getAddressBook().getEmployeeList().get(0);
        Employee employeeWithDuplicateName = new EmployeeBuilder()
                .withName(employeeInList.getName().fullName).build();

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Employee expectedEmployee = expectedModel.addEmployee(employeeWithDuplicateName);

        assertCommandSuccess(new AddCommand(employeeWithDuplicateName), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedEmployee)), expectedModel);
    }

}
