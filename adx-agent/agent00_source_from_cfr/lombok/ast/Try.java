/*
 * Decompiled with CFR 0_110.
 */
package lombok.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.ast.ASTVisitor;
import lombok.ast.Argument;
import lombok.ast.Block;
import lombok.ast.Node;
import lombok.ast.Statement;

public final class Try
extends Statement<Try> {
    private final List<Argument> catchArguments = new ArrayList<Argument>();
    private final List<Block> catchBlocks = new ArrayList<Block>();
    private final Block tryBlock;
    private Block finallyBlock;

    public Try(Block tryBlock) {
        this.tryBlock = this.child(tryBlock);
    }

    public Try Catch(Argument catchArgument, Block catchBlock) {
        this.catchArguments.add(this.child(catchArgument));
        this.catchBlocks.add(this.child(catchBlock));
        return this;
    }

    public Try Finally(Block finallyBlock) {
        this.finallyBlock = this.child(finallyBlock);
        return this;
    }

    @Override
    public <RETURN_TYPE, PARAMETER_TYPE> RETURN_TYPE accept(ASTVisitor<RETURN_TYPE, PARAMETER_TYPE> v, PARAMETER_TYPE p) {
        return v.visitTry(this, p);
    }

    public List<Argument> getCatchArguments() {
        return this.catchArguments;
    }

    public List<Block> getCatchBlocks() {
        return this.catchBlocks;
    }

    public Block getTryBlock() {
        return this.tryBlock;
    }

    public Block getFinallyBlock() {
        return this.finallyBlock;
    }
}

