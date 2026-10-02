package com.soldierskull.eclipse.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.soldierskull.eclipse.client.ClientStatsCache;
import com.soldierskull.eclipse.faction.FactionType;
import com.soldierskull.eclipse.network.PacketHandler;
import com.soldierskull.eclipse.network.PacketRequestStats;
import com.soldierskull.eclipse.network.PacketUnlockSkill;
import com.soldierskull.eclipse.race.RaceType;
import com.soldierskull.eclipse.skills.Skill;
import com.soldierskull.eclipse.skills.SkillOwnerType;
import com.soldierskull.eclipse.skills.SkillTree;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.client.resources.I18n;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
/**
 * Tela da árvore de habilidades - raça e facção, uma aba de cada vez.
 *
 * Texturas:
 * assets/eclipse/textures/gui/skills/
 *     vampire_skills/
 *     werewolf_skills/
 *     hunter_skills/
 *     cultist_skills/
 *
 * O nome do arquivo vem de Skill#getTextureName().
 * Por padrão, getTextureName() retorna o próprio id da skill.
 */
public class SkillTreeScreen extends Screen {

    private enum Tab {
        RACE,
        FACTION
    }

    // =========================
    // LAYOUT
    // =========================

    private static final int NODE_SIZE = 20;
    private static final int HORIZONTAL_GAP = 50;
    private static final int VERTICAL_GAP = 35;
    private static final int PANEL_TOP_MARGIN = 50;
    private static final int TREE_BOTTOM_MARGIN = 20;
    private static final int SCROLL_SPEED = 20;

    private static final int TAB_BUTTON_WIDTH = 90;
    private static final int TAB_BUTTON_HEIGHT = 20;

    // =========================
    // CORES
    // =========================

    private static final int COLOR_LINE = 0xFF666666;

    private static final int COLOR_UNLOCKED = 0xFF3CB043;
    private static final int COLOR_AVAILABLE = 0xFF3B8FD6;
    private static final int COLOR_LOCKED = 0xFF555555;
    private static final int COLOR_NODE_BORDER = 0xFF000000;

    /**
     * Overlay da situação da skill.
     *
     * 0x00 = transparente
     * Quanto maior o alpha, mais a imagem será tingida/escurecida.
     */
    private static final int OVERLAY_UNLOCKED = 0x503CB043;
    private static final int OVERLAY_AVAILABLE = 0x303B8FD6;
    private static final int OVERLAY_LOCKED = 0xA0000000;

    // =========================
    // ESTADO
    // =========================

    private Tab currentTab = Tab.RACE;

    private int scrollOffset = 0;

    /**
     * Posição original de cada nó.
     * [0] = X
     * [1] = Y
     */
    private final Map<String, int[]> nodePositions = new HashMap<>();

    private Button raceTabButton;
    private Button factionTabButton;

    public SkillTreeScreen() {
        super(new StringTextComponent(I18n.get("gui.eclipse.skill_tree.title")));
    }

    // =========================================================
    // INIT
    // =========================================================

    @Override
    protected void init() {
        super.init();

        scrollOffset = 0;

        PacketHandler.INSTANCE.sendToServer(new PacketRequestStats());

        int centerX = this.width / 2;
        int tabY = 20;

        this.raceTabButton = this.addButton(new Button(
                centerX - TAB_BUTTON_WIDTH - 2,
                tabY,
                TAB_BUTTON_WIDTH,
                TAB_BUTTON_HEIGHT,
                new StringTextComponent(I18n.get("gui.eclipse.skill_tree.race")),
                button -> switchTab(Tab.RACE)
        ));

        this.factionTabButton = this.addButton(new Button(
                centerX + 2,
                tabY,
                TAB_BUTTON_WIDTH,
                TAB_BUTTON_HEIGHT,
                new StringTextComponent(I18n.get("gui.eclipse.skill_tree.faction")),
                button -> switchTab(Tab.FACTION)
        ));

        recalculateLayout();
        clampScroll();
    }

    // =========================================================
    // ABA
    // =========================================================

    private void switchTab(Tab tab) {
        this.currentTab = tab;
        this.scrollOffset = 0;

        recalculateLayout();
        clampScroll();
    }

    // =========================================================
    // LAYOUT DA ÁRVORE
    // =========================================================

