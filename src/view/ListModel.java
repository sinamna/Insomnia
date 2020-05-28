package view;
import model.*;
import javax.swing.*;

public class ListModel extends DefaultListModel<Request> {
    public void updateList(){
        fireContentsChanged(this,0,this.getSize());
    }
}
