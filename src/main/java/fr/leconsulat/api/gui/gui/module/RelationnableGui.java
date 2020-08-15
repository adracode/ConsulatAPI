package fr.leconsulat.api.gui.gui.module;

import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Pageable;
import fr.leconsulat.api.gui.gui.module.api.Relationnable;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RelationnableGui<Gui extends IGui & Relationnable> implements Relationnable {
    
    private final @NotNull Map<Object, Relationnable> children = new HashMap<>();
    private Gui gui;
    private @Nullable Relationnable father;
    
    public RelationnableGui(Gui gui){
        this.gui = gui;
    }
    
    @Override
    public boolean hasFather(){
        return father != null;
    }
    
    @Override
    public @NotNull Relationnable getFather(){
        if(father == null){
            throw new NullPointerException("Father is null, use hasFather before calling method");
        }
        return father;
    }
    
    @Override
    public Relationnable setFather(@Nullable Relationnable father){
        this.father = father;
        if(father != null){
            gui.setTitle();
            if(gui.isBackButton()){
                gui.setItem((gui.getLine() - 1) * 9, IGui.getItem("§cRetour", -1, Material.RED_STAINED_GLASS_PANE));
            }
        }
        return this;
    }
    
    @Override
    public void addChild(Object key, @NotNull Relationnable gui){
        gui.setFather(this.gui);
        children.put(key, gui);
    }
    
    @Override
    public Collection<Relationnable> getChildren(){
        return Collections.unmodifiableCollection(children.values());
    }
    
    @Override
    public @NotNull Relationnable getChild(Object key){
        Relationnable child = getLegacyChild(key);
        if(child == null){
            child = createChild(key);
            if(child == null){
                throw new NullPointerException("Child couldn't be created");
            }
            addChild(key, child);
            child.getGui().onCreate();
        }
        return child;
    }
    
    @Override
    public @Nullable Relationnable createChild(@Nullable Object key){
        return gui.createChild(key);
    }
    
    @Override
    public @Nullable Relationnable getLegacyChild(@Nullable Object key){
        return children.get(key);
    }
    
    @Override
    public void removeChild(Object key){
        children.remove(key);
    }
    
    @Override
    public IGui getGui(){
        return gui;
    }
    
    @Override
    public void setTitle(){
        for(Relationnable child : children.values()){
            child.getGui().setTitle();
            if(child instanceof Pageable){
                ((Pageable)child).getMainPage().setTitle();
            }
        }
    }
    
    @Override
    public String buildInventoryTitle(String title){
        if(hasFather()){
            return getFather().getGui().getName() + " > " + title;
        }
        return title;
    }
    
}
