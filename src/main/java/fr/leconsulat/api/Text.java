package fr.leconsulat.api;

import fr.leconsulat.api.player.ConsulatPlayer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

public final class Text {
    
    public static final String PREFIX = "§7[§6Consulat§7]§6 ";
    public static final String BROADCAST_PREFIX = ChatColor.RED + "§l[ANNONCE] ";
    public static final String MODERATION_PREFIX = ChatColor.DARK_GREEN + "(Staff)" + ChatColor.GRAY + "[" + ChatColor.GOLD + "Modération" + ChatColor.GRAY + "] ";
    public static final String ANNOUNCE_PREFIX = ChatColor.GRAY + "§l[" + ChatColor.GOLD + "Modération" + ChatColor.GRAY + "§l]§r ";
    
    public static final String ERROR = PREFIX + "§cUne erreur est survenue.";
    public static final String NO_CUSTOM_RANK = PREFIX + "§cTu n'as pas de grade personnalisé.";
    public static final String CUSTOM_RANK_RESET = PREFIX + "§aTon grade personnalisé a été réinitialisé.";
    public static final String CHOOSE_CUSTOM_RANK_COLOR = PREFIX + "§6Choisis la couleur de ton grade: ";
    public static final String PLAYER_DOESNT_EXISTS = PREFIX + "§cCe joueur n'existe pas.";
    public static final String MAYBE_UNBAN_PLAYER = Text.MODERATION_PREFIX + "Si le joueur était banni, il a été dé-banni.";
    public static final String MAYBE_UNMUTE_PLAYER = PREFIX + "Si le joueur était mute, il a été dé-mute." ;
    public static final String UNMUTE_PLAYER = PREFIX + "§aJoueur démute." ;
    public static final String PLAYER_NOT_MUTE = PREFIX + "§cCe joueur n'est pas mute.";
    public static final String NO_ANTECEDENT = PREFIX + "§cCe joueur n'a pas d'antécédents.";
    public static final String ALREADY_MUTED = PREFIX + "§cCe joueur est déjà mute.";
    public static final String PLAYER_NOT_CONNECTED = PREFIX + "§cCe joueur n'est pas connecté.";
    public static final String YOU_KICKED_PLAYER = PREFIX + "§aJoueur exclu !";
    public static final String API_ALREADY_ON = PREFIX + "§cTon API est déjà activé.";
    public static final String API_ON = PREFIX + "§7Ton API est maintenant §aactivé";
    public static final String API_ALREADY_OFF = PREFIX + "§cTon API est déjà désactivé.";
    public static final String API_OFF = PREFIX + "§7Ton API est maintenant §adésactivé";
    
    public static String CUSTOM_RANK_COLOR_CHOSEN(ChatColor color){return PREFIX + "§7Tu as choisi " + color + "cette couleur !\n§6Écris dans le chat le nom de ton grade: §o(10 caractères maximum, celui-ci aura des crochets par défaut)";}
    public static String NEW_CUSTOM_RANK(ConsulatPlayer player){return PREFIX + "§6Voilà ton nouveau grade: " + player.getDisplayName();}
    public static String BRODCAST(String player, String message){return BROADCAST_PREFIX + "§4" + player + "§7: §b" + message;}
    public static String KICK_PLAYER(String reason){return "§7§l§m ----[§r §6§lLe Consulat §7§l§m]----\n\n§cTu as été exclu.\n§cRaison: §4" + reason;}
    
    public static TextComponent SANCTION_BANNED(String targetName, String sanctionName, String duration, String modName, int recidive){
        TextComponent textComponent = new TextComponent(Text.MODERATION_PREFIX + "§c" + targetName + "§4 a été banni.");
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Motif: §8" + sanctionName +
                        "§7\nPendant: §8" + duration +
                        "§7\nPar: §8" + modName +
                        "§7\nRécidive: §8" + recidive
                ).create()));
        return textComponent;
    }
    public static TextComponent SANCTION_MUTED(String targetName, String sanctionName, String duration, String modName, int recidive){
        TextComponent textComponent = new TextComponent(Text.MODERATION_PREFIX + "§e" + targetName + "§6 a été mute.");
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("§7Motif: §8" + sanctionName +
                        "§7\nPendant: §8" + duration +
                        "§7\nPar: §8" + modName +
                        "§7\nRécidive: §8" + recidive
                ).create()));
        return textComponent;
    }
    public static String PLAYER_BANNED(String player){return ANNOUNCE_PREFIX + "§c" + player + "§4 a été banni.";}
    public static String PLAYER_MUTED(String player){return Text.ANNOUNCE_PREFIX + "§6" + player + " §ea été mute.";}
    
    public static String HELP_API(ConsulatPlayer player){
        return PREFIX + "§7L'API est un moyen d'obtenir certaines informations publiquement (à définir)." +
                "Ton §cAPI §7est " + (player.isApi() ? "§aactivé" : "§cdésactivé");
    }
}
