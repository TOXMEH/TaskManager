package taskpack.model;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 27.07.2016.
 */
public class WorkDay {
    private static WorkDay instance;

    private static int amountOfUncompletedTasks;
    private static int amountOfCompletedTasks;
    private static boolean hasTimerFinished;
    private static int minutesRest;
    private static int secondsRest;
    private static Timer timer;
    private static TimerTask timerTask;
    private static LocalDateTime startTime;

    private WorkDay() {
        TaskList.getInstance().setCurrentTaskPosition(0);
    }

    public static WorkDay getInstance() {
        if (instance == null)
            instance = new WorkDay();
        return instance;
    }

    static public void clearStateForTesting() {
        instance = null;
    }

    public static void startDoingTasks(int currentTaskPosition) {
        startTime = LocalDateTime.now();

        TaskList.getInstance().setCurrentTaskPosition(currentTaskPosition);

//        System.out.println("current"+TaskList.getInstance().getCurrentTaskPosition());
        if (currentTaskPosition < TaskList.getInstance().getSize()) {
            hasTimerFinished = false;

            int taskDuration = TaskRepository.getInstance().getDurationOfTaskOnPosition(currentTaskPosition) * 60 * 1000;//* 60 seconds

            long delay = 1000;
            long period = 1000;
            timer = new Timer(true);

            minutesRest = TaskRepository.getInstance().getDurationOfTaskOnPosition(currentTaskPosition) - 1;
            secondsRest = 60;

            timer.purge();

            runTimer();
        }
    }

    public static void runTimer() {
        Timer taskTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if ((secondsRest == 1) && (minutesRest == 0)) {
                    cancel();

                    startDoingTasks(TaskList.getInstance().getCurrentTaskPosition() + 1);

//                    timer.cancel();
//                    timer.purge();
//                    cancelTimer();
//                    return;
                }

                secondsRest--;

                if (secondsRest == 0) {
                    minutesRest--;
                    secondsRest = 60;
                }
            }
        };

        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private static void cancelTimer() {
        timerTask.cancel();
        timer.cancel();
        timer.purge();
    }

    public int getMinutesRest() {
        return minutesRest;
    }

    public int getSecondsRest() {
        return secondsRest;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getAmountOfUncompletedTasks() {
        return amountOfUncompletedTasks;
    }

    public int getAmountOfCompletedTasks() {
        return amountOfCompletedTasks;
    }

//    @Override
//    public void run() {
//        startDoingTasks();
//    }
}
