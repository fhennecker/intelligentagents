/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.ads.properties.generators;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import tau.tac.adx.ads.properties.AdType;
import tau.tac.adx.ads.properties.generators.AdTypeGenerator;
import tau.tac.adx.util.EnumGenerator;

public class SimpleAdTypeGenerator
implements AdTypeGenerator {
    private final EnumGenerator<AdType> generator;

    public SimpleAdTypeGenerator() {
        HashMap<AdType, Integer> weights = new HashMap<AdType, Integer>();
        weights.put(AdType.text, 1);
        weights.put(AdType.video, 1);
        this.generator = new EnumGenerator(weights);
    }

    @Override
    public Collection<AdType> generate(int amount) {
        LinkedList<AdType> adTypes = new LinkedList<AdType>();
        int i = 0;
        while (i < amount) {
            adTypes.add(this.generator.randomType());
            ++i;
        }
        return adTypes;
    }
}

