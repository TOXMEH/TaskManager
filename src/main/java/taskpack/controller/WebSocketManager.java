package taskpack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import taskpack.Application;
import taskpack.model.WorkDay;

/**
 * Created by Anton Nesudimov on 30.08.2016.
 */
@Controller
public class WebSocketManager {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/rest_time")
    public void receiveRestTime(int minutesRest, int secondsRest) {
        Application.LOG.info("message.getMinutesRest() = " + minutesRest);
        Application.LOG.info("message.getSecondsRest() = " + secondsRest);
    }

    @Scheduled(fixedDelay = 1000)
    private void getRestTime() {
        TimeRest timeRest = new TimeRest();
        timeRest.setMinutesRest(WorkDay.getInstance().getMinutesRest());
        timeRest.setSecondsRest(WorkDay.getInstance().getSecondsRest());
        simpMessagingTemplate.convertAndSend("/topic/rest_time", timeRest);
    }

    private class TimeRest {
        private int minutesRest;
        private int secondsRest;

        public int getMinutesRest() {
            return minutesRest;
        }

        public void setMinutesRest(int minutesRest) {
            this.minutesRest = minutesRest;
        }

        public int getSecondsRest() {
            return secondsRest;
        }

        public void setSecondsRest(int secondsRest) {
            this.secondsRest = secondsRest;
        }

    }
}