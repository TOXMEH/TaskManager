package taskpack.model;

/**
 * Created by Anton on 25.07.2016.
 */
public class Archive {
    private static Archive instance = new Archive();

    private Archive() {
    }

    public static Archive getInstance() {
        if (instance == null)
            instance = new Archive();

        return instance;
    }

    static public void clearStateForTesting() {
        instance = null;
    }

    public int getArchiveListSize() {
        return TaskRepository.getInstance().getSize(instance);
    }

    public boolean returnTask(Task task) {

        if ((TaskList.getInstance().getCommonDurationOfTasks() + task.getDuration() > 480) || (task.getDuration() == 0) || (TaskList.getInstance().workHasBegun()))
            return false;
        else {
            TaskRepository.getInstance().returnTaskFromArchive(task);

            TaskList.getInstance().setCommonDurationOfTasks(TaskList.getInstance().getCommonDurationOfTasks() + task.getDuration());

            return true;
        }
    }

    public void deleteTask(Task task) {
        TaskRepository.getInstance().delete(task);
    }


}
