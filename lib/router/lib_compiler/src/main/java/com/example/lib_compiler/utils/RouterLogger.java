package com.example.lib_compiler.utils;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class RouterLogger {
    private Messager messager;
    private boolean openLog=true;

    public RouterLogger(Messager messager) {
        this.messager = messager;
    }

    public void setOpenLog(boolean openLog) {
        this.openLog = openLog;
    }

    public void info(CharSequence info) {
        if (!openLog) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.NOTE, info);
    }

    public void info(Element element, CharSequence info) {
        if (!openLog) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.NOTE, info, element);
    }

    public void warn(CharSequence info) {
        if (!openLog) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.WARNING, info);
    }

    public void warn(Element element, CharSequence info) {
        if (!openLog) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.WARNING, info, element);
    }

    public void error(CharSequence info) {
        if (!openLog) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.ERROR, info);
    }

    public void error(Element element, CharSequence info) {
        if (!openLog) {
            return;
        }
        messager.printMessage(Diagnostic.Kind.ERROR, info, element);
    }
}
