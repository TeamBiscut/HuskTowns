package me.william278.husktowns.command;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import me.william278.husktowns.HuskTowns;
import me.william278.husktowns.MessageManager;
import me.william278.husktowns.data.pluginmessage.PluginMessage;
import me.william278.husktowns.data.pluginmessage.PluginMessageType;
import me.william278.husktowns.object.cache.PlayerCache;
import me.william278.husktowns.object.town.Town;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class TownChatCommand extends CommandBase {

    @Override
    protected void onCommand(Player player, Command command, String label, String[] args) {
        if (HuskTowns.getSettings().doTownChat()) {
            PlayerCache playerCache = HuskTowns.getPlayerCache();
            String town = playerCache.getTown(player.getUniqueId());
            if (town != null) {
                if (args.length == 0) {
                    if (HuskTowns.getSettings().doToggleableTownChat()) {
                        toggleTownChat(player);
                    } else {
                        MessageManager.sendMessage(player, "error_invalid_syntax", command.getUsage());
                    }
                    return;
                }
                StringBuilder message = new StringBuilder();
                for (int i = 1; i <= args.length; i++) {
                    message.append(args[i - 1]).append(" ");
                }

                sendTownChatMessage(player, town, message.toString());
            } else {
                MessageManager.sendMessage(player, "error_not_in_town");
            }
        } else {
            MessageManager.sendMessage(player, "error_town_chat_disabled");
        }
    }

    public static void dispatchTownMessage(String townName, String senderName, String message) {
        PlayerCache cache = HuskTowns.getPlayerCache();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ComponentBuilder townMessage = new ComponentBuilder();
            if (cache.getTown(p.getUniqueId()).equals(townName)) {
                townMessage.append(new MineDown(MessageManager.getRawMessage("town_chat",
                        townName, senderName)).toComponent());

            } else if (p.hasPermission("husktowns.town_chat_spy")) {
                townMessage.append(new MineDown(MessageManager.getRawMessage("town_chat_spy",
                        townName, senderName)).toComponent());
            } else {
                continue;
            }
            townMessage.append(message);
            p.spigot().sendMessage(townMessage.create());
        }
    }

    public static void sendTownChatMessage(Player sender, String townName, String message) {
        if (message.contains("💲")) {
            MessageManager.sendMessage(sender, "error_town_chat_invalid_characters");
            return;
        }
        dispatchTownMessage(townName, sender.getName(), message);
        if (HuskTowns.getSettings().doBungee()) {
            new PluginMessage(PluginMessageType.TOWN_CHAT_MESSAGE, townName, sender.getName(), message.replaceAll("\\$", "💲")).sendToAll(sender);
        }
    }

    private void toggleTownChat(Player player) {
        if (HuskTowns.townChatters.contains(player.getUniqueId())) {
            HuskTowns.townChatters.remove(player.getUniqueId());
            MessageManager.sendMessage(player, "town_chat_toggled_off");
        } else {
            HuskTowns.townChatters.add(player.getUniqueId());
            MessageManager.sendMessage(player, "town_chat_toggled_on");
        }
    }
}
