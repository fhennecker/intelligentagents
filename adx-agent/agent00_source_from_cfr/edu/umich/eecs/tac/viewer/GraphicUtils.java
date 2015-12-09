/*
 * Decompiled with CFR 0_110.
 */
package edu.umich.eecs.tac.viewer;

import edu.umich.eecs.tac.props.Product;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

public class GraphicUtils {
    private static final ImageIcon GENERIC;
    private static final ImageIcon INVALID;
    private static final Map<Product, ImageIcon> PRODUCT_ICONS;
    private static final Map<String, ImageIcon> MANUFACTURER_ICONS;
    private static final Map<String, ImageIcon> COMPONENT_ICONS;

    static {
        String name;
        GENERIC = new ImageIcon(GraphicUtils.class.getResource("/generic_regular.gif"));
        INVALID = new ImageIcon(GraphicUtils.class.getResource("/invalid_regular.gif"));
        PRODUCT_ICONS = new HashMap<Product, ImageIcon>();
        String[] arrstring = new String[]{"lioneer", "pg", "flat"};
        int n = arrstring.length;
        int n2 = 0;
        while (n2 < n) {
            String manufacturer = arrstring[n2];
            String[] arrstring2 = new String[]{"tv", "dvd", "audio"};
            int n3 = arrstring2.length;
            int n4 = 0;
            while (n4 < n3) {
                String component = arrstring2[n4];
                PRODUCT_ICONS.put(new Product(manufacturer, component), new ImageIcon(GraphicUtils.class.getResource(String.format("/%s_%s_regular.gif", manufacturer, component))));
                ++n4;
            }
            ++n2;
        }
        MANUFACTURER_ICONS = new HashMap<String, ImageIcon>();
        arrstring = new String[]{"lioneer", "pg", "flat"};
        n = arrstring.length;
        n2 = 0;
        while (n2 < n) {
            name = arrstring[n2];
            MANUFACTURER_ICONS.put(name, new ImageIcon(GraphicUtils.class.getResource(String.format("/%s_thumb.gif", name))));
            ++n2;
        }
        COMPONENT_ICONS = new HashMap<String, ImageIcon>();
        arrstring = new String[]{"tv", "dvd", "audio"};
        n = arrstring.length;
        n2 = 0;
        while (n2 < n) {
            name = arrstring[n2];
            COMPONENT_ICONS.put(name, new ImageIcon(GraphicUtils.class.getResource(String.format("/%s_thumb.gif", name))));
            ++n2;
        }
    }

    private GraphicUtils() {
    }

    public static ImageIcon genericIcon() {
        return GENERIC;
    }

    public static ImageIcon invalidIcon() {
        return INVALID;
    }

    public static ImageIcon iconForProduct(Product product) {
        return PRODUCT_ICONS.get(product);
    }

    public static ImageIcon iconForManufacturer(String manufacturer) {
        return MANUFACTURER_ICONS.get(manufacturer);
    }

    public static ImageIcon iconForComponent(String component) {
        return COMPONENT_ICONS.get(component);
    }
}

