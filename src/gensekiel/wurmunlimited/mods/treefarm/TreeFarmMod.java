package gensekiel.wurmunlimited.mods.treefarm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class TreeFarmMod implements
	WurmServerMod,
	Initable,
	PreInitable,
	Configurable,
	ServerStartedListener
{
//======================================================================
	public static WateringAction wateringaction = new WateringAction();
	public static FertilizingAction fertilizingaction = new FertilizingAction();
	public static HedgeAction hedgeaction = new HedgeAction();
	public static GrassGrowAction grassaction = new GrassGrowAction();
	public static TreeGrassAction treegrassaction = new TreeGrassAction();
	public static KelpReedGrowAction kelpreedaction = new KelpReedGrowAction();
	public static ForageBotanizeAction foragebotanizeaction = new ForageBotanizeAction();
	public static TrellisFruitAction trellisfruitaction = new TrellisFruitAction();
	public static TrellisAgeAction trellisageaction = new TrellisAgeAction();
	public static PlanterFruitAction planterfruitaction = new PlanterFruitAction();
	public static PlanterAgeAction planterageaction = new PlanterAgeAction();
	public static FarmGrowAction farmgrowaction = new FarmGrowAction();
	public static FarmFertAction farmfertaction = new FarmFertAction();

	private static boolean allowGrow = true;
	private static boolean allowFert = true;
	private static boolean allowHedges = true;
	private static boolean allowGrass = true;
	private static boolean allowFandB = true;
	private static boolean allowTrell = true;
	private static boolean allowPlant = true;
	private static boolean allowFarms = true;
	private static boolean augmentExamine = true;
	private static boolean debug = false;
	private static boolean useOriginalGrowthFunction = false;
//======================================================================
	@Override
	public void onServerStarted()
	{
		boolean kelpreed = (KelpReedGrowAction.getAllowReed() || KelpReedGrowAction.getAllowKelp());
		boolean treebush = (TileAction.getAllowTrees() || TileAction.getAllowBushes());
		if(allowGrow && treebush)    wateringaction.register();
		if(allowFert && treebush)    fertilizingaction.register();
		if(allowGrow && allowHedges) hedgeaction.register();
		if(allowGrow && allowGrass)  grassaction.register();
		if(allowGrow && allowGrass)  treegrassaction.register();
		if(allowGrow && kelpreed)    kelpreedaction.register();
		if(allowFert && allowFandB)  foragebotanizeaction.register();
		if(allowFert && allowTrell)  trellisfruitaction.register();
		if(allowGrow && allowTrell)  trellisageaction.register();
		if(allowFert && allowPlant)  planterfruitaction.register();
		if(allowGrow && allowPlant)  planterageaction.register();
		if(allowGrow && allowFarms)  farmgrowaction.register();
		if(allowFert && allowFarms)  farmfertaction.register();
		if(augmentExamine)           ModActions.registerAction(new ExamineAction());

		if(debug){
			WateringAction wa2 = new WateringAction("Water (debug)");
			FertilizingAction fa2 = new FertilizingAction("Fertilize (debug)");
			HedgeAction ha2 = new HedgeAction("Water (debug)");
			GrassGrowAction ga2 = new GrassGrowAction("Water ground (debug)");
			TreeGrassAction tg2 = new TreeGrassAction("Water ground (debug)");
			KelpReedGrowAction kr2 = new KelpReedGrowAction("Fertilize (debug)");
			ForageBotanizeAction fb2 = new ForageBotanizeAction("Fertilize ground (debug)");
			TrellisFruitAction ia2 = new TrellisFruitAction("Fertilize (debug)");
			TrellisAgeAction ia3 = new TrellisAgeAction("Water (debug)");
			PlanterFruitAction pa2 = new PlanterFruitAction("Fertilize (debug)");
			PlanterAgeAction pl2 = new PlanterAgeAction("Water (debug)");
			FarmGrowAction fg2 = new FarmGrowAction("Water (debug)");
			FarmFertAction ff2 = new FarmFertAction("Fertilize (debug)");

			wa2.setCost(0); wa2.setTime(0); wa2.setItem(0);
			fa2.setCost(0); fa2.setTime(0); fa2.setItem(0);
			ha2.setCost(0); ha2.setTime(0); ha2.setItem(0);
			ga2.setCost(0); ga2.setTime(0); ga2.setItem(0);
			tg2.setCost(0); tg2.setTime(0); tg2.setItem(0);
			kr2.setCost(0); kr2.setTime(0); kr2.setItem(0);
			fb2.setCost(0); fb2.setTime(0); fb2.setItem(0);
			ia2.setCost(0); ia2.setTime(0); ia2.setItem(0);
			ia3.setCost(0); ia3.setTime(0); ia3.setItem(0);
			pa2.setCost(0); pa2.setTime(0); pa2.setItem(0);
			pl2.setCost(0); pl2.setTime(0); pl2.setItem(0);
			fg2.setCost(0); fg2.setTime(0); fg2.setItem(0);
			ff2.setCost(0); ff2.setTime(0); ff2.setItem(0);

			wa2.register();
			fa2.register();
			ha2.register();
			ga2.register();
			tg2.register();
			kr2.register();
			fb2.register();
			ia2.register();
			ia3.register();
			pa2.register();
			pl2.register();
			fg2.register();
			ff2.register();

			new ResetAction().register();
			new BlessAction().register();
			new HedgePollAction().register();
			new SkillAction("-> Max skills!").register();
			new NoSkillAction("-> Wipe skills!").register();
			new GainSomeSkillAction("-> Max relevant skills!").register();
			new WipeSomeSkillAction("-> Wipe relevant skills!").register();
			new SproutAction("-> Sprouts! Now!").register();
			new NoSproutAction("-> Delete sprouts!").register();
			new FeedAction("-> Feed me").register();
			new ReconfigureAction("-> Reconfigure").register();
		}
	}
//======================================================================
	@Override
	public void init()
	{
		if(useOriginalGrowthFunction){
			Hooks.injectTreeGrowthWrapper();
			Hooks.injectGrassGrowthWrapper();
			Hooks.injectTreeGrassGrowthWrapper();
			Hooks.injectSeedGrowthWrapper();
			Hooks.injectFarmGrowthWrapper();
		}

		Hooks.registerListLoadingHook();
		Hooks.registerPollingHook();
		Hooks.registerListSavingHook();

		if(TaskPoller.getProtectTasks()){
			Hooks.registerTreeProtectionHook();
			Hooks.registerHedgeProtectionHook();
			Hooks.registerGrassProtectionHook();
			Hooks.registerTreeGrassProtectionHook();
			Hooks.registerSeedProtectionHook();
			Hooks.registerFarmProtectionHook();
		}
	}
//======================================================================
	@Override
	public void preInit()
	{
		ModActions.init();
	}
//======================================================================
	private static Integer getOption(String option, Integer default_value, Properties properties){ return Integer.valueOf(properties.getProperty(option, Integer.toString(default_value))); }
	private static Double getOption(String option, Double default_value, Properties properties){ return Double.valueOf(properties.getProperty(option, Double.toString(default_value))); }
	private static Long getOption(String option, Long default_value, Properties properties){ return Long.valueOf(properties.getProperty(option, Long.toString(default_value))); }
	private static Boolean getOption(String option, Boolean default_value, Properties properties){ return Boolean.valueOf(properties.getProperty(option, Boolean.toString(default_value))); }
	private static Byte getOption(String option, Byte default_value, Properties properties){ return Byte.valueOf(properties.getProperty(option, Byte.toString(default_value))); }
//======================================================================
	public static boolean reconfigure()
	{
		Path propfile = Paths.get("mods", "modtreefarm.properties");
		Properties properties = new Properties();
		if(Files.exists(propfile)){
			try(InputStream is = Files.newInputStream(propfile)){
				properties.load(is);
				reconfigure(properties);
			}catch(IOException e){
				return false;
			}
		}else return false;
		return true;
	}
//======================================================================
	public static void reconfigure(Properties properties)
	{
		debug = getOption("Debug", debug, properties);
		ExamineAction.setDebug(debug);
		allowGrow = getOption("AllowGrow", allowGrow, properties);
		allowFert = getOption("AllowFertilize", allowFert, properties);
		allowHedges = getOption("AllowHedges", allowHedges, properties);
		allowGrass = getOption("AllowGrass", allowGrass, properties);
		allowFandB = getOption("AllowForageBotanize", allowFandB, properties);
		allowTrell = getOption("AllowTrellises", allowTrell, properties);
		allowPlant = getOption("AllowPlanters", allowPlant, properties);
		allowFarms = getOption("AllowFarms", allowFarms, properties);
		augmentExamine = getOption("StatusOnExamine", augmentExamine, properties);

		AbstractAction.setObeyProtection(getOption("ObeyProtection", AbstractAction.getObeyProtection(), properties));

		TileAction.setAllowTrees(getOption("AllowTrees", TileAction.getAllowTrees(), properties));
		TileAction.setAllowBushes(getOption("AllowBushes", TileAction.getAllowBushes(), properties));
		KelpReedGrowAction.setAllowReed(getOption("AllowReed", KelpReedGrowAction.getAllowReed(), properties));
		KelpReedGrowAction.setAllowKelp(getOption("AllowKelp", KelpReedGrowAction.getAllowKelp(), properties));
		GrassGrowAction.setAllowFlowers(getOption("AllowFlowers", GrassGrowAction.getAllowFlowers(), properties));

		wateringaction.setCost(getOption("WateringCost", wateringaction.getCost(), properties));
		wateringaction.setTime(getOption("WateringTime", wateringaction.getTime(), properties));
		wateringaction.setItem(getOption("WateringItem", wateringaction.getItem(), properties));

		hedgeaction.setCost(getOption("WateringCost", hedgeaction.getCost(), properties));
		hedgeaction.setTime(getOption("WateringTime", hedgeaction.getTime(), properties));
		hedgeaction.setItem(getOption("WateringItem", hedgeaction.getItem(), properties));

		grassaction.setCost(getOption("WateringCost", grassaction.getCost(), properties));
		grassaction.setTime(getOption("WateringTime", grassaction.getTime(), properties));
		grassaction.setItem(getOption("WateringItem", grassaction.getItem(), properties));

		treegrassaction.setCost(getOption("WateringCost", treegrassaction.getCost(), properties));
		treegrassaction.setTime(getOption("WateringTime", treegrassaction.getTime(), properties));
		treegrassaction.setItem(getOption("WateringItem", treegrassaction.getItem(), properties));

		kelpreedaction.setCost(getOption("FertilizingCost", kelpreedaction.getCost(), properties));
		kelpreedaction.setTime(getOption("FertilizingTime", kelpreedaction.getTime(), properties));
		kelpreedaction.setItem(getOption("FertilizingItem", kelpreedaction.getItem(), properties));

		fertilizingaction.setCost(getOption("FertilizingCost", fertilizingaction.getCost(), properties));
		fertilizingaction.setTime(getOption("FertilizingTime", fertilizingaction.getTime(), properties));
		fertilizingaction.setItem(getOption("FertilizingItem", fertilizingaction.getItem(), properties));

		foragebotanizeaction.setCost(getOption("FertilizingCost", foragebotanizeaction.getCost(), properties));
		foragebotanizeaction.setTime(getOption("FertilizingTime", foragebotanizeaction.getTime(), properties));
		foragebotanizeaction.setItem(getOption("FertilizingItem", foragebotanizeaction.getItem(), properties));

		trellisfruitaction.setCost(getOption("FertilizingCost", trellisfruitaction.getCost(), properties));
		trellisfruitaction.setTime(getOption("FertilizingTime", trellisfruitaction.getTime(), properties));
		trellisfruitaction.setItem(getOption("FertilizingItem", trellisfruitaction.getItem(), properties));

		trellisageaction.setCost(getOption("WateringCost", trellisageaction.getCost(), properties));
		trellisageaction.setTime(getOption("WateringTime", trellisageaction.getTime(), properties));
		trellisageaction.setItem(getOption("WateringItem", trellisageaction.getItem(), properties));

		planterfruitaction.setCost(getOption("FertilizingCost", planterfruitaction.getCost(), properties));
		planterfruitaction.setTime(getOption("FertilizingTime", planterfruitaction.getTime(), properties));
		planterfruitaction.setItem(getOption("FertilizingItem", planterfruitaction.getItem(), properties));

		planterageaction.setCost(getOption("WateringCost", planterageaction.getCost(), properties));
		planterageaction.setTime(getOption("WateringTime", planterageaction.getTime(), properties));
		planterageaction.setItem(getOption("WateringItem", planterageaction.getItem(), properties));

		farmgrowaction.setCost(getOption("WateringCost", farmgrowaction.getCost(), properties));
		farmgrowaction.setTime(getOption("WateringTime", farmgrowaction.getTime(), properties));
		farmgrowaction.setItem(getOption("WateringItem", farmgrowaction.getItem(), properties));

		farmfertaction.setCost(getOption("FertilizingCost", farmfertaction.getCost(), properties));
		farmfertaction.setTime(getOption("FertilizingTime", farmfertaction.getTime(), properties));
		farmfertaction.setItem(getOption("FertilizingItem", farmfertaction.getItem(), properties));

		useOriginalGrowthFunction = getOption("UseOriginalGrowthFunction", useOriginalGrowthFunction, properties);
		Hooks.preventVanillaFarmGrowth = getOption("PreventVanillaFarmGrowth", Hooks.preventVanillaFarmGrowth, properties);
		TreeGrowTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);
		GrassGrowTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);
		TreeGrassTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);
		ForageBotanizeTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);
		FarmGrowTask.setUseOriginalGrowthFunction(useOriginalGrowthFunction);
		FarmGrowTask.setStateLock(getOption("RetainFarmState", FarmGrowTask.getStateLock(), properties));

		AbstractAction.setCostSkillMultiplier(getOption("CostSkillMultiplier", AbstractAction.getCostSkillMultiplier(), properties));
		AbstractAction.setCostAgeMultiplier(getOption("CostAgeMultiplier", AbstractAction.getCostAgeMultiplier(), properties));
		AbstractAction.setTimeSkillMultiplier(getOption("ActionTimeSkillMultiplier", AbstractAction.getTimeSkillMultiplier(), properties));
		AbstractAction.setGrowthTimeQualityMultiplier(getOption("TimeQualityMultiplier", AbstractAction.getGrowthTimeQualityMultiplier(), properties));
		AbstractAction.setGrowthTimeSkillMultiplier(getOption("TimeSkillMultiplier", AbstractAction.getGrowthTimeSkillMultiplier(), properties));
		AbstractAction.setGainSkill(getOption("GainSkill", AbstractAction.getGainSkill(), properties));

		AbstractAction.setCheckIfPolled(getOption("CheckIfPolled", AbstractAction.getCheckIfPolled(), properties));
		AbstractAction.setCheckConditions(getOption("CheckConditions", AbstractAction.getCheckConditions(), properties));

		CoolDownTask.setCoolDownMultiplier(getOption("CoolDownMultiplier", CoolDownTask.getCoolDownMultiplier(), properties));

		TaskPoller.setPollInterval(getOption("PollInterval", TaskPoller.getPollInterval(), properties));
		TaskPoller.setPreserveList(getOption("PreserveList", TaskPoller.getPreserveList(), properties));
		TaskPoller.setProtectTasks(getOption("ProtectTasks", TaskPoller.getProtectTasks(), properties));

		AbstractTask.setCheckForWUPoll(getOption("CheckForWUPoll", AbstractTask.getCheckForWUPoll(), properties));
		AbstractTask.setBaseGrowthTime(getOption("BaseTaskTime", AbstractTask.getBaseGrowthTime(), properties));

		TreeGrowTask.setAgeLimit(getOption("TreeAgeLimit", TreeGrowTask.getAgeLimit(), properties));
		HedgeTask.setAgeLimit(getOption("HedgeAgeLimit", HedgeTask.getAgeLimit(), properties));
		GrassGrowTask.setAgeLimit(getOption("GrassAgeLimit", GrassGrowTask.getAgeLimit(), properties));
		TreeGrassTask.setAgeLimit(getOption("GrassAgeLimit", TreeGrassTask.getAgeLimit(), properties));
		TrellisAgeTask.setAgeLimit(getOption("TrellisAgeLimit", TrellisAgeTask.getAgeLimit(), properties));
		PlanterAgeTask.setAgeLimit(getOption("PlanterAgeLimit", PlanterAgeTask.getAgeLimit(), properties));
		FarmGrowTask.setAgeLimit(getOption("FarmAgeLimit", FarmGrowTask.getAgeLimit(), properties));

		double growthMultiplierGrow = getOption("TimeMultiplierGrow", 1.0, properties);
		TreeGrowTask .setGrowthMultiplier(growthMultiplierGrow);
		GrassGrowTask.setGrowthMultiplier(growthMultiplierGrow);
		TreeGrassTask.setGrowthMultiplier(growthMultiplierGrow);
		HedgeTask.setGrowthMultiplier(growthMultiplierGrow * getOption("TimeMultiplierHedge", HedgeTask.getGrowthMultiplier(), properties));
		FruitTask.setGrowthMultiplier(getOption("TimeMultiplierFruit", FruitTask.getGrowthMultiplier(), properties));
		TreeTileTask.setGrowthMultiplierTree(getOption("TimeMultiplierTree", TreeTileTask.getGrowthMultiplierTree(), properties));
		TreeTileTask.setGrowthMultiplierBush(getOption("TimeMultiplierBush", TreeTileTask.getGrowthMultiplierBush(), properties));
		GrassTileTask.setGrowthMultiplier(getOption("TimeMultiplierGrass", GrassTileTask.getGrowthMultiplier(), properties));
		ForageBotanizeTask.setGrowthMultiplier(getOption("TimeMultiplierForageBotanize", ForageBotanizeTask.getGrowthMultiplier(), properties));
		FlowerGrowTask.setGrowthMultiplier(growthMultiplierGrow * getOption("TimeMultiplierFlowers", FlowerGrowTask.getGrowthMultiplier(), properties));
		TrellisTask.setGrowthMultiplier(getOption("TimeMultiplierTrellises", TrellisTask.getGrowthMultiplier(), properties));
		TrellisFruitTask.setGrowthMultiplier(getOption("TimeMultiplierFruit", TrellisFruitTask.getGrowthMultiplier(), properties));
		TrellisAgeTask.setGrowthMultiplier(getOption("TimeMultiplierGrow", TrellisAgeTask.getGrowthMultiplier(), properties));
		PlanterTask.setGrowthMultiplier(getOption("TimeMultiplierPlanters", PlanterTask.getGrowthMultiplier(), properties));
		PlanterFruitTask.setGrowthMultiplier(getOption("TimeMultiplierFruit", PlanterFruitTask.getGrowthMultiplier(), properties));
		PlanterAgeTask.setGrowthMultiplier(getOption("TimeMultiplierGrow", PlanterAgeTask.getGrowthMultiplier(), properties));
		FarmGrowTask.setGrowthMultiplier(growthMultiplierGrow * getOption("TimeMultiplierFarms", FarmGrowTask.getGrowthMultiplier(), properties));

		PlanterAgeTask.setPlanterAgeStep(getOption("PlanterAgeStep", PlanterAgeTask.getPlanterAgeStep(), properties));
		PlanterFruitAction.setCostMultiplier(getOption("CostMultiplierPlanters", PlanterFruitAction.getCostMultiplier(), properties));
		PlanterAgeAction.setCostMultiplier(getOption("CostMultiplierPlanters", PlanterAgeAction.getCostMultiplier(), properties));

		TreeTileTask.setGrowthMultiplierBirch   (getOption("TimeMultiplierBirch",    TreeTileTask.getGrowthMultiplierBirch(),    properties));
		TreeTileTask.setGrowthMultiplierPine    (getOption("TimeMultiplierPine",     TreeTileTask.getGrowthMultiplierPine(),     properties));
		TreeTileTask.setGrowthMultiplierOak     (getOption("TimeMultiplierOak",      TreeTileTask.getGrowthMultiplierOak(),      properties));
		TreeTileTask.setGrowthMultiplierCedar   (getOption("TimeMultiplierCedar",    TreeTileTask.getGrowthMultiplierCedar(),    properties));
		TreeTileTask.setGrowthMultiplierWillow  (getOption("TimeMultiplierWillow",   TreeTileTask.getGrowthMultiplierWillow(),   properties));
		TreeTileTask.setGrowthMultiplierMaple   (getOption("TimeMultiplierMaple",    TreeTileTask.getGrowthMultiplierMaple(),    properties));
		TreeTileTask.setGrowthMultiplierApple   (getOption("TimeMultiplierApple",    TreeTileTask.getGrowthMultiplierApple(),    properties));
		TreeTileTask.setGrowthMultiplierLemon   (getOption("TimeMultiplierLemon",    TreeTileTask.getGrowthMultiplierLemon(),    properties));
		TreeTileTask.setGrowthMultiplierOlive   (getOption("TimeMultiplierOlive",    TreeTileTask.getGrowthMultiplierOlive(),    properties));
		TreeTileTask.setGrowthMultiplierCherry  (getOption("TimeMultiplierCherry",   TreeTileTask.getGrowthMultiplierCherry(),   properties));
		TreeTileTask.setGrowthMultiplierChestnut(getOption("TimeMultiplierChestnut", TreeTileTask.getGrowthMultiplierChestnut(), properties));
		TreeTileTask.setGrowthMultiplierWalnut  (getOption("TimeMultiplierWalnut",   TreeTileTask.getGrowthMultiplierWalnut(),   properties));
		TreeTileTask.setGrowthMultiplierFir     (getOption("TimeMultiplierFir",      TreeTileTask.getGrowthMultiplierFir(),      properties));
		TreeTileTask.setGrowthMultiplierLinden  (getOption("TimeMultiplierLinden",   TreeTileTask.getGrowthMultiplierLinden(),   properties));
		TreeTileTask.setGrowthMultiplierOrange  (getOption("TimeMultiplierOrange",   TreeTileTask.getGrowthMultiplierOrange(),   properties));

		TileTask.setGrowthMultiplierNormal   (getOption("TimeMultiplierNormal",    TileTask.getGrowthMultiplierNormal(),    properties));
		TileTask.setGrowthMultiplierEnchanted(getOption("TimeMultiplierEnchanted", TileTask.getGrowthMultiplierEnchanted(), properties));
		TileTask.setGrowthMultiplierMycelium (getOption("TimeMultiplierMycelium",  TileTask.getGrowthMultiplierMycelium(),  properties));

		TreeTileTask.setGrowthMultiplierCamellia (getOption("TimeMultiplierCamellia",  TreeTileTask.getGrowthMultiplierCamellia(),  properties));
		TreeTileTask.setGrowthMultiplierGrape    (getOption("TimeMultiplierGrape",     TreeTileTask.getGrowthMultiplierGrape(),     properties));
		TreeTileTask.setGrowthMultiplierLavender (getOption("TimeMultiplierLavender",  TreeTileTask.getGrowthMultiplierLavender(),  properties));
		TreeTileTask.setGrowthMultiplierOleander (getOption("TimeMultiplierOleander",  TreeTileTask.getGrowthMultiplierOleander(),  properties));
		TreeTileTask.setGrowthMultiplierRose     (getOption("TimeMultiplierRose",      TreeTileTask.getGrowthMultiplierRose(),      properties));
		TreeTileTask.setGrowthMultiplierThorn    (getOption("TimeMultiplierThorn",     TreeTileTask.getGrowthMultiplierThorn(),     properties));
		TreeTileTask.setGrowthMultiplierHazelnut (getOption("TimeMultiplierHazelnut",  TreeTileTask.getGrowthMultiplierHazelnut(),  properties));
		TreeTileTask.setGrowthMultiplierBlueberry(getOption("TimeMultiplierBlueberry", TreeTileTask.getGrowthMultiplierBlueberry(), properties));
		TreeTileTask.setGrowthMultiplierRaspberry(getOption("TimeMultiplierRaspberry", TreeTileTask.getGrowthMultiplierRaspberry(), properties));

		for(int i = 0; i < 15; i++){
			TreeTileTask.setGrowthMultiplierAge( i, getOption("TimeMultiplierAge" + i, TreeTileTask.getGrowthMultiplierAge(i), properties));
		}
		for(int i = 0; i < 3; i++){
			GrassGrowTask.setGrowthMultiplierAge( i, getOption("TimeMultiplierAge" + i, GrassGrowTask.getGrowthMultiplierAge(i), properties));
		}
		for(int i = 0; i < 7; i++){
			FarmGrowTask.setGrowthMultiplierAge( i, getOption("TimeMultiplierAge" + i, FarmGrowTask.getGrowthMultiplierAge(i), properties));
		}

		AbstractAction.setChanceSkillMultiplier(getOption("ChanceSkillMultiplier", AbstractAction.getChanceSkillMultiplier(), properties));
		AbstractAction.setChanceQualityMultiplier(getOption("ChanceQualityMultiplier", AbstractAction.getChanceQualityMultiplier(), properties));
		AbstractAction.setRndSkillMultiplier(getOption("RandomSkillMultiplier", AbstractAction.getRndSkillMultiplier(), properties));
		AbstractAction.setRndQualityMultiplier(getOption("RandomQualityMultiplier", AbstractAction.getRndQualityMultiplier(), properties));

		TreeTileTask.setChanceTrees(getOption("ChanceTrees", TreeTileTask.getChanceTrees(), properties));
		TreeTileTask.setChanceBushes(getOption("ChanceBushes", TreeTileTask.getChanceBushes(), properties));
		HedgeTask.setChanceMultiplier(getOption("ChanceHedges", HedgeTask.getChanceMultiplier(), properties));
		GrassTileTask.setChanceMultiplier(getOption("ChanceGrass", GrassTileTask.getChanceMultiplier(), properties));
		FlowerGrowTask.setChanceMultiplier(getOption("ChanceFlowers", FlowerGrowTask.getChanceMultiplier(), properties));
		TrellisTask.setChanceMultiplier(getOption("ChanceTrellises", TrellisTask.getChanceMultiplier(), properties));
		PlanterTask.setChanceMultiplier(getOption("ChancePlanters", PlanterTask.getChanceMultiplier(), properties));
		FarmGrowTask.setChanceMultiplier(getOption("ChanceFarms", FarmGrowTask.getChanceMultiplier(), properties));
		ForageBotanizeTask.setChanceMultiplier(getOption("ChanceForageBotanize", ForageBotanizeTask.getChanceMultiplier(), properties));

		TreeTileTask.setRndTrees(getOption("RandomTimeTrees", TreeTileTask.getRndTrees(), properties));
		TreeTileTask.setRndBushes(getOption("RandomTimeBushes", TreeTileTask.getRndBushes(), properties));
		HedgeTask.setRndMultiplier(getOption("RandomTimeHedges", HedgeTask.getRndMultiplier(), properties));
		GrassTileTask.setRndMultiplier(getOption("RandomTimeGrass", GrassTileTask.getRndMultiplier(), properties));
		FlowerGrowTask.setRndMultiplier(getOption("RandomTimeFlowers", FlowerGrowTask.getRndMultiplier(), properties));
		TrellisTask.setRndMultiplier(getOption("RandomTimeTrellises", TrellisTask.getRndMultiplier(), properties));
		PlanterTask.setRndMultiplier(getOption("RandomTimePlanters", PlanterTask.getRndMultiplier(), properties));
		FarmGrowTask.setRndMultiplier(getOption("RandomTimeFarms", FarmGrowTask.getRndMultiplier(), properties));
		ForageBotanizeTask.setRndMultiplier(getOption("RandomTimeForageBotanize", ForageBotanizeTask.getRndMultiplier(), properties));
	}
//======================================================================
	@Override
	public void configure(Properties properties)
	{
		reconfigure(properties);
	}
//======================================================================
}
