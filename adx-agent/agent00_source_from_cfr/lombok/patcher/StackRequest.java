/*
 * Decompiled with CFR 0_110.
 */
package lombok.patcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum StackRequest {
    RETURN_VALUE(-1),
    THIS(-1),
    PARAM1(0),
    PARAM2(1),
    PARAM3(2),
    PARAM4(3),
    PARAM5(4),
    PARAM6(5);
    
    private final int paramPos;
    public static final List<StackRequest> PARAMS_IN_ORDER;

    private StackRequest(int paramPos) {
        this.paramPos = paramPos;
    }

    public int getParamPos() {
        return this.paramPos;
    }

    static {
        PARAMS_IN_ORDER = Collections.unmodifiableList(Arrays.asList(new StackRequest[]{PARAM1, PARAM2, PARAM3, PARAM4, PARAM5, PARAM6}));
    }
}

