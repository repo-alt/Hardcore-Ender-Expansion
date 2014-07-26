package chylex.hee.gui;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.brewing.PotionTypes;

class SlotBrewingStandIngredient extends Slot{
	public SlotBrewingStandIngredient(IInventory inv, int id, int x, int z){
		super(inv,id,x,z);
	}

	@Override
	public boolean isItemValid(ItemStack is){
		if (is == null)return false;
		Item item = is.getItem();
		return PotionTypes.getItemIndexes(is).length > 0 || item.isPotionIngredient(is) || item == ItemList.instability_orb || item == ItemList.silverfish_blood;
	}

	@Override
	public int getSlotStackLimit(){
		return 64;
	}
}