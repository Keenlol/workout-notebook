public class UI {
    public static final int W = 80;
    private static final int INNER = W - 4;

    // colorrsss
    public static final String RESET  = "\033[0m";
    public static final String DIM    = "\033[2m";
    public static final String GREEN  = "\033[32m";
    public static final String RED    = "\033[31m";
    public static final String YELLOW = "\033[33m";
    public static final String CYAN   = "\033[36m";
    public static final String BOLD   = "\033[1m";

    // box
    public static String boxTop(String crumb) {
        int maxCrumb = W - 7;
        if (crumb.length() > maxCrumb) crumb = crumb.substring(0, maxCrumb - 3) + "...";

        String label = "─ " + RESET + CYAN + crumb + RESET + DIM + " ";

        int fill = W - 2 - (3 + crumb.length());
        return DIM + "┌" + label + "─".repeat(Math.max(1, fill)) + "┐" + RESET;
    }

    public static String boxLine(String content) {

        int visibleLen = visibleLength(content);
        int pad = Math.max(0, INNER - visibleLen);
        return DIM + "│ " + RESET + content + " ".repeat(pad) + DIM + " │" + RESET;
    }

    public static String boxSep() {
        return DIM + "├" + "─".repeat(W - 2) + "┤" + RESET;
    }

    public static String boxBottom() {
        return DIM + "└" + "─".repeat(W - 2) + "┘" + RESET;
    }

    public static String sep() {
        return DIM + "─".repeat(W) + RESET;
    }

    public static int visibleLength(String s) {
        return s.replaceAll("\033\\[[0-9;]*m", "").length();
    }
}
