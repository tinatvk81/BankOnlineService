import java.io.File;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;

public class Transaction{
    protected static RandomAccessFile transactions;
    File transactionFile;
    private String date;
    private int amount;
    private String accountPassword;
    private TransactionKind kind;
    private String billID;
    private String  destinationAccount;
    public Transaction(String accountPassword,TransactionKind kind,int amount, String billID){        //for pay bill
        this.accountPassword=accountPassword;
        this.kind=kind;
        this.amount=amount;
        this.billID=billID;
        LocalDate currentDate=LocalDate.now();
        this.date=""+currentDate.getYear()+"_"+currentDate.getMonthValue()+"_"+
                currentDate.getDayOfMonth();
    }
    public Transaction(String accountPassword,TransactionKind kind,String targetAccount,int amount){  // for transfer
        this.accountPassword=accountPassword;
        this.kind=kind;
        this.amount=amount;
        this.destinationAccount=targetAccount;
        LocalDate currentDate=LocalDate.now();
        this.date=""+currentDate.getYear()+"_"+currentDate.getMonthValue()+"_"+
                currentDate.getDayOfMonth();
    }
    public  Transaction(String accountPassword,TransactionKind kind,int amount){//for  loan
        this.accountPassword=accountPassword;
        this.kind=kind;
        this.amount=amount;
        LocalDate currentDate=LocalDate.now();
        this.date=""+currentDate.getYear()+"_"+currentDate.getMonthValue()+"_"+
                currentDate.getDayOfMonth();
    }
    public Transaction(String accountPassword,String date,TransactionKind kind,int amount){//to load saved loan
        this.accountPassword=accountPassword;
        this.kind=kind;
        this.amount=amount;
        this.date=date;
    }
    public Transaction(String accountPassword,TransactionKind kind,int amount, String billID,String date){        //for load saved pay bill
        this.accountPassword=accountPassword;
        this.kind=kind;
        this.amount=amount;
        this.billID=billID;
        this.date=date;
    }
    public Transaction(String accountPassword,String date,TransactionKind kind,String targetAccount,int amount){  // for load saved transfer
        this.accountPassword=accountPassword;
        this.kind=kind;
        this.amount=amount;
        this.destinationAccount=targetAccount;
        this.date=date;
    }
    public int getAmount() {
        return amount;
    }
    public String getDestinationAccount() {
        return destinationAccount;
    }
    public String getAccountPassword() {
        return accountPassword;
    }
    public TransactionKind getKind() {
        return kind;
    }
    public String getDate() {
        return date;
    }
    public String getBillID() {
        return billID;
    }
    public String getTargetAccount() {
        return destinationAccount;
    }
    public synchronized boolean saveTransaction(){
        try {
            transactionFile=new File("src/transactions.dat");
            transactions=new RandomAccessFile(transactionFile,"rw");
            while (transactions.read()>0);
            transactions.writeBytes(this.accountPassword+"\n");
            transactions.writeBytes(this.kind.name()+"\n");
            transactions.writeBytes(this.amount+"\n");
            transactions.writeBytes(this.date+"\n");
            switch (this.kind){
                case payBill:transactions.writeBytes(this.billID+"\n");
                    break;
                case transfer:transactions.writeBytes(this.destinationAccount+"\n");
                    break;
            }
            transactions.close();
        }catch (Exception e){
            return false;
        }

        return true;
    }
    public synchronized  static ArrayList loadLoanPayTransaction(Account account) throws Exception {
        File transactionFile=new File("src/transactions.dat");
        transactions=new RandomAccessFile(transactionFile,"rw");
        ArrayList<Transaction> transactionList=new ArrayList<Transaction>();
        while (transactions.read()!= -1) {
            transactions.seek(transactions.getFilePointer()-1);
            String password=transactions.readLine();
            if(password.equals(account.getPassword())){
                String kind=transactions.readLine();
                if(kind.equals("loanPayment")){
                    String amount=transactions.readLine();
                    String date=transactions.readLine();
                    transactionList.add(new Transaction(password,date,TransactionKind.loanPayment,
                            Integer.valueOf(amount)));
                }else {
                    transactions.readLine();
                    transactions.readLine();
                    if(kind.equals("transfer")||kind.equals("payBill"))
                        transactions.readLine();
                }
            }else {
                String kind=transactions.readLine();
                transactions.readLine();
                transactions.readLine();
                if(kind.equals("transfer")||kind.equals("payBill"))
                    transactions.readLine();
            }
        }
        return transactionList;
    }
    public synchronized  static ArrayList loadGetLoanTransaction(Account account)throws Exception{
        File transactionFile=new File("src/transactions.dat");
        transactions=new RandomAccessFile(transactionFile,"rw");
        ArrayList<Transaction> transactionList=new ArrayList<Transaction>();
        while (transactions.read()!= -1) {
            transactions.seek(transactions.getFilePointer()-1);
            String password=transactions.readLine();
            if(password.equals(account.getPassword())){
                String kind=transactions.readLine();
                if(kind.equals("getLoan")){
                    String amount=transactions.readLine();
                    String date=transactions.readLine();
                    transactionList.add(new Transaction(password,date,TransactionKind.loanPayment,
                            Integer.valueOf(amount)));
                }else {
                    transactions.readLine();
                    transactions.readLine();
                    if(kind.equals("transfer")||kind.equals("payBill"))
                        transactions.readLine();
                }
            }else {
                String kind=transactions.readLine();
                transactions.readLine();
                transactions.readLine();
                if(kind.equals("transfer")||kind.equals("payBill"))
                    transactions.readLine();
            }
        }
        return transactionList;
    }
    public synchronized  static ArrayList loadTransferTransaction(Account account)throws Exception{
        File transactionFile=new File("src/transactions.dat");
        transactions=new RandomAccessFile(transactionFile,"rw");
        ArrayList<Transaction> transactionList=new ArrayList<Transaction>();
        while (transactions.read()!= -1) {
            transactions.seek(transactions.getFilePointer() - 1);
            String password = transactions.readLine();
            if (password.equals(account.getPassword())) {
                String kind = transactions.readLine();
                if (kind.equals("transfer")) {
                    String amount = transactions.readLine();
                    String date = transactions.readLine();
                    String destinationAccount= transactions.readLine();
                    transactionList.add(new Transaction(password,date,TransactionKind.transfer,destinationAccount
                            ,Integer.valueOf(amount)));
                } else if (kind.equals("payBill")) {
                    transactions.readLine();
                }
            } else {
                String kind = transactions.readLine();
                transactions.readLine();
                transactions.readLine();
                if (kind.equals("payBill")) {
                    transactions.readLine();
                }
            }
        }
        return transactionList;
    }
    public synchronized  static ArrayList loadPayBilTransaction(Account account)throws Exception{
        File transactionFile=new File("src/transactions.dat");
        transactions=new RandomAccessFile(transactionFile,"rw");
        ArrayList<Transaction> transactionList=new ArrayList<Transaction>();
        while (transactions.read()!= -1) {
            transactions.seek(transactions.getFilePointer() - 1);
            String password = transactions.readLine();
            System.out.println(" password in transaction file is :"+password);
            if (password.equals(account.getPassword())) {
                System.out.println(" password.equals(account.getPassword()==true");
                String kind = transactions.readLine();
                System.out.println("kind in transaction file is: "+kind);
                if (kind.equals("payBill")) {
                    System.out.println(" kind.equals(payBill)==true");
                    String amount = transactions.readLine();
                    System.out.println(" amount: "+amount);
                    String date = transactions.readLine();
                    System.out.println(" date:" +date);
                    String billId = transactions.readLine();
                    System.out.println(" billId: "+billId);
                    transactionList.add(new Transaction(password, TransactionKind.payBill, Integer.valueOf(amount),
                            billId, date));
                }else {
                    String amount = transactions.readLine();
                    String date = transactions.readLine();
                    if (kind.equals("transfer")) {
                        transactions.readLine();
                    }
                }
            } else {
                String kind = transactions.readLine();
                transactions.readLine();
                transactions.readLine();
                if (kind.equals("transfer")) {
                    transactions.readLine();
                }
            }
        }
        return transactionList;
    }
}
