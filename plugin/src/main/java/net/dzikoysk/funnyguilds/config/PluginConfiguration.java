package net.dzikoysk.funnyguilds.config;

import com.google.common.collect.ImmutableMap;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Exclude;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.configs.serdes.commons.duration.DurationSpec;
import eu.okaeri.validator.annotation.DecimalMax;
import eu.okaeri.validator.annotation.DecimalMin;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.NotBlank;
import eu.okaeri.validator.annotation.Pattern;
import eu.okaeri.validator.annotation.Positive;
import eu.okaeri.validator.annotation.PositiveOrZero;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.sections.CommandsConfiguration;
import net.dzikoysk.funnyguilds.config.sections.HeartConfiguration;
import net.dzikoysk.funnyguilds.config.sections.MysqlConfiguration;
import net.dzikoysk.funnyguilds.config.sections.TntProtectionConfiguration;
import net.dzikoysk.funnyguilds.config.sections.TopConfiguration;
import net.dzikoysk.funnyguilds.feature.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.feature.notification.bossbar.provider.BossBarOptions;
import net.dzikoysk.funnyguilds.nms.Reflections;
import net.dzikoysk.funnyguilds.rank.RankSystem;
import net.dzikoysk.funnyguilds.shared.Cooldown;
import net.dzikoysk.funnyguilds.shared.LegacyUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.EntityUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MaterialUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("                                #")
@Header("          FunnyGuilds           #")
@Header("         4.10.3 Snowdrop        #")
@Header("                                #")
@Header("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@Header("FunnyGuilds wspiera PlaceholderAPI, lista dodawanych placeholderów znajduje się tutaj:")
@Header("https://github.com/FunnyGuilds/FunnyGuilds/wiki/%5BPL%5D-PlaceholderAPI")
@Header(" ")
@Header("Jeżeli chcesz, aby dana wiadomość była pusta, zamiast wiadomości umieść: ''")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class PluginConfiguration extends OkaeriConfig {

    @Exclude
    public final Cooldown<UUID> informationMessageCooldowns = new Cooldown<>();

    @Comment("")
    @Comment("Wyświetlana nazwa pluginu")
    public String pluginName = "FunnyGuilds";

    @Comment("")
    @Comment("Czy plugin ma działać w trybie debug - służy on do wysyłania dodatkowych wiadomości, w celu diagnozowania błędów")
    public boolean debugMode = false;

    @Comment("")
    @Comment("Czy informacje o aktualizacji mają być widoczne podczas wejścia na serwer")
    public boolean updateInfo = true;

    @Comment("")
    @Comment("Czy informacje o aktualizacji wersji nightly mają być widoczne podczas wejścia na serwer")
    @Comment("Ta opcja działa tylko wtedy, gdy włączona jest opcja 'update-info'")
    public boolean updateNightlyInfo = true;

    @Comment("")
    @Comment("Czy ma być włączona możliwość zakładania gildii (można ją zmienić także za pomocą komendy /ga enabled)")
    public boolean guildsEnabled = true;

    @Comment("")
    @Comment("Czy tworzenie regionów gildii, oraz inne związane z nimi rzeczy, mają byc włączone")
    @Comment("UWAGA - dobrze przemyśl decyzję o wyłączeniu regionów!")
    @Comment("Gildie nie będą miały w sobie żadnych informacji o regionach, a jeśli regiony są włączone - te informacje muszą byc obecne")
    @Comment("Jeśli regiony miałyby być znowu włączone - będzie trzeba wykasować WSZYSTKIE dane pluginu")
    @Comment("Wyłączenie tej opcji nie powinno spowodować żadnych błędów, jeśli już są utworzone regiony gildii")
    public boolean regionsEnabled = true;

    @Comment("")
    @Comment("Bloki, które można stawiać na terenie gildii, niezależnie od tego, czy jest się jej członkiem")
    @Comment("Zostaw puste, aby wyłączyć")
    @Comment("Nazwy bloków muszą pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/Material.html")
    public Set<Material> placingBlocksBypassOnRegion = Collections.emptySet();

    @Comment("")
    @Comment("Zablokuj rozlewanie się wody i lawy poza terenem gildii")
    @Comment("Działa tylko jeśli regiony są włączone")
    @CustomKey("water-and-lava-flow-only-for-regions")
    public boolean blockFlow = false;

    @Comment("")
    @Comment("Czy gracz po śmierci ma się pojawiać w bazie swojej gildii")
    @Comment("Działa tylko jeśli regiony są włączone")
    public boolean respawnInBase = true;

    @Min(1)
    @Comment("")
    @Comment("Maksymalna długość nazwy gildii")
    @CustomKey("name-length")
    public int createNameLength = 22;

    @Min(1)
    @Comment("")
    @Comment("Minimalna długość nazwy gildii")
    @CustomKey("name-min-length")
    public int createNameMinLength = 4;

    @Min(1)
    @Comment("")
    @Comment("Maksymalna długość tagu gildii")
    @CustomKey("tag-length")
    public int createTagLength = 4;

    @Min(1)
    @Comment("")
    @Comment("Minimalna długość tagu gildii")
    @CustomKey("tag-min-length")
    public int createTagMinLength = 2;

    @Comment("")
    @Comment("Zasada sprawdzania nazwy gildii przy jej tworzeniu")
    @Comment("Dostepne zasady:")
    @Comment("LOWERCASE - umożliwia użycie tylko małych liter")
    @Comment("UPPERCASE - umożliwia użycie tylko wielkich liter")
    @Comment("DIGITS - umożliwia użycie tylko cyfr")
    @Comment("LOWERCASE_DIGITS - umożliwia użycie małych liter i cyfr")
    @Comment("UPPERCASE_DIGITS - umożliwia użycie wielkich liter i cyfr")
    @Comment("LETTERS - umożliwia użycie małych i wielkich liter")
    @Comment("LETTERS_DIGITS - umożliwia użycie małych i wielkich liter oraz cyfr")
    @Comment("LETTERS_DIGITS_UNDERSCORE - umożliwia użycie małych i wielkich liter, cyfr oraz podkreślnika")
    @Comment(" ")
    @Comment("Dodatkowo można stworzyć własną zasadę regexa - pomocna może okazać sięprzy tym strona https://regex101.com/")
    public FunnyPattern nameRegex = new FunnyPattern(DefaultRegex.LETTERS);

    @Comment("")
    @Comment("Zasada sprawdzania tagu gildii przy jej tworzeniu")
    @Comment("Możliwe zasady są takie same jak w przypadku name-regex")
    public FunnyPattern tagRegex = new FunnyPattern(DefaultRegex.LETTERS);

    @Comment("")
    @Comment("Zasada sprawdzania nicków graczy")
    @Comment("Możliwe zasady są takie same jak w przypadku name-regex")
    public FunnyPattern playerNameRegex = new FunnyPattern(DefaultRegex.LETTERS_DIGITS_UNDERSCORE);

    @Comment("")
    @Comment("Minimalna długość nicku gracza")
    public int playerNameMinLength = 3;

    @Comment("")
    @Comment("Maksymalna długość nicku gracza")
    public int playerNameMaxLength = 16;

    @Min(0)
    @Comment("")
    @Comment("Minimalna liczba graczy w gildii, aby zaliczała się ona do rankingu")
    @CustomKey("guild-min-members")
    public int minMembersToInclude = 1;

    @Comment("")
    @Comment("Czy wiadomości o braku potrzebnych przedmiotów maja zawierać elementy, na które można najechać")
    @Comment("Takie elementy pokazują informacje o przedmiocie, np. jego typ, nazwę czy opis")
    @Comment("Funkcja jest obecnie trochę niedopracowana i może powodować problemy na niektórych wersjach MC, np. 1.8.8")
    public boolean enableItemComponent = false;

    @Comment("")
    @Comment("Przedmioty wymagane do założenia gildii")
    @Comment("Tylko wartości ujęte w <> są wymagane - reszta, ujeta w [], jest opcjonalna")
    @Comment("Wzór: <ilosc> <przedmiot>:[metadata] [name:lore:enchants:eggtype:skullowner:armorcolor:flags]")
    @Comment("Przykład: \"5 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!\"")
    @Comment(" ")
    @Comment("Zamiast spacji wstawiaj podkreślnik: _")
    @Comment("Aby zrobić nową linię lore wstaw hash: #")
    @Comment("Aby w lore użyć znaku # wstaw {HASH}")
    @Comment(" ")
    @Comment("eggtype to typ jajka do spawnu moba, używane tylko gdy typem przedmiotu jest MONSTER_EGG")
    @Comment("skullowner to nick gracza, którego głowa jest tworzona, używane tylko gdy typem przedmiotu jest SKULL_ITEM")
    @Comment("armorcolor to kolor, w którym będzie przedmiot, używane tylko gdy przedmiot jest częścią zbroi skórzanej")
    @Comment("flags to flagi, które maja byc nałożone na przedmiot. Dostepne flagi: HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS, HIDE_PLACED_ON, HIDE_POTION_EFFECTS")
    @Comment("Kolor musi byc podany w postaci: \"R_G_B\"")
    @Comment(" ")
    @Comment("UWAGA: Nazwy przedmiotów musza pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/Material.html")
    @Comment("UWAGA: Typ jajka musi pasować do typów entity podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/entity/EntityType.html")
    @CustomKey("items")
    public List<ItemStack> createItems = ItemUtils.parseItems("5 stone", "5 dirt", "5 tnt");

    @Min(0)
    @Comment("")
    @Comment("Ilość doświadczenia wymagana do założenia gildii")
    public int requiredExperience = 0;

    @Min(0)
    @Comment("")
    @Comment("Ilość pieniędzy wymagana do założenia gildii")
    @Comment("UWAGA: Aby ta opcja mogła działać - na serwerze musi być plugin Vault oraz plugin dodający ekonomię")
    public double requiredMoney = 0;

    @Comment("")
    @Comment("Przedmioty wymagane do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.items")
    @CustomKey("items-vip")
    public List<ItemStack> createItemsVip = ItemUtils.parseItems("1 gold_ingot");

    @Min(0)
    @Comment("")
    @Comment("Ilość doświadczenia wymagana do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.items")
    public int requiredExperienceVip = 0;

    @Min(0)
    @Comment("")
    @Comment("Ilość pieniędzy wymagana do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.items")
    @Comment("UWAGA: Aby ta opcja mogła działać - na serwerze musi być plugin Vault oraz plugin dodający ekonomię")
    public double requiredMoneyVip = 0;

    @Comment("")
    @Comment("Czy opcja wymaganego rankingu do założenia gildii ma byc włączona")
    public boolean rankCreateEnable = true;

    @Comment("")
    @Comment("Minimalny ranking wymagany do założenia gildii")
    public int rankCreate = 1000;

    @Comment("")
    @Comment("Minimalny ranking wymagany do założenia gildii, dla osoby z uprawnieniem funnyguilds.vip.rank")
    public int rankCreateVip = 800;

    @Comment("")
    @Comment("Czy GUI z przedmiotami na gildię ma być wspólne dla wszystkich")
    @Comment("Jeśli włączone - wszyscy gracze będą widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip będzie ignorowane")
    public boolean useCommonGUI = false;

    @Comment("")
    @Comment("GUI z przedmiotami na gildię, dla osób bez uprawnienia funnyguilds.vip.items")
    @Comment("Jeśli włączone jest use-common-gui - poniższe GUI jest używane także dla osób z uprawnieniem funnyguilds.vip.items")
    @Comment("Każda linijka listy oznacza jeden slot, liczba slotów powinna byc wielokrotnością liczby 9 i nie powinna byc większa niz 54")
    @Comment("Aby użyć przedmiotu, stworzonego w jednym slocie, w innym - można użyć {GUI-nr}, np. {GUI-1} wstawi ten sam przedmiot, który jest w pierwszym slocie")
    @Comment("Aby wstawić przedmiot na gildię należy użyć {ITEM-nr}, np. {ITEM-1} wstawi pierwszy przedmiot na gildię")
    @Comment("Aby wstawić przedmiot na gildię z listy vip należy użyć {VIPITEM-nr}")
    @CustomKey("gui-items")
    public List<String> guiItems_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{ITEM-1}", "{ITEM-2}", "{ITEM-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @Exclude
    public List<ItemStack> guiItems;

    @Comment("")
    @Comment("Nazwa GUI z przedmiotami na gildię, dla osób bez uprawnienia funnyguilds.vip.items")
    @Comment("Nazwa może zawierać max. 32 znaki, wliczając w to kody kolorów")
    public RawString guiItemsTitle = new RawString("&5&lPrzedmioty na gildie");

    @Comment("")
    @Comment("GUI z przedmiotami na gildię, dla osób z uprawnieniem funnyguilds.vip.items")
    @Comment("Zasada tworzenia GUI jest taka sama jak w przypadku sekcji gui-items")
    @Comment("Poniższe GUI będzie ignorowane, jeśli wlaczone jest use-common-gui")
    @CustomKey("gui-items-vip")
    public List<String> guiItemsVip_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{GUI-1}", "{VIPITEM-1}", "{GUI-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @Exclude
    public List<ItemStack> guiItemsVip;

    @Comment("")
    @Comment("Nazwa GUI z przedmiotami na gildię, dla osób z uprawnieniem funnyguilds.vip.items")
    @Comment("Nazwa może zawierać max. 32 znaki, wliczając w to kody kolorów")
    public RawString guiItemsVipTitle = new RawString("&5&lPrzedmioty na gildie (VIP)");

    @Comment("")
    @Comment("Zmiana nazwy i koloru przedmiotów na gildię (nie ma znaczenia uprawnienie funnyguilds.vip.items)")
    @Comment("Jeśli nie chcesz używac tej funkcji - to pozostaw gui-items-name: \"\"")
    @Comment("{ITEM} - nazwa przedmiotu (np. 1 golden_apple)")
    @Comment("{ITEM-NO-AMOUNT} - nazwa przedmiotu bez liczby (np. golden_apple)")
    public RawString guiItemsName = new RawString("&7>> &a{ITEM-NO-AMOUNT} &7<<");

    @Comment("")
    @Comment("Czy do przedmiotów na gildię, które są w GUI, mają być dodawane dodatkowe linie opisu")
    @Comment("Linie te można ustawić poniżej")
    public boolean addLoreLines = true;

    @Comment("")
    @Comment("Dodatkowe linie opisu, dodawane do każdego przedmiotu, który jest jednocześnie przedmiotem na gildię")
    @Comment("Dodawane linie nie zależą od otwieranego GUI - są wspólne dla zwykłego i VIP")
    @Comment("Możliwe do użycia zmienne:")
    @Comment("{REQ-AMOUNT} - całkowita wymagana liczba przedmiotów")
    @Comment("{PINV-AMOUNT} - liczba danego przedmiotu, jaką gracz ma przy sobie")
    @Comment("{PINV-PERCENT} - procent wymaganej liczby danego przedmiotu, jaki gracz ma przy sobie")
    @Comment("{EC-AMOUNT} - liczba danego przedmiotu, jaką gracz ma w enderchescie")
    @Comment("{EC-PERCENT} - procent wymaganej liczby danego przedmiotu, jaki gracz ma w enderchescie")
    @Comment("{ALL-AMOUNT} - liczba danego przedmiotu, jaką gracz ma przy sobie i w enderchescie")
    @Comment("{ALL-PERCENT} - procent wymaganej liczby danego przedmiotu, jaki gracz ma przy sobie i w enderchescie")
    public List<RawString> guiItemsLore = RawString.listOf("", "&aPosiadasz juz:", "&a{PINV-AMOUNT} przy sobie &7({PINV-PERCENT}%)",
            "&a{EC-AMOUNT} w enderchescie &7({EC-PERCENT}%)", "&a{ALL-AMOUNT} calkowicie &7({ALL-PERCENT}%)");

    @Comment("")
    @Comment("Minimalna odległość od spawnu")
    public int createDistance = 100;

    @Comment("")
    @Comment("Minimalna odległość regionu gildii od granicy mapy")
    @CustomKey("create-guild-min-distance")
    public double createMinDistanceFromBorder = 50;

    @Comment("")
    @Comment("Konfiguracja serca gildii")
    @CustomKey("heart-configuration")
    public HeartConfiguration heart = new HeartConfiguration();

    @Comment("")
    @Comment("Typy bloków, z którymi osoba spoza gildii NIE może prowadzić interakcji na terenie innej gildii")
    public List<Material> blockedInteract = Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST);

    @Comment("")
    @Comment("Czy funkcja efektu 'zbugowanych' klocków ma byc włączona (działa tylko na terenie wrogiej gildii)")
    public boolean buggedBlocks = false;

    @Min(0)
    @Comment("")
    @Comment("Czas po którym 'zbugowane' klocki maja zostać usunięte")
    @Comment("Czas podawany w tickach (20 ticków = 1 sekunda)")
    public long buggedBlocksTimer = 20L;

    @Comment("")
    @Comment("Bloki, których nie można 'bugować'")
    @Comment("Nazwy bloków muszą pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/Material.html")
    public Set<Material> buggedBlocksExclude = MaterialUtils.parseMaterials(false,
            // Ban basic
            "TNT", "STATIONARY_LAVA", "STATIONARY_WATER",
            // Ban TNT Minecart placement
            "RAILS", "DETECTOR_RAIL", "ACTIVATOR_RAIL", "POWERED_RAIL",
            // Ban gravity blocks that won't be removed when fallen
            "ANVIL", "GRAVEL", "SAND", "DRAGON_EGG",
            // Ban pistons and other components that may produce redstone output or interact with it
            "PISTON_BASE", "PISTON_STICKY_BASE",
            "REDSTONE_BLOCK", "REDSTONE_TORCH_ON", "REDSTONE_TORCH_OFF", "DIODE", "REDSTONE_COMPARATOR", "DAYLIGHT_DETECTOR",
            "DISPENSER", "HOPPER", "DROPPER", "OBSERVER",
            "STONE_PLATE", "WOOD_PLATE", "GOLD_PLATE", "IRON_PLATE", "LEVER", "TRIPWIRE_HOOK", "TRAP_DOOR", "IRON_TRAPDOOR", "WOOD_BUTTON", "STONE_BUTTON",
            "WOOD_DOOR", "IRON_DOOR", "SPRUCE_DOOR_ITEM", "BIRCH_DOOR_ITEM", "JUNGLE_DOOR_ITEM", "ACACIA_DOOR_ITEM", "DARK_OAK_DOOR_ITEM",
            "FENCE_GATE", "SPRUCE_FENCE_GATE", "JUNGLE_FENCE_GATE", "DARK_OAK_FENCE_GATE", "BIRCH_FENCE_GATE",
            "REDSTONE_LAMP_ON", "REDSTONE_LAMP_OFF",
            "TRAPPED_CHEST", "CHEST"
    );

    @Comment("")
    @Comment("Czy klocki po 'zbugowaniu' mają zostać oddane")
    public boolean buggedBlocksReturn = false;

    @Min(1)
    @Comment("")
    @Comment("Maksymalna liczba członków w gildii")
    @CustomKey("max-members")
    public int maxMembersInGuild = 15;

    @Min(0)
    @Comment("")
    @Comment("Maksymalna liczba sojuszy między gildiami")
    @CustomKey("max-allies")
    public int maxAlliesBetweenGuilds = 15;

    @Min(0)
    @Comment("")
    @Comment("Maksymalna liczba wojen między gildiami")
    @CustomKey("max-enemies")
    public int maxEnemiesBetweenGuilds = 15;

    @Comment("")
    @Comment("Lista nazw światów, na których możliwość utworzenia gildii ma być zablokowana")
    public List<String> blockedWorlds = Collections.singletonList("some_world");

    @Comment("")
    @Comment("Możliwość ucieczki z terenu innej gildii")
    @Comment("Funkcja niedostępna, jeśli możliwość teleportacji do gildii jest wyłączona")
    public boolean escapeEnable = true;

    @Min(0)
    @Comment("")
    @Comment("Czas w sekundach, jaki musi upłynąć od włączenia ucieczki do teleportacji")
    public int escapeDelay = 120;

    @Comment("")
    @Comment("Możliwość ucieczki na spawn dla graczy bez gildii")
    public boolean escapeSpawn = true;

    @Comment("")
    @Comment("Możliwość teleportacji do gildii")
    public boolean baseEnable = true;

    @PositiveOrZero
    @Comment("")
    @Comment("Czas oczekiwania na teleportację, w sekundach")
    public Duration baseDelay = Duration.ofSeconds(5);

    @PositiveOrZero
    @Comment("")
    @Comment("Czas oczekiwania na teleportację, w sekundach, dla graczy posiadających uprawnienie funnyguilds.vip.baseTeleportTime")
    public Duration baseDelayVip = Duration.ofSeconds(3);

    @Comment("")
    @Comment("Koszt teleportacji do gildii, jeżeli teleportacja ma byc darmowa - wystarczy wpisac: base-items: []")
    public List<ItemStack> baseItems = ItemUtils.parseItems("1 diamond", "1 emerald");

    @Comment("")
    @Comment("Koszt dołączenia do gildii, jeżeli dołączenie ma byc darmowe - wystarczy wpisac: join-items: []")
    public List<ItemStack> joinItems = ItemUtils.parseItems("1 diamond");

    @Comment("")
    @Comment("Możliwość powiększania gildii")
    public boolean enlargeEnable = true;

    @Comment("")
    @Comment("O ile powiększany jest teren gildii przy zwiększeniu poziomu")
    public int enlargeSize = 5;

    @Comment("")
    @Comment("Koszt powiększania gildii")
    @Comment("Każdy myślnik to 1 poziom powiększenia")
    public List<ItemStack> enlargeItems = ItemUtils.parseItems("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond");

    @Min(1)
    @Comment("")
    @Comment("Wielkość regionu gildii")
    public int regionSize = 50;

    @Min(0)
    @Comment("")
    @Comment("Minimalna odległość między terenami gildii")
    public int regionMinDistance = 10;

    @Positive
    @Comment("")
    @Comment("Czas wyświetlania powiadomienia na pasku powiadomień, w sekundach")
    public Duration regionNotificationTime = Duration.ofSeconds(15);

    @Min(1)
    @Comment("")
    @Comment("Co ile może byc wywoływany pasek powiadomień przez jednego gracza, w sekundach")
    public int regionNotificationCooldown = 60;

    @Comment("")
    @Comment("Komendy zablokowane na terenie gildii, dla graczy niebędących członkami")
    @CustomKey("region-commands")
    public List<String> regionCommands = Collections.singletonList("sethome");

    @Comment("")
    public BlockTeleportOnRegion blockTeleportOnRegion = new BlockTeleportOnRegion();

    public static class BlockTeleportOnRegion extends OkaeriConfig {

        @Comment("Czy ma być blokowana teleportacja na teren neutralnej gildii")
        public boolean neutral = true;

        @Comment("")
        @Comment("Czy ma być blokowana teleportacja na teren wrogiej gildii")
        public boolean enemy = true;

        @Comment("")
        @Comment("Czy ma być blokowana teleportacja na teren sojuszniczej gildii")
        public boolean ally = false;

    }

    @Exclude
    public boolean eventTeleport = false;

    @Comment("")
    @Comment("Czy proces usunięcia gildii powinien zostać przerwany, jezeli ktoś spoza gildii jest na jej terenie")
    public boolean guildDeleteCancelIfSomeoneIsOnRegion = false;

    @Comment("")
    public TntProtectionConfiguration tntProtection = new TntProtectionConfiguration();

    @Min(0)
    @Comment("")
    @Comment("Przez ile sekund po wybuchu nie można budować na terenie gildii")
    public int regionExplode = 120;

    @Comment("")
    @Comment("Czy blokada budowania przy wybuchu powinna działać jeśli gildia jest chroniona")
    public boolean regionExplodeBlockProtected = false;

    @Comment("Czy blokada budowania przy wybuchu powinna działać jeśli TNT jest wyłączone")
    public boolean regionExplodeBlockTntDisabled = false;

    @Comment("")
    @Comment("Lista entity, których wybuch nie powoduje blokady budowania na terenie gildii")
    @Comment("Nazwy entity muszą pasować do nazw podanych tutaj: https://spigotdocs.okaeri.cloud/select/org/bukkit/entity/EntityType.html (nie wszystkie entity wybuchają)")
    public Set<EntityType> regionExplodeExcludeEntities = EntityUtils.parseEntityTypes(true,  "CREEPER", "WITHER", "WITHER_SKULL", "FIREBALL");

    @Comment("")
    @Comment("Czy blokada po wybuchu ma obejmować rownież niszczenie bloków")
    public boolean regionExplodeBlockBreaking = false;

    @Comment("")
    @Comment("Czy blokada po wybuchu ma obejmować rownież interakcje z blocked-interact")
    public boolean regionExplodeBlockInteractions = false;

    @Min(0)
    @Comment("")
    @Comment("Zasięg pobieranych przedmiotów po wybuchu, jeżeli chcesz wyłączyć - wpisz 0")
    public int explodeRadius = 3;

    @Comment("")
    @Comment("Jakie materiały, i z jaka szansą, maja byc niszczone po wybuchu")
    @Comment("<material>: <szansa (w %)>")
    @Comment("Jeżeli wszystkie materiały mają mieć określony % na wybuch - uzyj specjalnego znaku '*'")
    @CustomKey("explode-materials")
    public Map<String, Double> explodeMaterials_ = ImmutableMap.of(
            "ender_chest", 20.0,
            "enchantment_table", 20.0,
            "obsidian", 20.0,
            "water", 33.0,
            "lava", 33.0
    );

    @Exclude
    public Map<Material, Double> explodeMaterials;
    @Exclude
    public boolean allMaterialsAreExplosive;
    @Exclude
    public double defaultExplodeChance = -1.0;

    @Comment("")
    @Comment("Czy powstałe wybuchy powinny niszczyć bloki wyłącznie na terenach gildii")
    public boolean explodeShouldAffectOnlyGuild = false;

    @Comment("")
    @Comment("Możliwość podbijania gildii")
    public boolean warEnabled = true;

    @Min(1)
    @Comment("")
    @Comment("Ile żyć ma gildia")
    public int warLives = 3;

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Po jakim czasie od założenia można zaatakować gildię")
    @CustomKey("war-protection")
    public Duration warProtection = Duration.ofHours(24);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.HOURS)
    @Comment("")
    @Comment("Ile czasu trzeba czekać do następnego ataku na gildię")
    @CustomKey("war-wait")
    public Duration warWait = Duration.ofHours(24);

    @Comment("")
    @Comment("Czy gildia podczas okresu ochronnego ma posiadać ochronę przeciw TNT")
    public boolean warTntProtection = true;

    @Comment("")
    @Comment("Czy zwierzęta na terenie gildii mają być chronione przed osobami spoza gildii")
    public boolean animalsProtection = false;

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Jaką ważność ma gildia po jej założeniu")
    @CustomKey("validity-start")
    public Duration validityStart = Duration.ofDays(14);

    @Positive
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Ile czasu dodaje przedłużenie ważności gildii")
    @CustomKey("validity-time")
    public Duration validityTime = Duration.ofDays(14);

    @PositiveOrZero
    @DurationSpec(fallbackUnit = ChronoUnit.DAYS)
    @Comment("")
    @Comment("Ile dni przed końcem wygasania można przedłużyć gildię, wpisz 0 jeżeli funkcja ma byc wylaczona")
    @CustomKey("validity-when")
    public Duration validityWhen = Duration.ofDays(14);

    @Comment("")
    @Comment("Koszt przedłużenia gildii")
    public List<ItemStack> validityItems = ItemUtils.parseItems("10 diamond");

    @Comment("")
    @Comment("Czy wiadomość o zabiciu gracza powinna być pokazywana wszystkim")
    @Comment("Jeśli wyłączone - będzie pokazywana tylko graczom, którzy brali udział w walce")
    public boolean broadcastDeathMessage = true;

    @Comment("")
    @Comment("Czy wiadomość o zabiciu gracza powinna być wyświetlana bez względu na wyłączone wiadomości o śmierci")
    @CustomKey("ignore-death-messages-disabled")
    public boolean ignoreDisabledDeathMessages = false;

    @Comment("")
    @Comment("Ranking, od którego rozpoczyna gracz")
    public int rankStart = 1000;

    @Comment("")
    @Comment("Czy blokada nabijania rankingu na tych samych osobach powinna byc włączona")
    public boolean rankFarmingProtect = true;

    @Comment("")
    @Comment("Czy opcja blokady nabijania rankingu powinna działać w obie strony tzn. jeśli gracz nas zabije, a potem zabijemy go my to nie dostaniemy punktów")
    public boolean bidirectionalRankFarmingProtect = false;

    @Comment("")
    @Comment("Czy ostatnia osoba, która zaatakowała gracza, który zginął, ma być uznawana za jego zabójcę")
    @CustomKey("rank-farming-last-attacker-as-killer")
    public boolean considerLastAttackerAsKiller = false;

    public DamageTracking damageTracking = new DamageTracking();

    public static class DamageTracking extends OkaeriConfig {

        @Comment("Czas po którym zadane obrażenia, stają się \"przestarzałe\"")
        public Duration expireTime = Duration.ofMinutes(1);

        @Min(-1)
        @Comment("Jak długa ma być historia zadanych obrażeń.")
        @Comment("Wstaw -1 jeśli ma być nieskończona.")
        public int maxTracks = 30;

    }

    @PositiveOrZero
    @Comment("")
    @Comment("Czas przez jaki osoba, która zaatakowała gracza, który zginął, ma być uznawany za jego zabójcę")
    @Comment("Format: <wartość><jednostka><wartość><jednostka><...>")
    @Comment("Jednostki: ns - nanosekundy, ms - milisekundy, s - sekundy, m - minuty, h - godziny, d - dni")
    @Comment("Przykład: 1m30s")
    @CustomKey("rank-farming-consideration-timeout")
    public Duration lastAttackerAsKillerConsiderationTimeout = Duration.ofSeconds(30);

    @PositiveOrZero
    @Comment("")
    @Comment("Czas trwania blokady nabijania rankingu po walce dwóch osób")
    public Duration rankFarmingCooldown = Duration.ofHours(2);

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki mają taki sam adres IP")
    public boolean rankIPProtect = false;

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki są członkami tej samej gildii")
    public boolean rankMemberProtect = false;

    @Comment("")
    @Comment("Czy ma być zablokowana zmiana rankingu, jeśli obie osoby z walki są z sojuszniczych gildii")
    public boolean rankAllyProtect = false;

    @Comment("")
    @Comment("Czy gracze z uprawnieniem 'funnyguilds.ranking.exempt' powinni byc uwzględnieni przy wyznaczaniu pozycji gracza w rankingu")
    @CustomKey("skip-privileged-players-in-rank-positions")
    public boolean skipPrivilegedPlayersInRankPositions = false;

    @Min(1)
    @Comment("")
    @Comment("Co ile ticków ranking graczy oraz gildii powinien być odświeżany (20 ticków = 1 sekunda)")
    public int rankingUpdateInterval = 40;

    @Comment("")
    @Comment("Czy system asyst ma byc włączony")
    @CustomKey("rank-assist-enable")
    public boolean assistEnable = true;

    @Min(-1)
    @Comment("")
    @Comment("Limit asyst, wpisz liczbę ujemną aby wyłączyć")
    @CustomKey("assists-limit")
    public int assistsLimit = -1;

    @DecimalMin("0")
    @DecimalMax("1")
    @Comment("")
    @Comment("Jaka część rankingu za zabicie idzie na konto zabójcy")
    @Comment("1 to cały ranking, 0 to nic")
    @Comment("Reszta rankingu rozdzielana jest między osoby asystujące, w zaleznosci od zadanych obrażeń")
    @CustomKey("rank-assist-killer-share")
    public double assistKillerShare = 0.5;

    @Comment("")
    @Comment("Czy zabójcy zawsze mają dzielić sie ilością punktów według rank-assist-killer-share, nawet gdy nie ma osób asystujących")
    @CustomKey("rank-assist-victim-always-share")
    public boolean assistKillerAlwaysShare = false;

    @Comment("")
    @Comment("Na jakich regionach ma być ignorowane nadawanie asyst")
    @Comment("UWAGA: wymagany plugin WorldGuard")
    @CustomKey("assists-regions-ignored")
    public List<String> assistsRegionsIgnored = Collections.singletonList("spawnGuildHeart");

    @Comment("")
    @Comment("System rankingowy używany przez plugin, do wyboru:")
    @Comment(" ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @Comment(" PERCENT - system, który obu graczom zabiera procent rankingu osoby zabitej")
    @Comment(" STATIC - system, który zawsze zabiera x rankingu zabijającemu i x zabitemu")
    @CustomKey("rank-system")
    public RankSystem.Type rankSystem = RankSystem.Type.ELO;

    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Lista stałych do obliczeń rankingowych ELO - im mniejsza stała, tym mniejsze zmiany rankingu")
    @Comment("Stałe określają też o ile maksymalnie może zmienić się ranking pochodzący z danego przedziału")
    @Comment("Lista powinna być podana od najmniejszych do największych rankingów i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minRank-maxRank stała\", np.: \"0-1999 32\"")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w gore, np.: \"2401-* 16\"")
    @CustomKey("elo-constants")
    public List<String> eloConstants_ = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");

    @Exclude
    public Map<NumberRange, Integer> eloConstants;

    @Positive
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Dzielnik obliczeń rankingowych ELO - im mniejszy dzielnik, tym większe zmiany rankingu")
    @Comment("Dzielnik powinien być liczbą dodatnią, niezerową")
    @CustomKey("elo-divider")
    public double eloDivider = 400.0D;

    @Positive
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest ELO!")
    @Comment("Wykładnik potęgi obliczeń rankingowych ELO - im mniejszy wykładnik, tym wieksze zmiany rankingu")
    @Comment("Wykładnik powinien być liczbą dodatnią, niezerową")
    @CustomKey("elo-exponent")
    public double eloExponent = 10.0D;

    @DecimalMin("0")
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest PERCENT!")
    @Comment("Procent rankingu osoby zabitej, o jaki zmienią się rankingi po walce")
    @CustomKey("percent-rank-change")
    public double percentRankChange = 1.0;

    @Min(0)
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest STATIC!")
    @Comment("Punkty dawane osobie, która wygrała walkę")
    @CustomKey("static-attacker-change")
    public int staticAttackerChange = 15;

    @Min(0)
    @Comment("")
    @Comment("Sekcja używana TYLKO jeśli wybranym rank-system jest STATIC!")
    @Comment("Punkty zabierane osobie, która przegrała walkę")
    @CustomKey("static-victim-change")
    public int staticVictimChange = 10;

    @Comment("")
    @Comment("Czy pokazywać informacje przy kliknięciu PPM na gracza")
    @CustomKey("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @Comment("")
    @Comment("Czy pokazać informacje z komendy /gracz przy kliknięciu PPM")
    @Comment("Jeśli wyłączone - pokazywane będą informacje z sekcji \"playerRightClickInfo\" z messages.yml")
    @CustomKey("info-player-command")
    public boolean infoPlayerCommand = true;

    @Comment("")
    @Comment("Cooldown pomiędzy pokazywaniem informacji przez PPM")
    @CustomKey("info-player-cooldown")
    public Duration infoPlayerCooldown = Duration.ofSeconds(5);

    @Comment("")
    @Comment("Czy trzeba kucać, aby przy kliknięciu PPM na gracza wyświetliło informacje o nim")
    @CustomKey("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    @Comment("")
    @Comment("Czy członkowie gildii mogą sobie zadawać obrażenia (domyślnie)")
    @CustomKey("damage-guild")
    public boolean damageGuild = false;

    @Comment("")
    @Comment("Czy sojuszniczy mogą sobie zadawać obrażenia")
    @CustomKey("damage-ally")
    public boolean damageAlly = false;

    @Comment("")
    public LivesRepeatingSymbol livesRepeatingSymbol = new LivesRepeatingSymbol();

    public static class LivesRepeatingSymbol extends OkaeriConfig {

        @Comment("Symbol (lub słowo), który ma być powtarzany przy użyciu placeholdera LIVES-SYMBOL lub LIVES-SYMBOL-ALL")
        public RawString full = new RawString("&c\u2764");

        @Comment("")
        @Comment("Symbol (lub słowo), który ma być powtarzany przy użyciu placeholdera LIVES-SYMBOL")
        public RawString empty = new RawString("&8\u2764");

        @Comment("")
        @Comment("Symbol (lub słowo), który ma być pokazywany na końcu placeholdera LIVES-SYMBOL, kiedy gildia posiada więcej żyć niz podstawowe (war-lives)")
        public RawString more = new RawString("&a+");

    }

    @Comment("")
    @Comment("Wygląd znacznika {POS} wstawionego w format chatu")
    @Comment("Znacznik ten pokazuje czy ktoś jest liderem, zastępcą lub zwykłym członkiem gildii")
    public RawString chatPosition = new RawString("&b{POS} ");

    @Comment("")
    @Comment("Znacznik dla lidera gildii")
    @CustomKey("chat-position-leader")
    public RawString chatPositionLeader = new RawString("**");

    @Comment("")
    @Comment("Znacznik dla zastępcy gildii")
    @CustomKey("chat-position-deputy")
    public RawString chatPositionDeputy = new RawString("*");

    @Comment("")
    @Comment("Znacznik dla członka gildii")
    @CustomKey("chat-position-member")
    public RawString chatPositionMember = new RawString("");

    @Comment("")
    @Comment("Wygląd znacznika {TAG} wstawionego w format chatu")
    public RawString chatGuild = new RawString("&b{TAG} ");

    @Comment("")
    @Comment("Wygląd znacznika {RANK} wstawionego w format chatu")
    public RawString chatRank = new RawString("&b{RANK} ");

    @Comment("")
    @Comment("Wygląd znacznika {POINTS} wstawionego w format chatu")
    @Comment("Możesz tu także użyć znacznika {POINTS-FORMAT}")
    public RawString chatPoints = new RawString("&b{POINTS} ");

    @Comment("")
    public TopConfiguration top = new TopConfiguration();

    @Comment("")
    @Comment("Wygląd znacznika {POINTS-FORMAT} i {G-POINTS-FORMAT} w zależności od wartości punktów")
    @Comment("{G-POINTS-FORMAT}, tak samo jak {G-POINTS}, jest używane jedynie na liście graczy")
    @Comment("Lista powinna być podana od najmniejszych do największych rankingów i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minRank-maxRank wygląd\", np.: \"0-750 &4{POINTS}\"")
    @Comment("Pamiętaj, aby każdy możliwy ranking miał ustalony format!")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
    public List<RangeFormatting> pointsFormat = Arrays.asList(
            new RangeFormatting(0, 749, "&4{POINTS}"),
            new RangeFormatting(750, 999, "&c{POINTS}"),
            new RangeFormatting(1000, 1499, "&a{POINTS}"),
            new RangeFormatting(1500, Integer.MAX_VALUE, "&6&l{POINTS}")
    );

    @Comment("")
    @Comment("Znacznik z punktami dodawany do zmiennej {PTOP-x}")
    @Comment("Używaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @Comment("Jeśli nie chcesz wyświetlać punktów, tylko sam nick - nie podawaj tu nic")
    public RawString ptopPoints = new RawString(" &7[{POINTS}&7]");

    @Comment("")
    @Comment("Znacznik z punktami dodawany do zmiennej {GTOP-x}")
    @Comment("Używaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @Comment("Jeśli nie chcesz wyświetlać punktów, tylko sam tag - nie podawaj tu nic")
    public RawString gtopPoints = new RawString(" &7[&b{POINTS-FORMAT}&7]");

    @Comment("")
    @Comment("Wygląd znacznika {MINUS-FORMATTED} i {PLUS-FORMATTED}, w zależności od wartości zmiany w rankingu")
    @Comment("Lista powinna być podana od najmniejszych do największych wartości i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minChange-maxChange wygląd\", np.: \"0-50 &a+{CHANGE}\"")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minChange w górę, np.: \"50-* &2+{CHANGE}\"")
    @Comment("  lub wszystkie wartości do danego maxChange w dół, np.: \"*-50 &4-{CHANGE}\"")
    public List<RangeFormatting> killPointsChangeFormat = Arrays.asList(
            new RangeFormatting(Integer.MIN_VALUE, -1, "&c-{CHANGE}"),
            new RangeFormatting(0, 0, "&7{CHANGE}"),
            new RangeFormatting(1, Integer.MAX_VALUE, "&a+{CHANGE}")
    );

    @Comment("")
    @Comment("Wygląd znacznika {PING-FORMAT} w zależności od wartości pingu")
    @Comment("Lista powinna być podana od najmniejszych do największych wartości i zawierać tylko liczby naturalne, z zerem włącznie")
    @Comment("Elementy listy powinny być postaci: \"minPing-maxPing wygląd\", np.: \"0-75 &a{PING}\"")
    @Comment("* użyta w zapisie elementu listy oznacza wszystkie wartości od danego minPing w górę, np.: \"301-* &c{PING}\"")
    public List<RangeFormatting> pingFormat = Arrays.asList(
            new RangeFormatting(0, 75, "&a{PING}"),
            new RangeFormatting(76, 150, "&e{PING}"),
            new RangeFormatting(151, 300, "&c{PING}"),
            new RangeFormatting(301, Integer.MAX_VALUE, "&c{PING}")
    );

    @NotBlank
    @Comment("")
    @Comment("Symbol, od którego zaczyna się wiadomość do gildii")
    @CustomKey("chat-priv")
    public String chatPriv = "!";

    @NotBlank
    @Comment("")
    @Comment("Symbol od którego zaczyna się wiadomość do sojuszników gildii")
    @CustomKey("chat-ally")
    public String chatAlly = "!!";

    @NotBlank
    @Comment("")
    @Comment("Symbol od którego zaczyna się wiadomość do wszystkich gildii")
    @CustomKey("chat-global")
    public String chatGlobal = "!!!";

    @Comment("")
    @Comment("Wygląd wiadomości wysyłanej na czacie gildii")
    @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    public RawString chatPrivDesign = new RawString("&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Wygląd wiadomości wysyłanej na czacie dla sojuszników")
    @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    public RawString chatAllyDesign = new RawString("&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Wygląd wiadomości wysyłanej na czacie globalnym gildii")
    @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    public RawString chatGlobalDesign = new RawString("&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Wygląd wiadomoci wysyłanej na czacie gildyjnym/sojuszniczym/globalnym gildii, dla osób z włączonym /ga spy")
    @Comment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    public RawString chatSpyDesign = new RawString("&8[&6Spy&8] &7{PLAYER}&8:&f {MESSAGE}");

    @Comment("")
    @Comment("Czy wiadomości z chatów gildyjnych powinny być wyświetlane w logach serwera")
    @CustomKey("log-guild-chat")
    public boolean logGuildChat = false;

    @Comment("")
    @Comment("Wygląd tagu osób w tej samej gildii")
    public RawString prefixOur = new RawString("&a{TAG}&f ");

    @Comment("")
    @Comment("Wygląd tagu gildii sojuszniczej")
    public RawString prefixAllies = new RawString("&6{TAG}&f ");

    @Comment("")
    @Comment("Wygląd tagu wrogiej gildii")
    public RawString prefixEnemies = new RawString("&c{TAG}&f ");

    @Comment("")
    @Comment("Wygląd tagu gildii neutralnej, widziany również przez graczy bez gildii")
    public RawString prefixOther = new RawString("&7{TAG}&f ");

    @Comment("")
    @Comment("Czy ptop-online/ptop-offline mają uznawać graczy na vanishu za graczy offline")
    @Comment("UWAGA: opcja powinna wspierać pluginy jak VanishNoPacket, SuperVanish czy PremiumVanish")
    @Comment("Jeśli opcja by nie działała z tymi (lub innymi) pluginami - proszę stworzyć issue na GitHubie")
    public boolean ptopRespectVanish = true;

    @Comment("")
    @Comment("Kolory dodawane przed nickiem gracza online przy zamianie zmiennej {PTOP-x}")
    @Comment("Jeśli nie chcesz kolorowania zależnego od statusu online - pozostaw tę sekcję (i ptop-offline) pustą")
    public RawString ptopOnline = new RawString("&a");

    @Comment("")
    @Comment("Kolory dodawane przed nickiem gracza offline przy zamianie zmiennej {PTOP-x}")
    @Comment("Jeśli nie chcesz kolorowania zależnego od statusu online - pozostaw tę sekcję (i ptop-online) pustą")
    public RawString ptopOffline = new RawString("&c");

    @CustomKey("use-shared-scoreboard")
    @Comment("")
    @Comment("Czy FunnyGuilds powinno korzystać ze wspoldzielonego scoreboarda")
    @Comment("Ta opcja pozwala na wspólne działanie pluginu FunnyGuilds oraz innych pluginów modyfikujących scoreboard")
    @Comment("UWAGA: opcja eksperymentalna i może powodować błędy przy wyświetlaniu rzeczy zależnych od scoreboardów!")
    public boolean useSharedScoreboard = false;

    @Comment("")
    @Comment("Czy włączyć dummy z punktami")
    @Comment("UWAGA: zalecane jest wyłączenie tej opcji w przypadku konfliktów z BungeeCordem, więcej szczegółów tutaj: https://github.com/FunnyGuilds/FunnyGuilds/issues/769")
    @CustomKey("dummy-enable")
    public boolean dummyEnable = true;

    @Comment("")
    @Comment("Wygląd nazwy wyświetlanej za punktami")
    public RawString dummySuffix = new RawString("pkt");

    @Comment("")
    @Comment("Czy tagi gildyjne obok nicku gracza mają byc włączone")
    @CustomKey("guild-tag-enabled")
    public boolean guildTagEnabled = true;

    @Comment("")
    @Comment("Czy tag gildii podany przy tworzeniu gildii powinien zachować formę taką, w jakiej został wpisany")
    @Comment("UWAGA: gdy ta opcja jest włączona, opcja \"guild-tag-uppercase\" nie będzie miała wpływu na tag gildii")
    @CustomKey("guild-tag-keep-case")
    public boolean guildTagKeepCase = true;

    @Comment("")
    @Comment("Czy tagi gildii powinny byc pokazywane wielkimi literami")
    @Comment("Działa dopiero po zrestartowaniu serwera")
    @CustomKey("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    @Comment("")
    @Comment("Czy włączyć tłumaczenie nazw przedmiotów?")
    @CustomKey("translated-materials-enable")
    public boolean translatedMaterialsEnable = true;

    @Comment("")
    @Comment("Tłumaczenia nazw przedmiotów dla znaczników {ITEM}, {ITEMS}, {ITEM-NO-AMOUNT}, {WEAPON}")
    @Comment("Wpisywać w formacie - nazwa_przedmiotu: \"tłumaczona nazwa przedmiotu\"")
    @CustomKey("translated-materials-name")
    public Map<Material, String> translatedMaterials = ImmutableMap.<Material, String>builder()
            .put(Material.DIAMOND_SWORD, "&3diamentowy miecz")
            .put(Material.IRON_SWORD, "&7zelazny miecz")
            .put(Material.GOLD_INGOT, "&eZloto")
            .build();

    @Comment("")
    @Comment("Wygląd znaczników {ITEM} i {ITEMS} za liczbą przedmiotu")
    @Comment("Dla np. item-amount-suffix: \"szt.\" otrzymamy 1szt. golden_apple")
    public RawString itemAmountSuffix = new RawString("x");

    @Comment("")
    @Comment("Czy sprawdzanie zakazanych nazw i tagów gildii powinno być włączone")
    @CustomKey("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @Comment("")
    @Comment("Jeśli ustawione na false - nazwy i tagi z list 'restricted-guild-names', 'restricted-guild-tags' będą niedozwolone")
    @Comment("Jeśli ustawione na true - jedynie nazwy i tagi z list 'restricted-guild-names', 'restricted-guild-tags' będą dozwolone")
    @Comment("Przydatne, gdy chcesz ograniczyć tworzenie np. do 2 gildii \"RED\", \"BLUE\"")
    @CustomKey("whitelist")
    public boolean whitelist = false;

    @Comment("")
    @Comment("Wyrażenia zakazane/dozwolone do użycia jako nazwa gildii")
    @CustomKey("restricted-guild-names")
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @Comment("")
    @Comment("Wyrażenia zakazane/dozwolone do użycia jako tag gildii")
    @CustomKey("restricted-guild-tags")
    public List<String> restrictedGuildTags = Collections.singletonList("TEST");

    @Comment("")
    @Comment("Czy powiadomienie o zabójstwie gracza powinno się wyświetlać dla zabójcy jako title")
    @CustomKey("display-title-notification-for-killer")
    public boolean displayTitleNotificationForKiller = false;

    @Comment("")
    @Comment("Czy powiadomienia o wejściu na teren gildii członka gildii powinny byc wyświetlane")
    @CustomKey("notification-guild-member-display")
    public boolean regionEnterNotificationGuildMember = false;

    @Comment("")
    @Comment("Gdzie mają pojawiać się wiadomości związane z poruszaniem się po terenach gildii")
    @Comment("Możliwe miejsca wyświetlania: ACTIONBAR, BOSSBAR, CHAT, TITLE")
    @CustomKey("region-move-notification-style")
    public List<NotificationStyle> regionEnterNotificationStyle = Arrays.asList(NotificationStyle.ACTIONBAR, NotificationStyle.BOSSBAR);

    @Min(1)
    @Comment("")
    @Comment("Jak długo title/subtitle powinien się pojawiać")
    @Comment("Czas podawany w tickach (20 tickow = 1 sekunda)")
    @CustomKey("notification-title-fade-in")
    public int notificationTitleFadeIn = 10;

    @Min(1)
    @Comment("")
    @Comment("Jak długo title/subtitle powinien pozostać na ekranie gracza")
    @Comment("Czas podawany w tickach (20 tickow = 1 sekunda)")
    @CustomKey("notification-title-stay")
    public int notificationTitleStay = 10;

    @Min(1)
    @Comment("")
    @Comment("Jak długo title/subtitle powinien znikać")
    @Comment("Czas podawany w tickach (20 tickow = 1 sekunda)")
    @CustomKey("notification-title-fade-out")
    public int notificationTitleFadeOut = 10;

    @Pattern("PINK|BLUE|RED|GREEN|YELLOW|PURPLE|WHITE")
    @Comment("")
    @Comment("Jakiego koloru powinien byc boss bar podczas wyświetlania powiadomienia")
    @Comment("Dostępne kolory: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    @CustomKey("notification-boss-bar-color")
    public String bossBarColor = "RED";

    @Pattern("SOLID|SEGMENTED_6|SEGMENTED_10|SEGMENTED_12|SEGMENTED_20")
    @Comment("")
    @Comment("Jakiego stylu powinien byc boss bar podczas wyświetlania powiadomienia")
    @Comment("Dostępne style: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20")
    @CustomKey("notification-boss-bar-style")
    public String bossBarStyle = "SOLID";

    @Comment("")
    @Comment("Jakie flagi powinny być nałożone na byc boss bar podczas wyświetlania powiadomienia")
    @Comment("Dostępne flagi: DARKEN_SKY, PLAY_BOSS_MUSIC, CREATE_FOG")
    @CustomKey("notification-boss-bar-flags")
    public List<String> bossBarFlags = Collections.singletonList("CREATE_FOG");

    @Exclude
    public BossBarOptions bossBarOptions_;

    @Comment("")
    @Comment("Czy osoba, która założyła pierwszą gildię na serwerze powinna dostać nagrodę")
    @CustomKey("should-give-rewards-for-first-guild")
    public boolean giveRewardsForFirstGuild = false;

    @Comment("")
    @Comment("Przedmioty, które zostaną przyznane graczowi, który pierwszy założył gildię na serwerze")
    @CustomKey("rewards-for-first-guild")
    public List<ItemStack> firstGuildRewards = ItemUtils.parseItems("1 diamond name:&bNagroda_za_pierwsza_gildie_na_serwerze");

    @Comment("")
    @Comment("Lista przedmiotów wymaganych do resetu rankingu")
    @CustomKey("rank-reset-needed-items")
    public List<ItemStack> rankResetItems = ItemUtils.parseItems("1 diamond");

    @Comment("")
    @Comment("Lista przedmiotów wymaganych do resetu statystyk")
    @CustomKey("stats-reset-needed-items")
    public List<ItemStack> statsResetItems = ItemUtils.parseItems("1 diamond");

    @Comment("")
    @Comment("Czy przy szukaniu danych o graczu ma byc pomijana wielkość znaków jego nicku")
    @CustomKey("player-lookup-ignorecase")
    public boolean playerLookupIgnorecase = false;

    @Comment("")
    @Comment("Nazwy komend")
    @CustomKey("commands")
    public CommandsConfiguration commands = new CommandsConfiguration();

    @Comment("")
    @Comment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyświetlanie powiadomień o wejściu na teren gildii)")
    @CustomKey("event-move")
    public boolean eventMove = true;

    @Exclude
    public boolean eventPhysics;

    @Comment("")
    @Comment("Czy System Security ma byc włączony?")
    public boolean systemSecurityEnable = true;

    @DecimalMin("0")
    @Comment("")
    @Comment("Margines sprawdzania z jak daleka gracz uderzył serce gildii")
    @Comment("Jeśli dostajesz fałszywe alarmy od Security - zwiększ tę wartość do około 0.50 lub więcej")
    public double reachCompensation = 0.26;

    @Min(0)
    @Comment("")
    @Comment("Margines sprawdzania przez ile bloków gracz uderzył serce gildii")
    @CustomKey("freecam-compensation")
    public int freeCamCompensation = 0;

    @Min(1)
    @Comment("")
    @Comment("Liczba wątków używanych przez ConcurrencyManager")
    public int concurrencyThreads = 1;

    @Min(1)
    @Comment("")
    @Comment("Co ile minut dane są automatycznie zapisywane")
    public int dataInterval = 1;

    @Comment("")
    @Comment("Jak długo plugin powinien czekać na zatrzymanie wszystkich bieżących zadań przy wyłączaniu serwera")
    public Duration pluginTaskTerminationTimeout = Duration.ofSeconds(30);

    @Comment("")
    @Comment("Jaki argument powinien zostać podany przez gracza, gdy chce zaprosić wszystkich graczy w danym promieniu")
    public String inviteCommandAllArgument = "*";

    @Comment("")
    @Comment("Czy wielkość liter powinna być ignorowana dla argumentu od zapraszania wszystkich graczy w danym promieniu")
    public boolean inviteCommandAllArgumentIgnoreCase = true;

    @Comment("")
    @Comment("Maksymalna odległość, w jakiej zapraszani są gracze w momencie użycia komendy \"/invite *\"")
    public double inviteCommandAllMaxRange = 50.0;

    @Comment("")
    @Comment("Domyślna odległość, w jakiej zapraszani są gracze w momencie użycia komendy \"/invite *\"")
    public double inviteCommandAllDefaultRange = 10.0;

    @Comment("")
    @Comment("Hooki do pluginów, które powinny zostać wyłączone, opcja ta powinna być stosowania jedynie w awaryjnych sytuacjach!")
    @Comment("Lista hooków, które można wyłączyć: WorldEdit, WorldGuard, Vault, PlaceholderAPI, HolographicDisplays")
    @Comment("Aby zostawić wszystkie hooki włączone wystarczy wpisać: disabled-hooks: []")
    public Set<String> disabledHooks = new HashSet<>();

    @Comment("")
    @Comment("Typ zapisu danych:")
    @Comment(" FLAT - lokalne pliki")
    @Comment(" MYSQL - baza danych, kompatybilna z MySQL")
    public DataModel dataModel = DataModel.FLAT;

    @Comment("")
    @Comment("Dane wymagane do połączenia z bazą")
    @Comment("UWAGA: connectionTimeout jest w milisekundach!")
    @Comment(" ")
    @Comment("Sekcja poolSize odpowiada za liczbę zarezerwowanych połączeń, domyślna wartość 5 powinna wystarczyć")
    @Comment("Aby umożliwić FG automatyczne zarządzanie liczbą połączeń - ustaw poolSize na -1")
    @Comment(" ")
    @Comment("Sekcje usersTableName, guildsTableName i regionsTableName to nazwy tabel z danymi FG w bazie danych")
    @Comment("Najlepiej zmieniać te nazwy tylko wtedy, gdy np. występuje konflikt z innym pluginem")
    @Comment("Aby zmienić nazwy tabel, gdy masz juz w bazie jakieś dane z FG:")
    @Comment("1. Wyłącz serwer")
    @Comment("2. Zmień dane w configu FG")
    @Comment("3. Zmień nazwy tabel w bazie używając np. phpMyAdmin")
    public MysqlConfiguration mysql = new MysqlConfiguration();

    private List<ItemStack> loadGUI(List<String> contents) {
        List<ItemStack> items = new ArrayList<>();

        for (String guiEntry : contents) {
            ItemStack item = null;

            if (guiEntry.contains("GUI-")) {
                int index = LegacyUtils.getIndex(guiEntry);
                if (index > 0 && index <= items.size()) {
                    item = items.get(index - 1);
                }
            }
            else if (guiEntry.contains("VIPITEM-")) {
                try {
                    int index = LegacyUtils.getIndex(guiEntry);
                    if (index > 0 && index <= this.createItemsVip.size()) {
                        item = this.createItemsVip.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + guiEntry + " is > " + this.createItemsVip.size() + " or <= 0");
                }
            }
            else if (guiEntry.contains("ITEM-")) {
                try {
                    int index = LegacyUtils.getIndex(guiEntry);
                    if (index > 0 && index <= this.createItems.size()) {
                        item = this.createItems.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getPluginLogger().parser("Index given in " + guiEntry + " is > " + this.createItems.size() + " or <= 0");
                }
            }
            else {
                item = ItemUtils.parseItem(guiEntry);
            }

            if (item == null) {
                item = new ItemBuilder(MaterialUtils.matchMaterial("stained_glass_pane"), 1, 14)
                        .setName("&c&lERROR IN GUI CREATION: " + guiEntry, true).getItem();
            }

            items.add(item);
        }

        return items;
    }

    @Override
    public OkaeriConfig load() throws OkaeriException {
        super.load();

        this.heart.loadProcessedProperties();
        this.loadProcessedProperties();

        return this;
    }

    public void loadProcessedProperties() {
        this.guiItems = this.loadGUI(this.guiItems_);

        if (!this.useCommonGUI) {
            this.guiItemsVip = this.loadGUI(this.guiItemsVip_);
        }

        if (this.heart.createMaterial != null && MaterialUtils.hasGravity(this.heart.createMaterial.getFirst())) {
            this.eventPhysics = true;
        }

        if (!this.enlargeEnable) {
            this.enlargeSize = 0;
            this.enlargeItems = null;
        }

        if (this.rankSystem == RankSystem.Type.ELO) {
            this.eloConstants = new HashMap<>();

            NumberRange.parseIntegerRange(this.eloConstants_, false).forEach((key, value) -> {
                int constant = Option.attempt(NumberFormatException.class, () -> Integer.parseInt(value)).orElseGet(() -> {
                    FunnyGuilds.getPluginLogger().parser("\"" + value + "\" is not a valid elo constant!");
                    return 0;
                });

                this.eloConstants.put(key, constant);
            });
        }

        if (this.blockTeleportOnRegion.neutral || this.blockTeleportOnRegion.enemy || this.blockTeleportOnRegion.ally) {
            this.eventTeleport = true;
        }

        this.explodeMaterials = new EnumMap<>(Material.class);
        for (Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            double chance = entry.getValue();
            if (chance < 0) {
                continue;
            }

            if (entry.getKey().equalsIgnoreCase("*")) {
                this.allMaterialsAreExplosive = true;
                this.defaultExplodeChance = chance;
                continue;
            }

            Material material = MaterialUtils.parseMaterial(entry.getKey(), true);
            if (material == null || material == Material.AIR) {
                continue;
            }

            this.explodeMaterials.put(material, chance);
        }

        this.tntProtection.time.passingMidnight = this.tntProtection.time.startTime.getTime().isAfter(this.tntProtection.time.endTime.getTime());

        if (!"v1_8_R3".equals(Reflections.SERVER_VERSION)) {
            this.bossBarOptions_ = BossBarOptions.builder()
                    .color(this.bossBarColor)
                    .style(this.bossBarStyle)
                    .flags(this.bossBarFlags)
                    .build();
        }
    }

    public enum DataModel {

        FLAT,
        MYSQL

    }

}
