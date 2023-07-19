import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
public class Customer {
    private ArrayList<Account> accounts=new ArrayList<>();
    private ArrayList<Account> commonList=new ArrayList<>();
    private String name;
    private String lastName;
    private String password;
    private String id;
    private String emailAddress;
    private String phoneNum;
    public Customer(String name, String lastName,String password,String id,String emailAddress,String phoneNum){
        this.name=name;
        this.lastName=lastName;
        this.password=password;
        this.id=id;
        this.phoneNum=phoneNum;
        this.emailAddress=emailAddress;
    }
    public String getId() {
        return id;
    }
    public String getPassword() {
        return password;
    }
    public String getName() {
        return name;
    }
    public String getLastName() {
        return lastName;
    }
    public ArrayList<Account> getAccounts() {
        return accounts;
    }
    public ArrayList<Account>getCommonList(){
        return commonList;
    }
    public Account findAccount(String password){
        for(int i=0;i<accounts.size();i++){
            if(accounts.get(i).getPassword().equals(password))
                return accounts.get(i);
        }
        return null;
    }
    public   synchronized void loadCustomerAccounts(String password,String id) {
        RandomAccessFile accountFile = null;
        try {
            accountFile = Server.getAccountFile();
            while (accountFile.read() > 0) {
                accountFile.seek(accountFile.getFilePointer() - 1);
                if (accountFile.readLine().equals(id)) {
                    if (accountFile.readLine().equals(password)) {
                        String accountPass = accountFile.readLine();
                        String money = accountFile.readLine();
                        String kindStr = accountFile.readLine();
                        AccountKind accountKind = null;
                        switch (kindStr) {
                            case "CheckingAccount": accountKind = AccountKind.CheckingAccount;
                                break;
                            case "SavingAccount": accountKind = AccountKind.SavingAccount;
                        }
                        if(!accounts.contains(new Account( accountPass,password,id, Integer.valueOf(money), accountKind)))
                            this.accounts.add(new Account( accountPass,password,id, Integer.valueOf(money), accountKind));
                    } else {
                        accountFile.readLine();
                        accountFile.readLine();
                        accountFile.readLine();
                    }
                } else {
                    accountFile.readLine();
                    accountFile.readLine();
                    accountFile.readLine();
                    accountFile.readLine();
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public synchronized void loadAlias(){
        RandomAccessFile alias=Server.getAlias();
        try {
            for(int i=0;i<accounts.size();i++) {
                while (alias.read() > 0) {
                    alias.seek(alias.getFilePointer() - 1);
                    if(alias.readLine().equals(accounts.get(i).getPassword())){
                        accounts.get(i).setAlias(alias.readLine());//rad shodan
                    }else alias.readLine();
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
