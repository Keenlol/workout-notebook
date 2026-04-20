import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataStore {

    private static DataStore instance;

    private static final String DATA_DIR = "data";
    private static final String SPLITS_FILE = DATA_DIR + "/splits.csv";
    private static final String SESSIONS_FILE = DATA_DIR + "/sessions.csv";

    private List<Split> splits = new ArrayList<>();
    private List<WorkoutSession> sessions = new ArrayList<>();
    private List<WorkoutObserver> observers = new ArrayList<>();

    private DataStore() {
        loadAll();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    //Observer 

    public void addObserver(WorkoutObserver o) {
        observers.add(o);
    }

    private void notifyObservers() {
        for (WorkoutObserver o : observers) {
            o.onDataChanged();
        }
    }

    // splits 

    public List<Split> getSplits() {
        return Collections.unmodifiableList(splits);
    }

    public void addSplit(Split s) {
        splits.add(s);
        saveAll();
    }

    public void updateSplit(Split updated) {
        for (int i = 0; i < splits.size(); i++) {
            if (splits.get(i).getName().equalsIgnoreCase(updated.getName())) {
                splits.set(i, updated);
                break;
            }
        }
        saveAll();
    }

    public void removeSplit(String name) {
        splits.removeIf(s -> s.getName().equalsIgnoreCase(name));
        saveAll();
    }

    //sessions
    public List<WorkoutSession> getSessions() {
        return Collections.unmodifiableList(sessions);
    }

    public void addSession(WorkoutSession s) {
        sessions.add(s);
        saveAll();
        notifyObservers();
    }

    public void removeSession(int index) {
        if (index < 0 || index >= sessions.size()) return;
        sessions.remove(index);
        saveAll();
        notifyObservers();
    }

    // csv io

    private void loadAll() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Warning: could not create data directory.");
        }
        loadSplits();
        loadSessions();
    }

    private void loadSplits() {
        File f = new File(SPLITS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                String name = parts[0];
                List<String> exercises = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    if (!parts[i].isEmpty()) exercises.add(parts[i]);
                }
                splits.add(new Split(name, exercises));
            }
        } catch (IOException e) {
            System.err.println("Warning: could not read splits.csv");
        }
    }

    private void loadSessions() {
        File f = new File(SESSIONS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 2) continue;
                String date = parts[0];
                String splitName = parts[1];
                List<ExerciseLog> logs = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    String[] ex = parts[i].split(":", -1);
                    if (ex.length < 4) continue;
                    String exName = ex[0];
                    double weight = Double.parseDouble(ex[1]);
                    int reps = Integer.parseInt(ex[2]);
                    int sets = Integer.parseInt(ex[3]);
                    logs.add(new ExerciseLog(exName, weight, reps, sets));
                }
                sessions.add(new WorkoutSession(date, splitName, logs));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: could not read sessions.csv");
        }
    }

    private void saveAll() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Warning: could not create data directory.");
        }
        saveSplits();
        saveSessions();
    }

    private void saveSplits() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SPLITS_FILE))) {
            for (Split s : splits) {
                StringBuilder sb = new StringBuilder(s.getName());
                for (String ex : s.getExercises()) {
                    sb.append(",").append(ex);
                }
                pw.println(sb);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not write splits.csv");
        }
    }

    private void saveSessions() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SESSIONS_FILE))) {
            for (WorkoutSession ws : sessions) {
                StringBuilder sb = new StringBuilder();
                sb.append(ws.getDate()).append(",").append(ws.getSplitName());
                for (ExerciseLog log : ws.getLogs()) {
                    sb.append(",")
                      .append(log.getExerciseName()).append(":")
                      .append(log.getWeight()).append(":")
                      .append(log.getReps()).append(":")
                      .append(log.getSets());
                }
                pw.println(sb);
            }
        } catch (IOException e) {
            System.err.println("Warning: could not write sessions.csv");
        }
    }
}
