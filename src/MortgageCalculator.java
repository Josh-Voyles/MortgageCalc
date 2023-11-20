/**
 * Josh Voyles
 * CMSC 335 7381
 * 8 Nov 23
 * Week 4 Discussion "GUI"
 * This project displays a GUI that lets you calculate the house price you can afford based on monthly payment
 * Formula:
 * PMT =  PV * i * (1+i) ** n/(1+i) ** n−1
 * PMT = Payment
 * PV = mortgage amount
 * i = interest rate
 * n = term in months
 */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

public class MortgageCalculator extends JFrame {
    // variables used for calculation
    private double interestRate;
    private int mortgageTerm;
    private double monthlyPayment;
    private double hOAExpense;
    private double propertyTax;
    private double downPayment = 0;
    private double mortgageAmount;

    public MortgageCalculator() {

        // components of the GUI
        final JTextField monthlyPaymentTextField;
        final JComboBox<String> termComboBox;
        final JTextField interestRateTextField;
        final JTextField propertyTaxTextField;
        final JTextField downPaymentTextField;
        final JTextField hOATextField;
        final JTextField mortgageResult;
        final JLabel resultLabel;

        // basic gui structure
        setTitle("Mortgage Calculator");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // build content pane so we have a little more control
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridLayout(9, 2, 10, 10));
        contentPane.setBorder(new EmptyBorder(25, 25, 10, 25));

        // build out gui components
        JLabel monthlyPaymentLabel = new JLabel("Desired Monthly Payment:");
        monthlyPaymentTextField = new JTextField();

        JLabel termLabel = new JLabel("Term (years):");
        String[] termOptions = {"15", "30"};
        termComboBox = new JComboBox<>(termOptions);

        JLabel interestRateLabel = new JLabel("Interest Rate (%):");
        interestRateTextField = new JTextField();

        JLabel propertyTaxLabel = new JLabel("Property Tax Rate (Percent)*");
        propertyTaxTextField = new JTextField();


        JLabel downPaymentLabel = new JLabel("Down Payment (Dollars):");
        downPaymentTextField = new JTextField();

        JLabel hOAExpenseLabel = new JLabel("Monthly HOA Expense?");
        hOATextField = new JTextField();

        JLabel youCanAfford = new JLabel("Housing price you can afford: ");
        mortgageResult = new JTextField();

        JButton calculateButton = new JButton("Calculate");
        resultLabel = new JLabel();

        JLabel explainPropertyTax = new JLabel("*Assumes home value = purchase price.");

        // add all gui components to the frame
        contentPane.add(monthlyPaymentLabel);
        contentPane.add(monthlyPaymentTextField);
        contentPane.add(termLabel);
        contentPane.add(termComboBox);
        contentPane.add(interestRateLabel);
        contentPane.add(interestRateTextField);
        contentPane.add(propertyTaxLabel);
        contentPane.add(propertyTaxTextField);
        contentPane.add(hOAExpenseLabel);
        contentPane.add(hOATextField);
        contentPane.add(downPaymentLabel);
        contentPane.add(downPaymentTextField);
        contentPane.add(youCanAfford);
        contentPane.add(mortgageResult);
        contentPane.add(explainPropertyTax);
        contentPane.add(calculateButton);
        contentPane.add(resultLabel);
        setContentPane(contentPane);
        pack();

