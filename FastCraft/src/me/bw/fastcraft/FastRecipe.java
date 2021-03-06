package me.bw.fastcraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class FastRecipe {
	private IngredientList ingredients;
	private ItemStack result;
	private Recipe recipe;
	
	public FastRecipe(Recipe recipe){
		this.recipe = recipe;
		ingredients = new IngredientList();
		if (recipe instanceof ShapedRecipe){
			ShapedRecipe cur = (ShapedRecipe)recipe;
			for (ItemStack is : cur.getIngredientMap().values()){
				if (is != null && is.getType() != Material.AIR){
					ingredients.add(new Ingredient(is, 1));
				}
			}
			result = recipe.getResult();
		}else if (recipe instanceof ShapelessRecipe){
			ShapelessRecipe cur = (ShapelessRecipe)recipe;
			for (ItemStack i : cur.getIngredientList()){
				ingredients.add(new Ingredient(i, 1));
			}
			result = recipe.getResult();
		}
	}
	public FastRecipe(IngredientList ingredients, ItemStack result){
		this.ingredients = ingredients;
		this.result = result;
	}
	
	public Recipe getRecipe(){
		return recipe;
	}
	
	public IngredientList getIngredients(){
		return ingredients;
	}
	public static boolean isCraftRecipe(Recipe recipe){
		return (recipe instanceof ShapedRecipe) || (recipe instanceof ShapelessRecipe);
	}
	public boolean canCraft(IngredientList withItems){
		return withItems.containsAll(ingredients);
	}
	public ItemStack getResult(){
		return result;
	}
	public boolean equals(Object obj){
		if (!(obj instanceof FastRecipe)) return false;
		FastRecipe test = (FastRecipe)obj;
		if (!this.getIngredients().equals(test.getIngredients())) return false;
		if (!this.getResult().equals(test.getResult())) return false;
		return true;
	}
	
	public CraftingInventory createCraftingInventory(HumanEntity p){
		final Inventory inv = Bukkit.createInventory(p, 18);
		if (recipe instanceof ShapedRecipe){
			ShapedRecipe r = (ShapedRecipe)recipe;
			for (int row = 0; row < 3 && row < r.getShape().length; row++){
				for (int col = 0; col < 3 && col < r.getShape()[row].length(); col++){
					inv.setItem(row * 3 + col + 1, r.getIngredientMap().get(r.getShape()[row].charAt(col)));
				}
			}
		}else if (recipe instanceof ShapelessRecipe){
			ShapelessRecipe r = (ShapelessRecipe)recipe;
			int curSlot = 1;
			for (ItemStack ing : r.getIngredientList()){
				if (curSlot > 10) break;
				inv.setItem(curSlot++, ing);
			}
		}
		inv.setItem(0, recipe.getResult());
		return new MyCraftingInventory(inv, recipe);
	}
}
