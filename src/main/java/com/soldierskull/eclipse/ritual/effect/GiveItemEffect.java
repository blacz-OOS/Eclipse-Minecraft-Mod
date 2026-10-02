package com.soldierskull.eclipse.ritual.effect;

import java.util.function.Supplier;

import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualEffect;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

/**
 * Entrega um item ao jogador que iniciou o ritual. Usa
 * {@link Supplier} em vez de um {@link ItemStack} fixo pra permitir NBT
 * variável (ex.: o Charged Abyss Crystal do {@code abyss_crystal_charging}
 * precisa nascer com {@code Charges: 3} - Bloco F/#48).
 */
public class GiveItemEffect implements RitualEffect {

    private final Supplier<ItemStack> stackSupplier;

    public GiveItemEffect(Supplier<ItemStack> stackSupplier) {
        this.stackSupplier = stackSupplier;
    }

    @Override
    public void apply(RitualContext context) {
        if (context.getInitiator() == null) {
            return;
        }
        MinecraftServer server = context.getWorld().getServer();
        if (server == null) {
            return;
        }
        ServerPlayerEntity player = server.getPlayerList().getPlayer(context.getInitiator());
        if (player == null) {
            return; // jogador desconectou - item fica perdido neste protótipo (TODO: dropar no altar)
        }
        ItemStack stack = this.stackSupplier.get();
        if (!player.inventory.add(stack)) {
            player.drop(stack, false);
        }
    }
}
