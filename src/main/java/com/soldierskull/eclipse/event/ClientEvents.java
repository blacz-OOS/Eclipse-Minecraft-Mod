package com.soldierskull.eclipse.event;

import com.soldierskull.eclipse.client.KeyInit;
import com.soldierskull.eclipse.client.gui.StatsScreen;
import com.soldierskull.eclipse.network.PacketActivateSkill;
import com.soldierskull.eclipse.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(
        modid = "eclipse",
        value = {Dist.CLIENT}
)
public class ClientEvents {
    public ClientEvents() {
    }

    /**
     * Uses InputEvent.KeyInputEvent instead of polling
     * KeyBinding.consumeClick() on a ClientTickEvent.
     *
     * Forge fires KeyInputEvent for EVERY raw keypress, before Minecraft
     * even checks whether a Screen is open. That makes it the one
     * key-handling hook that's guaranteed to fire while our own
     * StatsScreen is the active screen. consumeClick()-based polling, by
     * contrast, relies on a click counter that Minecraft/Forge only
     * increments under certain screen-open conditions - in practice it's
     * unreliable for a "same key opens AND closes a GUI" pattern, even
     * with KeyConflictContext.UNIVERSAL set. Listening to the raw event
     * sidesteps that entirely.
     */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyInit.openStatusKey == null) {
            return;
        }
        // React only to the actual key-down press (ignore release/repeat).
        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }
        // Always compare against the key CURRENTLY bound (read live from
        // the KeyBinding) rather than a hardcoded keycode, so this keeps
        // working correctly even if the player rebinds the key in
        // Options > Controls.
        if (event.getKey() != KeyInit.openStatusKey.getKey().getValue()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof StatsScreen) {
            // Same key, screen already open -> close it.
            minecraft.setScreen(null);
        } else if (minecraft.screen == null) {
            // Same key, nothing else open -> open Stats. (Deliberately does
            // NOT open over top of some other screen - e.g. the player's
            // inventory or a chest - only when the game itself has focus.)
            minecraft.setScreen(new StatsScreen());
        }
    }

    /**
     * "Ataque Básico": envia ao servidor a intenção de ativar Presas
     * Vampíricas. Fixo nessa skill por enquanto (foi a única pedida
     * explicitamente por keybind na Fase 8) - uma GUI/hotbar de skills
     * futura usaria o mesmo PacketActivateSkill pra qualquer id.
     */
    @SubscribeEvent
    public static void onKeyInputAttackBasic(InputEvent.KeyInputEvent event) {
        if (KeyInit.attackBasicKey == null) {
            return;
        }
        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }
        if (event.getKey() != KeyInit.attackBasicKey.getKey().getValue()) {
            return;
        }
        if (Minecraft.getInstance().screen != null) {
            return; // nao dispara com alguma tela (inventario, GUI, etc.) aberta
        }
        PacketHandler.INSTANCE.sendToServer(new PacketActivateSkill("vampiro_presas_vampiricas"));
    }

    /**
     * Abre/fecha a SkillTreeScreen - mesmo raciocínio do onKeyInput
     * (openStatusKey) acima: usa o evento cru de tecla em vez de
     * consumeClick(), porque precisa continuar funcionando mesmo com a
     * própria tela já aberta (pra fechar com a mesma tecla).
     */
    @SubscribeEvent
    public static void onKeyInputOpenSkillTree(InputEvent.KeyInputEvent event) {
        if (KeyInit.openSkillTreeKey == null) {
            return;
        }
        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }
        if (event.getKey() != KeyInit.openSkillTreeKey.getKey().getValue()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof com.soldierskull.eclipse.client.gui.SkillTreeScreen) {
            minecraft.setScreen(null);
        } else if (minecraft.screen == null) {
            minecraft.setScreen(new com.soldierskull.eclipse.client.gui.SkillTreeScreen());
        }
    }

}
