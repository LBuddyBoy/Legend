package dev.lbuddyboy.legend.features.enderdragon.listener;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.LocationUtils;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.events.api.event.EventCapturedEvent;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.enderdragon.EnderDragonHandler;
import dev.lbuddyboy.legend.features.enderdragon.model.CustomEnderDragon;
import dev.lbuddyboy.legend.team.model.Team;
import io.papermc.paper.event.block.DragonEggFormEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public class EnderDragonListener implements Listener {

    private final EnderDragonHandler enderDragonHandler = LegendBukkit.getInstance().getEnderDragonHandler();

    public void onDeath(CustomEnderDragon dragon) {
        if (dragon.isDead()) return;

        List<Map.Entry<UUID, Double>> teamDamage = new ArrayList<>(this.enderDragonHandler.getTeamDamage().entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).toList());
        Collections.reverse(teamDamage);

        dragon.setDead(true);
        Tasks.run(this.enderDragonHandler::despawnDragon);

        if (!teamDamage.isEmpty()) {
            Map.Entry<UUID, Double> winnerEntry = teamDamage.getFirst();
            Team team = LegendBukkit.getInstance().getTeamHandler().getTeamById(winnerEntry.getKey()).orElse(null);

            if (team == null) {
                return;
            }

            List<UUID> members = team.getOnlinePlayers().stream().map(Entity::getUniqueId).toList();
            List<Map.Entry<UUID, Double>> memberDamage = new ArrayList<>(this.enderDragonHandler.getIndividualDamage().entrySet().stream().filter(e -> members.contains(e.getKey())).sorted(Comparator.comparingDouble(Map.Entry::getValue)).toList());

            Collections.reverse(memberDamage);

            if (!memberDamage.isEmpty()) {
                Player winner = Bukkit.getPlayer(memberDamage.getFirst().getKey());

                this.enderDragonHandler.getLootTable().open(winner);
            }

            Arrays.asList(
                    " ",
                    "&7███████",
                    "&7█&5█████&7█",
                    "&7█&5█&7█████",
                    "&7█&5█&7█████ <blend:&5;&d>&lEnder Dragon Event</>",
                    "&7█&5████&7██ &aThe ender dragon was slain!",
                    "&7█&5█&7█████ &eWinner&7: &f" + team.getName(),
                    "&7█&5█&7█████",
                    "&7█&5█████&7█",
                    "&7███████",
                    " "
            ).forEach(s -> Bukkit.broadcastMessage(CC.translate(s)));
        }
    }

    @EventHandler
    public void onForm(DragonEggFormEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof EnderDragon dragon)) return;

        if (dragon.isDead()) {
            event.setCancelled(true);
            return;
        }

        UUID teamId = Events.getApi().getTeamId(damager.getUniqueId());
        double damage = this.enderDragonHandler.getTeamDamage().getOrDefault(teamId, 0.0D) + event.getDamage();

        this.enderDragonHandler.getTeamDamage().put(teamId, damage);
        this.enderDragonHandler.getIndividualDamage().put(damager.getUniqueId(), this.enderDragonHandler.getIndividualDamage().getOrDefault(damager.getUniqueId(), 0.0D) + event.getDamage());

        if (dragon.getHealth() - event.getFinalDamage() <= 0) {
            this.onDeath(this.enderDragonHandler.getEnderDragon());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageProjectile(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile projectile)) return;
        if (!(projectile.getShooter() instanceof Player damager)) return;

        if (!(event.getEntity() instanceof EnderDragon dragon)) return;
        if (dragon.isDead()) {
            event.setCancelled(true);
            return;
        }

        UUID teamId = Events.getApi().getTeamId(damager.getUniqueId());
        double damage = this.enderDragonHandler.getTeamDamage().getOrDefault(teamId, 0.0D) + event.getDamage();

        this.enderDragonHandler.getTeamDamage().put(teamId, damage);
        this.enderDragonHandler.getIndividualDamage().put(damager.getUniqueId(), this.enderDragonHandler.getIndividualDamage().getOrDefault(damager.getUniqueId(), 0.0D) + event.getDamage());

        if (dragon.getHealth() - event.getFinalDamage() <= 0) {
            this.onDeath(this.enderDragonHandler.getEnderDragon());
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

}
