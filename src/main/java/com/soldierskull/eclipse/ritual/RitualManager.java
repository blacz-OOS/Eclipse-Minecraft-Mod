package com.soldierskull.eclipse.ritual;

import com.soldierskull.eclipse.effect.ModEffects;
import com.soldierskull.eclipse.ritual.altar.AltarTileEntity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;

/**
 * O motor de verdade - coordena qual ritual está sendo executado em
 * cada altar (Bloco C/D da especificação: "RitualManager coordena").
 * {@link AltarTileEntity} só guarda dados; toda a lógica de estado fica
 * aqui, num único lugar, pra não repetir a mesma lógica quando
 * Blood/Moon/Abyss Altar forem implementados (Etapa 9) - eles vão
 * chamar os mesmos métodos estáticos, só com {@code allowedCategories}/
 * {@code ritualPowerMax} diferentes.
 */
public final class RitualManager {

    private RitualManager() {
    }

    public static void tick(AltarTileEntity altar, ServerWorld world) {
        BlockPos pos = altar.getBlockPos();

        // Reconstrução do contexto depois de um load() (altar sobreviveu a um restart com ritual RUNNING).
        if (altar.getState() == RitualState.RUNNING && altar.getActiveContext() == null) {
            Ritual ritual = altar.getCandidateRitual();
            if (ritual != null && altar.hasPendingRestore()) {
                int progress = altar.consumePendingRestoreProgressTicks();
                RitualContext restored = new RitualContext(world, pos, altar, ritual, altar.getPendingRestoreInitiator());
                restored.setProgressTicks(progress);
                altar.setActiveContext(restored);
            } else {
                altar.setState(RitualState.FAILED);
                altar.setTransientTimer(20);
                return;
            }
        }

        switch (altar.getState()) {
            case RUNNING:
                tickRunning(altar, world, pos);
                break;
            case COMPLETED:
            case FAILED:
                tickTransient(altar);
                break;
            case INACTIVE:
            case READY:
            default:
                tickIdle(altar, world, pos);
                break;
        }
    }

    /** Chamado pelo AltarBlock quando o jogador clica no altar segurando um item. */
    public static void setPrimaryIngredient(AltarTileEntity altar, ItemStack stack) {
        altar.setPrimaryIngredient(stack.copy());
        altar.setEvaluateCooldown(0); // força reavaliação já na próxima tick
    }

    /**
     * Pega exatamente 1 unidade do item na mão do jogador e acumula no
     * ingrediente principal do altar - se já havia algo de tipo
     * diferente, devolve pro jogador antes de trocar. Evita o bug óbvio
     * de "clicar com uma stack de 5 sacrifica os 5 de uma vez" (o
     * consumo real na conclusão do ritual só tira o que a receita pede).
     */
    public static void offerPrimaryIngredient(AltarTileEntity altar, PlayerEntity player, Hand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) {
            return;
        }
        ItemStack current = altar.getPrimaryIngredient();
        boolean sameType = !current.isEmpty() && ItemStack.isSame(current, held) && ItemStack.tagMatches(current, held);

        if (!current.isEmpty() && !sameType) {
            if (!player.inventory.add(current)) {
                player.drop(current, false);
            }
            current = ItemStack.EMPTY;
        }

