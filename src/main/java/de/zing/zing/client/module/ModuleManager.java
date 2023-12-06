package de.zing.zing.client.module;

import de.zing.zing.client.module.modules.JoinMessage.SendMessageModule;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ModuleManager {
    private static final ArrayList<Module> modules = new ArrayList<>();

    public static void init() {
        modules.add(new SendMessageModule());
    }

    public static @Nullable Module getModule(String s) {
        for (Module module : ModuleManager.modules) {
            if (module.getName().equals(s)) {
                return module;
            }
        }
        return null;
    }
}
