package chylex.hee.entity.fx;

public final class FXType{
	public enum Basic{
		ESSENCE_ALTAR_SMOKE,
		LASER_BEAM_DESTROY,
		SPOOKY_LOG_DECAY,
		SPOOKY_LEAVES_DECAY,
		DUNGEON_PUZZLE_BURN,
		DRAGON_EGG_RESET,
		GEM_LINK,
		GEM_TELEPORT_TO,
		ENDER_PEARL_FREEZE,
		IGNEOUS_ROCK_MELT,
		ENDERMAN_BLOODLUST_TRANSFORMATION,
		LOUSE_ARMOR_HIT,
		HOMELAND_ENDERMAN_TP_OVERWORLD;
		
		public static FXType.Basic[] values = values();
	}
	
	public enum Entity{
		CHARM_CRITICAL,
		CHARM_WITCH,
		CHARM_BLOCK_EFFECT,
		CHARM_LAST_RESORT,
		GEM_TELEPORT_FROM,
		ORB_TRANSFORMATION,
		LOUSE_REGEN,
		HOMELAND_ENDERMAN_RECRUIT,
		BABY_ENDERMAN_GROW,
		ENDER_GUARDIAN_DASH;
		
		public static FXType.Entity[] values = values();
	}
	
	public enum Line{
		DRAGON_EGG_TELEPORT,
		CHARM_SLAUGHTER_IMPACT,
		CHARM_DAMAGE_REDIRECTION,
		LOUSE_HEAL_ENTITY,
		ENDERMAN_TELEPORT,
		DUNGEON_PUZZLE_TELEPORT,
		HOMELAND_ENDERMAN_GUARD_CALL;
		
		public static FXType.Line[] values = values();
	}
}
