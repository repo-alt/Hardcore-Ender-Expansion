package chylex.hee.world.biome;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeEndDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.logging.Log;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.WorldGenBlob;
import chylex.hee.world.feature.WorldGenEndPowderOre;
import chylex.hee.world.feature.WorldGenMeteoroid;
import chylex.hee.world.feature.WorldGenSpikes;

public class BiomeDecoratorHardcoreEnd extends BiomeEndDecorator{
	private final WorldGenBlob blobGen;
	private final WorldGenMeteoroid meteoroidGen;
	private final WorldGenEndPowderOre endPowderOreGen;
	
	public BiomeDecoratorHardcoreEnd(){
		spikeGen = new WorldGenSpikes();
		blobGen = new WorldGenBlob();
		meteoroidGen = new WorldGenMeteoroid();
		endPowderOreGen = new WorldGenEndPowderOre();
	}
	
	@Override
	protected void genDecorations(BiomeGenBase biome){
		if (currentWorld.provider.dimensionId != 1){
			super.genDecorations(biome);
			return;
		}
		
		DragonSavefile file = WorldDataHandler.get(DragonSavefile.class);
		
		randomGenerator.nextInt(1+file.getDragonDeathAmount()); // each time, the world is a little different
		generateOres();

		double distFromCenter = Math.sqrt(MathUtil.square(chunk_X>>4)+MathUtil.square(chunk_Z>>4))*16D;
		
		if (distFromCenter < 120D && randomGenerator.nextInt(5) == 0){
			int xx = chunk_X+randomGenerator.nextInt(16)+8;
			int zz = chunk_Z+randomGenerator.nextInt(16)+8;
			spikeGen.generate(currentWorld,randomGenerator,xx,currentWorld.getTopSolidOrLiquidBlock(xx,zz),zz);
		}
		
		if (distFromCenter > 102D && Math.abs(randomGenerator.nextGaussian()) < 0.285D){
			blobGen.prepare(chunk_X+8,chunk_Z+8);
			blobGen.generate(currentWorld,randomGenerator,chunk_X+randomGenerator.nextInt(10)+11,32+randomGenerator.nextInt(60),chunk_Z+randomGenerator.nextInt(10)+11);
		}
		
		if (distFromCenter > 480D && randomGenerator.nextFloat()*randomGenerator.nextFloat() > 0.666F){
			for(int a = 0; a < randomGenerator.nextInt(3); a++){
				meteoroidGen.generate(currentWorld,randomGenerator,chunk_X+randomGenerator.nextInt(16)+8,8+randomGenerator.nextInt(112),chunk_Z+randomGenerator.nextInt(16)+8);
			}
		}
		
		try{
			for(int attempt = 0, placed = 0, xx, yy, zz; attempt < 22 && placed < 4+randomGenerator.nextInt(5); attempt++){
				xx = chunk_X+randomGenerator.nextInt(16);
				yy = 35+randomGenerator.nextInt(92);
				zz = chunk_Z+randomGenerator.nextInt(16);
				
				if (currentWorld.getBlock(xx,yy,zz) == Blocks.end_stone && endPowderOreGen.generate(currentWorld,randomGenerator,xx,yy,zz)){
					++placed;
				}
			}
		}catch(Exception e){
			Log.error("End Powder Ore generation failed.");
		}

		if (chunk_X == 0 && chunk_Z == 0){
			file.setDragonExists();
			
			EntityBossDragon dragon = new EntityBossDragon(currentWorld);
			dragon.setLocationAndAngles(0D,128D,0D,randomGenerator.nextFloat()*360F,0F);
			currentWorld.spawnEntityInWorld(dragon);
		}
	}

	@Override
	protected void generateOres(){
		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(currentWorld,randomGenerator,chunk_X,chunk_Z));
		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(currentWorld,randomGenerator,chunk_X,chunk_Z));
	}
}
