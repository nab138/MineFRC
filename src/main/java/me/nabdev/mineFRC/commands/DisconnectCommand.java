package me.nabdev.mineFRC.commands;

import edu.wpi.first.networktables.NetworkTableInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DisconnectCommand  implements CommandExecutor {
    private final ConnectCommand connectCommand;

    public DisconnectCommand(ConnectCommand connectCommand) {
        this.connectCommand = connectCommand;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        connectCommand.stopLoop();
        NetworkTableInstance inst = NetworkTableInstance.getDefault();
        inst.disconnect();
        inst.stopClient();
        sender.sendMessage("Disconnected!");
        return true;
    }
}
