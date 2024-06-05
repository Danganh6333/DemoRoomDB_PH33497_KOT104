package com.dangchph33497.fpoly.demoroomdb

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.room.Room
import com.dangchph33497.fpoly.demoroomdb.ui.theme.DemoRoomDBTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoRoomDBTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(16.dp)
                ) { innerPadding ->
                    HomeScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    var hoTen: String?
    var mssv: String?
    var diemTB: Float?

    val db = Room.databaseBuilder(
        context,
        StudentDB::class.java, "student-db"
    ).allowMainThreadQueries().build()

    var listStudents by remember {
        mutableStateOf(db.studentDAO().getAll())
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<StudentModel?>(null) }

    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Quản Lý Sinh Viên",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Button(onClick = {
                showAddDialog = true
            }) {
                Text(text = "Thêm SV")
            }
        }
        LazyColumn {
            items(listStudents) { student ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(modifier = Modifier.weight(1f), text = student.uid.toString())
                    Column(
                        modifier = Modifier.weight(3f)
                    ) {
                        Text(text = student.mssv.toString())
                        Text(text = student.hoten.toString())
                    }
                    Text(modifier = Modifier.weight(1f), text = student.diemTB.toString())
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            db.studentDAO().delete(student)
                            listStudents = db.studentDAO().getAll()
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(text = "Xóa", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            showUpdateDialog = true
                            selectedStudent = student
                        },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(text = "Cập nhật", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Divider()
            }
        }

        if (showAddDialog) {
            AddDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { student ->
                    db.studentDAO().insert(student)
                    listStudents = db.studentDAO().getAll()
                    showAddDialog = false
                }
            )
        }
        if (showUpdateDialog) {
            UpdateDialog(
                onDismiss = { showUpdateDialog = false },
                student = selectedStudent,
                onUpdate = { student ->
                    db.studentDAO().update(student)
                    listStudents = db.studentDAO().getAll()
                    showUpdateDialog = false
                })
        }
    }
}


@Composable
fun AddDialog(
    onDismiss: () -> Unit,
    onAdd: (StudentModel) -> Unit
) {
    var hoTen by remember { mutableStateOf("") }
    var mssv by remember { mutableStateOf("") }
    var diemTB by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            shape = RoundedCornerShape(5.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Thêm Sinh Viên Mới",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    value = hoTen,
                    onValueChange = { hoTen = it },
                    label = { Text(text = "Nhập họ tên sinh viên") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = mssv,
                    onValueChange = { mssv = it },
                    label = { Text(text = "Nhập mã số sinh viên") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = diemTB,
                    onValueChange = { diemTB = it },
                    label = { Text(text = "Nhập điểm trung bình sinh viên") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val diemTBValue = diemTB.toFloatOrNull() ?: 0f
                            onAdd(StudentModel(hoten = hoTen, mssv = mssv, diemTB = diemTBValue))
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Thêm")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Bỏ Qua")
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateDialog(
    onDismiss: () -> Unit,
    onUpdate: (StudentModel) -> Unit,
    student: StudentModel?
) {
    var hoTen by remember { mutableStateOf(student?.hoten ?: "") }
    var mssv by remember { mutableStateOf(student?.mssv ?: "") }
    var diemTB by remember { mutableStateOf(student?.diemTB?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            shape = RoundedCornerShape(5.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Cập Nhật Sinh Viên",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(

                    value = hoTen,
                    onValueChange = { hoTen = it },
                    label = { Text(text = "Nhập họ tên sinh viên") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = mssv,
                    onValueChange = { mssv = it },
                    label = { Text(text = "Nhập mã số sinh viên") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = diemTB,
                    onValueChange = { diemTB = it },
                    label = { Text(text = "Nhập điểm trung bình sinh viên") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val diemTBValue = diemTB.toFloatOrNull() ?: 0f
                            onUpdate(StudentModel(uid = student?.uid ?: 0, hoten = hoTen, mssv = mssv, diemTB = diemTBValue))
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Cập Nhật")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Bỏ Qua")
                    }
                }
            }
        }
    }
}
