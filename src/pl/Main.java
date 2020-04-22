package pl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
	double x1, y1, z1, x2, y2, z2;
	boolean b1 = false, b2 = false;
	FileConfiguration message, arena;
	File conf;
	ArrayList<String> arenas = new ArrayList<String>();
	Random r = new Random();
	public static Economy econ = null;

	public void onEnable() {
		if (setupedEconomySuccess()) {
			getLogger().info("Vault and Economy plugins found!");
		} else {
			getLogger().info("Vault and Economy plugins not found");
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		getLogger().info("Parkour was activated.");
		getServer().getPluginManager().registerEvents(this, this);

		File confFile = new File(this.getDataFolder(), "message.yml");
		message = YamlConfiguration.loadConfiguration(confFile);
		if (!confFile.exists()) {
			message.set("info", ChatColor.GOLD + "[Parkour] available commands:" + "\n"

					+ ChatColor.YELLOW + "/parkour createarena [arenaname]" + ChatColor.DARK_GREEN + " - create arena"
					+ "\n"

					+ ChatColor.YELLOW + "/parkour delarena [arenaname]" + ChatColor.DARK_GREEN + " - delete arena"
					+ "\n"

					+ ChatColor.YELLOW + "/parkour setspawn [arenaname]" + ChatColor.DARK_GREEN
					+ " - set arena starting point" + "\n"

					+ ChatColor.YELLOW + "/parkour setpos1" + ChatColor.DARK_GREEN
					+ " - select first corner to create arena" + "\n"

					+ ChatColor.YELLOW + "/parkour setpos2" + ChatColor.DARK_GREEN
					+ " - select second corner to create arena" + "\n"

					+ ChatColor.YELLOW + "/parkour setend" + ChatColor.DARK_GREEN + " - set parkour finish point" + "\n"

					+ ChatColor.YELLOW + "/parkour reload" + ChatColor.DARK_GREEN + " - reload plugin" + "\n"

					+ ChatColor.YELLOW + "/parkour setreward [arena] [reward]" + ChatColor.DARK_GREEN
					+ " - set prize for completing parkour");

			message.set("firstpos", ChatColor.GRAY + "First point selected ");
			message.set("secondpos", ChatColor.GRAY + "Second point selected ");
			message.set("nameofarena", ChatColor.RED + "Introduce the name of the arena ");
			message.set("errcreate", ChatColor.RED + "Select region using /parkour setpos1  /parkour setpos2 ");
			message.set("setspawn", ChatColor.GREEN + "Parkour spawnpoint setup complete");
			message.set("alreadycreated", ChatColor.RED + "This arena name already exists ");
			message.set("notfound", ChatColor.RED + "Arena with that name does not exist ");
			message.set("noperm", ChatColor.RED + "You don not have permission to do that ");
			message.set("stop", ChatColor.RED + "You can not stop! Run fast to complete the parkour! ");
			message.set("potion", ChatColor.GRAY + "Potions effects removed ");
			message.set("setend", ChatColor.GREEN + "Parkour endpoint setup complete ");
			message.set("reload", ChatColor.GRAY + "Plugin reloaded ");

			try {
				message.save(confFile);
			} catch (IOException e) {
			}

			conf = new File(this.getDataFolder(), "arena.yml");
			arena = YamlConfiguration.loadConfiguration(conf);

			if (!conf.exists()) {
				try {
					arena.save(conf);
				} catch (IOException e) {
				}

			} else {
				try {
					arena.load(conf);
				} catch (IOException | InvalidConfigurationException e) {
					this.getPluginLoader().disablePlugin(this);
					getLogger().info("Error loading plugin " + e);
				}
			}

		}

		conf = new File(this.getDataFolder(), "arena.yml");

		arena = YamlConfiguration.loadConfiguration(conf);
		try {
			arena.load(conf);
		} catch (IOException | InvalidConfigurationException e1) {

		}

		if (conf.exists())
			if (arena.isConfigurationSection("arenas"))
				if (!arena.getConfigurationSection("arenas").getValues(false).keySet().isEmpty())
					for (String a : arena.getConfigurationSection("arenas").getValues(false).keySet()) {
						arenas.add(a);
						getLogger().info("Parkour arena " + a + " activated successfully");
					}
		if (conf.exists())
			if (!arena.isConfigurationSection("arenas"))
				getLogger().info("No parkour arenas exist");
			else {
				if (arena.getConfigurationSection("arenas").getValues(false).keySet().isEmpty())
					getLogger().info("No parkour arenas found");

			}

	}

	public void onDisable() {
		getLogger().info("Parkour was disactivated");
	}

	private boolean setupedEconomySuccess() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("parkour")) {

			if (sender.hasPermission("parkour.admin") || sender.isOp()) {
				if (sender instanceof Player) {

					if (args.length < 1) {
						sender.sendMessage(message.getString("info"));
					}

					if (args.length > 0) {

						if (args[0].equalsIgnoreCase("createarena") || args[0].equalsIgnoreCase("delarena")
								|| args[0].equalsIgnoreCase("setspawn") || args[0].equalsIgnoreCase("setpos1")
								|| args[0].equalsIgnoreCase("setpos2") || args[0].equalsIgnoreCase("setend")
								|| args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setreward")) {

							if (args[0].equalsIgnoreCase("reload")) {
								File confFile = new File(this.getDataFolder(), "message.yml");
								message = YamlConfiguration.loadConfiguration(confFile);
								if (!confFile.exists()) {
									message.set("info", ChatColor.GOLD + "[Parkour] available commands:" + "\n"

											+ ChatColor.YELLOW + "/parkour createarena [arenaname]"
											+ ChatColor.DARK_GREEN + " - create arena" + "\n"

											+ ChatColor.YELLOW + "/parkour delarena [arenaname]" + ChatColor.DARK_GREEN
											+ " - delete arena" + "\n"

											+ ChatColor.YELLOW + "/parkour setspawn [arenaname]" + ChatColor.DARK_GREEN
											+ " - set arena starting point" + "\n"

											+ ChatColor.YELLOW + "/parkour setpos1" + ChatColor.DARK_GREEN
											+ " - select first corner to create arena" + "\n"

											+ ChatColor.YELLOW + "/parkour setpos2" + ChatColor.DARK_GREEN
											+ " - select second corner to create arena" + "\n"

											+ ChatColor.YELLOW + "/parkour setend" + ChatColor.DARK_GREEN
											+ " - set parkour finish point" + "\n"

											+ ChatColor.YELLOW + "/parkour reload" + ChatColor.DARK_GREEN
											+ " - reload plugin" + "\n"

											+ ChatColor.YELLOW + "/parkour setreward [arena] [reward]"
											+ ChatColor.DARK_GREEN + " - set prize for completing parkour");

									message.set("firstpos", ChatColor.GRAY + "First point selected ");
									message.set("secondpos", ChatColor.GRAY + "Second point selected ");
									message.set("nameofarena", ChatColor.RED + "Introduce the name of the arena ");
									message.set("errcreate",
											ChatColor.RED + "Select region using /parkour setpos1  /parkour setpos2 ");
									message.set("setspawn", ChatColor.GREEN + "Parkour spawnpoint setup complete");
									message.set("alreadycreated", ChatColor.RED + "This arena name already exists ");
									message.set("notfound", ChatColor.RED + "Arena with that name does not exist ");
									message.set("noperm", ChatColor.RED + "You don not have permission to do that ");
									message.set("stop",
											ChatColor.RED + "You can not stop! Run fast to complete the parkour! ");
									message.set("potion", ChatColor.GRAY + "Potions effects removed ");
									message.set("setend", ChatColor.GREEN + "Parkour endpoint setup complete ");
									message.set("reload", ChatColor.GRAY + "Plugin reloaded ");
									try {
										message.save(confFile);
									} catch (IOException e) {
									}
								} else {
									try {
										message.load(confFile);
									} catch (IOException | InvalidConfigurationException e) {
									}
								}

								if (arena.isConfigurationSection("arenas"))
									for (String a : arena.getConfigurationSection("arenas").getValues(false).keySet())
										arenas.remove(a);

								conf = new File(this.getDataFolder(), "arena.yml");
								arena = YamlConfiguration.loadConfiguration(conf);
								if (conf.exists())
									try {
										arena.load(conf);
									} catch (IOException | InvalidConfigurationException e1) {
										getLogger().info("Error occured " + e1);
									}

								if (arena.isConfigurationSection("arenas"))
									for (String a : arena.getConfigurationSection("arenas").getValues(false).keySet()) {
										arenas.add(a);
										getLogger().info("Parkour arena " + a + " found");
									}
								if (arena.isConfigurationSection("arenas"))
									if (arena.getConfigurationSection("arenas").getValues(false).keySet().isEmpty())
										getLogger().info("No parkour arenas found");
								sender.sendMessage(message.getString("reload"));
								getLogger().info("[Parkour] reload complete");
							}

							if (args[0].equalsIgnoreCase("setpos1")) {
								sender.sendMessage(message.getString("firstpos"));
								x1 = ((Player) sender).getLocation().getX();
								y1 = ((Player) sender).getLocation().getY();
								z1 = ((Player) sender).getLocation().getZ();
								b1 = true;
							}

							if (args[0].equalsIgnoreCase("setpos2")) {
								sender.sendMessage(message.getString("secondpos"));
								x2 = ((Player) sender).getLocation().getX();
								y2 = ((Player) sender).getLocation().getY();
								z2 = ((Player) sender).getLocation().getZ();
								b2 = true;
							}

							if (args[0].equalsIgnoreCase("createarena")) {

								if (args.length < 2) {
									sender.sendMessage(message.getString("nameofarena"));
								} else {
									if (b1 && b2) {
										conf = new File(this.getDataFolder(), "arena.yml");

										arena = YamlConfiguration.loadConfiguration(conf);
										try {
											arena.load(conf);
										} catch (IOException | InvalidConfigurationException e1) {
										}
										if (!arena.contains("arenas." + args[1])) {
											arena.set("arenas." + args[1] + ".x1", x1);
											arena.set("arenas." + args[1] + ".y1", y1);
											arena.set("arenas." + args[1] + ".z1", z1);
											arena.set("arenas." + args[1] + ".x2", x2);
											arena.set("arenas." + args[1] + ".y2", y2);
											arena.set("arenas." + args[1] + ".z2", z2);
											try {
												arena.save(conf);
												arena.load(conf);

											} catch (IOException | InvalidConfigurationException e) {
											}
											arenas.add(args[1]);
											sender.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.DARK_GREEN
													+ " created successfully");
										} else {
											sender.sendMessage(message.getString("alreadycreated"));
										}
									} else {
										sender.sendMessage(message.getString("errcreate"));
									}
								}

							}

							if (args[0].equalsIgnoreCase("setend")) {
								if (args.length < 2) {
									sender.sendMessage(message.getString("nameofarena"));
								} else {
									conf = new File(this.getDataFolder(), "arena.yml");

									arena = YamlConfiguration.loadConfiguration(conf);
									try {
										arena.load(conf);
									} catch (IOException | InvalidConfigurationException e1) {
									}

									if (arena.contains("arenas." + args[1])) {
										Location sign = ((Player) sender).getTargetBlock(null, 5).getLocation();
										double xf = sign.getX();
										double yf = sign.getY();
										double zf = sign.getZ();
										arena.set("arenas." + args[1] + ".end.x", xf);
										arena.set("arenas." + args[1] + ".end.y", yf);
										arena.set("arenas." + args[1] + ".end.z", zf);
										try {
											arena.save(conf);
											arena.load(conf);
										} catch (IOException | InvalidConfigurationException e) {
										}
										sender.sendMessage(message.getString("setend"));

									} else {
										sender.sendMessage(message.getString("notfound"));
									}
								}
							}

							if (args[0].equalsIgnoreCase("setreward")) {
								if (args.length < 3) {
									sender.sendMessage(message.getString("nameofarena"));

								} else {
									conf = new File(this.getDataFolder(), "arena.yml");

									arena = YamlConfiguration.loadConfiguration(conf);
									try {
										arena.load(conf);
									} catch (IOException | InvalidConfigurationException e1) {
									}
									if (arena.contains("arenas." + args[1])) {
										arena.set("arenas." + args[1] + ".reward", Integer.parseInt(args[2]));
										try {
											arena.save(conf);
											arena.load(conf);
										} catch (IOException | InvalidConfigurationException e) {
										}

										sender.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.DARK_GREEN
												+ "reward now is " + args[2]);
									} else {
										sender.sendMessage(message.getString("notfound"));
									}

								}
							}

							if (args[0].equalsIgnoreCase("setspawn")) {
								if (args.length < 2) {
									sender.sendMessage(message.getString("nameofarena"));
								} else {
									conf = new File(this.getDataFolder(), "arena.yml");

									arena = YamlConfiguration.loadConfiguration(conf);
									try {
										arena.load(conf);
									} catch (IOException | InvalidConfigurationException e1) {
									}
									if (arena.contains("arenas." + args[1])) {
										arena.set("arenas." + args[1] + ".spawnpoint.x",
												((Player) sender).getLocation().getX());
										arena.set("arenas." + args[1] + ".spawnpoint.y",
												((Player) sender).getLocation().getY());
										arena.set("arenas." + args[1] + ".spawnpoint.z",
												((Player) sender).getLocation().getZ());
										try {
											arena.save(conf);
											arena.load(conf);
										} catch (IOException | InvalidConfigurationException e) {
										}

										sender.sendMessage(message.getString("setspawn"));
									} else {
										sender.sendMessage(message.getString("notfound"));
									}

								}
							}
							if (args[0].equalsIgnoreCase("delarena")) {
								if (args.length < 2) {
									sender.sendMessage(message.getString("nameofarena"));
								} else {
									conf = new File(this.getDataFolder(), "arena.yml");

									arena = YamlConfiguration.loadConfiguration(conf);
									try {
										arena.load(conf);
									} catch (IOException | InvalidConfigurationException e1) {
									}

									if (arena.contains("arenas." + args[1])) {
										arena.set("arenas." + args[1], null);
										arenas.remove(args[1]);
										try {

											arena.save(conf);
											arena.load(conf);

										} catch (IOException | InvalidConfigurationException e) {
										}
										sender.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.DARK_GREEN
												+ " deleted successfully");

									} else {
										sender.sendMessage(message.getString("notfound"));
									}
								}

							}
						} else {

							sender.sendMessage(message.getString("info"));
						}
					}

				}

			} else {
				sender.sendMessage(message.getString("noperm"));
			}
		}
		return true;

	}

	@EventHandler
	public void run(PlayerMoveEvent e) {

		conf = new File(this.getDataFolder(), "arena.yml");

		arena = YamlConfiguration.loadConfiguration(conf);
		try {
			arena.load(conf);
		} catch (IOException | InvalidConfigurationException e1) {
		}

		Player p = e.getPlayer();
		if (!arenas.isEmpty()) {
			for (String name : arenas) {
				if (this.hasPlayerInside(p, name)) {
					if (!p.hasPermission("parkour.invulnerability")) {
						if (!((Player) p).getActivePotionEffects().isEmpty()) {
							for (PotionEffect act : ((Player) p).getActivePotionEffects()) {
								((Player) p).removePotionEffect(act.getType());
							}
							p.sendMessage(message.getString("potion"));

						}
						if (!p.isSprinting() || p.isFlying() || p.isSneaking()) {
							Location newLoc = new Location(p.getWorld(),
									arena.getDouble("arenas." + name + ".spawnpoint.x"),
									arena.getDouble("arenas." + name + ".spawnpoint.y"),
									arena.getDouble("arenas." + name + ".spawnpoint.z"));
							p.teleport(newLoc);
							p.sendMessage(message.getString("stop"));
						}
					}
				}

			}

		}
	}

	@EventHandler
	public void win(PlayerInteractEvent e) {
		int randomSuperprize = r.nextInt(100);

		if (e.getClickedBlock() != null) {
			if (e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.SIGN
					|| e.getClickedBlock().getType() == Material.WALL_SIGN) {
				Sign s = (Sign) e.getClickedBlock().getState();
				if (s.getLine(0).equals(ChatColor.LIGHT_PURPLE + "[Parkour]")) {
					for (String name : arenas) {
						if (s.getLine(1).contains(name)) {
							Location loc = new Location(s.getWorld(),
									arena.getDouble("arenas." + name + ".spawnpoint.x"),
									arena.getDouble("arenas." + name + ".spawnpoint.y"),
									arena.getDouble("arenas." + name + ".spawnpoint.z"));
							e.getPlayer().teleport(loc);
						}
					}
				}
			}
			for (String name : arenas) {
				if (Math.floor(arena.getDouble("arenas." + name + ".end.x")) == e.getClickedBlock().getLocation().getX()
						&& Math.floor(arena.getDouble("arenas." + name + ".end.y")) == e.getClickedBlock().getLocation()
								.getY()
						&& Math.floor(arena.getDouble("arenas." + name + ".end.z")) == e.getClickedBlock().getLocation()
								.getZ()) {
					if (!e.getPlayer().hasPermission("parkour.nogift")) {
						e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
						EconomyResponse r = econ.depositPlayer(e.getPlayer(),
								arena.getDouble("arenas." + name + ".reward"));
						if (r.transactionSuccess()) {
							getServer().broadcastMessage(ChatColor.GOLD + e.getPlayer().getName()
									+ " completed the parkour and won the prize of "
									+ Math.round(arena.getDouble("arenas." + name + ".reward")) + "$");
						}
						if (randomSuperprize == 5) {
							getServer().broadcastMessage(ChatColor.GOLD + e.getPlayer().getName()
									+ " won a superprize with probability of 5/100!");

							getLogger().info("[Parkour] " + e.getPlayer() + " won a superprize");
						} else {
							e.getPlayer().sendMessage(ChatColor.RED + "You didn't win the superprize :(");
						}
					} else {
						e.getPlayer().sendMessage(message.getString("noperm"));
					}
				}
			}
		}
	}

	@EventHandler
	public void sign(SignChangeEvent e) {
		if (e.getLine(0).equals("[Parkour]") && e.getPlayer().hasPermission("parkour.signs")) {
			e.setLine(0, ChatColor.LIGHT_PURPLE + "[Parkour]");
			if (!arenas.contains(e.getLine(1))) {
				e.getBlock().breakNaturally();
				e.getPlayer().sendMessage(message.getString("notfound"));
			} else {
				for (String name : arenas) {
					if (e.getLine(1).equals(name)) {
						e.setLine(1, ChatColor.DARK_AQUA + name);
						e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Reward sign created successfully for "
								+ ChatColor.YELLOW + name);

					}
				}
			}
		}
	}

	public boolean hasLocInside(Location loc, String name) {
		double xmin, ymin, zmin, xmax, ymax, zmax;
		xmax = maxNum(arena.getDouble("arenas." + name + ".x1"), arena.getDouble("arenas." + name + ".x2"));
		ymax = maxNum(arena.getDouble("arenas." + name + ".y1"), arena.getDouble("arenas." + name + ".y2"));
		zmax = maxNum(arena.getDouble("arenas." + name + ".z1"), arena.getDouble("arenas." + name + ".z2"));
		xmin = minNum(arena.getDouble("arenas." + name + ".x1"), arena.getDouble("arenas." + name + ".x2"));
		ymin = minNum(arena.getDouble("arenas." + name + ".y1"), arena.getDouble("arenas." + name + ".y2"));
		zmin = minNum(arena.getDouble("arenas." + name + ".z1"), arena.getDouble("arenas." + name + ".z2"));

		if (xmin <= loc.getX() && xmax >= loc.getX() && ymin <= loc.getY() && ymax >= loc.getY() && zmin <= loc.getZ()
				&& zmax >= loc.getZ()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean hasPlayerInside(Player player, String name) {
		return hasLocInside(player.getLocation().getBlock().getLocation(), name);
	}

	public double maxNum(double x, double y) {
		if (x > y)
			return x;
		else
			return y;
	}

	public double minNum(double x, double y) {
		if (x < y)
			return x;
		else
			return y;
	}

}