        // when user clicks the button, a lot of math happens, then house price is displayed
        calculateButton.addActionListener(e -> {
            // pull the variables we need from the GUI
            interestRate = parseInterestRate(interestRateTextField.getText().strip().replace("%", ""));
            mortgageTerm = parseTerm(termComboBox.getSelectedIndex());
            monthlyPayment = parseMonthlyPayment(monthlyPaymentTextField.getText().strip());
            hOAExpense = parseHOA(hOATextField.getText().strip());
            propertyTax = parsePropertyTax(propertyTaxTextField.getText());
            downPayment = parseDownPayment(downPaymentTextField.getText().strip());

            mortgageResult.setText(""); // clear the result block each time

            // finds house price based on payment first
            mortgageAmount = calculateMortgage(monthlyPayment, mortgageTerm, interestRate, downPayment, hOAExpense);

            // refactors house price after accounting for monthly property tax payments.
            mortgageAmount = adjustForPropertyTaxes(mortgageAmount, interestRate, propertyTax, mortgageTerm,
                    monthlyPayment, downPayment, hOAExpense);

            // sets result for display in GUI
            mortgageResult.setText("$%.0f".formatted(mortgageAmount));

        });
    }

    public static void main(String[] args) {
        MortgageCalculator mortgageCalculator = new MortgageCalculator();
        mortgageCalculator.setVisible(true);
    }

    // this section parses all the variables and displays a pop-up if there's an error

    private double parsePropertyTax(String propertyTaxRateString) {
        double propertyTaxRate;
        try {
            propertyTaxRate = Double.parseDouble(propertyTaxRateString);
            return propertyTaxRate;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Property Tax");
        }
        return 0;
    }

    private double parseInterestRate(String interestRateString) {
        double interestRate;

        try {
            interestRate = Double.parseDouble(interestRateString) / 100 / 12;
            return interestRate;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Interest Rate");
        }
        return 0;
    }

    private double parseHOA(String hOAAmmountString) {
        double hoa;
        try {
            hoa = Double.parseDouble(hOAAmmountString);
            return hoa;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error HOA Entry");
        }
        return 0;
    }

    private int parseTerm(int termIndex) {
        if (Objects.equals(termIndex, 0))
            return 180;  // term in months
        return 360;  // term in months
    }

    private double parseMonthlyPayment(String monthlyPaymentString) {
        double monthlyPayment;
        try {
            monthlyPayment = Double.parseDouble(monthlyPaymentString);
            return monthlyPayment;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Monthly Payment");
        }
        return 0;
    }

    private double parseDownPayment(String downPaymentString) {
        double downPayment;
        try {
            downPayment = Double.parseDouble(downPaymentString);
            return downPayment;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error Down Payment");
        }
        return 0;
    }

    // calculate how much house you can buy based on monthly payment
    private double calculateMortgage(double monthlyPayment, int mortgageTerm,
                                     double interestRate, double downPayment, double hoa) {
        monthlyPayment = monthlyPayment - hoa;

        double numerator = interestRate * Math.pow((1 + interestRate), mortgageTerm);
        double denominator = Math.pow((1 + interestRate), mortgageTerm) - 1;
        double propertyValue = (monthlyPayment * denominator) / numerator;
        propertyValue = propertyValue + downPayment;
        if (propertyValue > 0)
            return Math.round(propertyValue);
        return 0;
    }

    private double adjustForPropertyTaxes(double mortgageAmount, double interestRate, double propertyTax,
                                          int mortgageTerm, double monthlyPayment, double downPayment, double hoa) {

        double newPayment = taxLoop(mortgageAmount, monthlyPayment, propertyTax, interestRate,
                mortgageTerm, downPayment);

        // calculate total home we can afford factoring in new lower payment because of taxes
        mortgageAmount = calculateMortgage(newPayment, mortgageTerm, interestRate, downPayment, hoa);

        return mortgageAmount;

    }

    // most complex part
    // property taxes would increase our monthly payment, so we need to reduce our pre-tax payment then add
    // the property taxes back in to reach our original monthly goal
    private double taxLoop(double mortgageAmount, double monthlyPayment, double propertyTax,
                           double interestRate, int mortgageTerm, double downPayment) {
        double propertyTaxX100 = propertyTax / 100;
        double payment;
        double monthlyTaxPayment = (mortgageAmount + downPayment) * propertyTaxX100 / 12;
        double newPayment = monthlyPayment + monthlyTaxPayment;

        // trying to get payment plus taxes to match our monthly goal
        while (newPayment > monthlyPayment) {
            monthlyTaxPayment = (mortgageAmount + downPayment) * propertyTaxX100 / 12;
            mortgageAmount--;  // reduce house price $1 at a time to recalculate property taxes

            payment = calculateMonthlyPayment(mortgageAmount, interestRate, mortgageTerm);
            newPayment = payment + monthlyTaxPayment;
        }
        return newPayment - monthlyTaxPayment;  // return our reduced monthly payment

    }

    // refactors payment based on house price (standard formula)
    private double calculateMonthlyPayment(double mortgageAmount, double interestRate, int mortgageTerm) {
        return (mortgageAmount * interestRate * Math.pow((1 + interestRate), mortgageTerm)
                / ((Math.pow((1 + interestRate), mortgageTerm) - 1)));
    }
}