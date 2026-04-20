import java.time.LocalDate;
import java.util.*;

public class LogWorkout extends Screen {

    public LogWorkout(DataStore dataStore) {
        super(dataStore);
        this.crumb = "menu > log workout";
    }

    @Override
    public void render() { /* driven by activate */ }

    @Override
    public void activate(Scanner sc) {
        List<Split> splits = dataStore.getSplits();
        if (splits.isEmpty()) {
            clearScreen();
            System.out.println(UI.boxTop(crumb));
            System.out.println(UI.boxLine(""));
            System.out.println(UI.boxLine("  No splits defined. Use [2] Edit split to add one."));
            System.out.println(UI.boxLine(""));
            System.out.println(UI.boxBottom());
            System.out.println(UI.sep());
            System.out.print("  Press ENTER to continue...");
            sc.nextLine();
            return;
        }

        // step 1: choose split
        Split chosen = null;
        while (chosen == null) {
            clearScreen();
            System.out.println(UI.boxTop(crumb));
            System.out.println(UI.boxLine(""));
            System.out.println(UI.boxLine("  Choose a split:"));
            System.out.println(UI.boxLine(""));
            for (int i = 0; i < splits.size(); i++) {
                String line = String.format("  [%d] %-14s (%d exercises)",
                        i + 1, splits.get(i).getName(), splits.get(i).getExercises().size());
                System.out.println(UI.boxLine(line));
            }
            System.out.println(UI.boxLine(""));
            System.out.println(UI.boxBottom());
            System.out.println(UI.sep());
            System.out.print("> ");
            String raw = sc.nextLine().trim();
            try {
                int idx = Integer.parseInt(raw) - 1;
                if (idx >= 0 && idx < splits.size()) {
                    chosen = splits.get(idx);
                } else {
                    System.out.println("  Invalid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  Enter a number.");
            }
        }

        // Step 2: log each exercise
        crumb = "menu > log workout > " + chosen.getName();
        WorkoutSession lastSession = findLastSession(chosen.getName());
        List<ExerciseLog> logs = new ArrayList<>();
        List<String> exercises = chosen.getExercises();

        for (int i = 0; i < exercises.size(); i++) {
            String exName = exercises.get(i);
            ExerciseLog prev = findPrevLog(lastSession, exName);

            ExerciseLog log = null;
            while (log == null) {
                clearScreen();
                System.out.println(UI.boxTop(crumb));
                System.out.println(UI.boxLine(""));

                //  full checklist: done / current / upcoming
                for (int j = 0; j < exercises.size(); j++) {
                    if (j < i) {
                        // Done= green tick with values
                        ExerciseLog done = logs.get(j);
                        String line = UI.GREEN + "  ✓ " + UI.RESET
                                + String.format("%-20s  %5.1f kg  %dx%d",
                                done.getExerciseName(), done.getWeight(), done.getReps(), done.getSets());
                        System.out.println(UI.boxLine(line));
                    } else if (j == i) {
                        // Current = cyan bold arrow
                        System.out.println(UI.boxLine(
                                "  " + UI.CYAN + UI.BOLD + "► " + exercises.get(j) + UI.RESET));
                    } else {
                        // Upcoming = dim
                        System.out.println(UI.boxLine(
                                UI.DIM + "    " + exercises.get(j) + UI.RESET));
                    }
                }

                // reference table for current exercise
                System.out.println(UI.boxLine(""));
                System.out.println(UI.boxSep());
                System.out.println(UI.boxLine(""));
                if (prev != null) {
                    String wLine = String.format("   %-8s  %s%-10s%s  →  ___",
                            "weight", UI.DIM, prev.getWeight() + " kg",   UI.RESET);
                    String rLine = String.format("   %-8s  %s%-10s%s  →  ___",
                            "reps",   UI.DIM, prev.getReps()   + " reps", UI.RESET);
                    String sLine = String.format("   %-8s  %s%-10s%s  →  ___",
                            "sets",   UI.DIM, prev.getSets()   + " sets", UI.RESET);
                    System.out.println(UI.boxLine(wLine));
                    System.out.println(UI.boxLine(rLine));
                    System.out.println(UI.boxLine(sLine));
                } else {
                    System.out.println(UI.boxLine(UI.DIM + "   (no previous data for this exercise)" + UI.RESET));
                }
                System.out.println(UI.boxLine(""));
                System.out.println(UI.boxBottom());

                //Command guide
                System.out.println("  [ENTER] copy last   [x] skip   [-] keep that field");
                System.out.println(UI.DIM + "  weight · reps · sets" + UI.RESET);
                System.out.println(UI.DIM + "  e.g. \"- 10 3\"  keeps the weight, enters 10 reps and 3 sets" + UI.RESET);
                System.out.println(UI.sep());
                String lastHint = (prev != null)
                        ? "  " + UI.DIM + "(last: " + prev.getWeight() + "  " + prev.getReps() + "  " + prev.getSets() + ")" + UI.RESET
                        : "";
                System.out.print("  " + exName + lastHint + "  > ");
                String input = sc.nextLine().trim();

                if (input.isEmpty()) {
                    if (prev != null) {
                        log = new ExerciseLog(exName, prev.getWeight(), prev.getReps(), prev.getSets());
                    } else {
                        System.out.println("  No previous data — please enter values manually.");
                    }
                } else if (input.equalsIgnoreCase("x")) {
                    log = new ExerciseLog(exName, 0, 0, 0);
                } else {
                    String[] parts = input.split("\\s+");
                    if (parts.length < 3) {
                        System.out.println("  Need 3 values — weight, reps, sets.");
                        continue;
                    }
                    try {
                        double w = parts[0].equals("-") ? (prev != null ? prev.getWeight() : 0) : Double.parseDouble(parts[0]);
                        int r   = parts[1].equals("-") ? (prev != null ? prev.getReps()   : 0) : Integer.parseInt(parts[1]);
                        int s   = parts[2].equals("-") ? (prev != null ? prev.getSets()   : 0) : Integer.parseInt(parts[2]);
                        log = new ExerciseLog(exName, w, r, s);
                    } catch (NumberFormatException e) {
                        System.out.println("  Invalid input — need 3 numbers.");
                    }
                }
            }
            logs.add(log);
        }

        //step 3: summary
        clearScreen();
        System.out.println(UI.boxTop(crumb + " — summary"));
        System.out.println(UI.boxLine(""));
        System.out.println(UI.boxLine(String.format("  %-18s  %-9s  %-9s  %s",
                "Exercise", "Weight", "Reps×Sets", "Volume")));
        System.out.println(UI.boxLine("  " + "─".repeat(58)));
        double totalVol = 0;
        for (ExerciseLog log : logs) {
            double vol = log.getVolume();
            totalVol += vol;
            String line = String.format("  %-18s  %5.1f kg   %2dx%-5d  %.0f kg",
                    log.getExerciseName(), log.getWeight(), log.getReps(), log.getSets(), vol);
            System.out.println(UI.boxLine(line));
        }
        System.out.println(UI.boxLine("  " + "─".repeat(58)));
        System.out.println(UI.boxLine(String.format("  %-18s  %21s  %.0f kg", "TOTAL", "", totalVol)));
        System.out.println(UI.boxLine(""));
        System.out.println(UI.boxLine("  Date: " + LocalDate.now()));
        System.out.println(UI.boxLine(""));
        System.out.println(UI.boxBottom());
        System.out.println("  [ENTER] Save   [x] Cancel");
        System.out.println(UI.sep());
        System.out.print("> ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!confirm.equals("x")) {
            WorkoutSession session = new WorkoutSession(LocalDate.now().toString(), chosen.getName(), logs);
            dataStore.addSession(session);
            System.out.println("  " + UI.GREEN + "Saved!" + UI.RESET);
        } else {
            System.out.println("  " + UI.DIM + "Cancelled." + UI.RESET);
        }
        System.out.print("  Press ENTER to continue...");
        sc.nextLine();
        crumb = "menu > log workout"; // reset for next use
    }

    private WorkoutSession findLastSession(String splitName) {
        List<WorkoutSession> sessions = dataStore.getSessions();
        for (int i = sessions.size() - 1; i >= 0; i--) {
            if (sessions.get(i).getSplitName().equalsIgnoreCase(splitName)) return sessions.get(i);
        }
        return null;
    }

    private ExerciseLog findPrevLog(WorkoutSession session, String exName) {
        if (session == null) return null;
        for (ExerciseLog log : session.getLogs()) {
            if (log.getExerciseName().equalsIgnoreCase(exName)) return log;
        }
        return null;
    }
}
