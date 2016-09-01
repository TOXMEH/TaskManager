package taskpack.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import taskpack.model.Task;
import taskpack.model.TaskList;
import taskpack.model.TaskRepository;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Anton Nesudimov on 03.08.2016.
 */
@EnableWebMvc
@RestController
@RequestMapping("/api")
public class TaskListController extends WebMvcConfigurerAdapter {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Task> getInitialTasks() {
        return TaskRepository.getInstance().getUnarchivatedTasks();
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<JSONObject> createTask(@RequestParam("name") String name, @RequestParam("duration") int duration) {
        Task task = new Task(name, duration);
        int id = TaskList.getInstance().createTask(task);
        int position = task.getPosition();
        if (id >= 0) {
            JSONObject entity = new JSONObject();
            entity.put("id", id);
            entity.put("position", position);
            return ResponseEntity.status(HttpStatus.OK).body(entity);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
    }

    @RequestMapping(value = "/edit-{id}", method = POST)
    public ResponseEntity<JSONObject> editTask(@PathVariable int id, @RequestParam("name") String newName, @RequestParam("duration") int newDuration) {
        Task task = TaskRepository.getInstance().getTaskById(id);
        if (TaskList.getInstance().editTask(task, newName, newDuration)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<JSONObject> deleteTask(@PathVariable int id) {

        Task task = TaskRepository.getInstance().getTaskById(id);
        if (TaskList.getInstance().deleteTask(task)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
    }

    @RequestMapping(value = "/send_to_archive-{id}", method = POST)
    public ResponseEntity<JSONObject> sendTaskToArchive(@PathVariable int id) {
        Task task = TaskRepository.getInstance().getTaskById(id);
        if (TaskList.getInstance().sendTaskToArchive(task)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
    }

    @RequestMapping(value = "/start_work", method = POST)
    public ResponseEntity<JSONObject> startWork() {

        if (TaskList.getInstance().startWork()) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
    }

    @RequestMapping(value = "/move-down-{id}", method = POST)
    public ResponseEntity<JSONObject> moveTaskDown(@PathVariable int id) {
        Task task = TaskRepository.getInstance().getTaskById(id);

        if (TaskRepository.getInstance().moveTask(task, task.getPosition() + 1))
            return ResponseEntity.status(HttpStatus.OK).body(null);
        else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
    }

    @RequestMapping(value = "/move-up-{id}", method = POST)
    public ResponseEntity<JSONObject> moveTaskUp(@PathVariable int id) {
        Task task = TaskRepository.getInstance().getTaskById(id);

        if (TaskRepository.getInstance().moveTask(task, task.getPosition() - 1))
            return ResponseEntity.status(HttpStatus.OK).body(null);
        else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
    }

}
