package util;

public class Log {
	private static boolean door = true;
	
	public static void E(Object info){
		if(door==true){
			System.out.println(String.valueOf(info));
		}
	}
}
