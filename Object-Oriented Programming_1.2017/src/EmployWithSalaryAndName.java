abstract class EmployWithSalaryAndName extends Employ{ //����� ������� ���� ������ �� �� �������
protected double salary;
protected String name;
public double getSalary() { 
	return salary;
}
protected void addSalary (double num) {
	salary+=num;
}
}
