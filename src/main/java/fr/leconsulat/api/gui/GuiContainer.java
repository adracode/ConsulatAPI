package fr.leconsulat.api.gui;

import fr.leconsulat.api.gui.gui.IGui;
import fr.leconsulat.api.gui.gui.module.api.Datable;
import fr.leconsulat.api.gui.gui.module.api.Relationnable;
import fr.leconsulat.api.gui.gui.template.DataRelatGui;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Cette classe peut être vu comme la racine d'un arbre de Gui.
 * Elle n'est pas obligatoire pour créer un système de gui, mais
 * permet de stocker les différentes versions d'un même listener.
 *
 * @param <T> Le type de donnée utilsé par le GuiListener
 */
public abstract class GuiContainer<T> {
    
    //Contient les différents guis, accessibles par leur "data"
    private Map<T, Datable<T>> guis = new HashMap<>();
    
    public abstract Datable<T> createGui(T data);
    
    public Datable<T> addGui(Datable<T> gui){
        guis.put(gui.getData(), gui);
        return gui;
    }
    
    public @NotNull IGui getGui(T data){
        return Objects.requireNonNull(getGui(true, data));
    }
    
    public boolean removeGui(T data){
        Datable<T> removed = guis.remove(data);
        return removed != null;
    }
    
    void remove(@NotNull DataRelatGui<T> key){
        this.guis.remove(key.getData());
    }
    
    @Nullable
    public IGui getGui(boolean create, T key, Object... path){
        Datable<?> gui = guis.get(key);
        if(gui == null){
            if(create){
                gui = addGui(createGui(key));
                gui.getGui().onCreate();
            } else {
                return null;
            }
        }
        for(Object childKey : path){
            if(gui.getGui() instanceof Relationnable){
                if(create){
                    gui = (Datable<?>)((Relationnable)gui.getGui()).getChild(childKey);
                } else {
                    gui = (Datable<?>)((Relationnable)gui.getGui()).getLegacyChild(childKey);
                    if(gui == null){
                        return null;
                    }
                }
            }
        }
        if(gui == null){
            return null;
        }
        return gui.getGui();
    }
    
}
