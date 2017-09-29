package de.technikforlife.firstaid.client;

import de.technikforlife.firstaid.FirstAid;
import de.technikforlife.firstaid.FirstAidConfig;
import de.technikforlife.firstaid.client.gui.GuiApplyHealthItem;
import de.technikforlife.firstaid.client.gui.HUDHandler;
import de.technikforlife.firstaid.client.tutorial.GuiTutorial;
import de.technikforlife.firstaid.damagesystem.capability.PlayerDataManager;
import de.technikforlife.firstaid.items.FirstAidItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(FirstAidItems.BANDAGE, 0, new ModelResourceLocation("firstaid:bandage"));
        ModelLoader.setCustomModelResourceLocation(FirstAidItems.PLASTER, 0, new ModelResourceLocation("firstaid:plaster"));
        ModelLoader.setCustomModelResourceLocation(FirstAidItems.MORPHINE, 0, new ModelResourceLocation("firstaid:morphine"));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase entityLiving = event.getEntityLiving();
        if (entityLiving instanceof EntityPlayer) {
            if (entityLiving.getName().equals(Minecraft.getMinecraft().player.getName()) && GuiApplyHealthItem.isOpen)
                Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        if (ClientProxy.showWounds.isPressed()) {
            if (!FirstAidConfig.hasTutorial) {
                FirstAidConfig.hasTutorial = true;
                ConfigManager.sync(FirstAid.MODID, Config.Type.INSTANCE);
                Minecraft.getMinecraft().displayGuiScreen(new GuiTutorial());
            }
            else {
                Minecraft.getMinecraft().displayGuiScreen(new GuiApplyHealthItem(PlayerDataManager.getDamageModel(Minecraft.getMinecraft().player)));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        if (!FirstAidConfig.hasTutorial)
            event.player.sendMessage(new TextComponentString("[First Aid] Press " + ClientProxy.showWounds.getDisplayName() + " for the tutorial"));
    }

    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent.Post event) {
        RenderGameOverlayEvent.ElementType type = event.getType();
        if (type == RenderGameOverlayEvent.ElementType.HEALTH) {
            GuiIngameForge.renderHealth = false;
            event.setCanceled(true);
        } else if (type == RenderGameOverlayEvent.ElementType.ALL)
            HUDHandler.renderOverlay(event.getResolution());
    }
}
