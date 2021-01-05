import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector; 

public class MainProgram {

	public static void main(String[] args) {
		//Given: clientes iniciales
		Vector<Client> clientList = new Vector<Client>();
		clientList.add(new Client(0));
		clientList.add(new Client(1));
		clientList.add(new Client(2));
		Scanner in = new Scanner(System.in);

		while (true) {
			int enteredID = -1;
			//Validacion de entrada de ID inicial.
			while (true){
				try {
					System.out.println("==============================================================");
					System.out.println("Bienvenido al sistema mock de banco. Ingrese su ID de cliente:");
					System.out.println("==============================================================");
					in = new Scanner(System.in);
					enteredID = in.nextInt(); 
					assertTrue(enteredID == 0 || enteredID == 1 || enteredID == 2);
					break;
				} 
				catch (java.util.InputMismatchException expectedException) {

					System.out.println("Por favor, ingrese un input valido.");
				}
				catch (AssertionError expectedException) {

					System.out.println("Por favor, ingrese un ID valido");
				}
			}


			//Loop de menu
			while (true) {
				//Input inicial
				int menuOption = -1;
				while (true){
					try {
						System.out.println("Por favor, elija su operacion.");
						System.out.println("0 Retiro ");
						System.out.println("1 Deposito");
						System.out.println("2 Historial");
						System.out.println("3 Salir");
						in = new Scanner(System.in);
						menuOption = in.nextInt(); 
						//Verificamos que el ID ingresado sea correcto
						assertTrue(menuOption == 0 || menuOption == 1 || menuOption == 2 || menuOption == 3);
						break;
					} 
					catch (java.util.InputMismatchException expectedException) {

						System.out.println("Por favor, ingrese un input valido.");
					}
					catch (AssertionError expectedException) {

						System.out.println("Por favor, ingrese una operacion valida");
					}
				}
				
				//Retiro
				if (menuOption == 0) {
					while (true){
						int amount;
						int currency;
						try {
							System.out.println("Por favor, elija su moneda (0 - CLP, 1 - USD).");
							in = new Scanner(System.in);
							currency = in.nextInt(); 
							//Verificamos que el ID ingresado sea correcto
							assertTrue(currency == 0 || currency == 1);
							System.out.println("Por favor, ingrese la cantidad a retirar");
							
							in = new Scanner(System.in);
							amount = in.nextInt(); 
							//Verificamos que el ID ingresado sea correcto
							assertTrue(amount > 0);
							
							Boolean success = clientList.get(enteredID).Withdraw(currency == 0, amount);
							if (success) {
								System.out.println("La operacion fue exitosa.");
								clientList.get(currency).PrintInfo();
							}
							else {
								System.out.println("La operacion no pudo concretarse.");
							}
							break;
						} 
						catch (java.util.InputMismatchException expectedException) {

							System.out.println("Por favor, ingrese un input valido.");
						}
						catch (AssertionError expectedException) {

							System.out.println("Por favor, ingrese una moneda y una cantidad de dinero valida.");
						}
					}
				}
				//Deposito
				if (menuOption == 1) {
					while (true){
						int amount;
						int currency;
						try {
							System.out.println("Por favor, elija su moneda (0 - CLP, 1 - USD).");
							in = new Scanner(System.in);
							currency = in.nextInt(); 
							//Verificamos que el ID ingresado sea correcto
							assertTrue(currency == 0 || currency == 1);
							System.out.println("Por favor, ingrese la cantidad a depositar");
							
							in = new Scanner(System.in);
							amount = in.nextInt(); 
							//Verificamos que el ID ingresado sea correcto
							assertTrue(amount > 0);
							
							Boolean success = clientList.get(enteredID).Deposit(currency == 0, amount);
							if (success) {
								System.out.println("La operacion fue exitosa.\n");
								clientList.get(currency).PrintInfo();
							}
							else {
								System.out.println("La operacion no pudo concretarse.");
							}
							break;
						} 
						catch (java.util.InputMismatchException expectedException) {

							System.out.println("Por favor, ingrese un input valido.");
						}
						catch (AssertionError expectedException) {

							System.out.println("Por favor, ingrese una moneda y una cantidad de dinero valida.");
						}
					}
				}
				//Historial
				if (menuOption == 2) {
					clientList.get(enteredID).PrintHistory();
				}
				//Cerrar Sesion
				if (menuOption == 3) {
					clientList.get(enteredID).ResetHistory();
					break;
				}
			}
		}

	}

