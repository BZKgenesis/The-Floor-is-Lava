package net.bzkgns.theFloorIsLavaManager.items.abilities.gambling;

import java.util.Map;

public class GamblingEngine {
    private static final Map<GamblingSymbol, Double> SYMBOL_PROBABILITIES = Map.of(
            GamblingSymbol.CERISE, 0.30,
            GamblingSymbol.CITRON, 0.24,
            GamblingSymbol.RAISIN, 0.18,
            GamblingSymbol.CLOCHE, 0.12,
            GamblingSymbol.ETOILE, 0.09,
            GamblingSymbol.DIAMANT, 0.05,
            GamblingSymbol.SEVEN, 0.02
    );
    private static final Map<GamblingSymbol, Float> SYMBOL_JACKPOT_MULTIPLIERS = Map.of(
            GamblingSymbol.CERISE, 3.0f,
            GamblingSymbol.CITRON, 6.0f,
            GamblingSymbol.RAISIN, 10.0f,
            GamblingSymbol.CLOCHE, 15.0f,
            GamblingSymbol.ETOILE, 25.0f,
            GamblingSymbol.DIAMANT, 50.0f,
            GamblingSymbol.SEVEN, 100.0f
    );
    private static final Map<GamblingSymbol, Float> SYMBOL_TWO_OF_KIND_MULTIPLIERS = Map.of(
            GamblingSymbol.CERISE, 0.5f,
            GamblingSymbol.CITRON, 0.75f,
            GamblingSymbol.RAISIN, 1.25f,
            GamblingSymbol.CLOCHE, 1.5f,
            GamblingSymbol.ETOILE, 5.0f,
            GamblingSymbol.DIAMANT, 10.0f,
            GamblingSymbol.SEVEN, 20.0f
    );
    private static final Map<GamblingSymbol, Float> SYMBOL_ONE_OF_KIND_MULTIPLIERS = Map.of(
            GamblingSymbol.CERISE, 0.0f,
            GamblingSymbol.CITRON, 0.0f,
            GamblingSymbol.RAISIN, 0.1f,
            GamblingSymbol.CLOCHE, 0.15f,
            GamblingSymbol.ETOILE, 0.2f,
            GamblingSymbol.DIAMANT, 0.25f,
            GamblingSymbol.SEVEN, 0.5f
    );

    public static float calculateGain(GamblingSymbol[] symbols) {
        if (symbols == null || symbols.length != 3)
            return 0;

        if (symbols[0] == symbols[1] && symbols[1] == symbols[2]) {
            return SYMBOL_JACKPOT_MULTIPLIERS.getOrDefault(symbols[0], 0f);
        }

        if (symbols[0] == symbols[1]) {
            return  SYMBOL_TWO_OF_KIND_MULTIPLIERS.getOrDefault(symbols[0], 0f);
        }
        if (symbols[0] == symbols[2]) {
            return  SYMBOL_TWO_OF_KIND_MULTIPLIERS.getOrDefault(symbols[0], 0f);
        }
        if (symbols[1] == symbols[2]) {
            return  SYMBOL_TWO_OF_KIND_MULTIPLIERS.getOrDefault(symbols[1], 0f);
        }


        return SYMBOL_ONE_OF_KIND_MULTIPLIERS.getOrDefault(symbols[0], 0f) +
                SYMBOL_ONE_OF_KIND_MULTIPLIERS.getOrDefault(symbols[1], 0f) +
                SYMBOL_ONE_OF_KIND_MULTIPLIERS.getOrDefault(symbols[2], 0f);
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
