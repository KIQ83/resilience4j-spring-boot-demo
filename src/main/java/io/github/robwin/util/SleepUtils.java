package io.github.robwin.util;

public class SleepUtils {

    public static void cleanSleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
