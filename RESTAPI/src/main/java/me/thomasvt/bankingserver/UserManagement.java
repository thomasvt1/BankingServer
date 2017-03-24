package me.thomasvt.bankingserver;

import java.sql.Statement;

class UserManagement {

    /*
    Method to add new user to the database
     */
    void createUser(String pin, int cardID, double money, String firstName, String lastName) {
        try {
            String hashedPin = new Hashing().hashString(pin);

            int newUserID = getNewUserID();

            Statement stmt = App.getDatabase().getStmt();

            stmt.addBatch("INSERT INTO `user` (`userid`) VALUES ("+newUserID+");");
            //stmt.addBatch("INSERT INTO `userinfo` (`userid`, `firstname`, `lastname`) VALUES ('"+newUserID+"', '"+firstName+"', '"+lastName+"');");
            stmt.addBatch("INSERT INTO `card` (`userid`, `cardid`, `pinhash`, `trys`, `blocked`) VALUES ('"+newUserID+"', '"+cardID+"', '"+hashedPin+"', '0', '0');");
            //stmt.addBatch("INSERT INTO `money` (`userid`, `balance`) VALUES ('"+newUserID+"', '"+money+"');");
            stmt.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addMoney(int user, int money) {
        String sql = "UPDATE `money` SET `balance` = balance + '"+money+"' WHERE `money`.`userid` ="+user+";";

        App.getDatabase().createStatement(sql);
    }

    void removeMoney(String cardid, double money) {
        String sql = "UPDATE account,card SET balance = balance - "+money+" WHERE account.accountid=card.accountid AND card.carduuid='"+cardid+"'";

        App.getDatabase().createStatement(sql);
    }

    boolean accountExists(String cardid) {
    	String sql = "SELECT accountid FROM `card` WHERE `carduuid` LIKE '"+cardid+"'";
        String s = App.getDatabase().selectStatement(sql);

        return s != null;
    }

    private int getNewUserID() {
        String sql = "SELECT MAX(`userid`) FROM user;";
        String s = App.getDatabase().selectStatement(sql);

        if (s == null)
            return 1;

        try {
            return Integer.parseInt(s) + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    double getBalance(String cardid) {
        String sql = "SELECT balance FROM card, account WHERE card.accountid=account.accountid AND carduuid='" + cardid + "'";
        String s = App.getDatabase().selectStatement(sql);

        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
    }

    String getFirstName(int user) {
        String sql = "SELECT `firstname` FROM `userinfo` WHERE `userid` = "+user+"";
        return App.getDatabase().selectStatement(sql);
    }

    String getLastName(int user) {
        String sql = "SELECT `lastname` FROM `userinfo` WHERE `userid` = "+user+"";
        return App.getDatabase().selectStatement(sql);
    }

    String getPinHash(String cardid) {
        String sql = "SELECT `pinhash` FROM `card` WHERE `carduuid` = '"+cardid+"'";
        return App.getDatabase().selectStatement(sql);
    }

    void setPin(int user, String pin) {
        try {
            String hashedPin = new Hashing().hashString(pin);
            String sql = "UPDATE `card` SET `pinhash` = '"+hashedPin+"' WHERE `card`.`userid` = "+user+"";

            App.getDatabase().createStatement(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}