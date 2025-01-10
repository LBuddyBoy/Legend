import dev.lbuddyboy.commons.api.APIConstants;

import java.util.Calendar;
import java.util.Date;

public class Test {

    public static void main(String[] args) {
        System.out.println(getDate(7, 15, 0).getTime() / 1000);
        System.out.println(getDate(7, 15, 15).getTime() / 1000);
        System.out.println(getDate(7, 15, 30).getTime() / 1000);
        System.out.println(getDate(7, 15, 45).getTime() / 1000);
        System.out.println(getDate(7, 16, 0).getTime() / 1000);
        System.out.println(getDate(7, 16, 30).getTime() / 1000);
        System.out.println(getDate(7, 17, 15).getTime() / 1000);
/*
        this.createPublic(sender, "two-ability-barrel-all", 7, 15, 15, true, false, true, "<blend:&3;&b>&l2X ABILITY BARREL ALL</>", "abilityitem giveboxall 2");
        this.createPublic(sender, "two-legend-key-all", 7, 15, 30, true, false, true, "<blend:&5;&d>&l2X LEGEND KEY ALL</>", "crate giveall Legend 2");
        this.createPublic(sender, "random-coins", 7, 15, 45, true, false, true, "<blend:&6;&e>&l1,000 COINS GIVEAWAY</>", "schedule giverandomcoins 1000");
        this.createPublic(sender, "random-minigame", 7, 16, 0, true, false, true, "<blend:&4;&c>&lRANDOM MINIGAME FOR 7D LEGEND RANK</>", "minigame startrandom");
        this.createPublic(sender, "two-legend-key-all", 7, 16, 30, true, false, true, "<blend:&5;&c>&l3X AMETHYST KEY ALL</>", "crate giveall Amethyst 3");
        this.createPublic(sender, "random-koth", 7, 17, 15, true, false, true, "<blend:&9;&b>&lRANDOM KOTH</>", "koth start Winter");
*/

    }

    public static Date getDate(int dayOfWeek, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance(APIConstants.TIME_ZONE, APIConstants.LOCALE);

        // Set the calendar to the nearest upcoming dayOfWeek, hourOfDay, and minute
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0); // Reset seconds and milliseconds
        calendar.set(Calendar.MILLISECOND, 0);

        // If the calculated time is in the past, add 7 days to get the next occurrence
        if (calendar.getTime().before(new Date())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return calendar.getTime();
    }

}
