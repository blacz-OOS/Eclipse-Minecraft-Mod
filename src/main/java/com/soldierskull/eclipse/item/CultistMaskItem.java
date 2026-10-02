package com.soldierskull.eclipse.item;

import com.soldierskull.eclipse.client.model.CultistMaskModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CultistMaskItem extends ArmorItem {

    public CultistMaskItem(Item.Properties properties) {
        super(
                CultistMaskArmorMaterial.CULTIST_MASK,
                EquipmentSlotType.HEAD,
                properties
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public <A extends BipedModel<?>> A getArmorModel(
            LivingEntity entityLiving,
            ItemStack itemStack,
            EquipmentSlotType armorSlot,
            A defaultModel) {

        if (armorSlot != EquipmentSlotType.HEAD) {
            return defaultModel;
        }

        CultistMaskModel model = new CultistMaskModel();

        /*
         * Copia as transformações do modelo do jogador
         * para o modelo da máscara.
         */
        ((BipedModel<LivingEntity>) defaultModel).copyPropertiesTo(model);

        return (A) model;
    }

    @Override
    public String getArmorTexture(
            ItemStack stack,
            Entity entity,
            EquipmentSlotType slot,
            String type) {

        return "eclipse:textures/models/armor/cultist_mask.png";
    }
}