package com.example.requisitafacilofc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.painterResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log


@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
)

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun getUser(username: String, password: String): User?
}


@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "requisita_facil_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("main_menu") { MainMenuScreen(navController, username = "Lucas") }
        composable("generate_request") { GenerateRequestScreen(navController = navController) } // Aqui está correto
        composable("consult_request") { ConsultRequestScreen() }
        composable("generate_report") { GenerateReportScreen() }
        composable("analyze_request") { AnalyzeRequestScreen() }
        composable("approved_requests") { ApprovedRequestsScreen() }
        composable("users") { UsersScreen() }
    }
}


@Composable
fun UsersScreen() {
    TODO("Not yet implemented")
}

@Composable
fun ApprovedRequestsScreen() {
    TODO("Not yet implemented")
}

@Composable
fun AnalyzeRequestScreen() {
    TODO("Not yet implemented")
}

@Composable
fun ConsultRequestScreen() {
    TODO("Not yet implemented")
}

@Composable
fun GenerateReportScreen() {
    TODO("Not yet implemented")
}


@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") } // Estado para armazenar mensagens de erro

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context) // Cria a instância do banco de dados

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF000000), // Preto
                        Color(0xFF8B0000), // Vermelho escuro
                        Color(0xFFFF0000)  // Vermelho forte
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Exibe o logo antes do texto "Login"
            Image(
                painter = painterResource(id = R.drawable.arpex), // Substitua "logo" pelo nome do seu arquivo
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp) // Defina o tamanho da imagem conforme necessário
                    .clip(RoundedCornerShape(19.dp)) // Aplica bordas arredondadas
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espaço entre a imagem e o texto

            Text(
                text = "Login",
                color = Color.White,
                fontSize = 34.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Usuário
            StyledTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Usuário"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Senha
            StyledTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Senha",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exibe a mensagem de erro, se houver
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Botão de Entrar
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val userDao = database.userDao()
                            val user = userDao.getUser(username, password)

                            if (user != null) {
                                // Mude para o contexto principal para navegar
                                withContext(Dispatchers.Main) {
                                    navController.navigate("main_menu")
                                }
                            } else {
                                // Atualize a mensagem de erro no contexto principal
                                withContext(Dispatchers.Main) {
                                    errorMessage = "Usuário ou senha inválidos"
                                }
                            }
                        } catch (e: Exception) {
                            // Lide com qualquer erro inesperado
                            withContext(Dispatchers.Main) {
                                errorMessage = "Erro ao tentar fazer login: ${e.message}"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp), // Defina uma altura maior para o botão
                shape = RoundedCornerShape(3.dp), // Borda quadrada,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // Cor do fundo do botão
                    contentColor = Color.White  // Cor do texto do botão
                )
            ) {
                Text(text = "Entrar", fontSize = 16.sp)
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Botão para Navegar para Registro
            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text(text = "Não tem conta? Registre-se", color = Color.White)
            }
        }
    }
}

@Composable
fun RegisterScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current // To get the application context

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF000000), // Preto
                        Color(0xFF8B0000), // Vermelho escuro
                        Color(0xFFFF0000)  // Vermelho forte
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Exibe o logo no topo da tela
            Image(
                painter = painterResource(id = R.drawable.arpex), // Substitua pelo nome do seu recurso
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp) // Ajuste o tamanho da imagem
                    .clip(RoundedCornerShape(19.dp)) // Bordas arredondadas no logo
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espaço entre o logo e o texto

            Text(
                text = "Crie sua conta",
                color = Color.White,
                fontSize = 34.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de Nome de Usuário
            StyledTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Nome de usuário"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Email
            StyledTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "E-mail"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de Senha
            StyledTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Senha",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de Registro
            Button(
                onClick = {
                    val newUser = User(username = username, email = email, password = password)

                    // Get database instance and insert user in background thread
                    val database = AppDatabase.getDatabase(context) // Pass the context here
                    val userDao = database.userDao()

                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(newUser)
                    }

                    navController.navigate("login") // Navigate to login after registration
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp),
                shape = RoundedCornerShape(3.dp), // Estilo quadrado
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // Fundo vermelho
                    contentColor = Color.White  // Texto branco
                )
            ) {
                Text(text = "Registrar", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão para Voltar ao Login
            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(text = "Já tem uma conta? Faça login", color = Color.White)
            }
        }
    }
}


