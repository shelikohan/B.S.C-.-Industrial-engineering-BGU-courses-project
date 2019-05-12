import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Comparator;
public class MyAdt  {
	private int i;
	private TreeMap <Integer,TreeSet<Integer>> OrderMap;// map that holds the numbers according to their remainder with i, and treeset by insert order
	private TreeSet <Integer> TreeSetForContain;//Avltree that holds all the numbers that inserted
	private Hashtable <Integer, Integer> MaxTable; //map that holds for each reminder key the Max number that inserted
	
	public MyAdt (int i) {//initialize the data structure
		this.i =i;
		OrderMap =  new TreeMap <Integer,TreeSet<Integer>> ();// comparator compare by value size 
		TreeSetForContain = new TreeSet <Integer> ();
		MaxTable = new Hashtable <Integer,Integer> ();
	}
	
	public void insert (int x) {
		TreeSet<Integer> ValueTreeSet  = null; 
		if(OrderMap.containsKey(x%i)){// check if OrderMap already has the key
			ValueTreeSet = OrderMap.get(x%i);// pointer to the tree of the key
		}
		else
			ValueTreeSet = new TreeSet <Integer> (new OrderComparator ());// create new treeset for the remainder
		ValueTreeSet.add(x);
		OrderMap.put(x%i,ValueTreeSet);//insert the number to the right map according to the remainder
		TreeSetForContain.add(x);
	int max = x;
		if (MaxTable.containsKey(x%i)) {//check if MaxTable has the key for the remainder
			if (MaxTable.get(x%i) > max ) //check if the new number is the maximum 
				max = MaxTable.get(x%i);	
		}			
		MaxTable.put(x%i,max);// update the maximum if needed and insert to the map
	}
	public int findOrder (int order, int j) { 
		TreeSet<Integer> treeSetByOrder = null;
		if (OrderMap.containsKey(j))//check if the map has a J key
			treeSetByOrder =OrderMap.get(j);// get the number for the key
		else return -1;
		Iterator<Integer> itr =treeSetByOrder.iterator();//pointer to the number
		int val = -1;
		while(order!=0 && itr.hasNext()){//while we didn't get the right number
			val =itr.next();
			order--;
		}
		if (order ==0) 
			return val;
		else 
			return -1;
	}
	public int findBiggest(int j) {
		if (!MaxTable.containsKey(j)) 
			return -1;//if we don't have numbers that has the remainder J
		return MaxTable.get(j);
	}
	public boolean contains (int x) {
		return (TreeSetForContain.contains(x) );//check if the number x was inserted
	}


private class OrderComparator implements Comparator<Integer>
 {
	public int compare(Integer o1, Integer o2) {
		if (o1==o2) return 0; //if equal return 0
		return 1; //else return 1 - orginize treeSet by time that the num was inserted
	}
 }
}