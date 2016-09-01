package taskpack.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import taskpack.model.*;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Created by Anton Nesudimov on 03.08.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TaskListController.class)
@WebAppConfiguration
public class TaskListControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Autowired
    private TaskListController taskListController;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        TaskList.clearStateForTesting();
        Archive.clearStateForTesting();
        WorkDay.clearStateForTesting();
        TaskRepository.clearStateForTesting();
    }

    @Test
    public void editTask() throws Exception {

        Task task = new Task("FirstTask", 22);

        Integer id = TaskList.getInstance().createTask(task);

        mockMvc.perform(post("/edit-" + id.toString())
                .param("name", "FirstTask")
                .param("duration", "40"))
                .andExpect(status().isOk());
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 40);
    }

    @Test
    public void editTaskWithNotAcceptable() throws Exception {
        Task task = new Task("FirstTask", 22);

        Integer id = TaskList.getInstance().createTask(task);

        mockMvc.perform(post("/edit-" + id.toString())
                .param("name", "FirstTask")
                .param("duration", "0"))
                .andExpect(status().isNotAcceptable());
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), task.getDuration());
    }

    @Test
    public void createTask() throws Exception {
        mockMvc.perform(put("/")
                .param("name", "FirstTask")
                .param("duration", "40")
        )
                .andExpect(status().isOk());
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 40);
    }

    @Test
    public void createTaskWithNotAcceptableHttpStatus() throws Exception {
        mockMvc.perform(put("/")
                .param("name", "FirstTask")
                .param("duration", "0"))
                .andExpect(status().isNotAcceptable());
        assertEquals(TaskList.getInstance().getSize(), 0);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 0);

        mockMvc.perform(put("/")
                .param("name", "FirstTask")
                .param("duration", "660"))
                .andExpect(status().isNotAcceptable());
        assertEquals(TaskList.getInstance().getSize(), 0);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 0);
    }


    @Test
    public void deleteTask() throws Exception {
        Task task = new Task("FirstTask", 22);

        Integer id = TaskList.getInstance().createTask(task);

        mockMvc.perform(delete("/" + id.toString()))
                .andExpect(status().isOk());
        assertEquals(TaskList.getInstance().getSize(), 0);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 0);
    }

    @Test
    public void deleteTaskWithNotAcceptable() throws Exception {
        Task task = new Task("FirstTask", 22);

        Integer id = TaskList.getInstance().createTask(task);

        mockMvc.perform(post("/start_work"));

        mockMvc.perform(delete("/" + id.toString()))
                .andExpect(status().isNotAcceptable());
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 22);
    }

    @Test
    public void sendTaskToArchive() throws Exception {
        Task task = new Task("FirstTask", 22);

        Integer id = TaskList.getInstance().createTask(task);

        mockMvc.perform(post("/send_to_archive-" + id.toString()))
                .andExpect(status().isOk());
    }

    @Test
    public void sendTaskToArchiveWithNotAcceptable() throws Exception {
        Task firstTask = new Task("FirstTask", 22);
        Task secondTask = new Task("Sec", 15);

        Integer id = TaskList.getInstance().createTask(firstTask);
        TaskList.getInstance().createTask(secondTask);

        mockMvc.perform(post("/start_work"));

        mockMvc.perform(post("/send_to_archive-" + id.toString())
                .param("id", "0"))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void startWork() throws Exception {
        mockMvc.perform(put("/")
                .param("name", "FirstTask")
                .param("duration", "40"));
        mockMvc.perform(post("/start_work"))
                .andExpect(status().isOk());
    }

    @Test
    public void startWorkWithNotAcceptable() throws Exception {
        mockMvc.perform(post("/start_work"))
                .andExpect(status().isNotAcceptable());
    }
}
