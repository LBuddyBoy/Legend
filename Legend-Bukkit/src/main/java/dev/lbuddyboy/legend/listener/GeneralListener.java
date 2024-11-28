package dev.lbuddyboy.legend.listener;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.recipients.Recipients;
import dev.lbuddyboy.commons.CommonsPlugin;
import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.settings.Setting;
import dev.lbuddyboy.legend.user.model.LegendUser;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class GeneralListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity(), killer = victim.getKiller();

        if (CitizensAPI.getNPCRegistry().isNPC(victim)) return;
        if (killer == null) return;
        if (!LegendBukkit.getInstance().getSettings().getBoolean("take-money-on-death", true)) return;

        LegendUser victimUser = LegendBukkit.getInstance().getUserHandler().getUser(victim.getUniqueId()), killerUser = LegendBukkit.getInstance().getUserHandler().getUser(killer.getUniqueId());

        killerUser.setBalance(killerUser.getBalance() + victimUser.getBalance());
        killer.sendMessage(CC.translate(LegendBukkit.getInstance().getLanguage().getString("took-money-on-death")
                .replaceAll("%amount%", APIConstants.formatNumber(victimUser.getBalance()))
                .replaceAll("%target%", victim.getName())
        ));
        victimUser.setBalance(0);
        killer.playSound(killer, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (event.getModifiedType() != PotionEffectType.INVISIBILITY) return;

        CommonsPlugin.getInstance().getNameTagHandler().updatePlayer(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Apollo.getModuleManager().getModule(NametagModule.class).resetNametag(Recipients.ofEveryone(), player.getUniqueId());
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getItem().getItemStack().getType() != Material.COBBLESTONE
                && event.getItem().getItemStack().getType() != Material.DEEPSLATE
                && event.getItem().getItemStack().getType() != Material.COBBLED_DEEPSLATE
                && event.getItem().getItemStack().getType() != Material.GRANITE
                && event.getItem().getItemStack().getType() != Material.DIORITE
        ) return;
        if (Setting.COBBLESTONE.isToggled(player.getUniqueId())) return;

        event.setCancelled(true);
    }

}
