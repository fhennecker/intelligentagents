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

public interface ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> {
    public RETURN_TYPE visitAnnotation(Annotation var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitArgument(Argument var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitAssignment(Assignment var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitBinary(Binary var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitBlock(Block var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitBooleanLiteral(BooleanLiteral var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitBreak(Break var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitCall(Call var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitCase(Case var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitCast(Cast var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitCharLiteral(CharLiteral var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitClassDecl(ClassDecl var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitConstructorDecl(ConstructorDecl var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitContinue(Continue var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitDefaultValue(DefaultValue var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitDoWhile(DoWhile var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitEnumConstant(EnumConstant var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitFieldDecl(FieldDecl var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitFieldRef(FieldRef var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitForeach(Foreach var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitIf(If var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitArrayRef(ArrayRef var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitInitializer(Initializer var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitInstanceOf(InstanceOf var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitJavaDoc(JavaDoc var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitLocalDecl(LocalDecl var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitMethodDecl(MethodDecl var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitNameRef(NameRef var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitNew(New var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitNewArray(NewArray var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitNullLiteral(NullLiteral var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitNumberLiteral(NumberLiteral var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitReturn(Return var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitReturnDefault(ReturnDefault var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitStringLiteral(StringLiteral var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitSwitch(Switch var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitSynchronized(Synchronized var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitThis(This var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitThrow(Throw var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitTry(Try var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitTypeParam(TypeParam var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitTypeRef(TypeRef var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitUnary(Unary var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitWhile(While var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitWildcard(Wildcard var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitWrappedExpression(WrappedExpression var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitWrappedMethodDecl(WrappedMethodDecl var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitWrappedStatement(WrappedStatement var1, PARAMETER_TYPE var2);

    public RETURN_TYPE visitWrappedTypeRef(WrappedTypeRef var1, PARAMETER_TYPE var2);
}

