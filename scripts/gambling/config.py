import numpy as np
import yaml
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

# Mapping entre le nom d'affichage dans SYMBOLS et le préfixe dans le YAML
SYMBOL_MAPPING = {
    "Cerise": "cerise",
    "Citron": "citron",
    "Raisin": "raisin",
    "Cloche": "cloche",
    "Étoile": "etoile",
    "Diamant": "diamond",
    "Seven": "seven",
}
import os

def load(filepath: str = "config.yaml"):
    """Charge les paramètres depuis un fichier YAML.

    Retourne:
        PROBABILITIES, JACKPOT_MULTIPLIERS, TWO_KIND_MULTIPLIERS, ONE_KIND_MULTIPLIERS
    """
    print("📁 Python cherche dans le dossier :", os.getcwd())
    with open(filepath, "r", encoding="utf-8") as f:
        data = yaml.safe_load(f)

    gambling_data = data.get("gambling", {})

    probabilities = []
    jackpot_multipliers = []
    two_kind_multipliers = []
    one_kind_multipliers = []

    for symbol in SYMBOLS:
        key = SYMBOL_MAPPING[symbol]

        probabilities.append(float(gambling_data[f"{key}-probability"]))
        jackpot_multipliers.append(float(gambling_data[f"{key}-jackpot"]))
        two_kind_multipliers.append(float(gambling_data[f"{key}-two-kind"]))
        one_kind_multipliers.append(float(gambling_data[f"{key}-one-kind"]))

    return probabilities, jackpot_multipliers, two_kind_multipliers, one_kind_multipliers


def save(
    filepath: str,
    probabilities: list,
    jackpot_multipliers: list,
    two_kind_multipliers: list,
    one_kind_multipliers: list,
):
    """Sauvegarde les variables dans un fichier YAML au format structuré."""
    gambling_data = {}

    for i, symbol in enumerate(SYMBOLS):
        key = SYMBOL_MAPPING[symbol]
        gambling_data[f"{key}-probability"] = float(probabilities[i])
        gambling_data[f"{key}-jackpot"] = float(jackpot_multipliers[i])
        gambling_data[f"{key}-two-kind"] = float(two_kind_multipliers[i])
        gambling_data[f"{key}-one-kind"] = float(one_kind_multipliers[i])

    full_data = {"gambling": gambling_data}

    with open(filepath, "w", encoding="utf-8") as f:
        yaml.dump(full_data, f, sort_keys=False, allow_unicode=True)


# ==============================================================================
# EXECUTION / CHARGEMENT DES DONNÉES
# ==============================================================================

# Charger les données depuis le fichier YAML
PROBABILITIES, JACKPOT_MULTIPLIERS, TWO_KIND_MULTIPLIERS, ONE_KIND_MULTIPLIERS = (
    load("scripts/gambling/default-gambling.yml")
)

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

