package net.dzikoysk.funnyguilds.feature.command.user;

import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemovePlayerRequest;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalUpdatePlayer;
import net.dzikoysk.funnyguilds.event.FunnyEvent.EventCause;
import net.dzikoysk.funnyguilds.event.SimpleEventHandler;
import net.dzikoysk.funnyguilds.event.guild.member.GuildMemberLeaveEvent;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;
import net.dzikoysk.funnyguilds.feature.command.IsMember;
import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.entity.Player;

import static net.dzikoysk.funnyguilds.feature.command.DefaultValidation.when;

@FunnyComponent
public final class LeaveCommand extends AbstractFunnyCommand {

    @FunnyCommand(
            name = "${user.leave.name}",
            description = "${user.leave.description}",
            aliases = "${user.leave.aliases}",
            permission = "funnyguilds.leave",
            acceptsExceeded = true,
            playerOnly = true
    )
    public void execute(Player player, @IsMember User member, Guild guild) {
        when(member.isOwner(), this.messages.leaveIsOwner);

        if (!SimpleEventHandler.handle(new GuildMemberLeaveEvent(EventCause.USER, member, guild, member))) {
            return;
        }

        guild.removeMember(member);
        member.removeGuild();

        this.concurrencyManager.postRequests(
                new PrefixGlobalRemovePlayerRequest(this.individualPrefixManager, member.getName()),
                new PrefixGlobalUpdatePlayer(this.individualPrefixManager, player)
        );

        FunnyFormatter formatter = new FunnyFormatter()
                .register("{GUILD}", guild.getName())
                .register("{TAG}", guild.getTag())
                .register("{PLAYER}", member.getName());

        member.sendMessage(formatter.format(this.messages.leaveToUser));
        this.broadcastMessage(formatter.format(this.messages.broadcastLeave));
    }

}
