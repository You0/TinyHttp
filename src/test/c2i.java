package test;

public class c2i {
	public static void main(String[] args) throws InterruptedException {
		System.out.println((int)'\r');
		System.out.println((int)'\n');
		long start =  System.currentTimeMillis();
		Thread.sleep(10000);
		long end = System.currentTimeMillis();
		System.out.println((end-start)/1000);
		
	}

}
