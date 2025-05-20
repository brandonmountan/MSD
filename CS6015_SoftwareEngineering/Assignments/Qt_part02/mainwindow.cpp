#include "mainwindow.h"
#include "MSDScript/parse.hpp"
#include "MSDScript/expr.h"
#include "MSDScript/val.h"
#include <QMessageBox>

MainWindow::MainWindow(QWidget *parent) : QMainWindow(parent)
{
    setupUI();
    setupConnections();
    setWindowTitle("MSDScript Interpreter");
    resize(600, 500);
}

void MainWindow::setupUI()
{
    QWidget *centralWidget = new QWidget(this);
    QVBoxLayout *mainLayout = new QVBoxLayout(centralWidget);

    // Input group
    QGroupBox *inputGroup = new QGroupBox("MSDScript Expression", this);
    QVBoxLayout *inputLayout = new QVBoxLayout();
    inputEdit = new QTextEdit();
    inputEdit->setPlaceholderText("Enter your MSDScript expression here...");
    inputLayout->addWidget(inputEdit);
    inputGroup->setLayout(inputLayout);

    // Output group
    QGroupBox *outputGroup = new QGroupBox("Result", this);
    QVBoxLayout *outputLayout = new QVBoxLayout();
    outputEdit = new QTextEdit();
    outputEdit->setReadOnly(true);
    outputLayout->addWidget(outputEdit);
    outputGroup->setLayout(outputLayout);

    // Buttons
    QHBoxLayout *buttonLayout = new QHBoxLayout();
    interpretButton = new QPushButton("Interpret", this);
    printButton = new QPushButton("Pretty Print", this);
    loadButton = new QPushButton("Load from File", this);
    buttonLayout->addWidget(interpretButton);
    buttonLayout->addWidget(printButton);
    buttonLayout->addWidget(loadButton);

    // Add to main layout
    mainLayout->addWidget(inputGroup);
    mainLayout->addWidget(outputGroup);
    mainLayout->addLayout(buttonLayout);

    setCentralWidget(centralWidget);
}

void MainWindow::setupConnections()
{
    connect(interpretButton, &QPushButton::clicked, this, &MainWindow::interpretExpression);
    connect(printButton, &QPushButton::clicked, this, &MainWindow::printExpression);
    connect(loadButton, &QPushButton::clicked, this, &MainWindow::loadFromFile);
}

void MainWindow::interpretExpression()
{
    try {
        std::string input = inputEdit->toPlainText().toUtf8().constData();
        PTR(Expr) e = parse_str(input);
        PTR(Val) v = e->interp(Env::empty); // Changed to use interp() without Env parameter
        outputEdit->setText(QString::fromStdString(v->to_string()));
    } catch (std::runtime_error &e) {
        QMessageBox::critical(this, "Error", e.what());
    }
}

void MainWindow::printExpression()
{
    try {
        std::string input = inputEdit->toPlainText().toUtf8().constData();
        PTR(Expr) e = parse_str(input);
        outputEdit->setText(QString::fromStdString(e->to_pretty_string()));
    } catch (std::runtime_error &e) {
        QMessageBox::critical(this, "Error", e.what());
    }
}

void MainWindow::loadFromFile()
{
    QString fileName = QFileDialog::getOpenFileName(this, "Open MSDScript File", "", "Text Files (*.txt);;All Files (*)");
    if (!fileName.isEmpty()) {
        QFile file(fileName);
        if (file.open(QIODevice::ReadOnly | QIODevice::Text)) {
            QTextStream in(&file);
            inputEdit->setText(in.readAll());
            file.close();
        } else {
            QMessageBox::critical(this, "Error", "Could not open file");
        }
    }
}