        ItemStack taken = held.split(1);
        ItemStack merged = current.isEmpty() ? taken : current.copy();
        if (!current.isEmpty()) {
            merged.grow(1);
        }
        altar.setPrimaryIngredient(merged);
        altar.setEvaluateCooldown(0);
    }

    /** Chamado pelo AltarBlock quando o jogador clica de mão vazia. Retorna se o ritual começou. */
    public static boolean tryStart(AltarTileEntity altar, ServerWorld world, ServerPlayerEntity player) {
        if (altar.getState() != RitualState.READY || altar.getCandidateRitual() == null) {
            return false;
        }
        Ritual ritual = altar.getCandidateRitual();
        if (!altar.isCircleValid(world, altar.getBlockPos(), ritual)
                || altar.getRitualPower() < ritual.getRitualPowerCost()
                || (!ritual.getOfferings().isEmpty() && !RitualOfferingScanner.allOfferingsPresent(
                        world, altar.getBlockPos(), ritual.getCircleSize().getRadius(), ritual.getOfferings()))) {
            altar.setEvaluateCooldown(0);
            return false;
        }
        RitualContext validation = new RitualContext(world, altar.getBlockPos(), altar, ritual, player.getUUID());
        RitualRequirement failingRequirement = ritual.findFailingRequirement(validation);
        if (failingRequirement != null) {
            altar.setLastMessage(new StringTextComponent(failingRequirement.describe()));
            return false;
        }
        altar.setActiveContext(validation);
        altar.setState(RitualState.RUNNING);
        return true;
    }

    private static void tickIdle(AltarTileEntity altar, ServerWorld world, BlockPos pos) {
        int cooldown = altar.getEvaluateCooldown();
        if (cooldown > 0) {
            altar.setEvaluateCooldown(cooldown - 1);
            return;
        }
        altar.setEvaluateCooldown(AltarTileEntity.getEvaluateIntervalTicks());
        evaluate(altar, world, pos);
    }

    private static void evaluate(AltarTileEntity altar, ServerWorld world, BlockPos pos) {
        Ritual candidate = findCandidateRitual(altar);
        altar.setCandidateRitual(candidate);

        if (candidate == null) {
            altar.setState(RitualState.INACTIVE);
            altar.setLastMessage(new StringTextComponent("Nenhum ritual reconhecido"));
            return;
        }

        if (!altar.isCircleValid(world, pos, candidate)) {
            altar.setState(RitualState.INACTIVE);
            altar.setLastMessage(new StringTextComponent("Circulo invalido (precisa "
                    + candidate.getCircleSize().getSize() + "x" + candidate.getCircleSize().getSize() + ")"));
            return;
        }

        if (altar.getRitualPower() < candidate.getRitualPowerCost()) {
            altar.setState(RitualState.INACTIVE);
            altar.setLastMessage(new StringTextComponent("Ritual Power insuficiente ("
                    + altar.getRitualPower() + "/" + candidate.getRitualPowerCost() + ")"));
            return;
        }

        // Contexto "de sondagem", sem iniciador definido - suficiente pros
        // requirements atuais (NightRequirement só olha o mundo). Quando
        // existir um requirement que dependa do jogador (ex.: RaceRequirement),
        // ele deve tratar initiator == null como "não avaliável ainda" e
        // retornar false, nunca lançar exceção.
        RitualContext probe = new RitualContext(world, pos, altar, candidate, null);
        RitualRequirement failing = findFailingWorldRequirement(candidate, probe);
        if (failing != null) {
            altar.setState(RitualState.INACTIVE);
            altar.setLastMessage(new StringTextComponent(failing.describe()));
            return;
        }

        if (!candidate.getOfferings().isEmpty() && !RitualOfferingScanner.allOfferingsPresent(
                world, pos, candidate.getCircleSize().getRadius(), candidate.getOfferings())) {
            altar.setState(RitualState.INACTIVE);
            altar.setLastMessage(new StringTextComponent("Oferendas faltando no circulo"));
            return;
        }

        altar.setState(RitualState.READY);
        altar.setLastMessage(new StringTextComponent("Pronto - clique para iniciar"));
    }

    /** Ignora condições pessoais até haver um jogador tentando iniciar o ritual. */
    private static RitualRequirement findFailingWorldRequirement(Ritual ritual, RitualContext context) {
        for (RitualRequirement requirement : ritual.getRequirements()) {
            if (!requirement.requiresInitiator() && !requirement.test(context)) {
                return requirement;
            }
        }
        return null;
    }

    /** Escolhe qual Ritual conhecido bate com o ingrediente principal atualmente no altar (Bloco C/#18). */
    private static Ritual findCandidateRitual(AltarTileEntity altar) {
        ItemStack held = altar.getPrimaryIngredient();
        for (Ritual ritual : RitualRegistry.getAll().values()) {
            if (!altar.getAllowedCategories().contains(ritual.getCategory())) {
                continue;
            }
            RitualIngredient required = ritual.getPrimaryIngredient();
            if (required == null) {
                if (held.isEmpty()) {
                    return ritual; // ex.: channel_power - só candidato se o altar estiver vazio
                }
            } else if (required.matches(held)) {
                return ritual;
            }
        }
        return null;
    }

    private static void tickRunning(AltarTileEntity altar, ServerWorld world, BlockPos pos) {
        RitualContext context = altar.getActiveContext();
        if (context == null) {
            altar.setState(RitualState.INACTIVE);
            return;
        }
        Ritual ritual = context.getRitual();

        if (!altar.isCircleValid(world, pos, ritual)) {
            fail(altar, world, new StringTextComponent("Circulo foi quebrado"));
            return;
        }

        RitualRequirement failingReq = ritual.findFailingRequirement(context);
        if (failingReq != null) {
            fail(altar, world, new StringTextComponent(failingReq.describe()));
            return;
        }

        if (ritual.requiresInitiatorNearby()) {
            ServerPlayerEntity player = resolvePlayer(world, context);
            if (player == null || player.level != world || player.blockPosition().distSqr(pos) > 36) { // 6 blocos
                fail(altar, world, new StringTextComponent("Iniciador saiu de perto do altar"));
                return;
            }
        }

        context.tickProgress();
        if (context.isDurationComplete()) {
            complete(altar, world, pos);
        }
    }

    private static void complete(AltarTileEntity altar, ServerWorld world, BlockPos pos) {
        RitualContext context = altar.getActiveContext();
        if (context == null) {
            return;
        }
        Ritual ritual = context.getRitual();

        if (!ritual.getOfferings().isEmpty() && !RitualOfferingScanner.allOfferingsPresent(
                world, pos, ritual.getCircleSize().getRadius(), ritual.getOfferings())) {
            fail(altar, world, new StringTextComponent("Oferenda sumiu antes da conclusao"));
            return;
        }
        if (!ritual.getOfferings().isEmpty()) {
            RitualOfferingScanner.consumeOfferings(world, pos, ritual.getCircleSize().getRadius(), ritual.getOfferings());
        }
        if (ritual.getPrimaryIngredient() != null) {
            altar.setPrimaryIngredient(ItemStack.EMPTY);
        }
        altar.removeRitualPower(ritual.getRitualPowerCost());

        for (RitualEffect effect : ritual.getEffects()) {
            effect.apply(context);
        }

        altar.setActiveContext(null);
        altar.setState(RitualState.COMPLETED);
        altar.setTransientTimer(40); // ~2s
        altar.setLastMessage(new StringTextComponent(ritual.getDisplayName().getString() + " concluido"));
    }

    private static void fail(AltarTileEntity altar, ServerWorld world, ITextComponent reason) {
        RitualContext context = altar.getActiveContext();

        // Backlash só se já havia progresso investido - decisão confirmada
        // (Bloco C/#22-23): falha antes de RUNNING avançar nunca pune.
        if (context != null && context.getProgressFraction() > 0.5F) {
            applyBacklash(context);
        }

        // Ingrediente principal nunca é consumido em falha - devolve pro
        // iniciador se possível, em vez de simplesmente sumir.
        if (context != null && !altar.getPrimaryIngredient().isEmpty() && context.getInitiator() != null) {
            ServerPlayerEntity player = resolvePlayer(world, context);
            if (player != null) {
                ItemStack returned = altar.getPrimaryIngredient().copy();
                if (!player.inventory.add(returned)) {
                    player.drop(returned, false);
                }
                altar.setPrimaryIngredient(ItemStack.EMPTY);
            }
        }

        altar.setActiveContext(null);
        altar.setState(RitualState.FAILED);
        altar.setTransientTimer(40);
        altar.setLastMessage(reason);
    }

    /**
     * Backlash calibrado por categoria (Bloco C/#23, decisão
     * confirmada). Sempre estrutural - só chega aqui se
     * {@code getProgressFraction() > 0.5F} (ver {@link #fail}), nunca
     * RNG puro.
     */
    private static void applyBacklash(RitualContext context) {
        ServerPlayerEntity player = resolvePlayer(context.getWorld(), context);
        if (player == null) {
            return;
        }
        ServerWorld world = context.getWorld();
        BlockPos pos = context.getAltarPos();

        switch (context.getRitual().getCategory()) {
            case GENERAL:
            case HUNTER:
                player.hurt(DamageSource.MAGIC, 2.0F);
                break;
            case LUNAR:
            case WEREWOLF:
                player.hurt(DamageSource.MAGIC, 3.0F);
                player.addEffect(new EffectInstance(Effects.CONFUSION, 100, 0)); // fúria/instinto descontrolado, ~5s
                break;
            case BLOOD:
            case VAMPIRE:
                player.hurt(DamageSource.MAGIC, 4.0F);
                player.addEffect(new EffectInstance(ModEffects.BLEEDING.get(), 100, 0));
                break;
            case ABYSSAL:
            case CULTIST:
                player.hurt(DamageSource.MAGIC, 5.0F);
                player.addEffect(new EffectInstance(Effects.WEAKNESS, 200, 1));
                spawnCorruptionMob(world, pos);
                break;
            case ECLIPSE:
            case ENDGAME:
                player.hurt(DamageSource.MAGIC, 8.0F);
                world.explode(null, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                        2.5F, Explosion.Mode.NONE); // sem quebrar blocos - so o dano/efeito visual
                break;
            default:
                break;
        }
    }

    /** ABYSSAL backlash - "spawn hostil" (Bloco C/#23). Zumbi vanilla por simplicidade; trocar por um mob do mod é so mudar esta linha. */
    private static void spawnCorruptionMob(ServerWorld world, BlockPos pos) {
        ZombieEntity zombie = EntityType.ZOMBIE.create(world);
        if (zombie != null) {
            zombie.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 0.0F, 0.0F);
            zombie.finalizeSpawn(world, world.getCurrentDifficultyAt(pos), SpawnReason.MOB_SUMMONED, null, null);
            world.addFreshEntity(zombie);
        }
    }

    private static void tickTransient(AltarTileEntity altar) {
        int timer = altar.getTransientTimer() - 1;
        altar.setTransientTimer(timer);
        if (timer <= 0) {
            altar.setState(RitualState.INACTIVE);
            altar.setCandidateRitual(null);
            altar.setLastMessage(null);
        }
    }

    private static ServerPlayerEntity resolvePlayer(ServerWorld world, RitualContext context) {
        if (context.getInitiator() == null || world.getServer() == null) {
            return null;
        }
        return world.getServer().getPlayerList().getPlayer(context.getInitiator());
    }
}
