package fr.leconsulat.api.guis.moderation;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.channel.ChannelManager;
import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.template.DataRelatGui;
import fr.leconsulat.api.moderation.MuteReason;
import fr.leconsulat.api.moderation.SanctionType;
import fr.leconsulat.api.moderation.sync.SanctionPlayer;
import fr.leconsulat.api.player.CPlayerManager;
import fr.leconsulat.api.player.ConsulatOffline;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MuteGui extends DataRelatGui<ConsulatOffline> {
    
    public MuteGui(ConsulatOffline player){
        super(player, "§6§lMute", 3);
    }
    
    @Override
    public void onOpen(GuiOpenEvent event){
        
        ConsulatOffline consulatOffline = getData();
        Player target = Bukkit.getPlayer(consulatOffline.getUUID());
        Player moderator = event.getPlayer().getPlayer();
        
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            HashMap<MuteReason, Integer> muteHistory;
            
            if(target == null){
                try {
                    muteHistory = getMuteHistory(consulatOffline);
                } catch(SQLException e){
                    Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                        moderator.sendMessage(Text.ERROR);
                        moderator.closeInventory();
                        e.printStackTrace();
                    });
                    return;
                }
            } else {
                ConsulatPlayer survivalPlayer = CPlayerManager.getInstance().getConsulatPlayer(target.getUniqueId());
                muteHistory = survivalPlayer.getMuteHistory();
            }
            
            MuteReason[] values = MuteReason.values();
            for(int i = 0; i < values.length; ++i){
                MuteReason mute = values[i];
                GuiItem item = IGui.getItem("§e" + mute.getSanctionName(), i, mute.getGuiMaterial());
                
                item.setDescription("§eDurée: §6" + mute.getFormatDuration(), "§7Récidive: " + (muteHistory.containsKey(mute) ? muteHistory.get(mute) : "0"));
                item.setAttachedObject(mute);
                setItem(i, item);
            }
        });
    }
    
    @Override
    public void onClose(GuiCloseEvent event){
        event.setOpenFatherGui(false);
    }
    
    @Override
    public void onClick(GuiClickEvent event){
        ConsulatOffline offlineTarget = getData();
        ConsulatPlayer target = CPlayerManager.getInstance().getConsulatPlayer(offlineTarget.getUUID());
        ConsulatPlayer muter = event.getPlayer();
        if(target != null && target.isMuted()){
            event.getPlayer().sendMessage(Text.ALREADY_MUTED);
            event.getPlayer().getPlayer().closeInventory();
            return;
        }
        
        GuiItem item = getItem(event.getSlot());
        List<String> description = item.getDescription();
        String recidive = description.get(1).split(":")[1].trim();
        int recidiveNumber = Integer.parseInt(recidive);
        double multiply = recidiveNumber * 1.5;
        
        if(multiply == 0) multiply = 1;
        
        long currentTime = System.currentTimeMillis();
        MuteReason muteReason = (MuteReason)getItem(event.getSlot()).getAttachedObject();
        double durationMute = (muteReason.getDurationSanction() * 1000) * multiply;
        long resultTime = currentTime + Math.round(durationMute);
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            try {
                ConsulatAPI.getConsulatAPI().getModerationDatabase().addSanction(
                        offlineTarget.getUUID(), offlineTarget.getName(), muter.getPlayer(),
                        "MUTE", muteReason.getSanctionName(), resultTime, currentTime);
                ConsulatAPI.getConsulatAPI().getModerationDatabase().mutePlayer(new SanctionPlayer(
                        resultTime, SanctionType.MUTE, offlineTarget.getUUID(), muteReason.name(), muter.getUUID()
                ));
            } catch(SQLException e){
                muter.sendMessage(Text.ERROR);
                e.printStackTrace();
            }
        });
        long durationRound = Math.round(durationMute);
        long days = ((durationRound / (1000 * 60 * 60 * 24)));
        long hours = ((durationRound / (1000 * 60 * 60)) % 24);
        long minutes = ((durationRound / (1000 * 60)) % 60);
        ChannelManager.getInstance().getChannel("staff").sendMessage(Text.SANCTION_MUTED(offlineTarget.getName(), muteReason.getSanctionName(), days + "J" + hours + "H" + minutes + "M", muter.getName(), recidiveNumber));
        muter.getPlayer().closeInventory();
    }
    
    private HashMap<MuteReason, Integer> getMuteHistory(ConsulatOffline consulatOffline) throws SQLException{
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT reason FROM antecedents WHERE playeruuid = ? AND sanction = 'MUTE' AND cancelled = 0");
        preparedStatement.setString(1, consulatOffline.getUUID().toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        
        HashMap<MuteReason, Integer> muteHistory = new HashMap<>();
        while(resultSet.next()){
            String reason = resultSet.getString("reason");
            MuteReason muteReason = Arrays.stream(MuteReason.values()).filter(ban -> ban.getSanctionName().equals(reason)).findFirst().orElse(null);
            
            if(muteReason != null){
                if(muteHistory.containsKey(muteReason)){
                    int number = muteHistory.get(muteReason);
                    muteHistory.put(muteReason, ++number);
                } else {
                    muteHistory.put(muteReason, 1);
                }
            }
        }
        
        return muteHistory;
    }
}
