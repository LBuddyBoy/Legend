package dev.lbuddyboy.legend.features.shop.model;

import dev.lbuddyboy.commons.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ShopCategory {

    BUY(
            11,
            "Buy Shop",
            "&2",
            "&a",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQwNjNiYTViMTZiNzAzMGEyMGNlNmYwZWE5NmRjZDI0YjA2NDgzNmY1NzA0NTZjZGJmYzllODYxYTc1ODVhNSJ9fX0="
    ),
    SELL(
            12,
            "Sell Shop",
            "&4",
            "&c",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIwZWYwNmRkNjA0OTk3NjZhYzhjZTE1ZDJiZWE0MWQyODEzZmU1NTcxODg2NGI1MmRjNDFjYmFhZTFlYTkxMyJ9fX0="
    ),
    WOOL(
            13,
            "Wool Shop",
            "&9",
            "&b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI3NmFiZDFlYjQwNGRmNzRkY2VjYWFkZWUwMmQyMWU3NTYwMmRhYjk0OGFhZDc2MzI1ODUzYzVkNDljY2Y3ZSJ9fX0="
    ),
    CONCRETE(
            14,
            "Concrete Shop",
            "&5",
            "&d",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBjMDVkNTYwZDhlMTNmMGNiMjVjMTVjODMxYmM1OTU0NTBjNWU1NGNlMzVmYTU0ZTE3ZTA0OTUyNjdjIn19fQ=="
    ),
    GLASS(
            15,
            "Glass Shop",
            "&6",
            "&e",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VkZTBjMmFhNmE3ZDI3MzA3OGU4ZGNjZGVmYzlkZWE0NjVmN2UyYjQ5NTEyOWU2ZGEwNmYzNGE2MTMyOWU1MiJ9fX0="
    ),
    EXTRA(
            17,
            "Extra Shop",
            "&3",
            "&b",
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGViZjZiZTgyZTgyM2U1ODJmZjhmNDVlMDFjZjUxNzUxNzM0ZDlhNzc2Y2ZmNTE0ZTg4ZWEyNmQ0OTczYmJmMCJ9fX0="
    );

    private final int menuSlot;
    private final String name, primaryColor, secondaryColor, headTexture;

    public String getColoredName() {
        return CC.blend(this.name, this.primaryColor, this.secondaryColor, "&l");
    }

}
