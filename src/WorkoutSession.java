import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkoutSession {
    private String date;
    private String splitName;
    private List<ExerciseLog> logs;

    public WorkoutSession(String date, String splitName, List<ExerciseLog> logs) {
        this.date = date;
        this.splitName = splitName;
        this.logs = new ArrayList<>(logs);
    }

    public String getDate() { return date; }
    public String getSplitName() { return splitName; }

    public List<ExerciseLog> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    public double getTotalVolume() {
        double total = 0;
        for (ExerciseLog log : logs) {
            total += log.getVolume();
        }
        return total;
    }
}
