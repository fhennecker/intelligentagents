/*
 * Decompiled with CFR 0_110.
 */
package lombok.installer.eclipse;

import java.io.File;
import java.net.URL;
import lombok.installer.CorruptedIdeLocationException;
import lombok.installer.eclipse.EclipseLocation;

public class STSLocation
extends EclipseLocation {
    public STSLocation(String nameOfLocation, File pathToEclipseIni) throws CorruptedIdeLocationException {
        super(nameOfLocation, pathToEclipseIni);
    }

    public URL getIdeIcon() {
        return STSLocation.class.getResource("STS.png");
    }

    protected String getIniFileName() {
        return "STS.ini";
    }

    protected String getTypeName() {
        return "STS";
    }
}

