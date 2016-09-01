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
import taskpack.model.WorkDay;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Anton Nesudimov on 03.08.2016.
 */
@EnableWebMvc
@RestController
@RequestMapping("/api/workday")
public class WorkDayController extends WebMvcConfigurerAdapter {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<JSONObject> getCurrentTask() {

        int currentTaskPosition = TaskList.getInstance().getCurrentTaskPosition();

        if (currentTaskPosition < TaskList.getInstance().getSize()) {

            Task currentTask = TaskRepository.getInstance().getTaskByPosition(currentTaskPosition);

            JSONObject entity = new JSONObject();
            entity.put("name", currentTask.getName());
            entity.put("duration", currentTask.getDuration());
            entity.put("minutesRest", WorkDay.getInstance().getMinutesRest());
            entity.put("secondsRest", WorkDay.getInstance().getSecondsRest());

            return ResponseEntity.status(HttpStatus.OK).body(entity);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);
        }
    }

    @RequestMapping(value = "/change-completion-{id}", method = POST)
    public ResponseEntity<JSONObject> changeTaskCompletion(@PathVariable int id) {

        TaskRepository.getInstance().changeCompletion(id);

        return ResponseEntity.status(HttpStatus.OK).body(null);

    }

    @RequestMapping(value = "/delete-completed-tasks", method = POST)
    public ResponseEntity<JSONObject> deleteCompletedTasksAndGetStatisticsInfo() {
        JSONObject entity = new JSONObject();
        entity.put("commonTaskDuration", TaskRepository.getInstance().getDurationOfUnarchivatedTasks());
        entity.put("numberOfCompletedTasks", TaskRepository.getInstance().getNumberOfCompletedTasks());
        entity.put("startHour", WorkDay.getInstance().getStartTime().getHour());
        entity.put("startMinute", WorkDay.getInstance().getStartTime().getMinute());

        TaskRepository.getInstance().deleteCompletedTasks();

        return ResponseEntity.status(HttpStatus.OK).body(entity);
    }

//    @MessageMapping(value="/rest_time")
//    public ResponseEntity<JSONObject> getRestTime() {
//        JSONObject entity = new JSONObject();
//        entity.put("minutesRest", WorkDay.getInstance().getMinutesRest());
//        entity.put("secondsRest", WorkDay.getInstance().getSecondsRest());
//
//        return ResponseEntity.status(HttpStatus.OK).body(entity);
//    }
}
