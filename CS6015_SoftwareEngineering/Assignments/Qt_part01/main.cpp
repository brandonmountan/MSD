#include <QApplication>
#include "mainwidget.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);

    MainWidget mainWidget;
    mainWidget.setWindowTitle("Qt Form Application");
    mainWidget.resize(400, 400);
    mainWidget.show();

    return app.exec();
}
