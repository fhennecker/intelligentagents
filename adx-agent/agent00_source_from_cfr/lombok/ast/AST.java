/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import lombok.ast.Annotation;
import lombok.ast.Argument;
import lombok.ast.ArrayRef;
import lombok.ast.Assignment;
import lombok.ast.Binary;
import lombok.ast.Block;
import lombok.ast.BooleanLiteral;
import lombok.ast.Break;
import lombok.ast.Call;
import lombok.ast.Case;
import lombok.ast.Cast;
import lombok.ast.CharLiteral;
import lombok.ast.ClassDecl;
import lombok.ast.ConstructorDecl;
import lombok.ast.Continue;
import lombok.ast.DefaultValue;
import lombok.ast.DoWhile;
import lombok.ast.EnumConstant;
import lombok.ast.Expression;
import lombok.ast.FieldDecl;
import lombok.ast.FieldRef;
import lombok.ast.Foreach;
import lombok.ast.If;
import lombok.ast.Initializer;
import lombok.ast.InstanceOf;
import lombok.ast.JavaDoc;
import lombok.ast.LocalDecl;
import lombok.ast.MethodDecl;
import lombok.ast.NameRef;
import lombok.ast.New;
import lombok.ast.NewArray;
import lombok.ast.NullLiteral;
import lombok.ast.NumberLiteral;
import lombok.ast.Return;
import lombok.ast.ReturnDefault;
import lombok.ast.Statement;
import lombok.ast.StringLiteral;
import lombok.ast.Switch;
import lombok.ast.Synchronized;
import lombok.ast.This;
import lombok.ast.Throw;
import lombok.ast.Try;
import lombok.ast.TypeParam;
import lombok.ast.TypeRef;
import lombok.ast.Unary;
import lombok.ast.While;
import lombok.ast.Wildcard;
import lombok.ast.WrappedExpression;
import lombok.ast.WrappedMethodDecl;
import lombok.ast.WrappedStatement;
import lombok.ast.WrappedTypeRef;

public final class AST {
    public static Binary Add(Expression<?> left, Expression<?> right) {
        return new Binary(left, "+", right);
    }

    public static Binary And(Expression<?> left, Expression<?> right) {
        return new Binary(left, "&&", right);
    }

    public static Annotation Annotation(TypeRef type) {
        return new Annotation(type);
    }

    public static Argument Arg(TypeRef type, String name) {
        return (Argument)new Argument(type, name).makeFinal();
    }

    public static Assignment Assign(Expression<?> left, Expression<?> right) {
        return new Assignment(left, right);
    }

    public static Binary Binary(Expression<?> left, String operator, Expression<?> right) {
        return new Binary(left, operator, right);
    }

    public static Block Block() {
        return new Block();
    }

    public static Break Break() {
        return new Break();
    }

    public static Break Break(String label) {
        return new Break(label);
    }

    public static Call Call(String name) {
        return new Call(name);
    }

    public static Call Call(Expression<?> receiver, String name) {
        return new Call(receiver, name);
    }

    public static Cast Cast(TypeRef type, Expression<?> expression) {
        return new Cast(type, expression);
    }

    public static Case Case(Expression<?> expression) {
        return new Case(expression);
    }

    public static Case Case() {
        return new Case();
    }

    public static CharLiteral Char(String character) {
        return new CharLiteral(character);
    }

    public static ClassDecl ClassDecl(String name) {
        return new ClassDecl(name);
    }

    public static ConstructorDecl ConstructorDecl(String name) {
        return new ConstructorDecl(name);
    }

    public static Continue Continue() {
        return new Continue();
    }

    public static Continue Continue(String label) {
        return new Continue(label);
    }

    public static DefaultValue DefaultValue(TypeRef type) {
        return new DefaultValue(type);
    }

    public static DoWhile Do(Statement<?> action) {
        return new DoWhile(action);
    }

    public static EnumConstant EnumConstant(String name) {
        return new EnumConstant(name);
    }

    public static Binary Equal(Expression<?> left, Expression<?> right) {
        return new Binary(left, "==", right);
    }

    public static Binary NotEqual(Expression<?> left, Expression<?> right) {
        return new Binary(left, "!=", right);
    }

