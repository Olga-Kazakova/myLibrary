// Импорт класса для чтения пользовательского ввода
import java.util.Scanner

// Интерфейс определяет контракт для работы с библиотекой
interface LibraryOperations {
    // Найти номер полки по автору (возвращает null если не найдено)
    fun findShelf(author: String): Int?

    // Добавить книгу (возвращает true если успешно)
    fun addBook(author: String, book: String): Boolean

    // Получить все книги в виде неизменяемого словаря
    fun getAllBooks(): Map<String, String>
}

// Реализация интерфейса LibraryOperations
class Library : LibraryOperations {
    // Хранилище автор -> книга (можно изменять)
    private val authorToBook = mutableMapOf(
        "Толстой" to "Война и мир",
        "Пушкин" to "Евгений Онегин"
    )

    // Список полок, где каждая полка - множество книг
    private val shelves = mutableListOf(
        mutableSetOf("Война и мир"),   // Полка 1
        mutableSetOf("Евгений Онегин")  // Полка 2
    )

    // Реализация поиска полки
    override fun findShelf(author: String): Int? {
        // Ищем книгу автора, если нет - возвращаем null
        val book = authorToBook[author] ?: return null
        // Ищем индекс полки и добавляем 1 (так как индексы с 0)
        return shelves.indexOfFirst { it.contains(book) } + 1
    }

    // Реализация добавления книги
    override fun addBook(author: String, book: String): Boolean {
        // Добавляем/обновляем запись об авторе
        authorToBook[author] = book

        // Ищем первую полку с местом (не более 2 книг на полке)
        val shelf = shelves.firstOrNull { it.size < 2 } ?: run {
            // Если нет места - создаем новую полку
            shelves.add(mutableSetOf())
            shelves.last()
        }
        // Добавляем книгу на полку
        return shelf.add(book)
    }

    // Получение копии всех книг
    override fun getAllBooks(): Map<String, String> {
        // Возвращаем неизменяемую копию
        return authorToBook.toMap()
    }
}

// Класс пользовательского интерфейса
class LibraryUI(private val library: LibraryOperations) {
    // Объект для чтения ввода
    private val scanner = Scanner(System.`in`)

    // Запуск интерфейса
    fun start() {
        while (true) {  // Бесконечный цикл меню
            printMenu()
            when (scanner.nextLine()) {  // Обработка команд
                "1" -> searchBook()
                "2" -> addNewBook()
                "3" -> showAllBooks()
                "0" -> { println("До свидания!"); return }  // Выход
                else -> println("Неверная команда!")
            }
        }
    }

    // Вывод меню
    private fun printMenu() {
        println("\n=== Библиотечная система ===")
        println("1. Найти книгу по автору")
        println("2. Добавить новую книгу")
        println("3. Показать все книги")
        println("0. Выход")
        print("Выберите действие: ")
    }

    // Поиск книги
    private fun searchBook() {
        print("Введите имя автора: ")
        val author = scanner.nextLine()
        val shelf = library.findShelf(author)
        // Красивое форматирование результата
        println(shelf?.let { "Книга находится на полке $it" }
            ?: "Книга не найдена")
    }

    // Добавление книги
    private fun addNewBook() {
        print("Введите автора: ")
        val author = scanner.nextLine()
        print("Введите название книги: ")
        val book = scanner.nextLine()

        // Обработка результата добавления
        if (library.addBook(author, book)) {
            println("Книга успешно добавлена!")
        } else {
            println("Ошибка при добавлении книги")
        }
    }

    // Показ всех книг
    private fun showAllBooks() {
        println("\n=== Список всех книг ===")
        // Красивый вывод всех книг
        library.getAllBooks().forEach { (author, book) ->
            println("$author: '$book'")
        }
    }
}

// Точка входа
fun main() {
    // Создаем библиотеку
    val library = Library()
    // Создаем интерфейс с привязкой к библиотеке
    val ui = LibraryUI(library)
    // Запускаем интерфейс
    ui.start()
}