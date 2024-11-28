import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ClaimTest {

    @AllArgsConstructor
    @Getter
    private static class Claim {

        private UUID uuid;
        private int x1;
        private int z1;

        private int x2;
        private int z2;

        public boolean contains(int x, int z) {
            return x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2;
        }

    }

    public static List<Claim> CLAIMS = new ArrayList<>(){{
        for (int i = 0; i < 1000; i++) {
            add(new ClaimTest.Claim(UUID.randomUUID(), ThreadLocalRandom.current().nextInt(1000, 1500), ThreadLocalRandom.current().nextInt(1000, 1500), ThreadLocalRandom.current().nextInt(1000, 1500), ThreadLocalRandom.current().nextInt(1000, 1500)));
        }
    }};

    public static void main(String[] args) {
        int playerLocX = 1250, playerLocZ = 1250;
        long startedAt = System.currentTimeMillis();
        List<Claim> found = new ArrayList<>();
        int radius = 100;

        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                int modifiedX = playerLocX + x;
                int modifiedZ = playerLocZ + z;

                for (Claim claim : CLAIMS) {
                    if (!claim.contains(modifiedX, modifiedZ)) continue;
                    if (found.contains(claim)) continue;

                    found.add(claim);
                }
            }
        }

        System.out.println("Found " + found.size() + " claims in " + (System.currentTimeMillis() - startedAt) + " ms");

    }

}

