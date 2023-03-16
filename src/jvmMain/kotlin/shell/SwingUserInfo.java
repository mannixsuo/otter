package shell;

import com.jcraft.jsch.UserInfo;

import javax.swing.*;

public class SwingUserInfo implements UserInfo {

    private String password;
    private String myPassphrase = null;

    private JTextField myPasswordField = new JPasswordField(20);

    @Override
    public String getPassphrase() {
        return myPassphrase;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(String message) {
        final Object[] ob = {myPasswordField};
        final int result = JOptionPane.showConfirmDialog(null, ob, message,
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            password = myPasswordField.getText();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }

    @Override
    public boolean promptYesNo(String message) {
        final Object[] options = {"yes", "no"};
        final int foo = JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                options, options[0]);
        return foo == 0;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
