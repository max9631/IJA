package common;

public class Formatter {

    public static String formatTime(int timestamp) {
        String secondsPrefix = timestamp%60 > 9 ? "" : "0";
        return timestamp/60 + ":" + secondsPrefix + timestamp%60;
    }

    public static String formatTime(double timestamp) {
        return formatTime((int) timestamp);
    }
}
