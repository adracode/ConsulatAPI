package fr.leconsulat.api.gui;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe peut être vu comme la racine d'un arbre de Gui.
 * Elle n'est pas obligatoire pour créer un système de gui, mais
 * permet de stocker les différentes versions d'un même listener.
 * @param <T> Le type de donnée utilsé par le GuiListener
 */
public abstract class GuiContainer<T> extends GuiListener<T> {
    
    //Contient les différents guis, accessibles par leur "data"
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
        Gui<T> removed = guis.get(data);
        if(removed == null){
            return false;
        }
        removed.remove();
        return true;
    }
    
    @Override
    void remove(Gui<T> key){
        this.guis.remove(key.getData());
        super.remove(key);
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
