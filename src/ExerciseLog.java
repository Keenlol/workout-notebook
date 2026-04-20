public class ExerciseLog {
    private String exerciseName;
    private double weight;
    private int reps;
    private int sets;

    public ExerciseLog(String exerciseName, double weight, int reps, int sets) {
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
    }

    public String getExerciseName() { return exerciseName; }
    public double getWeight() { return weight; }
    public int getReps() { return reps; }
    public int getSets() { return sets; }

    public double getVolume() {
        return weight * reps * sets;
    }
}
