package net.dzikoysk.funnyguilds.nms.v1_18R2.statistics;

import com.google.common.base.Preconditions;
import net.dzikoysk.funnyguilds.nms.api.statistics.StatisticsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V1_18R2StatisticsAccessor implements StatisticsAccessor {

    @Override
    public double getTpsInLastMinute() {
        return ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
    }

    @Override
    public int getReloadCount() {
        return ((CraftServer) Bukkit.getServer()).reloadCount;
    }

    @Override
    public int getPlayerPing(Player player) {
        Preconditions.checkNotNull(player, "player can't be null!");
        return ((CraftPlayer) player).getHandle().e;
    }

}
