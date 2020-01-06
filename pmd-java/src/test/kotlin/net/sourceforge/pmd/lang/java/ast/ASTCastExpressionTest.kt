/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

class ASTCastExpressionTest : ParserTestSpec({

    parserTest("Simple cast") {

        inContext(ExpressionParsingCtx) {

            "(Foo) obj" should parseAs {
                castExpr {
                    it::getCastType shouldBe classType("Foo")
                    unspecifiedChild()
                }
            }

            "(@F Foo) obj" should parseAs {
                castExpr {
                    it::getCastType shouldBe classType("Foo") {
                        annotation("F")
                    }
                    unspecifiedChild()
                }
            }
        }
    }

    parserTest("Nested casts") {

        inContext(ExpressionParsingCtx) {
            "(Foo) (int) obj" should parseAs {
                castExpr {
                    classType("Foo")
                    castExpr {
                        primitiveType(INT)
                        variableAccess("obj")
                    }
                }
            }
        }
    }

    parserTest("Test intersection in cast", javaVersions = JavaVersion.J1_8..Latest) {

        inContext(ExpressionParsingCtx) {
            "(@F Foo & Bar) obj" should parseAs {

                castExpr {
                    it::getCastType shouldBe child<ASTIntersectionType> {

                        it::getDeclaredAnnotations shouldBe emptyList()

                        classType("Foo") {
                            // annotations nest on the inner node
                            it::getDeclaredAnnotations shouldBe listOf(annotation("F"))
                        }

                        classType("Bar")
                    }

                    unspecifiedChild()
                }

            }

            "(@F Foo & @B@C Bar) obj" should parseAs {

                castExpr {
                    it::getCastType shouldBe child<ASTIntersectionType> {

                        it::getDeclaredAnnotations shouldBe emptyList()

                        classType("Foo") {
                            // annotations nest on the inner node
                            it::getDeclaredAnnotations shouldBe listOf(annotation("F"))
                        }

                        classType("Bar") {
                            it::getDeclaredAnnotations shouldBe listOf(annotation("B"), annotation("C"))
                        }
                    }

                    unspecifiedChild()
                }
            }
        }
    }

    parserTest("Test intersection ambiguity", javaVersions = Earliest..Latest) {
        inContext(ExpressionParsingCtx) {


            "(modifiers & InputEvent.Foo) != 0" should parseAs {
                equalityExpr(BinaryOp.NE) {
                    parenthesized {
                        andExpr {
                            variableAccess("modifiers")
                            fieldAccess("Foo") {
                                unspecifiedChild()
                            }
                        }
                    }

                    number()
                }
            }


            "(modifiers) != 0" should parseAs {
                equalityExpr(BinaryOp.NE) {
                    parenthesized {
                        variableAccess("modifiers")
                    }

                    number()
                }
            }


            "(modifiers) * 0" should parseAs {
                multiplicativeExpr(BinaryOp.MUL) {
                    parenthesized {
                        variableAccess("modifiers")
                    }

                    number()
                }
            }

        }
    }


})