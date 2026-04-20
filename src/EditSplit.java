import java.util.*;

public class EditSplit extends Screen {

    public EditSplit(DataStore dataStore) {
        super(dataStore);
        this.crumb = "menu > edit split";
    }

    @Override
    public void render() { /* driven by activate */ }

    @Override
    public void activate(Scanner sc) {
        boolean running = true;
        while (running) {
            clearScreen();
            System.out.println(UI.boxTop(crumb));
            System.out.println(UI.boxLine(""));
            renderSplits();
            System.out.println(UI.boxLine(""));
            System.out.println(UI.boxBottom());
            System.out.println("  [A <name>] Add   [E <name>] Edit   [R <name>] Remove   [B] Back");
            System.out.println(UI.sep());
            System.out.print("> ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("b")) {
                running = false;
            } else if (input.toLowerCase().startsWith("a ")) {
                addSplit(input.substring(2).trim());
            } else if (input.toLowerCase().startsWith("r ")) {
                removeSplit(sc, input.substring(2).trim());
            } else if (input.toLowerCase().startsWith("e ")) {
                editSplit(sc, input.substring(2).trim());
            } else {
                System.out.println("  Unknown command.");
                pause(sc);
            }
        }
        crumb = "menu > edit split"; // reset
    }

    private void renderSplits() {
        List<Split> splits = dataStore.getSplits();
        if (splits.isEmpty()) {
            System.out.println(UI.boxLine("  No splits defined yet."));
            return;
        }
        for (Split s : splits) {
            System.out.println(UI.boxLine("  [" + s.getName() + "]"));
            List<String> exs = s.getExercises();
            if (exs.isEmpty()) {
                System.out.println(UI.boxLine("    (no exercises)"));
            } else {
                for (int i = 0; i < exs.size(); i++) {
                    System.out.println(UI.boxLine(String.format("    %d. %s", i + 1, exs.get(i))));
                }
            }
            System.out.println(UI.boxLine(""));
        }
    }

    private void addSplit(String name) {
        if (name.isEmpty()) { System.out.println("  Name cannot be empty."); return; }
        for (Split s : dataStore.getSplits()) {
            if (s.getName().equalsIgnoreCase(name)) {
                System.out.println("  Split '" + name + "' already exists.");
                return;
            }
        }
        dataStore.addSplit(new Split(name, new ArrayList<>()));
    }

    private void removeSplit(Scanner sc, String name) {
        boolean found = dataStore.getSplits().stream().anyMatch(s -> s.getName().equalsIgnoreCase(name));
        if (!found) {
            System.out.println("  Split '" + name + "' not found.");
            pause(sc);
        } else {
            dataStore.removeSplit(name);
        }
    }

    private void editSplit(Scanner sc, String name) {
        Split target = null;
        for (Split s : dataStore.getSplits()) {
            if (s.getName().equalsIgnoreCase(name)) { target = s; break; }
        }
        if (target == null) {
            System.out.println("  Split '" + name + "' not found.");
            pause(sc);
            return;
        }

        Split copy = new Split(target.getName(), new ArrayList<>(target.getExercises()));
        crumb = "menu > edit split > " + copy.getName();

        boolean editing = true;
        while (editing) {
            clearScreen();
            System.out.println(UI.boxTop(crumb));
            System.out.println(UI.boxLine(""));
            List<String> exs = copy.getExercises();
            if (exs.isEmpty()) {
                System.out.println(UI.boxLine("  (no exercises)"));
            } else {
                for (int i = 0; i < exs.size(); i++) {
                    System.out.println(UI.boxLine(String.format("  %d. %s", i + 1, exs.get(i))));
                }
            }
            System.out.println(UI.boxLine(""));
            System.out.println(UI.boxBottom());
            System.out.println("  [A <exercise>] Add   [R <exercise>] Remove   [B] Back");
            System.out.println(UI.sep());
            System.out.print("> ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("b")) {
                dataStore.updateSplit(copy);
                editing = false;
            } else if (input.toLowerCase().startsWith("a ")) {
                String ex = input.substring(2).trim();
                if (!ex.isEmpty()) copy.addExercise(ex);
            } else if (input.toLowerCase().startsWith("r ")) {
                copy.removeExercise(input.substring(2).trim());
            } else {
                System.out.println("  Unknown command.");
                pause(sc);
            }
        }
        crumb = "menu > edit split";
    }

    private void pause(Scanner sc) {
        System.out.print("  Press ENTER to continue...");
        sc.nextLine();
    }
}
