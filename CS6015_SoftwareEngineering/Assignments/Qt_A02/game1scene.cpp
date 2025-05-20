#include "game1scene.h"
#include <QtWidgets/QGraphicsView>

Game1Scene::Game1Scene(QObject* parent) : QGraphicsScene(parent)
{
    // Set the scene dimensions and background
    setSceneRect(0, 0, 908, 510);
    setBackgroundBrush(QBrush(QImage(":/images/background.jpg").scaledToHeight(512).scaledToWidth(910)));

    // Create and add the bucket
    bucket = new Bucket();
    addItem(bucket);
    bucket->setPos(400, 365);
    bucket->setFocus();

    // Set up timer to spawn droplets
    spawnTimer = new QTimer(this);
    connect(spawnTimer, &QTimer::timeout, this, &Game1Scene::spawnDroplet);
    spawnTimer->start(1000);  // Spawn a droplet every second
}

void Game1Scene::spawnDroplet()
{
    // Create a new droplet and add it to the scene
    Droplet* droplet = new Droplet();
    addItem(droplet);
}
