abstract class Event { //����� ���� ������ ��������
	protected String Address ;
	protected  int State;
	protected int distance;
	public String getAddress() {
		return Address;
	}
	public int getDistance () {
		return distance;
	}
	public int getSeverity() {
		return State;
	}	
}
