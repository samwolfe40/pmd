/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/* Generated By:JJTree: Do not edit this line. ASTSwitchLabel.java */

package net.sourceforge.pmd.lang.java.ast;

public class ASTSwitchLabel extends AbstractJavaNode {
    public ASTSwitchLabel(int id) {
        super(id);
    }

    public ASTSwitchLabel(JavaParser p, int id) {
        super(p, id);
    }

    private boolean isDefault;

    public void setDefault() {
        isDefault = true;
    }

    public boolean isDefault() {
        return isDefault;
    }


    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}