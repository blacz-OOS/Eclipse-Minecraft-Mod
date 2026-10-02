package com.soldierskull.eclipse.block;

import java.util.Set;

import com.soldierskull.eclipse.ritual.RitualCategory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

/**
 * Bloco de chalk usado pra desenhar o perímetro do círculo ritualístico
 * (Bloco A/#17, Bloco C/#17). Cada instância carrega o conjunto de
 * {@link RitualCategory} que ela desenha um círculo válido para -
 * {@code Ritual Chalk} cobre GENERAL/HUNTER, {@code Blood Chalk} cobre
 * BLOOD/VAMPIRE. Moon Altar e Abyss Altar não usam chalk (a própria
 * estrutura deles já é o círculo - ver Bloco A/D), então não têm bloco
 * de chalk correspondente.
 *
 * BUGFIX (colisão): esta classe nunca sobrescreveu getCollisionShape,
 * então o chalk usava a colisão PADRÃO de Block - um cubo cheio de
 * 16x16x16 - apesar do modelo visual ter só 0.1 de altura. Na prática
 * isso fazia o chalk se comportar como um bloco sólido de verdade: o
 * jogador tinha que pular em cima dele, podia ficar preso na borda,
 * etc. Não bastava mudar só a textura/render (isso já estava certo
 * desde o fix anterior) - o problema real estava na física, não na
 * aparência. Fix: `.noCollission()` nas Properties (ver ModBlocks) é o
 * mesmo mecanismo que o vanilla usa pra trip wire, vinhas sem suporte
 * etc. - zera a colisão por completo (`AbstractBlock#hasCollision`),
 * então o jogador atravessa o chalk e colide só com o bloco sólido
 * embaixo dele, exatamente como andar sobre uma marca desenhada no
 * chão. O contorno de seleção (pra mirar/quebrar o bloco) continua
 * funcionando normal - só a colisão física foi zerada.
 *
 * FEATURE (marca aleatória): a propriedade MARK (0-4) escolhe qual das
 * 5 texturas/variantes de desenho (sun/abyss_signal/cross/spiral/vine)
 * este bloco usa. Sorteada uma vez, na colocação (getStateForPlacement),
 * e depois fica fixa naquele bloco - dois Ritual Chalk colocados lado a
 * lado podem sortear marcas diferentes, mas cada um mantém a marca dele
 * até ser quebrado e recolocado.
 */
public class RitualChalkBlock extends Block {

    public static final IntegerProperty MARK = IntegerProperty.create("mark", 0, 4);

    private final Set<RitualCategory> categories;

    public RitualChalkBlock(Properties properties, Set<RitualCategory> categories) {
        super(properties);
        this.categories = categories;
        this.registerDefaultState(this.stateDefinition.any().setValue(MARK, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(MARK);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        // Sorteio individual por colocação - cada chalk decide sua propria
        // marca, independente de qualquer chalk vizinho.
        int roll = context.getLevel().getRandom().nextInt(5);
        return this.defaultBlockState().setValue(MARK, roll);
    }

    public boolean supports(RitualCategory category) {
        return this.categories.contains(category);
    }
}
