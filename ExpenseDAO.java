import java.sql.*;
import java.util.*;

public class ExpenseDAO {

    public void addExpense(Expense e) {
        String sql = "INSERT INTO expenses (date, category, amount, note) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getDate());
            ps.setString(2, e.getCategory());
            ps.setDouble(3, e.getAmount());
            ps.setString(4, e.getNote());
            ps.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateExpense(Expense expense) {
    try (Connection con = DBConnection.getConnection();
         PreparedStatement stmt = con.prepareStatement(
             "UPDATE expenses SET date=?, category=?, amount=?, note=? WHERE id=?")) {
        stmt.setString(1, expense.getDate());
        stmt.setString(2, expense.getCategory());
        stmt.setDouble(3, expense.getAmount());
        stmt.setString(4, expense.getNote());
        stmt.setInt(5, expense.getId());
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public List<Expense> getAllExpenses() {
        List<Expense> list = new ArrayList<>();
        String sql = "SELECT * FROM expenses ORDER BY date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Expense(
                    rs.getInt("id"),
                    rs.getString("date"),
                    rs.getString("category"),
                    rs.getDouble("amount"),
                    rs.getString("note")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getTotalExpenseByDate(String date) {
        String sql = "SELECT SUM(amount) FROM expenses WHERE date=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
