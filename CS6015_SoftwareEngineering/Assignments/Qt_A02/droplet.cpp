#include "droplet.h"
#include <QtWidgets/QGraphicsScene>
#include <QList>
#include "bucket.h"

Droplet::Droplet(QObject* parent) : QObject(parent)
{
    // Set the droplet image
    setPixmap(QPixmap(":/images/water.png").scaled(30, 30));

    // Set random x position
    int randomX = rand() % 880;
    setPos(randomX, 0);

    // Connect timer to move the droplet
    QTimer* timer = new QTimer(this);
    connect(timer, &QTimer::timeout, this, &Droplet::move);
    timer->start(50);
}

void Droplet::move()
{
    // Move the droplet down
    setPos(x(), y() + 5);

    // Check for collisions with bucket
    QList<QGraphicsItem*> collidingItems = this->collidingItems();
    for (QGraphicsItem* item : collidingItems) {
        if (dynamic_cast<Bucket*>(item)) {
            // Remove and delete the droplet if caught by bucket
            scene()->removeItem(this);
            delete this;
            return;
        }
    }

    // Delete the droplet if it goes out of the scene
    if (y() > 512) {
        scene()->removeItem(this);
        delete this;
    }
}
