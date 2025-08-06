package bookmanager;

import bookmanager.dao.BookDaoImpl;
import bookmanager.dao.LoanDaoImpl;
import bookmanager.dao.MemberDaoImpl;
import bookmanager.model.Book;
import bookmanager.service.BookServiceImpl;
import bookmanager.service.LoanServiceImpl;
import bookmanager.service.MemberServiceImpl;
import bookmanager.util.ConnectionPoolManager;
import bookmanager.util.Dbutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public class MainApp {
    private final BufferedReader br; // 사용자 입력을 받기 위한 객체
    private final BookServiceImpl bookService;
    private final LoanServiceImpl loanService;
    private final MemberServiceImpl memberService;

    // 생성자에서 모든 DAO와 Service를 초기화
    public MainApp() {
        this.br = new BufferedReader(new InputStreamReader(System.in));

        // DAO 객체들 생성
        BookDaoImpl bookDao = new BookDaoImpl();
        LoanDaoImpl loanDao = new LoanDaoImpl();
        MemberDaoImpl memberDao = new MemberDaoImpl();

        // Service 객체들 생성 및 의존성 주입 (생성자를 통해 DAO 전달)
        this.bookService = new BookServiceImpl(bookDao, loanDao);
        this.loanService = new LoanServiceImpl(bookDao, loanDao, memberDao);
        this.memberService = new MemberServiceImpl(memberDao, loanDao);
    }

    /**
     * 애플리케이션의 메인 루프를 실행하는 메서드
     */
    public void start() {
        // 이 곳에 메인 메뉴를 표시하고 사용자 입력을 받는 루프를 구현합니다.
        try {
            while (true) {
                System.out.println("\n===== 도서 관리 시스템 =====");
                System.out.println("1. 도서 관리");
                System.out.println("2. 회원 관리");
                System.out.println("3. 대출/반납 관리");
                System.out.println("4. 프로그램 종료");
                System.out.print("메뉴를 선택하세요: ");

                String input = br.readLine();

                switch (input) {
                    case "1":
                        handleBookMenu();
                        break;
                    case "2":
                        handleMemberMenu();
                        break;
                    case "3":
                        handleLoanMenu();
                        break;
                    case "4":
                        System.out.println("프로그램을 종료합니다.");
                        return; // 프로그램을 종료
                    default:
                        System.out.println("잘못된 입력입니다. 다시 시도하세요.");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("입력 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 도서 관리 메뉴를 처리하는 메서드
     */
    private void handleBookMenu() {
        try {
            while (true) {
                System.out.println("\n===== 도서 관리 =====");
                System.out.println("1. 도서 추가");
                System.out.println("2. 도서 삭제");
                System.out.println("3. 전체 도서 목록 보기");
                System.out.println("9. 뒤로 가기");
                System.out.print("메뉴를 선택하세요: ");

                String input = br.readLine();

                switch (input) {
                    case "1":
                        addBook();
                        break;
                    case "2":
                        removeBook();
                        break;
                    case "3":
                        listAllBooks();
                        break;
                    case "9":
                        return; // 메인 메뉴로 돌아가기
                    default:
                        System.out.println("잘못된 입력입니다. 다시 시도하세요.");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("입력 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void addBook() throws IOException {
        System.out.println("\n[새 도서 추가]");
        System.out.print("제목: ");
        String title = br.readLine();
        System.out.print("저자: ");
        String author = br.readLine();
        System.out.print("isbn: ");
        String isbn = br.readLine();
        // 총 수량의 경우 registerbook에서 건드림
        //System.out.print("총 수량: ");
        //int totalCopies = Integer.parseInt(br.readLine());

        try {
            //그래서 totalCopie랑 availableCopies를 임의로 0,0으로 보냄
            Book book = new Book(0,title,author,isbn,0,0);
            bookService.registerBook(book);
            
            System.out.println("새 도서가 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            System.err.println("도서 추가 실패: " + e.getMessage());
        }
    }

    private void removeBook() throws IOException {
        System.out.println("\n[도서 삭제]");
        System.out.print("삭제할 도서의 ID를 입력하세요: ");
        try {
            int bookId = Integer.parseInt(br.readLine());
            bookService.removeBook(bookId);
            System.out.println("도서 ID " + bookId + "가 성공적으로 삭제되었습니다.");
        } catch (NumberFormatException e) {
            System.err.println("잘못된 ID 형식입니다. 숫자를 입력해주세요.");
        } catch (SQLException e) {
            System.err.println("도서 삭제 실패: " + e.getMessage());
        }
    }

    private void listAllBooks() {
        System.out.println("\n[전체 도서 목록]");
        try {
            //bookService.getAllBooks().forEach(book -> System.out.println(book.toString()));
            bookService.searchBooks("d");
        } catch (SQLException e) {
            System.err.println("도서 목록 조회 실패: " + e.getMessage());
        }
    }
    private void handleMemberMenu() {
        // 회원 관리 메뉴 로직
    }

    private void handleLoanMenu() {
        // 대출/반납 관리 메뉴 로직
    }

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // 데이터베이스 초기화
            conn = ConnectionPoolManager.getConnection();
            Dbutil.initializeDatabase(conn);
            System.out.println("데이터베이스 초기화 성공!");

            // MainApp 인스턴스 생성 및 애플리케이션 시작
            MainApp app = new MainApp();
            app.start();

        } catch (SQLException | IOException e) {
            System.err.println("애플리케이션 시작 실패: " + e.getMessage());
            e.printStackTrace();
            // 초기화 실패 시 애플리케이션 종료
            System.exit(1);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("초기화 연결 종료 실패: " + e.getMessage());
                }
            }
        }
    }
}