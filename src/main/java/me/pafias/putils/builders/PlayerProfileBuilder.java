package me.pafias.putils.builders;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.pafias.putils.pUtils;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerProfileBuilder {

    private UUID uuid;
    private String name;
    private boolean fetchProperties;
    private Set<ProfileProperty> properties;

    public PlayerProfileBuilder() {
    }

    public PlayerProfileBuilder setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public PlayerProfileBuilder randomUuid() {
        this.uuid = UUID.randomUUID();
        return this;
    }

    public PlayerProfileBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PlayerProfileBuilder setFetchProperties(boolean fetchProperties) {
        this.fetchProperties = fetchProperties;
        return this;
    }

    public PlayerProfileBuilder setProperties(Set<ProfileProperty> properties) {
        this.properties = properties;
        return this;
    }

    public PlayerProfile build() {
        PlayerProfile profile = pUtils.getPlugin().getServer().createProfile(uuid, name);
        if (properties != null)
            profile.setProperties(properties);
        if (fetchProperties)
            profile.complete();
        return profile;
    }

    public CompletableFuture<PlayerProfile> buildAsync() {
        return CompletableFuture.supplyAsync(this::build);
    }

}
