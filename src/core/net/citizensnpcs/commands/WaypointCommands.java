package net.citizensnpcs.commands;

import net.citizensnpcs.PermissionManager;
import net.citizensnpcs.npcdata.NPCDataManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.sk89q.Command;
import net.citizensnpcs.resources.sk89q.CommandContext;
import net.citizensnpcs.resources.sk89q.CommandRequirements;
import net.citizensnpcs.utils.ConversationUtils;
import net.citizensnpcs.utils.HelpUtils;
import net.citizensnpcs.utils.MessageUtils;
import net.citizensnpcs.utils.PageUtils;
import net.citizensnpcs.utils.PageUtils.PageInstance;
import net.citizensnpcs.utils.StringUtils;
import net.citizensnpcs.waypoints.Waypoint;
import net.citizensnpcs.waypoints.WaypointModifierType;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandRequirements()
public class WaypointCommands extends CommandHandler {

	@Command(
			aliases = { "wp", "waypoint" },
			usage = "modifier [type]",
			desc = "add a modifier",
			modifiers = { "modifier", "mod" },
			min = 2,
			max = 2)
	public static void modifier(CommandContext args, Player player, HumanNPC npc) {
		if (!NPCDataManager.pathEditors.containsKey(player.getName())) {
			player.sendMessage(ChatColor.GRAY
					+ "You must be editing your NPC's path.");
			return;
		}
		WaypointModifierType modifier = WaypointModifierType.value(args
				.getString(1).toUpperCase());
		if (modifier == null) {
			player.sendMessage(ChatColor.GRAY + "Invalid modifier type.");
			return;
		}
		if (!PermissionManager.generic(player, "citizens.waypoints.modifier"
				+ modifier.name().toLowerCase())) {
			player.sendMessage(MessageUtils.noPermissionsMessage);
			return;
		}
		player.sendMessage(ChatColor.AQUA
				+ StringUtils.listify(StringUtils.wrap(StringUtils
						.capitalise(modifier.name().toLowerCase()))
						+ " chat editor" + ChatColor.AQUA));
		Waypoint waypoint = npc.getWaypoints().getLast();
		ConversationUtils.addConverser(player, modifier.create(waypoint));
	}

	@Command(
			aliases = { "wp", "waypoint" },
			usage = "help (page)",
			desc = "waypoints help",
			modifiers = "help",
			min = 1,
			max = 2)
	public static void help(CommandContext args, Player player, HumanNPC npc) {
		HelpUtils.header(player, "Waypoints", 1, 1);
		HelpUtils.format(player, "waypoint", "modifiers",
				"view waypoint modifiers");
		HelpUtils.format(player, "waypoint", "modifier|mod [type]",
				"add a modifier to the current waypoint");
		HelpUtils.footer(player);
	}

	@Command(
			aliases = { "wp", "waypoint" },
			usage = "modifiers (page)",
			desc = "list waypoint types",
			modifiers = { "modifiers", "mods" },
			min = 1,
			max = 2)
	public static void listModifiers(CommandContext args, Player player,
			HumanNPC npc) {
		int page = args.argsLength() == 2 ? args.getInteger(1) : 1;
		if (page <= 0)
			page = 1;
		PageInstance instance = PageUtils.newInstance(player);
		instance.header(ChatColor.GREEN
				+ StringUtils.listify(ChatColor.YELLOW
						+ "Waypoint modifiers <%x/%y>" + ChatColor.GREEN));
		for (WaypointModifierType type : WaypointModifierType.values()) {
			instance.push(ChatColor.GREEN + "   - " + ChatColor.YELLOW
					+ type.name().toLowerCase());
		}
		if (page > instance.maxPages()) {
			player.sendMessage(ChatColor.GRAY
					+ "Invalid page number. There are " + instance.maxPages()
					+ " pages.");
			return;
		}
		instance.process(page);
	}

	@Override
	public void addPermissions() {
		for (WaypointModifierType modifier : WaypointModifierType.values()) {
			PermissionManager.addPermission("waypoints.modifier."
					+ modifier.name().toLowerCase());
		}
	}
}