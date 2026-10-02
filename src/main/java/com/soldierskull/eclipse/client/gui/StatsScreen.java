package com.soldierskull.eclipse.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.soldierskull.eclipse.client.ClientStatsCache;
import com.soldierskull.eclipse.network.PacketAddStat;
import com.soldierskull.eclipse.network.PacketConvertEnergy;
import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketRequestStats;
import com.soldierskull.eclipse.stats.AffinityType;
import com.soldierskull.eclipse.stats.AttributeType;
import com.soldierskull.eclipse.stats.ReputationFaction;
import com.soldierskull.eclipse.stats.StatBalance;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class StatsScreen extends Screen {

    // ---- Geometria do painel ---------------------------------------------------
    private static final int PANEL_WIDTH = 305;
    private static final int TITLE_Y = 8;
    private static final int FIRST_ROW_Y = 24;
    private static final int ROW_HEIGHT = 13;

    private static final int LEFT_COLUMN_X = 10;   // relativo à borda esquerda
    private static final int DIVIDER_X = 150;      // relativo à borda esquerda
    private static final int RIGHT_COLUMN_X = 160; // relativo à borda esquerda
    private static final int PLUS_BUTTON_X = 128;  // relativo à borda esquerda

    private static final int BUTTON_SIZE = 11;
    private static final int BAR_WIDTH = 45;
    private static final int BAR_HEIGHT = 7;

    // ---- Índices de linhas da coluna esquerda ----------------------------------
    private static final int LEFT_ROW_LEVEL = 0;
    private static final int LEFT_ROW_RACE = 1;
    private static final int LEFT_ROW_FACTION = 2;
    private static final int LEFT_ROW_XP = 3;
    private static final int LEFT_ROW_HP = 4;
    private static final int LEFT_ROW_STRENGTH = 5;
    private static final int LEFT_ROW_DEFENSE = 6;
    private static final int LEFT_ROW_SPEED = 7;
    private static final int LEFT_ROW_CONSTITUTION = 8;
    private static final int LEFT_ROW_ENERGY = 9;
    private static final int LEFT_ROW_POINTS = 10;
    private static final int LEFT_TOTAL_ROWS = 11;

    // ---- Índices de linhas da coluna direita -----------------------------------
    private static final int RIGHT_ROW_COMMON_ENERGY = 0;
    private static final int RIGHT_ROW_CORRUPTION = 1;
    private static final int RIGHT_ROW_CONVERT = 2;
    private static final int RIGHT_ROW_AFFINITY_HEADER = 3;
    private static final int RIGHT_ROW_AFFINITY_START = 4;
    private static final int RIGHT_ROW_REPUTATION_HEADER = RIGHT_ROW_AFFINITY_START + AffinityType.values().length;
    private static final int RIGHT_ROW_REPUTATION_START = RIGHT_ROW_REPUTATION_HEADER + 1;
    private static final int RIGHT_TOTAL_ROWS = RIGHT_ROW_REPUTATION_START + ReputationFaction.values().length;

    private static final int PANEL_HEIGHT =
            FIRST_ROW_Y + Math.max(LEFT_TOTAL_ROWS, RIGHT_TOTAL_ROWS) * ROW_HEIGHT + 16;

    // ---- Cores ------------------------------------------------------------------
    private static final int COLOR_PANEL_BG = 0xC0101015;
    private static final int COLOR_DIVIDER = 0xFF444444;
    private static final int COLOR_BAR_BG = 0xFF2A2A2A;
    private static final int COLOR_BAR_BORDER = 0xFF666666;
    private static final int COLOR_HP_BAR = 0xFFE05050;
    private static final int COLOR_COMMON_ENERGY_BAR = 0xFF55AAFF;
    private static final int COLOR_CORRUPTION_BAR = 0xFFAA55FF;
    private static final int COLOR_AFFINITY_BAR = 0xFFCC66FF;
    private static final int COLOR_REPUTATION_BAR = 0xFF66CC66;

    private int leftPos;
    private int topPos;

    private Button strengthButton;
    private Button defenseButton;
    private Button speedButton;
    private Button constitutionButton;
    private Button energyButton;
    private Button convertButton;

    public StatsScreen() {
        super(new TranslationTextComponent("gui.eclipse.statstitle"));
    }

    @Override
    protected void init() {
        super.init();

        PacketHandler.INSTANCE.sendToServer(new PacketRequestStats());

        this.leftPos = (this.width - PANEL_WIDTH) / 2;
        this.topPos = (this.height - PANEL_HEIGHT) / 2;

        this.strengthButton = addStatButton(LEFT_ROW_STRENGTH, AttributeType.STRENGTH);
        this.defenseButton = addStatButton(LEFT_ROW_DEFENSE, AttributeType.DEFENSE);
        this.speedButton = addStatButton(LEFT_ROW_SPEED, AttributeType.SPEED);
        this.constitutionButton = addStatButton(LEFT_ROW_CONSTITUTION, AttributeType.CONSTITUTION);
        this.energyButton = addStatButton(LEFT_ROW_ENERGY, AttributeType.ENERGY);

        int convertY = topPos + FIRST_ROW_Y + RIGHT_ROW_CONVERT * ROW_HEIGHT - 2;
        int convertX = leftPos + RIGHT_COLUMN_X;

        int skillTreeButtonSize = 14;
        this.addButton(new Button(
                leftPos + PANEL_WIDTH - skillTreeButtonSize - 2,
                topPos + PANEL_HEIGHT - skillTreeButtonSize - 2,
                skillTreeButtonSize, skillTreeButtonSize,
                new StringTextComponent(">"),
                button -> Minecraft.getInstance().setScreen(new SkillTreeScreen())));

        this.convertButton = this.addButton(new Button(convertX, convertY, 35, BUTTON_SIZE,
                new TranslationTextComponent("gui.eclipse.button.convert"),
                button -> PacketHandler.INSTANCE.sendToServer(new PacketConvertEnergy())));
    }

    private Button addStatButton(int row, AttributeType type) {
        int y = topPos + FIRST_ROW_Y + row * ROW_HEIGHT - 1;
        return this.addButton(new Button(leftPos + PLUS_BUTTON_X, y, BUTTON_SIZE, BUTTON_SIZE,
                new StringTextComponent("+"),
                button -> PacketHandler.INSTANCE.sendToServer(new PacketAddStat(type))));
    }

    @Override
    public void tick() {
        super.tick();
        boolean hasPoints = ClientStatsCache.points > 0;
        this.strengthButton.active = hasPoints;
        this.defenseButton.active = hasPoints;
        this.speedButton.active = hasPoints;
        this.constitutionButton.active = hasPoints;
        this.energyButton.active = hasPoints;
        this.convertButton.active = ClientStatsCache.corruption >= StatBalance.CORRUPTION_TO_COMMON_COST;
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        fill(matrixStack, leftPos, topPos, leftPos + PANEL_WIDTH, topPos + PANEL_HEIGHT, COLOR_PANEL_BG);

        ITextComponent titleComponent = this.title;
        int titleWidth = this.font.width(titleComponent);
        this.font.draw(matrixStack, titleComponent, leftPos + (PANEL_WIDTH - titleWidth) / 2F, topPos + TITLE_Y, 0xFFFFFF);

        fill(matrixStack, leftPos + DIVIDER_X, topPos + FIRST_ROW_Y - 8,
                leftPos + DIVIDER_X + 1, topPos + PANEL_HEIGHT - 8, COLOR_DIVIDER);

        renderLeftColumn(matrixStack);
        renderRightColumn(matrixStack);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderLeftColumn(MatrixStack matrixStack) {
        // Nível
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_LEVEL,
                new TranslationTextComponent("gui.eclipse.stat.level", ClientStatsCache.level), 0xFFFFFF);

        // Raça e Facção
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_RACE,
                new TranslationTextComponent("gui.eclipse.stat.race", ClientStatsCache.getRace().getDisplayName()), 0xFFFFFF);
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_FACTION,
                new TranslationTextComponent("gui.eclipse.stat.faction", ClientStatsCache.getFaction().getDisplayName()), 0xFFFFFF);

        // XP
        int xpPercent = ClientStatsCache.maxXp > 0 ? (ClientStatsCache.currentXp * 100 / ClientStatsCache.maxXp) : 0;
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_XP,
                new TranslationTextComponent("gui.eclipse.stat.xp", ClientStatsCache.currentXp, ClientStatsCache.maxXp, xpPercent), 0x55FF55);

        // HP
        PlayerEntity player = this.minecraft.player;
        int currentHp = player != null ? Math.round(player.getHealth()) : 0;
        int maxHp = player != null ? Math.round(player.getMaxHealth()) : 0;
        drawBarRow(matrixStack, LEFT_COLUMN_X, LEFT_ROW_HP,
                new TranslationTextComponent("gui.eclipse.stat.hp"), currentHp, maxHp, COLOR_HP_BAR, false);

        // Atributos Principais
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_STRENGTH,
                new TranslationTextComponent("gui.eclipse.stat.strength", ClientStatsCache.getAttribute(AttributeType.STRENGTH)), 0xFFFFFF);
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_DEFENSE,
                new TranslationTextComponent("gui.eclipse.stat.defense", ClientStatsCache.getAttribute(AttributeType.DEFENSE)), 0xFFFFFF);
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_SPEED,
                new TranslationTextComponent("gui.eclipse.stat.speed", ClientStatsCache.getAttribute(AttributeType.SPEED)), 0xFFFFFF);
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_CONSTITUTION,
                new TranslationTextComponent("gui.eclipse.stat.constitution", ClientStatsCache.getAttribute(AttributeType.CONSTITUTION)), 0xFFFFFF);
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_ENERGY,
                new TranslationTextComponent("gui.eclipse.stat.energy", ClientStatsCache.getAttribute(AttributeType.ENERGY)), 0xFFFFFF);

        // Pontos Disponíveis
        drawText(matrixStack, LEFT_COLUMN_X, LEFT_ROW_POINTS,
                new TranslationTextComponent("gui.eclipse.stat.points", ClientStatsCache.points), 0xFFFF00);
    }

    private void renderRightColumn(MatrixStack matrixStack) {
        // Energias e Corrupção
        drawBarRow(matrixStack, RIGHT_COLUMN_X, RIGHT_ROW_COMMON_ENERGY,
                new TranslationTextComponent("gui.eclipse.stat.common_energy"),
                ClientStatsCache.commonEnergy, ClientStatsCache.commonEnergyMax, COLOR_COMMON_ENERGY_BAR, false);

        drawBarRow(matrixStack, RIGHT_COLUMN_X, RIGHT_ROW_CORRUPTION,
                new TranslationTextComponent("gui.eclipse.stat.corruption"),
                ClientStatsCache.corruption, ClientStatsCache.corruptionMax, COLOR_CORRUPTION_BAR, false);

        // Cabeçalho de Afinidades
        drawText(matrixStack, RIGHT_COLUMN_X, RIGHT_ROW_AFFINITY_HEADER,
                new TranslationTextComponent("gui.eclipse.header.affinities"), 0xAAAAAA);

        AffinityType[] affinityTypes = AffinityType.values();
        for (int i = 0; i < affinityTypes.length; i++) {
            drawBarRow(matrixStack, RIGHT_COLUMN_X, RIGHT_ROW_AFFINITY_START + i, affinityTypes[i].getDisplayName(),
                    ClientStatsCache.getAffinity(affinityTypes[i]), 100, COLOR_AFFINITY_BAR, true);
        }

        // Cabeçalho de Reputações
        drawText(matrixStack, RIGHT_COLUMN_X, RIGHT_ROW_REPUTATION_HEADER,
                new TranslationTextComponent("gui.eclipse.header.reputations"), 0xAAAAAA);

        ReputationFaction[] factions = ReputationFaction.values();
        for (int i = 0; i < factions.length; i++) {
            drawBarRow(matrixStack, RIGHT_COLUMN_X, RIGHT_ROW_REPUTATION_START + i, factions[i].getDisplayName(),
                    ClientStatsCache.getReputation(factions[i]), 100, COLOR_REPUTATION_BAR, true);
        }
    }

    private void drawBarRow(MatrixStack matrixStack, int colX, int row, ITextComponent label, int current, int max,
                            int barColor, boolean asPercent) {
        int x = leftPos + colX;
        int y = topPos + FIRST_ROW_Y + row * ROW_HEIGHT;

        this.font.draw(matrixStack, label, x, y + 1, 0xFFFFFF);

        int barX = x + this.font.width(label) + 4;
        drawBar(matrixStack, barX, y, BAR_WIDTH, BAR_HEIGHT, current, max, barColor);

        String valueText = asPercent ? (current + "%") : (current + "/" + max);
        this.font.draw(matrixStack, valueText, barX + BAR_WIDTH + 4, y + 1, 0xCCCCCC);
    }

    private void drawBar(MatrixStack matrixStack, int x, int y, int width, int height, int current, int max, int fillColor) {
        fill(matrixStack, x, y, x + width, y + height, COLOR_BAR_BG);

        int filledWidth = max > 0 ? Math.round(width * Math.min(1F, current / (float) max)) : 0;
        if (filledWidth > 0) {
            fill(matrixStack, x, y, x + filledWidth, y + height, fillColor);
        }

        fill(matrixStack, x, y, x + width, y + 1, COLOR_BAR_BORDER);                   // topo
        fill(matrixStack, x, y + height - 1, x + width, y + height, COLOR_BAR_BORDER); // base
        fill(matrixStack, x, y, x + 1, y + height, COLOR_BAR_BORDER);                  // esquerda
        fill(matrixStack, x + width - 1, y, x + width, y + height, COLOR_BAR_BORDER);  // direita
    }

    private void drawText(MatrixStack matrixStack, int colX, int row, ITextComponent text, int color) {
        int x = leftPos + colX;
        int y = topPos + FIRST_ROW_Y + row * ROW_HEIGHT;
        this.font.draw(matrixStack, text, x, y + 1, color);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}