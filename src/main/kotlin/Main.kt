//Niks Niklāvs Martinsons 221RDB485

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.*

data class Task( //defined data class.
    val title: String,
    val description: String,
    val dueDate: Date,
    val priority: Int,
    var status: String
)

class TaskManager(private val connection: Connection) {
    val tasks = mutableListOf<Task>() //manages a list of tasks using a MutableList.
    var taskCreated = false //created to track whether any tasks have been created.
    var tasksFromDatabase = retrieveTasksFromDatabase()

    //adds new task to the list.
    fun createTask(title: String, description: String, dueDate: Date, priority: Int, status: String) {
        val task = Task(title, description, dueDate, priority, status)
        tasks.add(task)
        taskCreated = true //taskCreated value is changed to true because task has been created.
        insertTaskToDatabase(task) // Insert the task into the database
    }

    //retrieve tasks from the database.
    private fun retrieveTasksFromDatabase(): List<Task> {
        val tasks = mutableListOf<Task>()
        val selectSQL = "SELECT * FROM tasks"
        try {
            val preparedStatement = connection.prepareStatement(selectSQL)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                val title = resultSet.getString("title")
                val description = resultSet.getString("description")
                val dueDateMillis = resultSet.getLong("dueDate")
                val priority = resultSet.getInt("priority")
                val status = resultSet.getString("status")

                val formattedDueDate = Date(dueDateMillis)

                tasks.add(Task(title, description, formattedDueDate, priority, status))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tasks
    }

    //insert task into the database.
    private fun insertTaskToDatabase(task: Task) {
        val insertSQL = "INSERT INTO tasks(title, description, dueDate, priority, status) VALUES (?, ?, ?, ?, ?)"
        try {
            val preparedStatement = connection.prepareStatement(insertSQL)
            preparedStatement.setString(1, task.title)
            preparedStatement.setString(2, task.description)
            preparedStatement.setLong(3, task.dueDate.time)
            preparedStatement.setInt(4, task.priority)
            preparedStatement.setString(5, task.status)

            val rowsAffected = preparedStatement.executeUpdate()

            if (rowsAffected > 0) {
                println("Task inserted successfully!")
            } else {
                println("Task insertion failed.")
            }

            preparedStatement.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //displays the list of tasks, if no task has been created, the following message is printed out.
    fun viewTasks() {
        val tasksFromDatabase = retrieveTasksFromDatabase()

        if (tasksFromDatabase.isEmpty()) {
            println()
            println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
            println()
        } else {
            for ((id, task) in tasksFromDatabase.withIndex()) {
                println("Task ${id + 1}.")
                println("Title: ${task.title}")
                println("Description: ${task.description}")
                println("Due Date: ${task.dueDate}")
                println("Priority: ${task.priority}")
                println("Status: ${task.status}")
                println()
            }
        }
    }

    //modifies task details.
    fun updateTask(id: Int, title: String, description: String, dueDate: Date, priority: Int, status: String) {
        if (id >= 0 && id < tasks.size) { //the function checks if the provided id is within the valid range for the tasks list. It checks if index is greater than or equal to 0 and less than tasks.size, to ensure the index is within the bounds of the list.
            tasks[id] = Task(title, description, dueDate, priority, status)
            println("Task updated successfully!")
        } else {
            println("Task does not exist")
        }
    }

    //removes tasks from the list by specifying task id.
    //removes tasks from the list by specifying task index.
    fun deleteTask(index: Int) {
        if (index >= 0 && index < tasks.size) {
            tasks.removeAt(index) //removes tasks at specified index by user
            println("Task deleted successfully!")
        } else {// //if the provided index is out of range, the following message is printed out
            println("Task does not exist.")
        }
    }


    //sorts task by due dates in ascending order.
    fun sortByDueDate() {
        val tasksFromDatabase = retrieveTasksFromDatabase()

        if (tasksFromDatabase.isEmpty()) {
            println()
            println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
            println()
        } else {
            val sortedTasks = tasksFromDatabase.sortedBy { it.dueDate }
            println("Tasks sorted by due date:")
            for ((id, task) in sortedTasks.withIndex()) {
                println("Task ${id + 1}.")
                println("Title: ${task.title}")
                println("Description: ${task.description}")
                println("Due Date: ${task.dueDate}")
                println("Priority: ${task.priority}")
                println("Status: ${task.status}")
                println()
            }
        }
    }

}

fun main() {
    // Establish a database connection
    val url = "jdbc:sqlite:TaskManagerData.db" // Assuming your database file is in the project root
    val connection = DriverManager.getConnection(url)
    val taskManager = TaskManager(connection) //this line creates an instance of the TaskManager class, which will be used to manage tasks.

    while (true) { //this continues running until the user explicitly chooses to exit the program by choosing option 6.
        println("Task Manager Menu. Choose one option below:") //menu is displayed to the user.
        println("1. Create Task")
        println("2. View Tasks")
        println("3. Update Task")
        println("4. Delete Task")
        println("5. Sort by Due Date")
        println("6. Exit")
        print("Enter your choice: ")

        when (readLine()) { //the user's choice is read using readLine()
            "1" -> { //choice 1 to create task
                println("Enter task details:")
                print("Title: ")
                val title = readLine() ?: "" //Elvis operator, which provides a default value in case the value on the left is null. In this case, if the conversion to an integer fails, it will be replaced with the default value of 0.
                print("Description: ")
                val description = readLine() ?: ""
                print("Due Date (dd-MM-yyyy): ")
                val dueDateString = readLine() ?: ""
                val dueDate = SimpleDateFormat("dd-MM-yyyy").parse(dueDateString)
                print("Priority: ")
                val priority = readLine()?.toIntOrNull() ?: 0 //coverts string to integer, if string is not valid. If the conversion fails id will be assigned null. Elvis operator provides default value -1
                print("Status: ")
                val status = readLine() ?: ""
                if (title.isNotBlank() && description.isNotBlank() && dueDate != null) {
                    taskManager.createTask(title, description, dueDate, priority, status)
                    println("Task created successfully!")
                } else {
                    println("Invalid input. Task creation failed.")
                }
            }
            "2" -> { //choice 2 to view tasks
                taskManager.viewTasks()
            }
            "3" -> { //choice 3 to update task
                if (taskManager.tasksFromDatabase.isNotEmpty()) {
                    print("Enter the id of the task to update: ")
                    val id = readLine()?.toIntOrNull() ?: -1
                    if (id in 1..taskManager.tasks.size) { //in operator checks if the value of id is within the range of valid indices.
                        println("Enter task details:")
                        print("Title: ")
                        val title = readLine() ?: ""
                        print("Description: ")
                        val description = readLine() ?: ""
                        print("Due Date (dd-MM-yyyy): ")
                        val dueDateStr = readLine() ?: ""
                        val dueDate = SimpleDateFormat("dd-MM-yyyy").parse(dueDateStr)
                        print("Priority: ")
                        val priority = readLine()?.toIntOrNull() ?: 0
                        print("Status: ")
                        val status = readLine() ?: ""
                        if (title.isNotBlank() && description.isNotBlank() && dueDate != null) {
                            taskManager.updateTask(id - 1, title, description, dueDate, priority, status)
                        } else {
                            println("Invalid input. Task update failed.")
                        }
                    } else {
                        println("Invalid id. Task update failed.")
                    }
                } else {
                    println()
                    println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
                    println()
                }
            }
            "4" -> { //choice 4 to delete task
                if (taskManager.tasksFromDatabase.isNotEmpty()) {
                    print("Enter the id of the task to delete: ")
                    val id = readLine()?.toIntOrNull() ?: -1
                    if (id in 1..taskManager.tasks.size) {
                        taskManager.deleteTask(id - 1) //-1 to set back id starting at 0
                        println("Task deleted successfully!")
                    } else {
                        println("Invalid id. Task deletion failed.")
                    }
                } else {
                    println()
                    println("NO TASKS HAVE BEEN CREATED. CREATE TASKS FIRST!")
                    println()
                }
            }
            "5" -> { //choice 5 to sort tasks
                taskManager.sortByDueDate()
            }
            "6" -> { //exit option, it terminates the program
                return
            }
            else -> { //if no 1,2,3,4,5,6 option is typed, the following message is printed out.
                println("Invalid command. Try one more time.")
            }
        }
    }
    connection.close()
}
