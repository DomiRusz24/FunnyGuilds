package net.dzikoysk.funnyguilds

import net.dzikoysk.funnyguilds.config.MessageConfiguration
import net.dzikoysk.funnyguilds.config.NumberRange
import net.dzikoysk.funnyguilds.config.PluginConfiguration
import net.dzikoysk.funnyguilds.config.tablist.TablistConfiguration
import net.dzikoysk.funnyguilds.guild.GuildManager
import net.dzikoysk.funnyguilds.guild.GuildRankManager
import net.dzikoysk.funnyguilds.guild.RegionManager
import net.dzikoysk.funnyguilds.rank.DefaultTops
import net.dzikoysk.funnyguilds.rank.placeholders.RankPlaceholdersService
import net.dzikoysk.funnyguilds.shared.bukkit.ItemUtils
import net.dzikoysk.funnyguilds.user.UserManager
import net.dzikoysk.funnyguilds.user.UserRankManager
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.anyString
import org.mockito.Mockito.lenient
import org.mockito.Mockito.mockStatic
import org.mockito.junit.jupiter.MockitoExtension
import java.util.logging.Logger

@ExtendWith(MockitoExtension::class)
open class FunnyGuildsSpec : BukkitSpec() {

    private lateinit var mockedFunnyGuilds: MockedStatic<FunnyGuilds>
    private lateinit var mockedItemUtils: MockedStatic<ItemUtils>

    @BeforeEach
    fun openMockedFunnyGuilds() {
        mockedFunnyGuilds = mockStatic(FunnyGuilds::class.java)
        mockedItemUtils = mockStatic(ItemUtils::class.java)
    }

    @Mock
    lateinit var funnyGuilds: FunnyGuilds

    private val funnyGuildsLogger = TestLogger(Logger.getLogger("TestLogger"))

    protected lateinit var config: PluginConfiguration
    private lateinit var messages: MessageConfiguration
    private lateinit var tablistConfig: TablistConfiguration

    protected lateinit var userManager: UserManager
    protected lateinit var guildManager: GuildManager
    protected lateinit var userRankManager: UserRankManager
    protected lateinit var guildRankManager: GuildRankManager
    private lateinit var regionManager: RegionManager

    protected lateinit var rankPlaceholdersService: RankPlaceholdersService

    @BeforeEach
    fun prepareFunnyGuilds() {
        mockedFunnyGuilds.`when`<FunnyGuildsLogger> { FunnyGuilds.getPluginLogger() }.thenReturn(funnyGuildsLogger)

        mockedItemUtils.`when`<ItemStack> { ItemUtils.parseItem(anyString()) }.thenReturn(null)

        config = PluginConfiguration()
        messages = MessageConfiguration()
        tablistConfig = TablistConfiguration()

        preparePluginConfiguration()

        lenient().`when`(funnyGuilds.pluginConfiguration).thenReturn(config)
        lenient().`when`(funnyGuilds.messageConfiguration).thenReturn(messages)
        lenient().`when`(funnyGuilds.tablistConfiguration).thenReturn(tablistConfig)

        userManager = UserManager(config)
        guildManager = GuildManager(config)
        userRankManager = UserRankManager(config)
        userRankManager.register(DefaultTops.defaultUserTops(config, userManager))
        guildRankManager = GuildRankManager(config)
        guildRankManager.register(DefaultTops.defaultGuildTops(guildManager))
        regionManager = RegionManager(config)

        lenient().`when`(funnyGuilds.userManager).thenReturn(userManager)
        lenient().`when`(funnyGuilds.guildManager).thenReturn(guildManager)
        lenient().`when`(funnyGuilds.userRankManager).thenReturn(userRankManager)
        lenient().`when`(funnyGuilds.guildRankManager).thenReturn(guildRankManager)
        lenient().`when`(funnyGuilds.regionManager).thenReturn(regionManager)

        rankPlaceholdersService = RankPlaceholdersService(
            config,
            messages,
            tablistConfig,
            userRankManager,
            guildRankManager
        )

        lenient().`when`(funnyGuilds.rankPlaceholdersService).thenReturn(rankPlaceholdersService)
        mockedFunnyGuilds.`when`<FunnyGuilds> { FunnyGuilds.getInstance() }.thenReturn(funnyGuilds)
    }

    private fun preparePluginConfiguration() {
        val parsedData = mutableMapOf<NumberRange, Int>()

        NumberRange.parseIntegerRange(config.eloConstants_, false)
            .forEach { (range, number) -> parsedData[range] = number.toInt() }

        config.eloConstants = parsedData
    }

    @AfterEach
    fun closeMockedFunnyGuilds() {
        mockedFunnyGuilds.close()
        mockedItemUtils.close()
    }

}