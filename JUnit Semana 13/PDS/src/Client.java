import java.util.Vector;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;    

public class Client {
	//Given restrictions
	float maxCLP = 200000;
	float maxUSD = 100;
	float minCLP = 2000;
	float minUSD = 10;

	//Initial state
	float moneyCLP = 1000000;
	float moneyUSD = 0;
	int id = 0;

	Vector<String> history;
	float transactionNumber = 0;

	public String CurrentTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now);
	} 
	
	public Client(int newId) {
		id = newId;
		history = new Vector<String>();
	}

	//Checks if a transaction is valid for a client.
	public Boolean AllowTransaction(TransactionType type, Boolean isCLP, float amount) {
		if (transactionNumber >= 4) return false;
		if (type == TransactionType.WITHDRAW) {
			if (isCLP) {
				if (amount < minCLP || amount > maxCLP || moneyCLP < amount) return false;
			}
			else {
				if (amount < minUSD || amount > maxUSD || moneyUSD < amount) return false;
			}
		}
		if (type == TransactionType.DEPOSIT) {
			if (isCLP) {
				if (amount < minCLP) return false;
			}
			else {
				if (amount < minUSD) return false;
			}
		}
		return true;	
	}

	public Boolean Withdraw(Boolean isCLP, float amount) {
		Boolean ret = AllowTransaction(TransactionType.WITHDRAW, isCLP,amount);
		//Transaction failed
		if (!ret) return false;
		//If its allowed, we perform it
		if (isCLP) {
			moneyCLP -= amount;
		}
		else {
			moneyUSD -= amount;
		}
		
		history.add(  (CurrentTime() + " Retiro de " + amount +(isCLP? "CLP" : "USD")) );
		transactionNumber +=1;
		return true;
	}

	public Boolean Deposit(Boolean isCLP, float amount) {
		Boolean ret = AllowTransaction(TransactionType.DEPOSIT, isCLP,amount);
		//Transaction failed
		if (!ret) return false;
		//If its allowed, we perform it
		if (isCLP) {
			moneyCLP += amount;
		}
		else {
			moneyUSD += amount;
		}
		history.add(  (CurrentTime() + " Deposito de " + amount +(isCLP? "CLP" : "USD")) );
		transactionNumber +=1;
		return true;
	}
	
	public void PrintInfo() {
		System.out.println("Saldo en CLP:" + moneyCLP);
		System.out.println("Saldo en USD:" + moneyUSD);
		System.out.println("Transacciones Realizadas en eta sesion:" + transactionNumber);
		System.out.println("");
	}
	
	public void PrintHistory() {
		history.forEach((n) -> System.out.println(n)); 
	}
	
	public void ResetHistory() {
		transactionNumber = 0;
		history = new Vector<String>();
	}
		
	
	public enum TransactionType{
		WITHDRAW,
		DEPOSIT
	}
}
