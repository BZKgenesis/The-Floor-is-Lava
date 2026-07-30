
import dearpygui.dearpygui as dpg
import numpy as np

from .config import (
    SYMBOLS,
    PROBABILITIES,
    JACKPOT_MULTIPLIERS,
    TWO_KIND_MULTIPLIERS,
    ONE_KIND_MULTIPLIERS,
)
################################################################################
#                              GUI SIMULATION                                  #
################################################################################


def build_gain_table(jackpot, two, one):
    size = len(SYMBOLS)
    table = np.empty(
        (size, size, size),
        dtype=np.float64
    )

    for a in range(size):
        for b in range(size):
            for c in range(size):

                if a == b == c:
                    gain = jackpot[a]
                elif a == b:
                    gain = two[a]
                elif b == c:
                    gain = two[b]
                elif a == c:
                    gain = two[a]
                else:
                    gain = one[a] + one[b] + one[c]
                table[a,b,c] = gain
    return table



def simulate_gui():

    # Paramètres simulation
    NB_PLAYERS = 1000
    NB_BETS = 100
    START_MONEY = 10000
    BET_RATIO = 0.1

    # Récupération valeurs GUI

    probabilities = np.array([
        dpg.get_value(f"prob_{i}")
        for i in range(len(SYMBOLS))
    ])

    # évite les erreurs si la somme n'est pas exactement 1
    probabilities /= probabilities.sum()


    jackpot = np.array([
        dpg.get_value(f"jackpot_{i}")
        for i in range(len(SYMBOLS))
    ])

    two = np.array([
        dpg.get_value(f"two_{i}")
        for i in range(len(SYMBOLS))
    ])

    one = np.array([
        dpg.get_value(f"one_{i}")
        for i in range(len(SYMBOLS))
    ])


    gain_table = build_gain_table(
        jackpot,
        two,
        one
    )


    # Simulation

    rng = np.random.default_rng()

    symbols = rng.choice(
        len(SYMBOLS),
        size=(NB_PLAYERS, NB_BETS, 3),
        p=probabilities
    )


    gains = gain_table[
        symbols[:,:,0],
        symbols[:,:,1],
        symbols[:,:,2]
    ]


    money = np.full(
        NB_PLAYERS,
        START_MONEY,
        dtype=np.float64
    )


    history = np.zeros(
        (NB_PLAYERS, NB_BETS+1)
    )

    history[:,0] = money


    for i in range(NB_BETS):

        money *= (
            1 - BET_RATIO
            + BET_RATIO * gains[:,i]
        )

        history[:,i+1] = money



    # Statistiques

    winners = np.count_nonzero(
        money > START_MONEY
    )

    result = (
        f"===== Simulation =====\n"
        f"Joueurs : {NB_PLAYERS}\n"
        f"Parties : {NB_BETS}\n\n"

        f"Joueurs gagnants : "
        f"{100*winners/NB_PLAYERS:.2f}%\n"

        f"Argent moyen final : "
        f"{money.mean():.2f}\n"

        f"Meilleur joueur : "
        f"{money.max():.2f}\n"

        f"Pire joueur : "
        f"{money.min():.2f}\n"

        f"Gain moyen par partie : "
        f"{gains.mean():.4f}x\n"
    )


    dpg.set_value(
        "simulation_output",
        result
    )
    dpg.fit_axis_data("x_axis")
    dpg.fit_axis_data("y_axis")


    # affichage graphique
    dpg.delete_item("plot_series")
    
    for i in range(min(20, NB_PLAYERS)):
        dpg.add_line_series(
            np.arange(NB_BETS + 1),
            history[i],
            parent="y_axis"
        )

################################################################################
#                                   GUI                                        #
################################################################################

def print_config():

    print("\n========== CONFIGURATION ==========")

    print("\n--- Probabilités ---")
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"prob_{i}")
        print(f"{name}: {value:.3f}")

    print("\n--- Jackpot ---")
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"jackpot_{i}")
        print(f"{name}: {value}")

    print("\n--- Deux identiques ---")
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"two_{i}")
        print(f"{name}: {value}")

    print("\n--- Un symbole ---")
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"one_{i}")
        print(f"{name}: {value}")

    print("==================================\n")


def launch_gui(_):

    dpg.create_context()

    with dpg.window(label="Gambling Config",
                    width=1200,
                    height=800,
                    tag="main_window",
                    no_close=True,
                    no_move=True,
                    no_collapse=True,
                    no_resize=True):

        dpg.add_text("Probabilités")

        for i, symbol in enumerate(SYMBOLS):
            dpg.add_slider_float(
                label=symbol,
                tag=f"prob_{i}",
                default_value=PROBABILITIES[i],
                min_value=0,
                max_value=1,
                format="%.3f",
                
            )


        dpg.add_separator()

        dpg.add_text("Jackpot (3 symboles)")

        for i, symbol in enumerate(SYMBOLS):
            dpg.add_input_float(
                label=symbol,
                tag=f"jackpot_{i}",
                default_value=JACKPOT_MULTIPLIERS[i]
            )


        dpg.add_separator()

        dpg.add_text("Deux identiques")

        for i, symbol in enumerate(SYMBOLS):
            dpg.add_input_float(
                label=symbol,
                tag=f"two_{i}",
                default_value=TWO_KIND_MULTIPLIERS[i]
            )


        dpg.add_separator()

        dpg.add_text("Un symbole")

        for i, symbol in enumerate(SYMBOLS):
            dpg.add_input_float(
                label=symbol,
                tag=f"one_{i}",
                default_value=ONE_KIND_MULTIPLIERS[i]
            )


        dpg.add_separator()

        dpg.add_button(
            label="Afficher configuration",
            callback=print_config
        )
        
        dpg.add_separator()

        dpg.add_button(
            label="Lancer simulation",
            callback=lambda: simulate_gui()
        )


        dpg.add_text(
            "",
            tag="simulation_output"
        )


        with dpg.plot(
            label="Evolution argent joueur",
            height=300,
            width=450
        ):

            dpg.add_plot_axis(
                dpg.mvXAxis,
                label="Parties",
                tag="x_axis"
            )

            dpg.add_plot_axis(
                dpg.mvYAxis,
                label="Argent",
                tag="y_axis"
            )

            dpg.add_line_series(
                [0],
                [10000],
                label="Joueur 1",
                parent="y_axis",
                tag="plot_series"
            )


    dpg.create_viewport(
        title="Gambling Editor",
        width=1200,
        height=800
    )
    dpg.set_primary_window(
        "main_window",
        True
    )

    dpg.setup_dearpygui()

    dpg.show_viewport()

    dpg.start_dearpygui()

    dpg.destroy_context()
