/*
 * Decompiled with CFR 0_110.
 */
package org.apache.commons.lang3.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.Validate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EventListenerSupport<L>
implements Serializable {
    private static final long serialVersionUID = 3593265990380473632L;
    private List<L> listeners = new CopyOnWriteArrayList<L>();
    private transient L proxy;
    private transient L[] prototypeArray;

    public static <T> EventListenerSupport<T> create(Class<T> listenerInterface) {
        return new EventListenerSupport<T>(listenerInterface);
    }

    public EventListenerSupport(Class<L> listenerInterface) {
        this(listenerInterface, Thread.currentThread().getContextClassLoader());
    }

    public EventListenerSupport(Class<L> listenerInterface, ClassLoader classLoader) {
        this();
        Validate.notNull(listenerInterface, "Listener interface cannot be null.", new Object[0]);
        Validate.notNull(classLoader, "ClassLoader cannot be null.", new Object[0]);
        Validate.isTrue(listenerInterface.isInterface(), "Class {0} is not an interface", listenerInterface.getName());
        super.initializeTransientFields(listenerInterface, classLoader);
    }

    private EventListenerSupport() {
    }

    public L fire() {
        return this.proxy;
    }

    public void addListener(L listener) {
        Validate.notNull(listener, "Listener object cannot be null.", new Object[0]);
        this.listeners.add(listener);
    }

    int getListenerCount() {
        return this.listeners.size();
    }

    public void removeListener(L listener) {
        Validate.notNull(listener, "Listener object cannot be null.", new Object[0]);
        this.listeners.remove(listener);
    }

    public L[] getListeners() {
        return this.listeners.toArray(this.prototypeArray);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        ArrayList<L> serializableListeners = new ArrayList<L>();
        ObjectOutputStream testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
        for (L listener : this.listeners) {
            try {
                testObjectOutputStream.writeObject(listener);
                serializableListeners.add(listener);
            }
            catch (IOException exception) {
                testObjectOutputStream = new ObjectOutputStream(new ByteArrayOutputStream());
            }
        }
        objectOutputStream.writeObject(serializableListeners.toArray(this.prototypeArray));
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        Object[] listeners = (Object[])objectInputStream.readObject();
        this.listeners = new CopyOnWriteArrayList<Object>(listeners);
        Class listenerInterface = listeners.getClass().getComponentType();
        this.initializeTransientFields(listenerInterface, Thread.currentThread().getContextClassLoader());
    }

    private void initializeTransientFields(Class<L> listenerInterface, ClassLoader classLoader) {
        Object[] array;
        this.prototypeArray = array = (Object[])Array.newInstance(listenerInterface, 0);
        this.createProxy(listenerInterface, classLoader);
    }

    private void createProxy(Class<L> listenerInterface, ClassLoader classLoader) {
        this.proxy = listenerInterface.cast(Proxy.newProxyInstance(classLoader, new Class[]{listenerInterface}, this.createInvocationHandler()));
    }

    protected InvocationHandler createInvocationHandler() {
        return new ProxyInvocationHandler();
    }

    protected class ProxyInvocationHandler
    implements InvocationHandler {
        private static final long serialVersionUID = 1;

        protected ProxyInvocationHandler() {
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for (Object listener : EventListenerSupport.this.listeners) {
                method.invoke(listener, args);
            }
            return null;
        }
    }

}

