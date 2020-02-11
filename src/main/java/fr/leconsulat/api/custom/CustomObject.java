package fr.leconsulat.api.custom;

public class CustomObject {

    private boolean isBuyed;
    private String prefix;

    public CustomObject(boolean isBuyed, String prefix) {
        this.isBuyed = isBuyed;
        this.prefix = prefix;
    }

    public boolean isBuyed() {
        return isBuyed;
    }

    public void setBuyed(boolean buyed) {
        isBuyed = buyed;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
