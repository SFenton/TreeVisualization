/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 

 
/**
 * This application that requires the following additional files:
 *   TreeDemoHelp.html
 *    arnold.html
 *    bloch.html
 *    chan.html
 *    jls.html
 *    swingtutorial.html
 *    tutorial.html
 *    tutorialcont.html
 *    vm.html
 */
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
 
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
 
import java.net.URL;
import java.util.ArrayList;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
 
public class TreeDemo extends JPanel
                      implements TreeSelectionListener {
    private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;
    private static boolean DEBUG = false;
 
    //Optionally play with line styles.  Possible values are
    //"Angled" (the default), "Horizontal", and "None".
    private static boolean playWithLineStyle = false;
    private static String lineStyle = "Horizontal";
     
    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = false;
 
    public TreeDemo() throws IOException {
        super(new GridLayout(1,0));
       
 
        //Create the nodes.
        Node testNode = new Node();
        testNode.data = ".*";
        testNode.children = new ArrayList<Node>();
        testNode.order = new ArrayList<Order>();
        
        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode(testNode);
        createNodes(top);
        
        TreeParser parser = new TreeParser("C:\\Users\\Stephen Fenton\\Desktop\\Debaser - Pixies\\outtest.rif");
 
        //Create a tree that allows one selection at a time.
        tree = parser.getTree();
        
        createNodes((DefaultMutableTreeNode)tree.getModel().getRoot());
        
        tree = new JTree((DefaultMutableTreeNode)tree.getModel().getRoot());
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
 
        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);
        
        TreeWriter writer = new TreeWriter(tree, "sdfsdfds");
        writer.WriteTree();
 
        if (playWithLineStyle) {
            System.out.println("line style = " + lineStyle);
            tree.putClientProperty("JTree.lineStyle", lineStyle);
        }
 
        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);
 
        //Create the HTML viewing pane.
        /*htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        initHelp();
        JScrollPane htmlView = new JScrollPane(htmlPane);*/
 
        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        //splitPane.setBottomComponent(htmlView);
 
        Dimension minimumSize = new Dimension(100, 50);
        //htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100); 
        splitPane.setPreferredSize(new Dimension(500, 300));
 
        //Add the split pane to this panel.
        add(splitPane);
    }
 
    /** Required by TreeSelectionListener interface. */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();
 
        if (node == null) return;
 
        /*Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            Node book = (Node)nodeInfo;
            displayURL(book.toString());
            if (DEBUG) {
                System.out.print(book.bookURL + ":  \n    ");
            }
        } else {
            displayURL(helpURL); 
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }*/
    }
 
    /**
     * Read in the nodes here, create the data structure appropriately
     * @param top
     * @throws IOException 
     */
    private void createNodes(DefaultMutableTreeNode top) throws IOException {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;
 
        Node node = new Node();
        node.data = "(.*)\\((\\d{1,4})\\).(.*),(.*),(.*,.*),(.*,.*)(.*)";
        node.children = new ArrayList<Node>();
        node.parent = (Node) top.getUserObject();
        node.order = new ArrayList<Order>();
        
        category = new DefaultMutableTreeNode(node);
        top.add(category);
        
        node = new Node();
        node.data = "(.*)\\((\\d{1,4})\\).(.*),(.*),(.*,.*),(.*, \\d{1,4})";
        node.order = new ArrayList<Order>();
        
        node.order.add(Order.Author);
		node.order.add(Order.Year);
		node.order.add(Order.Title);
		node.order.add(Order.Publication);
		node.order.add(Order.Location);
		node.order.add(Order.Date);
        book = new DefaultMutableTreeNode(node);
        category.add(book);
        
        node = new Node();
        node.data = "(.*)\\((.*)\\).(.*),(.*),(.*,.*,.*),(.*\\d{1,4})";
        node.order = new ArrayList<Order>();
        
        node.order.add(Order.Author);
		node.order.add(Order.Year);
		node.order.add(Order.Title);
		node.order.add(Order.Publication);
		node.order.add(Order.Location);
		node.order.add(Order.Date);
        book = new DefaultMutableTreeNode(node);
        category.add(book);
        
        node = new Node();
        node.data = "Test";
        node.children = new ArrayList<Node>();
        node.parent = (Node) top.getUserObject();
        node.order = new ArrayList<Order>();
        
        category = new DefaultMutableTreeNode(node);
        top.add(category);
        
        node = new Node();
        node.data = "Test 2";
        node.order = new ArrayList<Order>();
        
        node.order.add(Order.Author);
		node.order.add(Order.Year);
		node.order.add(Order.Title);
		node.order.add(Order.Publication);
		node.order.add(Order.Location);
		node.order.add(Order.Date);
        book = new DefaultMutableTreeNode(node);
        category.add(book);
    }
         
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     * @throws IOException 
     */
    private static void createAndShowGUI() throws IOException {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }
 
        //Create and set up the window.
        JFrame frame = new JFrame("TreeDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.add(new TreeDemo());
        
        // Creates a menubar for a JFrame
        JMenuBar menuBar = new JMenuBar();
        
        // Define and add two drop down menu to the menubar
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        
        // Create and add simple menu item to one of the drop down menu
        JMenuItem newAction = new JMenuItem("Import Tree Structure");
        JMenuItem openAction = new JMenuItem("Export Tree Structure");
        JMenuItem refreshAction = new JMenuItem("Refresh Tree Structure");
        JMenuItem exitAction = new JMenuItem("Exit");
        
        fileMenu.add(newAction);
        fileMenu.add(openAction);
        fileMenu.add(refreshAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);

        frame.add(menuBar, BorderLayout.NORTH);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					createAndShowGUI();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
}