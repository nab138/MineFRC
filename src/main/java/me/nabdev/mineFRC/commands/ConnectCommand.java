package me.nabdev.mineFRC.commands;

import edu.wpi.first.networktables.*;
import me.nabdev.mineFRC.MineFRC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public final class ConnectCommand implements CommandExecutor {
    public static final float scaleFactor = 30.69486405f;
    private int schedule;
    private BlockDisplay botDisplay;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Location origin = ((Player) sender).getLocation();

        NetworkTableInstance inst = NetworkTableInstance.getDefault();

        String rawKey = args[0];
        if(rawKey.startsWith(("/"))) rawKey = rawKey.substring(1);
        String[] key = rawKey.split("/");
        if(key.length < 2) {
            sender.sendMessage("Invalid key");
            return false;
        }
        NetworkTable table = inst.getTable(key[0]);
        for(int i = 1; i < key.length - 1; i++) {
            table = table.getSubTable(key[i]);
        }
        DoubleArraySubscriber robotPos = table.getDoubleArrayTopic(key[key.length - 1]).subscribe(new double[]{0.0,0.0}, PubSubOption.periodic(0.01));
        inst.startClient4("MineFRC");
        String ip = "localhost";
        int port = 5810;
        if(args.length > 1) ip = args[1];
        if (args.length > 2) port = Integer.parseInt(args[2]);
        inst.setServer(ip, port);
        sender.sendMessage("Connected!");

        // Create a blockDisplay entity
        botDisplay = ((Player) sender).getWorld().spawn(origin, BlockDisplay.class, entity -> {
            entity.setBlock(Material.DISPENSER.createBlockData());
            float width = 0.686f * scaleFactor;
            float height = 0.820f * scaleFactor;
            entity.setTransformation(new Transformation(new Vector3f(-width / 2, 0, -height/2), new AxisAngle4f(), new Vector3f(width, 12, height), new AxisAngle4f()));
        });



        schedule = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineFRC.getInstance(), () -> {
            Location pos = new Location(origin.getWorld(), origin.getX() - (robotPos.get()[0] * scaleFactor), origin.getY(), origin.getZ() + (robotPos.get()[1] * scaleFactor));
            pos.setYaw((float) ((-robotPos.get()[2]) - 90) % 360);
            botDisplay.teleport(pos);
        }, 0, 1);
        return true;
    }

    public void stopLoop() {
        if(schedule != -1) Bukkit.getScheduler().cancelTask(schedule);
        schedule = -1;
        botDisplay.remove();
    }
}