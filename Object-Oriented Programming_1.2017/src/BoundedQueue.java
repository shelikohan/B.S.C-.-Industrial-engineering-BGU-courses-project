public class BoundedQueue<T> extends Queue<T>{ //סוג של תור
	private int maxSize;// קיבולת התור
	public BoundedQueue(int maxSize){// בנאי תור חסום
		super();
		this.maxSize= maxSize;
	}
	public synchronized void insert(T t){// הכנסת אובייקט מסויים לתור
		while (queue.size() == maxSize)
			try{
				wait();
			}

		catch (InterruptedException error){
		}
		queue.add(t);
		notifyAll();
	}
	public synchronized T extract (){// שליפת אובייקט מסויים מהתור
		while (queue.size() == 0 && DayIsOver == false)
			try{
				wait();
			}
		catch (InterruptedException error){
		}
		if (DayIsOver == false){

			T temp =  queue.get(0);
			queue.remove(temp);
			notifyAll();
			return temp;
		}
		else
			return null;
	}
	public synchronized void endDay(){//מעיר את כל הסרדים שישנים על המופע של התור
		DayIsOver= true;
		notifyAll();

	}
}