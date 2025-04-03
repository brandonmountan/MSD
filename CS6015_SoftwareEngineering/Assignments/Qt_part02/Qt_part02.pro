QT += widgets
TARGET = Qt_part02
TEMPLATE = app

SOURCES += main.cpp \
           mainwindow.cpp \
           msdscript/env.cpp \
           msdscript/expr.cpp \
           msdscript/parse.cpp \
           msdscript/val.cpp

HEADERS += mainwindow.h \
           MSDScript/parse.hpp \
           MSDScript/pointer.h \
           msdscript/env.h \
           msdscript/expr.h \
           msdscript/parse.h \
           msdscript/val.h