@Composable
fun MainMenuScreen(navController: NavHostController, username: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Olá, $username",
                    color = Color.Black,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(top = 25.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.arpexlogo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(90.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Menu Principal",
                color = Color.Black,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            val buttons = listOf(
                "Gerar Requisição" to "generate_request", // Vinculado à tela correta
                "Consultar Requisição" to "consult_request",
                "Gerar Relatório" to "generate_report",
                "Análise de Requisição" to "analyze_request",
                "Requisições Aprovadas" to "approved_requests",
                "Usuários" to "users"
            )

            buttons.forEach { (label, route) ->
                Button(
                    onClick = { navController.navigate(route) },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = label)
                }
            }
        }
    }
}

// GenerateRequest Screen
@Composable
fun GenerateRequestScreen(navController: NavHostController) {
    var motivo by remember { mutableStateOf("") }
    var itensDesejados by remember { mutableStateOf("") }
    var tipoItem by remember { mutableStateOf("") }
    var itensDevolvidos by remember { mutableStateOf("") }
    var aprovador by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF000000),
                        Color(0xFF8B0000),
                        Color(0xFFFF0000)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate("main_menu") }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_revert), // Ícone padrão do Android
                        contentDescription = "Voltar",
                        tint = Color.White
                    )

                }
                Text(
                    text = "Nova Requisição",
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            StyledTextField(value = motivo, onValueChange = { motivo = it }, placeholder = "Motivo")
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(value = itensDesejados, onValueChange = { itensDesejados = it }, placeholder = "Itens desejados")
            Spacer(modifier = Modifier.height(16.dp))
            DropdownMenuField(options = listOf("EPI", "Ferramentas"), selectedOption = tipoItem, onOptionSelected = { tipoItem = it }, label = "Tipo de Item")
            Spacer(modifier = Modifier.height(16.dp))
            DropdownMenuField(options = listOf("Sim", "Não"), selectedOption = itensDevolvidos, onOptionSelected = { itensDevolvidos = it }, label = "Há itens a serem devolvidos?")
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(value = aprovador, onValueChange = { aprovador = it }, placeholder = "Aprovador")
            Spacer(modifier = Modifier.height(16.dp))
            StyledTextField(value = descricao, onValueChange = { descricao = it }, placeholder = "Breve descrição da requisição")
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Lógica para salvar a requisição */ },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Salvar Requisição")
            }
        }
    }
}


// Composable Auxiliares
@Composable
fun DropdownMenuField(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            color = Color.White,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .background(Color.White)
                .clip(RoundedCornerShape(4.dp))
                .clickable { expanded = !expanded }
                .padding(12.dp)
        ) {
            Text(text = if (selectedOption.isEmpty()) "Selecione" else selectedOption)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) }, // Text agora é passado diretamente
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    val visualTransformation =
        (if (isPassword) PasswordVisualTransformation() else VisualTransformation.None).apply {

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(text = placeholder, color = Color.Gray) }, // Cor do texto placeholder
                visualTransformation = this,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFFFFFFF), // Fundo cinza do campo
                    focusedTextColor = Color.Black, // Cor do texto quando o campo está focado
                    unfocusedTextColor = Color.Black, // Cor do texto quando o campo não está focado
                    focusedBorderColor = Color.Red, // Cor da borda quando o campo está focado
                    unfocusedBorderColor = Color.Gray, // Cor da borda quando o campo não está focado
                    cursorColor = Color.Red, // Cor do cursor
                    focusedPlaceholderColor = Color.Gray, // Placeholder quando o campo está focado
                    unfocusedPlaceholderColor = Color.Gray // Placeholder quando o campo não está focado
                ))
        }
}