    private void recalculateLayout() {
        nodePositions.clear();

        Map<Integer, List<Skill>> porNivel = new TreeMap<>();

        for (Skill skill : currentTreeSkills()) {
            porNivel
                    .computeIfAbsent(skill.getRequiredLevel(), k -> new ArrayList<>())
                    .add(skill);
        }

        int centerX = this.width / 2;
        int currentY = PANEL_TOP_MARGIN;

        for (Map.Entry<Integer, List<Skill>> entry : porNivel.entrySet()) {

            List<Skill> skillsDoNivel = entry.getValue();

            int totalWidth = skillsDoNivel.size() * NODE_SIZE
                    + Math.max(0, skillsDoNivel.size() - 1) * HORIZONTAL_GAP;

            int startX = centerX - totalWidth / 2;

            for (int i = 0; i < skillsDoNivel.size(); i++) {

                Skill skill = skillsDoNivel.get(i);

                int x = startX + i * (NODE_SIZE + HORIZONTAL_GAP);
                int y = currentY;

                nodePositions.put(
                        skill.getId(),
                        new int[]{x, y}
                );
            }

            currentY += NODE_SIZE + VERTICAL_GAP;
        }
    }

    // =========================================================
    // SKILLS ATUAIS
    // =========================================================

    private Iterable<Skill> currentTreeSkills() {

        if (currentTab == Tab.RACE) {

            RaceType race = ClientStatsCache.getRace();

            if (race == RaceType.HUMAN) {
                return java.util.Collections.emptyList();
            }

            return SkillTree.getAllForRace(race);

        } else {

            FactionType faction = ClientStatsCache.getFaction();

            if (faction == FactionType.NONE) {
                return java.util.Collections.emptyList();
            }

            return SkillTree.getAllForFaction(faction);
        }
    }

    // =========================================================
    // ALTURA TOTAL DA ÁRVORE
    // =========================================================

    private int getTreeHeight() {

        int maxY = PANEL_TOP_MARGIN;

        for (int[] pos : nodePositions.values()) {

            if (pos == null) {
                continue;
            }

            maxY = Math.max(
                    maxY,
                    pos[1] + NODE_SIZE
            );
        }

        return maxY;
    }

    private int getMaxScroll() {

        int treeHeight = getTreeHeight();
        int visibleBottom = this.height - TREE_BOTTOM_MARGIN;

        return Math.max(
                0,
                treeHeight - visibleBottom
        );
    }

