import java.io.*;
import java.util.*;

public class Main {
	final static int MM = 22000;
//	public static ArrayList<String> IngredientList;
	public static ArrayList<String> RecipeList = new ArrayList<String>();
	public static HashMap<String, ArrayList<String>> RecipeIngredientList = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, ArrayList<String>> IngredientForRecipeList = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, Integer> RecipeAcquiredIngredientCount = new HashMap<String, Integer>();
	public static HashMap<String, Integer> IngredientCount = new HashMap<String, Integer>();
	
	public static void main(String[] args) throws IOException {
		ParseIngredientCSV();
		ParseRecipeCSV();
		
		SortRecipe();
		
		for (int i = 0; i < 10; i++) {
			println(RecipeList.get(i) + ": " + RecipeAcquiredIngredientCount.get(RecipeList.get(i)));
		}
	}
	
	public static void ParseIngredientCSV() {
		String file = "ingredient.csv";
		String line = "";
        String delimiter = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (String str : values) {
                	IngredientCount.put(str, 0);
                	IngredientForRecipeList.put(str, new ArrayList<String>());
                }
            }
            br.close();
        } catch (Exception e) {
            println(e);
        }
	}
	
	public static void ParseRecipeCSV() throws IOException {
		String file = "output.csv";
		String line = "";
        String delimiter = ",";
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter);
                for (int i = 0; i < values.length; i++) {
                	String str = values[i];
                	if (i == 0) {
                		RecipeList.add(str);
                		RecipeAcquiredIngredientCount.put(str, 0);
                		RecipeIngredientList.put(str, new ArrayList<String>());
                		continue;
                	}
                	if (i == 1) {
                		str = str.substring(1, str.length());
                	}
                	if (i == values.length-1) {
                		str = str.substring(0, str.length()-1);
                	}
                	
                	IngredientForRecipeList.get(str).add(values[0]);
                	RecipeIngredientList.get(values[0]).add(str);
                }
            }
            br.close();
        } catch (Exception e) {
            println(e);
        }
	}
	
	public static boolean AddIngredient(String str) {
		if (IngredientCount.get(str) == 0) {
			ArrayList<String> list = IngredientForRecipeList.get(str);
			for (String recipe : list) {
				RecipeAcquiredIngredientCount.put(recipe, RecipeAcquiredIngredientCount.get(recipe)+1);
			}
		}
		IngredientCount.put(str, IngredientCount.get(str)+1);
		
		return true;
	}
	
	public static boolean RemoveIngredient(String str) {
		if (IngredientCount.get(str) == 1) {
			ArrayList<String> list = IngredientForRecipeList.get(str);
			for (String recipe : list) {
				RecipeAcquiredIngredientCount.put(recipe, RecipeAcquiredIngredientCount.get(recipe)-1);
			}
		}
		IngredientCount.put(str, IngredientCount.get(str)-1);
		
		return true;
	}
	
	private static class RecipeComparator implements Comparator<String> {
		@Override
		public int compare(String str1, String str2) {
			double completion1 = (double)RecipeAcquiredIngredientCount.get(str1) / RecipeIngredientList.get(str1).size();
			double completion2 = (double)RecipeAcquiredIngredientCount.get(str2) / RecipeIngredientList.get(str2).size();
			
			return Double.compare(completion2, completion1);
		}
	}
	
	public static void SortRecipe() {
		Collections.sort(RecipeList, new RecipeComparator());
	}
	
	static void print(Object obj) {System.out.print(obj);}
	static void println(Object obj) {System.out.println(obj);}
}