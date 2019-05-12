import java.util.ArrayList;

public class Queue<T> { //תור גנרי לא חסום
	protected ArrayList<T> queue; 
	protected boolean DayIsOver;
	public Queue(){//בנאי לתור
		queue = new ArrayList<T>();
		DayIsOver = false;
	}
	public synchronized void add(T t){//הכנסת אובייקט מסויים לתור
		queue.add(t);
		notifyAll();
	}
	public synchronized T extract (){//שליפת אובייקט מסויים מהתור
		while(queue.size()==0 && DayIsOver == false){
			try{		
				wait();
			}

			catch(InterruptedException error){

			}
		}

		if(DayIsOver==true)
			return null;
		else{

			T temp = queue.get(0);
			queue.remove(temp);
			return temp;
		}


	}
	public synchronized void endDay(){//מעיר את כל הסרדים שישנים על המופע של התור
		DayIsOver= true;
		notifyAll();

	}
	public int size() {
		return queue.size();
	}
}