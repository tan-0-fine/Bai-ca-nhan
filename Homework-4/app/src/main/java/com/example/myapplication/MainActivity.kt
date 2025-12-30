package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.navigation.NavController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import android.util.Log
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.filled.Done
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.filled.Android
data class UIComponent(
    val name: String,
    val description: String
)
enum class Destination(
    val route: String,
    val icon: ImageVector,
    val label: String,
    val contentDescription: String
) {
    SONGS("songs", Icons.Filled.LibraryMusic, "Songs", "Songs screen"),
    ARTISTS("artists", Icons.Default.Person, "Artists", "Artists screen"),
    HOME("home", Icons.Default.Home, "Home", "Home screen")
}
val displayComponents = listOf(
    UIComponent("Text", "Displays text"),
    UIComponent("Image", "Displays an image")
)

val inputComponents = listOf(
    UIComponent("Button", "Clickable button"),
    UIComponent("OrderButton", "Clickable button"),
    UIComponent("Menu", "Clickable Menu"),
    UIComponent("NavigationBar", "Bottom navigation bar"),
    UIComponent("NavigationTab", "Tab navigation"),
    UIComponent("FilterChip", "Chip with text"),
    UIComponent("VerticalDivider", "Divider two text components"),
    UIComponent("TextField", "Input field for text"),
    UIComponent("PasswordField", "Input field for passwords"),
    UIComponent("Checkbox", "Select multiple options"),
    UIComponent("RadioButton", "Select one option"),
    UIComponent("Switch", "On / Off toggle"),
    UIComponent("Slider", "Select value by sliding")
)

