package view;

import javax.swing.*;

public class ErrorException extends Exception {
    private String errorMassage;
    public ErrorException(String errorMassage){
        this.errorMassage=errorMassage;
        ErrorException.showError(errorMassage);
    }
    public static void showError(String error) {
        JOptionPane.showMessageDialog(null, error, "Error occurred", JOptionPane.ERROR_MESSAGE);
    }
}
