package fr.leconsulat.api.gui.gui.module.api;

import fr.leconsulat.api.gui.gui.IGui;

public interface Datable<T> {
    
    T getData();
    
    IGui getGui();
    
}
