#ifndef MAINWIDGET_H
#define MAINWIDGET_H

#include <QWidget>
#include <QGridLayout>
#include <QVBoxLayout>
#include <QGroupBox>
#include <QLabel>
#include <QLineEdit>
#include <QSpinBox>
#include <QRadioButton>
#include <QPushButton>
#include <QTextEdit>
#include <QSpacerItem>

class MainWidget : public QWidget
{
    Q_OBJECT

public:
    explicit MainWidget(QWidget *parent = nullptr);

private slots:
    void fillSummary();
    void clearAll();

private:
    void setupGridLayout();
    void setupVerticalLayout();

    // Input widgets
    QLineEdit *firstNameEdit;
    QLineEdit *lastNameEdit;
    QSpinBox *ageSpinBox;
    QRadioButton *maleRadio;
    QRadioButton *femaleRadio;
    QTextEdit *summaryText;

    // Buttons
    QPushButton *refreshButton;
    QPushButton *finishButton;

    // Layouts
    QGridLayout *gridLayout;
    QVBoxLayout *verticalLayout;
    QGroupBox *genderGroup;
};

#endif // MAINWIDGET_H
