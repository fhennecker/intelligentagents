/*
 * Decompiled with CFR 0_110.
 */
package lombok.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import lombok.Lombok;
import lombok.core.LombokNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AST<A extends AST<A, L, N>, L extends LombokNode<A, L, N>, N> {
    private L top;
    private final String fileName;
    private final String packageDeclaration;
    private final Collection<String> imports;
    Map<N, Void> identityDetector = new IdentityHashMap<N, Void>();
    private Map<N, L> nodeMap = new IdentityHashMap<N, L>();
    private boolean changed = false;
    private static Map<Class<?>, Collection<FieldAccess>> fieldsOfASTClasses = new HashMap<Class<?>, Collection<FieldAccess>>();

    protected AST(String fileName, String packageDeclaration, Collection<String> imports) {
        this.fileName = fileName == null ? "(unknown).java" : fileName;
        this.packageDeclaration = packageDeclaration;
        this.imports = Collections.unmodifiableCollection(new ArrayList<String>(imports));
    }

    public void setChanged() {
        this.changed = true;
    }

    protected void clearChanged() {
        this.changed = false;
    }

    public boolean isChanged() {
        return this.changed;
    }

    protected void setTop(L top) {
        this.top = top;
    }

    public final String getPackageDeclaration() {
        return this.packageDeclaration;
    }

    public final Collection<String> getImportStatements() {
        return this.imports;
    }

    protected L putInMap(L node) {
        this.nodeMap.put(node.get(), node);
        this.identityDetector.put(node.get(), null);
        return node;
    }

    protected Map<N, L> getNodeMap() {
        return this.nodeMap;
    }

    protected void clearState() {
        this.identityDetector = new IdentityHashMap<N, Void>();
        this.nodeMap = new IdentityHashMap<N, L>();
    }

    protected boolean setAndGetAsHandled(N node) {
        if (this.identityDetector.containsKey(node)) {
            return true;
        }
        this.identityDetector.put(node, null);
        return false;
    }

    public String getFileName() {
        return this.fileName;
    }

    public L top() {
        return this.top;
    }

    public L get(N node) {
        return (L)((LombokNode)this.nodeMap.get(node));
    }

    L replaceNewWithExistingOld(Map<N, L> oldNodes, L newNode) {
        LombokNode oldNode = (LombokNode)oldNodes.get(newNode.get());
        L targetNode = oldNode == null ? newNode : oldNode;
        ArrayList<LombokNode> children = new ArrayList<LombokNode>();
        for (LombokNode child : newNode.children) {
            LombokNode oldChild = this.replaceNewWithExistingOld(oldNodes, child);
            children.add(oldChild);
            oldChild.parent = targetNode;
        }
        targetNode.children.clear();
        targetNode.children.addAll(children);
        return targetNode;
    }

    protected abstract L buildTree(N var1, Kind var2);

    protected Collection<FieldAccess> fieldsOf(Class<?> c) {
        Collection<FieldAccess> fields = fieldsOfASTClasses.get(c);
        if (fields != null) {
            return fields;
        }
        fields = new ArrayList<FieldAccess>();
        this.getFields(c, fields);
        fieldsOfASTClasses.put(c, fields);
        return fields;
    }

    private void getFields(Class<?> c, Collection<FieldAccess> fields) {
        if (c == Object.class || c == null) {
            return;
        }
        block0 : for (Field f : c.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            Class<?> t = f.getType();
            int dim = 0;
            if (t.isArray()) {
                while (t.isArray()) {
                    ++dim;
                    t = t.getComponentType();
                }
            } else {
                while (Collection.class.isAssignableFrom(t)) {
                    ++dim;
                    t = this.getComponentType(f.getGenericType());
                }
            }
            for (Class<N> statementType : this.getStatementTypes()) {
                if (!statementType.isAssignableFrom(t)) continue;
                f.setAccessible(true);
                fields.add(new FieldAccess(f, dim));
                continue block0;
            }
        }
        this.getFields(c.getSuperclass(), fields);
    }

    private Class<?> getComponentType(Type type) {
        if (type instanceof ParameterizedType) {
            Type component = ((ParameterizedType)type).getActualTypeArguments()[0];
            return component instanceof Class ? (Class)component : Object.class;
        }
        return Object.class;
    }

    protected abstract Collection<Class<? extends N>> getStatementTypes();

    protected Collection<L> buildWithField(Class<L> nodeType, N statement, FieldAccess fa) {
        ArrayList<E> list = new ArrayList<E>();
        this.buildWithField0(nodeType, statement, fa, list);
        return list;
    }

    protected boolean replaceStatementInNode(N statement, N oldN, N newN) {
        for (FieldAccess fa : this.fieldsOf(statement.getClass())) {
            if (!this.replaceStatementInField(fa, statement, oldN, newN)) continue;
            return true;
        }
        return false;
    }

    private boolean replaceStatementInField(FieldAccess fa, N statement, N oldN, N newN) {
        try {
            Object o = fa.field.get(statement);
            if (o == null) {
                return false;
            }
            if (o == oldN) {
                fa.field.set(statement, newN);
                return true;
            }
            if (fa.dim > 0) {
                if (o.getClass().isArray()) {
                    return this.replaceStatementInArray(o, oldN, newN);
                }
                if (Collection.class.isInstance(o)) {
                    return this.replaceStatementInCollection(fa.field, statement, new ArrayList<Collection<?>>(), (Collection)o, oldN, newN);
                }
            }
            return false;
        }
        catch (IllegalAccessException e) {
            throw Lombok.sneakyThrow(e);
        }
    }

    private boolean replaceStatementInCollection(Field field, Object fieldRef, List<Collection<?>> chain, Collection<?> collection, N oldN, N newN) throws IllegalAccessException {
        if (collection == null) {
            return false;
        }
        int idx = -1;
        for (? o : collection) {
            ++idx;
            if (o == null) continue;
            if (Collection.class.isInstance(o)) {
                Collection newC = (Collection)o;
                ArrayList<Collection<?>> newChain = new ArrayList<Collection<?>>(chain);
                newChain.add(newC);
                if (this.replaceStatementInCollection(field, fieldRef, newChain, newC, oldN, newN)) {
                    return true;
                }
            }
            if (o != oldN) continue;
            this.setElementInASTCollection(field, fieldRef, chain, collection, idx, newN);
            return true;
        }
        return false;
    }

    protected void setElementInASTCollection(Field field, Object fieldRef, List<Collection<?>> chain, Collection<?> collection, int idx, N newN) throws IllegalAccessException {
        if (collection instanceof List) {
            ((List)collection).set(idx, newN);
        }
    }

    private boolean replaceStatementInArray(Object array, N oldN, N newN) {
        if (array == null) {
            return false;
        }
        int len = Array.getLength(array);
        for (int i = 0; i < len; ++i) {
            Object o = Array.get(array, i);
            if (o == null) continue;
            if (o.getClass().isArray()) {
                if (!this.replaceStatementInArray(o, oldN, newN)) continue;
                return true;
            }
            if (o != oldN) continue;
            Array.set(array, i, newN);
            return true;
        }
        return false;
    }

    private void buildWithField0(Class<L> nodeType, N child, FieldAccess fa, Collection<L> list) {
        try {
            Object o = fa.field.get(child);
            if (o == null) {
                return;
            }
            if (fa.dim == 0) {
                L node = this.buildTree(o, Kind.STATEMENT);
                if (node != null) {
                    list.add(nodeType.cast(node));
                }
            } else if (o.getClass().isArray()) {
                this.buildWithArray(nodeType, o, list, fa.dim);
            } else if (Collection.class.isInstance(o)) {
                this.buildWithCollection(nodeType, o, list, fa.dim);
            }
        }
        catch (IllegalAccessException e) {
            Lombok.sneakyThrow(e);
        }
    }

    private void buildWithArray(Class<L> nodeType, Object array, Collection<L> list, int dim) {
        if (dim == 1) {
            for (Object v : (Object[])array) {
                L node;
                if (v == null || (node = this.buildTree(v, Kind.STATEMENT)) == null) continue;
                list.add(nodeType.cast(node));
            }
        } else {
            for (Object v : (Object[])array) {
                if (v == null) {
                    return;
                }
                this.buildWithArray(nodeType, v, list, dim - 1);
            }
        }
    }

    private void buildWithCollection(Class<L> nodeType, Object collection, Collection<L> list, int dim) {
        if (dim == 1) {
            for (E v : (Collection)collection) {
                L node;
                if (v == null || (node = this.buildTree(v, Kind.STATEMENT)) == null) continue;
                list.add(nodeType.cast(node));
            }
        } else {
            for (E v : (Collection)collection) {
                this.buildWithCollection(nodeType, v, list, dim - 1);
            }
        }
    }

    protected static class FieldAccess {
        public final Field field;
        public final int dim;

        FieldAccess(Field field, int dim) {
            this.field = field;
            this.dim = dim;
        }
    }

    public static enum Kind {
        COMPILATION_UNIT,
        TYPE,
        FIELD,
        INITIALIZER,
        METHOD,
        ANNOTATION,
        ARGUMENT,
        LOCAL,
        STATEMENT;
        

        private Kind() {
        }
    }

}

