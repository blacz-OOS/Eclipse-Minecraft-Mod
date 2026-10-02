package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketSyncStats;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

/**
 * TEMPLATE / EXAMPLE ITEM - not registered in ModItems by default.
 *
 * Right-clicking this item raises the player's Vampiric Affinity. It's
 * built by copying the exact same pattern as AbyssShardItem: get the
 * capability -> change one value on PlayerStats -> send a fresh
 * PacketSyncStats so the Status Screen bar updates immediately, without
 * needing to be reopened.
 *
 * HOW TO MAKE YOUR OWN "item that raises an Affinity" FROM THIS TEMPLATE:
 *   1. Copy this class, rename it (e.g. LupineFangItem).
 *   2. Change AFFINITY_TO_RAISE below to whichever AffinityType you want
 *      (or add a brand new constant to AffinityType.java first, if the
 *      race you want doesn't exist yet - that file explains how).
 *   3. Change AFFINITY_GAIN_AMOUNT to whatever amount makes sense.
 *   4. Register it in ModItems.java exactly like ABYSS_SHARD is
 *      registered, for example:
 *
 *      public static final RegistryObject<Item> VAMPIRIC_TOKEN = ITEMS.register("vampiric_token",
 *              () -> new ExampleAffinityItem(new Item.Properties().tab(ModItemGroup.ECLIPSE_GROUP).stacksTo(16)));
 *
 *   5. Like every other item, it still needs a texture
 *      (assets/eclipse/textures/item/vampiric_token.png) and an item
 *      model json (assets/eclipse/models/item/vampiric_token.json)
 *      before it shows up correctly in-game - that part is asset files,
 *      not code.
 *
 * That's the entire pattern for "an item that changes a stat": get the
 * capability, call one PlayerStats method, sync. The exact same approach
 * works for stats.addReputation(...) too - just swap the method call and
 * the enum type (ReputationFaction instead of AffinityType).
 */
public class ExampleAffinityItem extends Item {

    /** Which affinity this item raises. Change this to make a different-race item. */
    private static final AffinityType AFFINITY_TO_RAISE = AffinityType.VAMPIRIC;

    /** How many points (0-100 scale) this item grants per use. */
    private static final int AFFINITY_GAIN_AMOUNT = 5;

    public ExampleAffinityItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Affinity/Reputation changes are decided on the server only
        // (never trust the client), exactly like AbyssShardItem does for
        // Corruption.
        if (world.isClientSide) {
            return ActionResult.pass(stack);
        }
        if (!(player instanceof ServerPlayerEntity)) {
            return ActionResult.pass(stack);
        }
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        return serverPlayer.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).map(stats -> {
            // This one line is the item's actual gameplay effect.
            // addAffinity() already clamps the result to 0-100, so you
            // never need to check bounds yourself here.
            stats.addAffinity(AFFINITY_TO_RAISE, AFFINITY_GAIN_AMOUNT);

            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }

            // Push the new value to the client so the Status Screen's
            // Affinity bar updates immediately.
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketSyncStats(stats));
            return ActionResult.success(stack);
        }).orElse(ActionResult.pass(stack));
    }
}