	@Test
	public void CLPLowerLimit() {	  
		var client = new Client(0);
		Boolean expected = false;
		Boolean actual = client.Withdraw(true, 10);
		assertEquals(expected, actual);
	}
	
	@Test
	public void CLPUpperLimit() {	  
		var client = new Client(0);
		Boolean expected = false;
		Boolean actual = client.Withdraw(true, 999999);
		assertEquals(expected, actual);
	}
	
	@Test
	public void USDLowerLimit() {	  
		var client = new Client(0);
		Boolean expected = false;
		Boolean actual = client.Withdraw(false, 1);
		assertEquals(expected, actual);
	}
	
	@Test
	public void USDUpperrLimit() {	  
		var client = new Client(0);
		Boolean expected = false;
		Boolean actual = client.Withdraw(false, 999999);
		assertEquals(expected, actual);
	}
	
	@Test
	public void NegativeWithdraw() {	  
		var client = new Client(0);
		Boolean expected = false;
		Boolean actual = client.Withdraw(true, -5000);
		assertEquals(expected, actual);
	}
	
	@Test
	public void NegativeDeposit() {	  
		var client = new Client(0);
		Boolean expected = false;
		Boolean actual = client.Deposit(true, -5000);
		assertEquals(expected, actual);
	}
	
	@Test
	public void OperationLimit() {	  
		var client = new Client(0);
		Boolean expected = false;
		Boolean actual = client.Withdraw(true, 5000);
		actual = client.Withdraw(true, 5000);
		actual = client.Withdraw(true, 5000);
		actual = client.Withdraw(true, 5000);
		actual = client.Withdraw(true, 5000);
		actual = client.Withdraw(true, 5000);
		actual = client.Withdraw(true, 5000);
		assertEquals(expected, actual);
	}
	
	@Test
	public void NormalWithdrawalCLP() {	  
		var client = new Client(0);
		Boolean expected = true;
		Boolean actual = client.Withdraw(true, 5000);
		assertEquals(expected, actual);
	}
	
	@Test
	public void NormalWithdrawalUSD() {	  
		var client = new Client(0);
		client.moneyUSD = 3000;
		Boolean expected = true;
		Boolean actual = client.Withdraw(false, 50);
		assertEquals(expected, actual);
	}
	
	@Test
	public void CheckInputSanity() {	
		// Nota: Esto es una replica del error handling del ciclo main
		Boolean expected = false;
		Boolean actual = true;
		InputStream sysInBackup = System.in; // backup System.in to restore it later
		ByteArrayInputStream inputStream = new ByteArrayInputStream("String instead of Float".getBytes());
		System.setIn(inputStream);
		
		try {
			System.out.println("==============================================================");
			System.out.println("Bienvenido al sistema mock de banco. Ingrese su ID de cliente:");
			System.out.println("==============================================================");
			Scanner in = new Scanner(inputStream);
			int enteredID = in.nextInt(); 
			actual = true;
		} 
		catch (java.util.InputMismatchException expectedException) {

			System.out.println("Por favor, ingrese un input valido.");
			actual = false;
		}
		catch (AssertionError expectedException) {

			System.out.println("Por favor, ingrese un ID valido");
			actual = false;
		}
		System.setIn(sysInBackup);
		assertEquals(expected, actual);
	}
}
