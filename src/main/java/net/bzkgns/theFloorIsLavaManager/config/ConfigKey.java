package net.bzkgns.theFloorIsLavaManager.config;

import net.bzkgns.theFloorIsLavaManager.lang.Messages;
import org.bukkit.Bukkit;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConfigKey<T extends ConfigSection, R> {

    private final String key;
    private final String description_translation_key;

    protected final Function<T,R> getter;
    protected final BiConsumer<T,R> setter;
    protected final Function<String, R> parser;


    public ConfigKey(
            String key,
            String description_translation_key,
            Function<T,R> getter,
            BiConsumer<T,R> setter,
            Function<String, R> parser
    ){
        this.key = key;
        this.description_translation_key = description_translation_key;
        this.getter = getter;
        this.setter = setter;
        this.parser = parser;
    }


    public String getKey(){
        return key;
    }


    public String getDescription(){
        return Messages.string(Bukkit.getServer(), description_translation_key);
    }


    public R get(T config){
        return getter.apply(config);
    }


    public void set(T config,R value){
        setter.accept(config, value);
    }

    public void setFromString(T config, String value) {
        set(config, parser.apply(value));
    }
}
