/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.parser;

import edu.umich.eecs.tac.Parser;
import edu.umich.eecs.tac.props.BankStatus;
import edu.umich.eecs.tac.props.RetailCatalog;
import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import se.sics.tasim.logtool.ParticipantInfo;
import se.sics.tasim.props.SimulationStatus;
import se.sics.tasim.props.StartInfo;

public class BankStatusParser
extends Parser {
    private int day = 0;
    private final String[] participantNames;
    private final boolean[] is_Advertiser;
    private final ParticipantInfo[] participants;

    public BankStatusParser(LogReader reader) {
        super(reader);
        System.out.println("****AGENT INDEXES****");
        this.participants = reader.getParticipants();
        if (this.participants == null) {
            throw new IllegalStateException("no participants");
        }
        this.participantNames = new String[this.participants.length];
        this.is_Advertiser = new boolean[this.participants.length];
        int i = 0;
        int n = this.participants.length;
        while (i < n) {
            ParticipantInfo info = this.participants[i];
            int agent = info.getIndex();
            System.out.println(String.valueOf(info.getName()) + ": " + agent);
            this.participantNames[agent] = info.getName();
            this.is_Advertiser[agent] = info.getRole() == 1;
            ++i;
        }
        System.out.println("****BANK STATUS DATA****");
        System.out.println(String.valueOf(StringUtils.rightPad("Agent", 20)) + "\tDay\tBank Status");
    }

    @Override
    protected void message(int sender, int receiver, Transportable content) {
        if (content instanceof BankStatus && this.participants[receiver].getRole() == 5) {
            BankStatus status = (BankStatus)content;
            System.out.println(String.valueOf(StringUtils.rightPad(this.participantNames[receiver], 20)) + "\t" + this.day + "\t" + (int)status.getAccountBalance());
        } else if (content instanceof SimulationStatus) {
            SimulationStatus ss = (SimulationStatus)content;
            this.day = ss.getCurrentDate();
        }
    }

    @Override
    protected void dataUpdated(int type, Transportable content) {
        if (content instanceof StartInfo) {
            StartInfo startInfo = (StartInfo)content;
        } else if (content instanceof RetailCatalog) {
            RetailCatalog retailCatalog = (RetailCatalog)content;
        }
    }
}

