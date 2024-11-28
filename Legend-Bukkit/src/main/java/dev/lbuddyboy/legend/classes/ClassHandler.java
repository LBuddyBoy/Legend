package dev.lbuddyboy.legend.classes;

import dev.lbuddyboy.commons.api.util.IModule;
import dev.lbuddyboy.commons.util.Tasks;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.classes.impl.ArcherClass;
import dev.lbuddyboy.legend.classes.impl.BardClass;
import dev.lbuddyboy.legend.classes.impl.MinerClass;
import dev.lbuddyboy.legend.classes.impl.RogueClass;
import dev.lbuddyboy.legend.classes.listener.PvPClassListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ClassHandler implements IModule {

    private List<PvPClass> classes;
    private Map<UUID, PvPClass> activeClasses;

    @Override
    public void load() {
        this.activeClasses = new ConcurrentHashMap<>();
        this.classes = new ArrayList<>();

        if (LegendBukkit.getInstance().getSettings().getBoolean("classes.miner.enabled")) this.classes.add(new MinerClass());
        if (LegendBukkit.getInstance().getSettings().getBoolean("classes.bard.enabled")) this.classes.add(new BardClass());
        if (LegendBukkit.getInstance().getSettings().getBoolean("classes.archer.enabled")) this.classes.add(new ArcherClass());
        if (LegendBukkit.getInstance().getSettings().getBoolean("classes.rogue.enabled")) this.classes.add(new RogueClass());

        this.classes.forEach(c -> LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(c, LegendBukkit.getInstance()));

        Tasks.runTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!this.activeClasses.containsKey(player.getUniqueId())) {
                    if (player.getInventory().getArmorContents() == null) continue;
                    if (player.getInventory().getArmorContents().length < 4) continue;

                    for (PvPClass pvpClass : this.classes) {
                        if (!pvpClass.hasSetOn(player)) continue;
                        if (!pvpClass.shouldApply(player)) continue;

                        pvpClass.apply(player);
                    }

                    continue;
                }
                PvPClass active = this.activeClasses.get(player.getUniqueId());
                if (active.hasSetOn(player)) {
                    if (active.isTickable()) active.tick(player);
                    continue;
                }

                active.remove(player);
                this.activeClasses.remove(player.getUniqueId());
            }
        }, 5, 5);

        LegendBukkit.getInstance().getServer().getPluginManager().registerEvents(new PvPClassListener(), LegendBukkit.getInstance());
    }

    @Override
    public void unload() {
        this.activeClasses.entrySet().stream().filter(e -> Bukkit.getPlayer(e.getKey()) != null).forEach(e -> e.getValue().remove(Bukkit.getPlayer(e.getKey())));
    }

    public <T extends PvPClass> T getClass(Class<T> t) {
        return this.classes.stream().filter(c -> c.getClass().equals(t)).findFirst().map(t::cast).orElse(null);
    }

    public PvPClass getClassApplied(Player player) {
        return this.activeClasses.getOrDefault(player.getUniqueId(), null);
    }

    public boolean isClassApplied(Player player) {
        return this.activeClasses.containsKey(player.getUniqueId());
    }

    public <T extends PvPClass> boolean isClassApplied(Player player, Class<T> clazz) {
        if (!this.activeClasses.containsKey(player.getUniqueId())) return false;

        return clazz.equals(this.activeClasses.get(player.getUniqueId()).getClass());
    }

}
