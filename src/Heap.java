import java.util.ArrayList;
public class Heap<T extends Comparable<T>> {
    private ArrayList<T> list;

    public Heap(){
        list=new ArrayList<>();
    }

    //-----------Swap the elements------------//
    public void swap(int first,int second){
        T temp=list.get(first);
        list.set(first,list.get(second));
        list.set(second, temp);
    }
    
    //-------Extract Parent index----------//
    public int parent(int index){
        return (index-1)/2;
    }

    //------------Extract left index of parent----------//
    public int left(int index){
        return index*2+1;
    }

    //--------------Extract right index of parent---------//
    public int right(int index){
        return index*2+2;
    }

    //-----------Adding Values in the Heap--------//
    public void insert(T value){
        list.add(value);
        upHeap(list.size()-1);
    }
    public void upHeap(int index){
        if(index<=0)return;
        int p=parent(index);
        if(list.get(p).compareTo(list.get(index))>0){
            swap(index, p);
            upHeap(p);
        }
    }

    //-------------Removing the Smallest Node in Heap------------//
    public T remove() throws Exception{
        if(list.isEmpty()){
            throw new Exception("Heap is Empty");
        }
        T removed=list.get(0);
        T end=list.remove(list.size()-1);
        if(!list.isEmpty()){
            list.set(0,end);
            downHeap(0);
        }      
        return removed;
    }
    public void downHeap(int index){
        int curr=index;
        int left=left(index);
        int right=right(index);

        if(left<list.size() && list.get(curr).compareTo(list.get(left))>0){
            curr=left;
        }
        if(right<list.size() && list.get(curr).compareTo(list.get(right))>0){
            curr=right;
        }
        if(curr!=index){
        swap(curr, index);
        downHeap(curr);
        }
    }

    //---------------Length of Heap------------//
    public int size(){
        return list.size();
    }
}
