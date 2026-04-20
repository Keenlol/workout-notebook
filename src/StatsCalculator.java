import java.util.*;
import java.util.Collections;

public class StatsCalculator implements WorkoutObserver {

    private DataStore dataStore;
    private Map<String, Double> trendPerSplit = new HashMap<>();
    private Map<String, Double> lastVolumePerSplit = new HashMap<>();
    private Map<String, List<WorkoutSession>> bySplit = new LinkedHashMap<>();

    public StatsCalculator(DataStore dataStore) {
        this.dataStore = dataStore;
        onDataChanged();
    }

    @Override
    public void onDataChanged() {
        trendPerSplit.clear();
        lastVolumePerSplit.clear();
        bySplit.clear();

        for (WorkoutSession ws : dataStore.getSessions()) {
            bySplit.computeIfAbsent(ws.getSplitName(), k -> new ArrayList<>()).add(ws);
        }

        for (Map.Entry<String, List<WorkoutSession>> entry : bySplit.entrySet()) {
            String splitName = entry.getKey();
            List<WorkoutSession> sessionList = entry.getValue();

            // sort by date
            sessionList.sort(Comparator.comparing(WorkoutSession::getDate));

            double lastVol = sessionList.get(sessionList.size() - 1).getTotalVolume();
            lastVolumePerSplit.put(splitName, lastVol);

            if (sessionList.size() < 2) {
                trendPerSplit.put(splitName, 0.0);
            } else {
                double prev = sessionList.get(sessionList.size() - 2).getTotalVolume();
                trendPerSplit.put(splitName, lastVol - prev);
            }
        }
    }

    public double getTrend(String splitName) {
        return trendPerSplit.getOrDefault(splitName, 0.0);
    }

    public double getLastVolume(String splitName) {
        return lastVolumePerSplit.getOrDefault(splitName, 0.0);
    }

    public Set<String> getTrackedSplits() {
        return trendPerSplit.keySet();
    }

    public List<Double> getLastVolumes(String splitName, int count) {
        List<WorkoutSession> sessions = bySplit.getOrDefault(splitName, Collections.emptyList());
        int start = Math.max(0, sessions.size() - count);
        List<Double> result = new ArrayList<>();
        for (int i = start; i < sessions.size(); i++) {
            result.add(sessions.get(i).getTotalVolume());
        }
        return result;
    }
}
