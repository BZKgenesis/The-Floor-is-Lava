package net.bzkgns.theFloorIsLava.config.gambling;

import net.bzkgns.theFloorIsLava.config.ConfigKey;
import net.bzkgns.theFloorIsLava.config.ConfigSection;

import java.util.List;

public class GamblingConfig  implements ConfigSection<GamblingConfig> {

    private double ceriseProbability = 0.3;
    private double citronProbability = 0.24;
    private double raisinProbability = 0.18;
    private double clocheProbability = 0.12;
    private double etoileProbability = 0.09;
    private double diamondProbability = 0.05;
    private double sevenProbability = 0.02;

    private double ceriseJackpot = 3;
    private double citronJackpot = 6;
    private double raisinJackpot = 10;
    private double clocheJackpot = 15;
    private double etoileJackpot = 25;
    private double diamondJackpot = 50;
    private double sevenJackpot = 100;

    private double ceriseTwoKind = 1.0;
    private double citronTwoKind = 1.125;
    private double raisinTwoKind = 1.25;
    private double clocheTwoKind = 1.5;
    private double etoileTwoKind = 5;
    private double diamondTwoKind = 10;
    private double sevenTwoKind = 20;

    private double ceriseOneKind = 0.05;
    private double citronOneKind = 0.075;
    private double raisinOneKind = 0.1;
    private double clocheOneKind = 0.15;
    private double etoileOneKind = 0.2;
    private double diamondOneKind = 0.35;
    private double sevenOneKind = 0.5;

    private static final List<ConfigKey<GamblingConfig, ?>> KEYS = List.of(
            GamblingConfigKeys.CERISE_PROBABILITY,
            GamblingConfigKeys.CITRON_PROBABILITY,
            GamblingConfigKeys.RAISIN_PROBABILITY,
            GamblingConfigKeys.CLOCHE_PROBABILITY,
            GamblingConfigKeys.ETOILE_PROBABILITY,
            GamblingConfigKeys.DIAMOND_PROBABILITY,
            GamblingConfigKeys.SEVEN_PROBABILITY,

            GamblingConfigKeys.CERISE_JACKPOT,
            GamblingConfigKeys.CITRON_JACKPOT,
            GamblingConfigKeys.RAISIN_JACKPOT,
            GamblingConfigKeys.CLOCHE_JACKPOT,
            GamblingConfigKeys.ETOILE_JACKPOT,
            GamblingConfigKeys.DIAMOND_JACKPOT,
            GamblingConfigKeys.SEVEN_JACKPOT,

            GamblingConfigKeys.CERISE_TWO_KIND,
            GamblingConfigKeys.CITRON_TWO_KIND,
            GamblingConfigKeys.RAISIN_TWO_KIND,
            GamblingConfigKeys.CLOCHE_TWO_KIND,
            GamblingConfigKeys.ETOILE_TWO_KIND,
            GamblingConfigKeys.DIAMOND_TWO_KIND,
            GamblingConfigKeys.SEVEN_TWO_KIND,

            GamblingConfigKeys.CERISE_ONE_KIND,
            GamblingConfigKeys.CITRON_ONE_KIND,
            GamblingConfigKeys.RAISIN_ONE_KIND,
            GamblingConfigKeys.CLOCHE_ONE_KIND,
            GamblingConfigKeys.ETOILE_ONE_KIND,
            GamblingConfigKeys.DIAMOND_ONE_KIND,
            GamblingConfigKeys.SEVEN_ONE_KIND
    );

    // --- Getters / setters ---

    public void setCeriseProbability(double ceriseProbability) {this.ceriseProbability = ceriseProbability;}
    public double getCeriseProbability() {return ceriseProbability;}

    public void setCitronProbability(double citronProbability) {this.citronProbability = citronProbability;}
    public double getCitronProbability() {return citronProbability;}

    public void setRaisinProbability(double raisinProbability) {this.raisinProbability = raisinProbability;}
    public double getRaisinProbability() {return raisinProbability;}

    public void setClocheProbability(double clocheProbability) {this.clocheProbability = clocheProbability;}
    public double getClocheProbability() {return clocheProbability;}

    public void setEtoileProbability(double etoileProbability) {this.etoileProbability = etoileProbability;}
    public double getEtoileProbability() {return etoileProbability;}

