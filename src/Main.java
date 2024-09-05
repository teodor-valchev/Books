import Model.Book;

public class Main {
    public static void main(String[] args) {
        try {
            BookController controller = new BookController();

            // Пример: Добавяне на книга
            Book book = new Book(0, "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 10.99);
            controller.addBook(book);

            // Пример: Извличане на всички книги
            System.out.println("All books:");
            controller.getAllBooks().forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    }