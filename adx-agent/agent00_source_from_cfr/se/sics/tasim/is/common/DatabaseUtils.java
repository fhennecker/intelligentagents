/*
 * Decompiled with CFR 0_110.
 */
package se.sics.tasim.is.common;

import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.Database;
import se.sics.isl.db.PrefixDatabase;
import se.sics.isl.util.ConfigManager;

public class DatabaseUtils {
    private static final Logger log = Logger.getLogger(DatabaseUtils.class.getName());

    private DatabaseUtils() {
    }

    public static Database createDatabase(ConfigManager config, String configPrefix) {
        String databaseName = config.getProperty(String.valueOf(configPrefix) + "database", "infodb");
        return DatabaseUtils.createDatabase(config, configPrefix, databaseName.toLowerCase());
    }

    public static Database createUserDatabase(ConfigManager config, String configPrefix, Database parentDatabase) {
        String userDatabaseName = config.getProperty(String.valueOf(configPrefix) + "user.database");
        return userDatabaseName != null ? DatabaseUtils.createDatabase(config, String.valueOf(configPrefix) + "user.", userDatabaseName.toLowerCase()) : parentDatabase;
    }

    public static Database createChildDatabase(ConfigManager config, String configPrefix, String databasePrefix, Database parentDatabase) {
        return new PrefixDatabase(DatabaseUtils.createDatabasePrefix(databasePrefix), parentDatabase, config, configPrefix);
    }

    private static String createDatabasePrefix(String databasePrefix) {
        databasePrefix = databasePrefix.toLowerCase();
        StringBuffer sb = null;
        char c = databasePrefix.charAt(0);
        if (c < 'a' || c > 'z') {
            sb = new StringBuffer().append('s');
        }
        int i = 0;
        int n = databasePrefix.length();
        while (i < n) {
            c = databasePrefix.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '_')) {
                if (sb == null) {
                    sb = new StringBuffer();
                    if (i > 0) {
                        sb.append(databasePrefix.substring(0, i));
                    }
                }
                sb.append('_');
            } else if (sb != null) {
                sb.append(c);
            }
            ++i;
        }
        if (sb != null) {
            return sb.append('_').toString();
        }
        return String.valueOf(databasePrefix) + '_';
    }

    private static Database createDatabase(ConfigManager config, String configPrefix, String databaseName) {
        String databaseDriver = config.getProperty(String.valueOf(configPrefix) + "database.driver", "se.sics.isl.db.file.FileDatabase");
        Database database = null;
        do {
            try {
                Database base = (Database)Class.forName(databaseDriver).newInstance();
                base.init(databaseName, config, String.valueOf(configPrefix) + "database.");
                database = base;
                continue;
            }
            catch (Exception e) {
                log.log(Level.SEVERE, "could not create database driver of type '" + databaseDriver + '\'', e);
                log.severe("will retry database " + databaseName + " in 60 seconds...");
                try {
                    Thread.sleep(60000);
                    continue;
                }
                catch (InterruptedException var6_7) {
                    // empty catch block
                }
            }
        } while (database == null);
        return database;
    }
}