    public void setDiamondProbability(double diamondProbability) {this.diamondProbability = diamondProbability;}
    public double getDiamondProbability() {return diamondProbability;}

    public void setSevenProbability(double sevenProbability) {this.sevenProbability = sevenProbability;}
    public double getSevenProbability() {return sevenProbability;}


    public void setCeriseJackpot(double ceriseJackpot) {this.ceriseJackpot = ceriseJackpot;}
    public double getCeriseJackpot() {return ceriseJackpot;}

    public void setCitronJackpot(double citronJackpot) {this.citronJackpot = citronJackpot;}
    public double getCitronJackpot() {return citronJackpot;}

    public void setRaisinJackpot(double raisinJackpot) {this.raisinJackpot = raisinJackpot;}
    public double getRaisinJackpot() {return raisinJackpot;}

    public void setClocheJackpot(double clocheJackpot) {this.clocheJackpot = clocheJackpot;}
    public double getClocheJackpot() {return clocheJackpot;}

    public void setEtoileJackpot(double etoileJackpot) {this.etoileJackpot = etoileJackpot;}
    public double getEtoileJackpot() {return etoileJackpot;}

    public void setDiamondJackpot(double diamondJackpot) {this.diamondJackpot = diamondJackpot;}
    public double getDiamondJackpot() {return diamondJackpot;}

    public void setSevenJackpot(double sevenJackpot) {this.sevenJackpot = sevenJackpot;}
    public double getSevenJackpot() {return sevenJackpot;}


    public void setCeriseTwoKind(double ceriseTwoKind) {this.ceriseTwoKind = ceriseTwoKind;}
    public double getCeriseTwoKind() {return ceriseTwoKind;}

    public void setCitronTwoKind(double citronTwoKind) {this.citronTwoKind = citronTwoKind;}
    public double getCitronTwoKind() {return citronTwoKind;}

    public void setRaisinTwoKind(double raisinTwoKind) {this.raisinTwoKind = raisinTwoKind;}
    public double getRaisinTwoKind() {return raisinTwoKind;}

    public void setClocheTwoKind(double clocheTwoKind) {this.clocheTwoKind = clocheTwoKind;}
    public double getClocheTwoKind() {return clocheTwoKind;}

    public void setEtoileTwoKind(double etoileTwoKind) {this.etoileTwoKind = etoileTwoKind;}
    public double getEtoileTwoKind() {return etoileTwoKind;}

    public void setDiamondTwoKind(double diamondTwoKind) {this.diamondTwoKind = diamondTwoKind;}
    public double getDiamondTwoKind() {return diamondTwoKind;}

    public void setSevenTwoKind(double sevenTwoKind) {this.sevenTwoKind = sevenTwoKind;}
    public double getSevenTwoKind() {return sevenTwoKind;}


    public void setCeriseOneKind(double ceriseOneKind) {this.ceriseOneKind = ceriseOneKind;}
    public double getCeriseOneKind() {return ceriseOneKind;}

    public void setCitronOneKind(double citronOneKind) {this.citronOneKind = citronOneKind;}
    public double getCitronOneKind() {return citronOneKind;}

    public void setRaisinOneKind(double raisinOneKind) {this.raisinOneKind = raisinOneKind;}
    public double getRaisinOneKind() {return raisinOneKind;}

    public void setClocheOneKind(double clocheOneKind) {this.clocheOneKind = clocheOneKind;}
    public double getClocheOneKind() {return clocheOneKind;}

    public void setEtoileOneKind(double etoileOneKind) {this.etoileOneKind = etoileOneKind;}
    public double getEtoileOneKind() {return etoileOneKind;}

    public void setDiamondOneKind(double diamondOneKind) {this.diamondOneKind = diamondOneKind;}
    public double getDiamondOneKind() {return diamondOneKind;}

    public void setSevenOneKind(double sevenOneKind) {this.sevenOneKind = sevenOneKind;}
    public double getSevenOneKind() {return sevenOneKind;}


    @Override
    public String getName() {
        return "gambling";
    }

    @Override
    public List<ConfigKey<GamblingConfig,?>> getKeys() {
        return List.copyOf(KEYS);
    }

    @SuppressWarnings("unused")
    public Object getValueForKey(ConfigKey<GamblingConfig, ?> key) {
        return key.get(this);
    }
}