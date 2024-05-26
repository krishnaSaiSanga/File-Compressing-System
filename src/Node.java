public class Node implements Comparable<Node> {
    Byte data;
    int freq;
    Node left;
    Node right;

    public Node(Byte data,int freq){
        this.data=data;
        this.freq=freq;
    }

    @Override
    public int compareTo(Node that){
        return this.freq-that.freq;
    }
    
}
