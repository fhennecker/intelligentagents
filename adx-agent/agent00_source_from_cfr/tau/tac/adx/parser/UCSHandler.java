/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.parser;

import java.io.IOException;
import java.text.ParseException;
import se.sics.isl.util.IllegalConfigurationException;
import se.sics.tasim.logtool.LogHandler;
import se.sics.tasim.logtool.LogReader;
import tau.tac.adx.parser.UCSParser;

public class UCSHandler
extends LogHandler {
    @Override
    protected void start(LogReader reader) throws IllegalConfigurationException, IOException, ParseException {
        UCSParser parser = new UCSParser(reader);
        parser.start();
        parser.stop();
    }
}

