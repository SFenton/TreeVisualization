package util.io.binary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javafx.scene.control.TreeItem;

import tree.regex.components.ModifiedNode;
import tree.regex.components.Node;
import tree.regex.components.Order;


public class TreeWriter 
{
	private TreeItem<Node<String>> rootItem;
	private String filepath;
	private List<Long> keyList;
	private List<SimpleEntry<Long, String>> stringTable;
	private static List<ModifiedNode> nodes;
	
	private List<Byte> nobj_byteRep;
	private List<Byte> raw_nobj_data;
	
	private List<Byte> stbl_byteRep;
	private List<Byte> stbl_keys;
	private List<Byte> stbl_table;
	
	public TreeWriter(TreeItem<Node<String>> rootItem, String filepath)
	{
		this.rootItem = rootItem;
		this.filepath = filepath;
		keyList = new ArrayList<Long>();
		stringTable = new ArrayList<SimpleEntry<Long, String>>();
		
		nobj_byteRep = new ArrayList<Byte>();
		raw_nobj_data = new ArrayList<Byte>();
		
		stbl_keys = new ArrayList<Byte>();
		stbl_table = new ArrayList<Byte>();
		stbl_byteRep = new ArrayList<Byte>();
		nodes = new ArrayList<ModifiedNode>();
	}
	
	public void WriteTree() throws IOException
	{
		// Prepare the tree for writing.
		PrepareTree();
		
		// Write the node objects.
		writeNobj();
		
		// Write the string table
		writeStbl();
		
		// Combine the two lists
		stbl_byteRep.addAll(nobj_byteRep);
		
		Byte[] out = stbl_byteRep.toArray(new Byte[stbl_byteRep.size()]);
		byte[] finalOut = new byte[out.length];
		
		for (int i = 0; i < out.length; i++)
		{
			finalOut[i] = out[i];
		}
		
		// Write
		Files.write(Paths.get(filepath), finalOut);
	}

	private void writeStbl() 
	{
		// Write the identifier
		byte[] identifier = new String("STbl").getBytes();
		
		// Add identifier to byte list
		for (int j = 0; j < identifier.length; j++)
		{
			stbl_byteRep.add(identifier[j]);
		}
		
		// Add four bytes for chunk size; modify later
		// Also add zeroed data here
		for (int j = 0; j < 12; j++)
		{
			stbl_byteRep.add((byte)0);
		}
		
		// Add the number of entries to read
		byte[] numEntries = ByteBuffer.allocate(4).putInt(stringTable.size()).array();
		
		for (int j = 0; j < numEntries.length; j++)
		{
			stbl_byteRep.add(numEntries[j]);
		}
		
		// Add four bytes for zeroed data
		for (int j = 0; j < 4; j++)
		{
			stbl_byteRep.add((byte)0);
		}
		
		// Calculate the size of the string table
		int dataLeadingToTable = (stringTable.size() * 16) + 4;
		
		// Put respective data into string lists
		for (int j = 0; j < stringTable.size(); j++)
		{
			SimpleEntry<Long, String> entry = stringTable.get(j);
			
			// Get the byte representation of the key
			byte[] key = ByteBuffer.allocate(8).putLong(entry.getKey()).array();
			
			for (int i = 0; i < key.length; i++)
			{
				stbl_keys.add(key[i]);
			}
			
			// Get the offset to the key
			byte[] keyOffset = ByteBuffer.allocate(4).putInt(stbl_table.size()).array();
			
			for (int i = 0; i < keyOffset.length; i++)
			{
				stbl_keys.add(keyOffset[i]);
			}
			
			// Add zeroed data
			for (int i = 0; i < 4; i++)
			{
				stbl_keys.add((byte)0);
			}
			
			// Add the string to the table
			byte[] string = entry.getValue().getBytes();
			
			for (int i = 0; i < string.length; i++)
			{
				stbl_table.add(string[i]);
			}
			
			// Add terminator
			stbl_table.add((byte)0);
		}
		
		int tableSize = stbl_table.size();
		
		// Add table size
		byte[] tableSize_byte = ByteBuffer.allocate(4).putInt(tableSize).array();
		
		for (int i = 0; i < tableSize_byte.length; i++)
		{
			stbl_byteRep.add(tableSize_byte[i]);
		}
		
		// Add size leading to string table
		byte[] sizeToTable_byte = ByteBuffer.allocate(4).putInt(dataLeadingToTable).array();
		
		for (int i = 0; i < sizeToTable_byte.length; i++)
		{
			stbl_byteRep.add(sizeToTable_byte[i]);
		}
		
		// Add keys
		for (int i = 0; i < stbl_keys.size(); i++)
		{
			stbl_byteRep.add(stbl_keys.get(i));
		}
		
		// Add table
		for (int i = 0; i < stbl_table.size(); i++)
		{
			stbl_byteRep.add(stbl_table.get(i));
		}
		
		// Modify size
		int totalSize = stbl_byteRep.size() - 8;
		byte[] totalSize_byte = ByteBuffer.allocate(4).putInt(totalSize).array();
		
		int j = 4;
		for (int i = 0; i < totalSize_byte.length; i++)
		{
			stbl_byteRep.set(j, totalSize_byte[i]);
			j++;
		}
	}

