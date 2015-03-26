package gui.application.components;
import java.util.ArrayList;
import java.util.List;

import tree.regex.components.Node;
import tree.regex.components.Order;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * Class defining the behavior of a cell in a tree view.
 * This class allows tree cells to be edited with two text
 * fields.
 */
public final class NodeTreeCellImpl extends TreeCell<Node<String>> {

	/**
	 * {@link TextField} used to edit the regex field.
	 */
	private TextField regexTextField;
	/**
	 * {@link TextField} used to edit the Order field.
	 */
	private TextField orderTextField;
	/**
	 * VBox used to hold the edit text fields.
	 */
	private VBox editVbox;
	/**
	 * Context Menu to appear on right click of an internal node cell
	 */
	private ContextMenu internalMenu = new ContextMenu();
	/**
	 * Context Menu to appear on right click on a leaf node cell
	 */
	private ContextMenu leafMenu = new ContextMenu();

	/**
	 * {@link TextFieldTreeCellImpl} Constructor.
	 */
	public NodeTreeCellImpl() {
		// Setup context menu for internal nodes
		MenuItem internalAddMenuItem = new MenuItem("Add Child Node");
		internalMenu.getItems().add(internalAddMenuItem);
		internalAddMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Node<String> newRegexNode = new Node<String>();
				newRegexNode.data = "New Regex Node";
				TreeItem<Node<String>> newRegex = new TreeItem<Node<String>>(
						newRegexNode);
				getTreeItem().getChildren().add(newRegex);
			}
		});
		
		// Setup context menu for leaf nodes
		MenuItem leafAddMenuItem = new MenuItem("Add Child Node");
		leafMenu.getItems().add(leafAddMenuItem);
		leafAddMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				Node<String> newRegexNode = new Node<String>();
				newRegexNode.data = "New Regex Node";
				TreeItem<Node<String>> newRegex = new TreeItem<Node<String>>(
						newRegexNode);
				getTreeItem().getChildren().add(newRegex);
			}
		});
		MenuItem leafRemoveMenuItem = new MenuItem("Remove Leaf Node");
		leafMenu.getItems().add(leafRemoveMenuItem);
		leafRemoveMenuItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				getTreeView().getSelectionModel().getSelectedItem();
				TreeItem<Node<String>> c = (TreeItem<Node<String>>) getTreeView().getSelectionModel().getSelectedItem();
	            c.getParent().getChildren().remove(c);
			}
		});
	}

	/**
	 * Sets up the textField boxes to be used for editing
	 * and sets the cell graphic to the textField boxes.
	 */
	@Override
	public void startEdit() {
		super.startEdit();

		// Prep Vbox and textfields for edit
		if (editVbox == null) {
			createTextFieldsVBox();
		}
		regexTextField.setText(getRegexString());
		orderTextField.setText(getOrderString());
		
		setText(null);
		setGraphic(editVbox);
	}

	/**
	 * Return cell to standard form when edit is canceled.
	 */
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		
		// Ensure that textFields are properly set
		regexTextField.setText(getRegexString());
		orderTextField.setText(getOrderString());
		
		// Appropriately set cell's graphic/text
		setText(getNodeString());
		setGraphic(getTreeItem().getGraphic());
	}

	/**
	 * Update the tree cell text and graphics appropriately
	 * when a change is being made to the cell.
	 */
	@Override
	public void updateItem(Node<String> item, boolean empty) {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (regexTextField != null) {
					regexTextField.setText(getRegexString());
				}
				if (orderTextField != null) {
					orderTextField.setText(getOrderString());
				}
				setText(null);
				setGraphic(editVbox);
			} else {
				setText(getNodeString());
				setGraphic(getTreeItem().getGraphic());
				// When the cell is updated out of edit mode
				// set the context menus back to what it should
				// be for this cell.
				if (getTreeItem().isLeaf() && getTreeItem().getParent() != null) {
					setContextMenu(leafMenu);
				} else {
					setContextMenu(internalMenu);
				}
			}
		}
	}

	/**
	 * Creates the text field boxes to allow for tree cell editing.
	 * Sets up the KeyEvent interactions for the text fields.
	 */
	private void createTextFieldsVBox() {
		// Setup regex text field
		regexTextField = new TextField(getRegexString());
		regexTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					// Save the changes made to the cell's data item
					Node<String> editedNode = new Node<String>();
					editedNode.data = regexTextField.getText();
					editedNode.order = orderListFromString(orderTextField.getText());
					commitEdit(editedNode);
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
		
		// Setup orderTextField
		orderTextField = new TextField(getOrderString());
		orderTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					// Save the changes made to the cell's data item
					Node<String> editedNode = new Node<String>();
					editedNode.data = regexTextField.getText();
					editedNode.order = orderListFromString(orderTextField.getText());
					commitEdit(editedNode);
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
		
		// Place textfields in VBox to display at same time in cell.
		editVbox = new VBox();
		editVbox.getChildren().addAll(regexTextField, orderTextField);
	}

	/**
	 * Get a {@link List} of {@link Order} by parsing a given
	 * string for {@link Order} enums.
	 * 
	 * @param orderString string containing order enums delimited by commas
	 * @return {@link List} of {@link Order} representing enums parsed from
	 * 		the given string.
	 */
	private List<Order> orderListFromString(String orderString) {
		ArrayList<Order> finalOrders = new ArrayList<Order>();
		
		// Return null if there is no orderSring
		if (orderString == null || orderString.equals("")) {
			return finalOrders;
		}
		
		// Try to delimit the order string and get the order list
		String[] delimOrders = orderString.split(",");
		for (String order: delimOrders) {
			finalOrders.add(Order.valueOf(order));
		}
		return finalOrders;
	}
	
	/**
	 * Get the string of the cell item's regex value.
	 * 
	 * @return Empty string if cell has null item, otherwise
	 * 		returns string of cell item's regex value.
	 */
	private String getRegexString() {
		return getItem() == null ? "" : getItem().getRegexString();
	}
	
	/**
	 * Get the string of the cell item's order list values.
	 * 
	 * @return Empty string if the cell has null item, otherwise
	 * 		returns string of cell item's order list values.
	 */
	private String getOrderString() {
		return getItem() == null ? "" : getItem().getOrderString();
	}
	
	/**
	 * Get String to use for TreeView node cell when not editing.
	 * 
	 * @return newline delimited, regex string and order string.
	 */
	private String getNodeString() {
		return getRegexString() + "\n" + getOrderString();
	}
}
