package chylex.hee.entity.item;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.mechanics.orb.WeightedItem;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C12InstabilityOrbTransformEffect;
import chylex.hee.system.util.DragonUtil;

public class EntityItemInstabilityOrb extends EntityItem{
	private boolean isTntNearby = false;
	private boolean canExplode = true;
	
	public EntityItemInstabilityOrb(World world){
		super(world);
	}
	
	public EntityItemInstabilityOrb(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
		
		for(int a = 0; a < is.stackSize-1; a++){
			ItemStack newIS = is.copy();
			newIS.stackSize = 1;
			EntityItem item = new EntityItemInstabilityOrb(world,x,y,z,newIS);
			item.delayBeforeCanPickup = 40;
			world.spawnEntityInWorld(item);
		}
		
		is.stackSize = 1;
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (!worldObj.isRemote){
			age += 1+rand.nextInt(2+rand.nextInt(4))*rand.nextInt(3);
			if (age >= 666)detonate();
			else if (!isTntNearby && (ticksExisted&3) == 1){
				for(Object o:worldObj.getEntitiesWithinAABB(EntityTNTPrimed.class,boundingBox.expand(4D,4D,4D))){
					EntityTNTPrimed tnt = (EntityTNTPrimed)o;
					
					if (tnt.fuse < 6 && getDistance(tnt.posX,tnt.posY,tnt.posZ)/4D <= 1D){ // 4 = strength of TNT
						isTntNearby = true;
						
						for(EntityPlayer observer:ObservationUtil.getAllObservers(this,16D)){
							KnowledgeRegistrations.INSTABILITY_ORB.tryUnlockFragment(observer,0.6F,new byte[]{ 5 });
						}
						
						break;
					}
				}
			}
			
			if (ticksExisted%90 == 0){
				EntityPlayer observer = ObservationUtil.getRandomObserver(this,16D);
				if (observer != null)KnowledgeRegistrations.INSTABILITY_ORB.tryUnlockFragment(observer,0.12F);
			}
		}
		else{
			int chance = age/17-7;
			for(int a = 0; a < Math.min(7,(chance <= 0 ? (rand.nextInt(Math.abs(chance)+1) == 0 ? 1 : 0) : chance)); a++){
				HardcoreEnderExpansion.fx.instability(this);
			}
			
			if (rand.nextInt(3+Math.max(0,(int)((680F-age)/70F))) == 0)worldObj.playSound(posX,posY,posZ,"random.pop",0.15F+rand.nextFloat()*0.1F,0.7F+rand.nextFloat()*0.6F,false);
		}
	}
	
	private void detonate(){
		if (rand.nextInt(6) == 0 && canExplode){
			DragonUtil.createExplosion(this,posX,posY,posZ,2.8F+rand.nextFloat()*0.8F,rand.nextInt(3) == 0);
		}
		else if (rand.nextInt(6) == 0){
			Class<?> cls = null;
			int ele = rand.nextInt(OrbSpawnableMobs.classList.size());
			Iterator<Class<?>> iter = OrbSpawnableMobs.classList.iterator();
			
			while(iter.hasNext()){
				cls = iter.next();
				if (--ele < 0)break;
			}
			
			try{
				Entity e = (Entity)cls.getConstructor(World.class).newInstance(worldObj);
				e.setPositionAndRotation(posX,posY,posZ,rand.nextFloat()*360F-180F,0F);
				worldObj.spawnEntityInWorld(e);
				
				PacketPipeline.sendToAllAround(this,64D,new C12InstabilityOrbTransformEffect(posX,posY,posZ,e.width,e.height));
			}catch(Exception ex){
				DragonUtil.severe("Error spawning entity %0% in EntityItemInstabilityOrb",cls.getSimpleName());
			}
		}
		else{
			WeightedItem item = OrbAcquirableItems.idList.getRandomItem(rand);
			int meta = item.getDamageValues()[rand.nextInt(item.getDamageValues().length)];
			if (meta == 32767)meta = 0;
			
			EntityItem entityitem = new EntityItem(worldObj,posX,posY,posZ,new ItemStack(item.getItem(),1,meta));
			entityitem.motionX = entityitem.motionY = entityitem.motionZ = 0D;
			entityitem.delayBeforeCanPickup = 10;
			worldObj.spawnEntityInWorld(entityitem);
			
			PacketPipeline.sendToAllAround(this,64D,new C12InstabilityOrbTransformEffect(posX,posY,posZ,0.25F,0.4F));
		}
		
		for(EntityPlayer observer:ObservationUtil.getAllObservers(this,16D))KnowledgeRegistrations.INSTABILITY_ORB.tryUnlockFragment(observer,0.35F);
		
		setDead();
	}
	
	@Override
	public boolean combineItems(EntityItem item){
		return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isExplosion()){
			age -= 10-rand.nextInt(80);
			if (isTntNearby)canExplode = false;
			return false;
		}
		else return super.attackEntityFrom(source,amount);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("canExplode",canExplode);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		canExplode = nbt.getBoolean("canExplode");
	}
}