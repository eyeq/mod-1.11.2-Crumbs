package eyeq.crumbs;

import eyeq.util.client.model.UModelCreator;
import eyeq.util.client.model.UModelLoader;
import eyeq.util.client.model.gson.ItemmodelJsonFactory;
import eyeq.util.client.renderer.ResourceLocationFactory;
import eyeq.util.client.resource.ULanguageCreator;
import eyeq.util.client.resource.lang.LanguageResourceManager;
import eyeq.util.oredict.CategoryTypes;
import eyeq.util.oredict.UOreDictionary;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import eyeq.crumbs.entity.EntityBait;
import eyeq.crumbs.event.CrumbsEventHandler;
import eyeq.crumbs.item.ItemCrumbs;
import eyeq.crumbs.item.ItemFishingGloves;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.io.File;

import static eyeq.crumbs.Crumbs.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
@Mod.EventBusSubscriber
public class Crumbs {
    public static final String MOD_ID = "eyeq_crumbs";

    @Mod.Instance(MOD_ID)
    public static Crumbs instance;

    private static final ResourceLocationFactory resource = new ResourceLocationFactory(MOD_ID);

    public static Item crumbs;
    public static Item fishingGloves;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CrumbsEventHandler());
        addRecipes();
        registerEntities();
        if(event.getSide().isServer()) {
            return;
        }
        renderItemModels();
        createFiles();
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event) {
        ToolMaterial toolMaterialLeather = EnumHelper.addToolMaterial("leather", 0, 320, 1.0F, -2.0F, 5);

        crumbs = new ItemCrumbs(1, 0.0F, false).setUnlocalizedName("crumbs");
        fishingGloves = new ItemFishingGloves(toolMaterialLeather).setUnlocalizedName("fishingGloves");

        toolMaterialLeather.setRepairItem(new ItemStack(Items.LEATHER));

        GameRegistry.register(crumbs, resource.createResourceLocation("crumbs"));
        GameRegistry.register(fishingGloves, resource.createResourceLocation("fishing_gloves"));

        UOreDictionary.registerOre(CategoryTypes.COOKED, "crumbs", crumbs);
    }

    public static void addRecipes() {
        GameRegistry.addShapelessRecipe(new ItemStack(crumbs, 6), Items.BREAD);
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fishingGloves), "X X",
                'X', UOreDictionary.OREDICT_LEATHER));
    }

    public static void registerEntities() {
        EntityRegistry.registerModEntity(resource.createResourceLocation("Bait"), EntityBait.class, "Bait", 0, instance, 160, 1, false, 0xF5CA7C, 0xD3B273);
    }

    @SideOnly(Side.CLIENT)
    public static void renderItemModels() {
        UModelLoader.setCustomModelResourceLocation(crumbs);
        UModelLoader.setCustomModelResourceLocation(fishingGloves);
    }

    public static void createFiles() {
        File project = new File("../1.11.2-Crumbs");

        LanguageResourceManager language = new LanguageResourceManager();

        language.register(LanguageResourceManager.EN_US, crumbs, "Teared Bread");
        language.register(LanguageResourceManager.JA_JP, crumbs, "ちぎったパン");
        language.register(LanguageResourceManager.EN_US, fishingGloves, "Fishing Gloves");
        language.register(LanguageResourceManager.JA_JP, fishingGloves, "釣り手袋");

        language.register(LanguageResourceManager.EN_US, EntityBait.class, "Bait");
        language.register(LanguageResourceManager.JA_JP, EntityBait.class, "釣餌");

        ULanguageCreator.createLanguage(project, MOD_ID, language);

        UModelCreator.createItemJson(project, crumbs, ItemmodelJsonFactory.ItemmodelParent.GENERATED);
        UModelCreator.createItemJson(project, fishingGloves, ItemmodelJsonFactory.ItemmodelParent.GENERATED);
    }
}
