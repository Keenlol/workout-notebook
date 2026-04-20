import java.util.HashMap;
import java.util.Map;

public class ScreenFactory {

    private final Map<String,Screen>registry = new HashMap<>();

    public ScreenFactory(DataStore dataStore, StatsCalculator stats) {
        registry.put("log workout",new LogWorkout(dataStore));
        registry.put("edit split", new EditSplit(dataStore));
        registry.put("history", new History(dataStore));
        registry.put("main menu",new MainMenu(dataStore, stats));
    }

    public Screen getScreen(String pageName) {
        return registry.get(pageName);
    }
}
