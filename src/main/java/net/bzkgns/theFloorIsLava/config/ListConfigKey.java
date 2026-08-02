package net.bzkgns.theFloorIsLava.config;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class ListConfigKey<T extends ConfigSection<T>, E> extends ConfigKey<T, List<E>> {

    public record ElementField<E>(
            String descriptionTranslationKey,
            ToIntFunction<E> getter,
            BiFunction<E, Double, E> setter
    ) {}

    private final Function<E, Map<String, Object>> serializer;
    private final Function<Map<?, ?>, E> deserializer;
    private final Function<E, String> elementLabelProvider;
    private final List<ElementField<E>> elementFields;

    public ListConfigKey(
            String key,
            String descriptionTranslationKey,
            Function<T, List<E>> getter,
            BiConsumer<T, List<E>> setter,
            Function<E, Map<String, Object>> serializer,
            Function<Map<?, ?>, E> deserializer,
            Function<E, String> elementLabelProvider,
            List<ElementField<E>> elementFields
    ) {
        super(
                key,
                descriptionTranslationKey,
                getter,
                setter,
                value -> {
                    throw new UnsupportedOperationException(
                            "Le paramètre '" + key + "' est une liste, il ne peut pas être modifié" +
                                    " via /tfl config set. Modifiez directement le fichier de configuration" +
                                    " ou utilisez /tfl config gui."
                    );
                }
        );
        this.serializer = serializer;
        this.deserializer = deserializer;
        this.elementLabelProvider = elementLabelProvider;
        this.elementFields = List.copyOf(elementFields);
    }

    public Function<E, Map<String, Object>> getSerializer() {
        return serializer;
    }

    public Function<Map<?, ?>, E> getDeserializer() {
        return deserializer;
    }

    public Function<E, String> getElementLabelProvider() {
        return elementLabelProvider;
    }

    public List<ElementField<E>> getElementFields() {
        return elementFields;
    }
}