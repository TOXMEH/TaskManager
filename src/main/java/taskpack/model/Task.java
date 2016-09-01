package taskpack.model;


import javax.persistence.*;

/**
 * Created by Anton on 25.07.2016.
 */
@Entity
@Table(name = "TASK", schema = "PUBLIC")
public class Task {

    @Id
    @Column(name = "ID")
    @GeneratedValue
    private int id;

    @Basic
    @Column(name = "NAME")
    private String name;

    @Basic
    @Column(name = "DURATION")
    private int duration;

    @Basic
    @Column(name = "COMPLETED")
    private Boolean completed;

    @Basic
    @Column(name = "ARCHIVATED")
    private Boolean archivated;
    @Basic
    @Column(name = "POSITION")
    private int position;

    public Task() {
    }

    public Task(String name, int duration) {
        this.name = name;
        this.duration = duration;
        this.completed = false;
        archivated = false;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Boolean getArchivated() {
        return archivated;
    }

    public void setArchivated(Boolean archived) {
        this.archivated = archived;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Task that = (Task) o;
//
//        if (id != that.id) return false;
//        if (name != null ? !name.equals(that.name) : that.name != null) return false;
//        if (duration != null ? !duration.equals(that.duration) : that.duration != null) return false;
//        return completed != null ? completed.equals(that.completed) : that.completed == null;
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = id;
//        result = 31 * result + (name != null ? name.hashCode() : 0);
////        result = 31 * result + (duration != null ? duration.hashCode() : 0);
//        result = 31 * result + (completed != null ? completed.hashCode() : 0);
//        return result;
//    }
}
