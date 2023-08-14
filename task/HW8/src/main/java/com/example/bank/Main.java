package com.example.bank;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Main {

    public static String  spring ;
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SpringApplication.run(Main.class, args);

        try {
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            try {
                addExchangeRate();
                while (true){
                    System.out.println("1: add customer ");
                    System.out.println("2: change between bank account");
                    System.out.println("3: to replenish");
                    System.out.println("4: exchange money");
                    System.out.println("5: balance");
                    System.out.println("6: view customers ");
                    System.out.println("7: view bank account ");
                    System.out.println("8: view exchange rate ");
                    System.out.println("9: view transactions ");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s){
                        case "1":
                            addCustomerAndAccount(sc);
                            break;
                        case "2":
                            betweenBankAccount(sc);
                            viewOperationsSpring();
                            break;
                        case "3":
                            toReplenish(sc);
                            viewOperationsSpring();
                            System.out.println("eg");
                            break;
                        case "4":
                            exchangeMoney(sc);
                            viewOperationsSpring();
                            break;
                        case "5":
                            balance(sc);
                            break;
                        case "6":
                            viewCustomer();
                            break;
                        case "7":
                            viewBankAccount();
                            break;
                        case "8":
                            viewExchangeRate();
                            break;
                        case "9":
                            viewOperation();
                            break;
                        default:
                            return;
                    }
                }
            }finally {
                sc.close();
                em.close();
                emf.close();
            }
        }catch (Exception ex){
          ex.printStackTrace();
          return;
        }

    }

    private static void addCustomerAndAccount(Scanner sc){
        System.out.print("Enter name customer: ");
        String name = sc.nextLine();
        System.out.print("Enter age customer: ");
        String sAge = sc.nextLine();
        int age = Integer.parseInt(sAge);
        System.out.print("Enter gender customer: ");
        String gender = sc.nextLine();
        em.getTransaction().begin();

        try {
            Customer c = new Customer(name,age,gender);
            em.persist(c);
            em.getTransaction().commit();

        }catch (Exception ex){
            em.getTransaction().rollback();
        }

        System.out.print("Enter amount UAH: ");
        double amountUAH = Double.parseDouble(sc.nextLine());
        System.out.print("Enter amount USD: ");
        double amountUSD = Double.parseDouble(sc.nextLine());
        System.out.print("Enter amount EUR: ");
        double amountEUR = Double.parseDouble(sc.nextLine());
        em.getTransaction().begin();

        try {
            BankAccount b = new BankAccount(name,amountUAH,amountUSD,amountEUR);
            em.persist(b);
            em.getTransaction().commit();
        }catch (Exception ex){
            em.getTransaction().rollback();
        }

    }

    private static void viewCustomer(){
        Query query = em.createQuery("SELECT c FROM Customer c" ,Customer.class);
        List<Customer> list = (List<Customer>) query.getResultList();

        for (Customer c : list){
            System.out.println(c);
        }
    }

    private static void viewExchangeRate(){

        Query query = em.createQuery("SELECT c FROM ExchangeRate c", ExchangeRate.class);
        List<ExchangeRate> list = (List<ExchangeRate>) query.getResultList();

        for (ExchangeRate c: list){
            System.out.println(c);
        }
    }

    private static void addExchangeRate(){
        em.getTransaction().begin();
        try {
            ExchangeRate uan = new ExchangeRate("UAN",1);
            ExchangeRate usd = new ExchangeRate("USD",36.74);
            ExchangeRate eur = new ExchangeRate("EUR",40.22);

            em.persist(uan);
            em.persist(usd);
            em.persist(eur);
            em.getTransaction().commit();
        }catch (Exception ex){
            em.getTransaction().rollback();
        }
    }

    private static void viewBankAccount(){

        Query query = em.createQuery("SELECT c FROM BankAccount c", BankAccount.class);
        List<BankAccount> list = (List<BankAccount>) query.getResultList();

        for (BankAccount n: list){
            System.out.println(n);
        }
    }

    private static void betweenBankAccount(Scanner sc){
        System.out.print("Enter name giver: ");
        String nameFrom = sc.nextLine();
        System.out.print("Enter name receiver: ");
        String nameReceiver = sc.nextLine();
        System.out.print("Enter amount: ");
        Double amount = Double.parseDouble(sc.nextLine());
        System.out.print("Enter currency: ");
        String currency = sc.nextLine();

        BankAccount from = null;
        BankAccount receiver = null;
        try {
            Query query1 = em.createQuery("SELECT x FROM BankAccount x WHERE x.owner =: nameFrom", BankAccount.class);
            query1.setParameter("nameFrom", nameFrom);
            from = (BankAccount) query1.getSingleResult();
            Query query2 = em.createQuery("SELECT x FROM BankAccount x WHERE x.owner =: nameReceiver", BankAccount.class);
            query2.setParameter("nameReceiver",nameReceiver);
            receiver = (BankAccount) query2.getSingleResult();
        }catch (NoResultException ex){
            System.out.println("Bank Account not found!");
            return;
        }catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        }

        em.getTransaction().begin();

        try {
            Field field = BankAccount.class.getDeclaredField(currency);
            field.setAccessible(true);
            double a = (Double) field.get(from);
            field.set(from,a-amount);
            double b = (Double) field.get(receiver);
            field.set(receiver,b+amount);

            Operation c = new Operation(from.getOwner(),receiver.getOwner(),amount,currency);
            em.persist(c);
            em.getTransaction().commit();

        }catch (Exception ex){
            em.getTransaction().rollback();
        }

    }

    private static void toReplenish(Scanner sc){
        System.out.print("Enter owner: ");
        String owner = sc.nextLine();
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());
        System.out.print("Enter currency: ");
        String currency = sc.nextLine();
        BankAccount b = null;
        try {
            Query query = em.createQuery("SELECT x FROM BankAccount x WHERE x.owner =: owner", BankAccount.class);
            query.setParameter("owner",owner);
            b = (BankAccount) query.getSingleResult();
        }catch (NoResultException ex){
            System.out.println("Bank Account not found!");
            return;
        }catch (NonUniqueResultException ex) {
            System.out.println("Non unique result!");
            return;
        }

        em.getTransaction().begin();

        try {
            Field field = BankAccount.class.getDeclaredField(currency);
            field.setAccessible(true);
            double a =(Double) field.get(b);
            field.set(b,a+amount);
            Operation c = new Operation(owner,amount,currency);
            em.persist(c);
            em.getTransaction().commit();
        }catch (Exception ex){
            em.getTransaction().rollback();
        }

    }
    private static void viewOperation(){
        Query query = em.createQuery("SELECT  c FROM Operation c", Operation.class);
        List<Operation> list = (List<Operation>) query.getResultList();

        for (Operation a: list){
            System.out.println(a);
        }
    }

    private static void exchangeMoney(Scanner sc){
        System.out.print("Enter owner: ");
        String owner = sc.nextLine();
        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine());
        System.out.print("Enter currency1: ");
        String currency1 = sc.nextLine();
        System.out.print("Enter currency2: ");
        String currency2 = sc.nextLine();
        BankAccount ac = null;
        ExchangeRate rate = null;
        ExchangeRate rate2 = null;

        try {
            Query queryA = em.createQuery("select x FROM BankAccount x WHERE x.owner =: owner", BankAccount.class);
            queryA.setParameter("owner",owner);
            ac = (BankAccount) queryA.getSingleResult();

            Query queryR1 = em.createQuery("SELECT x FROM ExchangeRate x WHERE x.name =: currency1", ExchangeRate.class);
            queryR1.setParameter("currency1",currency1);
            rate = (ExchangeRate) queryR1.getSingleResult();
            Query queryR2 = em.createQuery("SELECT x from ExchangeRate x WHERE x.name =: currency2", ExchangeRate.class);
            queryR2.setParameter("currency2",currency2);
            rate2 = (ExchangeRate) queryR2.getSingleResult();
        }catch (NoResultException ex){
            System.out.println("Not found!");
            return;
        }catch (NonUniqueResultException ex){
            System.out.println("Non unique result!");
            return;
        }

        em.getTransaction().begin();

        try {
            Field field1 = BankAccount.class.getDeclaredField(currency1);
            field1.setAccessible(true);
            double number = (Double) field1.get(ac);
            field1.set(ac,number-amount);
            double uan = amount * rate.getExchangeRate();

            Field field2 = BankAccount.class.getDeclaredField(currency2);
            field2.setAccessible(true);
            double number2 = (Double) field2.get(ac);
            double result = uan / rate2.getExchangeRate();
            field2.set(ac,number2 + result);

            Operation c = new Operation(owner,amount,currency1,result,currency2);
            em.persist(c);
            em.getTransaction().commit();
        }catch (Exception ex){
            em.getTransaction().rollback();
        }
    }

    private static void balance(Scanner sc)  {
        System.out.print("Enter owner: ");
        String owner = sc.nextLine();

        BankAccount ac = null;
        try {
            Query query = em.createQuery("SELECT x FROM BankAccount x WHERE x.owner =: owner", BankAccount.class);
            query.setParameter("owner",owner);
            ac = (BankAccount) query.getSingleResult();

        }catch (NoResultException ex){
            System.out.println("Not found!");
            return;
        }catch (NonUniqueResultException ex){
            System.out.println("Non unique result!");
            return;
        }


        try {
            Field[] fieldsAc = BankAccount.class.getDeclaredFields();
            int a = fieldsAc.length;
            fieldsAc = Arrays.copyOfRange(fieldsAc,2,a);
            double result = 0;
            for (Field d:fieldsAc){
                double b = 0;
                ExchangeRate ex = null;
                String name = d.getName();
                Query query = em.createQuery("SELECT x FROM ExchangeRate x WHERE x.name =: name",ExchangeRate.class);
                query.setParameter("name",name);
                ex = (ExchangeRate) query.getSingleResult();
                double rate = ex.getExchangeRate();
                d.setAccessible(true);
                double sd= (Double) d.get(ac);
                b = sd * rate;
                result +=b;
            }
            System.out.println("Balance- "+result);
        }catch (IllegalAccessException ex){
            System.out.println("ERROR");
        }
    }

    public static void viewOperationsSpring(){
        spring = "";
        Query query = em.createQuery("SELECT c FROM Operation c", Operation.class);
        List<Operation> list = query.getResultList();
        for (Operation a: list){
            spring = spring + a.toString();
        }
    }

    public static int amountOperations(){
        int a = 0;

        Query query = em.createQuery("SELECT c FROM Operation c", Operation.class);
        List<Operation> list = query.getResultList();
        a = list.size();
        return a;
    }
    public static String totalBalance(){
        double uan = 0;
        double usd = 0;
        double eur = 0;

        Query query = em.createQuery("SELECT c FROM BankAccount c", BankAccount.class);
        List<BankAccount> list = query.getResultList();
        for(BankAccount a : list){
            uan = uan + a.getUAN();
            usd = usd + a.getUSD();
            eur = eur + a.getEUR();
        }
        return "UAN- "+ uan +" USD- "+ usd +" EUR- " + eur;
    }

}