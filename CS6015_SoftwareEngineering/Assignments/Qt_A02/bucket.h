#ifndef BUCKET_H
#define BUCKET_H

#include <QObject>
#include <QtWidgets/QGraphicsPixmapItem>
#include <QKeyEvent>

class Bucket : public QObject, public QGraphicsPixmapItem
{
    Q_OBJECT
public:
    Bucket(QObject* parent = nullptr);
    void keyPressEvent(QKeyEvent* event) override;
};

#endif // BUCKET_H
