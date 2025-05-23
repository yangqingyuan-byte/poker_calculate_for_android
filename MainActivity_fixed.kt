package com.example.myapplication2

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication2.ui.theme.MyApplication2Theme
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// 添加高刷新率支持所需导入
import android.view.Window
import android.view.WindowManager
import android.os.Build
import android.util.Log
import android.view.Display
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

// 高刷新率配置和工具类
object HighRefreshRateUtil {
    // 更简单、更高效的淡入动画
    val FastFadeIn = fadeIn(
        animationSpec = tween(
            durationMillis = 100  // 减少动画时间提高响应速度
        )
    )
    
    // 更简单、更高效的淡出动画
    val FastFadeOut = fadeOut(
        animationSpec = tween(
            durationMillis = 100  // 减少动画时间提高响应速度
        )
    )
    
    // 简化的页面进入动画
    val EnterTransition = slideInHorizontally(
        animationSpec = tween(durationMillis = 150), // 使用tween替代spring，降低CPU使用率
    ) + FastFadeIn
    
    // 简化的页面退出动画
    val ExitTransition = slideOutHorizontally(
        animationSpec = tween(durationMillis = 150), // 使用tween替代spring，降低CPU使用率
    ) + FastFadeOut

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 设置高刷新率优化
        configureWindowForHighRefreshRate()
        
