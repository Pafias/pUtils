package me.pafias.putils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SimplePlayer {

    private final UUID uniqueId;
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimplePlayer)) return false;
        return ((SimplePlayer) o).uniqueId.equals(uniqueId);
    }

}
