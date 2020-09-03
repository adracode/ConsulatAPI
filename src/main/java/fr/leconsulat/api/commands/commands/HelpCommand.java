package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.commands.Arguments;
import fr.leconsulat.api.commands.CommandManager;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.ranks.Rank;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class HelpCommand extends ConsulatCommand {
    
    private List<List<TextComponent>> titles = new ArrayList<>();
    private TextComponent close = new TextComponent("    §3§m-§e§m--§c§m---§e§m--§3§m-§a§m---------------§3§m-§e§m--§c§m---§e§m--§3§m-");
    private List<TextComponent> previous = new ArrayList<>();
    private List<TextComponent> next = new ArrayList<>();
    
    public HelpCommand(){
        super(ConsulatAPI.getConsulatAPI(), "help");
        setDescription("Voir l'aide").
                setUsage("/help - Voir l'aide\n" +
                        "/help <page> - Voir l'aide d'une page\n" +
                        "/help <commande> - Affiche l'aide d'une commande").
                setAliases("aide").
                setRank(Rank.INVITE).
                suggest(Arguments.word("commande").suggests((context, builder) -> {
                            ConsulatPlayer player = getConsulatPlayerFromContext(context.getSource());
                            if(player == null){
                                return builder.buildFuture();
                            }
                            Arguments.suggest(
                                    CommandManager.getInstance().getCommands().values(),
                                    Command::getName, command -> command instanceof ConsulatCommand &&
                                            player.hasPermission(command.getPermission()),
                                    builder
                            );
                            return builder.buildFuture();
                        }),
                        RequiredArgumentBuilder.argument("page", IntegerArgumentType.integer(0)));
    }
    
    private TextComponent getTitle(int page, int size){
        while(size >= titles.size()){
            titles.add(new ArrayList<>());
        }
        List<TextComponent> pageTitles = titles.get(size);
        for(int i = pageTitles.size(); i <= page; ++i){
            pageTitles.add(new TextComponent("    §3§m-§e§m--§c§m---§e§m--§3§m-§a Commandes [" + i + "/" + size + "] §3§m-§e§m--§c§m---§e§m--§3§m-"));
        }
        return pageTitles.get(page);
    }
    
    private TextComponent getPrevious(int page){
        if(page < 0){
            return null;
        }
        for(int i = previous.size(); i <= page; ++i){
            TextComponent current = new TextComponent("                          §b« Précédent");
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Page précédente").color(ChatColor.GRAY).create()));
            current.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + i));
            previous.add(current);
        }
        return previous.get(page);
    }
    
    private TextComponent getNext(int page){
        for(int i = next.size(); i <= page; ++i){
            TextComponent current = new TextComponent("                          §bSuivant »");
            current.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Page suivante").color(ChatColor.GRAY).create()));
            current.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + i));
            next.add(current);
        }
        return next.get(page);
    }
    
    public static void formatCommandDescription(@NotNull ConsulatPlayer sender, ConsulatCommand command, boolean showPrefix){
        List<String> aliases = command.getAliases();
        String aliasesStr = aliases.toString();
        sender.sendMessage(new ComponentBuilder(
                (showPrefix ? "§b[§aCommande§b]" : ""))
                .append(" /" + command.getName() + " - ").color(ChatColor.YELLOW)
                .append(command.getCommandDescription()).color(ChatColor.GRAY)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        (aliases.isEmpty() ? "" : "§cAlias: §7" + aliasesStr.substring(1, aliasesStr.length() - 1) + "\n") + "§cUtilisation:\n§7" + command.getUsage()).create()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/" + command.getName() + " "))
                .create());
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        int page;
        if(args.length == 0){
            page = 1;
        } else {
            try {
                page = Integer.parseInt(args[0]);
            } catch(NumberFormatException e){
                Command command = CommandManager.getInstance().getCommand(args[0]);
                if(!(command instanceof ConsulatCommand)){
                    page = 1;
                } else {
                    if(sender.hasPermission(command.getPermission())){
                        formatCommandDescription(sender, (ConsulatCommand)command, true);
                        return;
                    } else {
                        page = 1;
                    }
                }
            }
        }
        TreeSet<ConsulatCommand> commandsShowed = new TreeSet<>();
        for(Command cmd : CommandManager.getInstance().getCommands().values()){
            if(cmd instanceof ConsulatCommand && sender.hasPermission(cmd.getPermission())){
                commandsShowed.add((ConsulatCommand)cmd);
            }
        }
        if(commandsShowed.size() == 0){
            sender.sendMessage("§cUne erreur est survenue");
            return;
        }
        int pages;
        float nbCmd = commandsShowed.size();
        if((nbCmd / 5) > (int)(nbCmd / 5)){
            pages = (int)(nbCmd / 5) + 1;
        } else {
            pages = (int)(nbCmd / 5);
        }
        if(page > pages){
            page = pages;
        }
        sender.sendMessage("");
        sender.sendMessage(getTitle(page, pages));
        if(page != 1){
            sender.sendMessage(getPrevious(page - 1));
        } else {
            sender.sendMessage("");
        }
        ConsulatCommand[] commands = commandsShowed.toArray(new ConsulatCommand[0]);
        for(int i = (page - 1) * 5; i < page * 5; i++){
            if(i < commandsShowed.size()){
                formatCommandDescription(sender, commands[i], false);
            }
        }
        if(page != pages){
            sender.sendMessage(getNext(page + 1));
        } else {
            sender.sendMessage("");
        }
        sender.sendMessage(close);
    }
    
}
