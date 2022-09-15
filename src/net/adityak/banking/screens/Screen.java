package net.adityak.banking.screens;

import net.adityak.banking.utils.Utils;

public abstract class Screen {
    public abstract void render();

    public void onStart() {}
    public void onDestroy() {};

    public boolean shouldShowLogo() {
        return true;
    }

    public void update() {
        Utils.clearConsole();

        if (shouldShowLogo()) {
            System.out.println(ScreenManager.LOGO);
        }

        System.out.println();

        render();
    }
}
