package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketSyncStats;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import com.soldierskull.eclipse.stats.StatBalance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * Temporary Corruption source, as specified: "Until the final systems
 * are implemented, temporarily use the Abyss Shard item to restore
 * Corruption." Reuses the existing PlayerStats capability rather than
 * inventing a new energy-storage mechanism.
 */
/**
 * Fonte de Energia Abissal/Corrupção ao usar. A quantidade restaurada é
 * configurável no construtor - reaproveitada pelo Abyss Shard (original),
 * Abyss Crystal e Abyss Core (Fase 10), cada um restaurando mais que o
 * anterior conforme a raridade do material.
 */
public class AbyssShardItem extends Item {

    private final int restoreAmount;

    public AbyssShardItem(Item.Properties properties) {
        this(properties, StatBalance.ABYSS_SHARD_RESTORE_AMOUNT);
    }

    public AbyssShardItem(Item.Properties properties, int restoreAmount) {
        super(properties);
        this.restoreAmount = restoreAmount;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (world.isClientSide) {
            return ActionResult.pass(stack);
        }
        if (!(player instanceof ServerPlayerEntity)) {
            return ActionResult.pass(stack);
        }
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        return serverPlayer.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).map(stats -> {
            boolean restored = stats.restoreCorruption(this.restoreAmount);
            if (!restored) {
                return ActionResult.fail(stack);
            }
            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketSyncStats(stats));
            return ActionResult.success(stack);
        }).orElse(ActionResult.pass(stack));
    }
}
