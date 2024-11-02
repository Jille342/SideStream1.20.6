package client.features.modules.player;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.modules.Module;
import client.settings.BooleanSetting;
import client.settings.NumberSetting;
import client.utils.TimeHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Arrays;

public class InvManager extends Module {

    private  BooleanSetting openInv;
    private  NumberSetting delay;
    private  BooleanSetting equipArmor;
    private BooleanSetting dropArmor;
    private  BooleanSetting dropSword;
    private  BooleanSetting dropTools ;
    private BooleanSetting dropTrash;
    private final TimeHelper interactionTimer = new TimeHelper();

    public InvManager() {
        super("InvManager", 0, Category.PLAYER);
    }
    public void init()
    {
        openInv = new BooleanSetting("Open Inv" ,true);
        equipArmor = new BooleanSetting("Equip Armor", true);
        dropArmor = new BooleanSetting("Drop Armor", true);
        delay = new NumberSetting("Delay",250, 0, 1000, 1);
        dropTrash = new BooleanSetting("Drop Trash", true);
        dropTools = new BooleanSetting("Drop Tools", true);
        dropSword = new BooleanSetting("Drop Sword", true);
        super.init();
        addSetting(openInv,equipArmor,dropArmor,delay,dropTrash,dropTools,dropSword);
    }

    public void onEvent(Event<?> event) {
     if  (event instanceof EventUpdate){
            if (openInv.getValue() && mc.currentScreen == null) {
                return;
            }
            if (mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen)) {
                return;
            }
            MutablePair<Integer, Float>[] bestTools = new MutablePair[]{
                    new MutablePair<>(-1, 0f), // 0 = boots
                    new MutablePair<>(-1, 0f), // 1 = leggings
                    new MutablePair<>(-1, 0f), // 2 = chestplate
                    new MutablePair<>(-1, 0f), // 3 = helmet
                    new MutablePair<>(-1, 0f), // 4 = sword
                    new MutablePair<>(-1, 0f), // 5 = pickaxe
                    new MutablePair<>(-1, 0f), // 6 = axe
                    new MutablePair<>(-1, 0f), // 7 = shovel
                    new MutablePair<>(-1, 0f), // 8 = shears
                    new MutablePair<>(-1, 0f), // 9 = bow
                    new MutablePair<>(-1, 0f), // 10 = food
            };

            // Gather best armor indexes
            for (int i = 0; i < 40; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                Item item = stack.getItem();
                if (stack.isEmpty()) {
                    continue;
                }


                // armor
                if (item instanceof ArmorItem armorItem) {
                    float armorValue = getArmorValue(stack, armorItem);
                    int armorIndex = getIndexOfArmorType(armorItem.getType());

                    if (bestTools[armorIndex].right < armorValue) {
                        bestTools[armorIndex].left = i;
                        bestTools[armorIndex].right = armorValue;
                    }
                }

                // sword
                if (item instanceof SwordItem swordItem) {
                    float swordValue = getSwordValue(stack, swordItem);

                    if (bestTools[4].right < swordValue) {
                        bestTools[4].left = i;
                        bestTools[4].right = swordValue;
                    }
                }

                // pickaxe
                if (item instanceof PickaxeItem pickaxeItem) {
                    float pickaxeValue = getPickaxeValue(stack, pickaxeItem);

                    if (bestTools[5].right < pickaxeValue) {
                        bestTools[5].left = i;
                        bestTools[5].right = pickaxeValue;
                    }
                }

                // axe
                if (item instanceof AxeItem axeItem) {
                    float axeValue = getAxeValue(stack, axeItem);

                    if (bestTools[6].right < axeValue) {
                        bestTools[6].left = i;
                        bestTools[6].right = axeValue;
                    }
                }

                // shovel
                if (item instanceof ShovelItem shovelItem) {
                    float shovelValue = getShovelValue(stack, shovelItem);

                    if (bestTools[7].right < shovelValue) {
                        bestTools[7].left = i;
                        bestTools[7].right = shovelValue;
                    }
                }

                // shears
                if (item instanceof ShearsItem shearsItem) {
                    float shearsValue = getShearsValue(stack, shearsItem);

                    if (bestTools[8].right < shearsValue) {
                        bestTools[8].left = i;
                        bestTools[8].right = shearsValue;
                    }
                }

                // bow
                if (item instanceof BowItem bowItem) {
                    float bowValue = getBowValue(stack, bowItem);

                    if (bestTools[9].right < bowValue) {
                        bestTools[9].left = i;
                        bestTools[9].right = bowValue;
                    }
                }


            }

            if (equipArmor.getValue()) {
                for (int i = 0; i < 4; i++) {
                    MutablePair<Integer, Float> bestArmor = bestTools[i];
                    if (bestArmor.left == -1 || bestArmor.left >= 36) {
                        continue;
                    }

                    if (!mc.player.getInventory().armor.get(i).isEmpty()) {
                        drop(8 - i);
                    } else {
                        shiftClick(bestArmor.left < 9 ? 36 + bestArmor.left : bestArmor.left);
                    }
                }
            }

            // drop unnecessary items
            for (int i = 0; i < 40; i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                Item item = stack.getItem();
                if (stack.isEmpty()) {
                    continue;
                }
                boolean flag = false;
                for (Item goodItem : GOOD_ITEMS) {
                    if (item == goodItem) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue;

                // armor
                if (item instanceof ArmorItem armorItem) {
                    if (dropArmor.getValue()) {
                        if (bestTools[getIndexOfArmorType(armorItem.getType())].left != i) {
                            drop(i < 9 ? 36 + i : i);
                        }
                    }
                }

                // sword
                if (item instanceof SwordItem swordItem) {
                    if (dropSword.getValue()) {
                        if (bestTools[4].left != i) {
                            drop(i < 9 ? 36 + i : i);
                        }
                    }
                }

                // pickaxe
                if (item instanceof PickaxeItem pickaxeItem) {
                    if (dropTools.getValue()) {
                        if (bestTools[5].left != i) {
                            drop(i < 9 ? 36 + i : i);
                        }
                    }
                }

                // axe
                if (item instanceof AxeItem axeItem) {
                    if (dropTools.getValue()) {
                        if (bestTools[6].left != i) {
                            drop(i < 9 ? 36 + i : i);
                        }
                    }
                }

                // shovel
                if (item instanceof ShovelItem shovelItem) {
                    if (dropTools.getValue()) {
                        if (bestTools[7].left != i) {
                            drop(i < 9 ? 36 + i : i);
                        }
                    }
                }

                // shears
                if (item instanceof ShearsItem shearsItem) {
                    if (dropTools.getValue()) {
                        if (bestTools[8].left != i) {
                            drop(i < 9 ? 36 + i : i);
                        }
                    }
                }

                // bow
                if (item instanceof BowItem bowItem) {
                    if (dropTools.getValue()) {
                        if (bestTools[9].left != i) {
                            drop(i < 9 ? 36 + i : i);
                        }
                    }
                }
                if(isTrash(stack) && dropTrash.getValue()){
                    drop(i < 9 ? 36 + i : i);
                }

            }
        }
    }