    public static Expression<?> Expr(Object wrappedObject) {
        return new WrappedExpression(wrappedObject);
    }

    public static BooleanLiteral False() {
        return new BooleanLiteral(false);
    }

    public static FieldRef Field(Expression<?> receiver, String name) {
        return new FieldRef(receiver, name);
    }

    public static FieldRef Field(String name) {
        return new FieldRef(name);
    }

    public static FieldDecl FieldDecl(TypeRef type, String name) {
        return new FieldDecl(type, name);
    }

    public static Foreach Foreach(LocalDecl elementVariable) {
        return new Foreach(elementVariable);
    }

    public static If If(Expression<?> condition) {
        return new If(condition);
    }

    public static Initializer Initializer() {
        return new Initializer();
    }

    public static JavaDoc JavaDoc() {
        return new JavaDoc();
    }

    public static JavaDoc JavaDoc(String statement) {
        return new JavaDoc(statement);
    }

    public static InstanceOf InstanceOf(Expression<?> expression, TypeRef type) {
        return new InstanceOf(expression, type);
    }

    public static ArrayRef ArrayRef(Expression<?> indexed, Expression<?> index) {
        return new ArrayRef(indexed, index);
    }

    public static ClassDecl InterfaceDecl(String name) {
        return new ClassDecl(name).makeInterface();
    }

    public static LocalDecl LocalDecl(TypeRef type, String name) {
        return new LocalDecl(type, name);
    }

    public static NameRef Name(String name) {
        return new NameRef(name);
    }

    public static NameRef Name(Class<?> clazz) {
        return new NameRef(clazz);
    }

    public static New New(TypeRef type) {
        return new New(type);
    }

    public static NewArray NewArray(TypeRef type) {
        return new NewArray(type);
    }

    public static NewArray NewArray(TypeRef type, int dimensions) {
        return new NewArray(type, dimensions);
    }

    public static Unary Not(Expression<?> condition) {
        return new Unary("!", condition);
    }

    public static NullLiteral Null() {
        return new NullLiteral();
    }

    public static MethodDecl MethodDecl(TypeRef returnType, String name) {
        return new MethodDecl(returnType, name);
    }

    public static MethodDecl MethodDecl(Object wrappedObject) {
        return new WrappedMethodDecl(wrappedObject);
    }

    public static Argument NonFinalArg(TypeRef type, String name) {
        return new Argument(type, name);
    }

    public static NumberLiteral Number(Number number) {
        return new NumberLiteral(number);
    }

    public static Binary Or(Expression<?> left, Expression<?> right) {
        return new Binary(left, "||", right);
    }

    public static Return Return() {
        return new Return();
    }

    public static Return Return(Expression<?> expression) {
        return new Return(expression);
    }

    public static ReturnDefault ReturnDefault() {
        return new ReturnDefault();
    }

    public static Statement<?> Stat(Object wrappedObject) {
        return new WrappedStatement(wrappedObject);
    }

    public static StringLiteral String(String value) {
        return new StringLiteral(value);
    }

    public static Switch Switch(Expression<?> expression) {
        return new Switch(expression);
    }

    public static This This() {
        return new This();
    }

    public static This This(TypeRef type) {
        return new This(type);
    }

    public static Throw Throw(Expression<?> init) {
        return new Throw(init);
    }

    public static BooleanLiteral True() {
        return new BooleanLiteral(true);
    }

    public static Synchronized Synchronized(Expression<?> lock) {
        return new Synchronized(lock);
    }

    public static Try Try(Block tryBlock) {
        return new Try(tryBlock);
    }

    public static TypeRef Type(Class<?> clazz) {
        return new TypeRef(clazz);
    }

    public static TypeRef Type(String typeName) {
        return new TypeRef(typeName);
    }

    public static TypeRef Type(Object wrappedObject) {
        return new WrappedTypeRef(wrappedObject);
    }

    public static TypeParam TypeParam(String name) {
        return new TypeParam(name);
    }

    public static While While(Expression<?> condition) {
        return new While(condition);
    }

    public static Wildcard Wildcard() {
        return new Wildcard();
    }

    public static Wildcard Wildcard(Wildcard.Bound bound, TypeRef type) {
        return new Wildcard(bound, type);
    }

    private AST() {
    }
}

