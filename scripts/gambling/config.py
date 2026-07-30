import numpy as np
################################################################################
#                                CONFIGURATION                                 #
################################################################################
#
# Toutes les valeurs ci-dessous sont destinées à être modifiées à la main.
#
# PROBABILITIES            : probabilité d'apparition du symbole (la somme doit faire 1.0)
#
# JACKPOT_MULTIPLIERS      : gain pour 3 symboles identiques
#
# TWO_KIND_MULTIPLIERS     : gain pour exactement 2 symboles identiques
#
# ONE_KIND_MULTIPLIERS     : gain lorsqu'un seul symbole apparaît
#
################################################################################

SYMBOLS = [
    "Cerise",
    "Citron",
    "Raisin",
    "Cloche",
    "Étoile",
    "Diamant",
    "Seven",
]

PROBABILITIES = [
    0.30,
    0.24,
    0.18,
    0.12,
    0.09,
    0.05,
    0.02,
]

JACKPOT_MULTIPLIERS = [
    3, 6, 10, 15, 25, 50, 100
]

TWO_KIND_MULTIPLIERS = [
    0.5, 0.75, 1.25, 1.5, 5, 10, 20
]

ONE_KIND_MULTIPLIERS = [
    0, 0, 0.1, 0.15, 0.2, 0.25, 0.5
]

if len({
    len(SYMBOLS),
    len(PROBABILITIES),
    len(JACKPOT_MULTIPLIERS),
    len(TWO_KIND_MULTIPLIERS),
    len(ONE_KIND_MULTIPLIERS),
}) != 1:
    raise ValueError("Toutes les listes doivent avoir la même taille.")

if not np.isclose(sum(PROBABILITIES), 1.0):
    raise ValueError(
        f"La somme des probabilités vaut {PROBABILITIES.sum():.6f}, elle doit être égale à 1."
    )
NB_SYMBOLS = len(SYMBOLS)

PROBABILITIES = np.asarray(PROBABILITIES, dtype=np.float64)
PAYOUTS = np.asarray(JACKPOT_MULTIPLIERS, dtype=np.float64)
TWO = np.asarray(TWO_KIND_MULTIPLIERS, dtype=np.float64)
ONE = np.asarray(ONE_KIND_MULTIPLIERS, dtype=np.float64)

CDF = np.cumsum(PROBABILITIES)

GAIN_TABLE = np.empty(
    (NB_SYMBOLS, NB_SYMBOLS, NB_SYMBOLS),
    dtype=np.float64
)

for a in range(NB_SYMBOLS):
    for b in range(NB_SYMBOLS):
        for c in range(NB_SYMBOLS):
            if a == b == c: gain = PAYOUTS[a]
            elif a == b: gain = TWO[a]
            elif b == c: gain = TWO[b]
            elif a == c: gain = TWO[a]
            else: gain = ONE[a] + ONE[b] + ONE[c]
            GAIN_TABLE[a, b, c] = gain

