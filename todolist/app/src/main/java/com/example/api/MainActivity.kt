package com.example.api

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlin.collections.emptyList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.api.data.local.AppDatabase
import com.example.api.data.local.TaskEntity
import com.example.api.data.local.toTask
import com.example.api.repository.TaskRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class API<T>(
    val isSuccess: Boolean,
    val data: T,
    val message: String
)

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val dueDate: String,
    val category: String,
    val createdAt: String,
    val updatedAt: String,
    val subtasks: List<Subtask>,
    val attachments: List<Attachment>,
    val reminders: List<Reminder>,
)

data class Attachment(val id: Int,val fileName: String,val fileUrl: String)
data class Subtask(val id: Int,val title: String,val isCompleted: Boolean)
data class Reminder(val id: Int,val time: String,val type: String)

// ================== API ==================

interface APIService {

    @GET("tasks")
    suspend fun getTasks(): API<List<Task>>

    @DELETE("task/{id}")
    suspend fun deleteTask(@Path("id") id: Int): API<Unit>
}


// ================== RETROFIT ==================

object RetrofitClient{
    private const val BASE_URL="https://amock.io/api/researchUTH/"

    val api:APIService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}
// ================== VIEWMODEL ==================

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = TaskRepository(
        dao = AppDatabase.getDB(application).taskDao(),
        api = RetrofitClient.api // <--- THÊM DÒNG NÀY
    )

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _taskDetail =
        MutableLiveData<Task?>()
    val taskDetail: LiveData<Task?> = _taskDetail

    fun loadTasks() {
        viewModelScope.launch {
            var entities = repo.getAll()
            _tasks.value = entities.map { it.toTask() }
            repo.refreshTasksFromApi()
            entities = repo.getAll()
            _tasks.value = entities.map { it.toTask() }
        }
    }

    fun deleteTask(id:Int){
        viewModelScope.launch {
            repo.deleteById(id)
            loadTasks()
        }
    }

    fun addTask(title: String, desc: String) {
        viewModelScope.launch {
            val entity = TaskEntity(
                id = 0, // 0 để Room tự tăng ID
                title = title,
                description = desc,
                status = "Pending", // Mặc định là Pending để hiện màu Vàng
                priority = "Low",
                dueDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date()),
                category = "General"
            )

            // Gọi Repo lưu vào DB
            repo.insert(entity)

            loadTasks()
        }
    }

    fun loadTaskDetail(id:Int){
        _taskDetail.value =
            _tasks.value?.find { it.id == id }
    }
}



// ================== ACTIVITY ==================

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContent{
            val vm:TaskViewModel=viewModel()
            MainNav(vm)
        }
    }
}
@Composable
fun MainNav(viewModel: TaskViewModel) {
    val nav = rememberNavController()

    // Sửa lại startDestination là "list"
    NavHost(navController = nav, startDestination = "list") {

        // Màn hình danh sách
        composable("list") {
            TaskListScreen(vm = viewModel, nav = nav) // Truyền nav vào đây
        }

        // Màn hình chi tiết
        composable(
            "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) {
            val id = it.arguments!!.getInt("id")
            TaskDetailScreen(viewModel, id) {
                nav.popBackStack()
            }
        }

        // --- THÊM PHẦN NÀY: Màn hình Add Task ---
        composable("add") {
            AddTaskScreen(vm = viewModel) {
                nav.popBackStack() // Khi save xong thì quay lại
            }
        }
    }
}
@Composable
fun TaskListScreen(
    vm: TaskViewModel,
    nav: androidx.navigation.NavController // Nhận NavController
) {
    val tasks by vm.tasks.observeAsState(emptyList())

    LaunchedEffect(Unit) { vm.loadTasks() }

    if (tasks.isEmpty()) {
        // Giao diện khi chưa có task, cũng cần nút Add để thêm task đầu tiên
        TaskEmtyScreen { nav.navigate("add") }
    } else {
        HomeScreen(
            tasks = tasks,
            onItemClick = { nav.navigate("detail/$it") },
            onAddClick = { nav.navigate("add") } // Sự kiện chuyển màn hình
        )
    }
}

@Composable
fun HomeScreen(
    tasks: List<Task>,
    onItemClick: (Int) -> Unit,
    onAddClick: () -> Unit // Thêm tham số này
) {
    Scaffold(
        topBar = { TopbarScreen() },
        bottomBar = {
            BottomBar(
                selectedIndex = 0,
                onAdd = onAddClick // Gắn sự kiện vào đây
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onClick = { onItemClick(task.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopbarScreen() {
    TopAppBar(
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.uth),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF74CFFD))
                    .padding(4.dp)
            )
        },
        title = {
            Column {
                Text(
                    text = "SmartTasks",
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "A simple and efficient to-do app",
                    fontSize = 12.sp,
                    color = Color(0xFF9CB8FF)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* notification */ }) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.notifications_active_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                    ),
                    contentDescription = null,
                    tint = Color(0xFFFFC107)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}
fun TaskColor(status: String): Color {
    return when {
        status.equals("In Progress", ignoreCase = true) -> Color(0xFFFCA2A2) // Màu hồng
        status.equals("Pending", ignoreCase = true) -> Color(0xFFFFED57) // Màu vàng
        status.equals("Completed", ignoreCase = true) -> Color(0xFFC8E6C9) // Màu xanh lá (thêm vào)
        else -> Color(0xFF74CFFD)
    }
}
@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(TaskColor(task.status))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.status == "In Progress",
                onCheckedChange = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = task.description,
            color = Color.DarkGray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Status: ${task.status}", fontSize = 12.sp)
            Text(
                text = formatDate(task.dueDate)
            )
        }
    }
}

