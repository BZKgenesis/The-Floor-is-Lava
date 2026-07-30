
import dearpygui.dearpygui as dpg
import numpy as np

from .config import (
    SYMBOLS,
    PROBABILITIES,
    JACKPOT_MULTIPLIERS,
    TWO_KIND_MULTIPLIERS,
    ONE_KIND_MULTIPLIERS,
    save,
    load
)
################################################################################
#                              GUI SIMULATION                                  #
################################################################################

def compute_rtp_gui():

    probabilities = np.array(get_proba(), dtype=np.float64)
    probabilities /= probabilities.sum()

    jackpot = np.array(get_jackpot(), dtype=np.float64)
    two = np.array(get_two_kind(), dtype=np.float64)
    one = np.array(get_one_kind(), dtype=np.float64)

    gain_table = build_gain_table(jackpot, two, one)

    probs = (
        probabilities[:, None, None]
        * probabilities[None, :, None]
        * probabilities[None, None, :]
    )

    rtp = np.sum(probs * gain_table)

    dpg.set_value(
        "rtp_text",
        f"RTP : {rtp*100:.3f}%\n"
        f"Perte moyenne : {(1-rtp)*100:.3f}%"
    )

def on_parameter_changed(sender, app_data, user_data):
    compute_rtp_gui()

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

def set_values_from_config():
    for i in range(len(SYMBOLS)):
        dpg.set_value(f"prob_{i}", PROBABILITIES[i])
    for i in range(len(SYMBOLS)):
        dpg.set_value(f"jackpot_{i}", JACKPOT_MULTIPLIERS[i])
    for i in range(len(SYMBOLS)):
        dpg.set_value(f"two_{i}", TWO_KIND_MULTIPLIERS[i])
    for i in range(len(SYMBOLS)):
        dpg.set_value(f"one_{i}", ONE_KIND_MULTIPLIERS[i])

def load_config(file_path):
    global PROBABILITIES,JACKPOT_MULTIPLIERS, TWO_KIND_MULTIPLIERS, ONE_KIND_MULTIPLIERS
    PROBABILITIES, JACKPOT_MULTIPLIERS, TWO_KIND_MULTIPLIERS, ONE_KIND_MULTIPLIERS = (load(file_path))
    set_values_from_config()
    compute_rtp_gui()

def simulate_gui():
    # Paramètres simulation
    NB_PLAYERS = dpg.get_value("nb_players")
    NB_BETS = dpg.get_value("nb_bet")
    START_MONEY = dpg.get_value("start_money")
    BET_RATIO = dpg.get_value("bet_ratio")

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
    dpg.delete_item("y_axis", children_only=True)
    
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


def get_proba():
    liste =[]
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"prob_{i}")
        liste.append(value)
    return liste
def get_jackpot():
    liste =[]
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"jackpot_{i}")
        liste.append(value)
    return liste
def get_two_kind():
    liste =[]
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"two_{i}")
        liste.append(value)
    return liste
def get_one_kind():
    liste =[]
    for i, name in enumerate(SYMBOLS):
        value = dpg.get_value(f"one_{i}")
        liste.append(value)
    return liste
    

def open_file_dialog():
    dpg.show_item("file_dialog")
    dpg.hide_item("warning_popup")


def ask_open_file():
    dpg.show_item("warning_popup")
    width = dpg.get_viewport_width()
    height = dpg.get_viewport_height()

    dpg.set_item_pos(
        "warning_popup",
        [width // 2 - 150, height // 2 - 75]
    )


def cancel_file_dialog():
    dpg.hide_item("warning_popup")

def launch_gui(_):
    dpg.create_context()
    # Popup d'avertissement
    with dpg.window(
        label="Avertissement",
        modal=True,
        show=False,
        tag="warning_popup",
        width=300,
        height=150,
    ):
        dpg.add_text("Le chargement va remplacer\nles paramètres actuels.")
        
        with dpg.group(horizontal=True):
            dpg.add_button(
                label="Continuer",
                callback=open_file_dialog
            )
            dpg.add_button(
                label="Annuler",
                callback=cancel_file_dialog
            )
    
    with dpg.window(label="Gambling Config",
                    width=500,
                    height=1000,
                    tag="main_window",
                    no_close=True):
        dpg.add_text("Probabilités")
        for i, symbol in enumerate(SYMBOLS):
            dpg.add_slider_float(
                label=symbol,
                tag=f"prob_{i}",
                default_value=PROBABILITIES[i],
                min_value=0,
                max_value=1,
                format="%.3f",
                callback=on_parameter_changed
            )
        dpg.add_separator()
        dpg.add_text("Jackpot (3 symboles)")
        for i, symbol in enumerate(SYMBOLS):
            dpg.add_input_float(
                label=symbol,
                tag=f"jackpot_{i}",
                default_value=JACKPOT_MULTIPLIERS[i],
                callback=on_parameter_changed
            )
        dpg.add_separator()
        dpg.add_text("Deux identiques")
        for i, symbol in enumerate(SYMBOLS):
            dpg.add_input_float(
                label=symbol,
                tag=f"two_{i}",
                default_value=TWO_KIND_MULTIPLIERS[i],
                callback=on_parameter_changed
            )
        dpg.add_separator()
        dpg.add_text("Un symbole")
        for i, symbol in enumerate(SYMBOLS):
            dpg.add_input_float(
                label=symbol,
                tag=f"one_{i}",
                default_value=ONE_KIND_MULTIPLIERS[i],
                callback=on_parameter_changed
            )
        dpg.add_separator()
        with dpg.file_dialog(
            directory_selector=False,
            show=False,
            callback=lambda _,app_data: load_config(app_data["file_path_name"]),
            tag="file_dialog",
            width=700,
            height=400,
        ):
            dpg.add_file_extension("Config files (*.yml *.yaml){.yml,.yaml}")
        
        with dpg.file_dialog(
            show=False,
            callback=lambda _, app_data: save(app_data["file_path_name"], get_proba(), get_jackpot(), get_two_kind(), get_one_kind()),
            tag="save_dialog",
            width=700,
            height=400,
            default_filename="gambling",
        ):
            dpg.add_file_extension(".yml",)
            dpg.add_file_extension(".yaml")
        
        dpg.add_button(
            label="Charger",
            callback=ask_open_file
        )
        dpg.add_button(
            label="Sauvegarder",
            callback=lambda: dpg.show_item("save_dialog")
        )
        dpg.add_button(
            label="Afficher configuration",
            callback=print_config
        )
    with dpg.window(label="Simulation",
                    width=500,
                    height=600,
                    pos=(500,0),
                    tag="sim_window",
                    no_close=True):
        dpg.add_input_int(
            label="Nombre joueurs",
            tag="nb_players",
            default_value=1000,
        )
        dpg.add_input_int(
            label="Nombre parie d'affilé",
            tag="nb_bet",
            default_value=100,
        )
        dpg.add_input_int(
            label="Budget de départ",
            tag="start_money",
            default_value=10_000,
        )
        dpg.add_slider_float(
            label="Taux parie",
            tag="bet_ratio",
            default_value=0.1,
            min_value=0.0,
            max_value=1.0,
        )
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
    with dpg.window(label="RTP",
                    width=200,
                    height=50,
                    pos=(500,600),
                    tag="rtp_window",
                    no_close=True):

        dpg.add_text(
            "",
            tag="rtp_text"
        )
    dpg.create_viewport(
        title="Gambling Editor",
        width=1000,
        height=900
    )

    dpg.setup_dearpygui()
    dpg.show_viewport()
    compute_rtp_gui()
    dpg.start_dearpygui()
    dpg.destroy_context()
