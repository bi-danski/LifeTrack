package com.example.lifetrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lifetrack.model.data.Kiongozi
import com.example.lifetrack.model.data.Practitioner
import com.example.lifetrack.model.data.User
import com.example.lifetrack.model.repository.UserRepositoryImpl
import com.example.lifetrack.presenter.UserPresenter
import com.example.lifetrack.view.UserView


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Patients", "Practitioners", "Admins")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }) {
                        Text(
                            text = title,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            when (selectedTab) {
                0 -> ManagePatients()
                1 -> ManagePractitioners()
                2 -> ManageAdmins()
            }
        }
    }
}

@Composable
fun ManageAdmins() {
    val userView = remember {
        object : UserView {
            override fun showUserData(user: User) {}
            override fun updateUserUI(user: User) {}
            override fun showError(message: String) {}
            override fun showMessage(message: String) {}
            override fun onLogout() {}
        }
    }
    val userPresenter = remember { UserPresenter(userView, UserRepositoryImpl()) }
    var admins by remember { mutableStateOf(listOf<Kiongozi>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var adminToEdit by remember { mutableStateOf<Kiongozi?>(null) }

    LaunchedEffect(Unit) {
        userPresenter.getAdmins { fetchedList ->
            admins = fetchedList
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(admins.size) { index ->
                val admin = admins[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(admin.fullName, style = MaterialTheme.typography.titleMedium)
                            Text(admin.emailAddress, style = MaterialTheme.typography.bodySmall)
                        }
                        Row {
                            IconButton(onClick = {
                                adminToEdit = admin
                                showEditDialog = true
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Admin")
                            }
                            IconButton(onClick = {
                                userPresenter.deleteAdmin(admin) {
                                    admins = admins.filter { it.id != admin.id }
                                }
                            }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Admin")
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Admin")
        }
        if (showAddDialog) {
            AddAdminDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { newAdmin ->
                    userPresenter.addAdmin(newAdmin) { added ->
                        admins = admins + added
                        showAddDialog = false
                    }
                }
            )
        }
        if (showEditDialog && adminToEdit != null) {
            EditAdminDialog(
                admin = adminToEdit!!,
                onDismiss = { showEditDialog = false },
                onUpdate = { updatedAdmin ->
                    userPresenter.updateAdmin(updatedAdmin) { updated ->
                        admins = admins.map {
                            if (it.id == updated.id) updated else it
                        }
                        showEditDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun AddAdminDialog(onDismiss: () -> Unit, onAdd: (Kiongozi) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Admin") },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        onAdd(
                            Kiongozi(
                                id = java.util.UUID.randomUUID().toString(),
                                fullName = fullName,
                                emailAddress = email
                            )
                        )
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditAdminDialog(
    admin: Kiongozi,
    onDismiss: () -> Unit,
    onUpdate: (Kiongozi) -> Unit
) {
    var fullName by remember { mutableStateOf(admin.fullName) }
    var email by remember { mutableStateOf(admin.emailAddress) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Admin") },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        onUpdate(
                            admin.copy(
                                fullName = fullName,
                                emailAddress = email
                            )
                        )
                    }
                }
            ) { Text("Update") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ManagePatients() {
    val userView = remember {
        object : UserView {
            override fun showUserData(user: User) {}
            override fun updateUserUI(user: User) {}
            override fun showError(message: String) {}
            override fun showMessage(message: String) {}
            override fun onLogout() {}
        }
    }
    val userPresenter = remember { UserPresenter(userView, UserRepositoryImpl()) }
    var patients by remember { mutableStateOf(listOf<User>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var patientToEdit by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        userPresenter.getPatients { fetchedList ->
            patients = fetchedList
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(patients.size) { index ->
                val patient = patients[index]
                PatientCard(
                    user = patient,
                    onDelete = {
                        userPresenter.deletePatient(patient) {
                            patients = patients.filter { it.uuid != patient.uuid }
                        }
                    },
                    onUpdate = {
                        patientToEdit = patient
                        showEditDialog = true
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Patient")
        }

        if (showAddDialog) {
            AddPatientDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { newPatient ->
                    userPresenter.addPatient(newPatient) { added ->
                        patients = patients + added
                        showAddDialog = false
                    }
                }
            )
        }

        if (showEditDialog && patientToEdit != null) {
            EditPatientDialog(
                patient = patientToEdit!!,
                onDismiss = { showEditDialog = false },
                onUpdate = { updatedPatient ->
                    userPresenter.updatePatient(updatedPatient) { updated ->
                        patients = patients.map {
                            if (it.uuid == updated.uuid) updated else it
                        }
                        showEditDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun AddPatientDialog(onDismiss: () -> Unit, onAdd: (User) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Patient") },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        onAdd(
                            User(
                                lifetrackId = java.util.UUID.randomUUID().toString(),
                                fullName = fullName,
                                phoneNumber = phone,
                                emailAddress = email
                            )
                        )
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditPatientDialog(
    patient: User,
    onDismiss: () -> Unit,
    onUpdate: (User) -> Unit
) {
    var fullName by remember { mutableStateOf(patient.fullName) }
    var email by remember { mutableStateOf(patient.emailAddress) }
    var phone by remember { mutableStateOf(patient.phoneNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Patient") },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        onUpdate(
                            patient.copy(
                                fullName = fullName,
                                emailAddress = email,
                                phoneNumber = phone
                            )
                        )
                    }
                }
            ) { Text("Update") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PatientCard(user: User, onDelete: () -> Unit, onUpdate: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(user.fullName, style = MaterialTheme.typography.titleMedium)
                Text(user.emailAddress, style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onUpdate) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Patient")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Patient")
                }
            }
        }
    }
}

@Composable
fun PractitionerCard(user: Practitioner, onDelete: () -> Unit, onUpdate: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(user.fullName, style = MaterialTheme.typography.titleMedium)
                Text(user.emailAddress, style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = onUpdate) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Practitioner")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Practitioner")
                }
            }
        }
    }
}

@Composable
fun AddPractitionerDialog(onDismiss: () -> Unit, onAdd: (Practitioner) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Practitioner") },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        onAdd(
                            Practitioner(
                                lifetrackId = java.util.UUID.randomUUID().toString(),
                                fullName = fullName,
                                phoneNumber = phone,
                                emailAddress = email,
                                hospitalId = java.util.UUID.randomUUID().toString()
                            )
                        )
                    }
                }
            ) { Text("Add") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditPractitionerDialog(
    practitioner: Practitioner,
    onDismiss: () -> Unit,
    onUpdate: (Practitioner) -> Unit
) {
    var fullName by remember { mutableStateOf(practitioner.fullName) }
    var email by remember { mutableStateOf(practitioner.emailAddress) }
    var phone by remember { mutableStateOf(practitioner.phoneNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Practitioner") },
        text = {
            Column {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && email.isNotBlank()) {
                        onUpdate(
                            practitioner.copy(
                                fullName = fullName,
                                emailAddress = email,
                                phoneNumber = phone
                            )
                        )
                    }
                }
            ) { Text("Update") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ManagePractitioners() {
    val userView = remember {
        object : UserView {
            override fun showUserData(user: User) {}
            override fun updateUserUI(user: User) {}
            override fun showError(message: String) {}
            override fun showMessage(message: String) {}
            override fun onLogout() {}
        }
    }
    val userPresenter = remember { UserPresenter(userView, UserRepositoryImpl()) }
    var practitioners by remember { mutableStateOf(listOf<Practitioner>()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var practitionerToEdit by remember { mutableStateOf<Practitioner?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userPresenter.getPractitioners { fetchedList ->
            practitioners = fetchedList
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(practitioners.size) { index ->
                val practitioner = practitioners[index]
                PractitionerCard(
                    user = practitioner,
                    onDelete = {
                        userPresenter.deletePractitioner(practitioner) {
                            practitioners = practitioners.filter { it.lifetrackId != practitioner.lifetrackId }
                        }
                    },
                    onUpdate = {
                        practitionerToEdit = practitioner
                        showEditDialog = true
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Practitioner")
        }

        if (showAddDialog) {
            AddPractitionerDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { newPractitioner ->
                    userPresenter.addPractitioner(newPractitioner) { added ->
                        practitioners = practitioners + added
                        showAddDialog = false
                    }
                }
            )
        }

        if (showEditDialog && practitionerToEdit != null) {
            EditPractitionerDialog(
                practitioner = practitionerToEdit!!,
                onDismiss = { showEditDialog = false },
                onUpdate = { updatedPractitioner ->
                    userPresenter.updatePractitioner(updatedPractitioner) { updated ->
                        practitioners = practitioners.map {
                            if (it.lifetrackId == updated.lifetrackId) updated else it
                        }
                        showEditDialog = false
                    }
                }
            )
        }
    }
}
