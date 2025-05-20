#include <QtWidgets/QApplication>
#include <QtWidgets/QGraphicsView>
#include "game1scene.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    // Create the scene
    Game1Scene* scene = new Game1Scene();

    // Create the view and set its properties
    QGraphicsView* view = new QGraphicsView();
    view->setScene(scene);
    view->setFixedSize(910, 512);
    view->setHorizontalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
    view->setVerticalScrollBarPolicy(Qt::ScrollBarAlwaysOff);
    view->show();

    return a.exec();
}
