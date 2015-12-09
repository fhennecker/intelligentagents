/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SpiLoadUtil {
    private SpiLoadUtil() {
    }

    public static <T> List<T> readAllFromIterator(Iterable<T> findServices) {
        ArrayList<T> list = new ArrayList<T>();
        for (T t : findServices) {
            list.add(t);
        }
        return list;
    }

    public static <C> Iterable<C> findServices(Class<C> target) throws IOException {
        return SpiLoadUtil.findServices(target, Thread.currentThread().getContextClassLoader());
    }

    public static <C> Iterable<C> findServices(final Class<C> target, ClassLoader loader) throws IOException {
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        Enumeration<URL> resources = loader.getResources("META-INF/services/" + target.getName());
        LinkedHashSet<String> entries = new LinkedHashSet<String>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            SpiLoadUtil.readServicesFromUrl(entries, url);
        }
        final Iterator names = entries.iterator();
        final ClassLoader fLoader = loader;
        return new Iterable<C>(){

            @Override
            public Iterator<C> iterator() {
                return new Iterator<C>(){

                    @Override
                    public boolean hasNext() {
                        return names.hasNext();
                    }

                    @Override
                    public C next() {
                        try {
                            return (C)target.cast(Class.forName((String)names.next(), true, fLoader).newInstance());
                        }
                        catch (Exception e) {
                            if (e instanceof RuntimeException) {
                                throw (RuntimeException)e;
                            }
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }

        };
    }

    private static void readServicesFromUrl(Collection<String> list, URL url) throws IOException {
        InputStream in = url.openStream();
        try {
            String line;
            if (in == null) {
                return;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = r.readLine()) != null) {
                int idx = line.indexOf(35);
                if (idx != -1) {
                    line = line.substring(0, idx);
                }
                if ((line = line.trim()).length() == 0) continue;
                list.add(line);
            }
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Throwable ignore) {}
        }
    }

    public static Class<? extends Annotation> findAnnotationClass(Class<?> c, Class<?> base) {
        if (c == Object.class || c == null) {
            return null;
        }
        Class<? extends Annotation> answer = null;
        answer = SpiLoadUtil.findAnnotationHelper(base, c.getGenericSuperclass());
        if (answer != null) {
            return answer;
        }
        for (Type iface : c.getGenericInterfaces()) {
            answer = SpiLoadUtil.findAnnotationHelper(base, iface);
            if (answer == null) continue;
            return answer;
        }
        Class<? extends Annotation> potential = SpiLoadUtil.findAnnotationClass(c.getSuperclass(), base);
        if (potential != null) {
            return potential;
        }
        for (Class iface2 : c.getInterfaces()) {
            potential = SpiLoadUtil.findAnnotationClass(iface2, base);
            if (potential == null) continue;
            return potential;
        }
        return null;
    }

    private static Class<? extends Annotation> findAnnotationHelper(Class<?> base, Type iface) {
        if (iface instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType)iface;
            if (!base.equals(p.getRawType())) {
                return null;
            }
            Type target = p.getActualTypeArguments()[0];
            if (target instanceof Class && Annotation.class.isAssignableFrom((Class)target)) {
                return (Class)target;
            }
            throw new ClassCastException("Not an annotation type: " + target);
        }
        return null;
    }

}