val layoutComponents = listOf(
    UIComponent("Row", "Arranges elements horizontally"),
    UIComponent("Column", "Arranges elements vertically"),
    UIComponent("Box", "Stack elements"),
    UIComponent("LazyColumn", "Scrollable list"),
    UIComponent("Card", "Surface container"),
    UIComponent("OutlinedCard", "Surface container with border"),
    UIComponent("Dialog", "Popup dialog")
)



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "intro"
                ) {

                    // 🔹 Màn hình đầu (I’m ready)
                    composable("intro") {
                        val introScreen = IntroScreen(
                            onReadyClick = {
                                navController.navigate("list") {
                                    popUpTo("intro") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 🔹 Danh sách component
                    composable("list") {
                        ComponentListScreen(navController)
                    }

                    // 🔹 Màn hình chi tiết
                    composable("detail/{type}") { backStackEntry ->
                        val type = backStackEntry.arguments?.getString("type") ?: ""
                        ComponentDetailScreen(
                            type = type,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentListScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ===== DISPLAY =====
        item { SectionTitle("Display") }
        items(displayComponents) { component ->
            ComponentItem(component, navController)
        }

        // ===== INPUT =====
        item { SectionTitle("Input") }
        items(inputComponents) { component ->
            ComponentItem(component, navController)
        }

        // ===== LAYOUT =====
        item { SectionTitle("Layout") }
        items(layoutComponents) { component ->
            ComponentItem(component, navController)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetailScreen(
    type: String,
    navController: NavController
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    var passwordText by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(text = type, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    MinimalDropdownMenu()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (type) {
                "Text" -> TextDemo()
                "Image" -> ImageDemo()
                "Button" -> ButtonDemo()
                "OrderButton" -> OrderButtonsDemo(
                    orderAlphabetically = {},
                    orderByLength = {},
                    resetOrder = {}
                )
                "DropdownMenu" -> MinimalDropdownMenu()
                "Menu" -> UserRegistrationScreen()
                "FilterChip" -> FilterChip()
                "NavigationBar" -> NavigationBar()
                "NavigationTab" -> NavigationTab()
                "AppNavHost" -> AppNavHost(
                    navController = navController as NavHostController, // Ép kiểu nếu cần
                    startDestination = Destination.HOME,
                    modifier = Modifier.fillMaxSize()
                )
                "TextField" -> TextFieldDemo(
                    text = inputText,
                    onSave = { inputText = it }
                )

                "PasswordField" -> PasswordFieldDemo(
                    onSave = {
                        passwordText = it
                    }
                )
                "VerticalDivider" -> VerticalDivider()
                "Checkbox" -> CheckboxDemo()
                "RadioButton" -> RadioDemo()
                "Switch" -> SwitchDemo()
                "Slider" -> SliderDemo()
                "Row" -> RowDemo()
                "Column" -> ColumnDemo()
                "Box" -> BoxDemo()
                "LazyColumn" -> LazyColumnDemo()
                "Card" -> CardDemo()
                "OutlinedCard" -> OutlinedCard()
                "Dialog" -> DialogDemo()
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
@Composable
fun ComponentItem(
    component: UIComponent,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                navController.navigate("detail/${component.name}")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = component.name,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = component.description,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}
@Composable
fun IntroScreen(
    onReadyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.jp),
                contentDescription = "Jetpack Compose Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Jetpack Compose",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Jetpack Compose is a modern UI toolkit for building native Android applications using a declarative approach.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onReadyClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text("I'm ready")
        }
    }
}

@Composable
fun TextDemo() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = buildAnnotatedString {

                append("The ")
                withStyle(
                    SpanStyle(
                        textDecoration = TextDecoration.LineThrough
                    )
                ) {
                    append("quick ")
                }

                withStyle(
                    SpanStyle(
                        color = Color(0xFFFF9800),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Brown ")
                }

                // fox
                append("fox ")

                // j u m p s (TÁCH CHỮ)
                append("j u m p s ")

                // over (đậm)
                withStyle(
                    SpanStyle(fontWeight = FontWeight.Bold)
                ) {
                    append("over ")
                }

                // the (gạch chân)
                withStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline)
                ) {
                    append("the ")
                }

                // lazy (nghiêng)
                withStyle(
                    SpanStyle(fontStyle = FontStyle.Italic)
                ) {
                    append("lazy ")
                }

                // dog.
                append("dog.")
            },
            fontSize = 26.sp
        )
    }
}

@Composable
fun ImageDemo() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Image Detail",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // ===== ẢNH LOCAL =====
        Spacer(Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.uth),
            contentDescription = "Local Image",
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "In App",
            fontSize = 12.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(24.dp))

        // ===== ẢNH WEB =====

        Spacer(Modifier.height(8.dp))

        AsyncImage(
            model = "https://s.cmx-cdn.com/giaothongvantaitphcm.edu.vn/wp-content/uploads/2024/06/ky-niem-36-nam-thanh-lap-truong-dai-hoc-giao-thong-van-tai-tphcm-560px.jpg",
            contentDescription = "Web Image",
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            error = painterResource(R.drawable.ic_launcher_foreground)
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "https://s.cmx-cdn.com/giaothongvantaitphcm.edu.vn/wp-content/uploads/2024/06/ky-niem-36-nam-thanh-lap-truong-dai-hoc-giao-thong-van-tai-tphcm-560px.jpg",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}


@Composable
fun ButtonDemo() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ===== BUTTON THƯỜNG =====
        Button(onClick = {}) {
            Text("Button")
        }

        // ===== OUTLINED BUTTON =====
        OutlinedButton(onClick = {}) {
            Text("Outlined Button")
        }

        // ===== TEXT BUTTON =====
        TextButton(onClick = {}) {
            Text("Text Button")
        }

        // ===== ICON BUTTON =====
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = Color.Red
            )
        }

        // ===== BUTTON CÓ ICON =====
        Button(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null
            )
            Spacer(Modifier.width(8.dp))
            Text("Send")
        }

        // ===== DISABLED BUTTON =====
        Button(
            onClick = {},
            enabled = false
        ) {
            Text("Disabled Button")
        }

        // ===== FLOATING ACTION BUTTON =====
        FloatingActionButton(
            onClick = {}
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}
@Composable
private fun OrderButtonsDemo(
    resetOrder: () -> Unit,
    orderAlphabetically: () -> Unit,
    orderByLength: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        var selectedIndex by remember { mutableIntStateOf(0) }
        val options = listOf("Reset", "Alphabetical", "Length")

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ),
                    onClick = {
                        Log.d("AnimatedOrderedList", "selectedIndex: $selectedIndex")
                        selectedIndex = index
                        when (options[selectedIndex]) {
                            "Reset" -> resetOrder()
                            "Alphabetical" -> orderAlphabetically()
                            "Length" -> orderByLength()
                        }
                    },
                    selected = index == selectedIndex
                ) {
                    Text(label)
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTab(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(modifier = modifier) { contentPadding ->
        PrimaryTabRow(selectedTabIndex = selectedDestination, modifier = Modifier.padding(contentPadding)) {
            Destination.entries.forEachIndexed { index, destination ->
                Tab(
                    selected = selectedDestination == index,
                    onClick = {
                        navController.navigate(route = destination.route)
                        selectedDestination = index
                    },
                    text = {
                        Text(
                            text = destination.label,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
        AppNavHost(navController, startDestination)
    }
}
@Composable
fun UserMenu() {
    // 1. Khai báo trạng thái đóng/mở
    var expanded by remember { mutableStateOf(false) }

    // 2. LỚP CHA (Box) - Đóng vai trò là "neo"
    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        // 3. VẬT KÍCH HOẠT (Nút bấm)
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Mở menu")
        }

        // 4. NỘI DUNG (Menu) - Nó sẽ tự động hít vào cái Box phía trên
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Đổi mật khẩu") },
                onClick = { expanded = false }
            )
            DropdownMenuItem(
                text = { Text("Đăng xuất") },
                onClick = { expanded = false }
            )
        }
    }
}
@Composable
fun MinimalDropdownMenu() {
    // Trạng thái đóng/mở menu
    var expanded by remember { mutableStateOf(false) }

    // 1. Box ngoài cùng: Dùng để xác định vị trí trên màn hình
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), // Padding nhẹ để không sát mép màn hình
        contentAlignment = Alignment.TopEnd // Đẩy toàn bộ nội dung sang góc phải
    ) {
        // 2. Box Neo (Anchor): Bọc IconButton và DropdownMenu lại với nhau
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {

            // Nút bấm 3 chấm
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color(0xFF1976D2) // Màu xanh khớp với tiêu đề "Menu" của bạn
                )
            }

            // Nội dung Menu bung ra
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Mục Profile
                DropdownMenuItem(
                    text = { Text("Profile") },
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    onClick = {
                        expanded = false // Đóng menu sau khi chọn
                        /* Xử lý chuyển màn hình Profile ở đây */
                    }
                )

                // Mục Settings
                DropdownMenuItem(
                    text = { Text("Settings") },
                    leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                    onClick = {
                        expanded = false
                        /* Xử lý cài đặt ở đây */
                    }
                )

                HorizontalDivider() // Đường kẻ ngang phân cách

                // Mục Feedback
                DropdownMenuItem(
                    text = { Text("Send Feedback") },
                    leadingIcon = { Icon(Icons.Outlined.Feedback, contentDescription = null) },
                    trailingIcon = { Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = null) },
                    onClick = { expanded = false }
                )

                HorizontalDivider()

                // Mục About
                DropdownMenuItem(
                    text = { Text("About") },
                    leadingIcon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    onClick = { expanded = false }
                )

                // Mục Help
                DropdownMenuItem(
                    text = { Text("Help") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Help, contentDescription = null) },
                    trailingIcon = { Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null) },
                    onClick = { expanded = false }
                )
            }
        }
    }
}
@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Destination.SONGS.route) {
            // Thay bằng màn hình Songs của bạn
            Text("Màn hình Songs")
        }
        composable(Destination.ARTISTS.route) {
            Text("Màn hình Artists")
        }
        composable(Destination.HOME.route) {
            Text("Màn hình Home")
        }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "intro"
    ) {
        composable("intro") {
            IntroScreen(
                onReadyClick = {
                    navController.navigate("components")
                }
            )
        }

        composable("components") {
            ComponentsListScreen()
        }
    }
}