        setContent {
            MyApplication2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PokerCalculatorApp()
    
    // 配置窗口以支持高刷新率
    private fun configureWindowForHighRefreshRate() {
        // 设置窗口属性以支持边缘到边缘显示
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // 将应用设置为全屏模式以提高性能
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        
        // 更新状态栏颜色为白色，与主题保持一致
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.White.toArgb()
        
        // 设置状态栏图标为深色，以便在白色背景上更易见
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        
        // 启用硬件加速
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        
        try {
            // Android 9.0+支持刘海屏优化
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.attributes.layoutInDisplayCutoutMode = 
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            
            // 关键优化：直接设置高刷新率
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val display = display
                Log.d("RefreshRate", "开始设置高刷新率...")
                
                // 获取设备支持的最高刷新率
                    // 直接设置为最高刷新率模式
                    window.attributes = window.attributes.apply {
                        preferredDisplayModeId = highestMode.modeId
                        preferredRefreshRate = highestMode.refreshRate
                    
                    // 确保应用使用最高帧率渲染
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        window.attributes.preferredRefreshRate = highestMode.refreshRate
                // 旧版Android使用简单直接的方式设置刷新率
                @Suppress("DEPRECATION")
                val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                @Suppress("DEPRECATION")
                val display = windowManager.defaultDisplay
                
                // 获取最高的刷新率并直接设置
                val refreshRate = display.refreshRate
                Log.d("RefreshRate", "使用旧API设置刷新率: $refreshRate Hz")
                window.attributes.preferredRefreshRate = refreshRate
            
            // 强制应用GPU合成 - 在某些设备上可以提升性能
            window.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isDealer: Boolean = false,
    val initialChips: Int = 0,
    val currentChips: Int = initialChips,
    val buyIns: List<BuyIn> = emptyList()
)

data class BuyIn(
    val id: String = UUID.randomUUID().toString(),
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameResult(
    val player: Player,
    val initialChips: Int,
    val totalBuyIns: Int,
    val finalChips: Int,
    val netChange: Int,
    val moneyChange: Float
)

// 新增历史记录数据类
data class GameHistory(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val title: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(timestamp)),
    val players: List<Player>,
    val gameResults: List<GameResult> = emptyList(),
    val gameEnded: Boolean = false,
    val verificationResult: Boolean? = null
)

class PokerCalculatorViewModel : ViewModel() {
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _gameResults = MutableStateFlow<List<GameResult>>(emptyList())
    val gameResults: StateFlow<List<GameResult>> = _gameResults.asStateFlow()

    private val _gameEnded = MutableStateFlow(false)
    val gameEnded: StateFlow<Boolean> = _gameEnded.asStateFlow()

    private val _verificationResult = MutableStateFlow<Boolean?>(null)
    val verificationResult: StateFlow<Boolean?> = _verificationResult.asStateFlow()
    
    // 历史记录状态
    private val _gameHistories = MutableStateFlow<List<GameHistory>>(emptyList())
    val gameHistories: StateFlow<List<GameHistory>> = _gameHistories.asStateFlow()
    
    // 当前游戏ID，用于保存和加载
    private var currentGameId: String? = null
    
    // 添加预设玩家姓名列表状态流，替换原来的常量列表
    private val _presetPlayerNames = MutableStateFlow<List<String>>(listOf(
        "张宸宇", "杨清源", "李航", "罗靖", "周孜耕", 
        "罗春林", "马超", "谢剑宇", "jhn", "代兴意","李浩博","崔垚硕"
    ))
    val presetPlayerNames: StateFlow<List<String>> = _presetPlayerNames.asStateFlow()
    
    // 初始化，加载历史记录和预设玩家
    fun initialize(context: Context) {
        loadGameHistories(context)
        loadPresetPlayers(context)

    fun addPlayer(name: String, isDealer: Boolean = false) {
        val newPlayer = Player(name = name, isDealer = isDealer)
        _players.value = _players.value + newPlayer
        saveCurrentGameState(null)

    fun batchAddPlayers(playerNames: List<String>, initialChips: Int, dealerIndex: Int = -1) {
        // 如果已有玩家是庄家，则不再设置新的庄家
        
        val newPlayers = playerNames.mapIndexed { index, name ->
            Player(
                name = name,
                // 只有当没有现有庄家且dealerIndex有效时，才设置庄家
                isDealer = !existingDealerExists && dealerIndex >= 0 && index == dealerIndex,
                initialChips = initialChips,
                currentChips = initialChips
            )
        _players.value = _players.value + newPlayers
        saveCurrentGameState(null)

    // 添加一个带有每个玩家初始筹码的批量添加方法
    fun batchAddPlayersWithCustomChips(playerData: List<Pair<String, Int>>, dealerIndex: Int = -1) {
        // 如果已有玩家是庄家，则不再设置新的庄家
        
        val newPlayers = playerData.mapIndexed { index, (name, chips) ->
            Player(
                name = name,
                // 只有当没有现有庄家且dealerIndex有效时，才设置庄家
                isDealer = !existingDealerExists && dealerIndex >= 0 && index == dealerIndex,
                initialChips = chips,
                currentChips = chips
            )
        _players.value = _players.value + newPlayers
        saveCurrentGameState(null)

    fun updatePlayerName(playerId: String, name: String) {
        _players.value = _players.value.map { player ->
            if (player.id == playerId) {
                player.copy(name = name)
                player
        saveCurrentGameState(null)

    fun setDealer(playerId: String) {
        _players.value = _players.value.map { player ->
            player.copy(isDealer = player.id == playerId)
        saveCurrentGameState(null)

    fun clearDealer() {
        _players.value = _players.value.map { player ->
            if (player.isDealer) player.copy(isDealer = false) else player
        saveCurrentGameState(null)

    fun setInitialChips(playerId: String, chips: Int) {
        _players.value = _players.value.map { player ->
            if (player.id == playerId) {
                // 计算当前筹码与初始筹码的差额，保持相同的增量
                val chipsDiff = player.currentChips - player.initialChips
                player.copy(initialChips = chips, currentChips = chips + chipsDiff)
                player
        saveCurrentGameState(null)

    fun addBuyIn(playerId: String, amount: Int) {
        _players.value = _players.value.map { player ->
            if (player.id == playerId) {
                val newBuyIns = player.buyIns + BuyIn(amount = amount)
                player.copy(
                    buyIns = newBuyIns,
                    currentChips = player.currentChips + amount
                )
                player
        saveCurrentGameState(null)

    fun removeBuyIn(playerId: String, buyInId: String) {
        _players.value = _players.value.map { player ->
            if (player.id == playerId) {
                // 找到要删除的补码记录
                if (buyInToRemove != null) {
                    // 从补码列表中移除
                    // 从当前筹码中减去补码金额
                    val newCurrentChips = player.currentChips - buyInToRemove.amount
                    
                    player.copy(
                        buyIns = newBuyIns,
                        currentChips = newCurrentChips
                    )
                    player
                player
        saveCurrentGameState(null)

    fun setFinalChips(playerId: String, chips: Int) {
        _players.value = _players.value.map { player ->
            if (player.id == playerId) {
                player.copy(currentChips = chips)
                player
        saveCurrentGameState(null)

    fun endGame() {
        _gameEnded.value = true
        saveCurrentGameState(null)

    fun calculateResults() {
        val results = _players.value.map { player ->
            val initialTotal = player.initialChips + totalBuyIns
            val netChange = player.currentChips - initialTotal
            // 使用浮点数除法并保留一位小数
            val moneyChange = (netChange.toFloat() / 10f).let { 
                // 四舍五入到一位小数
                (Math.round(it * 10) / 10f)

            GameResult(
                player = player,
                initialChips = player.initialChips,
                totalBuyIns = totalBuyIns,
                finalChips = player.currentChips,
                netChange = netChange,
                moneyChange = moneyChange
            )
        _gameResults.value = results

        // 验证结果
        verifyResults(results)
        
        // 保存计算结果
        saveCurrentGameState(null)

    private fun verifyResults(results: List<GameResult>) {
        if (dealer != null) {
            // 使用toDouble确保使用正确的sumOf重载版本
            val dealerShouldGet = totalPlayerLoss - totalPlayerProfit
            
            // 由于浮点数比较可能有精度问题，使用近似相等进行比较
            _verificationResult.value = Math.abs(dealer.moneyChange - dealerShouldGet.toFloat()) < 0.01f

    fun resetGame() {
        // 在重置游戏前确保已保存当前游戏
        if (_players.value.isNotEmpty()) {
            saveCurrentGameState(null)
        
        // 重置游戏状态
        currentGameId = null
        _players.value = emptyList()
        _gameResults.value = emptyList()
        _gameEnded.value = false
        _verificationResult.value = null
    
    // 保存当前游戏状态到历史记录
    fun saveCurrentGameState(context: Context?) {
        if (_players.value.isEmpty()) return
        
        val gameId = currentGameId ?: UUID.randomUUID().toString()
        currentGameId = gameId
        
        val gameHistory = GameHistory(
            id = gameId,
            players = _players.value,
            gameResults = _gameResults.value,
            gameEnded = _gameEnded.value,
            verificationResult = _verificationResult.value
        )
        
        val existingHistories = _gameHistories.value.toMutableList()
        
        if (historyIndex != -1) {
            // 更新现有历史记录
            existingHistories[historyIndex] = gameHistory
            // 添加新的历史记录
            existingHistories.add(0, gameHistory)
        
        _gameHistories.value = existingHistories
        
        // 如果提供了Context，则持久化保存历史记录
        if (context != null) {
            saveGameHistoriesToStorage(context)
    
    // 从历史记录加载游戏
    fun loadGameFromHistory(gameId: String) {
        
        currentGameId = gameId
        _players.value = gameHistory.players
        _gameResults.value = gameHistory.gameResults
        _gameEnded.value = gameHistory.gameEnded
        _verificationResult.value = gameHistory.verificationResult
    
    // 删除历史记录
    fun deleteGameHistory(context: Context, gameId: String) {
        saveGameHistoriesToStorage(context)
        
        // 如果删除的是当前游戏，重置当前游戏状态
        if (currentGameId == gameId) {
            resetGame()
    
    // 从SharedPreferences加载所有历史记录
    private fun loadGameHistories(context: Context) {
        val sharedPreferences = context.getSharedPreferences("PokerCalculator", Context.MODE_PRIVATE)
        val historiesJson = sharedPreferences.getString("game_histories", null)
        
        if (!historiesJson.isNullOrEmpty()) {
            try {
                val histories = Gson().fromJson<List<GameHistory>>(historiesJson, type)
                _gameHistories.value = histories
                e.printStackTrace()
    
    // 将所有历史记录保存到SharedPreferences
    private fun saveGameHistoriesToStorage(context: Context) {
        viewModelScope.launch {
            try {
                val gson = GsonBuilder().create()
                val historiesJson = gson.toJson(_gameHistories.value)
                
                val sharedPreferences = context.getSharedPreferences("PokerCalculator", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("game_histories", historiesJson).apply()
                e.printStackTrace()
    
    // 加载预设玩家
    private fun loadPresetPlayers(context: Context) {
        val sharedPreferences = context.getSharedPreferences("PokerCalculator", Context.MODE_PRIVATE)
        val presetPlayersJson = sharedPreferences.getString("preset_players", null)
        
        if (presetPlayersJson != null) {
            try {
                val presetPlayers = Gson().fromJson<List<String>>(presetPlayersJson, type)
                if (presetPlayers.isNotEmpty()) {
                    _presetPlayerNames.value = presetPlayers
                // 如果解析失败，保留默认值
    
    // 保存预设玩家
    fun savePresetPlayers(context: Context) {
        val sharedPreferences = context.getSharedPreferences("PokerCalculator", Context.MODE_PRIVATE)
        val presetPlayersJson = Gson().toJson(_presetPlayerNames.value)
        
        sharedPreferences.edit()
            .putString("preset_players", presetPlayersJson)
            .apply()
    
    // 添加预设玩家
    fun addPresetPlayer(context: Context, playerName: String) {
        if (playerName.isBlank() || _presetPlayerNames.value.contains(playerName)) {
            return
        
        _presetPlayerNames.value = _presetPlayerNames.value + playerName
        savePresetPlayers(context)
    
    // 删除预设玩家
    fun removePresetPlayer(context: Context, playerName: String) {
        savePresetPlayers(context)
    
    // 重新排序预设玩家
    fun reorderPresetPlayers(context: Context, newOrder: List<String>) {
        if (newOrder.containsAll(_presetPlayerNames.value) && 
            newOrder.size == _presetPlayerNames.value.size) {
            _presetPlayerNames.value = newOrder
            savePresetPlayers(context)

@Composable
fun PokerCalculatorApp() {
    val navController = rememberNavController()
    val viewModel: PokerCalculatorViewModel = viewModel()
    val context = LocalContext.current
    
    // 获取当前导航状态，用于高性能动画
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    
    // 初始化ViewModel，加载历史记录
    LaunchedEffect(Unit) {
        viewModel.initialize(context)

    NavHost(
        navController = navController, 
        startDestination = "splash",
        // 使用高刷新率的导航动画
    ) {
        composable("splash") {
            SplashScreen(
                navController = navController,
                viewModel = viewModel
            )
        
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        
        composable("add_player") {
            AddPlayerScreen(
                navController = navController,
                viewModel = viewModel
            )
        
        composable("batch_add_players") {
            BatchAddPlayersScreen(
                navController = navController,
                viewModel = viewModel
            )
        
        composable(
        ) { backStackEntry ->
            val playerId = backStackEntry.arguments?.getString("playerId") ?: return@composable
            PlayerDetailScreen(
                navController = navController,
                viewModel = viewModel,
                playerId = playerId
            )
        
        composable("game_end") {
            GameEndScreen(
                navController = navController,
                viewModel = viewModel
            )
        
        composable("results") {
            ResultsScreen(
                navController = navController,
                viewModel = viewModel
            )
        
        composable("history") {
            HistoryScreen(
                navController = navController,
                viewModel = viewModel
            )

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel
) {
    val histories by viewModel.gameHistories.collectAsState()
    val latestOngoingGame = remember(histories) {
        // 找出最近的未结束游戏
    
    val latestFinishedGame = remember(histories) {
        // 找出最近的已结束游戏
    
    
    // 如果没有历史游戏，直接进入主页
    LaunchedEffect(latestOngoingGame, latestFinishedGame) {
        if (latestOngoingGame == null && latestFinishedGame == null) {
            navController.navigate("home") {
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "德州扑克计算器",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CircularProgressIndicator()
    
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                navController.navigate("home") {
            text = { 
                Column {
                    if (latestOngoingGame != null) {
                        val gameTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(Date(latestOngoingGame.timestamp))
                        
                        Text("发现一个正在进行的游戏（$gameTime）")
                        Spacer(modifier = Modifier.height(8.dp))
                    
                    if (latestFinishedGame != null && latestOngoingGame == null) {
                        val gameTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            .format(Date(latestFinishedGame.timestamp))
                        
                        Text("发现一个已结束的游戏（$gameTime）")
                        Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("是否恢复最近的游戏？")
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        
                        // 优先恢复未完成的游戏
                        val gameToRestore = latestOngoingGame ?: latestFinishedGame
                        if (gameToRestore != null) {
                            viewModel.loadGameFromHistory(gameToRestore.id)
                            
                            // 根据游戏状态决定导航到哪个界面
                            if (gameToRestore.gameEnded) {
                                if (gameToRestore.gameResults.isNotEmpty()) {
                                    navController.navigate("results") {
                                    navController.navigate("game_end") {
                                navController.navigate("home") {
                ) {
                    Text("恢复")
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        navController.navigate("home") {
                ) {
                    Text("开始新游戏")
        )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel
) {
    val players by viewModel.players.collectAsState()
    val gameEnded by viewModel.gameEnded.collectAsState()
    val gameResults by viewModel.gameResults.collectAsState()
    val context = LocalContext.current
    
    // 自动保存当前游戏状态
    LaunchedEffect(players, gameEnded, gameResults) {
        if (players.isNotEmpty()) {
            viewModel.saveCurrentGameState(context)

    // 计算总筹码数量
    val totalChips = initialChipsTotal + buyInsTotal

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    // 显示总筹码数量
                    if (players.isNotEmpty()) {
                        Card(
                            modifier = Modifier.padding(end = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                text = "总筹码: $totalChips",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                    
                    // 添加历史记录按钮
                        Icon(Icons.Default.History, contentDescription = "历史记录")
                    
                    if (players.isNotEmpty() && !gameEnded) {
                        Button(
                            onClick = {
                                viewModel.endGame()
                                navController.navigate("game_end")
                        ) {
                            Text("游戏结束")
            )
        floatingActionButton = {
            // 控制按钮显示逻辑
            when {
                // 游戏已结束时不显示按钮
                
                // 游戏未开始（没有玩家）时显示完整的添加按钮组
                players.isEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 批量添加玩家按钮
                        ExtendedFloatingActionButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        // 单个添加玩家按钮
                        FloatingActionButton(
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "添加玩家")
                
                // 游戏进行中（有玩家且未结束）时只显示一个小按钮
                else -> {
                    FloatingActionButton(
                        modifier = Modifier.size(48.dp) // 设置更小的尺寸
                    ) {
                        Icon(
                            Icons.Default.Add, 
                            contentDescription = "添加玩家",
                            modifier = Modifier.size(20.dp) // 图标也相应缩小
                        )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (players.isEmpty()) {
                Text(
                    "请添加玩家开始游戏",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                
                // 添加历史记录提示
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("查看历史记录")
                Text(
                    "玩家列表",
                    style = MaterialTheme.typography.headlineSmall
                )
                players.forEach { player ->
                    PlayerListItem(
                        player = player,
                    )
                
                // 添加显示所有补码记录的按钮
                if (hasBuyIns) {
                    
                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp)
                    ) {
                        Text("查看所有补码记录")
                    
                    // 显示所有补码记录的对话框
                    if (showAllBuyInsDialog) {
                        AlertDialog(
                            text = {
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(rememberScrollState())
                                        .padding(vertical = 8.dp)
                                ) {
                                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    
                                    // 按玩家分组显示补码记录
                                    players.forEach { player ->
                                        if (player.buyIns.isNotEmpty()) {
                                            Text(
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                            )
                                            
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(dateFormatter.format(Date(buyIn.timestamp)))
                                                    Text(
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                Divider(modifier = Modifier.padding(vertical = 2.dp))
                            confirmButton = {
                                    Text("关闭")
                        )
            
            if (gameEnded) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        viewModel.calculateResults()
                        navController.navigate("results") 
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("查看结算结果")
                
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("开始新游戏")

@Composable
fun PlayerListItem(player: Player, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (player.isDealer) {
                    Text(
                        text = "庄家",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlayerScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel
) {
    val players by viewModel.players.collectAsState()
    
    // 使用ViewModel中的预设玩家姓名列表
    val presetNames = viewModel.presetPlayerNames.collectAsState().value
    
    // 下拉菜单状态

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 替换为带下拉列表的输入框
            ExposedDropdownMenuBox(
                expanded = expanded,
            ) {
                OutlinedTextField(
                    value = playerName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                ) {
                    presetNames.forEach { name ->
                        DropdownMenuItem(
                            onClick = {
                                playerName = name
                                expanded = false
                        )
            
            OutlinedTextField(
                value = initialChips,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isDealer,
                    enabled = !dealerExists
                )
                Text("设为庄家")
            
            if (dealerExists && isDealer) {
                Text(
                    "已经有庄家了，不能再设置庄家",
                    color = MaterialTheme.colorScheme.error
                )
            
            Button(
                onClick = {
                    if (playerName.isNotBlank() && initialChips.isNotBlank()) {
                        viewModel.addPlayer(playerName, isDealer && !dealerExists)
                        val playerId = viewModel.players.value.last().id
                        viewModel.setInitialChips(playerId, initialChips.toIntOrNull() ?: 0)
                        navController.popBackStack()
                enabled = playerName.isNotBlank() && initialChips.isNotBlank() && (!isDealer || !dealerExists),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("添加")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel,
    playerId: String
) {
    val players by viewModel.players.collectAsState()
    val context = LocalContext.current
    
    val gameEnded by viewModel.gameEnded.collectAsState()
    
    // 添加编辑相关状态
    
    // 自动保存当前游戏状态
    LaunchedEffect(player) {
        viewModel.saveCurrentGameState(context)
    
    // 使用ViewModel中的预设玩家姓名列表
    val presetNames = viewModel.presetPlayerNames.collectAsState().value
    
    // 下拉菜单状态
    
    // 其他玩家是否已经是庄家
    
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                actions = {
                    if (!gameEnded) {
                        IconButton(
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.ArrowBack else Icons.Default.Edit,
                                contentDescription = if (isEditing) "取消编辑" else "编辑玩家信息"
                            )
            )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditing && !gameEnded) {
                // 编辑玩家信息卡片
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("编辑玩家信息", style = MaterialTheme.typography.titleMedium)
                        
                        // 使用下拉列表替换普通输入框
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                        ) {
                            OutlinedTextField(
                                value = editedName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expanded,
                            ) {
                                presetNames.forEach { name ->
                                    DropdownMenuItem(
                                        onClick = {
                                            editedName = name
                                            expanded = false
                                    )
                        
                        OutlinedTextField(
                            value = editedInitialChips,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = editedIsDealer,
                                enabled = !otherPlayerIsDealer || player.isDealer
                            )
                            Text("设为庄家")
                            
                            if (otherPlayerIsDealer && !player.isDealer) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "已有其他庄家",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                        
                        Button(
                            onClick = {
                                // 保存编辑的信息
                                if (editedName.isNotBlank() && editedInitialChips.isNotBlank()) {
                                    viewModel.updatePlayerName(player.id, editedName)
                                    
                                    // 只有在初始筹码变化时才更新初始筹码
                                    val initialChips = editedInitialChips.toIntOrNull() ?: 0
                                    if (initialChips != player.initialChips) {
                                        viewModel.setInitialChips(player.id, initialChips)
                                    
                                    // 更新庄家状态
                                    if (editedIsDealer && !player.isDealer) {
                                        viewModel.setDealer(player.id)
                                        // 如果取消了庄家，则设置为无庄家
                                        viewModel.clearDealer()
                                    
                                    isEditing = false
                            enabled = editedName.isNotBlank() && editedInitialChips.isNotBlank() &&
                                    (!editedIsDealer || !otherPlayerIsDealer || player.isDealer),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("保存")
                // 显示玩家信息卡片
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        
                        if (player.buyIns.isNotEmpty()) {
            
            if (!gameEnded) {
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("补码", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = buyInAmount,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                            
                            Button(
                                onClick = {
                                    if (buyInAmount.isNotBlank()) {
                                        val amount = buyInAmount.toIntOrNull() ?: 0
                                        if (amount > 0) {
                                            viewModel.addBuyIn(player.id, amount)
                                            buyInAmount = ""
                                enabled = buyInAmount.isNotBlank() && (buyInAmount.toIntOrNull() ?: 0) > 0
                            ) {
                                Text("补码")
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("设置最终筹码", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 添加直接设置最终筹码的输入框 - 已修改为默认值为0且自动应用变更
                        
                        OutlinedTextField(
                            value = finalChips,
                            onValueChange = { newValue ->
                                // 过滤非数字字符
                                
                                // 处理前导0的情况
                                finalChips = when {
                                    // 如果是空字符串，设为0
                                    filteredValue.isEmpty() -> "0"
                                    // 如果当前值是"0"且输入新字符，则只保留新输入的内容（删除前导0）
                                    finalChips == "0" && filteredValue.length > 1 -> filteredValue.substring(1)
                                    // 如果输入的新值以0开头但不是单独的0，则去除前导0
                                    filteredValue != "0" && filteredValue.startsWith("0") -> filteredValue.replaceFirst("^0+".toRegex(), "")
                                    // 其他情况保持原值
                                    else -> filteredValue
                                
                                // 更新玩家的最终筹码
                                val chips = finalChips.toIntOrNull() ?: 0
                                viewModel.setFinalChips(player.id, chips)
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
            
            if (player.buyIns.isNotEmpty()) {
                Text("补码记录", style = MaterialTheme.typography.titleMedium)
                
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dateFormatter.format(Date(buyIn.timestamp)),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            
                            if (!gameEnded) {
                                IconButton(
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "删除补码",
                                        tint = MaterialTheme.colorScheme.error
                                    )
    
    // 删除确认对话框
    if (showDeleteConfirmDialog != null) {
        val buyInId = showDeleteConfirmDialog ?: ""
        
        if (buyIn != null) {
            AlertDialog(
                text = { 
                confirmButton = {
                    Button(
                        onClick = { 
                            viewModel.removeBuyIn(player.id, buyInId)
                            showDeleteConfirmDialog = null
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("删除")
                dismissButton = {
                    Button(
                    ) {
                        Text("取消")
            )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEndScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel
) {
    val players by viewModel.players.collectAsState()
    val context = LocalContext.current
    
    // 初始化时将所有玩家的筹码设置为0
    LaunchedEffect(Unit) {
        players.forEach { player ->
            viewModel.setFinalChips(player.id, 0)
    
    // 自动保存当前游戏状态
    LaunchedEffect(players) {
        viewModel.saveCurrentGameState(context)
    
    // 计算初始筹码总数和当前筹码总数
    val gameTotalChips = initialChipsTotal + buyInsTotal // 游戏中的总筹码数量

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!dealerExists) {
                Text(
                    "请先指定一位玩家为庄家",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            
            // 添加筹码统计卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "本场游戏筹码统计",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("初始筹码总数: $initialChipsTotal")
                    Text("总补码数量: $buyInsTotal")
                    Text("本局游戏筹码总数: $gameTotalChips")
                    Text("玩家手里的筹码总数: $currentTotalChips")
                    
                    if (currentTotalChips == gameTotalChips) {
                        Text(
                            "筹码总数平衡",
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            color = MaterialTheme.colorScheme.error
                        )
            
            Text(
                "游戏已结束，请为每位玩家设置最终筹码数量",
                style = MaterialTheme.typography.bodyLarge
            )
            
            // 替换原来的玩家列表，直接在这个界面设置最终筹码
            players.forEach { player ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        // 玩家名称和庄家标识
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = player.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (player.isDealer) {
                                    Text(
                                        text = "庄家",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 显示初始和当前筹码信息
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (player.buyIns.isNotEmpty()) {
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 添加直接设置最终筹码的输入框 - 已修改为默认值为0且自动应用变更
                        
                        OutlinedTextField(
                            value = finalChips,
                            onValueChange = { newValue ->
                                // 过滤非数字字符
                                
                                // 处理前导0的情况
                                finalChips = when {
                                    // 如果是空字符串，设为0
                                    filteredValue.isEmpty() -> "0"
                                    // 如果当前值是"0"且输入新字符，则只保留新输入的内容（删除前导0）
                                    finalChips == "0" && filteredValue.length > 1 -> filteredValue.substring(1)
                                    // 如果输入的新值以0开头但不是单独的0，则去除前导0
                                    filteredValue != "0" && filteredValue.startsWith("0") -> filteredValue.replaceFirst("^0+".toRegex(), "")
                                    // 其他情况保持原值
                                    else -> filteredValue
                                
                                // 更新玩家的最终筹码
                                val chips = finalChips.toIntOrNull() ?: 0
                                viewModel.setFinalChips(player.id, chips)
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    viewModel.calculateResults()
                    navController.navigate("results")
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("完成设置并结算")
    
    if (showAlert) {
        AlertDialog(
            confirmButton = {
                    Text("确认")
        )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel
) {
    val results by viewModel.gameResults.collectAsState()
    val verificationResult by viewModel.verificationResult.collectAsState()
    val context = LocalContext.current
    
    // 自动保存当前游戏状态
    LaunchedEffect(results, verificationResult) {
        if (results.isNotEmpty()) {
            viewModel.saveCurrentGameState(context)
    
    // 如果结果为空，自动计算一次结果
    LaunchedEffect(Unit) {
        if (results.isEmpty()) {
            viewModel.calculateResults()
    
    val scrollState = rememberScrollState()
    
    // 计算总的筹码统计
    val totalGameChips = initialChipsTotal + buyInsTotal

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                actions = {
                    IconButton(onClick = { 
                        if (results.isNotEmpty()) {
                            copyResultsToClipboard(context, results)
                        Icon(Icons.Default.ContentCopy, contentDescription = "复制结果")
            )
    ) { padding ->
        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 筹码总数统计卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "本场游戏筹码统计",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("初始筹码总数: $initialChipsTotal")
                        Text("总补码数量: $buyInsTotal")
                        Text("本局游戏筹码总数: $totalGameChips")
                        Text("最终筹码总数: $finalChipsTotal")
                        
                        if (finalChipsTotal == totalGameChips) {
                            Text(
                                "筹码总数平衡",
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                color = MaterialTheme.colorScheme.error
                            )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (verificationResult == true) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else 
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        if (verificationResult == true) {
                            Text(
                                text = "庄家盈亏与其他玩家盈亏总和相等",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "庄家盈亏与其他玩家盈亏总和不相等，请检查筹码输入",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("结算详情", style = MaterialTheme.typography.headlineSmall)
                    
                    Button(
                    ) {
                        Text("复制结果")
                
                // 庄家结果
                if (dealer != null) {
                    ResultDetailCard(result = dealer, isDealer = true)
                    
                    // 其他玩家结果
                    otherPlayers.forEach { result ->
                        ResultDetailCard(result = result, isDealer = false)
                
                Button(
                    onClick = { 
                        viewModel.resetGame()
                        navController.navigate("home") {
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("开始新游戏")

private fun copyResultsToClipboard(context: Context, results: List<GameResult>) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    val stringBuilder = StringBuilder("德州扑克游戏结算\n\n")
    
    stringBuilder.append("【结算详情】\n")
    results.forEach { result ->
        
        val resultText = if (result.moneyChange > 0) {
            "收支平衡"
        stringBuilder.append("$resultText\n")
        
        if (!result.player.isDealer) {
            val transferText = if (result.moneyChange > 0) {
                "无需转账"
            stringBuilder.append("$transferText\n")
        stringBuilder.append("\n")
    
    stringBuilder.append("【补码记录】\n")
    results.forEach { result ->
        if (result.player.buyIns.isNotEmpty()) {
            result.player.buyIns.forEachIndexed { index, buyIn ->
            stringBuilder.append("\n")
    
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("德州扑克游戏结算", stringBuilder.toString())
    clipboardManager.setPrimaryClip(clipData)
    
    Toast.makeText(context, "结果已复制到剪贴板", Toast.LENGTH_SHORT).show()

@Composable
fun ResultDetailCard(result: GameResult, isDealer: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    style = MaterialTheme.typography.titleMedium
                )
                
                val moneyText = if (result.moneyChange > 0) {
                    "0.0"
                
                val textColor = when {
                    result.moneyChange > 0 -> MaterialTheme.colorScheme.primary
                    result.moneyChange < 0 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                
                Text(
                    text = "¥$moneyText",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                
                Column(horizontalAlignment = Alignment.End) {
                    if (!isDealer) {
                        val transferText = if (result.moneyChange > 0) {
                            "无需转账"
                        
                        val transferTextColor = when {
                            result.moneyChange > 0 -> MaterialTheme.colorScheme.primary
                            result.moneyChange < 0 -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        
                        Text(
                            text = transferText,
                            color = transferTextColor
                        )

@Composable
fun GameLogCard(results: List<GameResult>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            
            results.forEach { result ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (result.player.buyIns.isNotEmpty()) {
                        Text("补码记录:")
                        result.player.buyIns.forEachIndexed { index, buyIn ->
                    
                    
                    val resultText = if (result.moneyChange > 0) {
                        "收支平衡"
                    
                    Text(resultText)
                    
                    if (!result.player.isDealer) {
                        val transferText = if (result.moneyChange > 0) {
                            "无需转账"
                        
                        Text(transferText)
                
                if (result != results.last()) {
                    Divider()

private fun copyGameLogToClipboard(context: Context, results: List<GameResult>) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    val stringBuilder = StringBuilder("德州扑克游戏日志\n\n")
    
    // 添加总计信息
    
    stringBuilder.append("【游戏总计】\n")
    stringBuilder.append("初始筹码总数: $initialChipsTotal\n")
    stringBuilder.append("补码总额: $buyInsTotal\n")
    stringBuilder.append("最终筹码总数: $finalChipsTotal\n")
    if (initialChipsTotal + buyInsTotal == finalChipsTotal) {
        stringBuilder.append("筹码总数平衡\n")
    stringBuilder.append("\n")
    
    stringBuilder.append("【玩家详情】\n")
    results.forEach { result ->
        
        if (result.player.buyIns.isNotEmpty()) {
            stringBuilder.append("补码记录:\n")
            result.player.buyIns.forEachIndexed { index, buyIn ->
            stringBuilder.append("无补码记录\n")
        
        
        val resultText = if (result.moneyChange > 0) {
            "收支平衡"
        
        stringBuilder.append("$resultText\n")
        
        if (!result.player.isDealer) {
            val transferText = if (result.moneyChange > 0) {
                "无需转账"
            
            stringBuilder.append("$transferText\n")
        stringBuilder.append("\n")
    
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("德州扑克游戏日志", stringBuilder.toString())
    clipboardManager.setPrimaryClip(clipData)
    
    Toast.makeText(context, "游戏日志已复制到剪贴板", Toast.LENGTH_SHORT).show()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchAddPlayersScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel
) {
    // 用于跟踪选中的玩家
    val presetNames = viewModel.presetPlayerNames.collectAsState().value
    
    // 改用Map存储每个玩家是否被选中，而不是用List，避免顺序依赖于点击顺序
    val selectedPlayerMap = remember { 
        mutableStateMapOf<String, Boolean>().apply {
    
    // 用于存储每个玩家的筹码数量
    
    // 默认统一筹码值和庄家选择
    
    // 当前玩家列表中是否已有庄家
    val players by viewModel.players.collectAsState()
    
    // 管理常用玩家对话框状态
    val context = LocalContext.current

    // 计算已选玩家数量

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                actions = {
                    // 添加管理常用玩家按钮
                        Icon(Icons.Default.Edit, contentDescription = "管理常用玩家")
            )
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 选择筹码设置模式
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("筹码设置", style = MaterialTheme.typography.titleMedium)
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = useUniformChips,
                        )
                        Text(
                            text = "统一筹码",
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        RadioButton(
                            selected = !useUniformChips,
                        )
                        Text(
                            text = "个性化筹码",
                        )
                    
                    // 统一设置筹码输入框，仅在选择统一筹码时显示
                    if (useUniformChips) {
                        OutlinedTextField(
                            value = defaultChips,
                            onValueChange = { 
                                // 只接受数字输入
                                defaultChips = filtered
                                
                                // 同步更新所有选中玩家的筹码值
                                presetNames.forEach { name ->
                                    if (selectedPlayerMap[name] == true) {
                                        playerChips[name] = filtered
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
            
            // 选择玩家列表
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("选择要添加的玩家", style = MaterialTheme.typography.titleMedium)
                    
                    presetNames.forEachIndexed { index, name ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedPlayerMap[name] == true,
                                    onCheckedChange = { isChecked ->
                                        selectedPlayerMap[name] = isChecked
                                        if (isChecked) {
                                            // 如果是统一筹码模式，设置为默认值
                                            if (useUniformChips && defaultChips.isNotEmpty()) {
                                                playerChips[name] = defaultChips
                                                // 确保每个玩家都有一个筹码值记录
                                                playerChips[name] = ""
                                            // 如果取消选中庄家，清除庄家设置
                                            if (selectedDealerName == name) {
                                                selectedDealerName = null
                                )
                                
                                Text(name, modifier = Modifier.weight(1f))
                                
                                // 只有当玩家被选中且没有已存在的庄家时，才能设为庄家
                                if (selectedPlayerMap[name] == true) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("庄家")
                                        
                                        Checkbox(
                                            checked = selectedDealerName == name,
                                            onCheckedChange = { isChecked ->
                                                selectedDealerName = if (isChecked) name else null
                                            enabled = !dealerExists
                                        )
                            
                            // 如果玩家被选中且是个性化筹码模式，显示筹码输入框
                            if (selectedPlayerMap[name] == true && !useUniformChips) {
                                OutlinedTextField(
                                    value = playerChips[name] ?: "",
                                    onValueChange = { value ->
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 32.dp, bottom = 8.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            
                            if (index < presetNames.size - 1) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                    
                    if (dealerExists) {
                        Text(
                            "已经有庄家了，不能在批量添加中设置庄家",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
            
            // 添加按钮
            Button(
                onClick = {
                    // 按照原列表顺序收集选中的玩家
                    
                    // 找出庄家在选中玩家列表中的位置
                    val dealerIndexInSelected = if (dealerExists || selectedDealerName == null) {
                        -1 // 不设置庄家
                        selectedPlayers.indexOf(selectedDealerName)
                    
                    // 根据不同的筹码设置模式进行处理
                    if (useUniformChips) {
                        // 统一筹码模式
                        val chips = defaultChips.toIntOrNull() ?: 0
                        if (selectedPlayers.isNotEmpty() && chips > 0) {
                            viewModel.batchAddPlayers(selectedPlayers, chips, dealerIndexInSelected)
                            navController.popBackStack()
                        // 个性化筹码模式
                        // 收集所有选中的玩家和他们的筹码值，保持原列表顺序
                        val playerData = selectedPlayers.mapNotNull { name ->
                            val chips = playerChips[name]?.toIntOrNull() ?: 0
                            if (chips > 0) Pair(name, chips) else null
                        
                        if (playerData.isNotEmpty()) {
                            viewModel.batchAddPlayersWithCustomChips(playerData, dealerIndexInSelected)
                            navController.popBackStack()
                enabled = if (useUniformChips) {
                    selectedCount > 0 && defaultChips.isNotBlank() && (defaultChips.toIntOrNull() ?: 0) > 0
                    selectedCount > 0 && presetNames.all { name -> 
                        selectedPlayerMap[name] != true || (playerChips[name]?.toIntOrNull() ?: 0) > 0 
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("批量添加 ($selectedCount)")

            // 显示管理常用玩家对话框
            if (showManagePlayersDialog) {
                ManagePresetPlayersDialog(
                    presetPlayers = presetNames,
                    onAddPlayer = { newName -> 
                        viewModel.addPresetPlayer(context, newName) 
                        // 为新添加的玩家初始化状态
                        selectedPlayerMap[newName] = false
                    onRemovePlayer = { name -> 
                        // 如果要删除的玩家已被选中，更新状态
                        if (selectedPlayerMap[name] == true) {
                            playerChips.remove(name)
                            // 处理庄家设置
                            if (selectedDealerName == name) {
                                selectedDealerName = null
                        // 从选中Map中移除该玩家
                        selectedPlayerMap.remove(name)
                        viewModel.removePresetPlayer(context, name) 
                )

@Composable
fun ManagePresetPlayersDialog(
    presetPlayers: List<String>,
    onDismiss: () -> Unit,
    onAddPlayer: (String) -> Unit,
    onRemovePlayer: (String) -> Unit
) {
    
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 添加新玩家的输入框和按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newPlayerName,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            if (newPlayerName.isNotBlank() && !presetPlayers.contains(newPlayerName)) {
                                onAddPlayer(newPlayerName)
                                newPlayerName = ""
                        enabled = newPlayerName.isNotBlank() && !presetPlayers.contains(newPlayerName)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "添加玩家")
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // 现有玩家列表
                Text("当前玩家列表", style = MaterialTheme.typography.titleMedium)
                
                if (presetPlayers.isEmpty()) {
                    Text(
                        "暂无常用玩家，请添加",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    presetPlayers.forEach { name ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = name,
                                modifier = Modifier.weight(1f)
                            )
                            
                            IconButton(
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "删除玩家",
                                    tint = MaterialTheme.colorScheme.error
                                )
                        
                        Divider()
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("完成")
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: PokerCalculatorViewModel
) {
    val histories by viewModel.gameHistories.collectAsState()
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
            )
    ) { padding ->
        if (histories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("暂无历史记录")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "游戏历史记录",
                    style = MaterialTheme.typography.headlineSmall
                )
                
                histories.forEach { history ->
                    HistoryListItem(
                        history = history,
                        onRestore = {
                            viewModel.loadGameFromHistory(history.id)
                            // 根据游戏状态决定导航到哪个界面
                            if (history.gameEnded) {
                                if (history.gameResults.isNotEmpty()) {
                                    navController.navigate("results")
                                    navController.navigate("game_end")
                                navController.navigate("home") {
                    )
    
    // 删除确认对话框
    if (showDeleteConfirmDialog != null) {
        val historyId = showDeleteConfirmDialog ?: ""
        
        if (history != null) {
            AlertDialog(
                text = { 
                confirmButton = {
                    Button(
                        onClick = { 
                            viewModel.deleteGameHistory(context, historyId)
                            showDeleteConfirmDialog = null
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("删除")
                dismissButton = {
                    Button(
                    ) {
                        Text("取消")
            )

@Composable
fun HistoryListItem(
    history: GameHistory,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(history.timestamp))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onRestore
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除历史记录",
                        tint = MaterialTheme.colorScheme.error
                    )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 玩家信息
            Text("庄家: $dealerName")
            
            // 游戏状态
            val statusText = if (history.gameEnded) {
                if (history.gameResults.isNotEmpty()) "已结算" else "待结算"
                "进行中"
            
            val statusColor = when {
                history.gameEnded && history.gameResults.isNotEmpty() -> MaterialTheme.colorScheme.primary
                history.gameEnded -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.secondary
            
            Text(
                text = "状态: $statusText",
                color = statusColor
            )
            
            Button(
                onClick = onRestore,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("恢复此游戏")
