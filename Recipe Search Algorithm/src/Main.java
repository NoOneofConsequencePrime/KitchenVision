import java.io.*;
import java.util.*;

public class Main {
	final static int MM = 22000;
//	public static ArrayList<String> IngredientList;
	public static ArrayList<String> RecipeList = new ArrayList<String>();
	public static HashMap<String, Integer> IngredientID = new HashMap<String, Integer>();
	public static HashMap<String, Integer> RecipeID = new HashMap<String, Integer>();
	public static HashMap<Integer, ArrayList<Integer>> RecipeIngredientList = new HashMap<Integer, ArrayList<Integer>>();
	public static HashMap<Integer, Integer> RecipeAcquiredIngredientCount = new HashMap<Integer, Integer>();
	public static int[] IngredientCount = new int[MM];
	
	public static void main(String[] args) throws IOException {
		ParseIngredientCSV();
		ParseRecipeCSV();
		
		AddIngredient("baby spinach leaves");
		AddIngredient("cherry tomatoes");
		SortRecipe();
		
		for (int i = 0; i < 10; i++) {
			println(RecipeList.get(i)+": "+RecipeAcquiredIngredientCount.get(RecipeID.get(RecipeList.get(i))));
		}
//		println(RecipeList.size());
	}
	
	public static void ParseIngredientCSV() {
		ArrayList<String> list = new ArrayList<String>();
		String file = "ingredient.csv";
		String line = "";
        String delimiter = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (String str : values) {
                	IngredientID.put(str, IngredientID.size());
                }
            }
            br.close();
        } catch (Exception e) {
            println(e);
        }
	}
	public static void ParseRecipeCSV() throws IOException {
		for (int i = 0; i <= MM; i++) {
			RecipeIngredientList.put(i, new ArrayList<Integer>());
			RecipeAcquiredIngredientCount.put(i, 0);
		}
		ArrayList<String> list = new ArrayList<String>();
		String file = "test.csv";
		String line = "";
        String delimiter = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (int i = 0; i < values.length; i++) {
                	String str = values[i];
                	if (i == 0) {
                		RecipeID.put(str, RecipeID.size());
                		RecipeList.add(str);
                		continue;
                	}
                	if (i == 1) {
                		str = str.substring(1, str.length());
                	}
                	if (i == values.length-1) {
                		str = str.substring(0, str.length()-1);
                	}
                	
                	RecipeIngredientList.get(IngredientID.get(str)).add(RecipeID.get(values[0]));
                	println(RecipeIngredientList.get(IngredientID.get(str)).size());
                }
            }
            br.close();
        } catch (Exception e) {
            println(e);
        }
	}
	
	public static boolean AddIngredient(String str) {
		Integer ingredientID = IngredientID.get(str);
		if (ingredientID == null) {return false;}
		
		if (IngredientCount[ingredientID] == 0) {
			ArrayList<Integer> list = RecipeIngredientList.get(ingredientID);
			for (Integer recipeID : list) {
				RecipeAcquiredIngredientCount.put(recipeID, RecipeAcquiredIngredientCount.get(recipeID)+1);
			}
		}
		IngredientCount[ingredientID]++;
		
		return true;
	}
	
	public static boolean RemoveIngredient(String str) {
		Integer ingredientID = IngredientID.get(str);
		if (ingredientID == null) {return false;}
		
		if (IngredientCount[ingredientID] < 1) {return false;}
		if (IngredientCount[ingredientID] == 1) {
			ArrayList<Integer> list = RecipeIngredientList.get(ingredientID);
			for (Integer recipeID : list) {
				RecipeAcquiredIngredientCount.put(recipeID, RecipeAcquiredIngredientCount.get(recipeID)-1);
			}
		}
		IngredientCount[ingredientID]--;
		
		return true;
	}
	
	private static class RecipeComparator implements Comparator<String> {
		@Override
		public int compare(String str1, String str2) {
			int recipeID1 = RecipeID.get(str1), recipeID2 = RecipeID.get(str2);
			println(recipeID1 + ":::" + recipeID2);
			int completion1 = 100*RecipeAcquiredIngredientCount.get(recipeID1) / RecipeIngredientList.get(recipeID1).size();
			int completion2 = 100*RecipeAcquiredIngredientCount.get(recipeID2) / RecipeIngredientList.get(recipeID2).size();
			
			if (completion1 != 0) {println(str1+": "+RecipeAcquiredIngredientCount.get(recipeID1));}
			if (completion2 != 0) {println(str2+": "+RecipeAcquiredIngredientCount.get(recipeID2));}
			return Double.compare(completion1, completion2);
		}
	}
	
	public static void SortRecipe() {
		Collections.sort(RecipeList, new RecipeComparator());
	}
	
	static void print(Object obj) {System.out.print(obj);}
	static void println(Object obj) {System.out.println(obj);}
}