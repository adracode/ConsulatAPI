package fr.leconsulat.api.guis.moderation;

import fr.leconsulat.api.ConsulatAPI;
import fr.leconsulat.api.Text;
import fr.leconsulat.api.gui.GuiContainer;
import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.GuiManager;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCreateEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Datable;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.gui.gui.template.DataPagedGui;
import fr.leconsulat.api.moderation.SanctionType;
import fr.leconsulat.api.moderation.SanctionedPlayer;
import fr.leconsulat.api.player.ConsulatOffline;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AntecedentsGui extends DataPagedGui<ConsulatOffline> {
    
    public AntecedentsGui(ConsulatOffline player){
        super(player, "§6§lAntécédents §7↠ §e" + player.getName(), 5);
        setDeco(Material.BLACK_STAINED_GLASS_PANE, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
        setDynamicItems(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);
        setTemplateItems(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
    }
    
    @Override
    public void onCreate(){
        ConsulatOffline consulatOffline = getData();
        Bukkit.getScheduler().runTaskAsynchronously(ConsulatAPI.getConsulatAPI(), () -> {
            try {
                List<SanctionedPlayer> sanctions = getAntecedents(consulatOffline.getUUID().toString());
                Bukkit.getScheduler().runTask(ConsulatAPI.getConsulatAPI(), () -> {
                    for(int i = 0; i < sanctions.size(); i++){
                        SanctionedPlayer sanctionObject = sanctions.get(i);
                        GuiItem item = IGui.getItem("§cSANCTION", i, sanctionObject.getSanctionType().getMaterial(),
                                "§6Le: §e" + sanctionObject.getSanctionAt(),
                                "§6Jusqu'au: §e" + sanctionObject.getExpire(),
                                "§6Motif: §e" + sanctionObject.getSanctionName(),
                                "§6Modérateur: §e" + sanctionObject.getMod_name(),
                                "§6Annulé: §e" + (sanctionObject.isCancelled() ? "Oui" : "Non"),
                                "§6Actif: §e" + (sanctionObject.isActive() ? "Oui" : "Non"));
                        addItem(item);
                    }
                });
            } catch(SQLException e){
                e.printStackTrace();
            }
        });
    }
    
    @Override
    public void onPageCreated(GuiCreateEvent event, Pageable pageGui){
        IGui gui = pageGui.getGui();
        int page = pageGui.getPage();
        if(page != 0){
            gui.setItem(IGui.getItem("§7Précédent", 38, Material.ARROW));
            getPage(page - 1).getGui().setItem(IGui.getItem("§7Suivant", 42, Material.ARROW));
            gui.setDeco(Material.BLACK_STAINED_GLASS_PANE, 42);
        }
    }
    
    @Override
    public void onPageOpened(GuiOpenEvent event, Pageable pageGui){
        if(pageGui.getPage() == 0 && pageGui.getGui().getItem(10) == null){
            event.getPlayer().getPlayer().closeInventory();
            event.getPlayer().sendMessage(Text.NO_ANTECEDENT);
        }
    }
    
    @Override
    public void onPageClick(GuiClickEvent event, Pageable pageGui){
        switch(event.getSlot()){
            case 38:
                if(pageGui.getGui().getItem(event.getSlot()).getType() == Material.ARROW){
                    getPage(pageGui.getPage() - 1).getGui().open(event.getPlayer());
                }
                break;
            case 42:
                if(pageGui.getGui().getItem(event.getSlot()).getType() == Material.ARROW){
                    getPage(pageGui.getPage() + 1).getGui().open(event.getPlayer());
                }
                break;
        }
    }
    
    private List<SanctionedPlayer> getAntecedents(String uuid) throws SQLException{
        PreparedStatement preparedStatement = ConsulatAPI.getDatabase().prepareStatement("SELECT * FROM antecedents WHERE playeruuid = ?");
        preparedStatement.setString(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        ArrayList<SanctionedPlayer> sanctions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        while(resultSet.next()){
            SanctionType sanctionType = SanctionType.valueOf(resultSet.getString("sanction"));
            String sanctionName = resultSet.getString("reason");
            
            calendar.setTimeInMillis(resultSet.getLong("applicated"));
            String sanctionAt = new SimpleDateFormat("dd/MM/yyyy 'à' kk:mm").format(calendar.getTime());
            
            calendar.setTimeInMillis(resultSet.getLong("expire"));
            String expire = new SimpleDateFormat("dd/MM/yyyy 'à' kk:mm").format(calendar.getTime());
            String moderatorName = resultSet.getString("modname");
            boolean isActive = resultSet.getBoolean("active");
            boolean isCancel = resultSet.getBoolean("cancelled");
            
            sanctions.add(new SanctionedPlayer(sanctionType, sanctionName, sanctionAt, expire, moderatorName, isActive, isCancel));
        }
        
        return sanctions;
    }
    
    public static class Container extends GuiContainer<ConsulatOffline> {
        
        private static Container instance;
        
        public Container(){
            if(instance != null){
                throw new IllegalStateException();
            }
            instance = this;
            GuiManager.getInstance().addContainer("antecedents", this);
        }
        
        @Override
        public Datable<ConsulatOffline> createGui(ConsulatOffline player){
            return new AntecedentsGui(player);
        }
    }
    
}