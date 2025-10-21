import java.util.*;

// Represents a single stock
class Stock {
    String symbol;
    String name;
    double price;

    Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    void updatePrice() {
        // Random price change simulation (-5% to +5%)
        double change = (Math.random() - 0.5) * 0.1;
        price += price * change;
        if (price < 1) price = 1; // minimum price safeguard
    }
}

// Represents a stock transaction
class Transaction {
    String type; // BUY or SELL
    Stock stock;
    int quantity;
    double totalAmount;
    Date date;

    Transaction(String type, Stock stock, int quantity, double totalAmount) {
        this.type = type;
        this.stock = stock;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.date = new Date();
    }

    public String toString() {
        return "[" + date + "] " + type + " " + quantity + "x " + stock.symbol +
               " @ ₹" + String.format("%.2f", stock.price) + " (Total ₹" + String.format("%.2f", totalAmount) + ")";
    }
}

// Represents user's portfolio
class Portfolio {
    Map<String, Integer> holdings = new HashMap<>();
    List<Transaction> history = new ArrayList<>();
    double balance = 50000; // starting virtual balance

    void buyStock(Stock stock, int quantity) {
        double cost = stock.price * quantity;
        if (cost > balance) {
            System.out.println("❌ Not enough balance to buy " + stock.symbol);
            return;
        }
        balance -= cost;
        holdings.put(stock.symbol, holdings.getOrDefault(stock.symbol, 0) + quantity);
        history.add(new Transaction("BUY", stock, quantity, cost));
        System.out.println("✅ Bought " + quantity + " shares of " + stock.symbol);
    }

    void sellStock(Stock stock, int quantity) {
        int owned = holdings.getOrDefault(stock.symbol, 0);
        if (quantity > owned) {
            System.out.println("❌ You don't have enough shares to sell " + stock.symbol);
            return;
        }
        double income = stock.price * quantity;
        balance += income;
        holdings.put(stock.symbol, owned - quantity);
        history.add(new Transaction("SELL", stock, quantity, income));
        System.out.println("✅ Sold " + quantity + " shares of " + stock.symbol);
    }

    void showPortfolio(List<Stock> market) {
        System.out.println("\n===== 🧾 YOUR PORTFOLIO =====");
        double totalValue = balance;

        for (String symbol : holdings.keySet()) {
            int qty = holdings.get(symbol);
            if (qty > 0) {
                Stock s = market.stream().filter(st -> st.symbol.equals(symbol)).findFirst().get();
                double val = qty * s.price;
                totalValue += val;
                System.out.println(symbol + " - " + qty + " shares @ ₹" + String.format("%.2f", s.price) + " = ₹" + String.format("%.2f", val));
            }
        }

        System.out.println("💰 Balance: ₹" + String.format("%.2f", balance));
        System.out.println("📈 Total Portfolio Value: ₹" + String.format("%.2f", totalValue));
    }

    void showHistory() {
        System.out.println("\n===== 📜 TRANSACTION HISTORY =====");
        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (Transaction t : history) {
                System.out.println(t);
            }
        }
    }
}

public class StockTradingPlatform {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        List<Stock> market = new ArrayList<>();
        Portfolio portfolio = new Portfolio();

        // Sample stock data
        market.add(new Stock("TCS", "Tata Consultancy Services", 3800));
        market.add(new Stock("INFY", "Infosys", 1600));
        market.add(new Stock("RELI", "Reliance Industries", 2900));
        market.add(new Stock("HDFC", "HDFC Bank", 1650));
        market.add(new Stock("ITC", "ITC Ltd.", 480));

        while (true) {
            System.out.println("\n===== 📊 STOCK TRADING PLATFORM =====");
            System.out.println("1. View Market Prices");
            System.out.println("2. Buy Stock");
            System.out.println("3. Sell Stock");
            System.out.println("4. View Portfolio");
            System.out.println("5. View Transaction History");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            for (Stock s : market) s.updatePrice(); // update all prices randomly

            switch (choice) {
                case 1:
                    System.out.println("\n===== 💹 MARKET DATA =====");
                    for (Stock s : market) {
                        System.out.println(s.symbol + " (" + s.name + ") : ₹" + String.format("%.2f", s.price));
                    }
                    break;
                case 2:
                    System.out.print("Enter stock symbol to BUY: ");
                    String symBuy = sc.next().toUpperCase();
                    Stock stockBuy = findStock(market, symBuy);
                    if (stockBuy == null) {
                        System.out.println("❌ Invalid symbol!");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int qtyBuy = sc.nextInt();
                    portfolio.buyStock(stockBuy, qtyBuy);
                    break;
                case 3:
                    System.out.print("Enter stock symbol to SELL: ");
                    String symSell = sc.next().toUpperCase();
                    Stock stockSell = findStock(market, symSell);
                    if (stockSell == null) {
                        System.out.println("❌ Invalid symbol!");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int qtySell = sc.nextInt();
                    portfolio.sellStock(stockSell, qtySell);
                    break;
                case 4:
                    portfolio.showPortfolio(market);
                    break;
                case 5:
                    portfolio.showHistory();
                    break;
                case 6:
                    System.out.println("👋 Exiting platform. Goodbye!");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("❌ Invalid option. Try again!");
            }
        }
    }

    static Stock findStock(List<Stock> market, String symbol) {
        for (Stock s : market) {
            if (s.symbol.equalsIgnoreCase(symbol)) return s;
        }
        return null;
    }
}
