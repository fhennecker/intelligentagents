/*
 * Decompiled with CFR 0_110.
 */
package tau.tac.adx.parser;

import edu.umich.eecs.tac.Parser;
import java.io.PrintStream;
import java.util.List;
import se.sics.isl.transport.Transportable;
import se.sics.tasim.logtool.LogReader;
import tau.tac.adx.props.PublisherCatalog;
import tau.tac.adx.props.PublisherCatalogEntry;

public class PublisherCatalogParser
extends Parser {
    boolean first = true;

    public PublisherCatalogParser(LogReader reader) {
        super(reader);
        System.out.println("Participating publishers: \n");
    }

    @Override
    protected void dataUpdated(int type, Transportable content) {
    }

    @Override
    protected void message(int sender, int receiver, Transportable content) {
        if (this.first && content instanceof PublisherCatalog) {
            PublisherCatalog publisherCatalog = (PublisherCatalog)content;
            List<PublisherCatalogEntry> publishers = publisherCatalog.getPublishers();
            for (PublisherCatalogEntry publisherCatalogEntry : publishers) {
                System.out.println(publisherCatalogEntry.getPublisherName());
            }
            this.first = false;
        }
    }
}

