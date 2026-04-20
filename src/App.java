import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {

        DataStore dataStore = DataStore.getInstance();
        StatsCalculator stats = new StatsCalculator(dataStore);
        dataStore.addObserver(stats);
        ScreenFactory factory = new ScreenFactory(dataStore, stats);

        Map<String, String> menuMap = new HashMap<>();
        menuMap.put("1", "log workout");
        menuMap.put("2", "edit split");
        menuMap.put("3", "history");

        // main loop 
        MainMenu mainMenu = (MainMenu) factory.getScreen("main menu");
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            mainMenu.render();
            System.out.print("> ");
            String input = sc.nextLine().trim().toLowerCase();

            if (input.equals("q")) {
                running = false;
            } else {
                String pageName = menuMap.get(input);
                Screen screen = (pageName != null) ? factory.getScreen(pageName) : null;
                if (screen != null) {
                    screen.activate(sc);
                } else if (!input.isEmpty()) {
                    System.out.println("  Unknown command. Use 1, 2, 3, or Q.");
                    System.out.print("  Press ENTER to continue...");
                    sc.nextLine();
                }
            }
        }

        System.out.println("  Goodbye!");
        sc.close();
    }
}
