package dev.lbuddyboy.legend.team.menu;

import dev.lbuddyboy.commons.api.APIConstants;
import dev.lbuddyboy.commons.menu.IButton;
import dev.lbuddyboy.commons.menu.IMenu;
import dev.lbuddyboy.commons.menu.config.ISettingMenu;
import dev.lbuddyboy.commons.menu.settings.GUISettings;
import dev.lbuddyboy.commons.util.CC;
import dev.lbuddyboy.commons.util.ItemFactory;
import dev.lbuddyboy.commons.util.ItemUtils;
import dev.lbuddyboy.legend.LangConfig;
import dev.lbuddyboy.legend.team.model.Team;
import dev.lbuddyboy.legend.team.model.brew.BrewData;
import dev.lbuddyboy.legend.team.model.brew.BrewType;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TeamBrewMenu extends IMenu {

    private static final int[] SLOTS = new int[]{
            14, 15, 16, 17,
            23, 24, 25, 26,
            32, 33, 34, 35,
    };

    private final Team team;

    public TeamBrewMenu(Team team) {
        this.team = team;

        BrewType.init();
    }

    @Override
    public String getTitle(Player player) {
        return team.getName() + "'s Team Brew";
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    @Override
    public Map<Integer, IButton> getButtons(Player player) {
        Map<Integer, IButton> buttons = new HashMap<>();
        List<Material> materials = BrewType.MATERIALS;

        for (BrewType type : BrewType.values()) {
            buttons.put(type.getSlot(), new PotionButton(type));
        }

        int index = 0;
        for (int slot : SLOTS) {
            if (index >= materials.size()) continue;

            buttons.put(slot, new MaterialButton(materials.get(index++)));
        }

        return buttons;
    }

    @Override
    public boolean autoFills(Player player) {
        return true;
    }

    @Override
    public boolean autoUpdating(Player player) {
        return true;
    }

    @Override
    public long autoUpdateEvery() {
        return 1_000L;
    }

    @AllArgsConstructor
    public class PotionButton extends IButton {

        private final BrewType type;

        @Override
        public ItemStack getItem(Player player) {
            List<String> lore = new ArrayList<>();

            lore.add(" ");
            lore.add(CC.blend("&lRequired Materials", type.getPrimaryColor(), type.getSecondaryColor()));

            for (Material material : type.getBrewMaterials()) {
                ChatColor color = ChatColor.GREEN;
                if (team.getBrewData().getBrewingMaterials(material) <= 0) color = ChatColor.RED;

                lore.add(type.getSecondaryColor() + "• " + color + ItemUtils.getName(material));
            }

            lore.addAll(Arrays.asList(
                    "",
                    CC.blend("&lInformation", type.getPrimaryColor(), type.getSecondaryColor()),
                    type.getSecondaryColor() + "• &fAmount&7: " + type.getSecondaryColor() + APIConstants.formatNumber(team.getBrewData().getBrewedPotions(this.type)),
                    type.getSecondaryColor() + "• &fBrewing&7: " + (team.getBrewData().isBrewing(type) ? "&a&lON &7(" + team.getBrewData().getBrewTimeLeft(this.type) + "s)" : "&c&lOFF"),
                    type.getSecondaryColor() + "• &fMaterials&7: " + (team.getBrewData().canBrew(type) ? "&a&lVALID" : "&c&lINVALID"),
                    "",
                    "&a&o(( Left Click to start the brewing process ))",
                    "&c&o(( Right Click to stop the brewing process ))",
                    "&e&o(( Shift + Left Click to withdraw x1 potion ))",
                    "&d&o(( Shift + Right Click to withdraw ALL potions ))"
            ));

            return new ItemFactory(type.isSplash() ? Material.SPLASH_POTION : Material.POTION)
                    .displayName(type.getColoredNameBold())
                    .addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
                    .lore(lore)
                    .potionEffect(this.type.getType())

                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            BrewData brewData = team.getBrewData();

            if (clickType == ClickType.RIGHT || clickType == ClickType.LEFT) {
                if (clickType == ClickType.RIGHT) {
                    if (!brewData.getStartedBrewing().containsKey(this.type)) {
                        LangConfig.TEAM_BREW_NOT_BREWING.sendMessage(player);
                        return;
                    }

                    brewData.getStartedBrewing().remove(this.type);
                    brewData.getLastBrewedTimes().put(this.type, -1L);
                    return;
                }
                if (brewData.getStartedBrewing().containsKey(this.type)) {
                    LangConfig.TEAM_BREW_ALREADY_BREWING.sendMessage(player);
                    return;
                }

                brewData.getStartedBrewing().put(this.type, true);
                brewData.getLastBrewedTimes().put(this.type, System.currentTimeMillis());
                return;
            }

            if (clickType != ClickType.SHIFT_RIGHT && clickType != ClickType.SHIFT_LEFT) return;

            long amountOfPotions = team.getBrewData().getBrewedPotions(this.type);
            long amountToWithdraw = Math.min(amountOfPotions, clickType == ClickType.SHIFT_LEFT ? 1 : 64);
            int amountWithdrawn = 0;

            ItemStack item = new ItemFactory(type.isSplash() ? Material.SPLASH_POTION : Material.POTION).potionEffect(type.getType()).build();

            for (int i = 0; i < amountToWithdraw; i++) {
                ItemStack stack = ItemUtils.tryFit(player, item);

                if (stack == null) {
                    player.getInventory().addItem(item);
                    amountWithdrawn++;
                    continue;
                }

                if (amountWithdrawn == 0) {
                    player.sendMessage(CC.translate("&cYour inventory is full..."));
                    return;
                }
            }

            brewData.getBrewedPotions().put(this.type, amountOfPotions - amountWithdrawn);
            team.flagSave();
            team.sendMessage(LangConfig.TEAM_BREW_WITHDRAW.getString()
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%amount%", String.valueOf(amountWithdrawn))
                    .replaceAll("%material%", this.type.getDisplayName())
            );
            updateMenu(player, true);
        }
    }

    @AllArgsConstructor
    public class MaterialButton extends IButton {

        private final Material material;

        @Override
        public ItemStack getItem(Player player) {
            int amount = (int) Math.min(team.getBrewData().getBrewingMaterials(this.material), 64);

            return new ItemFactory(this.material)
                    .amount(1)
                    .displayName(CC.translate("<blend:#FB9C08;#FB5708>&l" + ItemUtils.getName(this.material) + "</>"))
                    .lore(Arrays.asList(
                            "&#fb9c08• &7Deposit and withdraw materials to",
                            "&#fb9c08• &7begin brewing for intense PvP!",
                            " ",
                            "&#fb9c08• &fAmount&7: &#fb9c08" + APIConstants.formatNumber(team.getBrewData().getBrewingMaterials(this.material)),
                            " ",
                            "&a&o(( Left Click to deposit +1 " + ItemUtils.getName(this.material) + " ))",
                            "&a&o(( Shift + Left Click to deposit +64 " + ItemUtils.getName(this.material) + " ))",
                            "&c&o(( Right Click to withdraw +1 " + ItemUtils.getName(this.material) + " ))",
                            "&c&o(( Shift + Right Click to withdraw +64 " + ItemUtils.getName(this.material) + " ))"
                    ))
                    .amount(Math.max(1, amount))
                    .build();
        }

        @Override
        public void action(Player player, ClickType clickType, int slot) {
            BrewData brewData = team.getBrewData();
            long materials = brewData.getBrewingMaterials(this.material);

            if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
                if (materials <= 0) {
                    LangConfig.TEAM_BREW_NO_MATERIALS_TEAM.sendMessage(player);
                    return;
                }

                int amountToWithdraw = (int) Math.min(materials, clickType == ClickType.RIGHT ? 1 : 64);

                for (int i = 0; i < amountToWithdraw; i++) {
                    ItemUtils.tryFit(player, new ItemStack(this.material, 1), false);
                }

                brewData.getBrewingMaterials().put(this.material, brewData.getBrewingMaterials(this.material) - amountToWithdraw);
                team.flagSave();
                team.sendMessage(LangConfig.TEAM_BREW_WITHDRAW.getString()
                        .replaceAll("%player%", player.getName())
                        .replaceAll("%amount%", String.valueOf(amountToWithdraw))
                        .replaceAll("%material%", ItemUtils.getName(this.material))
                );
                updateMenu(player, true);
                return;
            }

            if (clickType != ClickType.LEFT && clickType != ClickType.SHIFT_LEFT) return;

            int amountInInventory = ItemUtils.countStackAmountMatching(player.getInventory().getStorageContents(), stack -> stack != null && stack.getType() == this.material && stack.getEnchantments().isEmpty());

            if (amountInInventory <= 0) {
                LangConfig.TEAM_BREW_NO_MATERIALS_PLAYER.sendMessage(player);
                return;
            }

            int amountToDeposit = Math.min(clickType == ClickType.LEFT ? 1 : 64, amountInInventory);

            brewData.getBrewingMaterials().put(this.material, materials + amountToDeposit);
            ItemUtils.removeAmount(player.getInventory(), new ItemStack(this.material), amountToDeposit);

            team.flagSave();
            updateMenu(player, true);

            team.sendMessage(LangConfig.TEAM_BREW_DEPOSITED.getString()
                    .replaceAll("%player%", player.getName())
                    .replaceAll("%amount%", String.valueOf(amountToDeposit))
                    .replaceAll("%material%", ItemUtils.getName(this.material))
            );
        }
    }

}
