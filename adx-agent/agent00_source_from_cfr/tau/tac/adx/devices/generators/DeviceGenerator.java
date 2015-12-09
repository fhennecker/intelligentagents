/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.devices.generators;

import java.util.Collection;
import tau.tac.adx.devices.Device;
import tau.tac.adx.generators.GenericGenerator;

public interface DeviceGenerator
extends GenericGenerator<Device> {
    @Override
    public Collection<Device> generate(int var1);
}

