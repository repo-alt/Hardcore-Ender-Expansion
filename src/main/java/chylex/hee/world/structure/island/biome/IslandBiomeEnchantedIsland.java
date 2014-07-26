package chylex.hee.world.structure.island.biome;
import java.util.Random;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.world.structure.island.biome.data.BiomeContentVariation;
import chylex.hee.world.structure.island.biome.decorator.BiomeDecoratorEnchantedIsland;
import chylex.hee.world.structure.island.biome.decorator.IslandBiomeDecorator;
import chylex.hee.world.structure.util.pregen.LargeStructureWorld;
import chylex.hee.world.util.SpawnEntry;

public class IslandBiomeEnchantedIsland extends IslandBiomeBase{
	public static final BiomeContentVariation HOMELAND = new BiomeContentVariation(2,6);
	public static final BiomeContentVariation LABORATORY = new BiomeContentVariation(6,4);
	
	private final BiomeDecoratorEnchantedIsland decorator = new BiomeDecoratorEnchantedIsland();
	
	protected IslandBiomeEnchantedIsland(int biomeID, KnowledgeRegistration knowledgeRegistration){
		super(biomeID,knowledgeRegistration);
		
		contentVariations.add(HOMELAND);
		
		getSpawnEntries(HOMELAND).addAll(new SpawnEntry[]{
			new SpawnEntry(EntityMobEnderGuardian.class,9,30),
			new SpawnEntry(EntityMobBabyEnderman.class,16,20)
		});
	}

	@Override
	protected void decorate(LargeStructureWorld world, Random rand, int centerX, int centerZ){
		if (data.content == HOMELAND)decorator.genHomeland();
	}
	
	@Override
	public float getIslandMassHeightMultiplier(){
		return 0.8F;
	}
	
	@Override
	public float getIslandFillFactor(){
		return 0.92F;
	}
	
	@Override
	public float getCaveAmountMultiplier(){
		return 0.45F;
	}
	
	@Override
	public float getCaveBranchingChance(){
		return 0.005F;
	}
	
	@Override
	public float getOreAmountMultiplier(){
		return 1.25F;
	}

	@Override
	protected IslandBiomeDecorator getDecorator(){
		return decorator;
	}
	
	@Override
	public int getTopBlockMeta(){
		return BlockEndstoneTerrain.metaEnchanted;
	}
}