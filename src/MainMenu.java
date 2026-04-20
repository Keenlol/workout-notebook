import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

public class MainMenu extends Screen {

    private StatsCalculator stats;

    public MainMenu(DataStore dataStore, StatsCalculator stats) {
        super(dataStore);
        this.crumb = "menu";
        this.stats = stats;
    }

    @Override
    public void render() {
        clearScreen();
        System.out.println(UI.boxTop(crumb));
        System.out.println(UI.boxLine(""));

        renderCalendar();
        System.out.println(UI.boxLine(""));

        System.out.println(UI.boxSep());

        renderTrends();
        System.out.println(UI.boxLine(""));

        System.out.println(UI.boxSep());

        renderRecentHistory();
        System.out.println(UI.boxLine(""));

        System.out.println(UI.boxBottom());
        System.out.println("  [1] Log workout   [2] Edit split   [3] Full history   [Q] Quit");
        System.out.println(UI.sep());
    }

    private void renderCalendar() {
        LocalDate today = LocalDate.now();
        Map<String, String> dateToSplit = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            dateToSplit.put(today.minusDays(i).toString(), null);
        }
        for (WorkoutSession ws : dataStore.getSessions()) {
            if (dateToSplit.containsKey(ws.getDate())) {
                dateToSplit.put(ws.getDate(), ws.getSplitName());
            }
        }

        StringBuilder header = new StringBuilder(" ");
        StringBuilder splits = new StringBuilder(" ");
        for (Map.Entry<String, String> entry : dateToSplit.entrySet()) {
            LocalDate d = LocalDate.parse(entry.getKey());
            String day = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    + " " + String.format("%02d", d.getDayOfMonth());
            String label = entry.getValue() != null ? abbrev(entry.getValue()) : " .  ";
            header.append(String.format("%-10s", day));
            splits.append(String.format("%-10s", label));
        }

        System.out.println(UI.boxLine(" " + UI.CYAN + UI.BOLD + "[CALENDAR]" + UI.RESET));
        System.out.println(UI.boxLine(header.toString()));
        System.out.println(UI.boxLine(splits.toString()));
    }

    private String abbrev(String name) {
        return name.length() > 6 ? name.substring(0, 6) : name;
    }

    private void renderTrends() {
        System.out.println(UI.boxLine(" " + UI.CYAN + UI.BOLD + "[TRENDS]" + UI.RESET));
        Set<String> tracked = stats.getTrackedSplits();
        if (tracked.isEmpty()) {
            System.out.println(UI.boxLine("   No history yet."));
            return;
        }
        for (String name : tracked) {
            double trend = stats.getTrend(name);
            String graph = UI.GREEN + miniGraph(name) + UI.RESET;
            String arrow;
            String delta;
            if (trend > 0) {
                arrow = UI.GREEN + "[++]" + UI.RESET;
                delta = UI.GREEN + String.format("+%.0f kg/session", trend) + UI.RESET;
            } else if (trend < 0) {
                arrow = UI.RED + "[--]" + UI.RESET;
                delta = UI.RED + String.format("%.0f kg/session", trend) + UI.RESET;
            } else {
                arrow = UI.YELLOW + "[ = ]" + UI.RESET;
                delta = UI.YELLOW + "─ kg/session" + UI.RESET;
            }
            // Format name and graph without color first for alignment, then inject color
            String line = String.format("  %-10s  %s  %-5s  %s", name, graph, arrow, delta);
            System.out.println(UI.boxLine(line));
        }
    }

    private String miniGraph(String splitName) {
        String blocks = "▁▂▃▄▅▆▇█";
        List<Double> vols = stats.getLastVolumes(splitName, 5);
        if (vols.isEmpty()) return "     ";
        double min = Collections.min(vols);
        double max = Collections.max(vols);
        StringBuilder sb = new StringBuilder();
        // Left-pad with spaces if fewer than 5 sessions
        for (int i = vols.size(); i < 5; i++) sb.append(' ');
        for (double v : vols) {
            int level = (max == min) ? 3 : (int) Math.round((v - min) / (max - min) * 7);
            sb.append(blocks.charAt(level));
        }
        return sb.toString();
    }

    private void renderRecentHistory() {
        System.out.println(UI.boxLine(" " + UI.CYAN + UI.BOLD + "[RECENT HISTORY]" + UI.RESET));
        List<WorkoutSession> sessions = dataStore.getSessions();
        if (sessions.isEmpty()) {
            System.out.println(UI.boxLine("   No sessions logged."));
            return;
        }
        int start = Math.max(0, sessions.size() - 5);
        for (int i = sessions.size() - 1; i >= start; i--) {
            WorkoutSession ws = sessions.get(i);
            String delta = computeDelta(sessions, i);
            String line = String.format("  %s  %-10s  %7.0f kg  %s",
                    ws.getDate(), ws.getSplitName(), ws.getTotalVolume(), delta);
            System.out.println(UI.boxLine(line));
        }
    }

    private String computeDelta(List<WorkoutSession> sessions, int idx) {
        WorkoutSession cur = sessions.get(idx);
        for (int i = idx - 1; i >= 0; i--) {
            if (sessions.get(i).getSplitName().equalsIgnoreCase(cur.getSplitName())) {
                double diff = cur.getTotalVolume() - sessions.get(i).getTotalVolume();
                if (diff >= 0) return UI.GREEN + String.format("+%.0f kg", diff) + UI.RESET;
                else           return UI.RED   + String.format("%.0f kg", diff)  + UI.RESET;
            }
        }
        return UI.DIM + "(first)" + UI.RESET;
    }

    @Override
    public void activate(Scanner sc) {
        // Driven by App's main loop
    }
}
