/*
 * Decompiled with CFR 0_110.
 * 
 * Could not load the following classes:
 *  org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 */
package lombok.core.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.core.debug.DebugSnapshot;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

public class DebugSnapshotStore {
    public static final DebugSnapshotStore INSTANCE = new DebugSnapshotStore();
    public static boolean GLOBAL_DSS_DISABLE_SWITCH = true;
    private final Map<CompilationUnitDeclaration, List<DebugSnapshot>> map = new WeakHashMap<CompilationUnitDeclaration, List<DebugSnapshot>>();

    public /* varargs */ void snapshot(CompilationUnitDeclaration owner, String message, Object ... params) {
        if (GLOBAL_DSS_DISABLE_SWITCH) {
            return;
        }
        DebugSnapshot snapshot = new DebugSnapshot(owner, 1, message, params);
        Map<CompilationUnitDeclaration, List<DebugSnapshot>> map = this.map;
        synchronized (map) {
            List<DebugSnapshot> list = this.map.get((Object)owner);
            if (list == null) {
                list = new ArrayList<DebugSnapshot>();
                this.map.put(owner, list);
                list.add(snapshot);
            } else if (!list.isEmpty()) {
                list.add(snapshot);
            }
        }
    }

    public /* varargs */ void log(CompilationUnitDeclaration owner, String message, Object ... params) {
        if (GLOBAL_DSS_DISABLE_SWITCH) {
            return;
        }
        DebugSnapshot snapshot = new DebugSnapshot(owner, -1, message, params);
        Map<CompilationUnitDeclaration, List<DebugSnapshot>> map = this.map;
        synchronized (map) {
            List<DebugSnapshot> list = this.map.get((Object)owner);
            if (list == null) {
                list = new ArrayList<DebugSnapshot>();
                this.map.put(owner, list);
                list.add(snapshot);
            } else if (!list.isEmpty()) {
                list.add(snapshot);
            }
        }
    }

    public /* varargs */ String print(CompilationUnitDeclaration owner, String message, Object ... params) {
        ArrayList list;
        if (GLOBAL_DSS_DISABLE_SWITCH) {
            return null;
        }
        Map<CompilationUnitDeclaration, List<DebugSnapshot>> map = this.map;
        synchronized (map) {
            this.snapshot(owner, message == null ? "Printing" : message, params);
            list = new ArrayList();
            list.addAll(this.map.get((Object)owner));
            if (list.isEmpty()) {
                return null;
            }
            this.map.get((Object)owner).clear();
        }
        Collections.sort(list);
        int idx = 1;
        StringBuilder out = new StringBuilder();
        out.append("---------------------------\n");
        for (DebugSnapshot snapshot2 : list) {
            Object[] arrobject = new Object[]{idx++, snapshot2.shortToString()};
            out.append(String.format("%3d: %s\n", arrobject));
        }
        out.append("******\n");
        idx = 1;
        for (DebugSnapshot snapshot2 : list) {
            Object[] arrobject = new Object[]{idx++, snapshot2.toString()};
            out.append(String.format("%3d: %s", arrobject));
        }
        try {
            File logFile = new File(System.getProperty("user.home", "."), String.format("lombok164-%d.err", System.currentTimeMillis()));
            FileOutputStream stream = new FileOutputStream(logFile);
            try {
                stream.write(out.toString().getBytes("UTF-8"));
            }
            finally {
                stream.close();
            }
            return logFile.getAbsolutePath();
        }
        catch (Exception e) {
            System.err.println(out);
            return "(can't write log file - emitted to system err)";
        }
    }
}

