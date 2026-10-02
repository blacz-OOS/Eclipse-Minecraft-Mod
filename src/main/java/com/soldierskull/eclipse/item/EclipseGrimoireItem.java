package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.quests.Quest;
import com.soldierskull.eclipse.quests.QuestManager;
import com.soldierskull.eclipse.quests.QuestRegistry;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketSyncStats;
import com.soldierskull.eclipse.ritual.Ritual;
import com.soldierskull.eclipse.ritual.RitualCategory;
import com.soldierskull.eclipse.ritual.RitualContext;
import com.soldierskull.eclipse.ritual.RitualRegistry;
import com.soldierskull.eclipse.stats.PlayerStats;
import com.soldierskull.eclipse.stats.PlayerStatsProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Grimório único do mod - substitui os 5 itens de livro que existiam
 * antes (Eclipse Grimoire "de lore" + Vampire Tome + Werewolf Tome +
 * Hunter Journal + Forbidden Tome). Os 4 "tomes" faziam exatamente a
 * mesma coisa (entregar/aceitar quest, listar rituais conhecidos) só
 * que cada um hardcoded pra UMA raça/facção específica - agora é UM
 * item só, que lê a raça/facção ATUAL do próprio jogador (via
 * PlayerStats) em vez de depender de qual item físico ele carrega.
 *
 * Clique normal: assistente de quest/ritual (comportamento do antigo
 *   QuestBookItem, agora dinâmico) - entrega quest pronta, senão aceita
 *   quest nova, senão lista rituais conhecidos da categoria do jogador.
 * Shift + clique: sempre abre o livro de lore (páginas gerais sobre
 *   todos os sistemas do mod), não importa o estado de quest.
 */
public class EclipseGrimoireItem extends Item {

    public EclipseGrimoireItem(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0F, 0.8F);

        if (player.isShiftKeyDown()) {
            if (world.isClientSide) {
                openLoreScreen();
            }
            return ActionResult.sidedSuccess(stack, world.isClientSide());
        }

        if (!world.isClientSide && player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.getCapability(PlayerStatsProvider.PLAYER_STATS_CAP).ifPresent(stats -> {
                if (tryDeliverCompletableQuest(serverPlayer, stats)) {
                    return;
                }
                if (tryAcceptNewQuest(serverPlayer, stats)) {
                    return;
                }
                listKnownRituals(serverPlayer, stats);
            });
        }

        return ActionResult.sidedSuccess(stack, world.isClientSide());
    }

    // ---- Assistente de quest/ritual (comportamento do antigo QuestBookItem) ----
    // A diferença chave: "matchesCategory" agora lê a raça/facção ATUAL do
    // jogador (stats), em vez de um filtro fixo guardado no item - o
    // mesmo grimório serve qualquer jogador, de qualquer raça ou facção.

    private boolean matchesCategory(Quest quest, PlayerStats stats) {
        if (quest.getRaceRequirement() != null) {
            return quest.getRaceRequirement() == stats.getRace();
        }
        if (quest.getFactionRequirement() != null) {
            return quest.getFactionRequirement() == stats.getFaction();
        }
        return false;
    }

    private boolean tryDeliverCompletableQuest(ServerPlayerEntity player, PlayerStats stats) {
        for (String questId : new ArrayList<>(stats.getActiveQuests())) {
            Quest quest = QuestRegistry.get(questId);
            if (quest == null || !matchesCategory(quest, stats)) {
                continue;
            }
            if (QuestManager.canComplete(player, stats, questId)) {
                QuestManager.complete(player, stats, questId);
                player.displayClientMessage(
                        new TranslationTextComponent("message.eclipse.quest.delivered", quest.getDisplayName()), false);
                return true;
            }
        }
        return false;
    }

    private boolean tryAcceptNewQuest(ServerPlayerEntity player, PlayerStats stats) {
        for (Quest quest : QuestRegistry.getAll()) {
            if (!matchesCategory(quest, stats)) {
                continue;
            }
            QuestManager.AcceptResult result = QuestManager.accept(stats, quest.getId());
            if (result == QuestManager.AcceptResult.OK) {
                player.displayClientMessage(
                        new TranslationTextComponent("message.eclipse.quest.accepted", quest.getDisplayName(), quest.getDescription()), false);
                PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new PacketSyncStats(stats));
                return true;
            }
        }
        return false;
    }

    private Set<RitualCategory> ritualCategories(PlayerStats stats) {
        if (stats.getRace() == RaceType.VAMPIRE) {
            return EnumSet.of(RitualCategory.VAMPIRE, RitualCategory.BLOOD);
        }
        if (stats.getRace() == RaceType.WEREWOLF) {
            return EnumSet.of(RitualCategory.WEREWOLF, RitualCategory.LUNAR);
        }
        if (stats.getFaction() == FactionType.HUNTERS) {
            return EnumSet.of(RitualCategory.HUNTER, RitualCategory.GENERAL);
        }
        if (stats.getFaction() == FactionType.CULTISTS) {
            return EnumSet.of(RitualCategory.CULTIST, RitualCategory.ABYSSAL, RitualCategory.ECLIPSE, RitualCategory.ENDGAME);
        }
        return EnumSet.noneOf(RitualCategory.class);
    }

    private void listKnownRituals(ServerPlayerEntity player, PlayerStats stats) {
        Set<RitualCategory> categories = ritualCategories(stats);
        if (categories.isEmpty()) {
            player.displayClientMessage(new TranslationTextComponent("message.eclipse.quest.book.empty"), false);
            return;
        }

        StringTextComponent text = new StringTextComponent("");
        text.append(new TranslationTextComponent("message.eclipse.quest.book.known_rituals"));
        boolean any = false;
        for (Ritual ritual : RitualRegistry.getAll().values()) {
            if (!categories.contains(ritual.getCategory())) {
                continue;
            }
            any = true;
            String id = ritual.getId().toString();

            if (!stats.knowsRitual(id) && ritual.getCategory() != RitualCategory.GENERAL) {
                RitualContext probe = new RitualContext((ServerWorld) player.level, player.blockPosition(), null, ritual, player.getUUID());
                if (ritual.findFailingRequirement(probe) == null) {
                    stats.learnRitual(id);
                }
            }

            text.append("\n - ");
            if (stats.knowsRitual(id)) {
                text.append(ritual.getDisplayName());
            } else {
                text.append("???");
            }
        }
        if (!any) {
            text.append(" ").append(new TranslationTextComponent("message.eclipse.quest.book.none_registered"));
        }
        player.displayClientMessage(text, false);
    }

    // ---- Livro de lore (comportamento do antigo EclipseGrimoireItem) ----

    @OnlyIn(Dist.CLIENT)
    private void openLoreScreen() {
        List<ITextComponent> pages = GrimoirePages.getPages();
        CompoundNBT bookTags = createBookTag(pages);

        ItemStack bookStack = new ItemStack(net.minecraft.item.Items.WRITTEN_BOOK);
        bookStack.setTag(bookTags);

        ReadBookScreen.WrittenBookInfo bookInfo = new ReadBookScreen.WrittenBookInfo(bookStack);
        Minecraft.getInstance().setScreen(new ReadBookScreen(bookInfo));
    }

    private CompoundNBT createBookTag(List<ITextComponent> pages) {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("title", "Grimoire of the Eclipse");
        tag.putString("author", "Order of Shadows");

        ListNBT pageList = new ListNBT();
        for (ITextComponent page : pages) {
            pageList.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(page)));
        }

        tag.put("pages", pageList);
        return tag;
    }
}
