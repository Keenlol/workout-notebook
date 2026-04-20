import java.util.*;

public class History extends Screen {

    public History(DataStore dataStore) {
        super(dataStore);
        this.crumb = "menu > history";
    }

    @Override
    public void render() { /* driven by activate */ }

    @Override
    public void activate(Scanner sc) {
        boolean running = true;
        while (running) {
            clearScreen();
            List<WorkoutSession> sessions = dataStore.getSessions();
            List<WorkoutSession> reversed = new ArrayList<>(sessions);
            Collections.reverse(reversed);

            System.out.println(UI.boxTop(crumb));
            System.out.println(UI.boxLine(""));

            if (sessions.isEmpty()) {
                System.out.println(UI.boxLine("  No sessions logged yet."));
                System.out.println(UI.boxLine(""));
                System.out.println(UI.boxBottom());
                System.out.println("  [B] Back");
                System.out.println(UI.sep());
                System.out.print("> ");
                sc.nextLine();
                running = false;
                continue;
            }

            // Table header
            System.out.println(UI.boxLine(String.format(
                    "  %-4s  %-12s  %-12s  %-10s  %s", "#", "Date", "Split", "Volume", "vs Prev")));
            System.out.println(UI.boxLine("  " + "─".repeat(55)));

            for (int i = 0; i < reversed.size(); i++) {
                WorkoutSession ws = reversed.get(i);
                int origIdx = sessions.size() - 1 - i;
                String delta = computeDelta(sessions, origIdx);
                String line = String.format("  %-4d  %-12s  %-12s  %7.0f kg  %s",
                        i + 1, ws.getDate(), ws.getSplitName(), ws.getTotalVolume(), delta);
                System.out.println(UI.boxLine(line));
            }

            System.out.println(UI.boxLine(""));
            System.out.println(UI.boxBottom());
            System.out.println("  [R <number>] Remove   [B] Back");
            System.out.println(UI.sep());
            System.out.print("> ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("b")) {
                running = false;
            } else if (input.toLowerCase().startsWith("r ")) {
                String numStr = input.substring(2).trim();
                try {
                    int displayIdx = Integer.parseInt(numStr);
                    if (displayIdx < 1 || displayIdx > reversed.size()) {
                        System.out.println("  Invalid session number.");
                        pause(sc);
                    } else {
                        int origIdx = sessions.size() - displayIdx;
                        WorkoutSession ws = sessions.get(origIdx);
                        System.out.printf("  Remove #%d (%s %s)? [y/N]: ",
                                displayIdx, ws.getDate(), ws.getSplitName());
                        if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                            dataStore.removeSession(origIdx);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("  Invalid session number.");
                    pause(sc);
                }
            } else {
                System.out.println("  Unknown command.");
                pause(sc);
            }
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

    private void pause(Scanner sc) {
        System.out.print("  Press ENTER to continue...");
        sc.nextLine();
    }
}
