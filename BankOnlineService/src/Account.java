import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;

public class Account {
    File accounts=new File("account.dat");///////////////
    private String password;
    private String ownerId;
    private String ownerPassword;
    private int money;
    private String alias=null;
    private AccountKind  kind;
    private String date;
    public Account(String password,String ownerPassword,String ownerId,int amount,AccountKind s){
        this.money=amount;
        this.password=password;
        this.ownerId=ownerId;
        this.ownerPassword=ownerPassword;
        this.kind=s;
    }
    public Account(String password,String ownerPassword,String ownerId,AccountKind kind){
        this.password=password;
        this.ownerId=ownerId;
        this.ownerPassword=ownerPassword;
        this.kind=kind;
        this.money=0;
    }
    public String getPassword() {
        return password;
    }
    public int getMoney() {
        return money;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public String getOwnerPassword(){
        return ownerPassword;
    }
    public void setAlias(String alias){
        this.alias=alias;
    }
    public AccountKind getKind() {
        return kind;
    }
    public String getKindName(){
        return kind.name();
    }
    public String getAlias() {
        return alias;
    }
    public void setDate(String date) {
        LocalDate currentDate = LocalDate.now();
        this.date = "" + currentDate.getDayOfMonth() + " /" + currentDate.getMonthValue() +
                " /" + currentDate.getYear();
    }
    public void setMoney(int money) {
        this.money = money;
    }
    public String getDate() {
        return date;
    }
    public boolean paybill(int amountT, String paymentNumT, String billNumT, String passwordT){
        if (money >= amountT) {
            this.money-=amountT;
            new Transaction(passwordT, TransactionKind.payBill, amountT, billNumT).saveTransaction();
            return true;
        } else {
            return false;
        }
    }
    public boolean transfer(String passwordT, String destinationPassT, int amountT,Account destination){
        if(this.money<amountT)
            return false;
        else {
            this.money-=amountT;
            destination.changeMoney(destination.getMoney()+amountT);
            new Transaction(passwordT,TransactionKind.transfer,amountT).saveTransaction();
            return true;
        }
    }
    public void getLoan(String dateT, int amountT, int repaymentT)  {
        this.money+=amountT;
        new Transaction(password,dateT,TransactionKind.getLoan,amountT).saveTransaction();
    }
    public boolean loanPay(String dateT, int amountT, int repaymentT,String passwordT) {
        LocalDate currentDate = LocalDate.now();
        String currentD=String.valueOf(currentDate.getDayOfMonth()+currentDate.getMonthValue()
                +currentDate.getYear());
        String d = String.valueOf(currentDate.getDayOfMonth());
        String date1 = dateT.substring(0, 2);
        String date2 = dateT.substring(0, 1);
        if(!currentD.equals(dateT)) {
            while (repaymentT != 0) {
                if (Date1(dateT) && d.equals(date1)) {
                    if (money >= money * 18 / 100) {
                        money -=money * 18 / 100;
                        new Transaction(passwordT,dateT,TransactionKind.loanPayment,amountT- 18 / 100).saveTransaction();
                        return true;
                    } else {
                        return false;
                    }
                } else if (Date2(dateT) && d.equals(date2)) {
                    if (money >= money * 18 / 100) {
                        money -=money * 18 / 100;
                        new Transaction(passwordT,dateT,TransactionKind.loanPayment,amountT- 18 / 100).saveTransaction();
                        return true;
                    } else {
                        return false;
                    }
                }
                repaymentT--;
            }
        }
        return true;
    }
    public  boolean saveNewAccount(){
        try {
            RandomAccessFile accountFile = Server.getAccountFile();
            while (accountFile.read()>0);
            accountFile.writeBytes(ownerId+"\n");
            accountFile.writeBytes(this.getOwnerPassword()+"\n");
            accountFile.writeBytes(password+"\n");
            accountFile.writeBytes(money+"\n");
            accountFile.writeBytes(this.getKindName()+"\n");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public boolean saveAlias(String alias){
        try {
            RandomAccessFile aliases = Server.getAlias();
            while (aliases.read()>0);
            aliases.writeBytes(password);
            aliases.writeBytes(alias);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public void changeAlias(String alias){
        try{
            RandomAccessFile aliases=Server.getAlias();
            RandomAccessFile s=new RandomAccessFile("src/alias.dat","rw");
            while (aliases.read()>0) {
                aliases.seek(aliases.getFilePointer()-1);
                String password=aliases.readLine();
                if(password.equals(this.getPassword())){
                    aliases.readLine();
                }else {
                    s.writeBytes(password);
                    s.writeBytes(aliases.readLine());
                }
            }
            s.writeBytes(this.getPassword());
            s.writeBytes(alias);
            aliases=s;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public  boolean changeMoney(int n){
        try {
            RandomAccessFile accountFile = Server.getAccountFile();
            RandomAccessFile r=new RandomAccessFile("src/account.dat","rw");
            while (accountFile.read()>0){
                accountFile.seek(accountFile.getFilePointer() - 1);
                String ID=accountFile.readLine();
                if(ID.equals(this.getOwnerId())){
                    accountFile.readLine();
                    accountFile.readLine();
                    accountFile.readLine();
                    accountFile.readLine();
                }else {
                    r.writeBytes(ID);
                    r.writeBytes(accountFile.readLine());
                    r.writeBytes(accountFile.readLine());
                    r.writeBytes(accountFile.readLine());
                    r.writeBytes(accountFile.readLine());
                }
            }
            r.writeBytes(this.getOwnerId()+"\n");
            r.writeBytes(this.getOwnerPassword()+"\n");
            r.writeBytes(this.getPassword()+"\n");
            r.writeBytes(n+"\n");
            r.writeBytes(this.getKind().name()+"\n");
            accountFile=r;
        } catch (Exception exception) {
            return false;
        }
        return true;
    }
    public boolean Date1(String dateT){
        String date1 = dateT.substring(0, 2);
        for (int i = 0; i < date1.length(); i++) {
            if (date1.charAt(i)< '0' || date1.charAt(i) >'9') {
                return false;
            }
        }
        return true;
    }
    public boolean Date2(String dateT){
        String date2 = dateT.substring(0, 1);
        for (int i = 0; i < date2.length(); i++) {
            if (date2.charAt(i)< '0' || date2.charAt(i) >'9') {
                return false;
            }
        }
        return true;
    }
    public static Account findAccount(String password){
        Account account=null;
        try {
            RandomAccessFile accountFile = Server.getAccountFile();
            while (accountFile.read() > 0) {
                accountFile.seek(accountFile.getFilePointer() - 1);
                String ownerID=accountFile.readLine();
                String ownerPassword=accountFile.readLine();
                String accountPass=accountFile.readLine();
                if(accountPass.equals(password)){
                    String money = accountFile.readLine();
                    String kindStr = accountFile.readLine();
                    AccountKind accountKind = null;
                    switch (kindStr) {
                        case "CheckingAccount":
                            accountKind = AccountKind.CheckingAccount;
                            break;
                        case "SavingAccount":
                            accountKind = AccountKind.SavingAccount;
                    }
                    account=new Account(  accountPass, ownerPassword,ownerID,Integer.valueOf(money), accountKind);
                }else {
                    accountFile.readLine();
                    accountFile.readLine();
                }
            }
        }catch (Exception e){
        }
        return account;
    }
    public boolean deletAccount() {
        try {
            RandomAccessFile accountFile = Server.getAccountFile();
            RandomAccessFile r = new RandomAccessFile("src/account.dat", "rw");
            while (accountFile.read() > 0) {
                accountFile.seek(accountFile.getFilePointer() - 1);
                String ID = accountFile.readLine();
                if (ID.equals(this.getOwnerId())) {
                    accountFile.readLine();
                    accountFile.readLine();
                    accountFile.readLine();
                    accountFile.readLine();
                } else {
                    r.writeBytes(ID);
                    r.writeBytes(accountFile.readLine());
                    r.writeBytes(accountFile.readLine());
                    r.writeBytes(accountFile.readLine());
                    r.writeBytes(accountFile.readLine());
                }
            }
        } catch (Exception exception) {
            return false;
        }
        return true;
    }
}
