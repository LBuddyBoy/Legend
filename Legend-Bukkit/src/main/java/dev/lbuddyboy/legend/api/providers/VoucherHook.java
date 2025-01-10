package dev.lbuddyboy.legend.api.providers;

import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.legend.LegendBukkit;
import dev.lbuddyboy.legend.team.model.TeamType;
import dev.lbuddyboy.legend.timer.impl.CombatTimer;
import dev.lbuddyboy.vouchers.api.impl.VoucherAPI;
import dev.lbuddyboy.vouchers.object.Voucher;
import org.bukkit.entity.Player;

public class VoucherHook extends VoucherAPI {

    @Override
    public boolean attemptUse(Player player, Voucher voucher) {
        if (!TeamType.SPAWN.appliesAt(player.getLocation()) && !LegendBukkit.getInstance().getTeamHandler().getClaimHandler().getTeam(player.getLocation()).map(at -> at.isMember(player.getUniqueId())).orElse(false)) {
            player.sendMessage(CC.translate("&cYou need to be either in a safezone or in your claim to do this."));
            return false;
        }
        if (LegendBukkit.getInstance().getTimerHandler().getTimer(CombatTimer.class).isActive(player.getUniqueId())) {
            player.sendMessage(CC.translate("&cYou cannot do this in combat."));
            return false;
        }

        return true;
    }
}
