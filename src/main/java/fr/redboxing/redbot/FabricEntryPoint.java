package fr.redboxing.redbot;

import net.fabricmc.api.ClientModInitializer;

public class FabricEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("RedBot Mod is ready to be used!");
    }
}
