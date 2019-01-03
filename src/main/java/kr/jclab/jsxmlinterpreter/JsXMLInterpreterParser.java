/*
 * Copyright 2018 JC-Lab. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.jclab.jsxmlinterpreter;

import kr.jclab.jsxmlinterpreter.exceptions.NotDefinedTagException;
import kr.jclab.jsxmlinterpreter.exceptions.ParseException;
import kr.jclab.jsxmlinterpreter.exceptions.UnknownException;
import kr.jclab.jsxmlinterpreter.instruction.AbstractInstruction;
import kr.jclab.jsxmlinterpreter.internal.instructions.*;
import kr.jclab.jsxmlinterpreter.parser.InstructionParser;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class JsXMLInterpreterParser {
    protected Document document = null;
    protected Element root = null;

    protected JexlEngine jexlEngine;

    protected Map<String, InstructionParser> m_instParsers = new HashMap<>();

    protected Map<String, AbstractInstruction> m_methods = new HashMap<>();

    public JsXMLInterpreterParser() {
        this.jexlEngine = new JexlEngine();
        this.jexlEngine.setCache(512);
        this.jexlEngine.setStrict(true);
        this.jexlEngine.setSilent(false);

        this.addInstructionParser(new IfInstructionParser());
        this.addInstructionParser(new VarInstructionParser());
        this.addInstructionParser(new ForeachInstructionParser());
        this.addInstructionParser(new ReturnInstructionParser());
        this.addInstructionParser(new BreakInstructionParser());
        this.addInstructionParser(new CallInstructionParser());
        this.addInstructionParser(new PrintInstructionParser());
    }

    public JexlEngine getJexlEngine() {
        return this.jexlEngine;
    }

    public Expression createExpression(String expr) {
        if(expr == null || expr.isEmpty())
            return null;
        return this.jexlEngine.createExpression(expr);
    }

    public JsXMLInterpreter parse(String xmlText) throws ParseException, UnknownException {
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(xmlText.getBytes("UTF-8"));
            return parse(inputStream);
        } catch (UnsupportedEncodingException e) {
            throw new UnknownException(e);
        } finally {
            if(inputStream != null) {
                try { inputStream.close(); } catch(IOException e) { }
            }
        }
    }

    public JsXMLInterpreter parse(InputStream inputStream) throws ParseException, UnknownException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.parse(inputStream);
        } catch (ParserConfigurationException e) {
            throw new UnknownException(e);
        } catch (SAXException e) {
            throw new UnknownException(e);
        } catch (IOException e) {
            throw new UnknownException(e);
        }

        this.root = this.document.getDocumentElement();
        if(!"data".equalsIgnoreCase(this.root.getTagName())) {
            throw new NotDefinedTagException(this.root.getTagName());
        }

        for(Node typeNode = this.root.getFirstChild(); typeNode != null; typeNode = typeNode.getNextSibling()) {
            if(typeNode.getNodeType() == Node.ELEMENT_NODE) {
                Element typeElement = (Element)typeNode;
                if ("usercodes".equalsIgnoreCase(typeElement.getTagName())) {
                    for(Node codeNode = typeElement.getFirstChild(); codeNode != null; codeNode = codeNode.getNextSibling()) {
                        if(codeNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element codeElement = (Element)codeNode;
                            String name = codeElement.getTagName() + ":" + JsXMLInterpreterParser.getElementAttribute(codeElement, "name");
                            m_methods.put("USERCODE:" + name, parseBlockElement(codeElement));
                        }
                    }
                } else if ("macros".equalsIgnoreCase(typeElement.getTagName())) {
                    for(Node codeNode = typeElement.getFirstChild(); codeNode != null; codeNode = codeNode.getNextSibling()) {
                        if(codeNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element codeElement = (Element)codeNode;
                            String name = JsXMLInterpreterParser.getElementAttribute(codeElement, "name");
                            m_methods.put("MACRO:" + name, parseBlockElement(codeElement));
                        }
                    }
                }
            }
        }

        return new JsXMLInterpreter(m_methods);
    }

    public void addInstructionParser(InstructionParser instructionParser) {
        m_instParsers.put(instructionParser.getTagName().toLowerCase(), instructionParser);
    }

    public void addFunction(String name, Object handler) {
        Map<String, Object> functions = this.jexlEngine.getFunctions();
        if(functions == null || functions.isEmpty())
            functions = new HashMap<>();
        functions.put(name, handler);
        this.jexlEngine.setFunctions(functions);
    }

    public AbstractInstruction parseBlockElement(Element codeElement) throws ParseException {
        AbstractInstruction firstInstraction = null;
        AbstractInstruction lastInstraction = null;
        for(Node childNode = codeElement.getFirstChild(); childNode != null; ) {
            if(childNode.getNodeType() == Element.ELEMENT_NODE) {
                InstructionParser.ParseResult subParseResult = parseSingleElement((Element)childNode);
                if(subParseResult.rewriteNextElement) {
                    childNode = subParseResult.nextElement;
                }else{
                    childNode = childNode.getNextSibling();
                }
                if(firstInstraction == null) {
                    firstInstraction = subParseResult.instruction;
                    lastInstraction = firstInstraction;
                }else{
                    lastInstraction.setNextInstruction(subParseResult.instruction);
                    lastInstraction = subParseResult.instruction;
                }
            }else{
                childNode = childNode.getNextSibling();
            }
        }
        return firstInstraction;
    }

    private InstructionParser.ParseResult parseSingleElement(Element element) throws ParseException {
        InstructionParser instructionParser = m_instParsers.get(element.getTagName().toLowerCase());
        if(instructionParser == null) {
            throw new NotDefinedTagException(element.getTagName());
        }
        return instructionParser.parseInstruction(this, element);
    }

    public static String getElementAttribute(Element element, String attrName) {
        if(element.hasAttribute(attrName))
            return element.getAttribute(attrName);
        else
            return null;
    }
}
