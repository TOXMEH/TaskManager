package taskpack.controller;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import taskpack.model.Archive;
import taskpack.model.Task;
import taskpack.model.TaskRepository;

import java.util.List;

/**
 * Created by Anton Nesudimov on 11.08.2016.
 */
@EnableWebMvc
@RestController
@RequestMapping("/api/archive")
public class ArchiveController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<Task> getInitialTasks() {
        return TaskRepository.getInstance().getArchivatedTasks();
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<JSONObject> deleteTask(@PathVariable int id) {

        Task task = TaskRepository.getInstance().getTaskById(id);
        Archive.getInstance().deleteTask(task);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @RequestMapping(value = "/return_from_archive-{id}", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> sendTaskToArchive(@PathVariable int id) {
        Task task = TaskRepository.getInstance().getTaskById(id);
        if (Archive.getInstance().returnTask(task)) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
    }
}
