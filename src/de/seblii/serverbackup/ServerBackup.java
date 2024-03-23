package de.seblii.serverbackup;

import de.seblii.serverbackup.commands.SBCommand;
import org.apache.commons.io.FileUtils;
import org.bstats.MetricsBase;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.logging.Level;

public class ServerBackup extends JavaPlugin implements Listener {

    private static ServerBackup sb;

    public static ServerBackup getInstance() {
        return sb;
    }

    public String backupDestination = "Backups//";

    public File backupInfo = new File("plugins//ServerBackup//backupInfo.yml");
    public YamlConfiguration bpInf = YamlConfiguration.loadConfiguration(backupInfo);

    public File cloudInfo = new File("plugins//ServerBackup//cloudAccess.yml");
    public YamlConfiguration cloud = YamlConfiguration.loadConfiguration(cloudInfo);

    public File messagesFile = new File("plugins//ServerBackup//messages.yml");
    public YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);

    boolean isUpdated = false;

    public boolean shutdownProgress = false;

    public String prefix = "";

    @Override
    public void onDisable() {
        stopTimer();

        for (BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
            task.cancel();
        }

        getLogger().log(Level.INFO, "ServerBackup: Plugin disabled.");
    }

    @Override
    public void onEnable() {
        sb = this;

        loadFiles();

        getCommand("backup").setExecutor(new SBCommand());

        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new DynamicBackup(), this);

        startTimer();

        getLogger().log(Level.INFO, "ServerBackup: Plugin enabled.");

        if (getConfig().getBoolean("UpdateAvailableMessage")) {
            checkVersion();
        }

        int mpid = 14673;

        Metrics metrics = new Metrics(this, mpid);

        metrics.addCustomChart(new SimplePie("player_per_server", () -> String.valueOf(Bukkit.getOnlinePlayers().size())));

        metrics.addCustomChart(new SimplePie("using_ftp_server", () -> getConfig().getBoolean("Ftp.UploadBackup") ? "yes" : "no"));

        metrics.addCustomChart(new SimplePie("using_dropbox", () -> getConfig().getBoolean("CloudBackup.Dropbox") ? "yes" : "no"));

        metrics.addCustomChart(new SimplePie("using_gdrive", () -> getConfig().getBoolean("CloudBackup.GoogleDrive") ? "yes" : "no"));

        metrics.addCustomChart(new SingleLineChart("total_backup_space", () -> {
            File file = new File(backupDestination);
            double fileSize = (double) FileUtils.sizeOf(file) / 1000 / 1000;
            fileSize = Math.round(fileSize * 100.0) / 100.0;
            return (int) fileSize;
        }));
    }

    private void checkVersion() {
        getLogger().log(Level.INFO, "ServerBackup: Searching for updates...");

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            int resourceID = 79320;
            try (InputStream inputStream = (new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=" + resourceID)).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    String latest = scanner.next();
                    String current = getDescription().getVersion();

                    int late = Integer.parseInt(latest.replaceAll("\\.", ""));
                    int curr = Integer.parseInt(current.replaceAll("\\.", ""));

                    if (curr >= late) {
                        getLogger().log(Level.INFO,
                                "ServerBackup: No updates found. The server is running the latest version.");
                    } else {
                        getLogger().log(Level.INFO, "ServerBackup: There is a newer version available - " + latest
                                + ", you are on - " + current);

                        if (getConfig().getBoolean("AutomaticUpdates")) {
                            getLogger().log(Level.INFO, "ServerBackup: Downloading newest version...");

                            URL url = new URL("https://server-backup.net/assets/downloads/alt/ServerBackup.jar");

                            if (Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19")) {
                                url = new URL("https://server-backup.net/assets/downloads/ServerBackup.jar");
                            }

                            try (InputStream in = url.openStream();
                                 ReadableByteChannel rbc = Channels.newChannel(in);
                                 FileOutputStream fos = new FileOutputStream("plugins/ServerBackup.jar")) {
                                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                                getLogger().log(Level.INFO,
                                        "ServerBackup: Download finished. Please reload the server to complete the update.");

                                isUpdated = true;
                            }
                        } else {
                            getLogger().log(Level.INFO,
                                    "ServerBackup: Please download the latest version - https://server-backup.net/");
                        }
                    }
                }
            } catch (IOException exception) {
                getLogger().log(Level.WARNING,
                        "ServerBackup: Cannot search for updates - " + exception.getMessage());
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void loadFiles() {
        // Code for loading files remains unchanged
    }

    public void loadCloud() {
        // Code for loading cloud configuration remains unchanged
    }

    public void saveCloud() {
        // Code for saving cloud configuration remains unchanged
    }

    public void loadBpInf() {
        // Code for loading backup information remains unchanged
    }

    public void saveBpInf() {
        // Code for saving backup information remains unchanged
    }

    public void loadMessages() {
        // Code for loading messages remains unchanged
    }

    public void saveMessages() {
        // Code for saving messages remains unchanged
    }

    public String processMessage(String msgCode) {
        // Code for processing messages remains unchanged
    }

    public void startTimer() {
        // Code for starting the backup timer remains unchanged
    }

    public void stopTimer() {
        // Code for stopping the backup timer remains unchanged
    }

    // Events
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Code for handling player join events remains unchanged
    }
}
