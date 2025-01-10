package dev.lbuddyboy.legend.util;

public class LevelUtil {

    public static double getExperience(long level, double initialExperience, double incrementFactor) {
        if (level <= 0) return 0;

        return (long) (initialExperience * (incrementFactor * level));
    }

    public static double getPercent(long level, double experience, double initialExperience, double incrementFactor) {
        if (experience <= 0) return 0D;

        double nextLevelExp = LevelUtil.getExperience(level + 1, initialExperience, incrementFactor);
        double progress = experience / nextLevelExp;
        double roundedProgress = Math.round(progress * 10000.0) / 100.0;

        if (roundedProgress < 0) roundedProgress = 0;

        return roundedProgress;
    }

}
