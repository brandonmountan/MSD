#ifndef GAME1SCENE_H
#define GAME1SCENE_H

#include <QtWidgets/QGraphicsScene>
#include <QTimer>
#include "bucket.h"
#include "droplet.h"

class Game1Scene : public QGraphicsScene
{
    Q_OBJECT
public:
    Game1Scene(QObject* parent = nullptr);
private:
    Bucket* bucket;
    QTimer* spawnTimer;
private slots:
    void spawnDroplet();
};

#endif // GAME1SCENE_H
