public class Table {
	double [] [] table ;
	int N ;
	public Table (int M, int H, int N) { //����
		this.N = N;
		table = new double [(H+2)*(M+1)] [8]; 
		int index=0; // number of row
		for (int i=0 ;i <= M ; i++){ //����� ����� �� ��' ����� ������ ������� ����� 
			int hour =-1; 
			for (int j=0; j< H+2 ; j++){
				table [index][0] = i;
				table [index] [1] = hour;
				hour++; //����� ���
				index++; //���� ����
			}
		}
		double inf = Double.NEGATIVE_INFINITY; //����� �� ������� ����� ����� -������ , �� ��� ��� ������� �� ��������� ��� ���� ��� �����������.
		for (int i=0;i<table.length;i++)
			for (int j=2;j<=5;j++)
				table[i] [j] =inf;
	}
	public void print() { //���� ������� �� �����
		for(int i=0;i<table.length;i++)
		{
			System.out.println();
			for (int j=0;j<table[i].length;j++)
				System.out.print("[ "+ table[i][j] +"] " );
		}
	}
	public void addArgu(int i,int j,double val)
	{//���� ������ ����� ���� ������� ����� ������ �����
		val = Math.round(val*100)/100.000;
		table[i][j]=val;
	}
	public double getVal(int i,int j)
	{  //���� ������� �� ���� �� ������ �����
		double t = table[i][j] ;
		return t;
	}
	public int getNumOfRows() { //���� ������� �� ��' ������ �����
		return table.length;
	}
	public int getN () { //���� ������� �� ����- ��' ����� �� �����
		return N;
	}
	public void printLine(int i)
	{//���� ������� �� ����� ����� �I
		for (int j=0;j<table[i].length-1;j++)
			System.out.print(table[i][j]+"             ");	
		System.out.println(table[i][7]-1+"             ");
	}

}

