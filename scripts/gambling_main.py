from __future__ import annotations
import argparse
from .gambling.gambling_sim import compute_rtp,simulate
from .gambling.gambling_sim_gui import launch_gui


################################################################################
#                       COMPILATION DES DONNÉES                                #
################################################################################


################################################################################
#                                   MAIN                                       #
################################################################################

def rtp_command(_):
    print(f"RTP {compute_rtp()}")

def simulation_command(_):
    result = simulate()
    print(f"%winner {result["winners_ratio"]:.3f}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()

    sub = parser.add_subparsers(required=True)

    rtp = sub.add_parser("rtp")
    rtp.set_defaults(func=rtp_command)

    sim = sub.add_parser("simulation")
    sim.set_defaults(func=simulation_command)

    parser_gui = sub.add_parser("gui")
    parser_gui.set_defaults(func=launch_gui)

    args = parser.parse_args()

    args.func(args)

"""
========== CONFIGURATION ==========

--- Probabilités ---
Cerise: 0.300
Citron: 0.240
Raisin: 0.180
Cloche: 0.120
Étoile: 0.090
Diamant: 0.050
Seven: 0.020

--- Jackpot ---
Cerise: 3.0
Citron: 6.0
Raisin: 10.0
Cloche: 15.0
Étoile: 25.0
Diamant: 50.0
Seven: 100.0

--- Deux identiques ---
Cerise: 0.7000000476837158
Citron: 0.9500000476837158
Raisin: 1.25
Cloche: 1.5
Étoile: 5.0
Diamant: 10.0
Seven: 20.0

--- Un symbole ---
Cerise: 0.10000000149011612
Citron: 0.10000000149011612
Raisin: 0.20000000298023224
Cloche: 0.25
Étoile: 0.30000001192092896
Diamant: 0.3499999940395355
Seven: 0.5
==================================
"""
