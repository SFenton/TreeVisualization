import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;


public class Node<T> {
        T data;
        Node<T> parent;
        List<Node<T>> children;
        List<Order> order;
        
        public String toString() {
            return data.toString();
        }
    }