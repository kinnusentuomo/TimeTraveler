package tuomomees.screentimecalculator;

import android.graphics.drawable.Drawable;

/**
 * Luokan on luonut tuomo päivämäärällä 22.9.2017.
 */


//KÄYTETÄÄN LISTVIEWIN MUOTOILUUN


public class Model {

    private Drawable icon;
    private String title;
    private String counter;

    private boolean isGroupHeader = false;


    public Model(Drawable icon, String title, String counter) {
        super();
        this.icon = icon;
        this.title = title;
        this.counter = counter;
    }
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCounter() {
        return counter;
    }
    public void setCounter(String counter) {
        this.counter = counter;
    }
    public boolean isGroupHeader() {
        return isGroupHeader;
    }
    public void setGroupHeader(boolean isGroupHeader) {
        this.isGroupHeader = isGroupHeader;
    }



}
