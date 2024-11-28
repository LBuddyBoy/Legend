package dev.lbuddyboy.legend.team.model.claim;

import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.legend.timer.impl.InvincibilityTimer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.UUID;
import java.util.function.Predicate;

@AllArgsConstructor
@Getter
public enum ClaimBorder {

    COMBAT_TAG(new MaterialData(Material.RED_STAINED_GLASS, (byte) 14), (uuid) -> LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(uuid)),
    INVINCIBILITY(new MaterialData(Material.LIME_STAINED_GLASS, (byte) 4), (uuid) -> LegendBukkit.getInstance().getTimerHandler().getTimer(InvincibilityTimer.class).isActive(uuid));

    private final MaterialData materialData;
    private final Predicate<UUID> qualifier;

}
