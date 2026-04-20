import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Split {
    private String name;
    private List<String> exercises;

    public Split(String name, List<String> exercises) {
        this.name = name;
        this.exercises = new ArrayList<>(exercises);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getExercises() {
        return Collections.unmodifiableList(exercises);
    }

    public void addExercise(String exercise) {
        exercises.add(exercise);
    }

    public void removeExercise(String exercise) {
        exercises.remove(exercise);
    }
}
