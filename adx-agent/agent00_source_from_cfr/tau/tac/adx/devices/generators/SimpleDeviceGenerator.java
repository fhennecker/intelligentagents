/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.devices.generators;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import tau.tac.adx.devices.Device;
import tau.tac.adx.devices.generators.DeviceGenerator;
import tau.tac.adx.util.EnumGenerator;

public class SimpleDeviceGenerator
implements DeviceGenerator {
    private final EnumGenerator<Device> generator;

    public SimpleDeviceGenerator() {
        HashMap<Device, Integer> weights = new HashMap<Device, Integer>();
        weights.put(Device.mobile, 1);
        weights.put(Device.pc, 1);
        this.generator = new EnumGenerator(weights);
    }

    @Override
    public Collection<Device> generate(int amount) {
        LinkedList<Device> adTypes = new LinkedList<Device>();
        int i = 0;
        while (i < amount) {
            adTypes.add(this.generator.randomType());
            ++i;
        }
        return adTypes;
    }
}

