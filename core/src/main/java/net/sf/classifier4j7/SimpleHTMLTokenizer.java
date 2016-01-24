/*
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Nick Lothian. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:  
 *       "This product includes software developed by the 
 *        developers of Classifier4J (http://classifier4j.sf.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "Classifier4J" must not be used to endorse or promote 
 *    products derived from this software without prior written 
 *    permission. For written permission, please contact   
 *    http://sourceforge.net/users/nicklothian/.
 *
 * 5. Products derived from this software may not be called 
 *    "Classifier4J", nor may "Classifier4J" appear in their names 
 *    without prior written permission. For written permission, please 
 *    contact http://sourceforge.net/users/nicklothian/.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package net.sf.classifier4j7;

import net.sf.classifier4j7.tokenizer.DefaultTokenizer;
import net.sf.classifier4j7.tokenizer.ITokenizer;
import net.sf.classifier4j7.tokenizer.TokenizerMethod;

import java.util.ArrayDeque;
import java.util.Deque;
/**
 * <p>Simple HTML Tokenizer. Its goal is to tokenize words that would be displayed
 * in a normal web browser.</p>
 * 
 * <p>It does not handle meta tags, alt or text attributes, but it does remove 
 * CSS style definitions and javascript code.</p>
 * 
 * <p>It handles entity references by replacing them with a space(!!). This can be 
 * overridden.</p> 
 * 
 * 
 * @since 18 Nov 2003
 * @author Nick Lothian
 */
public class SimpleHTMLTokenizer extends DefaultTokenizer {

    /**
     * Constructor that using the BREAK_ON_WORD_BREAKS tokenizer config by default
     */
    public SimpleHTMLTokenizer() {
        super();
    }

    public SimpleHTMLTokenizer(TokenizerMethod tokenizerConfig) {
        super(tokenizerConfig);
    }

    /**
     * Replaces entity references with spaces
     * 
     * @param contentsWithUnresolvedEntityReferences the contents with the entity references
     * @return the contents with the entities replaces with spaces
     */
    protected String resolveEntities(String contentsWithUnresolvedEntityReferences) {
        if (contentsWithUnresolvedEntityReferences == null) {
            throw new IllegalArgumentException("Cannot pass null");
        }

        return contentsWithUnresolvedEntityReferences.replaceAll("&.{2,8};", " ");
    }

    /**
     * @see ITokenizer#tokenize(java.lang.String)
     */
    @Override
    public String[] tokenize(String input) {
        Deque<Boolean> stack = new ArrayDeque<>();
        Deque<String> tagStack = new ArrayDeque<>();

        // iterate over the input string and parse find text that would be displayed
        char[] chars = input.toCharArray();

        StringBuilder result = new StringBuilder();

        StringBuilder currentTagName = new StringBuilder();
        for (char c : chars) {
            switch (c) {
                case '<':
                    stack.push(Boolean.TRUE);
                    currentTagName = new StringBuilder();
                    break;
                case '>':
                    stack.pop();
                    handleClosingTagChar(tagStack, currentTagName);
                    break;
                default:
                    handleChar(stack, tagStack, result, currentTagName, c);
                    break;
            }
        }

        return super.tokenize(resolveEntities(result.toString()).trim());
    }

    public void handleChar(Deque<Boolean> stack, Deque<String> tagStack, StringBuilder result, StringBuilder currentTagName, char c) {
        if (stack.isEmpty()) {
            String currentTag = tagStack.peek();
            // ignore everything inside <script></script> or <style></style> tags
            if (currentTag != null) {
                if (!(currentTag.startsWith("script") || currentTag.startsWith("style"))) {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        } else {
            currentTagName.append(c);
        }
    }

    public void handleClosingTagChar(Deque<String> tagStack, StringBuilder currentTagName) {
        if (currentTagName != null) {
            String currentTag = currentTagName.toString();
            if (currentTag.startsWith("/")) {
                tagStack.pop();
            } else {
                tagStack.push(currentTag.toLowerCase());
            }
        }
    }

}
