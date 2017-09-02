package Evaluate;

import java.util.Comparator;
import java.util.TreeSet;

public class test {

	public static void main(String[] args) {
		TestClass x = new TestClass(1);
		TestClass y = new TestClass(2);
		TestClass z = new TestClass(3);
		
		TreeSet<TestClass> set = new TreeSet<TestClass>(new Order());
		set.add(x);
		set.add(y);
		set.add(z);
		
		y.a = 10;
		
		while (!set.isEmpty()) {
			TestClass t = set.first();
			System.out.println(t.a);
			set.remove(t);
		}
		
	}

	static class Order implements Comparator<TestClass> {
		public int compare(TestClass x, TestClass y) {
			return Integer.compare(x.a, y.a);
		}
	}
}
