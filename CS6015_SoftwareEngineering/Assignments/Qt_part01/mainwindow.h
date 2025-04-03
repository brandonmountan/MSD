#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include <QTextEdit>
#include <QPushButton>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QFileDialog>
#include <QLabel>
#include <QGroupBox>

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget *parent = nullptr);

private slots:
    void interpretExpression();
    void printExpression();
    void loadFromFile();

private:
    void setupUI();
    void setupConnections();

    QTextEdit *inputEdit;
    QTextEdit *outputEdit;
    QPushButton *interpretButton;
    QPushButton *printButton;
    QPushButton *loadButton;
};

#endif // MAINWINDOW_H
