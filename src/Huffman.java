import java.util.*;
import java.io.*;

public class Huffman {
    private static StringBuilder sb = new StringBuilder();
    private static Map<Byte, String> huffmap = new HashMap<>();

    //-----------------------Encoding the File------------------// 
    //---------Read & Create the File-------------//
    public static void compress(String src, String dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            byte[] b = new byte[inStream.available()];
            inStream.read(b);
            byte[] huffmanBytes = createZip(b);
            OutputStream outStream = new FileOutputStream(dst);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(huffmanBytes);
            objectOutStream.writeObject(huffmap);
            inStream.close();
            objectOutStream.close();
            outStream.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    //-------End------------//

    //--------Compressing the File-------//
    private static byte[] createZip(byte[] bytes) throws Exception {
        Heap<Node> nodes = getNodes(bytes);
        Node root = createHuffmanTree(nodes);
        Map<Byte, String> huffmanCodes = getHuffCodes(root);
        byte[] huffmanCodeBytes = zipBytesWithCodes(bytes, huffmanCodes);
        return huffmanCodeBytes;
    }
    //-------End--------//

    //---------Create and Put Nodes in minHeap----------//
    private static Heap<Node> getNodes(byte[] bytes) {
        Heap<Node> minHeap = new Heap<Node>();
        Map<Byte, Integer> tempMap = new HashMap<>();
        for (byte b : bytes) {
            Integer value = tempMap.get(b);
            if (value == null)
                tempMap.put(b, 1);
            else
                tempMap.put(b, value + 1);
        }
        for (Map.Entry<Byte, Integer> entry : tempMap.entrySet()){
            Node newNode=new Node(entry.getKey(),entry.getValue());
            minHeap.insert(newNode);
        }
        return minHeap;
    }
    //------End--------//

    //-------Reducing Tree to single Node---------//
    private static Node createHuffmanTree(Heap<Node> nodes) throws Exception {
        while (nodes.size() > 1) {
            Node left = nodes.remove();
            Node right = nodes.remove();
            Node parent = new Node(null, left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            nodes.insert(parent);
        }
        return nodes.remove();
    }
    //-------End---------//

    //-------Creating Huffman Map----------//
    private static Map<Byte, String> getHuffCodes(Node root) {
        if (root == null) return null;
        getHuffCodes(root.left, "0", sb);
        getHuffCodes(root.right, "1", sb);
        return huffmap;
    }
    private static void getHuffCodes(Node node, String code, StringBuilder sb1) {
        StringBuilder sb2 = new StringBuilder(sb1);
        sb2.append(code);
        if (node != null) {
            if (node.data == null) {
                getHuffCodes(node.left, "0", sb2);
                getHuffCodes(node.right, "1", sb2);
            } else
                huffmap.put(node.data, sb2.toString());
        }
    }
    //---------End----------//

    //------------Reduce the Byte size of Original Bytes------------//
    private static byte[] zipBytesWithCodes(byte[] bytes, Map<Byte, String> huffCodes) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte b : bytes)
            strBuilder.append(huffCodes.get(b));

        int length=(strBuilder.length()+7)/8;
        byte[] huffCodeBytes = new byte[length];
        int idx = 0;
        for (int i = 0; i < strBuilder.length(); i += 8) {
            String strByte=(i+8>strBuilder.length())?strBuilder.substring(i):strBuilder.substring(i,i+8);
            huffCodeBytes[idx] = (byte) Integer.parseInt(strByte, 2);
            idx++;
        }
        return huffCodeBytes;
    }
    //----------End-------------//
    //----------------------------End of Compressing File----------------------//

    //-----------------Decompressing the compressed/Encoded file-----------------//
    public static void decompress(String src, String dst) {
        try {
            FileInputStream inStream = new FileInputStream(src);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            byte[] huffmanBytes = (byte[]) objectInStream.readObject();
            Map<Byte, String> huffmanCodes =(Map<Byte, String>) objectInStream.readObject();

            byte[] bytes = decomp(huffmanCodes, huffmanBytes);
            OutputStream outStream = new FileOutputStream(dst);
            outStream.write(bytes);
            inStream.close();
            objectInStream.close();
            outStream.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    //----------End-----------//

    //-----------Extracting HuffMap and Encoded Bytes---------------//
    public static byte[] decomp(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder sb1 = new StringBuilder();
        for (int i=0; i<huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);
            sb1.append(convertbyteInBit(!flag, b));
        }
        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        java.util.List<Byte> list = new java.util.ArrayList<>();
        for (int i = 0; i < sb1.length();) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                String key = sb1.substring(i, i + count);
                b = map.get(key);
                if (b == null) count++;
                else flag = false;
            }
            list.add(b);
            i += count;
        }
        byte b[] = new byte[list.size()];
        for (int i = 0; i < b.length; i++)
            b[i] = list.get(i);
        return b;
    }
    //------------End------------------//

    //-----------Extracting the original Bytes Helper Function-----
    private static String convertbyteInBit(boolean flag, byte b) {
        int byte0 = b;
        if (flag) byte0 |= 256;
        String str0 = Integer.toBinaryString(byte0);
        if (flag || byte0 < 0)
            return str0.substring(str0.length() - 8);
        else return str0;
    }
    //----------End------------//
    //------------End of Decompressing the file------------------//
}