fun formatDate(isoDate: String): String {
    return try {
        val input = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.getDefault()
        )
        input.timeZone = TimeZone.getTimeZone("UTC")

        val output = SimpleDateFormat(
            "HH:mm yyyy-MM-dd",
            Locale.getDefault()
        )

        val date = input.parse(isoDate)
        output.format(date!!)
    } catch (e: Exception) {
        isoDate
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEmtyScreen(
    onBack: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (
                    event.type == KeyEventType.KeyUp &&
                    (event.key == Key.Back || event.key == Key.DirectionLeft)
                ) {
                    onBack()
                    true
                } else {
                    false
                }
            }
    ) {

        TopAppBar(
            title = {
                Text(
                    text = "List",
                    color = Color.Blue,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Color(0xFF2196F3),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_arrow_back_ios_24),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.task),
                modifier = Modifier.size(200.dp),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No Tasks Yet!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Stay productive - add something todo", fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    vm:TaskViewModel,
    id:Int,
    onBack:()->Unit
) {
    val task by vm.taskDetail.observeAsState()

    LaunchedEffect(id){vm.loadTaskDetail(id)}

    if(task==null){
        Text("Loading...")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Detail",
                            color = Color.Blue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2196F3))
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            vm.deleteTask(id)
                            onBack()
                        },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2196F3))
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.delete_24dp_e3e3e3_fill0_wght400_grad0_opsz24
                            ),
                            contentDescription = "Delete",
                            tint = Color.Yellow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        if (task == null)
        {
            TaskEmtyScreen { onBack }
        } else {
            TaskDetailContent(
                task = task!!,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
            )
        }
    }
}
@Composable
fun DetailInfoCard(
    category: String,
    status: String,
    priority: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.09f)
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF3CFCF)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InforItem(
        icon = R.drawable.outline_category_24,
        title = "Category",
        value = category
    )
        InforItem(
            icon = R.drawable.assignment_24dp_e3e3e3_fill0_wght400_grad0_opsz24,
            title = "Status",
            value = status
        )
        InforItem(
            icon = R.drawable.workspace_premium_24dp_e3e3e3_fill0_wght400_grad0_opsz24,
            title = "Priority",
            value = priority
        )
    }
}

@Composable
fun InforItem(
    icon: Int,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
@Composable
fun TaskDetailContent(
    task: Task,
    modifier: Modifier = Modifier
){
    Column(modifier = modifier) {
        Text(
            text = task.title,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = task.description, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        DetailInfoCard(
            category = task.category,
            status = task.status,
            priority = task.priority
        )
        Spacer(modifier = Modifier.height(24.dp))
        SubtaskSection(task.subtasks)
        Spacer(modifier = Modifier.height(24.dp))
        AttachmentSection(task.attachments)
    }
}
@Composable
fun SubtaskSection(subtasks: List<Subtask>){
    Column {
        Text(text = "Subtasks", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        subtasks.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFFFF))
                    .padding(12.dp),
            ) {
                Checkbox(checked = it.isCompleted, onCheckedChange = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = it.title)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun AttachmentSection(attachments: List<Attachment>){
    Column {
        Text(text = "Attachments", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        attachments.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFFFF))
                    .padding(12.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_attachment_24),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = it.fileName)
            }
        }
    }
}
@Composable
fun BottomBar(
    selectedIndex: Int = 0,
    onHome: () -> Unit = {},
    onCalendar: () -> Unit = {},
    onAdd: () -> Unit = {},
    onStatus: () -> Unit = {},
    onSetting: () -> Unit = {}
) {
    Box {
        NavigationBar(
            containerColor = Color.White
        ) {
            NavigationBarItem(
                selected = selectedIndex == 0,
                onClick = onHome,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_home_24),
                        contentDescription = "Home"
                    )
                }
            )

            NavigationBarItem(
                selected = selectedIndex == 1,
                onClick = onCalendar,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_calendar_today_24),
                        contentDescription = "Calendar"
                    )
                }
            )

            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Spacer(modifier = Modifier.width(24.dp)) }
            )

            NavigationBarItem(
                selected = selectedIndex == 3,
                onClick = onStatus,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_list_alt_24),
                        contentDescription = "Status"
                    )
                }
            )

            NavigationBarItem(
                selected = selectedIndex == 4,
                onClick = onSetting,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_settings_24),
                        contentDescription = "Settings"
                    )
                }
            )
        }

        FloatingActionButton(
            onClick = onAdd,
            containerColor = Color(0xFF2196F3),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}
@Composable
fun AddTaskScreen(
    vm: TaskViewModel,
    onDone: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    // Xử lý nút Add
    val onAddClick = {
        if (title.isNotEmpty()) {
            vm.addTask(title, desc) // Gọi ViewModel để lưu vào Room DB
            onDone() // Quay lại màn hình danh sách
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text(
            "Add New Task",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onAddClick, // Gọi hàm xử lý ở trên
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Task")
        }

        // Nút Cancel (Tùy chọn)
        Spacer(Modifier.height(8.dp))
        androidx.compose.material3.TextButton(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel", color = Color.Gray)
        }
    }
}
