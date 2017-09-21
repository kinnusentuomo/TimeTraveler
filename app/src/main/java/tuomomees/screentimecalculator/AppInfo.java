package tuomomees.screentimecalculator;

import android.graphics.drawable.Drawable;

/**
 * Luokan on luonut tuomo päivämäärällä 21.9.2017.
 */


//Luokka, johon on tarkoitus tallettaa applikaatiot ja niiden käyttötiedot olioina
public class AppInfo {

    private String appName = null;
    private String appPackageName = null;
    private long appTotalUsageTime = 0;
    private int appRanking = 0;
    private long appLastTimeUsedMillis = 0;
    private Drawable appIcon = null;

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public long getAppLastTimeUsedMillis() {
        return appLastTimeUsedMillis;
    }

    public void setAppLastTimeUsedMillis(long appLastTimeUsedMillis) {
        this.appLastTimeUsedMillis = appLastTimeUsedMillis;
    }

    public int getAppRanking() {
        return appRanking;
    }

    public void setAppRanking(int appRanking) {
        this.appRanking = appRanking;
    }

    public long getAppTotalUsageTime() {
        return appTotalUsageTime;
    }

    public void setAppTotalUsageTime(long appTotalUsageTime) {
        this.appTotalUsageTime = appTotalUsageTime;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
