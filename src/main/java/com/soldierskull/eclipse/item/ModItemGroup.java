//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.soldierskull.eclipse.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;

public class ModItemGroup {
    public static final ItemGroup ECLIPSE_GROUP = new ItemGroup("EclipseTab") {
        public ItemStack makeIcon() {
            return new ItemStack((IItemProvider)ModItems.RAW_SILVER.get());
        }

        /** Grimório do Eclipse (mesmo conteúdo do /eclipse book) aparece aqui também, sem precisar de comando/OP. */
        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.add(com.soldierskull.eclipse.commands.subcommands.BookCommand.createBook());
        }
    };

    public ModItemGroup() {
    }
}
