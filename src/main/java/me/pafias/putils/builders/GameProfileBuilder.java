package me.pafias.putils.builders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GameProfileBuilder {

    private Object gameprofileHandle;

    private UUID uuid;
    private boolean generateUuidFromName = true;
    private String name;
    private boolean fetchProperties;
    private Object propertymapHandle;
    private String skinUrl;

    public GameProfileBuilder() {
    }

    @SneakyThrows
    private static Class<?> getGameProfileClass() {
        try {
            return Class.forName("com.mojang.authlib.GameProfile");
        } catch (ClassNotFoundException ex) {
            return Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
        }
    }

    @SneakyThrows
    private static Class<?> getPropertyMapClass() {
        try {
            return Class.forName("com.mojang.authlib.properties.PropertyMap");
        } catch (ClassNotFoundException ex) {
            return Class.forName("net.minecraft.util.com.mojang.authlib.properties.PropertyMap");
        }
    }

    @SneakyThrows
    private static Class<?> getPropertyClass() {
        try {
            return Class.forName("com.mojang.authlib.properties.Property");
        } catch (ClassNotFoundException ex) {
            return Class.forName("net.minecraft.util.com.mojang.authlib.properties.Property");
        }
    }

    @SneakyThrows
    public static GameProfileBuilder fromHandle(Object handle) {
        Class<?> gameProfileClass = getGameProfileClass();
        if (!gameProfileClass.isInstance(handle))
            throw new IllegalArgumentException("handle is not an instance of GameProfile");

        GameProfileBuilder builder = new GameProfileBuilder();
        builder.uuid = (UUID) gameProfileClass.getMethod("getId").invoke(handle);
        builder.name = (String) gameProfileClass.getMethod("getName").invoke(handle);
        builder.propertymapHandle = gameProfileClass.getMethod("getProperties").invoke(handle);
        return builder;
    }

    public GameProfileBuilder setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public GameProfileBuilder randomUuid() {
        this.uuid = UUID.randomUUID();
        return this;
    }

    public GameProfileBuilder setGenerateUuidFromName(boolean generateUuidFromName) {
        this.generateUuidFromName = generateUuidFromName;
        return this;
    }

    public GameProfileBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public GameProfileBuilder setFetchProperties(boolean fetchProperties) {
        this.fetchProperties = fetchProperties;
        return this;
    }

    public GameProfileBuilder setProperties(Object propertymapHandle) {
        if (propertymapHandle != null && !getPropertyMapClass().isInstance(propertymapHandle))
            throw new IllegalArgumentException("Provided propertymapHandle is not an instance of mojang's PropertyMap");

        this.propertymapHandle = propertymapHandle;
        return this;
    }

    public GameProfileBuilder putProperty(String key, String value, String signature) {
        if (this.propertymapHandle == null) {
            try {
                this.propertymapHandle = getPropertyMapClass().getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return this;
            }
        }
        try {
            Class<?> propertyClass = getPropertyClass();
            Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class, String.class);
            Object propertyHandle = propertyConstructor.newInstance(key, value, signature);

            Method putMethod = getPropertyMapClass().getMethod("put", Object.class, Object.class);
            putMethod.invoke(this.propertymapHandle, key, propertyHandle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public GameProfileBuilder setSkinUrl(String skinUrl) {
        this.skinUrl = skinUrl;
        return this;
    }

    @SneakyThrows
    public Object build() {
        Class<?> gameProfileClass = getGameProfileClass();
        Class<?> propertyMapClass = getPropertyMapClass();
        Class<?> propertyClass = getPropertyClass();

        final Constructor<?> constructor = gameProfileClass.getConstructor(UUID.class, String.class);

        if (uuid == null && name != null && generateUuidFromName)
            uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));

        gameprofileHandle = constructor.newInstance(uuid, name);
        final Object propertymapHandle = gameprofileHandle.getClass().getMethod("getProperties").invoke(gameprofileHandle);

        if (this.propertymapHandle != null) {
            Method asMapMethod = propertyMapClass.getMethod("asMap");
            Map<String, Collection<Object>> sourceMap = (Map<String, Collection<Object>>) asMapMethod.invoke(this.propertymapHandle);

            for (Map.Entry<String, Collection<Object>> entry : sourceMap.entrySet()) {
                String propertyKey = entry.getKey();
                for (Object propObj : entry.getValue()) {
                    Method putPropertyMethod = propertyMapClass.getMethod("put", Object.class, Object.class);
                    putPropertyMethod.invoke(propertymapHandle, propertyKey, propObj);
                }
            }
        }
        if (fetchProperties) {
            if (uuid == null && name != null) {
                try {
                    URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    try (Reader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                        JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
                        if (obj.has("id")) {
                            String raw = obj.get("id").getAsString();
                            String dashed = raw.replaceFirst(
                                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w+)",
                                    "$1-$2-$3-$4-$5"
                            );
                            this.uuid = UUID.fromString(dashed);

                            Field idField = gameprofileHandle.getClass().getDeclaredField("id");
                            idField.setAccessible(true);
                            idField.set(gameprofileHandle, this.uuid);
                        }
                    }
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (uuid != null) {
                try {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    try (Reader reader = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)) {
                        JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
                        if (name == null && obj.has("name")) {
                            this.name = obj.get("name").getAsString();

                            Field nameField = gameprofileHandle.getClass().getDeclaredField("name");
                            nameField.setAccessible(true);
                            nameField.set(gameprofileHandle, this.name);
                        }
                        if (obj.has("properties")) {
                            for (JsonElement element : obj.getAsJsonArray("properties")) {
                                JsonObject prop = element.getAsJsonObject();
                                String propName = prop.get("name").getAsString();
                                String propValue = prop.get("value").getAsString();
                                String propSignature = prop.has("signature") ? prop.get("signature").getAsString() : null;

                                Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class, String.class);
                                Object propertyHandle = propertyConstructor.newInstance(propName, propValue, propSignature);

                                // Add the created Property object to the GameProfile's PropertyMap
                                Method putMethod = propertyMapClass.getMethod("put", Object.class, Object.class);
                                putMethod.invoke(propertymapHandle, propName, propertyHandle);
                            }
                        }
                    }
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (skinUrl != null) {
            HttpURLConnection connection = null;
            try {
                URL apiUrl = new URL("https://api.mineskin.org/generate/url");
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStream outStream = connection.getOutputStream();
                DataOutputStream out = new DataOutputStream(outStream);
                out.writeBytes("{\"url\":\"" + skinUrl + "\"}");
                out.flush();
                out.close();
                outStream.close();

                try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
                    if (obj.has("error")) return null;
                    if (!obj.has("data")) return null;
                    JsonObject texture = obj.get("data").getAsJsonObject().get("texture").getAsJsonObject();
                    String propValue = texture.get("value").getAsString();
                    String propSignature = texture.has("signature") ? texture.get("signature").getAsString() : null;

                    Constructor<?> propertyConstructor = propertyClass.getConstructor(String.class, String.class, String.class);
                    Object propertyHandle = propertyConstructor.newInstance("textures", propValue, propSignature);

                    Method putMethod = propertyMapClass.getMethod("put", Object.class, Object.class);
                    putMethod.invoke(propertymapHandle, "textures", propertyHandle);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }
        return gameprofileHandle;
    }

    public CompletableFuture<Object> buildAsync() {
        return CompletableFuture.supplyAsync(this::build);
    }

}