    private static final Item[] GOOD_ITEMS = {
            Items.WATER_BUCKET,
            Items.LAVA_BUCKET,
            Items.BUCKET,
            Items.ENDER_PEARL,
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE
    };
    private static  boolean isTrash(ItemStack stack){
        return (stack.getItem().getTranslationKey().contains("tnt")) ||
                (stack.getItem().getTranslationKey().contains("stick")) ||
                (stack.getItem().getTranslationKey().contains("egg")) ||
                (stack.getItem().getTranslationKey().contains("string")) ||
                (stack.getItem().getTranslationKey().contains("cake")) ||
                (stack.getItem().getTranslationKey().contains("mushroom")) ||
                (stack.getItem().getTranslationKey().contains("flint")) ||
                (stack.getItem().getTranslationKey().contains("compass")) ||
                (stack.getItem().getTranslationKey().contains("dyePowder")) ||
                (stack.getItem().getTranslationKey().contains("feather")) ||
                (stack.getItem().getTranslationKey().contains("bucket")) ||
                (stack.getItem().getTranslationKey().contains("chest") && !stack.getName().getString().toLowerCase().contains("collect")) ||
                (stack.getItem().getTranslationKey().contains("snow")) ||
                (stack.getItem().getTranslationKey().contains("fish")) ||
                (stack.getItem().getTranslationKey().contains("enchant")) ||
                (stack.getItem().getTranslationKey().contains("exp")) ||
                (stack.getItem().getTranslationKey().contains("shears")) ||
                (stack.getItem().getTranslationKey().contains("anvil")) ||
                (stack.getItem().getTranslationKey().contains("torch")) ||
                (stack.getItem().getTranslationKey().contains("seeds")) ||
                (stack.getItem().getTranslationKey().contains("leather")) ||
                (stack.getItem().getTranslationKey().contains("reeds")) ||
                (stack.getItem().getTranslationKey().contains("skull")) ||
                (stack.getItem().getTranslationKey().contains("record")) ||
                (stack.getItem().getTranslationKey().contains("snowball")) ||
                (stack.getItem() instanceof GlassBottleItem) ||
                (stack.getItem().getTranslationKey().contains("piston"));
    }

    private static float getSwordValue(ItemStack stack, SwordItem item) {
        return item.getMaterial().getAttackDamage();
    }

    private static float getPickaxeValue(ItemStack stack, PickaxeItem item) {
        return item.getMaterial().getMiningSpeedMultiplier();
    }

    private static float getAxeValue(ItemStack stack, AxeItem item) {
        return item.getMaterial().getMiningSpeedMultiplier();
    }

    private static float getShovelValue(ItemStack stack, ShovelItem item) {
        return item.getMaterial().getMiningSpeedMultiplier();
    }

    private static float getShearsValue(ItemStack stack, ShearsItem item) {
        return 1 - (float) stack.getDamage() / stack.getMaxDamage();
    }

    private static float getBowValue(ItemStack stack, BowItem item) {
        float value = 0;
        value += EnchantmentHelper.getLevel(Enchantments.POWER, stack) * 0.5f;
        value += EnchantmentHelper.getLevel(Enchantments.PUNCH, stack) * 0.5f;
        value += EnchantmentHelper.getLevel(Enchantments.FLAME, stack) * 0.5f;
        return value;
    }

    private static float getFoodValue(ItemStack stack, FoodComponent item) {
        return stack.getCount() * item.nutrition();
    }

    private static float getArmorValue(ItemStack stack, ArmorItem item) {
        float value = item.getProtection();
        value += EnchantmentHelper.getLevel(Enchantments.PROTECTION, stack) * 0.5f;
        value += EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack) * 0.5f;
        return value;
    }

    private static int getIndexOfArmorType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 0;
            case LEGGINGS -> 1;
            case CHESTPLATE -> 2;
            case HELMET -> 3;
            case BODY -> 4;
        };
    }

    private void drop(int slot) {
        if (interactionTimer.hasReached((long) delay.getValue())) {
            interactionTimer.reset();
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 1, SlotActionType.THROW, mc.player);
        }
    }

    private void shiftClick(int slot) {
        if (interactionTimer.hasReached((long) delay.getValue())) {
            interactionTimer.reset();
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
        }
    }
}
