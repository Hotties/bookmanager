
// 패키지 경로
package bookmanager.model;

import java.util.*;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean isAvailable;


    public Book(int id, String title, String author, String isbn, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.isAvailable = isAvailable;
        this.author = author;
        this.isbn = isbn;
    }

    //데이터 베이스에 새로 저장될때의 생성자
    public Book(String title, String author, boolean isAvailable, String isbn) {
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
        this.isbn = isbn;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isAvailable() {
        return true;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        // ISBN은 책을 고유하게 식별할 수 있는 가장 좋은 필드이므로,
        // 여기서는 ISBN을 기준으로 동등성을 비교하도록 구현했습니다.
        // ID도 고유하지만, 데이터베이스에 저장되기 전에는 없을 수 있습니다.
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}


