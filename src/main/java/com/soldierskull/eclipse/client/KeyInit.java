package com.soldierskull.eclipse.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyInit {
    public static KeyBinding openStatusKey;
    /**
     * "Ataque Básico" - especificado na Fase 8 como a keybind que aciona
     * Presas Vampíricas (o único gatilho de habilidade que o documento
     * pediu explicitamente por nome). Outras habilidades ainda não têm
     * keybind própria - ver PacketActivateSkill para o ponto de entrada
     * genérico que uma futura GUI/hotbar de skills poderia usar.
     */
    public static KeyBinding attackBasicKey;

    /** Abre/fecha a SkillTreeScreen - mesmo padrão de openStatusKey
     *  (UNIVERSAL, pra funcionar como toggle mesmo com a tela já aberta). */
    public static KeyBinding openSkillTreeKey;

    public KeyInit() {
    }

    public static void register() {
        // KeyConflictContext.UNIVERSAL is the important part here.
        //
        // Forge's DEFAULT context is IN_GAME, which only lets a key
        // "click" register when NO Screen is currently open (vanilla
        // Minecraft skips KeyBinding.click() entirely while a GUI is on
        // screen, and Forge only overrides that skip for UNIVERSAL-context
        // bindings). Since this same key also needs to CLOSE the
        // StatsScreen while it's already open (see ClientEvents), it must
        // stay "clickable" regardless of whether a screen is showing.
        //
        // If you ever add another key that should behave normally (only
        // fire while no GUI is open), just don't pass a KeyConflictContext
        // for it - IN_GAME is fine for that case.
        openStatusKey = new KeyBinding(
                "key.eclipse.open_status",
                KeyConflictContext.UNIVERSAL,
                Type.KEYSYM,
                67, // 'C' key - unchanged from before
                "key.categories.eclipse"
        );
        ClientRegistry.registerKeyBinding(openStatusKey);

        attackBasicKey = new KeyBinding(
                "key.eclipse.attack_basic",
                KeyConflictContext.IN_GAME, // so dispara com nenhuma tela (GUI) aberta, diferente da openStatusKey
                Type.KEYSYM,
                71, // 'G' - livre por padrao; ajustavel em Opcoes > Controles
                "key.categories.eclipse"
        );
        ClientRegistry.registerKeyBinding(attackBasicKey);

        openSkillTreeKey = new KeyBinding(
                "key.eclipse.open_skilltree",
                KeyConflictContext.UNIVERSAL,
                Type.KEYSYM,
                75, // 'K' - livre por padrao
                "key.categories.eclipse"
        );
        ClientRegistry.registerKeyBinding(openSkillTreeKey);
    }
}
