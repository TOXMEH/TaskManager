package taskpack.model;

/**
 * Created by Anton on 25.07.2016.
 */

public class TaskList {

    private static TaskList instance = new TaskList();


    private int currentTaskPosition;
    /**
     * Настипил ли рабочий день
     */
    private boolean work;
    private int commonDurationOfTasks;


    private TaskList() {
        work = false;
        commonDurationOfTasks = 0;
    }

    public static TaskList getInstance() {
        if (instance == null)
            instance = new TaskList();

        return instance;
    }

    static public void clearStateForTesting() {
        instance = null;
    }

    public int getCurrentTaskPosition() {
        return currentTaskPosition;
    }

    public void setCurrentTaskPosition(int currentTaskPosition) {
        this.currentTaskPosition = currentTaskPosition;
    }

    public boolean workHasBegun() {
        return work;
    }

    public int getCommonDurationOfTasks() {
        return commonDurationOfTasks;
    }

    public void setCommonDurationOfTasks(int commonDurationOfTasks) {
        this.commonDurationOfTasks = commonDurationOfTasks;
    }

    public int getSize() {

        return TaskRepository.getInstance().getSize(instance);
    }

    /**
     * После нажатия на кнопку НРД вызыванется функция startWork, работающая в фоновом режиме до finishWork
     * реализовано будет с помощью threads
     * POST StartWork
     */
    public boolean startWork() {
        if (getSize() == 0)
            return false;
        else if (work == true)
            return true;
        else {
            work = true;
            WorkDay.getInstance().startDoingTasks(0);
            return true;
        }
    }

    /**
     * @return удалось ли создать, т.е. не превысили ли ограничение в 8 часов
     * ADD TaskName, TaskDuration
     */
    public int createTask(Task createdTask) {
        if ((commonDurationOfTasks + createdTask.getDuration() > 480) || (createdTask.getDuration() == 0) || (workHasBegun()))
            return -1;
        else {
            createdTask.setPosition(getSize());
            int id = TaskRepository.getInstance().insert(createdTask);

            commonDurationOfTasks += createdTask.getDuration();
            return id;
        }
    }

    public void move(Task task, int newPosition) {
        TaskRepository.getInstance().moveTask(task, newPosition);
    }

    /**
     * Вызывает функцию edit класса Task
     * Если во время startWork меняем duration, то вернет false
     * если commonDurationOfTasks превышает 8 часов,то вернет false
     * POST EDIT taskId, newName, newDuration
     */
    public boolean editTask(Task task, String newName, int newDuration) {
        if ((task.getDuration() != newDuration) && (workHasBegun()))//если хотим изменить время ов время работы
            return false;
        else if ((task.getDuration() == newDuration) && (workHasBegun())) {//если хотим изменить имя во время работы
            TaskRepository.getInstance().update(task, newName);

            return true;
        } else {
            int temporaryDuration = commonDurationOfTasks - task.getDuration() + newDuration;
            if ((newDuration == 0) || (temporaryDuration > 480))
                return false;
            commonDurationOfTasks = temporaryDuration;
            TaskRepository.getInstance().update(task, newName, newDuration);

            return true;
        }
    }

    /**
     * DELETE Task
     */
    public boolean deleteTask(Task task) {
        if (workHasBegun())
            return false;
        else {
            TaskRepository.getInstance().moveTask(task, getSize() - 1);//переносим задачу в конец списка
            commonDurationOfTasks -= task.getDuration();
            TaskRepository.getInstance().delete(task);
            return true;
        }
    }


    public boolean sendTaskToArchive(Task task) {
        if (workHasBegun())
            return false;
        else {
            TaskRepository.getInstance().moveTask(task, getSize() - 1);
            commonDurationOfTasks -= task.getDuration();
            TaskRepository.getInstance().sendTaskToArchive(task);
            return true;
        }
    }
}
