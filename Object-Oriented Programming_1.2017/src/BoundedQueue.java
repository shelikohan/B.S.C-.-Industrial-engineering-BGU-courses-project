public class BoundedQueue<T> extends Queue<T>{ //��� �� ���
	private int maxSize;// ������ ����
	public BoundedQueue(int maxSize){// ���� ��� ����
		super();
		this.maxSize= maxSize;
	}
	public synchronized void insert(T t){// ����� ������� ������ ����
		while (queue.size() == maxSize)
			try{
				wait();
			}

		catch (InterruptedException error){
		}
		queue.add(t);
		notifyAll();
	}
	public synchronized T extract (){// ����� ������� ������ �����
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
	public synchronized void endDay(){//���� �� �� ������ ������ �� ����� �� ����
		DayIsOver= true;
		notifyAll();

	}
}