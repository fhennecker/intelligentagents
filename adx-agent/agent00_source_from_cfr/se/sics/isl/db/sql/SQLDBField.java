/*
 * Decompiled with CFR 0_110.
 */
package se.sics.isl.db.sql;

import java.io.PrintStream;
import se.sics.isl.db.DBField;

public class SQLDBField
extends DBField {
    private boolean isSQLite = false;

    public SQLDBField(String name, int type, int size, int flags, Object defaultValue, boolean isSQLite) {
        super(name, type, size, flags, defaultValue);
        this.isSQLite = isSQLite;
    }

    protected void addBasicType(StringBuffer sb) {
        sb.append('`').append(this.name).append('`').append(' ').append(this.getTypeAsString(this.type));
        if (this.defaultValue != null) {
            sb.append(" DEFAULT '").append(this.defaultValue).append('\'');
        }
        if ((this.flags & 2) != 0) {
            sb.append(" AUTO_INCREMENT");
        }
    }

    protected void addExtraTypeInfo(StringBuffer sb) {
        if ((this.flags & 16) != 0) {
            sb.append(", PRIMARY KEY(`").append(this.name).append("`)");
        }
        if (!this.isSQLite && (this.flags & 4) != 0) {
            sb.append(", INDEX(`").append(this.name).append("`)");
        }
        if ((this.flags & 1) != 0) {
            sb.append(", UNIQUE(`").append(this.name).append("`)");
        }
    }

    protected void addExtraTypeInfoChange(StringBuffer sb) {
        if ((this.flags & 16) != 0) {
            sb.append(", DROP PRIMARY KEY, ADD PRIMARY KEY(`").append(this.name).append("`)");
        }
        if (!this.isSQLite && (this.flags & 4) != 0) {
            sb.append(", ADD INDEX(`").append(this.name).append("`)");
        }
        if ((this.flags & 1) != 0) {
            sb.append(", ADD UNIQUE(`").append(this.name).append("`)");
        }
    }

    private String getTypeAsString(int type) {
        if (this.isSQLite) {
            switch (type) {
                case 0: 
                case 1: 
                case 5: {
                    return "INTEGER";
                }
                case 2: {
                    return "TEXT";
                }
                case 3: {
                    return "REAL";
                }
                case 4: {
                    if (this.size > 255) {
                        return "BLOB";
                    }
                    return "TEXT";
                }
            }
            System.err.println("SQLDBField: unknown type: " + type);
            return "INTEGER";
        }
        switch (type) {
            case 0: {
                return "INT";
            }
            case 1: {
                return "BIGINT";
            }
            case 2: {
                return "TIMESTAMP";
            }
            case 3: {
                return "DOUBLE";
            }
            case 4: {
                if (this.size > 255) {
                    return "BLOB";
                }
                return "VARCHAR(" + (this.size > 0 ? this.size : 80) + ')';
            }
            case 5: {
                return "TINYINT";
            }
        }
        System.err.println("SQLDBField: unknown type: " + type);
        return "INT";
    }
}

