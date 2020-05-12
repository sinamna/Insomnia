import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * handles some menu-related events
 */
public class MenuHandler {
    /**
     * flips specified panel card to the top in layout
     */
    public static class MenuItemHandler implements ActionListener {
        private CardLayout layout;
        private JPanel mainPanel;

        /**
         * constructs listener with layout and panel given to it
         * @param layout the layout if the panel
         * @param mainPanel the panel with given layout
         */
        public MenuItemHandler(CardLayout layout, JPanel mainPanel) {
            this.layout = layout;
            this.mainPanel = mainPanel;
        }

        /**
         * flips the card(panels) based on their name
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem menuItem = (JMenuItem) e.getSource();
            // uses the text of menuItem to flip the panel top of layout
            layout.show(mainPanel, menuItem.getText());
        }
    }

    /**
     * flips specified panel on the layout
     */
    public static class MenuSelectionHandler implements MenuListener {
        private JPanel mainPanel;
        private CardLayout layout;

        /**
         * constructs a menuListener with given layout and panel
         * @param layout
         * @param mainPanel
         */
        public MenuSelectionHandler(CardLayout layout, JPanel mainPanel) {
            this.layout = layout;
            this.mainPanel = mainPanel;
        }

        /**
         * Invoked when a menu is selected.
         * and flips the panel on top with panel matches the menu's name
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
