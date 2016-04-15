import java.lang.Math;
import java.io.*;
import java.util.*;
import java.lang.Integer.*;

public class Affine
{
	public static void main(String[] args)
	{
		int a;
		int b;
		int action = 0;
		String fIn;
		String fOut;

		Scanner in = new Scanner(System.in);
		Scanner inString = new Scanner(System.in);

		//UI loop, loops while action != exit
		while(action != 3)
		{
			System.out.println("1. Encrypt");
			System.out.println("2. Decrypt");
			System.out.println("3. Exit");
			action = in.nextInt();

			if(action == 1) //Encrypt
			{
				//Read in a
				System.out.println("Encrypt");
				System.out.print("Enter value of a: ");
				a = in.nextInt();

				//Read in b
				System.out.print("Enter value of b: ");
				b = in.nextInt();

				//If key is valid continues
				if(isValidKey(a, b, 52))
				{
					//Read in file name for read
					System.out.print("Enter name of file to encrypt: ");
					fIn = inString.nextLine();
					
					//Read in file name for write
					System.out.print("Enter name of file to save encrypted text to: ");
					fOut = inString.nextLine();

					int[] inData = convertToArray(readFile(fIn)); //read in file and convert linked list to an array
					inData = encrypt(a, b, inData); //encrypt data with key
					writeFile(fOut, intArrayToString(inData)); //convert array to a string and write to file
				}
				else //Otherwise skips encrypt step
				{
					System.out.println("Invalid key entered");
				}
				
			}
			else if(action == 2) //Decrypt
			{
				System.out.println("Decrypt");

				//Read in a
				System.out.print("Enter value of a: ");
				a = in.nextInt();

				//Read in b
				System.out.print("Enter value of b: ");
				b = in.nextInt();

				//If key is valid continues
				if(isValidKey(a, b, 52))
				{
					//Read in file name for read
					System.out.print("Enter name of file to decrypt: ");
					fIn = inString.nextLine();
					
					//Read in file name for write
					System.out.print("Enter name of file to save decrypted text to: ");
					fOut = inString.nextLine();

					int[] inData = convertToArray(readFile(fIn));
					inData = decrypt(a, b, inData);
					writeFile(fOut, intArrayToString(inData));
				}
				else //Otherwise skips encrypt step
				{
					System.out.println("Invalid key entered");
				}
			}
			else if(action ==3) //Exit
			{
				System.out.println("Exiting...");
			}
			else //Invalid selection
			{
				System.out.println("Invalid selction, enter number range 1-3");
			}
		}


	}

	//Uses Euclids Algorithm to check if two values are coprime
	private static boolean isCoprime(int num1, int num2)
	{
		//Loop Calculates greatest common divisor
		while(num1 != 0 && num2 != 0)
		{
			if(num1 > num2)
			{
				num1 %= num2;
			}
			else
			{
				num2 %= num1;
			}
		}

		int gcd = Math.max(num1, num2);

		//If GCD == 1 then is coprime
		return gcd == 1;
	}

	//Checks if a and b are a valid key in affine cipher
	public static boolean isValidKey(int a, int b, int m)
	{
		boolean rVal = false;

		//a >= 0 and coprime with 26 and b <= 52
		if((a >= 0 && isCoprime(a, m)) && (b <= 52))
		{
			rVal = true;
		}

		return rVal;
	}

	private static int findInverseA(int a)
	{
		int[] inverseArray = {1, 35, 21, 15, 29, 19, 7, 49, 11, 5, 43, 25, 27,
							 9, 47, 41, 3, 45, 33, 23, 37, 31, 17, 51};

		int[] map = {1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25, 27, 29,
					31, 33, 35, 37, 41, 43, 45, 47, 49, 51};

		int location = 0, size = 0, i = 0;
		boolean found = false;

		size = inverseArray.length;

		while(found != true && i < size)
		{
			if(inverseArray[i] == a)
			{
				location = i;
				found = true;
			}

			i++;
		}

		return map[location];
	}

	public static int[] encrypt(int a, int b, int[] array)
	{
		int size = array.length;

		array = adjustFromAscii(array);

		for(int i = 0; i < size; i++)
		{
			array[i] = ((a * array[i]) + b) % 52;
		}

		array = adjustToAscii(array);

		return array;
	}


	public static int[] decrypt(int a, int b, int[] array)
	{
		int size = array.length;

		a = findInverseA(a); //find inverse a for equation

		array = adjustFromAscii(array);

		for(int i = 0; i < size; i++)
		{
			array[i] = (a * (array[i] - b));
			
			while(array[i] <= 0) //wrap around until value is positive
			{
				array[i] = array[i] + 52;
			}

			array[i] = array[i] % 52;
		}

		array = adjustToAscii(array);

		return array;
	}

	private static LinkedList<Integer> readFile(String fName)
	{
		FileReader in = null;
		LinkedList<Integer> input = new LinkedList<Integer>();

		try
		{
			in = new FileReader(fName);

			int c;

			//While c does not read null terminator
			while((c = in.read()) != -1)
			{
				//Range of valid ascii chracters, filters out symbols
				if((c >= 65 && c <= 90) || (c >= 97 && c <= 122))
				{
					input.addLast(new Integer(c));
				}
			}

			if(in != null)
			{
				in.close();
			}

		}
		catch(IOException e)
		{
			System.out.println("Exception has occured: " + e.getMessage());
		}

		return input;
	}

	private static void writeFile(String fName, String output)
	{
		FileWriter out = null;

		try
		{
			out = new FileWriter(fName);

			out.write(output);

			if(out != null)
			{
				out.close();
			}
		}
		catch(IOException e)
		{
			System.out.println("Exception has occured: " + e.getMessage());
		}
	}

	//Converts a linked list of Integer objects to an array of primitive ints
	private static int[] convertToArray(LinkedList<Integer> list)
	{
		int size = list.size();

		//Array to place unwrapped int objects
		int[] array = new int[size];

		//Convert list to array
		Object[] wrapped = list.toArray();

		//Places objects into array
		for(int i = 0; i < size; i++)
		{
			array[i] = ((Integer)wrapped[i]).intValue();
		}

		return array;
	}

	//Converts cipher text to string, adds single space after each int
	private static String intArrayToString(int[] array)
	{
		int size = array.length;
		String converted = "";

		//Build string
		for(int i = 0; i < size; i++)
		{
			converted = converted + (char)array[i];
		}

		return converted;
	}

	//Brings values down to A=0, B=1..etc from ascii
	private static int[] adjustFromAscii(int[] array)
	{
		int size = array.length;

		for(int i = 0; i < size; i++)
		{
			//Adjust capitals
			if(array[i] <= 90)
			{
				array[i] = array[i] - 65;
			}
			else //adjust lowercase
			{
				array[i] = array[i] - 71;
			}
		}

		return array;
	}

	//Brings values up from A=0, B=1...etc to ascii
	private static int[] adjustToAscii(int[] array)
	{
		//System.out.println(array[2] + " adjustToAscii ");

		int size = array.length;

		for(int i = 0; i < size; i++)
		{
			//Adjust capitals
			if(array[i] <= 25)
			{
				array[i] = array[i] + 65;
			}
			else //adjust lowercase
			{
				array[i] = array[i] + 71;
			}
		}

		return array;
	}

}