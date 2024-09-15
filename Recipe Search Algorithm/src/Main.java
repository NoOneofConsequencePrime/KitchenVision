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
	
	public static void main(String[] args) {
		ParseIngredientCSV();
		ParseRecipeCSV();
		SortRecipe();
		
		
		println("done");
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
        } catch (Exception e) {
            println(e);
        }
	}
	public static void ParseRecipeCSV() {
		for (int i = 0; i <= MM; i++) {
			RecipeIngredientList.put(i, new ArrayList<Integer>());
			RecipeAcquiredIngredientCount.put(i, 0);
		}
		ArrayList<String> list = new ArrayList<String>();
		String file = "recipe.csv";
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
                	} else if (i == 1) {
                		str = str.substring(1, str.length());
                	} else if (i == values.length-1) {
                		str = str.substring(0, str.length()-1);
                	}
                	
                	RecipeIngredientList.get(IngredientID.get(str)).add(RecipeID.get(values[0]));
                }
                break;
            }
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
			double completion1 = (double)RecipeAcquiredIngredientCount.get(str1) / RecipeIngredientList.get(str1).size();
			double completion2 = (double)RecipeAcquiredIngredientCount.get(str2) / RecipeIngredientList.get(str2).size();
			return Double.compare(completion1, completion2);
		}
	}
	
	public static void SortRecipe() {
		Collections.sort(RecipeList, new RecipeComparator());
	}
	
	static void print(Object obj) {System.out.print(obj);}
	static void println(Object obj) {System.out.println(obj);}
}