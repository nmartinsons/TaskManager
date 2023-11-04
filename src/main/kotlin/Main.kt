//Niks Niklāvs Martinsons 221RDB485

import java.text.SimpleDateFormat
import java.util.*

data class Task(
    val title: String,
    val description: String,
    val dueDate: Date,
    val priority: Int,
    var status: String
)

class TaskManager {
    val tasks = mutableListOf<Task>()

    var taskCreated = false

    fun createTask(title: String, description: String, dueDate: Date, priority: Int, status: String) {
        val task = Task(title, description, dueDate, priority, status)
        tasks.add(task)
        taskCreated = true
    }

    fun viewTasks() {
        if (tasks.isEmpty()) {
            println()
            println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
            println()
        } else {
            for ((index, task) in tasks.withIndex()) {
                println("Task: ${index + 1}")
                println("Title: ${task.title}")
                println("Description: ${task.description}")
                println("Due Date: ${task.dueDate}")
                println("Priority: ${task.priority}")
                println("Status: ${task.status}")
                println()
            }
        }
    }

    fun updateTask(index: Int, title: String, description: String, dueDate: Date, priority: Int, status: String) {
        if (index >= 0 && index < tasks.size) {
            tasks[index] = Task(title, description, dueDate, priority, status)
            println("Task updated successfully!")
        } else {
            println("Task does not exist")
        }
    }

    fun deleteTask(index: Int) {
        if (index >= 0 && index < tasks.size) {
            tasks.removeAt(index)
            println("Task deleted successfully!")
        } else {
            println("Task does not exist.")
        }
    }

    fun sortByDueDate() {
        if (tasks.isEmpty()) {
            println()
            println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
            println()
        } else {
            tasks.sortBy { it.dueDate }
            println("Tasks sorted by due date:")
        }
    }
}

fun main() {
    val taskManager = TaskManager()

    while (true) {
        println("Task Manager Menu. Choose one option below:")
        println("1. Create Task")
        println("2. View Tasks")
        println("3. Update Task")
        println("4. Delete Task")
        println("5. Sort by Due Date")
        println("6. Exit")
        print("Enter your choice: ")

        when (readLine()) {
            "1" -> {
                println("Enter task details:")
                print("Title: ")
                val title = readLine() ?: ""
                print("Description: ")
                val description = readLine() ?: ""
                print("Due Date (dd-MM-yyyy): ")
                val dueDateString = readLine() ?: ""
                val dueDate = SimpleDateFormat("dd-MM-yyyy").parse(dueDateString)
                print("Priority: ")
                val priority = readLine()?.toIntOrNull() ?: 0
                print("Status: ")
                val status = readLine() ?: ""
                if (title.isNotBlank() && description.isNotBlank() && dueDate != null) {
                    taskManager.createTask(title, description, dueDate, priority, status)
                    println("Task created successfully!")
                } else {
                    println("Invalid input. Task creation failed.")
                }
            }
            "2" -> {
                taskManager.viewTasks()
            }
            "3" -> {
                if (taskManager.taskCreated) {
                    print("Enter the index of the task to update: ")
                    val index = readLine()?.toIntOrNull() ?: -1
                    if (index in 1..taskManager.tasks.size) {
                        println("Enter task details:")
                        print("Title: ")
                        val title = readLine() ?: ""
                        print("Description: ")
                        val description = readLine() ?: ""
                        print("Due Date (dd-MM-yyyy): ")
                        val dueDateStr = readLine() ?: ""
                        val dueDate = try {
                            SimpleDateFormat("dd-MM-yyyy").parse(dueDateStr)
                        } catch (e: Exception) {
                            null
                        }
                        print("Priority: ")
                        val priority = readLine()?.toIntOrNull() ?: 0
                        print("Status: ")
                        val status = readLine() ?: ""
                        if (title.isNotBlank() && description.isNotBlank() && dueDate != null) {
                            taskManager.updateTask(index - 1, title, description, dueDate, priority, status)
                        } else {
                            println("Invalid input. Task update failed.")
                        }
                    } else {
                        println("Invalid index. Task update failed.")
                    }
                } else {
                    println()
                    println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
                    println()
                }
            }
            "4" -> {
                if (taskManager.taskCreated) {
                    print("Enter the index of the task to delete: ")
                    val index = readLine()?.toIntOrNull() ?: -1
                    if (index in 1..taskManager.tasks.size) {
                        taskManager.deleteTask(index - 1)
                        println("Task deleted successfully!")
                    } else {
                        println("Invalid index. Task deletion failed.")
                    }
                } else {
                    println()
                    println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
                    println()
                }
            }
            "5" -> {
                taskManager.sortByDueDate()
                taskManager.viewTasks()
            }
            "6" -> {
                return
            }
            else -> {
                println("Invalid command. Try one more time.")
            }
        }
    }
}
