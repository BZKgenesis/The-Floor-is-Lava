package net.bzkgns.theFloorIsLava.items.abilities.gambling;

import net.bzkgns.theFloorIsLava.config.gambling.GamblingConfigKeys;
import net.bzkgns.theFloorIsLava.managers.ConfigRegistry;

import java.util.Map;

public class GamblingEngine {
    private static final Map<GamblingSymbol, Double> SYMBOL_PROBABILITIES = Map.of(
            GamblingSymbol.CERISE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CERISE_PROBABILITY.getKey()),
            GamblingSymbol.CITRON, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CITRON_PROBABILITY.getKey()),
            GamblingSymbol.RAISIN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.RAISIN_PROBABILITY.getKey()),
            GamblingSymbol.CLOCHE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CLOCHE_PROBABILITY.getKey()),
            GamblingSymbol.ETOILE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.ETOILE_PROBABILITY.getKey()),
            GamblingSymbol.DIAMOND, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.DIAMOND_PROBABILITY.getKey()),
            GamblingSymbol.SEVEN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.SEVEN_PROBABILITY.getKey())
    );
    private static final Map<GamblingSymbol, Double> SYMBOL_JACKPOT_MULTIPLIERS = Map.of(
            GamblingSymbol.CERISE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CERISE_JACKPOT.getKey()),
            GamblingSymbol.CITRON, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CITRON_JACKPOT.getKey()),
            GamblingSymbol.RAISIN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.RAISIN_JACKPOT.getKey()),
            GamblingSymbol.CLOCHE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CLOCHE_JACKPOT.getKey()),
            GamblingSymbol.ETOILE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.ETOILE_JACKPOT.getKey()),
            GamblingSymbol.DIAMOND, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.DIAMOND_JACKPOT.getKey()),
            GamblingSymbol.SEVEN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.SEVEN_JACKPOT.getKey())
    );
    private static final Map<GamblingSymbol, Double> SYMBOL_TWO_OF_KIND_MULTIPLIERS = Map.of(
            GamblingSymbol.CERISE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CERISE_TWO_KIND.getKey()),
            GamblingSymbol.CITRON, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CITRON_TWO_KIND.getKey()),
            GamblingSymbol.RAISIN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.RAISIN_TWO_KIND.getKey()),
            GamblingSymbol.CLOCHE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CLOCHE_TWO_KIND.getKey()),
            GamblingSymbol.ETOILE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.ETOILE_TWO_KIND.getKey()),
            GamblingSymbol.DIAMOND, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.DIAMOND_TWO_KIND.getKey()),
            GamblingSymbol.SEVEN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.SEVEN_TWO_KIND.getKey())
    );
    private static final Map<GamblingSymbol, Double> SYMBOL_ONE_OF_KIND_MULTIPLIERS = Map.of(
            GamblingSymbol.CERISE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CERISE_ONE_KIND.getKey()),
            GamblingSymbol.CITRON, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CITRON_ONE_KIND.getKey()),
            GamblingSymbol.RAISIN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.RAISIN_ONE_KIND.getKey()),
            GamblingSymbol.CLOCHE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.CLOCHE_ONE_KIND.getKey()),
            GamblingSymbol.ETOILE, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.ETOILE_ONE_KIND.getKey()),
            GamblingSymbol.DIAMOND, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.DIAMOND_ONE_KIND.getKey()),
            GamblingSymbol.SEVEN, ConfigRegistry.getConfigManager("gambling").getDouble(GamblingConfigKeys.SEVEN_ONE_KIND.getKey())
    );

    public static double calculateGain(GamblingSymbol[] symbols) {
        if (symbols == null || symbols.length != 3)
            return 0;

        if (symbols[0] == symbols[1] && symbols[1] == symbols[2]) {
            return SYMBOL_JACKPOT_MULTIPLIERS.getOrDefault(symbols[0], 0.0);
        }

        if (symbols[0] == symbols[1]) {
            return  SYMBOL_TWO_OF_KIND_MULTIPLIERS.getOrDefault(symbols[0], 0.0);
        }
        if (symbols[0] == symbols[2]) {
            return  SYMBOL_TWO_OF_KIND_MULTIPLIERS.getOrDefault(symbols[0], 0.0);
        }
        if (symbols[1] == symbols[2]) {
            return  SYMBOL_TWO_OF_KIND_MULTIPLIERS.getOrDefault(symbols[1], 0.0);
        }


        return SYMBOL_ONE_OF_KIND_MULTIPLIERS.getOrDefault(symbols[0], 0.0) +
                SYMBOL_ONE_OF_KIND_MULTIPLIERS.getOrDefault(symbols[1], 0.0) +
                SYMBOL_ONE_OF_KIND_MULTIPLIERS.getOrDefault(symbols[2], 0.0);
    }

    public static GamblingSymbol[] rollSymbols() {
        GamblingSymbol[] symbols = new GamblingSymbol[3];
        for (int i = 0; i < 3; i++) {
            double randomValue = Math.random();
            double cumulativeProbability = 0.0;
            for (Map.Entry<GamblingSymbol, Double> entry : SYMBOL_PROBABILITIES.entrySet()) {
                cumulativeProbability += entry.getValue();
                if (randomValue <= cumulativeProbability) {
                    symbols[i] = entry.getKey();
                    break;
                }
            }
        }
        return symbols;
    }

    /**
     * Évalue le RTP global de la slot machine.
     */
    public static double computeRTP() {
        double rtp = 0.0;
        GamblingSymbol[] symbols = GamblingSymbol.values();

        // Produit cartésien (itertools.product(symbols, repeat=3))
        for (GamblingSymbol a : symbols) {
            for (GamblingSymbol b : symbols) {
                for (GamblingSymbol c : symbols) {

                    double probability = SYMBOL_PROBABILITIES.get(a)
                            * SYMBOL_PROBABILITIES.get(b)
                            * SYMBOL_PROBABILITIES.get(c);

                    double gain = calculateGain(new GamblingSymbol[]{a,b,c});

                    rtp += probability * gain;
                }
            }
        }

        return rtp;
    }
}
