package fr.leconsulat.api.gui.events;

import fr.leconsulat.api.gui.Gui;
import fr.leconsulat.api.gui.PagedGui;
import fr.leconsulat.api.player.ConsulatPlayer;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event appelé lorsque qu'un Gui est fermé
 */
public class GuiCloseEvent<T> implements Cancellable {

    @NotNull private final ConsulatPlayer player;
    @NotNull private final PagedGui<T> pagedGui;
    @NotNull private final T data;
    @Nullable private final Gui<?> father;
    private boolean openFatherGui;
    private boolean cancelled;
    
    public GuiCloseEvent(@NotNull ConsulatPlayer player,
                         @NotNull PagedGui<T> pagedGui,
                         @NotNull T data,
                         @Nullable Gui<?> father,
                         boolean openFatherGui){
        this.player = player;
        this.pagedGui = pagedGui;
        this.data = data;
        this.father = father;
        this.openFatherGui = openFatherGui;
    }
    
    @NotNull
    public PagedGui<T> getPagedGui(){
        return pagedGui;
    }
    
    @NotNull
    public Gui<T> getGui(){
        return pagedGui.getGui();
    }
    
    @NotNull
    public ConsulatPlayer getPlayer(){
        return player;
    }
    
    @NotNull
    public T getData(){
        return data;
    }
    
    @Nullable
    public Gui<?> getFather(){
        return father;
    }
    
    @Override
    public boolean isCancelled(){
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled){
        this.cancelled = cancelled;
    }
 
    
    public boolean openFatherGui(){
        return openFatherGui;
    }
    
    public void setOpenFatherGui(boolean openFatherGui){
        this.openFatherGui = openFatherGui;
    }
    
}
