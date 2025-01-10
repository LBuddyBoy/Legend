package dev.lbuddyboy.legend.features.enderdragon.model;

import dev.lbuddyboy.events.Events;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.features.enderdragon.EnderDragonHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

@Getter
public class CustomEnderDragon extends EnderDragon {

    @Setter private boolean dead = false;
    private final EnderDragonHandler enderDragonHandler = LegendBukkit.getInstance().getEnderDragonHandler();

    public CustomEnderDragon(Location location) {
        super(EntityType.ENDER_DRAGON, ((CraftWorld)location.getWorld()).getHandle());
        this.uuid = UUID.randomUUID();

        this.setDragonFight(new CustomDragonFight(((CraftWorld)location.getWorld()).getHandle(), 0, new EndDragonFight.Data(
                false,
                false,
                false,
                false,
                Optional.of(this.uuid),
                Optional.empty(),
                Optional.empty()
        )));

        visibleByDefault = true;
    }

    @Override
    public void handleDamageEvent(DamageSource damageSource) {
        super.handleDamageEvent(damageSource);

    }

    @Override
    protected void reallyHurt(ServerLevel world, DamageSource source, float amount) {
        super.reallyHurt(world, source, amount);


    }

    @Override
    protected void tickDeath() {
        if (this.getDragonFight() != null) {
            this.getDragonFight().setDragonKilled(this);
        }
        remove(RemovalReason.DISCARDED);
    }

    @Override
    public int getExpReward(ServerLevel worldserver, Entity entity) {
        return 0;
    }

}