@Composable
fun ComponentsListScreen() {
    TODO("Not yet implemented")
}



@Composable
fun NavigationBar(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.SONGS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(contentPadding))
    }
}
@Composable
fun FilterChip() {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        onClick = { selected = !selected },
        label = {
            Text("Filter chip")
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}
@Composable
fun InfoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String // Thêm tham số này để dùng chung cho nhiều mục đích
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Text(
            text = "Dữ liệu đã nhập: $value",
            color = Color.Blue,
            fontSize = 14.sp
        )
    }
}
@Composable
fun UserRegistrationScreen() {
    var username by rememberSaveable { mutableStateOf("") }
    var finalSummary by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "ĐĂNG KÝ TÀI KHOẢN",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1976D2)
        )

        // Tận dụng InfoTextField của bạn
        InfoTextField(
            value = username,
            onValueChange = { username = it },
            label = "Tên đăng nhập"
        )

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

        // Gọi PasswordFieldDemo
        // Lưu ý: Đã bỏ tham số 'password' nếu hàm Demo tự quản lý state bên trong
        PasswordFieldDemo(
            onSave = { password ->
                finalSummary = "Tài khoản: $username | Mật khẩu: $password"
            }
        )

        if (finalSummary.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEB3B))
            ) {
                Text(
                    text = "Hệ thống xác nhận:\n$finalSummary",
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
@Composable
fun PasswordFieldDemo(
    onSave: (String) -> Unit
) {
    var typingPassword by rememberSaveable { mutableStateOf("") }
    var savedPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val hasMinLength = typingPassword.length >= 8
    val hasUpperCase = typingPassword.any { it.isUpperCase() }
    val hasLowerCase = typingPassword.any { it.isLowerCase() }
    val hasDigit = typingPassword.any { it.isDigit() }

    val conditionsMet = listOf(hasMinLength, hasUpperCase, hasLowerCase, hasDigit).count { it }
    val strengthProgress = conditionsMet / 4f

    val strengthColor = when (conditionsMet) {
        0, 1 -> Color.Red
        2, 3 -> Color(0xFFFFB300)
        else -> Color(0xFF4CAF50)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Thiết lập mật khẩu bảo mật", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        OutlinedTextField(
            value = typingPassword,
            onValueChange = { typingPassword = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // Dùng Icons.AutoMirrored để tránh lỗi đỏ thư viện
                val image = if (passwordVisible) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            },
            isError = typingPassword.isNotEmpty() && conditionsMet < 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = strengthColor,
                unfocusedBorderColor = strengthColor.copy(alpha = 0.5f)
            )
        )

        LinearProgressIndicator(
            progress = { strengthProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = strengthColor,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SecurityCriteriaItem("Ít nhất 8 ký tự", hasMinLength)
            SecurityCriteriaItem("Có chữ Hoa và chữ Thường", hasUpperCase && hasLowerCase)
            SecurityCriteriaItem("Có ít nhất 1 chữ số", hasDigit)
        }

        Button(
            onClick = {
                savedPassword = typingPassword
                onSave(typingPassword)
            },
            enabled = conditionsMet == 4,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = strengthColor)
        ) {
            Text("Lưu mật khẩu an toàn")
        }

        if (savedPassword.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Text(
                    text = "Mật khẩu đã lưu: $savedPassword",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun SecurityCriteriaItem(text: String, isMet: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isMet) Color(0xFF4CAF50) else Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isMet) Color.Black else Color.Gray
        )
    }
}
@Composable
fun VerticalDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Dòng này cực kỳ quan trọng: ép Row cao bằng nội dung bên trong
            .height(IntrinsicSize.Min)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Mục bên trái")

        // Sửa lại Divider để dễ thấy hơn
        VerticalDivider(
            modifier = Modifier
                .width(2.dp) // Tăng độ dày nếu muốn nhìn rõ
                .fillMaxHeight(), // Bây giờ nó sẽ cao bằng Text nhờ IntrinsicSize.Min
            color = Color.Red // Thử đổi sang màu đỏ để kiểm tra xem nó có hiện không
        )

        Text("Mục bên phải")
    }
}
@Composable
fun TextFieldDemo(
    text: String,
    onSave: (String) -> Unit
) {
    var typingText by rememberSaveable { mutableStateOf("") }
    var savedText by rememberSaveable { mutableStateOf(text) }
    var isSaved by rememberSaveable { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "TextField Components",
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = typingText,
            onValueChange = {
                typingText = it
                isSaved = false   // 👉 đang nhập thì ẩn dòng đỏ
            },
            label = { Text("Thông tin nhập") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    savedText = typingText
                    onSave(typingText)
                    isSaved = true   // 👉 ấn Enter xong mới hiện
                }
            )
        )

        // 👉 Chỉ hiện sau khi đã lưu
        if (isSaved) {
            Text(
                text = "Tự động cập nhật dữ liệu theo textfield",
                color = Color.Red,
                fontSize = 13.sp
            )
        }

        if (savedText.isNotEmpty()) {
            Text(
                text = "Dữ liệu đã nhập: $savedText",
                color = Color.Blue,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun CheckboxDemo() {

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /* ---------- Checkbox đơn ---------- */

        var checked by rememberSaveable { mutableStateOf(true) }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Minimal checkbox")
        }

        Text(
            text = if (checked) "Checkbox is checked" else "Checkbox is unchecked",
            color = Color.Gray
        )

        Divider()

        /* ---------- TriStateCheckbox ---------- */

        val childCheckedStates = rememberSaveable {
            mutableStateListOf(false, false, false)
        }

        val parentState = when {
            childCheckedStates.all { it } -> ToggleableState.On
            childCheckedStates.none { it } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Parent
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TriStateCheckbox(
                    state = parentState,
                    onClick = {
                        val newState = parentState != ToggleableState.On
                        childCheckedStates.indices.forEach {
                            childCheckedStates[it] = newState
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select all")
            }

            // Children
            childCheckedStates.forEachIndexed { index, isChecked ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { childCheckedStates[index] = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Option ${index + 1}")
                }
            }
        }

        if (childCheckedStates.all { it }) {
            Text(
                "All options selected",
                color = Color.Green,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun RadioDemo() {
    val radioOptions = listOf("Calls", "Missed", "Friends")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(modifier = Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun SwitchDemo() {
    var on by remember { mutableStateOf(false) }
    Switch(on, { on = it })
}

@Composable
fun SliderDemo() {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    Column {
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it }
        )
        Text(text = sliderPosition.toString())
    }
}

@Composable
fun RowDemo() {
    val lightColor = Color(0xFF5A8DEE)
    val darkColor = Color(0xFFB3CCF6)

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) { rowIndex -> // Lặp 3 hàng
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                repeat(3) { colIndex -> // Lặp 3 cột
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                // CHỈNH SỬA TẠI ĐÂY:
                                // Nếu là hàng thứ 2 (rowIndex == 1) thì cho sáng cả 3 ô
                                if (rowIndex == 1) lightColor else darkColor,
                                RoundedCornerShape(12.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnDemo() {
    val lightColor = Color(0xFFB3CCF6)
    val darkColor = Color(0xFF5A8DEE)


    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) { col ->
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                if (col == 1) darkColor else lightColor,
                                RoundedCornerShape(12.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun BoxDemo() {
    val lightColor = Color(0xFF5A8DEE)
    val darkColor = Color(0xFFB3CCF6)

    Box(
        modifier = Modifier
            .size(240.dp)
            .padding(16.dp)
    ) {
        repeat(9) { index ->
            val row = index / 3
            val col = index % 3

            val isBottomRight =
                (row >= 1 && col >= 1)   // 4 ô góc dưới phải

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(
                        x = (col * 90).dp,
                        y = (row * 90).dp
                    )
                    .background(
                        if (isBottomRight) lightColor else darkColor,
                        RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}

@Composable
fun LazyColumnDemo() {
    LazyColumn {
        item {
            Text(text = "First item")
        }

        items(20) { index ->
            Text(text = "Item: $index")
        }

        item {
            Text(text = "Last item")
        }
    }
}

@Composable
fun CardDemo() {
    Card() {
        Text(text = "Hello, world!")
    }
}@Composable
fun OutlinedCard() {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "Outlined",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}
@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
@Composable
fun DialogDemo() {
    val openAlertDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { openAlertDialog.value = true }
        ) {
            Text("Open Dialog")
        }

        if (openAlertDialog.value) {
            AlertDialog(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    println("Confirmation registered")
                },
                dialogTitle = "Alert dialog example",
                dialogText = "This is an example of an alert dialog with buttons.",
                icon = Icons.Default.Info
            )
        }
    }
}