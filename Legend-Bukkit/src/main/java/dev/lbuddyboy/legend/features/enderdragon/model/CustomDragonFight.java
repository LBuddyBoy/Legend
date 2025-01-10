package dev.lbuddyboy.legend.features.enderdragon.model;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;

public class CustomDragonFight extends EndDragonFight {

    public CustomDragonFight(ServerLevel world, long gatewaysSeed, Data data) {
        super(world, gatewaysSeed, data);
    }

    @Override
    public boolean spawnNewGatewayIfPossible() {
        return false;
    }

    @Override
    public void spawnExitPortal(boolean previouslyKilled) {

    }

    @Override
    public void setDragonKilled(EnderDragon dragon) {
        super.setDragonKilled(dragon);


    }
}
