package me.pafias.putils;

import lombok.Getter;

import java.util.UUID;

public class MojangPlayer extends SimplePlayer {

    @Getter
    private final boolean mojangApiSuccess;

    public MojangPlayer(UUID uuid, String name, boolean mojangApiSuccess) {
        super(uuid, name);
        this.mojangApiSuccess = mojangApiSuccess;
    }

}
