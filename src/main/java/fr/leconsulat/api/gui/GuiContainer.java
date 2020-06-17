package fr.leconsulat.api.gui;

import fr.leconsulat.api.gui.events.GuiRemoveEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class GuiContainer<T> extends GuiListener<T> {
    
    private Map<T, Gui<T>> guis = new HashMap<>();
    
    public GuiContainer(int line){
        super(line);
    }
    
    public Gui<T> addGui(Gui<T> gui){
        guis.put(gui.getData(), gui);
        return gui;
    }
    
    public Gui<T> getGui(T data){
        Gui<T> gui = guis.get(data);
        return gui == null ? addGui(createGui(data)) : gui;
    }
    
    public boolean removeGui(T data){
        Gui<T> removed = guis.remove(data);
        if(removed == null){
            return false;
        }
        GuiRemoveEvent<T> event = new GuiRemoveEvent<>(removed, data);
        removed.getListener().onRemove(event);
        return true;
    }
    
    @SuppressWarnings("unchecked")
    @Nullable
    public <A> Gui<A> getGui(boolean create, T key, Object... path){
        Gui<?> mainGui = guis.get(key);
        if(mainGui == null){
            if(create){
                mainGui = getGui(key);
            } else {
                return null;
            }
        }
        if(create){
            for(Object childKey : path){
                if(mainGui == null){
                    break;
                }
                mainGui = mainGui.getChild(childKey);
            }
        } else {
            for(Object childKey : path){
                if(mainGui == null){
                    break;
                }
                mainGui = mainGui.getLegacyChild(childKey);
            }
        }
        return (Gui<A>)mainGui;
    }
    
}
