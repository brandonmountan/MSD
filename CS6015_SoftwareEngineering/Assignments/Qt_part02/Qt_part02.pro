QT += widgets
TARGET = Qt_part02
TEMPLATE = app

SOURCES += main.cpp \
           MSDScript/expr.cpp \
           MSDScript/parse.cpp \
           MSDScript/val.cpp \
           mainwindow.cpp \
           msdscript/env.cpp

HEADERS += mainwindow.h \
           MSDScript/env.h \
           MSDScript/expr.h \
           MSDScript/parse.hpp \
           MSDScript/val.h \
           msdscript/env.h \
           msdscript/parse.h
