/*
 * Decompiled with CFR 0_110.
 */
package adx.query;

import adx.query.IQueryAgg;
import adx.query.IQueryClosure;
import adx.query.IQueryClosureAgg;
import adx.query.IQueryGroup;
import adx.query.IQueryGrouper;
import adx.query.IQueryGrouperStar;
import adx.query.IQueryLValue;
import adx.query.IQueryOp;
import adx.query.IQueryRValue;
import adx.query.IQuerySelect;
import adx.query.IQuerySelectAgg;
import adx.query.IQuerySelectAggStar;
import adx.query.IQuerySelectStar;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Query<T> {
    protected Class<T> clazz;
    protected Iterable<T> elements;
    protected List<IExpr> groupers = new LinkedList<IExpr>();
    protected List<IAggregator> aggregators = new LinkedList<IAggregator>();
    protected List<IExpr> getters = new LinkedList<IExpr>();
    protected IExpr expr;
    protected IExpr exprHelper;
    protected IQuerySelect select;
    protected IQuerySelectStar selectStar;
    protected IQuerySelectAgg selectAgg;
    protected IQuerySelectAggStar selectAggStar;
    protected IQueryLValue lValue;
    protected IQueryOp op;
    protected IQueryRValue rValue;
    protected Object closure;
    protected IQueryGrouper grouper;
    protected IQueryGrouperStar grouperStar;

    public Query() {
        this.select = new QuerySelect(this);
        this.selectStar = new QuerySelectStar(this);
        this.selectAgg = new QuerySelectAgg(this);
        this.selectAggStar = new QuerySelectAggStar(this);
        this.lValue = new QueryLValue(this);
        this.op = new QueryOp(this);
        this.rValue = new QueryRValue(this);
        this.grouper = new QueryGrouper(this);
        this.grouperStar = new QueryGrouperStar(this);
    }

    public static <T> IQuerySelect select(Iterable<T> elements) {
        Query<T> query = new Query<T>();
        query.elements = elements;
        if (elements.iterator().hasNext()) {
            query.clazz = elements.iterator().next().getClass();
        }
        return query.select;
    }

    public static <T> void println(Iterable<T> elements) {
        if (elements.iterator().hasNext()) {
            Class clazz = elements.iterator().next().getClass();
            for (T element : elements) {
                if (element instanceof Object[]) {
                    System.out.print(Arrays.deepToString((Object[])element));
                } else {
                    Field[] arrfield = clazz.getDeclaredFields();
                    int n = arrfield.length;
                    int n2 = 0;
                    while (n2 < n) {
                        block9 : {
                            Field f = arrfield[n2];
                            try {
                                String p = new PropertyDescriptor(f.getName(), clazz).getReadMethod().invoke(element, new Object[0]).toString();
                                System.out.print(String.valueOf(p) + " ");
                            }
                            catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                if (f.isAccessible() || !f.getName().equals("value")) break block9;
                                f.setAccessible(true);
                                try {
                                    System.out.print(f.get(element));
                                }
                                catch (IllegalAccessException | IllegalArgumentException var9_10) {
                                    // empty catch block
                                }
                            }
                        }
                        ++n2;
                    }
                }
                System.out.println();
            }
        }
    }

    public static <T> int count(Iterable<T> elements) {
        if (elements instanceof Collection) {
            return ((Collection)elements).size();
        }
        int size = 0;
        for (T element : elements) {
            ++size;
        }
        return size;
    }

    protected static abstract class Aggregator {
        protected int index;

        public int getIndex() {
            return this.index;
        }

        public Aggregator(int index) {
            this.index = index;
        }
    }

    protected static class AggregatorCount
    extends Aggregator
    implements IAggregator {
        public AggregatorCount(int arg0) {
            super(arg0);
        }

        @Override
        public Object initialize(Object object) {
            return 1;
        }

        @Override
        public Object evaluate(Object lhs, Object rhs) {
            return (Integer)lhs + 1;
        }
    }

    protected static class AggregatorFirst
    extends Aggregator
    implements IAggregator {
        public AggregatorFirst(int arg0) {
            super(arg0);
        }

        @Override
        public Object initialize(Object object) {
            return object;
        }

        @Override
        public Object evaluate(Object lhs, Object rhs) {
            return lhs;
        }
    }

    protected static class AggregatorLast
    extends Aggregator
    implements IAggregator {
        public AggregatorLast(int arg0) {
            super(arg0);
        }

        @Override
        public Object initialize(Object object) {
            return object;
        }

        @Override
        public Object evaluate(Object lhs, Object rhs) {
            return rhs;
        }
    }

    protected static class AggregatorMax
    extends Aggregator
    implements IAggregator {
        public AggregatorMax(int arg0) {
            super(arg0);
        }

        @Override
        public Object initialize(Object object) {
            return object;
        }

        @Override
        public Object evaluate(Object lhs, Object rhs) {
            return ((Comparable)lhs).compareTo(rhs) >= 0 ? lhs : rhs;
        }
    }

    protected static class AggregatorMin
    extends Aggregator
    implements IAggregator {
        public AggregatorMin(int arg0) {
            super(arg0);
        }

        @Override
        public Object initialize(Object object) {
            return object;
        }

        @Override
        public Object evaluate(Object lhs, Object rhs) {
            return ((Comparable)lhs).compareTo(rhs) <= 0 ? lhs : rhs;
        }
    }

    protected static class AggregatorSum
    extends Aggregator
    implements IAggregator {
        public AggregatorSum(int arg0) {
            super(arg0);
        }

        @Override
        public Object initialize(Object object) {
            return object;
        }

        @Override
        public Object evaluate(Object lhs, Object rhs) {
            if (lhs instanceof Integer) {
                return (Integer)lhs + (Integer)rhs;
            }
            if (lhs instanceof Long) {
                return (Long)lhs + (Long)rhs;
            }
            if (lhs instanceof Float) {
                return Float.valueOf(((Float)lhs).floatValue() + ((Float)rhs).floatValue());
            }
            if (lhs instanceof Double) {
                return (Double)lhs + (Double)rhs;
            }
            throw new RuntimeException();
        }
    }

    protected static class ExprAnd
    extends ExprBinOp {
        public ExprAnd(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            if (((Boolean)this.left.evaluate(object)).booleanValue() && ((Boolean)this.right.evaluate(object)).booleanValue()) {
                return true;
            }
            return false;
        }
    }

    protected static abstract class ExprBinOp
    implements IExpr {
        protected IExpr left;
        protected IExpr right;

        public IExpr getLeft() {
            return this.left;
        }

        public IExpr getRight() {
            return this.right;
        }

        public void setLeft(IExpr left) {
            this.left = left;
        }

        public void setRight(IExpr right) {
            this.right = right;
        }

        public ExprBinOp(IExpr left, IExpr right) {
            this.left = left;
            this.right = right;
        }
    }

    protected static class ExprContains
    extends ExprBinOp {
        public ExprContains(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            Set lhs = (Set)this.left.evaluate(object);
            Object rhs = this.right.evaluate(object);
            return rhs instanceof Collection ? lhs.containsAll((Collection)rhs) : lhs.contains(rhs);
        }
    }

    protected static class ExprElement
    implements IExpr {
        protected ExprElement() {
        }

        @Override
        public Object evaluate(Object object) {
            return object;
        }
    }

    protected static class ExprEq
    extends ExprBinOp {
        public ExprEq(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            return this.left.evaluate(object).equals(this.right.evaluate(object));
        }
    }

    protected static class ExprGt
    extends ExprBinOp {
        public ExprGt(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            if (((Comparable)this.left.evaluate(object)).compareTo(this.right.evaluate(object)) > 0) {
                return true;
            }
            return false;
        }
    }

    protected static class ExprGte
    extends ExprBinOp {
        public ExprGte(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            if (((Comparable)this.left.evaluate(object)).compareTo(this.right.evaluate(object)) >= 0) {
                return true;
            }
            return false;
        }
    }

    protected static class ExprIn
    extends ExprBinOp {
        public ExprIn(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            return ((Set)this.right.evaluate(object)).contains(this.left.evaluate(object));
        }
    }

    protected static class ExprIndex
    implements IExpr {
        protected int index;

        @Override
        public Object evaluate(Object object) {
            return ((Object[])object)[this.index];
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public ExprIndex(int index) {
            this.index = index;
        }
    }

    protected static class ExprLt
    extends ExprBinOp {
        public ExprLt(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            if (((Comparable)this.left.evaluate(object)).compareTo(this.right.evaluate(object)) < 0) {
                return true;
            }
            return false;
        }
    }

    protected static class ExprLte
    extends ExprBinOp {
        public ExprLte(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            if (((Comparable)this.left.evaluate(object)).compareTo(this.right.evaluate(object)) <= 0) {
                return true;
            }
            return false;
        }
    }

    protected static class ExprMethod
    implements IExpr {
        protected Method method;

        @Override
        public Object evaluate(Object object) {
            try {
                return this.method.invoke(object, new Object[0]);
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public Method getMethod() {
            return this.method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public ExprMethod(Method method) {
            this.method = method;
        }
    }

    protected static class ExprNeq
    extends ExprBinOp {
        public ExprNeq(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            return !this.left.evaluate(object).equals(this.right.evaluate(object));
        }
    }

    protected static class ExprOr
    extends ExprBinOp {
        public ExprOr(IExpr arg0, IExpr arg1) {
            super(arg0, arg1);
        }

        @Override
        public Object evaluate(Object object) {
            if (!((Boolean)this.left.evaluate(object)).booleanValue() && !((Boolean)this.right.evaluate(object)).booleanValue()) {
                return false;
            }
            return true;
        }
    }

    protected static class ExprValue
    implements IExpr {
        protected Object value;

        @Override
        public Object evaluate(Object object) {
            return this.value;
        }

        public Object getValue() {
            return this.value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public ExprValue(Object value) {
            this.value = value;
        }
    }

    protected static interface IAggregator {
        public int getIndex();

        public Object initialize(Object var1);

        public Object evaluate(Object var1, Object var2);
    }

    protected static interface IExpr {
        public Object evaluate(Object var1);
    }

    protected static class QueryAgg<T>
    implements IQueryAgg,
    IQueryGroup {
        protected Query<T> container;

        @Override
        public IQuerySelectAgg first() {
            this.container.aggregators.add(new AggregatorFirst(this.container.getters.size() - 1));
            return this.container.selectAgg;
        }

        @Override
        public IQuerySelectAgg last() {
            this.container.aggregators.add(new AggregatorLast(this.container.getters.size() - 1));
            return this.container.selectAgg;
        }

        @Override
        public IQuerySelectAgg count() {
            this.container.aggregators.add(new AggregatorCount(this.container.getters.size() - 1));
            return this.container.selectAgg;
        }

        @Override
        public IQuerySelectAgg sum() {
            this.container.aggregators.add(new AggregatorSum(this.container.getters.size() - 1));
            return this.container.selectAgg;
        }

        @Override
        public IQuerySelectAgg min() {
            this.container.aggregators.add(new AggregatorMin(this.container.getters.size() - 1));
            return this.container.selectAgg;
        }

        @Override
        public IQuerySelectAgg max() {
            this.container.aggregators.add(new AggregatorMax(this.container.getters.size() - 1));
            return this.container.selectAgg;
        }

        @Override
        public IQueryGrouper groupBy() {
            return this.container.grouper;
        }

        public QueryAgg(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QueryClosure<T>
    implements IQueryClosure {
        protected Query<T> container;

        @Override
        public Iterable<Object> exec() {
            return this.container.selectStar.exec();
        }

        public IQueryLValue and() {
            this.container.expr = new ExprAnd(this.container.expr, null);
            return this.container.lValue;
        }

        public IQueryLValue or() {
            this.container.expr = new ExprOr(this.container.expr, null);
            return this.container.lValue;
        }

        public QueryClosure(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QueryClosureAgg<T>
    extends QueryClosure<T>
    implements IQueryClosureAgg {
        public QueryClosureAgg(Query<T> arg0) {
            super(arg0);
        }

        @Override
        public IQueryGrouper groupBy() {
            return this.container.grouper;
        }

        @Override
        public Iterable<Object> exec() {
            return this.container.selectAgg.exec();
        }
    }

    protected static class QueryGrouper<T>
    implements IQueryGrouper {
        protected Query<T> container;

        @Override
        public IQueryGrouperStar property(String name) {
            try {
                this.container.groupers.add(new ExprMethod(new PropertyDescriptor(name, this.container.clazz).getReadMethod()));
            }
            catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            return this.container.grouperStar;
        }

        @Override
        public IQueryGrouperStar index(int i) {
            this.container.groupers.add(new ExprIndex(i));
            return this.container.grouperStar;
        }

        public QueryGrouper(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QueryGrouperStar<T>
    extends QueryGrouper<T>
    implements IQueryGrouperStar {
        public QueryGrouperStar(Query<T> arg0) {
            super(arg0);
        }

        @Override
        public Iterable<Object> exec() {
            return this.container.selectAgg.exec();
        }
    }

    protected static class QueryLValue<T>
    implements IQueryLValue {
        protected Query<T> container;

        public IQueryOp property(String name) {
            try {
                this.container.exprHelper = new ExprMethod(new PropertyDescriptor(name, this.container.clazz).getReadMethod());
            }
            catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            return this.container.op;
        }

        public IQueryOp index(int i) {
            this.container.exprHelper = new ExprIndex(i);
            return this.container.op;
        }

        public IQueryOp element() {
            this.container.exprHelper = new ExprElement();
            return this.container.op;
        }

        public QueryLValue(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QueryOp<T>
    implements IQueryOp {
        protected Query<T> container;

        public IQueryRValue eq() {
            this.container.exprHelper = new ExprEq(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public IQueryRValue neq() {
            this.container.exprHelper = new ExprNeq(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public IQueryRValue lt() {
            this.container.exprHelper = new ExprLt(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public IQueryRValue lte() {
            this.container.exprHelper = new ExprLte(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public IQueryRValue gt() {
            this.container.exprHelper = new ExprGt(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public IQueryRValue gte() {
            this.container.exprHelper = new ExprGte(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public IQueryRValue in() {
            this.container.exprHelper = new ExprIn(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public IQueryRValue contains() {
            this.container.exprHelper = new ExprContains(this.container.exprHelper, null);
            return this.container.rValue;
        }

        public QueryOp(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QueryRValue<T>
    implements IQueryRValue {
        protected Query<T> container;

        public Object property(String name) {
            try {
                ((ExprBinOp)this.container.exprHelper).setRight(new ExprMethod(new PropertyDescriptor(name, this.container.clazz).getReadMethod()));
            }
            catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            if (this.container.expr instanceof ExprBinOp) {
                ((ExprBinOp)this.container.expr).setRight(this.container.exprHelper);
            } else {
                this.container.expr = this.container.exprHelper;
            }
            return this.container.closure;
        }

        public Object index(int i) {
            ((ExprBinOp)this.container.exprHelper).setRight(new ExprIndex(i));
            if (this.container.expr instanceof ExprBinOp) {
                ((ExprBinOp)this.container.expr).setRight(this.container.exprHelper);
            } else {
                this.container.expr = this.container.exprHelper;
            }
            return this.container.closure;
        }

        public Object value(Object value) {
            if (value instanceof Object[]) {
                value = new TreeSet<Object>(Arrays.asList((Object[])value));
            }
            ((ExprBinOp)this.container.exprHelper).setRight(new ExprValue(value));
            if (this.container.expr instanceof ExprBinOp) {
                ((ExprBinOp)this.container.expr).setRight(this.container.exprHelper);
            } else {
                this.container.expr = this.container.exprHelper;
            }
            return this.container.closure;
        }

        public QueryRValue(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QuerySelect<T>
    implements IQuerySelect {
        protected Query<T> container;

        @Override
        public IQuerySelectStar property(String name) {
            try {
                this.container.getters.add(new ExprMethod(new PropertyDescriptor(name, this.container.clazz).getReadMethod()));
            }
            catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            return this.container.selectStar;
        }

        @Override
        public IQuerySelectStar index(int i) {
            this.container.getters.add(new ExprIndex(i));
            return this.container.selectStar;
        }

        @Override
        public IQuerySelectStar all() {
            this.container.getters.add(new ExprElement());
            return this.container.selectStar;
        }

        public QuerySelect(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QuerySelectAgg<T>
    implements IQuerySelectAgg {
        protected Query<T> container;

        @Override
        public IQueryLValue<IQueryClosureAgg> where() {
            this.container.closure = new QueryClosureAgg<T>(this.container);
            return this.container.lValue;
        }

        @Override
        public IQueryGrouper groupBy() {
            return this.container.grouper;
        }

        @Override
        public Iterable<Object> exec() {
            IExpr[] getters = this.container.getters.toArray(new IExpr[this.container.getters.size()]);
            IExpr[] groupers = this.container.groupers.toArray(new IExpr[this.container.groupers.size()]);
            IAggregator[] aggregators = this.container.aggregators.toArray(new IAggregator[this.container.aggregators.size()]);
            if (groupers.length == 0) {
                groupers = new IExpr[]{new ExprValue(new Object())};
            }
            LinkedList<Object> newEntries = new LinkedList<Object>();
            HashMap associativeArray = new HashMap();
            Object[] tempEntry = new Object[getters.length];
            Object[] groups = new Object[groupers.length];
            for (Object entry : this.container.elements) {
                Object[] newEntry;
                int i;
                if (this.container.expr != null && !((Boolean)this.container.expr.evaluate(entry)).booleanValue()) continue;
                HashMap target = associativeArray;
                int i2 = 0;
                while (i2 < getters.length) {
                    tempEntry[i2] = getters[i2].evaluate(entry);
                    ++i2;
                }
                i2 = 0;
                while (i2 < groupers.length) {
                    groups[i2] = groupers[i2].evaluate(entry);
                    ++i2;
                }
                Object groupKey = groups[0];
                int i3 = 0;
                while (i3 < groups.length - 1) {
                    if (!((Map)target).containsKey(groupKey)) {
                        ((Map)target).put(groupKey, new HashMap());
                    }
                    target = ((Map)target).get(groupKey);
                    groupKey = groups[++i3];
                }
                if (!((Map)target).containsKey(groupKey)) {
                    newEntry = new Object[getters.length];
                    i = 0;
                    while (i < getters.length) {
                        newEntry[i] = tempEntry[i];
                        ++i;
                    }
                    i = 0;
                    while (i < aggregators.length) {
                        newEntry[aggregators[i].getIndex()] = aggregators[i].initialize(tempEntry[aggregators[i].getIndex()]);
                        ++i;
                    }
                    ((Map)target).put(groupKey, newEntry);
                    newEntries.add(newEntry);
                    continue;
                }
                newEntry = (Object[])((Map)target).get(groupKey);
                i = 0;
                while (i < aggregators.length) {
                    newEntry[aggregators[i].getIndex()] = aggregators[i].evaluate(newEntry[aggregators[i].getIndex()], tempEntry[aggregators[i].getIndex()]);
                    ++i;
                }
            }
            return newEntries;
        }

        @Override
        public IQuerySelectAggStar propery(String name) {
            return ((QuerySelectStar)this.container.select.property((String)name)).container.selectAggStar;
        }

        @Override
        public IQuerySelectAggStar index(int i) {
            return ((QuerySelectStar)this.container.select.index((int)i)).container.selectAggStar;
        }

        public QuerySelectAgg(Query<T> container) {
            this.container = container;
        }
    }

    protected static class QuerySelectAggStar<T>
    extends QueryAgg<T>
    implements IQuerySelectAggStar {
        public QuerySelectAggStar(Query<T> arg0) {
            super(arg0);
        }

        @Override
        public IQueryLValue<IQueryClosureAgg> where() {
            return this.container.selectAgg.where();
        }

        @Override
        public Iterable<Object> exec() {
            return this.container.selectAgg.exec();
        }

        @Override
        public IQuerySelectAgg propery(String name) {
            return ((QuerySelectStar)this.container.select.property((String)name)).container.selectAgg;
        }

        @Override
        public IQuerySelectAgg index(int i) {
            return ((QuerySelectStar)this.container.select.index((int)i)).container.selectAgg;
        }
    }

    protected static class QuerySelectStar<T>
    extends QueryAgg<T>
    implements IQuerySelectStar {
        public QuerySelectStar(Query<T> arg0) {
            super(arg0);
        }

        @Override
        public IQuerySelectStar property(String name) {
            return this.container.select.property(name);
        }

        @Override
        public IQuerySelectStar index(int i) {
            return this.container.select.index(i);
        }

        @Override
        public IQuerySelectStar all() {
            return this.container.select.all();
        }

        @Override
        public IQueryLValue<IQueryClosure> where() {
            this.container.closure = new QueryClosure(this.container);
            return this.container.lValue;
        }

        @Override
        public Iterable<Object> exec() {
            IExpr[] getters = this.container.getters.toArray(new IExpr[this.container.getters.size()]);
            LinkedList<Object> newEntries = new LinkedList<Object>();
            for (Object entry : this.container.elements) {
                if (this.container.expr != null && !((Boolean)this.container.expr.evaluate(entry)).booleanValue()) continue;
                Object[] newEntry = new Object[getters.length];
                int i = 0;
                while (i < getters.length) {
                    newEntry[i] = getters[i].evaluate(entry);
                    ++i;
                }
                newEntries.add(newEntry);
            }
            return newEntries;
        }
    }

}

