package net.dzikoysk.funnyguilds.concurrency.requests.prefix;

import net.dzikoysk.funnyguilds.concurrency.util.DefaultConcurrencyRequest;
import net.dzikoysk.funnyguilds.feature.prefix.IndividualPrefixManager;
import net.dzikoysk.funnyguilds.guild.Guild;

public class PrefixGlobalAddGuildRequest extends DefaultConcurrencyRequest {

    private final IndividualPrefixManager individualPrefixManager;
    private final Guild guild;

    public PrefixGlobalAddGuildRequest(IndividualPrefixManager individualPrefixManager, Guild guild) {
        this.individualPrefixManager = individualPrefixManager;
        this.guild = guild;
    }

    @Override
    public void execute() throws Exception {
        this.individualPrefixManager.addGuild(this.guild);
    }

}
