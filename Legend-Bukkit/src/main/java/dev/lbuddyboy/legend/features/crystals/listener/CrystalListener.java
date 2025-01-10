package dev.lbuddyboy.legend.features.crystals.listener;

import dev.lbuddyboy.commons.deathmessage.event.PlayerKilledEvent;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.EntityUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.user.model.LegendUser;
import org.bukkit.block.Block;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.concurrent.ThreadLocalRandom;

public class CrystalListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        double chance = LegendBukkit.getInstance().getCrystalHandler().getOreChances().getOrDefault(block.getType(), -1D);

        if (chance <= 0) return;
        if (ThreadLocalRandom.current().nextDouble(100) > chance) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(player.getUniqueId());
        int amount = ThreadLocalRandom.current().nextInt(1, 6);

        user.setCrystals(user.getCrystals() + amount);
        player.sendMessage(CC.translate("<blend:&3;&b>&lCRYSTALS</> &7» &fYou found &b" + amount + " crystals&f whilst mining."));
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Monster monster)) return;

        Player killer = monster.getKiller();
        if (killer == null) return;

        double chance = LegendBukkit.getInstance().getCrystalHandler().getMonsterChances().getOrDefault(monster.getType(), -1D);

        if (chance <= 0) return;
        if (ThreadLocalRandom.current().nextDouble(100) > chance) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(killer.getUniqueId());
        int amount = ThreadLocalRandom.current().nextInt(1, 6);

        user.setCrystals(user.getCrystals() + amount);
        killer.sendMessage(CC.translate("<blend:&3;&b>&lCRYSTALS</> &7» &fYou found &b" + amount + " crystals&f from killing a &b" + EntityUtils.getName(monster.getType()) + "&f."));
    }

    @EventHandler
    public void onKill(PlayerKilledEvent event) {
        Player killer = event.getKiller(), victim = event.getVictim();

        if (killer == null) return;

        LegendUser user = LegendBukkit.getInstance().getUserHandler().getUser(killer.getUniqueId());
        int amount = ThreadLocalRandom.current().nextInt(1, 6);

        user.setCrystals(user.getCrystals() + amount);
        killer.sendMessage(CC.translate("<blend:&3;&b>&lCRYSTALS</> &7» &fYou were rewarded &b" + amount + " crystals&f for killing &b" + victim.getName() + "&f."));
    }

}
