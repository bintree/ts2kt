/*
 * Copyright 2013-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package typescript

native
trait ISyntaxElement {
    fun kind(): SyntaxKind

    fun isNode(): Boolean
    fun isToken(): Boolean
    fun isList(): Boolean
    fun isSeparatedList(): Boolean

    fun childCount(): Int /* number */
    fun childAt(index: Int /* number */): ISyntaxElement

    // True if this element is typescript specific and would not be legal in pure javascript.
    fun isTypeScriptSpecific(): Boolean

    // True if this element cannot be reused in incremental parsing.  There are several situations
    // in which an element can not be reused.  They are:
    //
    // 1) The element contained skipped text.
    // 2) The element contained zero width tokens.
    // 3) The element contains tokens generated by the parser (like >> or a keyword -> identifier
    //    conversion).
    // 4) The element contains a regex token somewhere under it.  A regex token is either a
    //    regex itself (i.e. /foo/), or is a token which could start a regex (i.e. "/" or "/=").  This
    //    data is used by the incremental parser to decide if a node can be reused.  Due to the
    //    lookahead nature of regex tokens, a node containing a regex token cannot be reused.  Normally,
    //    changes to text only affect the tokens directly intersected.  However, because regex tokens
    //    have such unbounded lookahead (technically bounded at the end of a line, but htat's minor),
    //    we need to recheck them to see if they've changed due to the edit.  For example, if you had:
    //
    //         while (true) /3; return;
    //
    //    And you changed it to:
    //
    //         while (true) /3; return/;
    //
    //    Then even though only the 'return' and ';' colons were touched, we'd want to rescan the '/'
    //    token which we would then realize was a regex.
    fun isIncrementallyUnusable(): Boolean

    // With of this element, including leading and trailing trivia.
    fun fullWidth(): Int /* number */

    // Width of this element, not including leading and trailing trivia.
    fun width(): Int /* number */

    // Text for this element, including leading and trailing trivia.
    fun fullText(): String

    fun leadingTrivia(): ISyntaxTriviaList
    fun trailingTrivia(): ISyntaxTriviaList

    fun leadingTriviaWidth(): Int /* number */
    fun trailingTriviaWidth(): Int /* number */

    fun firstToken(): ISyntaxToken
    fun lastToken(): ISyntaxToken

    fun collectTextElements(elements: Array<String>)
}

native
trait ISyntaxNodeOrToken : ISyntaxElement {
    fun withLeadingTrivia(trivia: ISyntaxTriviaList): ISyntaxNodeOrToken
    fun withTrailingTrivia(trivia: ISyntaxTriviaList): ISyntaxNodeOrToken

    fun accept(visitor: ISyntaxVisitor<Any>): Any
}

native
trait ISyntaxNode : ISyntaxNodeOrToken

native
trait IModuleReferenceSyntax : ISyntaxNode

native
trait IModuleElementSyntax : ISyntaxNode

native
trait IStatementSyntax : IModuleElementSyntax

native
trait ITypeMemberSyntax : ISyntaxNode

native
trait IClassElementSyntax : ISyntaxNode

native
trait IMemberDeclarationSyntax : IClassElementSyntax

native
trait ISwitchClauseSyntax : ISyntaxNode

native
trait IExpressionSyntax : ISyntaxNodeOrToken

native
trait IUnaryExpressionSyntax : IExpressionSyntax

native
trait ITypeSyntax : ShouldBeEscaped, IUnaryExpressionSyntax

native
trait INameSyntax : ShouldBeEscaped, ITypeSyntax

// Sugar
native
trait IIdentifierSyntax : ShouldBeEscaped, ISyntaxToken

native
trait ShouldBeEscaped: ISyntaxElement

