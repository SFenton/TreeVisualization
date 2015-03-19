package util.io.binary;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TreeItem;

import tree.regex.components.Node;
import tree.regex.components.Order;

/**
 * Parses a binary file into a tree structure for visualization.
 * 
 * @author Stephen Fenton
 * @date 03/16/2015
 */
public class TreeParser
{
    // All objects found in the binary.
    public StringTable engParser;
    public StringTableParser parser;
    
    // The Parsed Root Node
    private TreeItem<Node<String>> rootItem;

    /**
     * Constructor for the class.  Initializes parsing.
     * @param filePath Path to the binary.
     * @throws IOException In event of read/write fail.
     */
    public TreeParser(String filePath) throws IOException
    {
    	// Read the file into a byte array
    	Path path = Paths.get(filePath);
        byte[] fileBytes = Files.readAllBytes(path);

        parser = new StringTableParser();
        stblParser(fileBytes);
        zobjParser(fileBytes);
    }

    /**
     * Returns the string associated with the provided key.
     * @param p The key to find a string with.
     * @return The string associated with key p.
     */
    public String FindValue(long p)
    {
        for (int i = 0; i < engParser.stringTable.size(); i++ )
        {
            if (engParser.stringTable.get(i).getKey() == p)
            {
                return engParser.stringTable.get(i).getValue();
            }
        }

        return null;
    }

