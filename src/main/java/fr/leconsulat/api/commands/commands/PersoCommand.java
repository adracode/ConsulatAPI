package fr.leconsulat.api.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.commands.ConsulatCommand;
import fr.leconsulat.api.player.ConsulatPlayer;
import fr.leconsulat.api.player.CustomRankState;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PersoCommand extends ConsulatCommand {
    
    private final BaseComponent[] customRankColors;
    private Set<String> forbiddenCustomRank = new HashSet<>(Arrays.asList(
            "modo", "moderateur", "modérateur", "admin", "animateur", "partenaire", "youtubeur", "streamer", "ami",
            "fonda", "dev", "builder", "fondateur"));
    private Set<ChatColor> allowedColors = new HashSet<>();
    
    public PersoCommand(){
        super(ConsulatAPI.getConsulatAPI(), "perso");
        setDescription("Gérer son grade personnalisé").
                setUsage("/perso - Gérer son grade").
                suggest((listener) -> {
                            ConsulatPlayer player = getConsulatPlayer(listener);
                            return player != null && player.hasCustomRank();
                        },
                        LiteralArgumentBuilder.literal("reset"));
        Map<ChatColor, String> colors = new LinkedHashMap<>();
        colors.put(ChatColor.DARK_RED, "Rouge foncé");
        //colors.put(ChatColor.RED, "Rouge");
        colors.put(ChatColor.GOLD, "Orange");
        colors.put(ChatColor.YELLOW, "Jaune");
        colors.put(ChatColor.GREEN, "Vert clair");
        colors.put(ChatColor.DARK_GREEN, "Vert foncé");
        colors.put(ChatColor.DARK_BLUE, "Bleu foncé");
        colors.put(ChatColor.BLUE, "Bleu");
        colors.put(ChatColor.DARK_AQUA, "Turquoise");
        colors.put(ChatColor.AQUA, "Bleu clair");
        colors.put(ChatColor.LIGHT_PURPLE, "Rose");
        colors.put(ChatColor.DARK_PURPLE, "Violet");
        colors.put(ChatColor.BLACK, "Noir");
        colors.put(ChatColor.DARK_GRAY, "Gris foncé");
        colors.put(ChatColor.GRAY, "Gris");
        colors.put(ChatColor.WHITE, "Blanc");
        ComponentBuilder customRankColors = new ComponentBuilder("");
        for(Map.Entry<ChatColor, String> colorData : colors.entrySet()){
            ChatColor color = colorData.getKey();
            customRankColors.append(new ComponentBuilder(
                    colorData.getValue() + (color == ChatColor.WHITE ? "" : "§r§7 - ")).
                    color(net.md_5.bungee.api.ChatColor.getByChar(color.getChar())).
                    event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oChoisir cette couleur").create())).
                    event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/perso " + color.toString().charAt(1))).create());
        }
        this.customRankColors = customRankColors.create();
        this.allowedColors.addAll(colors.keySet());
    }
    
    @Override
    public void onCommand(@NotNull ConsulatPlayer sender, @NotNull String[] args){
        if(!sender.hasCustomRank()){
            sender.sendMessage(Text.NO_CUSTOM_RANK);
            return;
        }
        if(args.length >= 1 && args[0].equalsIgnoreCase("reset")){
            sender.resetCustomRank();
            sender.sendMessage(Text.CUSTOM_RANK_RESET);
            return;
        }
        switch(sender.getPersoState()){
            case START:
                sender.setPersoState(CustomRankState.PREFIX_COLOR);
                sender.sendMessage(Text.CHOOSE_CUSTOM_RANK_COLOR);
                sender.sendMessage(customRankColors);
                break;
            case PREFIX_COLOR:{
                if(args.length != 1){
                    return;
                }
                ChatColor color = ChatColor.getByChar(args[0]);
                if(!allowedColors.contains(color)){
                    return;
                }
                sender.setColorPrefix(color);
                sender.setPersoState(CustomRankState.PREFIX);
                sender.sendMessage(Text.CUSTOM_RANK_COLOR_CHOSEN(color));
            }
            break;
            case NAME_COLOR:
                if(args.length != 1){
                    return;
                }
                ChatColor color = ChatColor.getByChar(args[0]);
                if(!allowedColors.contains(color)){
                    return;
                }
                sender.sendMessage(Text.NEW_CUSTOM_RANK(sender));
                sender.applyCustomRank();
                sender.setPersoState(CustomRankState.START);
                break;
        }
    }
    
    public BaseComponent[] getCustomRankColors(){
        return customRankColors;
    }
    
    public boolean isCustomRankForbidden(String rank){
        return forbiddenCustomRank.contains(rank.toLowerCase());
    }
}
