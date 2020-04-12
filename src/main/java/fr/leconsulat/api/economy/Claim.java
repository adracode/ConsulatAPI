package fr.leconsulat.api.economy;

import java.util.ArrayList;

public class Claim {

    private int firstX;
    private int firstZ;
    private String description;

    public ArrayList<String> access;
    public Claim(int firstX, int firstZ, ArrayList<String> access, String description)  {
        this.firstX = firstX;
        this.firstZ = firstZ;
        this.access = access;
        this.description = description;
    }

    public int getX()
    {
        return firstX;
    }

    public int getZ()
    {
        return firstZ;
    }

    public ArrayList<String> getAccessedPlayerName()
    {
        return access;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
