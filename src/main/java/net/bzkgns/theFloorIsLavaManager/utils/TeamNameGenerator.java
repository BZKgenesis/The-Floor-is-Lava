package net.bzkgns.theFloorIsLavaManager.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TeamNameGenerator {

    private static final Random RANDOM = new Random();

    private static final List<String> ADJECTIVES = List.of(
            "Crimson",
            "Golden",
            "Frozen",
            "Shadow",
            "Ancient",
            "Iron",
            "Silent",
            "Storm",
            "Emerald",
            "Infernal",
            "Royal",
            "Wild",
            "Scarlet",
            "Mystic",
            "Radiant"
    );

    private static final List<String> NOUNS = List.of(
            "Wolves",
            "Dragons",
            "Titans",
            "Phoenix",
            "Cobras",
            "Ravens",
            "Guardians",
            "Sentinels",
            "Raiders",
            "Nomads",
            "Valkyries",
            "Outlaws",
            "Voyagers",
            "Comets",
            "Cyclones",
            "Blaze",
            "Wardens",
            "Creepers",
            "Endermen",
            "Piglins"
    );

    private TeamNameGenerator() {}

    public static String generate(List<String> alreadyUsed) {

        List<String> possibilities = new ArrayList<>();

        for (String adjective : ADJECTIVES) {
            for (String noun : NOUNS) {
                String name = adjective + " " + noun;

                if (!alreadyUsed.contains(name)) {
                    possibilities.add(name);
                }
            }
        }

        if (possibilities.isEmpty()) {
            return "Team " + RANDOM.nextInt(10000);
        }

        return possibilities.get(RANDOM.nextInt(possibilities.size()));
    }

}