package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketSyncStats;
import com.soldierskull.eclipse.race.RaceType;
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
 * TEMPLATE / EXEMPLO - não registrado em ModItems por padrão, igual
 * ExampleAffinityItem.
 *
 * Registra "rituais" como uma das fontes de XP racial confirmadas na seção
 * 3 (junto com matar mobs específicos, quests e tempo em forma
 * transformada - essas outras fontes ainda não têm implementação, só
 * ficaram documentadas aqui como próximos passos). Ao usar o item, o
 * jogador ganha XP racial NA SUA RAÇA ATUAL (não numa raça fixa como o
 * ExampleAffinityItem faz com Afinidade) - um "ritual básico" só faz
 * sentido para quem já é sobrenatural; jogadores HUMANO simplesmente não
 * ganham nada (getRace() == HUMANO tem tabela de rank nula, então não
 * cobra XP racial deles).
 *
 * Isto é só uma PROVA DE CONCEITO da fonte "rituais" - não é um "ritual"
 * no sentido de estrutura/altar/multiblock que o documento de design
 * menciona (Blood Altar, Abyssal Shrine, etc. - isso é Fase 13,
 * estruturas). Quando você definir como um ritual de verdade deve
 * funcionar (multiblock? item + bloco? tempo de canalização?), este
 * template é só o ponto de partida para "o que ele concede", não para
 * "como ele é executado".
 *
 * HOW TO ADAPTAR:
 *   1. Copie esta classe, renomeie.
 *   2. Ajuste RACIAL_XP_GAIN para o valor certo.
 *   3. Registre em ModItems.java, igual ABYSS_SHARD.
 *   4. Precisa de textura + item model json antes de aparecer no jogo.
 */
public class BasicRitualItem extends Item {

    /** Quanto de XP racial este ritual concede por uso. Valor provisório. */
    private static final int RACIAL_XP_GAIN = 10;

    public BasicRitualItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Decisão sempre no servidor, nunca confiando no cliente - mesmo
        // padrão de AbyssShardItem/ExampleAffinityItem.
        if (world.isClientSide) {
            return ActionResult.pass(stack);
        }
        if (!(player instanceof ServerPlayerEntity)) {
            return ActionResult.pass(stack);
        }
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        return serverPlayer.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).map(stats -> {
            if (stats.getRace() == RaceType.HUMAN) {
                // Humano não tem progressão racial - ritual não faz nada.
                return ActionResult.pass(stack);
            }

            stats.addRacialXp(RACIAL_XP_GAIN);

            if (!player.abilities.instabuild) {
                stack.shrink(1);
            }

            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketSyncStats(stats));
            return ActionResult.success(stack);
        }).orElse(ActionResult.pass(stack));
    }
}
