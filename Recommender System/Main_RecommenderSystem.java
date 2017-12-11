 /*
 * NAME: SARANG RAKHECHA
 * BU ID:B00612591
 * COURSE:CS535-DATA MINING
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main_RecommenderSystem {
	//INITIALIZATION OF MATRICS 
	
	//INPUT MATRIX WITH THE BLANK VALUES UPDATED TO 0
	int[][] inputMatrix = new int[944][1683];			
	
	//THE CORRECT PREDICTION IS INSERTED INTO THIS MATRIX
	int[][] predictionMatrix = new int[944][1683];		
	
	//SIMILARITY VALUES OF USERS ARE SOTRED IN THIS MATRIX
	double[][]customerID = new double[944][944];		
	
	
	/*
	 *THIS FUNCTION IS USED TO CALCUALTE THE SIMILARITY BETWEEN THE THE USERS. IT THEN MAPS THE SIMILAR USERS AND PLACES THEM TOGETHER USING HASHMAPS.
	 *->SFOR STORING SIMILAR USERS, LISTS ARE USED. 
	 */
	public void Pearson()
	{
		double denominator1=0;
		
		List<Integer> similar;
		HashMap<Integer, List<Integer>> similarMap = new HashMap<Integer, List<Integer>>();
		
		for(int loop1=1 ;loop1<=943; loop1++)
		{
			double mean1 = 0.0;
	
			//CALCULATE THE MEAN FROM USER-1
			for(int loop2=1 ;loop2<=1682; loop2++)
			{
				mean1 += inputMatrix[loop1][loop2];
			}
			
			mean1 = mean1/1682;
			similar = new ArrayList<Integer>();
			
			
			for(int loop2=1; loop2<=943; loop2++)
			{
				double mean2 = 0.0;
				if(loop1 == loop2)
					continue;
				
				//CALCULATE THE MEAN FROM USER-2
				for(int loop3=1 ;loop3 <= 1682; loop3++)
				{
					mean2 += inputMatrix[loop2][loop3];
				}
				
				mean2 = mean2/1682;
				double numerator1 = 0, y_rating1 = 0,z_rating1 = 0;
				
				//TO EVALUATE THE FORMULA OF PEARSON's ALGORITHM
				for(int loop3=1;loop3<=1682;loop3++)
				{
					numerator1 += (inputMatrix[loop1][loop3] - mean1) * (inputMatrix[loop2][loop3] - mean2);
					y_rating1 +=  Math.pow((inputMatrix[loop1][loop3] - mean1), 2);
					z_rating1 += Math.pow((inputMatrix[loop2][loop3] - mean2), 2);	
				}
				
				//CALCULATING FINAL DENOMINATOR.
				denominator1 = Math.sqrt(y_rating1 * z_rating1);
				
				//CALCULATING PEARSON'sFINAL COEFFICIENT CORELATIN VALUE
				double answer = numerator1/denominator1;
				
				//SIMILARITY COEFFICIENT ANSWER IS STORED HERE, WHICH IS THEN LATER USED
				customerID[loop1][loop2] = answer;
				
			}
			//HashMaps USED TO STORE DATA OF SIMILAR USERS. LATER HELPS TO RETIEVE.
			//VALUE IS SOTRED IN A LIST
			similarMap.put(loop1, similar);
		}
		
		weightedSum();
		
	}
	
	/*
	 * THE OUTPUT IS THEN COLLECTED IN THE FILE NAMED "OutFile.txt" WHICH IS STORED ON A LOCAL ADDRESS ON THE SYSTEM. 
	 * CORRECT OUTPUT DATA IS STORED IN "predictionMATRIX FROM WHERE THE DATA IS TRANSFERED INTO OutFile.txt". 
	 */
	public void buildMatrix()
	{
		//TO PRINT THE OUTPUT IN THE FILE
		try {
			PrintStream out = new PrintStream(new FileOutputStream("OutFile.txt"));
			out.print("User		Item			Value");
			for(int row = 1; row <= 943; row++)
			{
				for(int col = 1; col <= 1682; col++)
				{
					out.println();
					out.print(row);
					out.print("		"+col);
					
					//PREDICTION MATRIX IS USED TO PRINT THE OUTPUT
					out.print("			"+predictionMatrix[row][col]);	
				}
			}
			
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * THIS FUNTION IS USED TO COMPUTE THE PREDICTION TO USER BASED ON OTHER SIMILAR USERS.
	 */
	public void weightedSum()
	{
		for(int row=1; row<944; row++)
		{
			for(int col=1; col<1683; col++)
			{
				if(inputMatrix[row][col] == 0)
				{
					double numerator2 = 0;
					double denominator2 = 0;
					double prediction = 0;
					for(int count=1;count<944;count++)
					{
						if(row!=count && inputMatrix[count][col] !=0)
						{
							 numerator2 += inputMatrix[count][col]*customerID[row][count];
							 denominator2 += Math.abs(customerID[row][count]);
						}
						
					}
					prediction = numerator2/denominator2;
					if(prediction < 1)
						prediction = 1;
					else if(prediction > 5)
						prediction = 5;
					predictionMatrix[row][col]= (int)Math.round(prediction);
				} 
				else 
				{
					predictionMatrix[row][col]=inputMatrix[row][col];
				}
			}
		}
	}
	
	
	/*
	 * THIS FUNCTION CREATES THE TOTAL MATRIX(943 x 1682) OF ALL 0 VALUE. IT THEN OVERWRITES ON THE DATA PROVIDED BY ALL THE RECOMMENDERS.
	 * DATA IS STORED IN "train_all_txt.txt" 
	 */
	public static void main(String[] args) throws IOException {
		
		Main_RecommenderSystem obj = new Main_RecommenderSystem();
		FileInputStream file = new FileInputStream("train_all_txt.txt");
		BufferedReader buff = new BufferedReader(new InputStreamReader(file));
		
		String strLine;
		String[] fileArray = new String[3];
		
		//READING FILE AND STORING IT in "Matrix_data.txt". ALSO INSERTING 0 VALUES ON RUN-TIME
		while((strLine = buff.readLine())!= null)
		{
			//System.out.println(strLine);
			fileArray=strLine.split(" ");
			obj.inputMatrix[Integer.parseInt(fileArray[0])][Integer.parseInt(fileArray[1])]=Integer.parseInt(fileArray[2]);
		}
		
		obj.Pearson();
		obj.buildMatrix();
		buff.close();
	}
}
