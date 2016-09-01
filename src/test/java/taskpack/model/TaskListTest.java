package taskpack.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by Anton on 25.07.2016.
 */
public class TaskListTest {


    @Before
    public void clearing() {
        TaskList.clearStateForTesting();
        Archive.clearStateForTesting();
        WorkDay.clearStateForTesting();
        TaskRepository.clearStateForTesting();
    }

    @Test
    public void initialState() {
        assertFalse(TaskList.getInstance().workHasBegun());
        assertEquals(TaskList.getInstance().getSize(), 0);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 0);
    }

    @Test
    public void createTaskInEmptyTaskList() {

        assertFalse(TaskList.getInstance().workHasBegun());

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());
    }

    @Test
    public void createTaskWithZeroDuration() {

        assertFalse(TaskList.getInstance().workHasBegun());

        Task firstTask = new Task("First", 0);

        assertEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 0);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 0);
    }

    @Test
    public void createTaskWithOverflowOfCommonDuration() {

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        Task secondTask = new Task("Second", 60);

        assertNotEquals(TaskList.getInstance().createTask(secondTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 105);

        Task thirdTask = new Task("Third", 600);

        assertEquals(TaskList.getInstance().createTask(thirdTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 105);
    }

    @Test
    public void createTaskAfterStartingWork() {

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), 0);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        TaskList.getInstance().startWork();

        Task secondTask = new Task("Second", 60);

        assertEquals(TaskList.getInstance().createTask(secondTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());
    }

    @Test
    public void editTask() {
        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        assertTrue(TaskList.getInstance().editTask(firstTask, "NewFirst", 60));
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 60);
    }

    @Test
    public void editTaskWithOverflowOfCommonDuration() {
        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        Task secondTask = new Task("Second", 60);

        assertNotEquals(TaskList.getInstance().createTask(secondTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 105);

        assertFalse(TaskList.getInstance().editTask(secondTask, "NewSecond", 600));
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 105);
    }

    @Test
    public void editTaskWithZeroDuration() {
        assertFalse(TaskList.getInstance().workHasBegun());

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        Task secondTask = new Task("Second", 60);

        assertNotEquals(TaskList.getInstance().createTask(secondTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 105);

        assertFalse(TaskList.getInstance().editTask(secondTask, "NewSecond", 0));
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 105);
    }

    @Test
    public void editDurationAfterStartingWork() {

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        TaskList.getInstance().startWork();

        assertFalse(TaskList.getInstance().editTask(firstTask, "NewFirst", 60));
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 45);
    }

    @Test
    public void editNameAfterStartingWork() {

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        assertTrue(TaskList.getInstance().editTask(firstTask, "NewFirst", 45));
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 45);
        assertEquals(firstTask.getName(), "NewFirst");
    }

    @Test
    public void deleteTask() {

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);

        Task secondTask = new Task("Second", 60);

        assertNotEquals(TaskList.getInstance().createTask(secondTask), -1);

        assertTrue(TaskList.getInstance().deleteTask(secondTask));

        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 45);
    }

    @Test
    public void deleteTaskAfterStartingWork() {

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        Task secondTask = new Task("Second", 60);

        assertNotEquals(TaskList.getInstance().createTask(secondTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());

        TaskList.getInstance().startWork();

        assertFalse(TaskList.getInstance().deleteTask(secondTask));

        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
    }

    @Test
    public void sendTaskToArchive() {

        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        Task secondTask = new Task("Second", 60);

        assertNotEquals(TaskList.getInstance().createTask(secondTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());

        assertTrue(TaskList.getInstance().sendTaskToArchive(secondTask));

        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());
        assertEquals(Archive.getInstance().getArchiveListSize(), 1);
    }

    @Test
    public void sendTaskToArchiveAfterStartingWorkingDay() {
        Task firstTask = new Task("First", 45);

        assertNotEquals(TaskList.getInstance().createTask(firstTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration());

        Task secondTask = new Task("Second", 60);

        assertNotEquals(TaskList.getInstance().createTask(secondTask), -1);
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());

        TaskList.getInstance().startWork();

        assertFalse(TaskList.getInstance().sendTaskToArchive(secondTask));
        assertEquals(TaskList.getInstance().getSize(), 2);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), firstTask.getDuration() + secondTask.getDuration());
    }

    @Test
    public void workWithEmptyList() {

        assertFalse(TaskList.getInstance().startWork());
    }

}
