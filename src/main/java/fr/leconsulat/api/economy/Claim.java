package fr.leconsulat.api.economy;

import java.util.ArrayList;

public class Claim {

    private int firstX;
    private int firstZ;
    public ArrayList<String> access;

    public Claim(int firstX, int firstZ, ArrayList<String> access)  {
        this.firstX = firstX;
        this.firstZ = firstZ;
        this.access = access;
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

}
