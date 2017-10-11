/**
 * Copyright (c) 2014 Richard Warburton (richard.warburton@gmail.com)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package com.insightfullogic.honest_profiler.core.parser;

import com.insightfullogic.honest_profiler.core.collector.Frame;

import java.util.Objects;

public final class Method implements LogEvent, Frame
{
    private final long methodId;
    private final String fileName;
    private final String className;
    private final String classNameGeneric;
    private final String methodName;
    private final String methodSignature;
    private final String methodSignatureGeneric;
    private final String methodReturnType;

    // TODO: parse class and method generic signatures
    public Method(long methodId, String fileName, String className, String classNameGeneric, 
                  String methodName, String methodSignature, String methodSignatureGeneric)
    {
        this.methodId = methodId;
        this.fileName = fileName;
        this.className = formatClassName(className);
        this.classNameGeneric = classNameGeneric; // TODO: parse to human-readable string
        this.methodName = methodName;
        this.methodSignature = formatSignature(methodSignature);
        this.methodSignatureGeneric = methodSignatureGeneric; // TODO: parse to human-readable string
        this.methodReturnType = getTypeFromSignature(methodSignature);
    }

    public Method(long methodId, String fileName, String className, String methodName) {
        this(methodId, fileName, className, "", methodName, "", "");
    }

    // Avoid formatting class name in copy()
    private Method(long methodId,
                   String fileName,
                   String className,
                   String classNameGeneric,
                   String methodName,
                   String methodSignature,
                   String methodSignatureGeneric,
                   String methodReturnType,
                   boolean dummy)
    {
        this.methodId = methodId;
        this.fileName = fileName;
        this.className = className;
        this.classNameGeneric = classNameGeneric;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
        this.methodSignatureGeneric = methodSignatureGeneric;
        this.methodReturnType = methodReturnType;
    }

    private String formatClassName(String className)
    {
        if (className.isEmpty())
        {
            return className;
        }

        return className.substring(1, className.length() - 1)
            .replace('/', '.');
    }

    private String getTypeFromSignature(String signature) {
        if (signature.isEmpty()) 
        {
            return signature;
        }
        StringBuilder strB = new StringBuilder();
        convertTypeName(strB, signature.indexOf(')') + 1, signature);
        return strB.toString();
    }

    private String formatSignature(String signature) 
    {
        if (signature.isEmpty()) 
        {
            return signature;
        }
        StringBuilder sbuf = new StringBuilder("(");
        int charPos = 1;
        while (signature.charAt(charPos) != ')') {
            if (charPos > 1) 
            {
                sbuf.append(',');
            }
            charPos = convertTypeName(sbuf, charPos, signature);
        }

        return sbuf.append(')').toString();
    }

    private int convertTypeName(StringBuilder strB, int pos, String signature) 
    {
        final char[] chars = signature.toCharArray();
        int k = 0;
        while (chars[pos] == '[') {
            k++;
            pos++;
        }
        int nextPos = pos + 1;
        switch (chars[pos]) {
        case 'B':
            strB.append("byte");
            break;
        case 'C':
            strB.append("char");
            break;
        case 'D':
            strB.append("double");
            break;
        case 'F':
            strB.append("float");
            break;
        case 'I':
            strB.append("int");
            break;
        case 'J':
            strB.append("long");
            break;
        case 'L':
            nextPos = signature.indexOf(';', pos) + 1;
            strB.append(formatClassName(signature.substring(pos, nextPos)));
            break;
        case 'S':
            strB.append("short");
            break;
        case 'V':
            strB.append("void");
            break;
        case 'Z':
            strB.append("boolean");
            break;
        }
        while (k > 0) {
            strB.append("[]");
            k--;
        }
        return nextPos;
    }

    @Override
    public void accept(LogEventListener listener)
    {
        listener.handle(this);
    }

    @Override
    public long getMethodId()
    {
        return methodId;
    }

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String getClassName()
    {
        return className;
    }

    @Override
    public String getMethodName()
    {
        return methodName;
    }

    @Override
    public String getMethodSignature()
    {
        return methodSignature;
    }

    @Override
    public String getMethodReturnType()
    {
        return methodReturnType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Method method = (Method) o;
        return methodId == method.methodId;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(methodId);
    }

    @Override
    public String toString()
    {
        return "Method{" +
            "methodId=" + methodId +
            ", fileName='" + fileName + '\'' +
            ", className='" + className + '\'' +
            ", classNameGeneric='" + classNameGeneric + '\'' +
            ", methodName='" + methodName + '\'' +
            ", methodSignature='" + methodSignature + '\'' +
            ", methodSignatureGeneric='" + methodSignatureGeneric + '\'' +
            ", methodReturnType='" + methodReturnType + '\'' +
            '}';
    }

    @Override
    public int getBci()
    {
        return Frame.BCI_ERR_IGNORE;
    }

    @Override
    public int getLine()
    {
        return 0;
    }

    @Override
    public Method copy()
    {
        return new Method(methodId, fileName, className, classNameGeneric, methodName, 
            methodSignature, methodSignatureGeneric, methodReturnType, true);
    }
}
