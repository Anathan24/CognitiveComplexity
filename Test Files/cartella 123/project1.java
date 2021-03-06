package paper1;

import java.util.Arrays;

public class dummy {
	public static void main(String[] args) {
	}
	
	public static void main01(String[] args) {
		int result = 1;
		int x = 4;
		while (x > 1) {
			result = result * x;
			x--;
		}
		System.out.println(result);
	}


	public static void main07 (String[] args) {
		int array[] = {2, 19, 5, 17};
		int result = array[0];
		for (int i = 1; i < array.length; i++)
			if (array[i] > result)
				result = array[i];
		System.out.println(result);
	}



	public static void main08(String[] args) {
		int number = 323;
		int result = 0;

		while (number!= 0) {
			result = result + number % 10;
			number = number / 10;
		}
		System.out.println(result);
	}


	public static void main09(String[] args){
		int number = 11;
		boolean result = true;
		for(int i = 2; i < number; i++) {
			if(number % i == 0) {
				result = false;
				break;
			}
		}
		System.out.println(result);
	}


	public static void main10(String[] args) {
		int num1 = 5;
		int num2 = 3;
		int num3 = 10;

		if (num1 > num2 && num1 > num3)
			System.out.println(num1);
		else if (num2 > num1 && num2 > num3)
			System.out.println(num2);
		else if (num3 > num1 && num3 > num2)
			System.out.println(num3);
	}

	public static void main11(String[] args) {
		int num1 = 2;
		int num2 = 3;
		int result = num1;
		for (int i = 1; i < num2; i++) {
			result = result * num1;
		}
		System.out.println(result);
	}



	public static void main13(String[] args) {
		int var1 = 23;
		int var2 = 42;
		int temp;
		temp = var1;
		var1 = var2;
		var2 = temp;
		System.out.println(var1);
	}


	public static void main14(String[] args) {
		String word = "Hello";
		String result = new String();
		for ( int j = word.length() - 1; j >= 0; j-- )
			result += word.charAt(j);
		System.out.println(word);
	}

	public static void main17(String[] args)
	{String word = "Programming in Java";
	String key1 = "Java";
	String key2 = "Pascal";
	int index1 = word.indexOf(key1);
	int index2 = word.indexOf(key2);
	if (index1 != -1)
		System.out.println("Substring is contained: " + key1);
	else
		System.out.println("Substring is not contained: " + key1);
	if (index2 != -1)
		System.out.println("Substring is contained: " + key2);
	else
		System.out.println("Substring is not contained: " + key2);
	}


	public static void main20(String[] args) {
		int i=14;
		String result="";
		while (i>0) {
			if (i%2 ==0)
				result="0"+result;
			else
				result="1"+result;
			i=i/2;
		}
		System.out.println(result); 
	}


	public static void main21(String[] args) {
		int[] array = { 1, 6, 4, 10, 2 };
		for (int i = 0; i <= array.length/2-1; i++){
			int tmp=array[array.length-i-1];
			array[array.length-i-1] = array[i];
			array[i]=tmp;
		}

		for (int i = 0; i <= array.length - 1; i++)
			System.out.println(array[i]);
	}


	public static void main22(String[] args) {
		int[] array={1,2,4,5,6,10};
		Arrays.sort(array);  // era array.sort(aufsteigend);
		float b;
		if (array.length % 2==1)
			b=array[array.length /2];
		else
			b=(array[array.length/2-1]+array[array.length/2])/2f;
		System.out.println(b);
	}


	public static void main02(String[] args) {
		String string1 = "Magdeburg";
		String string2 = "Hamburg";
		int length;
		if (string1.length() < string2.length())
			length = string1.length();
		else length = string2.length();
		int counter=0;
		for (int i = 0; i < length; i++) {
			if (string1.charAt(i) == string2.charAt(i)) {
				counter++;
			}
		}
		System.out.println(counter);
	}



	public static void main06 (String[] args) {
		int n = 4;
		int result = 0;
		for (int i = 1; i <= n; i++)
			result = result + i;
		System.out.println(result);
	}


	public static void main12(String[] args) {
		String word = "otto";
		boolean result = true;
		for (int i = 0, j = word.length() - 1; i < word.length()/2; i++, j--) {
			if (word.charAt(i) != word.charAt(j)) {
				result = false;
				break;
			}
		}
		System.out.println(result);
	}


	public static void main23(String[] args) {int[] array = { 1, 3, 11, 7, 4 };
	for (int i = 0; i < array.length; i++)
		array[i] = array[i] * 2;
	for (int i = 0; i <= array.length - 1; i++)
		System.out.println(array[i]);
	}


	public static void main03(int number1, int number2) {
		int temp;
		do {
			if (number1 < number2) {
				temp = number1;
				number1 = number2;
				number2 = temp;
			}
			temp = number1 % number2;
			if (temp != 0) {
				number1 = number2;
				number2 = temp;
			}
		} while (temp != 0);
		System.out.println(number2);
	}



	public static void main04(String[] args) {
		int array[] = {14,5,7};
		for (int counter1 = 0; counter1 < array.length; counter1++) {
			for (int counter2 = counter1; counter2 > 0; counter2--) {
				if (array[counter2 - 1] > array[counter2]) {
					int variable1 = array[counter2];
					array[counter2] = array[counter2 - 1];
					array[counter2 - 1] = variable1;
				}
			}
		}
		for (int counter3 = 0; counter3 < array.length; counter3++)
			System.out.println(array[counter3]);
	}



	public static void main05(String[] args) {
		int array[] = { 2, 4, 5, 6, 8, 10, 13 };
		int key = 5;
		int index1 = 0;
		int index2 = array.length - 1;
		while (index1 <= index2) {
			int m = (index1 + index2) / 2;
			if (key < array[m])
				index2 = m - 1;
			else if (key > array[m])
				index1 = m + 1;
			else {
				System.out.println(m);
				break;
			}
		}
	}



	public static void main15(String[] args) {
		int array[][] = {{5,6,7},{4,8,9}};
		int array1[][] = {{6,4},{5,7},{1,1}};
		int array2[][] = new int[3][3];
		int x = array.length;
		int y = array1.length;
		for(int i = 0; i < x; i++) {
			for(int j = 0; j < y-1; j++) {
				for(int k = 0; k < y; k++){
					array2[i][j] += array[i][k]*array1[k][j];
				}
			}
		}
		for(int i = 0; i < x; i++) {
			for(int j = 0; j < y-1; j++) {
				System.out.print(" "+array2[i][j]);
			}
		}
	}


	public static void main16(String[] args){
		int a = 4;
		int b = 8;
		int result = (a + b) / 2;
		System.out.println(result);
	}


	public static void main18(String[] args){
		int number1 = 23;
		int number2 = 42;
		int max, min;
		int result = -1;
		if (number1>number2) {
			max = number1; min = number2;
		} else {
			max = number2; min = number1;
		}
		for(int i=1; i<=min; i++) {
			if( (max*i)%min == 0 ) {
				result = i*max; break;
			}
		}
		if(result != -1)
			System.out.println(result);
		else
			System.out.println("Error!");
	}


	public static void main19(String[] args) {
		String s = "here are a bunch of words";
		final StringBuilder result = new StringBuilder(s.length());
		String[] words = s.split("\\s");
		for(int i=0,l=words.length;i<l;++i) {
			if(i>0) result.append(" ");
			result.append(Character.toUpperCase(words[i].charAt(0)))
			.append(words[i].substring(1));
		}
		System.out.println(result);
	}
}

