package fr.leconsulat.api.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Rollback {
    
    private List<Runnable> toPerform = new LinkedList<>();
    
    public void prepare(Runnable runnable){
        toPerform.add(runnable);
    }
    
    public void execute(){
        for(Iterator<Runnable> iterator = toPerform.iterator(); iterator.hasNext(); ){
            iterator.next().run();
            iterator.remove();
        }
    }
    
}