    private void clampScroll() {

        int maxScroll = getMaxScroll();

        if (scrollOffset < 0) {
            scrollOffset = 0;
        }

        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }
    }

    // =========================================================
    // TICK
    // =========================================================

    @Override
    public void tick() {
        super.tick();

        recalculateLayout();
        clampScroll();
    }

    // =========================================================
    // TEXTURAS DAS SKILLS
    // =========================================================

    /**
     * Escolhe a pasta da textura de acordo com o dono da skill.
     *
     * vampire_skills/
     * werewolf_skills/
     * hunter_skills/
     * cultist_skills/
     */
    private String getSkillTextureFolder(Skill skill) {

        if (skill.getOwnerType() == SkillOwnerType.RACE) {

            RaceType race = skill.getRaceOwner();

            if (race == RaceType.VAMPIRE) {
                return "vampire_skills";
            }

            if (race == RaceType.WEREWOLF) {
                return "werewolf_skills";
            }

        } else if (skill.getOwnerType() == SkillOwnerType.FACTION) {

            FactionType faction = skill.getFactionOwner();

            if (faction == FactionType.HUNTERS) {
                return "hunter_skills";
            }

            if (faction == FactionType.CULTISTS) {
                return "cultist_skills";
            }
        }

        return "generic_skills";
    }

    /**
     * Retorna o ResourceLocation final da textura.
     *
     * Exemplo:
     * eclipse:textures/gui/skills/vampire_skills/vampiric_sense.png
     */
    private ResourceLocation getSkillTexture(Skill skill) {

        return new ResourceLocation(
                "eclipse",
                "textures/gui/skills/"
                        + getSkillTextureFolder(skill)
                        + "/"
                        + skill.getTextureName()
                        + ".png"
        );
    }

    /**
     * Desenha a textura 20x20 da skill.
     */
    private void drawSkillTexture(
            MatrixStack matrixStack,
            Skill skill,
            int x,
            int y) {

        ResourceLocation texture = getSkillTexture(skill);

        Minecraft.getInstance()
                .getTextureManager()
                .bind(texture);

        blit(
                matrixStack,
                x,
                y,
                0,
                0,
                NODE_SIZE,
                NODE_SIZE,
                NODE_SIZE,
                NODE_SIZE
        );
    }

    /**
     * Overlay visual para manter a distinção entre:
     * verde = desbloqueada
     * azul = disponível
     * cinza/escura = bloqueada
     */
    private void drawSkillStateOverlay(
            MatrixStack matrixStack,
            Skill skill,
            int x,
            int y) {

        int overlayColor;

        if (ClientStatsCache.unlockedSkills.contains(skill.getId())) {
            overlayColor = OVERLAY_UNLOCKED;

        } else {
            boolean prereqsOk = skill.getPrerequisiteSkillIds()
                    .stream()
                    .allMatch(ClientStatsCache.unlockedSkills::contains);

            int nivelAtual = currentTab == Tab.RACE
                    ? ClientStatsCache.racialLevel
                    : ClientStatsCache.factionLevel;

            int pontosAtuais = currentTab == Tab.RACE
                    ? ClientStatsCache.skillPoints
                    : ClientStatsCache.factionSkillPoints;

            boolean levelOk = nivelAtual >= skill.getRequiredLevel();
            boolean hasPoints = pontosAtuais >= skill.getPointCost();

            overlayColor = prereqsOk && levelOk && hasPoints
                    ? OVERLAY_AVAILABLE
                    : OVERLAY_LOCKED;
        }

        fill(
                matrixStack,
                x,
                y,
                x + NODE_SIZE,
                y + NODE_SIZE,
                overlayColor
        );
    }

    // =========================================================
    // RENDER
    // =========================================================

    @Override
    public void render(
            @Nonnull MatrixStack matrixStack,
            int mouseX,
            int mouseY,
            float partialTicks) {

        this.renderBackground(matrixStack);

        int pontosDisponiveis = currentTab == Tab.RACE
                ? ClientStatsCache.skillPoints
                : ClientStatsCache.factionSkillPoints;

        this.font.draw(
                matrixStack,
                I18n.get("gui.eclipse.skill_tree.available_points", pontosDisponiveis),
                this.width / 2 - 60,
                4,
                0xFFFFFF
        );

        // =====================================================
        // LINHAS DE PRÉ-REQUISITO
        // =====================================================

        for (Skill skill : currentTreeSkills()) {

            int[] to = nodePositions.get(skill.getId());

            if (to == null) {
                continue;
            }

            int toY = to[1] - scrollOffset;

            for (String preId : skill.getPrerequisiteSkillIds()) {

                int[] from = nodePositions.get(preId);

                if (from == null) {
                    continue;
                }

                int fromY = from[1] - scrollOffset;

                boolean lineAbove =
                        fromY + NODE_SIZE < PANEL_TOP_MARGIN
                                && toY + NODE_SIZE < PANEL_TOP_MARGIN;

                boolean lineBelow =
                        fromY > this.height - TREE_BOTTOM_MARGIN
                                && toY > this.height - TREE_BOTTOM_MARGIN;

                if (lineAbove || lineBelow) {
                    continue;
                }

                drawLine(
                        matrixStack,
                        from[0] + NODE_SIZE / 2,
                        fromY + NODE_SIZE / 2,
                        to[0] + NODE_SIZE / 2,
                        toY + NODE_SIZE / 2,
                        COLOR_LINE
                );
            }
        }

        // =====================================================
        // NÓS
        // =====================================================

        for (Skill skill : currentTreeSkills()) {

            int[] pos = nodePositions.get(skill.getId());

            if (pos == null) {
                continue;
            }

            int drawY = pos[1] - scrollOffset;

            if (drawY + NODE_SIZE < PANEL_TOP_MARGIN
                    || drawY > this.height - TREE_BOTTOM_MARGIN) {
                continue;
            }

            // Borda preta 22x22 ao redor do ícone 20x20.
            fill(
                    matrixStack,
                    pos[0] - 1,
                    drawY - 1,
                    pos[0] + NODE_SIZE + 1,
                    drawY + NODE_SIZE + 1,
                    COLOR_NODE_BORDER
            );

            // Ícone personalizado 20x20.
            drawSkillTexture(
                    matrixStack,
                    skill,
                    pos[0],
                    drawY
            );

            // Mantém a informação visual do estado da skill.
            drawSkillStateOverlay(
                    matrixStack,
                    skill,
                    pos[0],
                    drawY
            );
        }

        // =====================================================
        // BOTÕES
        // =====================================================

        super.render(
                matrixStack,
                mouseX,
                mouseY,
                partialTicks
        );

        // =====================================================
        // TOOLTIP
        // =====================================================

        for (Skill skill : currentTreeSkills()) {

            int[] pos = nodePositions.get(skill.getId());

            if (pos == null) {
                continue;
            }

            int drawY = pos[1] - scrollOffset;

            if (mouseX >= pos[0]
                    && mouseX <= pos[0] + NODE_SIZE
                    && mouseY >= drawY
                    && mouseY <= drawY + NODE_SIZE) {

                if (drawY + NODE_SIZE >= PANEL_TOP_MARGIN
                        && drawY <= this.height - TREE_BOTTOM_MARGIN) {

                    renderComponentTooltip(
                            matrixStack,
                            buildTooltip(skill),
                            mouseX,
                            mouseY
                    );
                }
            }
        }
    }

    // =========================================================
    // TOOLTIP
    // =========================================================

    private List<net.minecraft.util.text.ITextComponent> buildTooltip(Skill skill) {

        List<net.minecraft.util.text.ITextComponent> lines =
                new ArrayList<>();

        // =====================================================
        // NOME
        // =====================================================

        lines.add(
                new StringTextComponent(
                        I18n.get(skill.getDisplayName())
                )
        );

        // =====================================================
        // DESCRIÇÃO
        // =====================================================

        lines.add(
                new StringTextComponent(
                        I18n.get(skill.getDescription())
                )
        );

        // =====================================================
        // NÍVEL NECESSÁRIO
        // =====================================================

        lines.add(
                new StringTextComponent(
                        I18n.get("skill.eclipse.required_level")
                                + ": "
                                + skill.getRequiredLevel()
                )
        );

        // =====================================================
        // CUSTO
        // =====================================================

        lines.add(
                new StringTextComponent(
                        I18n.get("skill.eclipse.cost")
                                + ": "
                                + skill.getPointCost()
                )
        );

        // =====================================================
        // PRÉ-REQUISITOS
        // =====================================================

        if (!skill.getPrerequisiteSkillIds().isEmpty()) {

            lines.add(
                    new StringTextComponent(
                            I18n.get("skill.eclipse.requires")
                                    + ":"
                    )
            );

            for (String preId : skill.getPrerequisiteSkillIds()) {

                Skill preSkill = SkillTree.find(preId);

                if (preSkill != null) {

                    boolean unlocked =
                            ClientStatsCache.unlockedSkills.contains(preId);

                    String prerequisiteName =
                            I18n.get(preSkill.getDisplayName());

                    String status =
                            unlocked
                                    ? I18n.get(
                                    "skill.eclipse.prerequisite_unlocked"
                            )
                                    : I18n.get(
                                    "skill.eclipse.prerequisite_locked"
                            );

                    lines.add(
                            new StringTextComponent(
                                    "  "
                                            + prerequisiteName
                                            + " "
                                            + status
                            )
                    );

                } else {

                    lines.add(
                            new StringTextComponent(
                                    "  "
                                            + I18n.get(
                                            "skill.eclipse.unknown_prerequisite",
                                            preId
                                    )
                            )
                    );
                }
            }
        }

        // =====================================================
        // COOLDOWN
        // =====================================================

        if (skill.getType()
                != com.soldierskull.eclipse.skills.SkillType.PASSIVE) {

            if (skill.getCooldownMillis() > 0) {

                lines.add(
                        new StringTextComponent(
                                I18n.get("skill.eclipse.cooldown")
                                        + ": "
                                        + (skill.getCooldownMillis() / 1000L)
                                        + "s"
                        )
                );
            }

            // =================================================
            // ENERGIA
            // =================================================

            if (skill.getEnergyCost() > 0) {

                lines.add(
                        new StringTextComponent(
                                I18n.get("skill.eclipse.energy")
                                        + ": "
                                        + skill.getEnergyCost()
                        )
                );
            }
        }

        // =====================================================
        // STATUS
        // =====================================================

        if (ClientStatsCache.unlockedSkills.contains(skill.getId())) {

            long cooldown =
                    ClientStatsCache.getSkillCooldownSecondsRemaining(
                            skill.getId()
                    );

            if (cooldown > 0) {

                lines.add(
                        new StringTextComponent(
                                I18n.get("skill.eclipse.on_cooldown")
                                        + ": "
                                        + cooldown
                                        + "s"
                        )
                );

            } else {

                lines.add(
                        new StringTextComponent(
                                I18n.get(
                                        "skill.eclipse.unlocked"
                                )
                        )
                );
            }
        }

        return lines;
    }


    // =========================================================
    // COR DOS NÓS
    // =========================================================

    /**
     * Mantido para compatibilidade com o sistema anterior.
     */
    private int colorFor(Skill skill) {

        if (ClientStatsCache.unlockedSkills.contains(
                skill.getId())) {

            return COLOR_UNLOCKED;
        }

        boolean prereqsOk =
                skill.getPrerequisiteSkillIds()
                        .stream()
                        .allMatch(
                                ClientStatsCache.unlockedSkills::contains
                        );

        int nivelAtual =
                currentTab == Tab.RACE
                        ? ClientStatsCache.racialLevel
                        : ClientStatsCache.factionLevel;

        int pontosAtuais =
                currentTab == Tab.RACE
                        ? ClientStatsCache.skillPoints
                        : ClientStatsCache.factionSkillPoints;

        boolean levelOk =
                nivelAtual >= skill.getRequiredLevel();

        boolean hasPoints =
                pontosAtuais >= skill.getPointCost();

        return prereqsOk && levelOk && hasPoints
                ? COLOR_AVAILABLE
                : COLOR_LOCKED;
    }

    // =========================================================
    // CLIQUE
    // =========================================================

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button) {

        for (Map.Entry<String, int[]> entry :
                nodePositions.entrySet()) {

            int[] pos = entry.getValue();

            if (pos == null) {
                continue;
            }

            int drawY = pos[1] - scrollOffset;

            if (drawY + NODE_SIZE < PANEL_TOP_MARGIN
                    || drawY > this.height - TREE_BOTTOM_MARGIN) {

                continue;
            }

            if (mouseX >= pos[0]
                    && mouseX <= pos[0] + NODE_SIZE
                    && mouseY >= drawY
                    && mouseY <= drawY + NODE_SIZE) {

                PacketHandler.INSTANCE.sendToServer(
                        new PacketUnlockSkill(entry.getKey())
                );

                return true;
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    // =========================================================
    // RODA DO MOUSE
    // =========================================================

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double delta) {

        if (getTreeHeight()
                > this.height - TREE_BOTTOM_MARGIN) {

            scrollOffset -=
                    (int) (delta * SCROLL_SPEED);

            clampScroll();

            return true;
        }

        return super.mouseScrolled(
                mouseX,
                mouseY,
                delta
        );
    }

    // =========================================================
    // PAUSA
    // =========================================================

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // =========================================================
    // LINHA ENTRE NÓS
    // =========================================================

    private void drawLine(
            MatrixStack matrixStack,
            int x1,
            int y1,
            int x2,
            int y2,
            int color) {

        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps =
                Math.max(
                        Math.abs(dx),
                        Math.abs(dy)
                );

        if (steps == 0) {

            fill(
                    matrixStack,
                    x1,
                    y1,
                    x1 + 2,
                    y1 + 2,
                    color
            );

            return;
        }

        float stepX =
                dx / (float) steps;

        float stepY =
                dy / (float) steps;

        for (int i = 0; i <= steps; i += 2) {

            int x =
                    Math.round(
                            x1 + stepX * i
                    );

            int y =
                    Math.round(
                            y1 + stepY * i
                    );

            fill(
                    matrixStack,
                    x,
                    y,
                    x + 2,
                    y + 2,
                    color
            );
        }
    }
}