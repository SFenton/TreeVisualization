import java.nio.ByteBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/// <summary>
/// Class for parsing string tables.
/// </summary>
public class StringTableParser
{

    /// <summary>
    /// Generates a key-value map of string keys to string values.
    /// </summary>
    /// <param name="chunkRawData">Raw string table.</param>
    /// <param name="index_original">Current index in the string table.</param>
    /// <param name="countEntries">Number of entries in the table.</param>
    /// <returns></returns>
    public List<SimpleEntry<Long, String>>generateDictionary(byte[] chunkRawData, int index_original, int countEntries)
    {
        int index = index_original;
        int offset = index + (countEntries * 16);
        List<SimpleEntry<Long, String>> stringTable_local = new ArrayList<SimpleEntry<Long, String>>();

        for (int q = 0; q < countEntries; q++)
        {
            // Get the string key
            long stringKey;
            byte[] stringKey_byte = new byte[8];
            int i = index;

            for (; index < i + 8; index++)
            {
                stringKey_byte[index - i] = chunkRawData[index];
            }

            ByteBuffer buffer = ByteBuffer.wrap(stringKey_byte);
            stringKey = buffer.getLong();

            // Get the offset
            int tableOffset;
            byte[] tableOffset_byte = new byte[4];
            i = index;

            for (; index < i + 4; index++)
            {
                tableOffset_byte[index - i] = chunkRawData[index];
            }

            buffer = ByteBuffer.wrap(tableOffset_byte);
            tableOffset = buffer.getInt();

            // Skip the next four bytes
            index += 4;

            // Get the string from the table
            int tempOffset = offset + tableOffset;
            String result = "";
            List<Byte> result_byte = new ArrayList<Byte>();

            while (chunkRawData[tempOffset] != 0x0)
            {
                result_byte.add(chunkRawData[tempOffset]);
                tempOffset++;
            }

            Byte[] bytes = result_byte.toArray(new Byte[result_byte.size()]);
            
            byte[] b2 = new byte[bytes.length];
            for (int k = 0; k < bytes.length; k++)
            {
                b2[k] = bytes[k];
            }
            
            result = new String(b2);
            
            // Attempt to add the string
            try
            {
                stringTable_local.add(new SimpleEntry<Long, String>(stringKey, result));
            }
            catch (Exception e)
            {

            }
        }

        return stringTable_local;
    }
}