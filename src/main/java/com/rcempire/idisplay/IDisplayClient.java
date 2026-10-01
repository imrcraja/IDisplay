package com.rcempire.idisplay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public final class IDisplayClient implements ClientModInitializer {
    public static final String MOD_ID = "idisplay";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config", "idisplay.json");
    private static final Map<UUID, String> DISPLAY_NAMES = new LinkedHashMap<>();

    private static final List<String> SUGGESTIONS = List.of(
            "Technoblade", "Dream", "MrBeast", "DrDonut", "Senpai", "DreamXD",
            "TommyInnit", "GeorgeNotFound", "Sapnap", "Skeppy", "BadBoyHalo",
            "Grian", "DanTDM", "CaptainSparklez", "Ranboo", "Purpled"
    );

    @Override
    public void onInitializeClient() {
        loadConfig();
        registerCommands();
        ClientPlayConnectionEvents.JOIN.register((handler, client) -> loadConfig());
    }

    private static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(
                literal("idisplay")
                    .then(literal("set").then(argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String name = StringArgumentType.getString(ctx, "name").trim();
                            if (name.isEmpty() || name.length() > 32) {
                                send(ctx.getSource(), "Name must be 1-32 characters.", Formatting.RED);
                                return 0;
                            }
                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client.player == null) return 0;
                            DISPLAY_NAMES.put(client.player.getUuid(), name);
                            saveConfig();
                            send(ctx.getSource(), "Your IDisplay name is now " + name + ".", Formatting.GREEN);
                            return 1;
                        })))
                    .then(literal("clear").executes(ctx -> {
                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client.player != null) {
                            DISPLAY_NAMES.remove(client.player.getUuid());
                            saveConfig();
                            send(ctx.getSource(), "Your IDisplay name has been cleared.", Formatting.YELLOW);
                        }
                        return 1;
                    }))
                    .then(literal("join").then(argument("username", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            SUGGESTIONS.forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> fakePresenceMessage(ctx.getSource(),
                                StringArgumentType.getString(ctx, "username"), true))))
                    .then(literal("leave").then(argument("username", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            SUGGESTIONS.forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> fakePresenceMessage(ctx.getSource(),
                                StringArgumentType.getString(ctx, "username"), false))))
                    .then(literal("reload").executes(ctx -> {
                        loadConfig();
                        send(ctx.getSource(), "IDisplay configuration reloaded.", Formatting.GREEN);
                        return 1;
                    }))
                    .then(literal("help").executes(ctx -> {
                        send(ctx.getSource(), "/idisplay set <name>", Formatting.AQUA);
                        send(ctx.getSource(), "/idisplay clear", Formatting.AQUA);
                        send(ctx.getSource(), "/idisplay join <username>", Formatting.AQUA);
                        send(ctx.getSource(), "/idisplay leave <username>", Formatting.AQUA);
                        return 1;
                    }))
            )
        );
    }

    private static int fakePresenceMessage(
            net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource source,
            String username, boolean joined) {
        if (username.isEmpty() || username.length() > 16) {
            send(source, "Username must be 1-16 characters.", Formatting.RED);
            return 0;
        }
        Text message = Text.literal(username + (joined ? " joined the world." : " left the world."))
                .formatted(joined ? Formatting.YELLOW : Formatting.GRAY);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.inGameHud != null) {
            client.inGameHud.getChatHud().addMessage(message);
        } else {
            source.sendFeedback(message);
        }
        return 1;
    }

    private static void send(
            net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource source,
            String message, Formatting formatting) {
        source.sendFeedback(Text.literal(message).formatted(formatting));
    }

    public static String getDisplayName(UUID uuid, String fallback) {
        return DISPLAY_NAMES.getOrDefault(uuid, fallback);
    }

    public static Text replaceOwnName(Text message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return message;
        String original = client.player.getGameProfile().getName();
        String custom = DISPLAY_NAMES.get(client.player.getUuid());
        if (custom == null || custom.equals(original)) return message;
        String plain = message.getString();
        if (!plain.contains(original)) return message;
        return Text.literal(plain.replace(original, custom));
    }

    private static void loadConfig() {
        DISPLAY_NAMES.clear();
        try {
            if (!Files.exists(CONFIG_PATH)) return;
            JsonObject root = JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject();
            if (!root.has("names")) return;
            JsonObject names = root.getAsJsonObject("names");
            for (String key : names.keySet()) {
                try {
                    DISPLAY_NAMES.put(UUID.fromString(key), names.get(key).getAsString());
                } catch (IllegalArgumentException ignored) {}
            }
        } catch (Exception ignored) {}
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            JsonObject root = new JsonObject();
            JsonObject names = new JsonObject();
            DISPLAY_NAMES.forEach((uuid, name) -> names.addProperty(uuid.toString(), name));
            root.add("names", names);
            Files.writeString(CONFIG_PATH, GSON.toJson(root));
        } catch (IOException ignored) {}
    }
}
