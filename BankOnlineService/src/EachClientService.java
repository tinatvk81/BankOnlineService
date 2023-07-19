import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class EachClientService extends Thread {
    private Socket socket;
    Customer customer;
    private boolean loginResult;
    private boolean registerResult;
    // BufferedWriter reader;   if needed
    private PrintWriter writer;
    private Stage primaryStage;
    public EachClientService(Socket socket){
        this.socket=socket;
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream())),true);
        }catch (IOException ioException){
            System.out.println(ioException.getMessage());
        }
        start();//run start
    }
    public void run() {
        System.out.println("client connected");
        try{
            com.sun.javafx.application.PlatformImpl.startup(()->{   //start javafx app
                Group root=new Group();
                primaryStage=new Stage();
                primaryStage.setTitle("online banking");
                Scene scene=new Scene(root,540,600);
                scene.setFill(Color.LAVENDER);
                entranceScene(root);
                primaryStage.setScene(scene);
                primaryStage.show();
            });
        }catch (Exception exception){
            System.out.println(exception.getMessage());
        }
    }
    private void entranceScene(Group group){
        Label idLabel=new Label("enter your id");
        Label passwordLabel=new Label("enter password");
        TextField idTextField=new TextField();
        TextField passwordTextField=new TextField();
        idTextField.setPromptText("only digits");
        passwordTextField.setPromptText("only 8 characters");
        passwordTextField.setPrefWidth(135);
        Text welcomeText=new Text(60,40,"Welcome");
        welcomeText.setFont(Font.font ("Verdana", FontPosture.ITALIC, 40));
        welcomeText.setFill(Color.PURPLE);
        Button registerButton=new Button("Register");
        registerButton.setMinSize(300,40);
        registerButton.setStyle("-fx-background-color: BLUEVIOLET");
        Button endButton=new Button("exit program");
        endButton.setPadding(new Insets(10));
        endButton.setStyle("-fx-background-color: SkyBlue");
        endButton.setOnAction(event -> end()
        );
        Button loginButton=new Button("login");
        loginButton.setStyle("-fx-background-color: DeepSkyBlue");
        HBox hBoxId=new HBox();
        hBoxId.setPadding(new Insets(20));
        hBoxId.setSpacing(20);
        hBoxId.getChildren().addAll(idLabel,idTextField);
        HBox hBoxPassword=new HBox();
        hBoxPassword.setPadding(new Insets(20));
        hBoxPassword.getChildren().addAll(passwordLabel,passwordTextField);
        hBoxPassword.setSpacing(20);
        VBox vBox=new VBox();
        vBox.setPadding(new Insets(40));
        vBox.getChildren().addAll(hBoxId,hBoxPassword,loginButton);
        loginButton.setOnAction(event -> {
            loginResult=login(passwordTextField.getText(),idTextField.getText());
            if (!loginResult) {
                writer.println(" There is wrong ID or password please try again");
                idTextField.clear();
                passwordTextField.clear();
            }
            if (loginResult){
                writer.println(" Login has be done successfully.hello "+"  "+
                        customer.getLastName());
                menuScene(group);
            }
        });
        BorderPane borderPane=new BorderPane();
        borderPane.setPadding(new Insets(60));
        borderPane.setTop(welcomeText);
        borderPane.setCenter(vBox);
        borderPane.setBottom(registerButton);
        borderPane.setRight(endButton);
        registerButton.setAlignment(Pos.CENTER);
        registerButton.setOnAction(event -> registerService(group));
        group.getChildren().add(borderPane);
    }
    private void end(){
        writer.println(" End program please close the window");
        System.out.println("client disconnected");
        try {
            for (int i=0;i<customer.getAccounts().size();i++){
                customer.getAccounts().get(i).changeMoney(customer.getAccounts().get(i).getMoney());
            }
            writer.close();
            socket.close();
        }catch (IOException ioException){
            writer.println("There is  problem in disconnecting the socket");
            writer.println(ioException.getMessage());
        }
    }
    private boolean login(String password,String id){
        boolean validInput=checkLoginInput(id,password);
        Customer customer=loadCustomer(password, id);
        if(customer==null)
            writer.println(" customer==nul");
        if(!validInput)
            writer.println(" invalid in put");
        if(customer==null || !validInput)
            return false;
        else {
            this.customer=customer;
            customer.loadCustomerAccounts(password,id);
            customer.loadAlias();
            return true;
        }
    }
    private  synchronized Customer loadCustomer(String password,String id){
        boolean find=false;
        Customer customer=null;
        try {
            Server.getCustomersFile().seek(0);
            while (!find && Server.getCustomersFile().read()>0){
                String name=Server.getCustomersFile().readLine();
                String lastName=Server.getCustomersFile().readLine();
                if(Server.getCustomersFile().readLine().equals(password)){
                    if(Server.getCustomersFile().readLine().equals(id)){
                        find=true;
                        String emailAddress=Server.getCustomersFile().readLine();
                        String phoneNum=Server.getCustomersFile().readLine();
                        customer=new Customer(name,lastName,password,id,emailAddress
                                ,phoneNum);
                    }else {
                        Server.getCustomersFile().readLine();
                        Server.getCustomersFile().readLine();
                    }
                }else{
                    Server.getCustomersFile().readLine();
                    Server.getCustomersFile().readLine();
                    Server.getCustomersFile().readLine();
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
            System.out.println(ioException.getMessage());
        }finally {
            return customer ;
        }
    }
    private void registerService(Group group){
        group.getChildren().clear();
        Label nameLabel=new Label("first name");
        Label lastLabel=new Label("last name");
        Label passwordLabel=new Label("password");
        Label idLabel=new Label("id number");
        Label phoneNumLabel=new Label("phone number");
        Label emailLabel=new Label("email address");
        TextField nameTextField=new TextField();
        nameTextField.setPromptText("only lower_case letters");
        TextField lastTextField=new TextField();
        lastTextField.setPromptText("only lower_case letters");
        TextField passTextField=new TextField();
        passTextField.setPromptText("only 8 characters");
        TextField idTextField=new TextField();
        idTextField.setPromptText("only digits");
        TextField phoneNumTextField=new TextField();
        TextField emailTextField=new TextField();
        HBox nameHbox=new HBox(nameLabel,nameTextField);
        HBox lastNaHbox=new HBox(lastLabel,lastTextField);
        HBox passwordHbox=new HBox(passwordLabel,passTextField);
        HBox idHbox=new HBox(idLabel,idTextField);
        HBox phoneHbox=new HBox(phoneNumLabel,phoneNumTextField);
        HBox emailHbox=new HBox(emailLabel,emailTextField);
        nameHbox.setSpacing(20);
        lastNaHbox.setSpacing(20);
        passwordHbox.setSpacing(20);
        idHbox.setSpacing(20);
        phoneHbox.setSpacing(20);
        emailHbox.setSpacing(20);
        Button endButton=new Button("exit program");
        endButton.setStyle("-fx-background-color: SkyBlue");
        endButton.setPadding(new Insets(20));
        endButton.setOnAction(event -> end()
        );
        Button submit=new Button("submit");
        submit.setStyle("-fx-background-color: LIGHTBLUE");
        submit.setOnAction(event -> {
            registerResult=saveNewCustomer(nameTextField.getText(),lastTextField.getText()
                    ,passTextField.getText(),idTextField.getText()
                    ,phoneNumTextField.getText(),emailTextField.getText());

            if(!registerResult ){
                writer.println(" There is a wrong input.Please try again.");
                passTextField.clear();
                nameTextField.clear();
                lastTextField.clear();
                idTextField.clear();
                phoneNumTextField.clear();
                emailTextField.clear();
            }
            if (registerResult){
                writer.println(" Registration has been done successfully.");
                this.customer=new Customer(nameTextField.getText(),lastTextField.getText()
                        ,passTextField.getText(),idTextField.getText()
                        ,emailTextField.getText(),phoneNumTextField.getText());
                menuScene(group);
            }
        });
        VBox vBox=new VBox(nameHbox,lastNaHbox,passwordHbox,idHbox,phoneHbox,emailHbox,submit);
        vBox.setPadding(new Insets(80));
        vBox.setSpacing(30);
        vBox.setAlignment(Pos.CENTER);
        endButton.setAlignment(Pos.TOP_RIGHT);
        group.getChildren().add(vBox);
        group.getChildren().add(endButton);
    }
    private synchronized boolean saveNewCustomer(String name,String lastName, String password, String id,
                                                 String phoneNum, String emailAddress){//frakhni:sabtnam
        boolean validInput=checkRegisterInput(name, lastName, password, id, phoneNum,emailAddress);
        Customer newCustomer=loadCustomer(password,id);
        if (!validInput || newCustomer!=null) {
            return false;
        }
        try {
            Server.getCustomersFile().writeBytes(name+"\n");
            Server.getCustomersFile().writeBytes(lastName+"\n");
            Server.getCustomersFile().writeBytes(password+"\n");
            Server.getCustomersFile().writeBytes(id+"\n");
            Server.getCustomersFile().writeBytes(phoneNum+"\n");
            Server.getCustomersFile().writeBytes(emailAddress+"\n");
            customer=new Customer(name,lastName,password,id,emailAddress,phoneNum);
        }catch (IOException ioException){
            writer.println(" There is a problem.");
            writer.println(" "+ioException.getMessage());
            return false;
        }
        return true;
    }
    private boolean checkLoginInput(String id,String password){
        for (int i = 0; i < id.length(); i++) {
            if(id.charAt(i)<'0' || id.charAt(i)>'9') {
                writer.println(" invalid id format");
                return false;
            }
        }
        if(password.length()!=8){
            writer.println(" invalid password length");
            return false;
        }
        return true;
    }
    private boolean checkRegisterInput(String name,String lastName, String password, String id,String phoneNum,
                                       String emailAddress){
        for (int i = 0; i < name.length(); i++) {
            if(name.charAt(i)>'z'|| name.charAt(i)<'a') {
                writer.println(" invalid first name format");
                return false;
            }
        }
        for (int i = 0; i < lastName.length(); i++) {
            if(lastName.charAt(i)>'z'|| lastName.charAt(i)<'a') {
                writer.println(" invalid last name format");
                return false;
            }
        }
        for (int i = 0; i < id.length(); i++) {
            if(id.charAt(i)<'0'|| id.charAt(i)>'9') {
                writer.println(" invalid id format");
                return false;
            }
        }
        if(!emailAddress.endsWith("email.com")){
            writer.println(" invalid email address");
            return false;
        }
        /* it throws .StringIndexOutOfBoundsException: String index out of range: 4
	    at java.lang.String.charAt(String.java:658)
        for (int i = 0; i <phoneNum.length(); i++) {
            if (phoneNum.charAt(i) < '0' || id.charAt(i) > '9'){
                return false;
            }
        }
        if(phoneNum.length()!=8)
            return false;
        */
        return true;
    }
    private void menuScene(Group group){
        group.getChildren().clear();
        Button endButton=new Button("exit program");
        endButton.setPadding(new Insets(10));
        endButton.setStyle("-fx-background-color: SkyBlue");
        endButton.setOnAction(event -> end()
        );
        Text menuText=new Text("Menu");
        menuText.setFont(Font.font ("Verdana", FontPosture.ITALIC, 40));
        menuText.setFill(Color.BLUEVIOLET);
        Button applyLoanBut=new Button("Apply for loan");
        applyLoanBut.setOnAction(event -> applyLoan(group));
        Button accountManagement=new Button("Account Management");
        accountManagement.setOnAction(event -> accountManagement(group));
        Button payBillBut=new Button("Pay bill");
        payBillBut.setOnAction(event -> payBillScene(group));
        VBox vBox=new VBox(applyLoanBut,accountManagement,payBillBut);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(30));
        BorderPane borderPane=new BorderPane();
        borderPane.setTop(menuText);
        borderPane.setCenter(vBox);
        borderPane.setRight(endButton);
        group.getChildren().add(borderPane);
    }
    private void accountManagement(Group group){
        group.getChildren().clear();
        Button returnToMenu=new Button("return to menu");
        returnToMenu.setStyle("-fx-background-color: Blue");
        returnToMenu.setOnAction(event -> menuScene(group));
        Button openAccountBut=new Button("Open Account");
        openAccountBut.setOnAction(event -> openAccount(group));
        Button seeAccountBut=new Button("Account report");
        seeAccountBut.setOnAction(event -> seeAccountsReport(group));
        Button makeCommonAccountListBut=new Button("Make common list");
        makeCommonAccountListBut.setOnAction(event -> makeCommonList(group));
        Button transferBut=new Button("Transfer");
        transferBut.setOnAction(event -> transfer(group));
        Button closeAccountBut=new Button("Close account");
        closeAccountBut.setOnAction(event -> closeAccount(group));
        closeAccountBut.setStyle("-fx-background-color: SkyBlue");
        openAccountBut.setStyle("-fx-background-color: SkyBlue");
        seeAccountBut.setStyle("-fx-background-color: SkyBlue");
        transferBut.setStyle("-fx-background-color: SkyBlue");
        makeCommonAccountListBut.setStyle("-fx-background-color: SkyBlue");
        VBox vBox=new VBox(openAccountBut,seeAccountBut,makeCommonAccountListBut,transferBut,closeAccountBut);
        returnToMenu.setPadding(new Insets(10));
        closeAccountBut.setPrefWidth(120);
        transferBut.setPrefWidth(120);
        makeCommonAccountListBut.setPrefWidth(120);
        openAccountBut.setPrefWidth(120);
        seeAccountBut.setPrefWidth(120);
        closeAccountBut.setPrefHeight(30);
        transferBut.setPrefHeight(30);
        makeCommonAccountListBut.setPrefHeight(30);
        openAccountBut.setPrefHeight(30);
        seeAccountBut.setPrefHeight(30);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(80));
        BorderPane borderPane=new BorderPane();
        borderPane.setCenter(vBox);
        borderPane.setRight(returnToMenu);
        vBox.setPadding(new Insets(60));
        group.getChildren().add(borderPane);
    }
    private void payBillScene(Group group){
        group.getChildren().clear();
        Label billNum=new Label("enter bill number");
        Label paymentNum=new Label("bill payment number");
        Label amount=new Label("enter amount");
        Label accountPass=new Label("account password");
        TextField billNumTextField=new TextField();
        TextField paymentNumTextField=new TextField();
        TextField amountTextField=new TextField();
        TextField passwordTextField=new TextField();
        HBox password=new HBox(accountPass,passwordTextField);
        password.setSpacing(20);
        HBox billNUmHbox=new HBox(billNum,billNumTextField);
        billNUmHbox.setSpacing(20);
        HBox paymentNumHbox=new HBox(paymentNum,paymentNumTextField);
        paymentNumHbox.setSpacing(20);
        HBox amoutHbox=new HBox(amount,amountTextField);
        amoutHbox.setSpacing(20);
        Button submit=new Button("submit");
        submit.setStyle("-fx-background-color: slateblue");
        submit.setOnAction(event -> {
            int amountT=Integer.valueOf(amountTextField.getText());
            String paymentNumT=paymentNumTextField.getText();
            String billNumT= billNumTextField.getText();
            String passwordT=passwordTextField.getText();
            if(checkPayBillInput(paymentNumT,billNumT,passwordT,amountT)){
                Account foundedAccount=customer.findAccount(passwordT);
                if(foundedAccount==null){//
                    writer.println(" wrong account password");
                    amountTextField.clear();
                    paymentNumTextField.clear();
                    billNumTextField.clear();
                    passwordTextField.clear();
                }else {
                    if(foundedAccount.paybill(amountT,paymentNumT,billNumT,passwordT)){
                        writer.println(" transaction has been done successfully.");
                    }else {
                        writer.println(" the account does not have enough money.try again");
                        amountTextField.clear();
                        paymentNumTextField.clear();
                        billNumTextField.clear();
                        passwordTextField.clear();
                    }
                }
            }else {
                amountTextField.clear();
                paymentNumTextField.clear();
                billNumTextField.clear();
                passwordTextField.clear();
            }
        });
        Button returnToMenu=new Button("return to menu");
        returnToMenu.setStyle("-fx-background-color: slateblue");
        returnToMenu.setOnAction(event -> menuScene(group));
        returnToMenu.setAlignment(Pos.TOP_LEFT);
        VBox vBox=new VBox(billNUmHbox,paymentNumHbox,amoutHbox,password,submit);//
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(120));
        vBox.setAlignment(Pos.CENTER);
        group.getChildren().addAll(vBox,returnToMenu);
    }
    private void applyLoan(Group group){
        group.getChildren().clear();
        Label repaymentPeriod=new Label("repayment period");
        Label amount=new Label("loan amount");
        Label date=new Label("date/month/year");
        Label passwordaccountT=new Label("password account");
        TextField repaymentTextField=new TextField();
        TextField amountTextField=new TextField();
        TextField dateTextField=new TextField();
        TextField passwordaccountTextField=new TextField();
        HBox passwordaccountHbox=new HBox(passwordaccountT,passwordaccountTextField);
        passwordaccountHbox.setSpacing(20);
        HBox repaymentHbox=new HBox(repaymentPeriod,repaymentTextField);
        repaymentHbox.setSpacing(20);
        HBox amountHbox=new HBox(amount,amountTextField);
        amountHbox.setSpacing(20);
        HBox dateHbox=new HBox(date,dateTextField);
        dateHbox.setSpacing(20);
        Button submit=new Button("submit");
        VBox vBox=new VBox(amountHbox,repaymentHbox,dateHbox,passwordaccountHbox,submit);
        Button returnToMenu=new Button("return to menu");
        returnToMenu.setStyle("-fx-background-color: mediumpurple");
        submit.setStyle("-fx-background-color: mediumpurple");
        submit.setOnAction(event -> {
            String dateT=String.valueOf(dateTextField.getText());
            String passwordT=String.valueOf(passwordaccountTextField.getText());
            if(checkApplyLoanInput(amountTextField.getText(),repaymentTextField.getText())){
                int amountT= Integer.valueOf(amountTextField.getText());
                int repaymentT= Integer.valueOf(repaymentTextField.getText());
                Account foundedAccount= customer.findAccount(passwordT);
                if(foundedAccount==null){
                    writer.println(" wrong account password");
                    dateTextField.clear();
                    passwordaccountTextField.clear();
                    repaymentTextField.clear();
                    amountTextField.clear();
                }else {
                    foundedAccount.getLoan( dateT,amountT, repaymentT);
                    if(foundedAccount.loanPay(dateT,amountT,repaymentT,passwordT)){
                        writer.println(" transaction done successfully");
                    }
                }
            }else {
                writer.println(" please try again");
                dateTextField.clear();
                passwordaccountTextField.clear();
                repaymentTextField.clear();
            }
        });
        returnToMenu.setOnAction(event -> menuScene(group));
        returnToMenu.setAlignment(Pos.CENTER);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(80));
        vBox.setAlignment(Pos.CENTER);
        group.getChildren().addAll(vBox,returnToMenu);
    }
    private void openAccount(Group group){
        group.getChildren().clear();
        Label accountPass=new Label("account password");
        TextField passwordTextField=new TextField();
        HBox password=new HBox(accountPass,passwordTextField);
        password.setSpacing(20);
        Label kindLabel=new Label("account kind:");
        String[] accountKinds={"Checking Account","Saving Account"};
        ChoiceBox choiceBox=new ChoiceBox(FXCollections.observableArrayList(accountKinds));
        HBox kindHbox=new HBox(kindLabel,choiceBox);
        kindHbox.setSpacing(20);
        Button returnToMenu=new Button("return to menu");
        Button submit=new Button("submit");
        submit.setOnAction(event -> {
            if(Account.findAccount(passwordTextField.getText())!=null){//vogod dashte
                writer.println(" there is an other account with this password.please try an other password");
                passwordTextField.clear();
            }
            else {
                AccountKind kind = null;
                if (choiceBox.getValue().toString().equals("Checking Account"))
                    kind = AccountKind.CheckingAccount;
                else if (choiceBox.getValue().toString().equals("Saving Account"))
                    kind = AccountKind.SavingAccount;
                System.out.println(" choicebox is"+choiceBox.getValue().toString());
                Account newAccount = new Account(passwordTextField.getText(), customer.getPassword(),
                        customer.getId(), kind);
                customer.getAccounts().add(newAccount);
                newAccount.saveNewAccount();
            }
        });
        returnToMenu.setStyle("-fx-background-color: mediumpurple");
        submit.setStyle("-fx-background-color: mediumpurple");
        returnToMenu.setOnAction(event -> menuScene(group));
        VBox vBox=new VBox(password,kindHbox,submit);
        vBox.setSpacing(30);
        vBox.setAlignment(Pos.CENTER);
        BorderPane borderPane=new BorderPane();
        borderPane.setRight(returnToMenu);
        borderPane.setCenter(vBox);
        group.getChildren().add(borderPane);
    }
    private void transfer(Group group){
        group.getChildren().clear();
        Label accountPass=new Label("account password");
        TextField passwordTextField=new TextField();
        HBox password=new HBox(accountPass,passwordTextField);
        password.setSpacing(20);
        Label destinationPass=new Label("destination account password");
        TextField destinationPassTextField=new TextField();
        HBox destination=new HBox(destinationPass,destinationPassTextField);
        destination.setSpacing(20);
        Label amount=new Label("enter amount");
        TextField amountTextField=new TextField();
        HBox amountHbox=new HBox(amount,amountTextField);
        amountHbox.setSpacing(20);
        Button submit=new Button("submit");
        submit.setStyle("-fx-background-color: slateblue");
        submit.setOnAction(event -> {
            String passwordT=passwordTextField.getText();
            String destinationPassT=destinationPassTextField.getText();
            String amountT=amountTextField.getText();
            if( checkTransferInput(destinationPassT,passwordT,amountT)){
                Account account=customer.findAccount(passwordT);
                if(account!=null){
                    if(customer.findAccount(destinationPassT)!=null){
                        if(account.transfer(passwordT,destinationPassT,Integer.valueOf(amountT),Account.findAccount(destinationPassT))) {
                            customer.findAccount(destinationPassT).setMoney(
                                    customer.findAccount(destinationPassT).getMoney() + Integer.valueOf(amountT));
                            writer.println(" transaction is done successfully.");
                        }else {
                            writer.println(" account does not have enough money.please try again");
                            passwordTextField.clear();
                            destinationPassTextField.clear();
                            amountTextField.clear();
                        }
                    }else {
                        writer.println(" wrong destination account.please try again");
                        passwordTextField.clear();
                        destinationPassTextField.clear();
                        amountTextField.clear();
                    }
                }else {
                    writer.println(" wrong account.please try again");
                    passwordTextField.clear();
                    destinationPassTextField.clear();
                    amountTextField.clear();
                }
            }
        });
        Button returnToMenu=new Button("return to menu");
        returnToMenu.setStyle("-fx-background-color: slateblue");
        returnToMenu.setOnAction(event -> menuScene(group));
        VBox vBox=new VBox(password,amountHbox,destination,submit);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(100));
        vBox.setAlignment(Pos.CENTER);
        group.getChildren().addAll(vBox,returnToMenu);
    }
    private void closeAccount(Group group){
        group.getChildren().clear();
        Label accountPass=new Label("account password");
        TextField passwordTextField=new TextField();
        HBox password=new HBox(accountPass,passwordTextField);
        password.setSpacing(20);
        Button returnToMenu=new Button("return to menu");
        returnToMenu.setStyle("-fx-background-color: slateblue");
        returnToMenu.setOnAction(event -> menuScene(group));
        Button submit=new Button("submit");
        submit.setStyle("-fx-background-color: slateblue");
        Label destinationPass=new Label();
        Button done=new Button("done");
        done.setStyle("-fx-background-color: slateblue");
        TextField destinationPassTectField=new TextField();
        HBox destination=new HBox(destinationPass);
        destination.setSpacing(20);
        VBox vBox=new VBox(password,submit,destination);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(120));
        vBox.setAlignment(Pos.CENTER);
        group.getChildren().addAll(vBox,returnToMenu);
        submit.setOnAction(event -> {
            System.out.println(customer.findAccount(passwordTextField.getText()));
            if(customer.findAccount(passwordTextField.getText())!=null){
                if(customer.findAccount(passwordTextField.getText()).getMoney()!=0){
                    writer.println(" your account has money."+
                            "Please enter password of an account to transfer the money");
                    destinationPass.setText("account password");//entegal money
                    destination.getChildren().add(destinationPassTectField);
                    vBox.getChildren().add(done);
                    done.setOnAction(event1 -> {
                        if(customer.findAccount(destinationPassTectField.getText())!=null){
                            customer.findAccount(destinationPassTectField.getText()).setMoney(
                                    customer.findAccount(destinationPassTectField.getText()).getMoney()+
                                            customer.findAccount(passwordTextField.getText()).getMoney());
                            customer.getAccounts().remove(customer.findAccount(passwordTextField.getText()));
                            if(customer.findAccount(destinationPassTectField.getText()).deletAccount()){
                                writer.println(" the account is closed successfully.");
                            }
                        }
                        else {
                            destinationPassTectField.clear();
                            writer.println(" wrong destination account password.please try again.");
                        }
                    });
                }else {
                    customer.findAccount(passwordTextField.getText()).deletAccount();
                    customer.getAccounts().remove(customer.findAccount(passwordTextField.getText()));
                    writer.println(" the account is closed successfully.");
                }
            }
            else {
                writer.println(" wrong account password.Please try again");
                passwordTextField.clear();
            }
        });
    }
    private void makeCommonList(Group group){//
        ArrayList<Account>accounts= customer.getAccounts();
        Button submit=new Button("submit");
        submit.setStyle("-fx-background-color: slateblue");
        ArrayList<CheckBox>checkBoxArrayList=new ArrayList<>();
        ArrayList<TextField>textFields=new ArrayList<>();
        CheckBox checkBox;
        TextField textField;
        Label label;
        HBox hBox;
        VBox vBox=new VBox(30,submit);
        if(accounts!=null) {
            for (int i = 0; i < accounts.size(); i++) {
                checkBox = new CheckBox();
                textField=new TextField();
                checkBox.setTranslateY(5);
                label = new Label(accounts.get(i).getPassword());
                label.setFont(Font.font(20));//
                hBox = new HBox(20,label,checkBox,textField);
                vBox.getChildren().add(hBox);
                checkBoxArrayList.add(checkBox);
                textFields.add(textField);
            }
            vBox.setPadding(new Insets(30,200,30,180));
            BorderPane borderPane=new BorderPane();
            ScrollPane s = new ScrollPane();//
            s.setContent(vBox);
            s.setFitToHeight(true);
            s.setPrefSize(450,500);
            s.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            group.getChildren().add(s);
            submit.setOnAction(event -> {
                for (int j = 0; j < checkBoxArrayList.size(); j++) {
                    if (checkBoxArrayList.get(j).isSelected()) {
                        customer.getCommonList().add(accounts.get(j));
                    }
                }
                writer.println(" chosen accounts added to common list successfully.");
                for (int j = 0; j < textFields.size(); j++) {
                    if(textFields.get(j).getText()!=null){
                        if(accounts.get(j).getAlias()==null) {
                            accounts.get(j).setAlias(textFields.get(j).getText());
                            accounts.get(j).saveAlias(textFields.get(j).getText());
                        }else {
                            accounts.get(j).setAlias(textFields.get(j).getText());
                            accounts.get(j).changeAlias(textFields.get(j).getText());
                        }
                        writer.println(" alias added to accounts successfully");
                    }
                }
                accountManagement(group);
            });
            borderPane.setCenter(vBox);
            borderPane.setRight(submit);
            group.getChildren().addAll(borderPane);
        }
        else writer.println(" You don't have any account to add  to common list or set alias.");
    }
    private void seeAccountsReport(Group group){
        group.getChildren().clear();
        Label password=new Label("account password");
        TextField passwordTextField=new TextField();
        HBox pass=new HBox(password,passwordTextField);
        pass.setSpacing(20);
        Label kindLabel=new Label("transaction kind:");
        String[] transactionType={"transfer","payBill","loanPayment","getLoan"};
        ChoiceBox choiceBox=new ChoiceBox(FXCollections.observableArrayList(transactionType));
        HBox kindHbox=new HBox(kindLabel,choiceBox);
        kindHbox.setSpacing(20);
        Button returnToMenu=new Button("return to menu");
        returnToMenu.setOnAction(event -> menuScene(group));
        returnToMenu.setStyle("-fx-background-color: mediumpurple");
        Button submit=new Button("submit");
        VBox vBox=new VBox(pass,kindHbox,submit,returnToMenu);
        vBox.setSpacing(30);
        vBox.setPadding(new Insets(20,100,50,100));
        group.getChildren().add(vBox);
        submit.setStyle("-fx-background-color: slateblue");
        submit.setOnAction(event -> {
            Account account=customer.findAccount(passwordTextField.getText());
            if(account!=null){
                writer.println(" account!=null");
                Text kindText=new Text("account kind : "+account.getKind().name());
                Text amountText=new Text("account amount :"+account.getMoney());
                vBox.getChildren().addAll(kindText,amountText);
                if(account.getAlias()!=null){
                    Text alias=new Text(" account alias is "+account.getAlias());
                    vBox.getChildren().add(alias);
                }
                switch (String.valueOf(choiceBox.getValue())){
                    case "transfer":makeTransferTableView(account,vBox);
                        break;
                    case "payBill":makePayBilTableView(account,vBox);
                        break;
                    case "loanPayment":makePayLoanTableView(account,vBox);
                        break;
                    case "getLoan":makeGetLoanTableView(account,vBox);
                        break;
                }

            }else{
                writer.println(" wrong password account.please try again.");
                passwordTextField.clear();
            }
        });
    }
    public void makeGetLoanTableView(Account account,VBox group){////////////////
        ArrayList<Transaction>list=null;
        try {
            list=Transaction.loadGetLoanTransaction(account);
        }catch (Exception e){
            writer.println(" can't show get loan transactions");
            writer.println(" "+e.getMessage());
        }
        if(list.size()!=0 && list!=null) {
            Text header = new Text("get loan transaction");
            TableView tableView = new TableView<Transaction>();
            TableColumn accountPass = new TableColumn<Transaction, String>("password");
            accountPass.setCellValueFactory(new PropertyValueFactory<Transaction, String>("accountPassword"));
            TableColumn amount = new TableColumn<Transaction, Integer>("amount");
            amount.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
            TableColumn date = new TableColumn<Transaction, String>("date");
            date.setCellValueFactory(new PropertyValueFactory<Transaction, String>("date"));
            tableView.getColumns().add(accountPass);
            tableView.getColumns().add(date);
            tableView.getColumns().add(amount);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            for (int i = 0; i <list.size() ; i++) {
                tableView.getItems().add(list.get(i));//
            }
            VBox vBox=new VBox(header,tableView);
            vBox.setSpacing(30);
            vBox.setPadding(new Insets(10));
            group.getChildren().add(vBox);
        }
    }
    public void makePayLoanTableView(Account account,VBox group){
        ArrayList<Transaction>list=null;
        try {
            list=Transaction.loadLoanPayTransaction(account);
        }catch (Exception e){
            writer.println(" can't show pay loan transactions");
            writer.println(" "+e.getMessage());
        }
        if(list.size()!=0 && list!=null) {
            Text header=new Text("loan payment transaction");
            TableView tableView = new TableView<Transaction>();
            TableColumn accountPass = new TableColumn<Transaction, String>("password");
            accountPass.setCellValueFactory(new PropertyValueFactory<Transaction, String>("accountPassword"));
            TableColumn amount = new TableColumn<Transaction, Integer>("amount");
            amount.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
            TableColumn date = new TableColumn<Transaction, String>("date");
            date.setCellValueFactory(new PropertyValueFactory<Transaction, String>("date"));
            tableView.getColumns().add(accountPass);
            tableView.getColumns().add(date);
            tableView.getColumns().add(amount);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            for (int i = 0; i <list.size() ; i++) {
                tableView.getItems().add(list.get(i));
            }
            VBox vBox=new VBox(header,tableView);
            vBox.setSpacing(30);
            vBox.setPadding(new Insets(10));
            group.getChildren().add(vBox);
        }
    }
    public void makePayBilTableView(Account account,VBox group){
        ArrayList<Transaction>list=null;
        try {
            list=Transaction.loadPayBilTransaction(account);
        }catch (Exception e){
            writer.println(" can't show pay bill transactions");
            writer.println(" "+e.getMessage());
        }
        if(list.size()!=0 && list!=null) {
            Text header = new Text("pay bill transaction");
            TableView tableView = new TableView<Transaction>();
            TableColumn accountPass = new TableColumn<Transaction, String>("password");
            accountPass.setCellValueFactory(new PropertyValueFactory<Transaction, String>("accountPassword"));
            TableColumn amount = new TableColumn<Transaction, Integer>("amount");
            amount.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
            TableColumn date = new TableColumn<Transaction, String>("date");
            date.setCellValueFactory(new PropertyValueFactory<Transaction, String>("date"));
            TableColumn billId = new TableColumn<Transaction, String>("bill ID");
            billId.setCellValueFactory(new PropertyValueFactory<Transaction, String>("billID"));
            tableView.getColumns().add(accountPass);
            tableView.getColumns().add(date);
            tableView.getColumns().add(amount);
            tableView.getColumns().add(billId);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            for (int i = 0; i <list.size() ; i++) {
                tableView.getItems().add(list.get(i));
            }
            VBox vBox=new VBox(header,tableView);
            vBox.setSpacing(10);
            vBox.setPadding(new Insets(10));
            group.getChildren().add(vBox);
        }
    }
    public void makeTransferTableView(Account account,VBox group){/////////////////
        ArrayList<Transaction>list=null;
        try {
            list=Transaction.loadTransferTransaction(account);
        }catch (Exception e){
            writer.println(" can't show transfer transactions");
            writer.println(" "+e.getMessage());
        }
        if(list.size()!=0 && list!=null) {
            Text header = new Text("transfer transaction");
            TableView tableView = new TableView<Transaction>();
            TableColumn accountPass = new TableColumn<Transaction, String>("password");
            accountPass.setCellValueFactory(new PropertyValueFactory<Transaction, String>("accountPassword"));
            TableColumn amount = new TableColumn<Transaction, Integer>("amount");
            amount.setCellValueFactory(new PropertyValueFactory<Transaction, Integer>("amount"));
            TableColumn date = new TableColumn<Transaction, String>("date");
            date.setCellValueFactory(new PropertyValueFactory<Transaction, String>("date"));
            TableColumn destination = new TableColumn<Transaction, String>("destination account");
            destination.setCellValueFactory(new PropertyValueFactory<Transaction, String>("destinationAccount"));
            tableView.getColumns().add(accountPass);
            tableView.getColumns().add(date);
            tableView.getColumns().add(amount);
            tableView.getColumns().add(destination);
            tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            for (int i = 0; i <list.size() ; i++) {
                tableView.getItems().add(list.get(i));
            }
            VBox vBox=new VBox(header,tableView);
            vBox.setSpacing(30);
            vBox.setPadding(new Insets(10));
            group.getChildren().add(vBox);
        }
    }
    private boolean checkTransferInput(String destinationpassword,String password,String amount){
        for (int i = 0; i <amount.length(); i++) {
            if(amount.charAt(i)<'0' ||amount.charAt(i)>'9') {
                writer.println(" invalid id format");
                return false;
            }
        }
        return true;
    }
    private boolean checkPayBillInput(String paymentNumT,String billNumT,String passwordT,int amountT){
        String amount=String.valueOf(amountT);
        for (int i = 0; i <amount.length(); i++) {
            if(amount.charAt(i)<'0' ||amount.charAt(i)>'9') {
                writer.println(" invalid amount format");
                return false;
            }
        }
        for (int i = 0; i <paymentNumT.length(); i++) {
            if(paymentNumT.charAt(i)<'0' ||paymentNumT.charAt(i)>'9') {
                writer.println(" invalid payment number format");
                return false;
            }
        }
        for (int i = 0; i <billNumT.length(); i++) {
            if(billNumT.charAt(i)<'0' ||billNumT.charAt(i)>'9') {
                writer.println(" invalid bill number format");
                return false;
            }
        }
        return true;
    }
    private boolean checkApplyLoanInput(String amount,String repayment){
        for (int i = 0; i < amount.length(); i++) {
            if(amount.charAt(i)>'9' || amount.charAt(i)<'0'){
                writer.println(" wrong amount format");
                return false;
            }
        }
        for (int i = 0; i < repayment.length(); i++) {
            if(repayment.charAt(i)>'9' || repayment.charAt(i)<'0'){
                writer.println(" wrong amount format");
                return false;
            }
        }
        return true;
    }
}
