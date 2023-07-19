import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client {
    final static int port=9039;
    private static String serverAddress="127.0.1";
    public static void main(String[] args) {
        BufferedReader reader=null;
        // PrintWriter writer;  if needed
        Socket socket=null;
        try {
            socket=new Socket(serverAddress,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (UnknownHostException exception){
            System.out.println(exception.getMessage());
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }
        finally {
            try {
                while (reader.read()>0) {
                    System.out.println(reader.readLine());
                }
                reader.close();
                socket.close();
            }
            catch(IOException e) {
                System.err.println("Socket can not be closed !");
            }
        }
    }
}
