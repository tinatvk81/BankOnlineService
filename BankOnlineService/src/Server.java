//band:::tina tavakkoli         9922762220
//band:::maryam allahkhani         9912762154
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
public class Server {
    protected static   RandomAccessFile accountFile;
    protected static RandomAccessFile customersFile;
    private static RandomAccessFile alias;
    public static void main(String[] args) {
        final int port = 9039;
        ServerSocket serverSocket=null;
        try{
            File customers=new File("src/customers.dat");
            File accounts=new File("src/account.dat");
            File aliasFile=new File("src/alias.dat");
            accountFile=new RandomAccessFile(accounts,"rw");
            customersFile= new RandomAccessFile(customers,"rw");
            alias=new RandomAccessFile(aliasFile,"rw");
        }catch (IOException ioException){
            System.out.println("Server can't read files");
            System.out.println(ioException.getMessage());
        }
        while (true) {
            try {
                serverSocket = new ServerSocket(port);
                Socket client = serverSocket.accept();
                new EachClientService(client);
            }catch (IOException ioException){
                System.out.println(ioException.getMessage());
            } finally {
                try {
                    serverSocket.close();
                }
                catch (IOException ioException){
                    System.out.println("Server can not be closed!");
                }

            }
        }
    }
    public static RandomAccessFile getAccountFile() throws FileNotFoundException {
        File accounts=new File("src/account.dat");
        accountFile=new RandomAccessFile(accounts,"rw");
        return accountFile;
    }
    public  static RandomAccessFile getCustomersFile() {
        return customersFile;
    }
    public static RandomAccessFile getAlias(){
        return alias;
    }
}
