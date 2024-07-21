package me.nabdev.mineFRC;

import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import me.nabdev.mineFRC.commands.ConnectCommand;
import me.nabdev.mineFRC.commands.DisconnectCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;

public final class MineFRC extends JavaPlugin {
    public static MineFRC plugin;
    public ConnectCommand connectCommand;
    @Override
    public void onEnable() {
        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        WPIMathJNI.Helper.setExtractOnStaticLoad(false);


        try {
            CombinedRuntimeLoader.loadLibraries(MineFRC.class, "wpiutiljni", "wpimathjni", "ntcorejni");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        plugin = this;

        connectCommand = new ConnectCommand();
        Objects.requireNonNull(getCommand("connect")).setExecutor(connectCommand);
        Objects.requireNonNull(getCommand("disconnect")).setExecutor(new DisconnectCommand(connectCommand));
    }

    @Override
    public void onDisable() {
        connectCommand.stopLoop();
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.disconnect();
        inst.stopClient();
    }

    public static MineFRC getInstance() {
        return plugin;
    }
}
