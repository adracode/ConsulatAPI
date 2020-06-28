package fr.leconsulat.api.gui;

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
    
    @SuppressWarnings("unchecked")
    public <A extends Datable<T>> @NotNull A getGui(T data){
        return (A)Objects.requireNonNull(getGui(true, data));
    }
    
    public boolean removeGui(T data){
        Datable<T> removed = guis.remove(data);
        return removed != null;
    }
    
    void remove(@NotNull DataRelatGui<T> key){
        this.guis.remove(key.getData());
    }
    
    @SuppressWarnings("unchecked")
    @Nullable
    public <A> Datable<A> getGui(boolean create, T key, Object... path){
        Datable<?> mainGui = guis.get(key);
        if(mainGui == null){
            if(create){
                mainGui = addGui(createGui(key));
                mainGui.onCreate();
            } else {
                return null;
            }
        }
        for(Object childKey : path){
            if(mainGui.getBaseGui() instanceof Relationnable){
                if(create){
                    mainGui = (Datable<?>)((Relationnable)mainGui.getBaseGui()).getChild(childKey);
                } else {
                    mainGui = (Datable<?>)((Relationnable)mainGui.getBaseGui()).getLegacyChild(childKey);
                    if(mainGui == null){
                        return null;
                    }
                }
            }
        }
        
        return (Datable<A>)mainGui;
    }
    
}
