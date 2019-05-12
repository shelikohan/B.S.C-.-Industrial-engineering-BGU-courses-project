import java.util.ArrayList;

public class Queue<T> { //��� ���� �� ����
	protected ArrayList<T> queue; 
	protected boolean DayIsOver;
	public Queue(){//���� ����
		queue = new ArrayList<T>();
		DayIsOver = false;
	}
	public synchronized void add(T t){//����� ������� ������ ����
		queue.add(t);
		notifyAll();
	}
	public synchronized T extract (){//����� ������� ������ �����
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
	public synchronized void endDay(){//���� �� �� ������ ������ �� ����� �� ����
		DayIsOver= true;
		notifyAll();

	}
	public int size() {
		return queue.size();
	}
}