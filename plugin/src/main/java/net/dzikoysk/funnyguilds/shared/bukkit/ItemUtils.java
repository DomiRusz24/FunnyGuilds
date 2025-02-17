package net.dzikoysk.funnyguilds.shared.bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.nms.EggTypeChanger;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.shared.FunnyFormatter;
import net.dzikoysk.funnyguilds.shared.FunnyStringUtils;
import net.dzikoysk.funnyguilds.shared.spigot.ItemComponentUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import panda.std.Option;
import panda.std.Pair;
import panda.std.stream.PandaStream;
import panda.utilities.text.Joiner;

public final class ItemUtils {

    private static Method BY_IN_GAME_NAME_ENCHANT;
    private static Method CREATE_NAMESPACED_KEY;

    private static Method GET_IN_GAME_NAME_ENCHANT;
    private static Method GET_NAMESPACED_KEY;

    static {
        if (!Reflections.USE_PRE_12_METHODS) {
            Class<?> namespacedKeyClass = Reflections.getBukkitClass("NamespacedKey");

            BY_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment.class, "getByKey");
            CREATE_NAMESPACED_KEY = Reflections.getMethod(namespacedKeyClass, "minecraft", String.class);

            GET_IN_GAME_NAME_ENCHANT = Reflections.getMethod(Enchantment.class, "getKey");
            GET_NAMESPACED_KEY = Reflections.getMethod(namespacedKeyClass, "getKey");
        }
    }

    private ItemUtils() {
    }

    public static boolean playerHasEnoughItems(Player player, List<ItemStack> requiredItems, String message) {
        boolean enableItemComponent = FunnyGuilds.getInstance().getPluginConfiguration().enableItemComponent;

        for (ItemStack requiredItem : requiredItems) {
            if (player.getInventory().containsAtLeast(requiredItem, requiredItem.getAmount())) {
                continue;
            }

            if (message.isEmpty()) {
                return false;
            }

            if (enableItemComponent) {
                player.spigot().sendMessage(ItemComponentUtils.translateComponentPlaceholder(message, requiredItems, requiredItem));
            }
            else {
                player.sendMessage(translateTextPlaceholder(message, requiredItems, requiredItem));
            }

            return false;
        }

        return true;
    }

    public static String translateTextPlaceholder(String message, Collection<ItemStack> items, ItemStack item) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        FunnyFormatter formatter = new FunnyFormatter();

        if (message.contains("{ITEM}")) {
            formatter.register("{ITEM}", item.getAmount() + config.itemAmountSuffix.getValue() + " " +
                    MaterialUtils.getMaterialName(item.getType()));
        }

        if (message.contains("{ITEMS}")) {
            formatter.register("{ITEMS}", FunnyStringUtils.join(
                    PandaStream.of(items)
                            .map(itemStack -> itemStack.getAmount() + config.itemAmountSuffix.getValue() + " " +
                                    MaterialUtils.getMaterialName(itemStack.getType()))
                            .toList(),
                    true)
            );
        }

        if (message.contains("{ITEM-NO-AMOUNT}")) {
            formatter.register("{ITEM-NO-AMOUNT}", MaterialUtils.getMaterialName(item.getType()));
        }

        return formatter.format(message);
    }

    public static ItemStack parseItem(String itemString) {
        String[] split = itemString.split(" ");
        String[] typeSplit = split[1].split(":");
        String subtype = typeSplit.length > 1 ? typeSplit[1] : "0";

        Material material = MaterialUtils.parseMaterial(typeSplit[0], false);
        Option<Integer> amount = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[0])).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown amount: " + split[0]);
        });

        Option<Integer> data = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(subtype)).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown data: " + subtype);
        });

        ItemBuilder item = new ItemBuilder(material, amount.orElseGet(1), data.orElseGet(0));
        FunnyFormatter formatter = new FunnyFormatter().register("_", " ").register("{HASH}", "#");

        for (int index = 2; index < split.length; index++) {
            String[] itemAttributes = split[index].split(":", 2);

            if (itemAttributes.length != 2) {
                FunnyGuilds.getPluginLogger().parser("Unknown item meta attribute: " + itemAttributes[0]);
                continue;
            }

            String attributeName = itemAttributes[0];
            String attributeValue = itemAttributes[1];

            switch (attributeName.toLowerCase(Locale.ROOT)) {
                case "name":
                case "displayname":
                    item.setName(formatter.format(attributeValue), true);
                    continue;
                case "lore":
                    List<String> lore = PandaStream.of(attributeValue.split("#")).map(formatter::format).toList();
                    item.setLore(lore, true);
                    continue;
                case "enchant":
                case "enchantment":
                    Pair<Enchantment, Integer> parsedEnchant = parseEnchant(attributeValue);
                    if (parsedEnchant.getFirst() == null) {
                        continue;
                    }

                    item.addEnchant(parsedEnchant.getFirst(), parsedEnchant.getSecond());
                    continue;
                case "enchants":
                case "enchantments":
                    PandaStream.of(attributeValue.split(","))
                            .map(ItemUtils::parseEnchant)
                            .filter(enchant -> enchant.getFirst() != null)
                            .forEach(enchant -> item.addEnchant(enchant.getFirst(), enchant.getSecond()));
                    continue;
                case "skullowner":
                    if (!(item.getMeta() instanceof SkullMeta)) {
                        FunnyGuilds.getPluginLogger().parser("Invalid item skull owner attribute (given item is not a skull!): " + split[index]);
                        continue;
                    }

                    ((SkullMeta) item.getMeta()).setOwner(attributeValue);
                    item.refreshMeta();
                    continue;
                case "flags":
                case "itemflags":
                    PandaStream.of(attributeValue.split(","))
                            .map(String::trim)
                            .mapOpt(ItemUtils::matchItemFlag)
                            .forEach(item::setFlag);

                    continue;
                case "armorcolor":
                    if (!(item.getMeta() instanceof LeatherArmorMeta)) {
                        FunnyGuilds.getPluginLogger().parser("Invalid item armor color attribute (given item is not a leather armor!): " + split[index]);
                        continue;
                    }

                    String[] colorSplit = attributeValue.split("_");

                    try {
                        Color color = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
                        ((LeatherArmorMeta) item.getMeta()).setColor(color);
                        item.refreshMeta();
                    }
                    catch (NumberFormatException numberFormatException) {
                        FunnyGuilds.getPluginLogger().parser("Invalid armor color: " + attributeValue);
                    }

                    continue;
                case "eggtype":
                    if (!EggTypeChanger.needsSpawnEggMeta()) {
                        FunnyGuilds.getPluginLogger().info("This MC version supports metadata for spawnGuildHeart egg type, " +
                                "no need to use eggtype in item creation!");
                        continue;
                    }

                    Option<EntityType> entityType = Option.attempt(IllegalArgumentException.class, () -> {
                        return EntityType.valueOf(attributeValue.toUpperCase(Locale.ROOT));
                    }).onEmpty(() -> {
                        FunnyGuilds.getPluginLogger().parser("Unknown entity type: " + attributeValue);
                    });

                    if (entityType.isPresent()) {
                        EggTypeChanger.applyChanges(item.getMeta(), entityType.get());
                        item.refreshMeta();
                    }
            }
        }

        return item.getItem();
    }

    public static List<ItemStack> parseItems(List<String> itemStrings) {
        return PandaStream.of(itemStrings).map(ItemUtils::parseItem).toList();
    }

    public static List<ItemStack> parseItems(String... itemStrings) {
        return parseItems(Arrays.asList(itemStrings));
    }

    public static String toString(ItemStack item) {
        String material = item.getType().toString().toLowerCase(Locale.ROOT);
        short durability = item.getDurability();
        int amount = item.getAmount();

        StringBuilder itemString = new StringBuilder(amount + " " + material + (durability > 0 ? ":" + durability : ""));
        FunnyFormatter formatter = new FunnyFormatter().register(" ", "_").register("#", "{HASH}");

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return itemString.toString();
        }

        if (meta.hasDisplayName()) {
            itemString.append(" name:").append(formatter.format(ChatUtils.decolor(meta.getDisplayName())));
        }

        if (meta.hasLore()) {
            List<String> lore = PandaStream.of(meta.getLore())
                    .map(ChatUtils::decolor)
                    .map(formatter::format)
                    .toList();

            itemString.append(" lore:").append(Joiner.on("#").join(lore));
        }

        if (meta.hasEnchants()) {
            List<String> enchants = PandaStream.of(meta.getEnchants().entrySet().stream())
                    .map(entry -> getEnchantName(entry.getKey()).toLowerCase(Locale.ROOT) + ":" + entry.getValue())
                    .toList();

            itemString.append(" enchants:").append(Joiner.on(",").join(enchants));
        }

        if (!meta.getItemFlags().isEmpty()) {
            List<String> flags = PandaStream.of(meta.getItemFlags())
                    .map(ItemFlag::name)
                    .map(name -> name.toLowerCase(Locale.ROOT))
                    .toList();

            itemString.append(" flags:").append(Joiner.on(",").join(flags));
        }

        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            if (skullMeta.hasOwner()) {
                itemString.append(" skullowner:").append(skullMeta.getOwner());
            }
        }

        if (meta instanceof LeatherArmorMeta) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;
            Color color = armorMeta.getColor();

            String colorString = color.getRed() + "_" + color.getGreen() + "_" + color.getBlue();
            itemString.append(" armorcolor:").append(colorString);
        }

        if (EggTypeChanger.needsSpawnEggMeta()) {
            if (meta instanceof SpawnEggMeta) {
                SpawnEggMeta eggMeta = (SpawnEggMeta) meta;
                String entityType = eggMeta.getSpawnedType().name().toLowerCase(Locale.ROOT);
                itemString.append(" eggtype:").append(entityType);
            }
        }

        return itemString.toString();
    }

    private static Enchantment matchEnchant(String enchantName) {
        if (BY_IN_GAME_NAME_ENCHANT != null && CREATE_NAMESPACED_KEY != null) {
            try {
                Object namespacedKey = CREATE_NAMESPACED_KEY.invoke(null, enchantName.toLowerCase(Locale.ROOT));
                Object enchantment = BY_IN_GAME_NAME_ENCHANT.invoke(null, namespacedKey);

                if (enchantment != null) {
                    return (Enchantment) enchantment;
                }
            }
            catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }

        return Enchantment.getByName(enchantName.toUpperCase(Locale.ROOT));
    }

    private static String getEnchantName(Enchantment enchantment) {
        if (GET_IN_GAME_NAME_ENCHANT != null && GET_NAMESPACED_KEY != null) {
            try {
                Object enchantmentName = GET_IN_GAME_NAME_ENCHANT.invoke(enchantment);
                Object namespacedKey = GET_NAMESPACED_KEY.invoke(enchantmentName);

                if (namespacedKey != null) {
                    return (String) namespacedKey;
                }
            }
            catch (InvocationTargetException | IllegalAccessException ignored) {
            }
        }

        return enchantment.getName();
    }

    private static Pair<Enchantment, Integer> parseEnchant(String enchantString) {
        String[] split = enchantString.split(":");

        Enchantment enchant = matchEnchant(split[0]);
        if (enchant == null) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant: " + split[0]);
        }

        Option<Integer> level = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(split[1]));
        if (level.isEmpty()) {
            FunnyGuilds.getPluginLogger().parser("Unknown enchant level: " + split[1]);
        }

        return Pair.of(enchant, level.orElseGet(1));
    }

    private static Option<ItemFlag> matchItemFlag(String flagName) {
        return Option.attempt(IllegalArgumentException.class, () -> {
            return ItemFlag.valueOf(flagName.toUpperCase(Locale.ROOT));
        }).onEmpty(() -> {
            FunnyGuilds.getPluginLogger().parser("Unknown item flag: " + flagName);
        });
    }

    public static int getItemAmount(ItemStack item, Inventory inv) {
        return PandaStream.of(inv.getContents())
                .filter(item::isSimilar)
                .toStream()
                .mapToInt(ItemStack::getAmount)
                .sum();
    }

    public static ItemStack[] toArray(Collection<ItemStack> collection) {
        return collection.toArray(new ItemStack[0]);
    }

}
