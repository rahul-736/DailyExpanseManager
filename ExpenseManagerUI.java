
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
//import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.jdatepicker.impl.*;  // JDatePicker

public class ExpenseManagerUI extends JFrame {

    private ExpenseDAO dao = new ExpenseDAO();
    private JTable table;
    private DefaultTableModel model;
    private JTextField categoryField, amountField, noteField;
    private JDatePickerImpl datePicker;

    public ExpenseManagerUI() {
        setTitle("Daily Money Expense Manager");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Date Picker Configuration
        UtilDateModel modelDate = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(modelDate, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Top Panel - Form
        JPanel topPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        categoryField = new JTextField();
        amountField = new JTextField();
        noteField = new JTextField();

        topPanel.add(new JLabel("Date:"));
        topPanel.add(new JLabel("Category:"));
        topPanel.add(new JLabel("Amount:"));
        topPanel.add(new JLabel("Note:"));
        topPanel.add(new JLabel());

        topPanel.add(datePicker);
        topPanel.add(categoryField);
        topPanel.add(amountField);
        topPanel.add(noteField);

        JButton addBtn = new JButton("Add Expense");
        topPanel.add(addBtn);

        
        

        // Center Panel - Table
        model = new DefaultTableModel(new String[]{"ID", "Date", "Category", "Amount", "Note"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Bottom Panel - Buttons
        JPanel bottomPanel = new JPanel();
        JButton deleteBtn = new JButton("Delete Selected");
        JButton totalBtn = new JButton("Show Total on Date");
        JButton editBtn = new JButton("Edit Selected");
        
        JTextField totalDateField = new JTextField(10);
        JLabel totalLabel = new JLabel("Total: ₹0.00");

        bottomPanel.add(deleteBtn);
        bottomPanel.add(new JLabel("Date:"));
        bottomPanel.add(totalDateField);
        bottomPanel.add(totalBtn);
        bottomPanel.add(totalLabel);
        bottomPanel.add(editBtn);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Event: Add Expense
        addBtn.addActionListener(e -> {
            if (datePicker.getModel().getValue() == null) {
                JOptionPane.showMessageDialog(this, "Please select a date.");
                return;
            }

            String category = categoryField.getText().trim();
            String amountText = amountField.getText().trim();
            String note = noteField.getText().trim();

            if (category.isEmpty() || amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in category and amount.");
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);
                Date selectedDate = (Date) datePicker.getModel().getValue();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

                dao.addExpense(new Expense(0, date, category, amount, note));
                JOptionPane.showMessageDialog(this, "Expense added!");
                clearFields();
                loadExpenses();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            }
        });

        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                    String date = dateField.getText().trim();
                    String category = categoryField.getText().trim();
                    String amountText = amountField.getText().trim();
                    String note = noteField.getText().trim();

                    if (date.isEmpty() || category.isEmpty() || amountText.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please fill in date, category, and amount.");
                        return;
                    }

                    double amount = Double.parseDouble(amountText);
                    dao.updateExpense(new Expense(id, date, category, amount, note));
                    JOptionPane.showMessageDialog(this, "Expense updated!");
                    clearFields();
                    loadExpenses();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid amount.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
            }
        });

        // Event: Delete selected row
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = Integer.parseInt(model.getValueAt(row, 0).toString());
                dao.deleteExpense(id);
                loadExpenses();
                JOptionPane.showMessageDialog(this, "Expense deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            }
        });

        // Event: Show total by date
        totalBtn.addActionListener(e -> {
            String date = totalDateField.getText().trim();
            if (!date.isEmpty()) {
                double total = dao.getTotalExpenseByDate(date);
                totalLabel.setText("Total: ₹" + total);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a date.");
            }
        });

        loadExpenses();
        setVisible(true);
    }

    private void loadExpenses() {
        model.setRowCount(0);
        List<Expense> list = dao.getAllExpenses();
        for (Expense e : list) {
            model.addRow(new Object[]{
                e.getId(), e.getDate(), e.getCategory(), e.getAmount(), e.getNote()
            });
        }
    }

    private void clearFields() {
        categoryField.setText("");
        amountField.setText("");
        noteField.setText("");
        datePicker.getModel().setValue(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseManagerUI::new);
    }

    // Inner class to format date
    class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private final String datePattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws java.text.ParseException {
            if (value != null) {
                Date date = (Date) value;
                return dateFormatter.format(date);
            }
            return "";
        }
    }
}
