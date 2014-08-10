package com.insightfullogic.honest_profiler.core.parser;

import java.util.Objects;

public final class Method implements LogEvent {

    private final long methodId;
    private final String fileName;
    private final String className;
    private final String methodName;

    public Method(long methodId, String fileName, String className, String methodName) {
        this.methodId = methodId;
        this.fileName = fileName;
        this.className = formatClassName(className);
        this.methodName = methodName;
    }

    private String formatClassName(String className) {
        if (className.isEmpty())
            return className;

        return className.substring(1, className.length() - 1)
                        .replace('/', '.');
    }

    @Override
    public void accept(EventListener listener) {
        listener.handle(this);
    }

    public long getMethodId() {
        return methodId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Method method = (Method) o;
        return methodId == method.methodId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodId);
    }

    @Override
    public String toString() {
        return "Method{" +
                "methodId=" + methodId +
                ", fileName='" + fileName + '\'' +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }

}
