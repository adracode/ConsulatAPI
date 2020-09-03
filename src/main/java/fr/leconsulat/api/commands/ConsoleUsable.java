package fr.leconsulat.api.commands;

import org.bukkit.command.CommandSender;

public interface ConsoleUsable {
    
    void onConsoleUse(CommandSender sender, String[] args);
    
}
