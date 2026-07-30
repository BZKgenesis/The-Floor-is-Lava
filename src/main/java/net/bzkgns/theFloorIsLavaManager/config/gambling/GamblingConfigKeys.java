package net.bzkgns.theFloorIsLavaManager.config.gambling;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;

public final class GamblingConfigKeys {

    private GamblingConfigKeys() {
    }
    public static final ConfigKey<GamblingConfig, Double> CERISE_PROBABILITY = new ConfigKey<>("cerise-probability","",GamblingConfig::getCeriseProbability,GamblingConfig::setCeriseProbability,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CITRON_PROBABILITY = new ConfigKey<>("citron-probability","",GamblingConfig::getCitronProbability,GamblingConfig::setCitronProbability,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> RAISIN_PROBABILITY = new ConfigKey<>("raisin-probability","",GamblingConfig::getRaisinProbability,GamblingConfig::setRaisinProbability,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CLOCHE_PROBABILITY = new ConfigKey<>("cloche-probability","",GamblingConfig::getClocheProbability,GamblingConfig::setClocheProbability,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> ETOILE_PROBABILITY = new ConfigKey<>("etoile-probability","",GamblingConfig::getEtoileProbability,GamblingConfig::setEtoileProbability,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> DIAMOND_PROBABILITY = new ConfigKey<>("diamond-probability","",GamblingConfig::getDiamondProbability,GamblingConfig::setDiamondProbability,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> SEVEN_PROBABILITY = new ConfigKey<>("seven-probability","",GamblingConfig::getSevenProbability,GamblingConfig::setSevenProbability,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CERISE_JACKPOT = new ConfigKey<>("cerise-jackpot","",GamblingConfig::getCeriseJackpot,GamblingConfig::setCeriseJackpot,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CITRON_JACKPOT = new ConfigKey<>("citron-jackpot","",GamblingConfig::getCitronJackpot,GamblingConfig::setCitronJackpot,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> RAISIN_JACKPOT = new ConfigKey<>("raisin-jackpot","",GamblingConfig::getRaisinJackpot,GamblingConfig::setRaisinJackpot,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CLOCHE_JACKPOT = new ConfigKey<>("cloche-jackpot","",GamblingConfig::getClocheJackpot,GamblingConfig::setClocheJackpot,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> ETOILE_JACKPOT = new ConfigKey<>("etoile-jackpot","",GamblingConfig::getEtoileJackpot,GamblingConfig::setEtoileJackpot,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> DIAMOND_JACKPOT = new ConfigKey<>("diamond-jackpot","",GamblingConfig::getDiamondJackpot,GamblingConfig::setDiamondJackpot,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> SEVEN_JACKPOT = new ConfigKey<>("seven-jackpot","",GamblingConfig::getSevenJackpot,GamblingConfig::setSevenJackpot,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CERISE_TWO_KIND = new ConfigKey<>("cerise-two-kind","",GamblingConfig::getCeriseTwoKind,GamblingConfig::setCeriseTwoKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CITRON_TWO_KIND = new ConfigKey<>("citron-two-kind","",GamblingConfig::getCitronTwoKind,GamblingConfig::setCitronTwoKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> RAISIN_TWO_KIND = new ConfigKey<>("raisin-two-kind","",GamblingConfig::getRaisinTwoKind,GamblingConfig::setRaisinTwoKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CLOCHE_TWO_KIND = new ConfigKey<>("cloche-two-kind","",GamblingConfig::getClocheTwoKind,GamblingConfig::setClocheTwoKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> ETOILE_TWO_KIND = new ConfigKey<>("etoile-two-kind","",GamblingConfig::getEtoileTwoKind,GamblingConfig::setEtoileTwoKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> DIAMOND_TWO_KIND = new ConfigKey<>("diamond-two-kind","",GamblingConfig::getDiamondTwoKind,GamblingConfig::setDiamondTwoKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> SEVEN_TWO_KIND = new ConfigKey<>("seven-two-kind","",GamblingConfig::getSevenTwoKind,GamblingConfig::setSevenTwoKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CERISE_ONE_KIND = new ConfigKey<>("cerise-one-kind","",GamblingConfig::getCeriseOneKind,GamblingConfig::setCeriseOneKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CITRON_ONE_KIND = new ConfigKey<>("citron-one-kind","",GamblingConfig::getCitronOneKind,GamblingConfig::setCitronOneKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> RAISIN_ONE_KIND = new ConfigKey<>("raisin-one-kind","",GamblingConfig::getRaisinOneKind,GamblingConfig::setRaisinOneKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> CLOCHE_ONE_KIND = new ConfigKey<>("cloche-one-kind","",GamblingConfig::getClocheOneKind,GamblingConfig::setClocheOneKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> ETOILE_ONE_KIND = new ConfigKey<>("etoile-one-kind","",GamblingConfig::getEtoileOneKind,GamblingConfig::setEtoileOneKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> DIAMOND_ONE_KIND = new ConfigKey<>("diamond-one-kind","",GamblingConfig::getDiamondOneKind,GamblingConfig::setDiamondOneKind,Double::parseDouble);
    public static final ConfigKey<GamblingConfig, Double> SEVEN_ONE_KIND = new ConfigKey<>("seven-one-kind","",GamblingConfig::getSevenOneKind,GamblingConfig::setSevenOneKind,Double::parseDouble);

}