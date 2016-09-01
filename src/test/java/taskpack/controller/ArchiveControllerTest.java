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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by Anton Nesudimov on 11.08.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ArchiveController.class)
@WebAppConfiguration
public class ArchiveControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Autowired
    private ArchiveController archiveController;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        TaskList.clearStateForTesting();
        Archive.clearStateForTesting();
        WorkDay.clearStateForTesting();
        TaskRepository.clearStateForTesting();
    }

    @Test
    public void deleteTask() throws Exception {
        Task task = new Task("FirstTask", 22);

        Integer id = TaskList.getInstance().createTask(task);

        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 22);

        TaskList.getInstance().sendTaskToArchive(task);

        assertEquals(TaskList.getInstance().getSize(), 0);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 0);

        mockMvc.perform(delete("/archive/" + id.toString()))
                .andExpect(status().isOk());

    }

    @Test
    public void returnTaskFromArchive() throws Exception {
        Task task = new Task("FirstTask", 22);

        Integer id = TaskList.getInstance().createTask(task);

        TaskList.getInstance().sendTaskToArchive(task);

        assertEquals(TaskList.getInstance().getSize(), 0);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 0);

        mockMvc.perform(post("/archive/return_from_archive-" + id.toString()))
                .andExpect(status().isOk());
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 22);
    }

    @Test
    public void returnTaskFromArchiveWithNotAcceptable() throws Exception {
        Task firstTask = new Task("FirstTask", 22);
        Task secondTask = new Task("Sec", 15);

        Integer id = TaskList.getInstance().createTask(firstTask);
        TaskList.getInstance().createTask(firstTask);

        TaskList.getInstance().sendTaskToArchive(firstTask);

        TaskList.getInstance().startWork();

        mockMvc.perform(post("/archive/return_from_archive-" + id.toString()))
                .andExpect(status().isNotAcceptable());
        assertEquals(TaskList.getInstance().getSize(), 1);
        assertEquals(TaskList.getInstance().getCommonDurationOfTasks(), 15);
    }


}