    /**
     * Searches for object chunks to parse.
     * @param indexBytes The file, in bytes, to parse.
     * @throws IOException If file input or writing fails.
     */
    private void zobjParser(byte[] indexBytes) throws IOException
    {
    	int index = 0;
        while (index < indexBytes.length)
        {
            String chunkType = "";
            byte[] chunkType_byte = new byte[4];
            byte[] chunkSize = new byte[4];
            int chunkSize_int;

            // Get the chunk type
            int i = index;
            for (; index < i + 4; index++)
            {
                chunkType_byte[index - i] = indexBytes[index];
            }

            chunkType = new String(chunkType_byte);

            // Get the chunk size
            i = index;
            for (; index < i + 4; index++)
            {
                chunkSize[index - i] = indexBytes[index];
            }

            ByteBuffer buffer = ByteBuffer.wrap(chunkSize);
            chunkSize_int = buffer.getInt();

            // Get the raw data
            byte[] chunkRawData = new byte[chunkSize_int];
            i = index;

            for (; index < i + chunkSize_int; index++)
            {
                chunkRawData[index - i] = indexBytes[index];
            }

            switch (chunkType)
            {
                case "NOBJ":
                    nobjHelper(chunkRawData);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Parses the object chunk into a suitable tree format.
     * @param chunkRawData The raw data to parse.
     */
    private void nobjHelper(byte[] chunkRawData)
    {
        int index = 0;
        int i = index;

        // Skip zeroed data
        index += 8;

        // Get the number of entries
        int numEntries;
        byte[] numEntries_byte = new byte[4];
        i = index;

        for (; index < i + 4; index++)
        {
            numEntries_byte[index - i] = chunkRawData[index];
        }

        ByteBuffer buffer = ByteBuffer.wrap(numEntries_byte);
        numEntries = buffer.getInt();

        // Get the entry size
        i = index;
        byte[] entrySize_byte = new byte[4];
        int entrySize;
        
        for (; index < i + 4; index++)
        {
        	entrySize_byte[index - i] = chunkRawData[index];
        }
        
        buffer = ByteBuffer.wrap(entrySize_byte);
        entrySize = buffer.getInt();

        // Skip zeroed data
        index += 4;

        generateEntries(chunkRawData, index, numEntries);
    }

    /**
     * Generates the JTree for use by the GUI.
     * @param chunkRawData The raw chunk to parse through.
     * @param index_original The index to parse from.
     * @param numEntries Number of entries in the chunk.
     * @return The generated JTree.
     */
    private void generateEntries(byte[] chunkRawData, int index_original, int numEntries)
    {
        rootItem = nodeGenerator(chunkRawData, index_original);
    }

    /**
     * Recursive method to generate the tree from the binary.
     * @param chunkRawData Chunk to parse through.
     * @param index_original Index to start at.
     * @return Node of the tree.
     */
    private TreeItem<Node<String>> nodeGenerator(byte[] chunkRawData, int index_original) 
    {
    	TreeItem<Node<String>> node = new TreeItem<Node<String>>();
    	
    	Node innerNode = new Node();
    	
    	int index = index_original;
    	int i = index;
    	
    	// Get the string data, set the data
    	long stringKey;
    	byte[] stringKey_byte = new byte[8];
    	
    	for (; index < i + 8; index++)
    	{
    		stringKey_byte[index - i] = chunkRawData[index];
    	}
    	
    	ByteBuffer buffer = ByteBuffer.wrap(stringKey_byte);
    	stringKey = buffer.getLong();
    	innerNode.data = FindValue(stringKey);
    	
    	// Skip zeroed data
    	index += 4;
    	
    	// Get the number of children
    	int numChildren;
    	byte[] numChildren_byte = new byte[4];
    	i = index;
    	
    	for (; index < i + 4; index++)
    	{
    		numChildren_byte[index - i] = chunkRawData[index];
    	}
    	
    	buffer = ByteBuffer.wrap(numChildren_byte);
    	numChildren = buffer.getInt();
    	
    	// Get the offset to the children elements
    	int childrenOffset = index;
    	byte[] childrenOffset_byte = new byte[4];
    	i = index;
    	
    	for (; index < i + 4; index++)
    	{
    		childrenOffset_byte[index - i] = chunkRawData[index];
    	}
    	
    	buffer = ByteBuffer.wrap(childrenOffset_byte);
    	childrenOffset += buffer.getInt();
    	
    	// Get the number of elements in the order list
    	int numOrder;
    	byte[] numOrder_byte = new byte[4];
    	i = index;
    	
    	for (; index < i + 4; index++)
    	{
    		numOrder_byte[index - i] = chunkRawData[index];
    	}
    	
    	buffer = ByteBuffer.wrap(numOrder_byte);
    	numOrder = buffer.getInt();
    	
    	// Get the offset to the beginning of the order data
    	int orderOffset = index;
    	byte[] orderOffset_byte = new byte[4];
    	i = index;
    	
    	for (; index < i + 4; index++)
    	{
    		orderOffset_byte[index - i] = chunkRawData[index];
    	}
    	
    	buffer = ByteBuffer.wrap(orderOffset_byte);
    	orderOffset += buffer.getInt();
    	
    	// Get a list of order elements, add to node
    	List<Order> orderList = new ArrayList<Order>();
    	int tempIndex = orderOffset;
    	
    	for (int j = 0; j < numOrder; j++)
    	{
    		int temp_i = tempIndex;
    		Order result;
    		byte[] key_byte = new byte[4];
    		
    		for (; tempIndex < temp_i + 4; tempIndex++)
    		{
    			key_byte[tempIndex - temp_i] = chunkRawData[tempIndex];
    		}
    		
    		buffer = ByteBuffer.wrap(key_byte);
    		result = Order.values()[buffer.getInt()];
    		
    		orderList.add(result);
    	}
    	
    	innerNode.order = orderList;
    	
    	// We disregard children in the TreeParser class, as they aren't necessary.
    	// The parser will act differently in TextTransformation project.
    	
    	// Add the children to the node recursively.
    	
    	//TODO: We should use a different class for storing data in the tree later.
    	// Right now we are storing a Node object in the TreeView's own nodes (TreeItem).
    	// Only the data and order variables are needed in Node, this should just be its
    	// own data object class.
    	node.setValue(innerNode);
    	
    	for (int j = 0; j < numChildren; j++)
    	{
    		node.getChildren().add(nodeGenerator(chunkRawData, childrenOffset + (j * 60)));
    	}
    	
		return node;
	}

	/**
     * Called to parse out the string table present in the binary file.
     * @param indexBytes The file being parsed, in byte format.
     * @throws IOException In the event of a failure to write information.
     */
    private void stblParser(byte[] indexBytes) throws IOException
    {
        int index = 0;
        while (index < indexBytes.length)
        {
            String chunkType = "";
            byte[] chunkType_byte = new byte[4];
            byte[] chunkSize = new byte[4];
            int chunkSize_int;

            // Get the chunk type
            int i = index;
            for (; index < i + 4; index++)
            {
                chunkType_byte[index - i] = indexBytes[index];
            }

            chunkType = new String(chunkType_byte);

            // Get the chunk size
            i = index;
            for (; index < i + 4; index++)
            {
                chunkSize[index - i] = indexBytes[index];
            }

            ByteBuffer buffer = ByteBuffer.wrap(chunkSize);
            chunkSize_int = buffer.getInt();

            // Get the raw data
            byte[] chunkRawData = new byte[chunkSize_int];
            i = index;

            for (; index < i + chunkSize_int; index++)
            {
                chunkRawData[index - i] = indexBytes[index];
            }

            switch (chunkType)
            {
                case "STbl":
                    stblHelper(chunkRawData);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Once a string table section is found, we call the string table helper to parse it.
     * @param chunkRawData The chunk to parse.
     * @throws IOException In the event of a failure to write information.
     */
    private void stblHelper(byte[] chunkRawData) throws IOException
    {
        // Skip zeroed data
        int index = 8;

        // Get the count of entries
        int countEntries;
        byte[] countEntries_byte = new byte[4];
        int i = index;

        for (; index < i + 4; index++)
        {
            countEntries_byte[index - i] = chunkRawData[index];
        }

        ByteBuffer buffer = ByteBuffer.wrap(countEntries_byte);
        countEntries = buffer.getInt();

        // Skip over zeroed data
        index += 4;

        // Get the size of the String table
        int tableSize;
        byte[] tableSize_byte = new byte[4];
        i = index;

        for (; index < i + 4; index++)
        {
            tableSize_byte[index - i] = chunkRawData[index];
        }

        buffer = ByteBuffer.wrap(tableSize_byte);
        tableSize = buffer.getInt();

        // Get the size of data leading up to the table
        int sizeKeys;
        byte[] sizeKeys_byte = new byte[4];
        i = index;

        for (; index < i + 4; index++)
        {
            sizeKeys_byte[index - i] = chunkRawData[index];
        }

        buffer = ByteBuffer.wrap(sizeKeys_byte);
        sizeKeys = buffer.getInt();

        // Switch based on language
        engParser = new StringTable();
        engParser.sizeTable = tableSize;
        engParser.sizeLeadingUpToTable = sizeKeys;
        engParser.stringTable = parser.generateDictionary(chunkRawData, index, countEntries);
        engParser.numEntries = engParser.stringTable.size();
    }
    
    /**
     * Returns the parsed JTree.
     * @return The tree.
     */
    public TreeItem<Node<String>> getRootItem()
    {
    	return rootItem;
    }
}
