#include "mainwidget.h"

MainWidget::MainWidget(QWidget *parent) : QWidget(parent)
{
    // Create widgets
    firstNameEdit = new QLineEdit(this);
    lastNameEdit = new QLineEdit(this);
    ageSpinBox = new QSpinBox(this);
    ageSpinBox->setRange(1, 120);

    maleRadio = new QRadioButton("Male", this);
    femaleRadio = new QRadioButton("Female", this);
    maleRadio->setChecked(true); // Default selection

    refreshButton = new QPushButton("Refresh", this);
    finishButton = new QPushButton("Finish", this);

    summaryText = new QTextEdit(this);
    summaryText->setReadOnly(true);

    // Create layouts
    gridLayout = new QGridLayout();
    verticalLayout = new QVBoxLayout();

    // Setup layouts
    setupGridLayout();
    setupVerticalLayout();

    // Set main layout
    QVBoxLayout *mainLayout = new QVBoxLayout(this);
    mainLayout->addLayout(gridLayout);
    mainLayout->addLayout(verticalLayout);

    // Connect signals to slots
    connect(refreshButton, &QPushButton::clicked, this, &MainWidget::fillSummary);
    connect(finishButton, &QPushButton::clicked, this, &MainWidget::clearAll);
}

void MainWidget::setupGridLayout()
{
    // Add labels and input fields to grid layout
    gridLayout->addWidget(new QLabel("First Name:"), 0, 0);
    gridLayout->addWidget(firstNameEdit, 0, 1);

    gridLayout->addWidget(new QLabel("Last Name:"), 1, 0);
    gridLayout->addWidget(lastNameEdit, 1, 1);

    gridLayout->addWidget(new QLabel("Age:"), 2, 0);
    gridLayout->addWidget(ageSpinBox, 2, 1);

    // Gender radio buttons in a group box
    genderGroup = new QGroupBox("Gender", this);
    QVBoxLayout *genderLayout = new QVBoxLayout();
    genderLayout->addWidget(maleRadio);
    genderLayout->addWidget(femaleRadio);
    genderGroup->setLayout(genderLayout);

    gridLayout->addWidget(genderGroup, 3, 0, 1, 2);

    // Add spacer for better layout
    gridLayout->addItem(new QSpacerItem(20, 40, QSizePolicy::Minimum, QSizePolicy::Expanding), 4, 0, 1, 2);
}

void MainWidget::setupVerticalLayout()
{
    verticalLayout->addWidget(summaryText);

    QHBoxLayout *buttonLayout = new QHBoxLayout();
    buttonLayout->addWidget(refreshButton);
    buttonLayout->addWidget(finishButton);

    verticalLayout->addLayout(buttonLayout);
}

void MainWidget::fillSummary()
{
    QString summary;
    summary += QString("First Name: %1\n").arg(firstNameEdit->text());
    summary += QString("Last Name: %1\n").arg(lastNameEdit->text());
    summary += QString("Age: %1\n").arg(ageSpinBox->value());
    summary += QString("Gender: %1\n").arg(maleRadio->isChecked() ? "Male" : "Female");

    summaryText->setText(summary);
}

void MainWidget::clearAll()
{
    firstNameEdit->clear();
    lastNameEdit->clear();
    ageSpinBox->setValue(0);
    maleRadio->setChecked(true);
    summaryText->clear();
}
