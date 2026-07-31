package net.bzkgns.theFloorIsLava.config;

import java.util.List;

public interface ConfigSection<T extends ConfigSection<T>> {
    String getName();
    List<ConfigKey<T, ?>> getKeys();

}
