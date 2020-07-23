package fr.leconsulat.api.task;

public interface Task {
    
    void onStart();
    
    void next();
    
    boolean isFinished();
    
    void onFinish();
    
}
