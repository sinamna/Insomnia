import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuHandler {
    /**
     * flips specified panel card to the top in layout
     */
    public static class MenuItemHandler implements ActionListener {
        private CardLayout layout;
        private JPanel mainPanel;

        public MenuItemHandler(CardLayout layout, JPanel mainPanel) {
            this.layout = layout;
            this.mainPanel = mainPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            layout.show(mainPanel, menuItem.getText());
        }
    }

    public static class MenuSelectionHandler implements MenuListener {
        private JPanel mainPanel;
        private CardLayout layout;

        public MenuSelectionHandler(CardLayout layout, JPanel mainPanel) {
            this.layout = layout;
            this.mainPanel = mainPanel;
        }

        /**
         * Invoked when a menu is selected.
         *
         * @param e a MenuEvent object
         */
        @Override
        public void menuSelected(MenuEvent e) {
            JMenu menu = (JMenu) e.getSource();
            layout.show(mainPanel, menu.getText());
        }

        /**
         * Invoked when the menu is deselected.
         *
         * @param e a MenuEvent object
         */
        @Override
        public void menuDeselected(MenuEvent e) {

        }

        /**
         * Invoked when the menu is canceled.
         *
         * @param e a MenuEvent object
         */
        @Override
        public void menuCanceled(MenuEvent e) {

        }
    }
}
