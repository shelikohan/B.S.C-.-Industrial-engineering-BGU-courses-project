public class Available { //����� ������� �� ���� ������� ���� ��� �����
private int permament; //����� ����� ����� ������ ��' ������
private int min; //�� ������ ���� ����� �����, ��� ������� ���� ���� 2
public Available(int permament ,int min){
	this.min = min;
	this.permament =permament;
}
public int TrucksNeeded(int freeTrucks, int state) {
	if (state==1&&freeTrucks >=4) return 4; //���� ������� �� ����� ������ 1 ��� 4
	if (state==2 &&freeTrucks >=5) return 5;
	if (state==3&&freeTrucks >=7) return 7;
	if (state==4&&freeTrucks >=9) return 9;
	if (state==5&&freeTrucks >=12) return 12; //���� ������� �� ����� ������ 5 ��� 12
	else return permament + Math.max(min, freeTrucks); //�� �� ������ �� ��������, ���� ��� ���.
}
}