	private void writeNobj() 
	{
		// Write the identifier
		byte[] identifier = new String("NOBJ").getBytes();
		
		// Add identifier to byte list
		for (int j = 0; j < identifier.length; j++)
		{
			nobj_byteRep.add(identifier[j]);
		}
		
		// Add four bytes for chunk size; modify later
		// Also add zeroed data here
		for (int j = 0; j < 12; j++)
		{
			nobj_byteRep.add((byte)0);
		}
		
		// Add the number of entries to read
		byte[] numEntries = ByteBuffer.allocate(4).putInt(nodes.size()).array();
		
		for (int j = 0; j < numEntries.length; j++)
		{
			nobj_byteRep.add(numEntries[j]);
		}
		
		// Add the entry size
		byte[] entrySize = ByteBuffer.allocate(4).putInt(60).array();
		
		for (int j = 0; j < entrySize.length; j++)
		{
			nobj_byteRep.add(entrySize[j]);
		}
		
		// Add zeroed data
		for (int j = 0; j < 4; j++)
		{
			nobj_byteRep.add((byte)0);
		}
		
		int curPos = nodes.size() - 1;
		// Run through the list.
		for (int i = 0; i < nodes.size(); i++)
		{
			ModifiedNode mNode = nodes.get(i);
			
			// Generate a string key for the data
			long stringKey = generateKey((String)mNode.data);
			
			// Add string key
			byte[] keyByte = ByteBuffer.allocate(8).putLong(stringKey).array();
			for (int j = 0; j < keyByte.length; j++)
			{
				nobj_byteRep.add(keyByte[j]);
			}
			
			// Add zeroed data
			for (int j = 0; j < 4; j++)
			{
				nobj_byteRep.add((byte)0);
			}
			
			// Number of children
			byte[] numChildren = ByteBuffer.allocate(4).putInt(mNode.numChildren).array();
			for (int j = 0; j < numChildren.length; j++)
			{
				nobj_byteRep.add(numChildren[j]);
			}
			
			// Offset to children
			byte[] offsetChildren = ByteBuffer.allocate(4).putInt(44 + (60 * mNode.offset)).array();
			for (int j = 0; j < offsetChildren.length; j++)
			{
				nobj_byteRep.add(offsetChildren[j]);
			}
			
			// Number of elements in order
			byte[] numOrder = ByteBuffer.allocate(4).putInt(mNode.order.size()).array();
			for (int j = 0; j < numOrder.length; j++)
			{
				nobj_byteRep.add(numOrder[j]);
			}
			
			// Offset to order data
			byte[] orderOffset = ByteBuffer.allocate(4).putInt(36 + (curPos * 60) + raw_nobj_data.size()).array();
			for (int j = 0; j < orderOffset.length; j++)
			{
				nobj_byteRep.add(orderOffset[j]);
			}
			
			// Add order data to raw chunk
			for (int j = 0; j < mNode.order.size(); j++)
			{
				int ordinal = Order.valueOf(mNode.order.get(j).toString()).ordinal();
				byte[] ordinalByte = ByteBuffer.allocate(4).putInt(ordinal).array();
				for (int k = 0; k < ordinalByte.length; k++)
				{
					raw_nobj_data.add(ordinalByte[k]);
				}
			}
			
			// Add zeroed data
			for (int j = 0; j < 32; j++)
			{
				nobj_byteRep.add((byte)0);
			}
			
			curPos--;
		}
		
		// Combine lists
		for (int i = 0; i < raw_nobj_data.size(); i++)
		{
			nobj_byteRep.add(raw_nobj_data.get(i));
		}
		
		// Modify size
		int totalSize = nobj_byteRep.size() - 8;
		byte[] totalSize_byte = ByteBuffer.allocate(4).putInt(totalSize).array();
		
		int j = 4;
		for (int i = 0; i < totalSize_byte.length; i++)
		{
			nobj_byteRep.set(j, totalSize_byte[i]);
			j++;
		}
	}

