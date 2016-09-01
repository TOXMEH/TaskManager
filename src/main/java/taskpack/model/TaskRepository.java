package taskpack.model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Anton Nesudimov on 09.08.2016.
 */

public class TaskRepository {

    private static final TaskRepository ourInstance = new TaskRepository();
    private static final SessionFactory ourSessionFactory;
    private static final ServiceRegistry serviceRegistry;
    private static SessionFactory sessionFactory;

    private static LocalDateTime countdownTimer;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private TaskRepository() {
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static TaskRepository getInstance() {
        return ourInstance;
    }

    public static void clearStateForTesting() {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Task").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public int insert(Task task) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        int id = (int) session.save(task);
        session.getTransaction().commit();
        session.close();
        return id;
    }

    public void update(Task task, String newName, int newDuration) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        task.setName(newName);
        task.setDuration(newDuration);
        session.update(task);
        session.getTransaction().commit();
        session.close();
    }

    public void update(Task task, String newName) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        task.setName(newName);
        session.update(task);
        session.getTransaction().commit();
        session.close();
    }

    public void delete(Task task) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(task);
        session.getTransaction().commit();
        session.close();
    }

    public int getSize(TaskList taskList) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        int count = ((Long) session.createQuery("select count(*) from Task WHERE archivated=false").uniqueResult()).intValue();

        session.getTransaction().commit();
        session.close();

        return count;
    }

    public int getSize(Archive archive) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        int count = ((Long) session.createQuery("select count(*) from Task WHERE archivated=true").uniqueResult()).intValue();

        session.getTransaction().commit();
        session.close();

        return count;
    }

    public int getDurationOfCompletedTasks() {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        int count = ((Long) session.createQuery("select count(duration) from Task WHERE completed=true").uniqueResult()).intValue();

        session.getTransaction().commit();
        session.close();

        return count;
    }

    public int getDurationOfUnarchivatedTasks() {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        int count = ((Long) session.createQuery("select count(duration) from Task WHERE archivated=false").uniqueResult()).intValue();

        session.getTransaction().commit();
        session.close();

        return count;
    }

    public int getNumberOfCompletedTasks() {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        int count = ((Long) session.createQuery("select count(*) from Task WHERE completed=true").uniqueResult()).intValue();

        session.getTransaction().commit();
        session.close();

        return count;
    }

    public void sendTaskToArchive(Task task) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        task.setArchivated(true);
        session.update(task);
        session.getTransaction().commit();
        session.close();
    }

    public void deleteCompletedTasks() {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        session.createQuery("DELETE FROM Task WHERE completed=true").executeUpdate();
        session.getTransaction().commit();
        session.close();
    }

    public void returnTaskFromArchive(Task task) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();
        task.setArchivated(false);
        task.setPosition(TaskList.getInstance().getSize());
        session.update(task);
        session.getTransaction().commit();
        session.close();
    }

    public int getDurationOfTaskOnPosition(int position) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        int duration = (int) session.createQuery("select E.duration from Task E WHERE E.archivated=false and E.position=" + position).uniqueResult();

        return duration;
    }

    public Task getTaskById(int id) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        Task task = (Task) session.createQuery("FROM Task WHERE id=" + id).uniqueResult();

        return task;
    }

    public Task getTaskByPosition(int position) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        Task task = (Task) session.createQuery("FROM Task WHERE archivated=false and position=" + position).uniqueResult();

        return task;
    }

    public List<Task> getUnarchivatedTasks() {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        List<Task> tasks = session.createQuery("FROM Task f WHERE archivated=false ORDER BY f.position ASC").list();

        return tasks;
    }

    public List<Task> getArchivatedTasks() {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        List<Task> tasks = session.createQuery("FROM Task f WHERE archivated=true ORDER BY f.position ASC").list();

        return tasks;
    }

    public void changeCompletion(int id) {
        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        Task task = (Task) session.createQuery("FROM Task WHERE id=" + id).uniqueResult();

        task.setCompleted(!task.getCompleted());

        session.update(task);

        session.getTransaction().commit();

        session.close();

    }

    public boolean moveTask(Task task, int newPosition) {
        int oldPosition = task.getPosition();

        Session session = getSession().getSessionFactory().openSession();
        session.beginTransaction();

        if ((newPosition > TaskList.getInstance().getSize() - 1) || (newPosition < 0))
            return false;
        if (oldPosition < newPosition) {
            while (oldPosition < newPosition) {
                session.createQuery("UPDATE Task SET position=" + oldPosition + " WHERE archivated=false and position=" + (oldPosition + 1)).executeUpdate();
                oldPosition++;
            }
        } else {
            while (oldPosition > newPosition) {
                session.createQuery("UPDATE Task SET position=" + oldPosition + " WHERE archivated=false and POSITION=" + (oldPosition - 1)).executeUpdate();
                oldPosition--;
            }
        }

        task.setPosition(newPosition);
        session.update(task);
        session.getTransaction().commit();

        session.close();

        return true;
    }
}
