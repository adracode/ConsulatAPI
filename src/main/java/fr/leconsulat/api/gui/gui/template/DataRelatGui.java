package fr.leconsulat.api.gui.gui.template;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.event.GuiClickEvent;
import fr.leconsulat.api.gui.event.GuiCloseEvent;
import fr.leconsulat.api.gui.event.GuiOpenEvent;
import fr.leconsulat.api.gui.event.GuiRemoveEvent;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.module.DatableGui;
import fr.leconsulat.api.gui.gui.module.RelationnableGui;
import fr.leconsulat.api.gui.gui.module.api.Datable;
import fr.leconsulat.api.gui.gui.module.api.Relationnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataRelatGui<T> extends BaseGui implements Datable<T>, Relationnable {
    
    private DatableGui<T> dataGui;
    private RelationnableGui relationnableGui;
    
    public DataRelatGui(T data, @NotNull String name, int line, GuiItem... items){
        super(name, line, items);
        this.relationnableGui = new RelationnableGui(this);
        this.dataGui = new DatableGui<>(this, data);
    }
    
    @Override
    public T getData(){
        return dataGui.getData();
    }
    
    @Override
    public boolean hasFather(){
        return relationnableGui.hasFather();
    }
    
    @Override
    public @NotNull Relationnable getFather(){
        return relationnableGui.getFather();
    }
    
    @Override
    public Relationnable setFather(@Nullable Relationnable father){
        relationnableGui.setFather(father);
        return this;
    }
    
    @Override
    public void addChild(Object key, @NotNull Relationnable gui){
        relationnableGui.addChild(key, gui);
    }
    
    @Override
    public Relationnable getChild(Object key){
        return relationnableGui.getChild(key);
    }
    
    @Override
    public Relationnable createChild(@Nullable Object key){
        throw new UnsupportedOperationException();
    }
    
    @Override
    public @Nullable Relationnable getLegacyChild(@Nullable Object key){
        return relationnableGui.getLegacyChild(key);
    }
    
    @Override
    public void removeChild(Object key){
        relationnableGui.removeChild(key);
    }
    
    @Override
    public String buildInventoryTitle(){
        //Peut être appelé avant l'initialisation de relationnableGui
        if(relationnableGui == null){
            return super.buildInventoryTitle();
        }
        return relationnableGui.buildInventoryTitle();
    }
    
    @Override
    public void onOpen(GuiOpenEvent event){
    }
    
    @Override
    public void onClose(GuiCloseEvent event){
    }
    
    @Override
    public void onClick(GuiClickEvent event){
    }
    
    @Override
    public void onRemove(GuiRemoveEvent event){
    }
    
    @Override
    public void setTitle(){
        super.setTitle();
    }
}