	public void PrepareTree()
	{
		// Add the breadth-first enumeration to the list of modified nodes
		Enumeration<TreeItem<Node<String>>> breadthFirst = breadthFirstEnumeration(rootItem);
		printEnumeration(breadthFirst);
		
		// Go through and set node offset data
		for (int i = 0; i < nodes.size(); i++)
		{
			ModifiedNode mNode = nodes.get(i);
			int numChildren = mNode.numChildren;
			
			if (numChildren > 0)
			{
				// Find first non-flagged element
				int j = i + 1;
				
				while (j < nodes.size() && nodes.get(j).flagChild == true)
				{
					j++;
				}
				
				int temp = j + numChildren;
				for (; j < temp; j++)
				{
					ModifiedNode setChild = nodes.get(j);
					setChild.flagChild = true;
					nodes.set(j, setChild);
				}
				
				mNode.offset = j - numChildren - i - 1;
			}
			else
			{
				mNode.offset = 0;
			}
			
			nodes.set(i, mNode);
		}
	}
	
	/**
	 * Get a Breadth First Enumeration of a Tree given a starting {@link TreeItem} root node.
	 * 
	 * @param rootItem The {@link TreeItem} to start the BFE from
	 * @return An {@link Enumeration} containing all nodes in BF ordering starting from the given
	 * 		root node.
	 */
	private Enumeration<TreeItem<Node<String>>> breadthFirstEnumeration(TreeItem<Node<String>> rootItem) {
		// Maintain a vector for TreeItems as we go along
		Vector<TreeItem<Node<String>>> items = new Vector<TreeItem<Node<String>>>();
		items.add(rootItem);
		
		for (int i = 0; i < items.size(); i++) {
			for (TreeItem<Node<String>> item : items.get(i).getChildren()) {
				items.add(item);
			}
		}
		return items.elements();
	}
	
	@SuppressWarnings("rawtypes")
	private static void printEnumeration(Enumeration<TreeItem<Node<String>>> e) {
	    while (e.hasMoreElements()) {
	      ModifiedNode mNode = new ModifiedNode();
	      TreeItem<Node<String>> node = (TreeItem<Node<String>>) e.nextElement();
	      mNode.data = (node.getValue()).data;
	      mNode.numChildren = node.getChildren().size();
	      mNode.order = (node.getValue()).order;
	      
	      nodes.add(mNode);
	    }
	  }
	
	/**
	 * Generates a key for the string table and node objects.
	 * @param value The value for the SimpleEntry.
	 * @return Key not present.
	 */
	private long generateKey(String value)
	{
		// Generate a random long.
		Random random = new Random();
		long key = random.nextLong();
		
		// If the list contains the long, regenerate.
		while (keyList.contains(key))
		{
			key = random.nextLong();
		}
		
		// Add the key, and SimpleEntry.
		keyList.add(key);
		stringTable.add(new SimpleEntry<Long, String>(key, value));
		
		return key;
	}

}
