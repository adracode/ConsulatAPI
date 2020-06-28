package fr.leconsulat.api.gui.gui.template;

import fr.leconsulat.api.gui.GuiItem;
import fr.leconsulat.api.gui.gui.BaseGui;
import fr.leconsulat.api.gui.gui.module.api.Datable;
import org.jetbrains.annotations.NotNull;

public class DataGui<T> extends BaseGui implements Datable<T> {
    
    private final T data;
    
    public DataGui(T data, @NotNull String name, int line, GuiItem... items){
        super(name, line, items);
        this.data = data;
    }
    
    @Override
    public T getData(){
        return data;
    }
}
