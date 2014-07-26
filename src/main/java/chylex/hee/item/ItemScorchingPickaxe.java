package chylex.hee.item;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemScorchingPickaxe extends Item{
	private static final Pattern blockRegex = Pattern.compile("(?:^ore[A-Z].+$)|(?:.+_ore$)|(?:.+Ore$)");
	private static final Map<Block,Boolean> cachedBlocks = new IdentityHashMap<>();
	private static final Random cacheRand = new Random(0);
	
	private static final boolean isBlockValid(Block block){
		if (cachedBlocks.containsKey(block))return cachedBlocks.get(block).booleanValue();
		
		if (FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(block)) != null){
			cachedBlocks.put(block,true);
			return true;
		}
		
		if (blockRegex.matcher(block.getUnlocalizedName().startsWith("tile.") ? block.getUnlocalizedName().substring(5) : "").find()){
			Item drop = block.getItemDropped(0,cacheRand,0);
			
			if (drop != null && !(drop instanceof ItemBlock)){
				cachedBlocks.put(block,true);
				return true;
			}
		}
		
		cachedBlocks.put(block,false);
		return false;
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack is, World world, Block block, int x, int y, int z, EntityLivingBase owner){
		if (block.getBlockHardness(world,x,y,z) != 0D)is.damageItem(1,owner);
		return true;
	}
	
	@Override
	public boolean hitEntity(ItemStack is, EntityLivingBase hitEntity, EntityLivingBase owner){
		is.damageItem(2,owner);
		return true;
	}
	
	@Override
	public float getDigSpeed(ItemStack stack, Block block, int meta){
		if (isBlockValid(block))return 8F;
		else return super.getDigSpeed(stack,block,meta);
	}
	
	@Override
	public boolean canHarvestBlock(Block block, ItemStack is){
		return isBlockValid(block);
	}

	@Override
	public int getHarvestLevel(ItemStack is, String toolClass){
		return toolClass.equals("pickaxe") ? 100 : -1;
	}

	@Override
	public Set<String> getToolClasses(ItemStack is){
		return ImmutableSet.of("pickaxe");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D(){
		return true;
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBlockDropItems(HarvestDropsEvent e){
		if (e.harvester == null)return;
		
		ItemStack heldItem = e.harvester.getHeldItem();
		if (heldItem == null || heldItem.getItem() != this)return;
		
		if (isBlockValid(e.block)){
			e.dropChance = 1F;
			
			ItemStack drop = e.drops.get(0);
			drop.stackSize = 1;
			
			for(ItemStack blockDrop:e.drops){
				if (drop.getItem() != blockDrop.getItem())return;
			}
			
			e.drops.clear();
			
			if (drop.getItem() instanceof ItemBlock){
				ItemStack result = null;
				ItemStack smelted = FurnaceRecipes.smelting().getSmeltingResult(drop);
				
				if (smelted == null)return;
				else if (smelted.getItem() instanceof ItemBlock)result = smelted.copy();
				else{
					result = smelted.copy();
					
					int fortune = 0;
					for(int a = 0; a < 5; a++)fortune += 1+e.world.rand.nextInt(3+e.world.rand.nextInt(3));
					fortune = 1+(int)Math.floor(fortune*((3D*e.world.rand.nextDouble()+e.world.rand.nextDouble()+0.2D)*Math.pow(FurnaceRecipes.smelting().func_151398_b(drop),1.8D)+(fortune*0.06D))/5.5D); // OBFUSCATED getExperience
					
					result.stackSize = fortune;
				}
				
				e.drops.add(result);
			}
			else{
				int fortune = 0;
				for(int a = 0; a < 4; a++)fortune += e.block.quantityDropped(e.blockMetadata,3+e.world.rand.nextInt(3)-e.world.rand.nextInt(2),e.world.rand);
				for(int a = 0; a < 4; a++)fortune += e.block.quantityDropped(e.blockMetadata,0,e.world.rand);
				fortune = 1+(int)Math.floor((fortune+e.block.getExpDrop(e.world,e.blockMetadata,0)/2D)*(e.world.rand.nextDouble()+(e.world.rand.nextDouble()*0.5D)+0.35D)/6D);
				
				drop.stackSize = fortune;
				e.drops.add(drop);
			}
		}
	}
}