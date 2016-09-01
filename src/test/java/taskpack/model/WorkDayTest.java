package taskpack.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by user on 27.07.2016.
 */
public class WorkDayTest {

    @Before
    public void clearing() {
        TaskList.clearStateForTesting();
        Archive.clearStateForTesting();
        WorkDay.clearStateForTesting();
        TaskRepository.clearStateForTesting();
    }

    @Test
    public void receivingInfoFromTaskList() {
        Task firstTask = new Task("First", 45);

        TaskList.getInstance().createTask(firstTask);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 45);

        Task secondTask = new Task("Second", 60);

        TaskList.getInstance().createTask(secondTask);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 105);

        TaskList.getInstance().startWork();

        assertEquals(TaskList.getInstance().getSize(), TaskList.getInstance().getSize());
        assertEquals(TaskList.getInstance().getCurrentTaskPosition(), 0);
    }

    @Test
    public void completionOfTheFirstTask() {
        Task firstTask = new Task("First", 1);

        TaskList.getInstance().createTask(firstTask);

        Task secondTask = new Task("Second", 60);

        TaskList.getInstance().createTask(secondTask);

        TaskList.getInstance().startWork();

        try {
            Thread.sleep(TaskRepository.getInstance().getDurationOfTaskOnPosition(0) * 60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(TaskList.getInstance().getCurrentTaskPosition(), 2);
    }

    @Test
    public void completionOfTheTaskWithoutDelay() {
        Task firstTask = new Task("First", 1);

        TaskList.getInstance().createTask(firstTask);

        Task secondTask = new Task("Second", 60);

        TaskList.getInstance().createTask(secondTask);

        TaskList.getInstance().startWork();


        assertEquals(TaskList.getInstance().getCurrentTaskPosition(), 0);
    }

    @Test
    public void finishingOfWorkDay() {
        Task firstTask = new Task("First", 1);

        TaskList.getInstance().createTask(firstTask);

        TaskList.getInstance().startWork();

        try {
            Thread.sleep(TaskRepository.getInstance().getDurationOfTaskOnPosition(0) * 60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(TaskList.getInstance().getCurrentTaskPosition(), 1);
        assertEquals(WorkDay.getInstance().getAmountOfUncompletedTasks(), TaskList.getInstance().getSize());
        assertEquals(WorkDay.getInstance().getAmountOfCompletedTasks(), 0);


    }

}