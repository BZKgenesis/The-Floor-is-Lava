from itertools import product

# ==========================
# Probabilités des symboles
# ==========================

SYMBOL_PROBABILITIES = {
    "CERISE": 0.30,
    "CITRON": 0.24,
    "RAISIN": 0.18,
    "CLOCHE": 0.12,
    "ETOILE": 0.09,
    "DIAMANT": 0.05,
    "SEVEN": 0.02,
}

# ==========================
# Multiplicateurs
# ==========================

PAYOUTS = {
    "CERISE": 3.0,
    "CITRON": 6.0,
    "RAISIN": 10.0,
    "CLOCHE": 15.0,
    "ETOILE": 25.0,
    "DIAMANT": 50.0,
    "SEVEN": 100.0,
}

TWO_OF_A_KIND = {
    "CERISE": 0.5,
    "CITRON": 0.75,
    "RAISIN": 1.25,
    "CLOCHE": 1.5,
    "ETOILE": 5.0,
    "DIAMANT": 10.0,
    "SEVEN": 20.0,
}

ONE_OF_A_KIN = {
    "CERISE": 0.0,
    "CITRON": 0.0,
    "RAISIN": 0.1,
    "CLOCHE": 0.15,
    "ETOILE": 0.2,
    "DIAMANT": 0.25,
    "SEVEN": 0.5,
}


# ==========================
# Calcul du gain
# ==========================

def calculate_gain(a, b, c):
    # Trois identiques
    if a == b == c:
        return PAYOUTS[a]

    # Exactement deux identiques
    if a == b:
        return TWO_OF_A_KIND[a]
    if b == c:
        return TWO_OF_A_KIND[b]
    if a == c:
        return TWO_OF_A_KIND[a]

    return ONE_OF_A_KIN[a] + ONE_OF_A_KIN[b] + ONE_OF_A_KIN[c]


# ==========================
# Calcul du RTP
# ==========================

rtp = 0.0

symbols = list(SYMBOL_PROBABILITIES.keys())

for a, b, c in product(symbols, repeat=3):

    probability = (
        SYMBOL_PROBABILITIES[a]
        * SYMBOL_PROBABILITIES[b]
        * SYMBOL_PROBABILITIES[c]
    )

    gain = calculate_gain(a, b, c)

    rtp += probability * gain


print(f"RTP : {rtp:.6f}")
print(f"RTP : {rtp*100:.2f}%")
print(f"Perte moyenne du joueur : {(1-rtp)*100:.2f}%")