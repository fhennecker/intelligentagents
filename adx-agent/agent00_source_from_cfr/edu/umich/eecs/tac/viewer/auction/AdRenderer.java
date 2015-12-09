/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer.auction;

import edu.umich.eecs.tac.props.Ad;
import edu.umich.eecs.tac.props.Product;
import edu.umich.eecs.tac.props.Query;
import edu.umich.eecs.tac.props.QueryType;
import edu.umich.eecs.tac.viewer.GraphicUtils;
import edu.umich.eecs.tac.viewer.auction.ResultsItem;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

public class AdRenderer
extends DefaultListCellRenderer {
    private String adCopy;
    private final Map<String, String> textCache = new HashMap<String, String>();
    private static /* synthetic */ int[] $SWITCH_TABLE$edu$umich$eecs$tac$props$QueryType;

    public AdRenderer(Query query) {
        switch (AdRenderer.$SWITCH_TABLE$edu$umich$eecs$tac$props$QueryType()[query.getType().ordinal()]) {
            case 1: {
                this.adCopy = "products";
                break;
            }
            case 2: {
                Object[] arrobject = new Object[1];
                arrobject[0] = query.getManufacturer() == null ? query.getComponent() : query.getManufacturer();
                this.adCopy = String.format("%s products", arrobject);
                break;
            }
            case 3: {
                this.adCopy = String.format("%s %s units", query.getManufacturer(), query.getComponent());
            }
        }
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ResultsItem item = (ResultsItem)value;
        ImageIcon icon = GraphicUtils.iconForProduct(item.getAd().getProduct());
        if (icon == null) {
            icon = item.getAd().isGeneric() ? GraphicUtils.genericIcon() : GraphicUtils.invalidIcon();
        }
        label.setIcon(icon);
        String text = this.textCache.get(item.getAdvertiser());
        if (text == null) {
            text = String.format("%s's %s", item.getAdvertiser(), this.adCopy);
            this.textCache.put(item.getAdvertiser(), text);
        }
        label.setText(text);
        return label;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$edu$umich$eecs$tac$props$QueryType() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$edu$umich$eecs$tac$props$QueryType;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[QueryType.values().length];
        try {
            arrn[QueryType.FOCUS_LEVEL_ONE.ordinal()] = 2;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[QueryType.FOCUS_LEVEL_TWO.ordinal()] = 3;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[QueryType.FOCUS_LEVEL_ZERO.ordinal()] = 1;
        }
        catch (NoSuchFieldError v3) {}
        $SWITCH_TABLE$edu$umich$eecs$tac$props$QueryType = arrn;
        return $SWITCH_TABLE$edu$umich$eecs$tac$props$QueryType;
    }
}

