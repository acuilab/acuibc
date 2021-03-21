package com.acuilab.bc.main.util;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 *
 * @see org.netbeans.modules.autoupdate.ui.actions.AutoupdateSettings
 * 此类没有被导出
 */
public class AutoupdateSettings {

    private static String tempIdeIdentity = null;
    private static final Logger err = Logger.getLogger (AutoupdateSettings.class.getName ());
    private static final String PROP_PERIOD = "period"; // NOI18N
    private static final String PROP_LAST_CHECK = "lastCheckTime"; // NOI18N
    
    public static final int EVERY_STARTUP = 0;
    public static final int EVERY_DAY = 1;
    public static final int EVERY_WEEK = 2;
    public static final int EVERY_2WEEKS = 3;
    public static final int EVERY_MONTH = 4;
    public static final int NEVER = 5;
    public static final int CUSTOM_CHECK_INTERVAL = 6;
    
    private AutoupdateSettings () {
    }
    
    public static int getPeriod () {
        return getPreferences ().getInt (PROP_PERIOD, EVERY_WEEK);
    }
    
    public static void setPeriod (int period) {
        err.log (Level.FINEST, "Called setPeriod (" + period +")");
        getPreferences ().putInt (PROP_PERIOD, period);
    }
    
    public static Date getLastCheck() {        
        long t = getPreferences ().getLong (PROP_LAST_CHECK, -1);
        return (t > 0) ? new Date (t) : null;

    }

    public static void setLastCheck (Date lastCheck) {
        err.log (Level.FINER, "Set the last check to " + lastCheck);
        if (lastCheck != null) {
            getPreferences().putLong (PROP_LAST_CHECK, lastCheck.getTime ());
        } else {
            getPreferences().remove (PROP_LAST_CHECK);
        }
    }
    
    private static Preferences getPreferences () {
        return NbPreferences.root ().node ("/org/netbeans/modules/autoupdate");
    }    
}
