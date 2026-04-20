import java.util.Scanner;

public abstract class Screen {
    protected DataStore dataStore;
    protected String crumb = "menu";

    public Screen(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    protected void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public abstract void render();

    public abstract void activate(Scanner sc);
}
