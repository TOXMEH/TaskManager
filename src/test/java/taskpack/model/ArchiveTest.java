package taskpack.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Anton on 25.07.2016.
 */
public class ArchiveTest {

    @Before
    public void clearing() {
        Archive.clearStateForTesting();
        TaskList.clearStateForTesting();
        WorkDay.clearStateForTesting();
        TaskRepository.clearStateForTesting();
    }

    @Test
    public void returnTaskFromArchiveToTaskList() {

        Task firstTask = new Task("First", 45);

        TaskList.getInstance().createTask(firstTask);

        Task secondTask = new Task("Second", 60);

        TaskList.getInstance().createTask(secondTask);

        assertTrue(TaskList.getInstance().sendTaskToArchive(secondTask));

        assertTrue(Archive.getInstance().returnTask(secondTask));

        assertEquals(TaskList.getInstance().getSize(), 2);
    }

    @Test
    public void returnTaskToFilledTaskList() {

        Task firstTask = new Task("First", 45);

        TaskList.getInstance().createTask(firstTask);

        Task secondTask = new Task("Second", 60);

        TaskList.getInstance().createTask(secondTask);

        assertTrue(TaskList.getInstance().sendTaskToArchive(secondTask));

        TaskList.getInstance().editTask(firstTask, "Long", 480);

        assertFalse(Archive.getInstance().returnTask(secondTask));

        assertEquals(Archive.getInstance().getArchiveListSize(), 1);
        assertEquals(TaskList.getInstance().getSize(), 1);
    }

    @Test
    public void returnTaskAfterStartingWork() {

        Task firstTask = new Task("First", 45);

        TaskList.getInstance().createTask(firstTask);

        Task secondTask = new Task("Second", 60);

        TaskList.getInstance().createTask(secondTask);

        assertTrue(TaskList.getInstance().sendTaskToArchive(secondTask));

        TaskList.getInstance().startWork();

        assertFalse(Archive.getInstance().returnTask(secondTask));

        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(Archive.getInstance().getArchiveListSize(), 1);
    }

    @Test
    public void deleteTaskFromArchive() {

        Task firstTask = new Task("First", 45);

        TaskList.getInstance().createTask(firstTask);

        Task secondTask = new Task("Second", 60);

        TaskList.getInstance().createTask(secondTask);

        assertTrue(TaskList.getInstance().sendTaskToArchive(firstTask));
        assertTrue(TaskList.getInstance().sendTaskToArchive(secondTask));

        assertEquals(Archive.getInstance().getArchiveListSize(), 2);

        Archive.getInstance().deleteTask(secondTask);

        Archive.getInstance().deleteTask(firstTask);

        assertEquals(Archive.getInstance().getArchiveListSize(), 0);
    }

}