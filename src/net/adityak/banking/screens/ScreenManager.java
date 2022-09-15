package net.adityak.banking.screens;

import java.util.Stack;

public class ScreenManager {
    public static String LOGO = " _       __     ____             \n" +
            "| |     / /__  / __ \\____ ___  __\n" +
            "| | /| / / _ \\/ /_/ / __ `/ / / /\n" +
            "| |/ |/ /  __/ ____/ /_/ / /_/ / \n" +
            "|__/|__/\\___/_/    \\__,_/\\__, /  \n" +
            "                        /____/   ";

    public static Stack<Screen> screens = new Stack<>();

    public static void push(Screen screen) {
        screens.push(screen);
        screen.onStart();
        screen.update();
    }

    public static void pop() {
        screens.pop().onDestroy();

        if (!screens.empty()) {
            screens.peek().update();
        }
    }

    public static void replace(Screen screen) {
        screens.pop().onDestroy();
        push(screen);
    }
}
