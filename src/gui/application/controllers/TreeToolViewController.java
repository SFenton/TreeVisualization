package gui.application.controllers;
import gui.application.components.NodeTreeCellImpl;

import java.io.File;
import java.io.IOException;

import tree.regex.components.Node;
import util.io.binary.TreeParser;
import util.io.binary.TreeWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controller Class for the tree_tool_view.
 * Handles actions performed on the view, and updates the view
 * with relevant data.
 * 
 * @author Dustin Thompson
 */
public class TreeToolViewController {
	/**
	 * Stage used to show TreeTool fxml.
	 */
	private Stage primaryStage;
	/**
	 * The {@link TreeView} storing all Regex nodes.
	 */
    @FXML
    private TreeView<Node<String>> tree;
    /**
     * Root {@link TreeItem} Node of the Regex {@link TreeView}.
     */
    @FXML
    private TreeItem<Node<String>> rootItem;
    /**
     * Opens File Explorer for OS to select a particular file.
     */
    private FileChooser fileChooser = new FileChooser();
    
    /**
     * Automatically called FXML initialization function to setup
     * variables dependent on FXML gui components.
     */
    public void initialize() {
    	// Setup tree view to use editable text nodes
    	tree.setCellFactory(new Callback<TreeView<Node<String>>,TreeCell<Node<String>>>(){
            @Override
            public TreeCell<Node<String>> call(TreeView<Node<String>> p) {
                return new NodeTreeCellImpl();
            }
        });
    	// Setup root node
    	Node<String> rootValue = new Node<String>();
    	rootValue.data = "New Regex Node";
    	rootItem = new TreeItem<Node<String>>(rootValue);
    	tree.setRoot(rootItem);
    }
    
    /**
     * Additional Initialization function to allow passing
     * of controller shared data.
     * 
     * @param primaryStage stage to set scene on and to open
     * 		dialogs on.
     */
    public void initData(Stage primaryStage) {
    	this.primaryStage = primaryStage;
    }
    
    /**
     * Handler for the 'Import Binary' MenuItem.
     * 
     * @param event - the event that triggered the handler
     */
    @FXML
    protected void handleImportAction(ActionEvent event) {
    	// Configure the file chooser before opening
		fileChooser.setTitle("Import Binary File");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				 new FileChooser.ExtensionFilter(".RGXT", "*.rgxt"));
		
		File binaryFile = fileChooser.showOpenDialog(primaryStage);
		if (binaryFile != null) {
			try {
				TreeParser parser = new TreeParser(binaryFile.getAbsolutePath());
				rootItem = parser.getRootItem();
				tree.setRoot(rootItem);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    /**
     * Handler for the 'Export Binary' MenuItem.
     * 
     * @param event - the event that triggered the handler
     */
    @FXML
    protected void handleExportAction(ActionEvent event) {
    	// Configure file chooser before opening
    	fileChooser.setTitle("Export Binary File");
    	fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    	fileChooser.getExtensionFilters().addAll(
    			new FileChooser.ExtensionFilter(".RGXT", "*.rgxt"));
    	
    	File exportFile = fileChooser.showSaveDialog(primaryStage);
    	if (exportFile != null) {
    		try {
    			TreeWriter writer = new TreeWriter(rootItem, exportFile.getAbsolutePath());
				writer.WriteTree();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

}

