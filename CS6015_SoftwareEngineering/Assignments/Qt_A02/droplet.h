#ifndef DROPLET_H
#define DROPLET_H

#include <QObject>
#include <QtWidgets/QGraphicsPixmapItem>
#include <QTimer>

class Droplet : public QObject, public QGraphicsPixmapItem
{
    Q_OBJECT
public:
    Droplet(QObject* parent = nullptr);
public slots:
    void move();
};

#endif // DROPLET_H
