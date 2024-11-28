import java.util.concurrent.ThreadLocalRandom;

public class Test {

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int chance = ThreadLocalRandom.current().nextInt(100);
            String side = chance <= 50 ? "H" : "T";
            System.out.println(side);
        }
    }

}
