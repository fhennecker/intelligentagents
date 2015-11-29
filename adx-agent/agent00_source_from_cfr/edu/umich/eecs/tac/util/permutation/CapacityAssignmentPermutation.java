/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.util.permutation;

import edu.umich.eecs.tac.sim.CapacityType;
import edu.umich.eecs.tac.util.permutation.PermutationOfEightGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class CapacityAssignmentPermutation {
    private static CapacityType[][] GROUP_CAPACITIES = new CapacityType[][]{{CapacityType.LOW, CapacityType.LOW, CapacityType.MED, CapacityType.MED, CapacityType.MED, CapacityType.MED, CapacityType.HIGH, CapacityType.HIGH}, {CapacityType.MED, CapacityType.MED, CapacityType.LOW, CapacityType.LOW, CapacityType.HIGH, CapacityType.HIGH, CapacityType.MED, CapacityType.MED}, {CapacityType.MED, CapacityType.MED, CapacityType.HIGH, CapacityType.HIGH, CapacityType.LOW, CapacityType.LOW, CapacityType.MED, CapacityType.MED}, {CapacityType.HIGH, CapacityType.HIGH, CapacityType.MED, CapacityType.MED, CapacityType.MED, CapacityType.MED, CapacityType.LOW, CapacityType.LOW}};

    private CapacityAssignmentPermutation() {
    }

    public static CapacityType[] permutation(int group, int groupOffset) {
        CapacityType[] p = new CapacityType[8];
        int[] eightPerm = CapacityAssignmentPermutation.permutationOfEight(group);
        int i = 0;
        while (i < 8) {
            p[i] = GROUP_CAPACITIES[groupOffset][eightPerm[i]];
            ++i;
        }
        return p;
    }

    private static int[] permutationOfEight(int group) {
        if ((group %= 40320) < 0) {
            group += 40320;
        }
        PermutationOfEightGenerator generator = new PermutationOfEightGenerator();
        int i = 0;
        while (i < group) {
            generator.next();
            ++i;
        }
        return generator.next();
    }

    public static CapacityType[] secretPermutation(int secret, int simulationId, int baseId) {
        int group = CapacityAssignmentPermutation.digest(secret, (simulationId - baseId) / 4);
        int groupOffset = (simulationId - baseId) % 4;
        if (groupOffset < 0) {
            groupOffset += 4;
        }
        return CapacityAssignmentPermutation.permutation(group, groupOffset);
    }

    private static int digest(int secret, int value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(Integer.valueOf(secret).byteValue());
            md.update(Integer.valueOf(value).byteValue());
            return CapacityAssignmentPermutation.bytesToInt(md.digest());
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static int bytesToInt(byte[] bytes) {
        int d = 0;
        int i = 0;
        while (i < Math.max(bytes.length, 4)) {
            d += (bytes[i] & 255) << 24 - i * 8;
            ++i;
        }
        return d;
    }
}

