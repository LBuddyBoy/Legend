package dev.lbuddyboy.legend.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

public class UHCListener implements Listener {

    private final Scoreboard mainBoard;
    private final Objective objective;

    public UHCListener() {
        this.mainBoard = LegendBukkit.getInstance().getServer().getScoreboardManager().getMainScoreboard();
        this.objective = this.mainBoard.getObjective("healthbarbelow") == null ? this.mainBoard.registerNewObjective("healthbarbelow", Criteria.HEALTH, CC.translate("&4❤"), RenderType.HEARTS) : this.mainBoard.getObjective("healthbarbelow");
        this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity(), killer = victim.getKiller();
        ItemFactory factory = new ItemFactory(event.getEntity().getUniqueId())
                .displayName(event.getEntity().getName() + "'s Head");

        if (killer != null) factory.lore("&c&oBeheaded by " + killer.getName());

        event.getDrops().add(factory.build());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR) return;
        if (item.getType() != Material.PLAYER_HEAD) return;

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasTag("golden-head")) return;

        player.getInventory().setItem(event.getHand(), ItemUtils.takeItem(item));
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 10, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1));
    }

/*    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.createHealthBar(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.removeHealthBar(event.getPlayer());
    }*/

    @EventHandler
    public void onShutdown(PluginDisableEvent event) {
        if (!(event.getPlugin() instanceof LegendBukkit)) return;

        this.objective.unregister();
        this.mainBoard.clearSlot(DisplaySlot.BELOW_NAME);
    }

    public void createHealthBar(Player player) {
        final double health = player.getHealth();
        final double max = player.getAttribute(Attribute.MAX_HEALTH).getValue();

        final int intHealth = BukkitUtil.roundUpPositive(health);
        final int intMax = BukkitUtil.roundUpPositive(max);

        Team team = this.mainBoard.getTeam("hbr" + intHealth + "-" + intMax);

        if (team == null) {
            team = this.mainBoard.registerNewTeam("hbr" + intHealth + "-" + intMax);
            team.setCanSeeFriendlyInvisibles(false);
        }

        team.addEntry(player.getName());
    }

    public void removeHealthBar(Player player) {
        final double health = player.getHealth();
        final double max = player.getAttribute(Attribute.MAX_HEALTH).getValue();

        final int intHealth = BukkitUtil.roundUpPositive(health);
        final int intMax = BukkitUtil.roundUpPositive(max);

        Team team = this.mainBoard.getTeam("hbr" + intHealth + "-" + intMax);

        if (team == null) {
            team = this.mainBoard.registerNewTeam("hbr" + intHealth + "-" + intMax);
            team.setCanSeeFriendlyInvisibles(false);
        }

        team.removeEntry(player.getName());
    }

    public void updateHealthBelow(final Player player) {
        final int score = getRawAmountOfHearts(player);

        this.objective.getScore(player.getName()).setScore(score);
    }

    public static int getRawAmountOfHearts(@NotNull final Player player) {
        if (player.isHealthScaled())
            return (int) Math.round(player.getHealth() * 10.0 / player.getAttribute(Attribute.MAX_HEALTH).getValue());
        else
            return (int) Math.round(player.getHealth());
    }

}
