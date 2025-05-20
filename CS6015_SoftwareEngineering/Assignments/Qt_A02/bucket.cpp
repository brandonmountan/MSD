#include "bucket.h"

Bucket::Bucket(QObject* parent) : QObject(parent)
{
    // Set the bucket image
    setPixmap(QPixmap(":/images/bucket.png").scaled(150, 150));

    // Make the bucket focusable
    setFlag(QGraphicsItem::ItemIsFocusable);
}

void Bucket::keyPressEvent(QKeyEvent* event)
{
    // Move the bucket left or right when arrow keys are pressed
    if (event->key() == Qt::Key_Left) {
        if (x() > 0) {
            setPos(x() - 20, y());
        }
    } else if (event->key() == Qt::Key_Right) {
        if (x() < 760) {  // Prevent going beyond right edge
            setPos(x() + 20, y());
        }
    }
}
