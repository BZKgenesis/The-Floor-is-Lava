
import numpy as np
import matplotlib.pyplot as plt
from .config import (
    SYMBOLS,
    PROBABILITIES,
    JACKPOT_MULTIPLIERS,
    TWO_KIND_MULTIPLIERS,
    ONE_KIND_MULTIPLIERS,
    GAIN_TABLE,
    CDF
    
)
################################################################################
#                              CALCUL RTP                                      #
################################################################################


def compute_rtp(_):

    probs = (
        PROBABILITIES[:, None, None]
        * PROBABILITIES[None, :, None]
        * PROBABILITIES[None, None, :]
    )

    rtp = np.sum(probs * GAIN_TABLE)

    print(f"RTP : {rtp:.6f}")
    print(f"RTP : {rtp*100:.2f}%")
    print(f"Perte joueur : {(1-rtp)*100:.2f}%")

################################################################################
#                              SIMULATION                                      #
################################################################################


def simulate(_):

    START_MONEY = 10_000
    NB_PLAYERS = 10000
    NB_BETS = 1000
    BET_RATIO = 0.10

    rng = np.random.default_rng()

    random_values = rng.random((NB_PLAYERS, NB_BETS, 3))

    symbols = np.searchsorted(CDF, random_values)

    gains = GAIN_TABLE[
        symbols[:, :, 0],
        symbols[:, :, 1],
        symbols[:, :, 2]
    ]

    money = np.full(NB_PLAYERS, START_MONEY, dtype=np.float64)

    history = np.empty((NB_PLAYERS, NB_BETS + 1))
    history[:, 0] = money

    for i in range(NB_BETS):
        money *= (1 - BET_RATIO + BET_RATIO * gains[:, i])
        history[:, i + 1] = money

    winners = np.count_nonzero(money > START_MONEY)

    print(f"Joueurs gagnants : {100*winners/NB_PLAYERS:.2f}%")

    plt.plot(history.T)
    plt.xlabel("Nombre de paris")
    plt.ylabel("Argent")
    plt.show()

