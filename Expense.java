public class Expense {
    private int id;
    private String date;
    private String category;
    private double amount;
    private String note;

    public Expense(int id, String date, String category, double amount, String note) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
}
