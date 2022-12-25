package com.kasp.rankedbot.commands.player;

import com.kasp.rankedbot.CommandSubsystem;
import com.kasp.rankedbot.EmbedType;
import com.kasp.rankedbot.Statistic;
import com.kasp.rankedbot.commands.Command;
import com.kasp.rankedbot.config.Config;
import com.kasp.rankedbot.instance.Player;
import com.kasp.rankedbot.instance.Theme;
import com.kasp.rankedbot.instance.embed.Embed;
import com.kasp.rankedbot.messages.Msg;
import net.dv8tion.jda.api.entities.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

public class StatsCmd extends Command {
    public StatsCmd(String command, String usage, String[] aliases, String description, CommandSubsystem subsystem) {
        super(command, usage, aliases, description, subsystem);
    }

    @Override
    public void execute(String[] args, Guild guild, Member sender, TextChannel channel, Message msg) {
        if (args.length > 2) {
            Embed reply = new Embed(EmbedType.ERROR, "Error", Msg.getMsg("wrong-usage").replaceAll("%usage%", "stats [ID/mention/\"full\"]"), 1);
            msg.replyEmbeds(reply.build()).queue();
            return;
        }

        String ID;
        if (args.length == 1)
            ID = sender.getId();
        else {
            if (args[1].equals("full"))
                ID = sender.getId();
            else
                ID = args[1].replaceAll("[^0-9]","");
        }

        Player player = new Player(ID, null);

        DecimalFormat f = new DecimalFormat("#.##");

        double templosses;
        if (player.getLosses() == 0)
            templosses = 1;
        else
            templosses = player.getLosses();

        int games = player.getWins() + player.getLosses();

        if (args.length == 2 && args[1].equals("full")) {
            msg.replyEmbeds(statsFulLEmbed(player, games, templosses).build()).queue();
            return;
        }

        if (new File("RBW/themes/default.png").exists()) {
            try {
                player.fix();

                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("RankedBot/fonts/stats.otf")));

                BufferedImage image = ImageIO.read(new File("RankedBot/themes/" + player.getTheme().getName() + ".png").toURI().toURL());
                BufferedImage skin = ImageIO.read(new URL("https://api.mineatar.io/body/full/" + player.getIgn() + "?scale=9"));

                Graphics2D gfx = (Graphics2D) image.getGraphics();
                gfx.setFont(new Font("Minecraft", Font.PLAIN, 30));
                gfx.setColor(new Color(250,250,87,255));
                gfx.drawString(player.getElo() + "", Integer.parseInt(Config.getValue("elo-pixels").split(",")[0]), Integer.parseInt(Config.getValue("elo-pixels").split(",")[1]));
                gfx.drawString(player.getMvp() + "", Integer.parseInt(Config.getValue("mvp-pixels").split(",")[0]), Integer.parseInt(Config.getValue("mvp-pixels").split(",")[1]));
                gfx.drawString(games + "", Integer.parseInt(Config.getValue("games-pixels").split(",")[0]), Integer.parseInt(Config.getValue("games-pixels").split(",")[1]));
                gfx.drawString(f.format(player.getWins() / templosses) + "", Integer.parseInt(Config.getValue("wlr-pixels").split(",")[0]), Integer.parseInt(Config.getValue("wlr-pixels").split(",")[1]));
                gfx.drawString(player.getWins() + "", Integer.parseInt(Config.getValue("wins-pixels").split(",")[0]), Integer.parseInt(Config.getValue("wins-pixels").split(",")[1]));
                gfx.drawString(player.getLosses() + "", Integer.parseInt(Config.getValue("losses-pixels").split(",")[0]), Integer.parseInt(Config.getValue("losses-pixels").split(",")[1]));
                gfx.drawString(player.getWinStreak() + "", Integer.parseInt(Config.getValue("winstreak-pixels").split(",")[0]), Integer.parseInt(Config.getValue("winstreak-pixels").split(",")[1]));
                gfx.drawString(player.getLossStreak() + "", Integer.parseInt(Config.getValue("losestreak-pixels").split(",")[0]), Integer.parseInt(Config.getValue("losestreak-pixels").split(",")[1]));
                gfx.drawString(player.getStrikes() + "", Integer.parseInt(Config.getValue("strikes-pixels").split(",")[0]), Integer.parseInt(Config.getValue("strikes-pixels").split(",")[1]));
                gfx.drawString(player.getScored() + "", Integer.parseInt(Config.getValue("scored-pixels").split(",")[0]), Integer.parseInt(Config.getValue("scored-pixels").split(",")[1]));

                Role role = guild.getRoleById(player.getRank().getID());

                gfx.setColor(role.getColor());

                gfx.drawString(role.getName(), Integer.parseInt(Config.getValue("role-pixels").split(",")[0]), Integer.parseInt(Config.getValue("role-pixels").split(",")[1]));

                gfx.setFont(new Font("Minecraft", Font.PLAIN, 42));
                gfx.setColor(Color.white);
                gfx.drawString(player.getIgn() + "", Integer.parseInt(Config.getValue("name-pixels").split(",")[0]), Integer.parseInt(Config.getValue("name-pixels").split(",")[1]));

                gfx.drawImage(skin, Integer.parseInt(Config.getValue("skin-pixels").split(",")[0]), Integer.parseInt(Config.getValue("skin-pixels").split(",")[1]), null);

                gfx.dispose();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", stream);

                msg.reply("** **").addFile(stream.toByteArray(), "stats.png").queue();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            msg.replyEmbeds(statsFulLEmbed(player, games, templosses).build()).queue();
        }
    }

    private Embed statsFulLEmbed(Player player, int games, double templosses) {
        DecimalFormat f = new DecimalFormat("#.##");

        Embed embed = new Embed(EmbedType.DEFAULT, player.getIgn() + "'s Stats", "",1);

        embed.addField("__General Stats__",
                "> `Elo` " + player.getElo() + " **(#" + player.getPlacement(Statistic.ELO) + ")**" +
                        "\n> ┗ `Peak` " + player.getPeakElo() + " **(#" + player.getPlacement(Statistic.PEAKELO) + ")**" +
                        "\n> `Games` " + games + " **(#" + player.getPlacement(Statistic.GAMES) + ")**" +
                        "\n> `WLR` " + f.format(player.getWins() / templosses) + " **(#" + player.getPlacement(Statistic.WLR) + ")**" +
                        "\n> `Mvp` " + player.getMvp() + " **(#" + player.getPlacement(Statistic.WLR) + ")**" +
                        "\n> `Strikes` " + player.getStrikes() + " **(#" + player.getPlacement(Statistic.STRIKES) + ")**" +
                        "\n> `Scored` " + player.getScored() + " **(#" + player.getPlacement(Statistic.SCORED) + ")**", false);

        embed.addField("__Games Stats__",
                "> **`Wins`** " + player.getWins() + " **(#" + player.getPlacement(Statistic.WINS) + ")**" +
                        "\n> `Winstreak` " + player.getWinStreak() + " **(#" + player.getPlacement(Statistic.WINSTREAK) + ")**" +
                        "\n> ┗ `Highest` " + player.getHighestWS() + " **(#" + player.getPlacement(Statistic.HIGHESTWS) + ")**" +
                        "\n> **`Losses`** " + player.getLosses() + " **(#" + player.getPlacement(Statistic.LOSSES) + ")**" +
                        "\n> `Losestreak` " + player.getLossStreak() + " **(#" + player.getPlacement(Statistic.LOSSSTREAK) + ")**" +
                        "\n> ┗ `Highest` " + player.getHighestLS() + " **(#" + player.getPlacement(Statistic.HIGHESTLS) + ")**", false);

        StringBuilder themes = new StringBuilder();
        for (Theme t : player.getOwnedThemes()) {
            themes.append("`" + t.getName() + "` ");
        }

        embed.addField("__Other Stats__",
                "> `Gold` " + player.getGold() + " **(#" + player.getPlacement(Statistic.GOLD) + ")**" +
                        "\n> `Level` " + player.getLevel() + " **(#" + player.getPlacement(Statistic.LEVEL) + ")**" +
                        "\n> `Xp` " + player.getXp() + " **(#" + player.getPlacement(Statistic.XP) + ")**" +
                        "\n> `Selected theme` " + player.getTheme().getName() +
                        "\n> `Owned themes` " + themes, false);

        return embed;
    }
}
