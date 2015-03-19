package tree.regex.components;
import java.util.ArrayList;
import java.util.List;


public class Node<T> {
        public T data;
        Node<T> parent;
        List<Node<T>> children;
        public List<Order> order = new ArrayList<Order>();
        
        /**
         * Get the string representing node data (regex).
         * 
         * @return String representing data.
         */
        public String getRegexString() {
        	if (data == null) {
        		return "";
        	}
        	return data.toString();
        }
        
        /**
         * Get a comma delimited string representing
         * the {@link Order} values in node order list.
         * 
         * @return String representing node order list.
         */
        public String getOrderString() {
        	if (order == null || order.isEmpty()) {
        		return "";
        	}
        	
        	// Build string of order values
        	StringBuilder builder = new StringBuilder("");
        	for (int i = 0; i < order.size(); i++) {
        		builder.append(order.get(i).name());
        		
        		// Delimit by comma unless the last order element
        		if (i != order.size() -1) {
        			builder.append(",");
        		}
        	}
        	return builder.toString();
        }
        
        public String toString() {
            return data.toString();
        }
}