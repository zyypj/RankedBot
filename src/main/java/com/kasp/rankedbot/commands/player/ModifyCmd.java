package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ModifyCmd extends Command {
    public ModifyCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length != 4) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("wrong-usage").replaceAll("%usage%", "modify <ID/mention> <statistic> <value>"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID = args[1].replaceAll("[^0-9]","");
        Statistic stat = Statistic.valueOf(args[2].toUpperCase());
        int amount = Integer.parseInt(args[3]);

        Player player = new Player(ID, null);

        Embed embed = new Embed(EmbedType.SUCCESS, "Modified `" + player.getIgn() + "`'s stats", "", 1);

        switch (stat) {
            case ELO:
                player.setElo(player.getElo() + amount);
                embed.setDescription("Player's elo: " + (player.getElo() - amount) + " > " + player.getElo());
                break;
            case WINS:
                player.setWins(player.getWins() + amount);
                embed.setDescription("Player's wins: " + (player.getWins() - amount) + " > " + player.getWins());
                break;
            case LOSSES:
                player.setLosses(player.getLosses() + amount);
                embed.setDescription("Player's losses: " + (player.getLosses() - amount) + " > " + player.getLosses());
                break;
            case MVP:
                player.setMvp(player.getMvp() + amount);
                embed.setDescription("Player's mvp: " + (player.getMvp() - amount) + " > " + player.getMvp());
                break;
            case KILLS:
                player.setKills(player.getKills() + amount);
                embed.setDescription("Player's kills: " + (player.getKills() - amount) + " > " + player.getKills());
                break;
            case DEATHS:
                player.setDeaths(player.getDeaths() + amount);
                embed.setDescription("Player's deaths: " + (player.getDeaths() - amount) + " > " + player.getDeaths());
                break;
            case STRIKES:
                player.setStrikes(player.getStrikes() + amount);
                embed.setDescription("Player's strikes: " + (player.getStrikes() - amount) + " > " + player.getStrikes());
                break;
            case SCORED:
                player.setScored(player.getScored() + amount);
                embed.setDescription("Player's scored: " + (player.getScored() - amount) + " > " + player.getScored());
                break;
            case GOLD:
                player.setGold(player.getGold() + amount);
                embed.setDescription("Player's gold: " + (player.getGold() - amount) + " > " + player.getGold());
                break;
            case LEVEL:
                player.setLevel(player.getLevel() + amount);
                embed.setDescription("Player's level: " + (player.getLevel() - amount) + " > " + player.getLevel());
                break;
            case XP:
                player.setXp(player.getXp() + amount);
                embed.setDescription("Player's xp: " + (player.getXp() - amount) + " > " + player.getXp());
                break;
            default:
                Embed error = new Embed(EmbedType.ERROR, "Unknown Stat", "Must be `ELO`/`WINS`/`LOSSES`/`MVP`/`KILLS`/`DEATHS`/`STRIKES`/`SCORED`/`GOLD`/`LEVEL`/`XP`", 1);
                msg.replyEmbeds(error.build()).queue();
                return;
        }

        msg.replyEmbeds(embed.build()).queue();
    }
}
