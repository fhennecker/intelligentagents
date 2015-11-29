/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.sics.isl.db.DBField;
import se.sics.isl.db.DBObject;
import se.sics.isl.db.DBResult;
import se.sics.isl.db.DBTable;
import se.sics.isl.db.Database;
import se.sics.isl.util.ArgumentManager;
import se.sics.isl.util.ConfigManager;
import se.sics.isl.util.IllegalConfigurationException;

public class Backuper {
    private static final Logger log = Logger.getLogger(Backuper.class.getName());

    private Backuper() {
    }

    public static void main(String[] args) throws IllegalConfigurationException {
        Logger.getLogger("").setLevel(Level.FINEST);
        ArgumentManager config = new ArgumentManager("Backuper", args);
        config.addOption("config", "configfile", "set the config file to use");
        config.addOption("source.name", "name", "set the source database name");
        config.addOption("target.name", "name", "set the target database name");
        config.addOption("source.driver", "driver", "set the source driver");
        config.addOption("target.driver", "driver", "set the target driver");
        config.addOption("source.table", "driver", "set the source table");
        config.addOption("target.table", "driver", "set the target table");
        config.addOption("dump", "dump the source table to standard out");
        config.addHelp("h", "show this help message");
        config.addHelp("help");
        config.validateArguments();
        String configFile = config.getArgument("config");
        if (configFile != null) {
            try {
                config.loadConfiguration(configFile);
                config.removeArgument("config");
            }
            catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                config.usage(1);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        config.finishArguments();
        String sourceName = Backuper.getConfig(config, "source.name", true);
        String sourceDriver = Backuper.getConfig(config, "source.driver", true);
        String sourceTable = Backuper.getConfig(config, "source.table", true);
        boolean dump = config.getPropertyAsBoolean("dump", false);
        String targetName = Backuper.getConfig(config, "target.name", !dump);
        String targetDriver = Backuper.getConfig(config, "target.driver", !dump);
        String targetTable = Backuper.getConfig(config, "target.table", !dump);
        try {
            Database source = (Database)Class.forName(sourceDriver).newInstance();
            source.init(sourceName, config, "");
            DBTable s = source.getTable(sourceTable);
            if (s == null) {
                throw new IllegalConfigurationException("source table '" + sourceTable + "' does not exist");
            }
            if (dump) {
                DBResult result = s.select();
                int count = 1;
                while (result.next()) {
                    System.out.print("" + count++ + ":");
                    int i = 0;
                    int n = result.getFieldCount();
                    while (i < n) {
                        DBField field = result.getField(i);
                        String fieldName = field.getName();
                        Object value = result.getObject(fieldName);
                        System.out.print("|" + value);
                        ++i;
                    }
                    System.out.println();
                }
                result.close();
            } else {
                Database target = (Database)Class.forName(targetDriver).newInstance();
                target.init(targetName, config, "");
                DBTable t = target.createTable(targetTable);
                DBResult result = s.select();
                DBObject object = new DBObject();
                if (result.next()) {
                    DBField field;
                    int i = 0;
                    int n = result.getFieldCount();
                    while (i < n) {
                        field = result.getField(i);
                        t.createField(field.getName(), field.getType(), field.getSize(), field.getFlags(), field.getDefaultValue());
                        ++i;
                    }
                    do {
                        i = 0;
                        n = result.getFieldCount();
                        while (i < n) {
                            field = result.getField(i);
                            String fieldName = field.getName();
                            Object value = result.getObject(fieldName);
                            if (value != null) {
                                object.setObject(fieldName, value);
                            }
                            ++i;
                        }
                        t.insert(object);
                        object.clear();
                    } while (result.next());
                }
                target.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getConfig(ConfigManager config, String name, boolean required) throws IllegalConfigurationException {
        String value = config.getProperty(name);
        if (required && value == null) {
            throw new IllegalConfigurationException("missing " + name);
        }
        return value;
    }
}

