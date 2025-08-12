package bookmanager;

import bookmanager.dao.BookDaoImpl;
import bookmanager.dao.LoanDaoImpl;
import bookmanager.dao.MemberDaoImpl;
import bookmanager.model.Book;
import bookmanager.model.Loan;
import bookmanager.model.Member;
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
import java.util.List;

public class MainApp {
    private final BufferedReader br; // 사용자 입력을 받기 위한 객체
    private final BookServiceImpl bookService;
    private final LoanServiceImpl loanService;
    private final MemberServiceImpl memberService;

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

    public void start() {
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
                        return;
                    default:
                        System.out.println("잘못된 입력입니다. 다시 시도하세요.");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("입력 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

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
                        return;
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
        System.out.print("ISBN: ");
        String isbn = br.readLine();

        try {
            // registerBook은 title, author, isbn만 필요
            bookService.registerBook(new Book(0, title, author, isbn, 0, 0));
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
            List<Book> books = bookService.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("등록된 도서가 없습니다.");
            } else {
                books.forEach(book -> System.out.println(book.toString()));
            }
        } catch (SQLException e) {
            System.err.println("도서 목록 조회 실패: " + e.getMessage());
        }
    }

    private void handleMemberMenu() {
        try {
            while (true) {
                System.out.println("\n===== 회원 관리 =====");
                System.out.println("1. 회원 추가");
                System.out.println("2. 회원 삭제");
                System.out.println("3. 전체 회원 목록 보기");
                System.out.println("9. 뒤로 가기");
                System.out.print("메뉴를 선택하세요: ");

                String input = br.readLine();

                switch (input) {
                    case "1":
                        addMember();
                        break;
                    case "2":
                        removeMember();
                        break;
                    case "3":
                        listAllMembers();
                        break;
                    case "9":
                        return;
                    default:
                        System.out.println("잘못된 입력입니다. 다시 시도하세요.");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("입력 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void addMember() throws IOException {
        System.out.println("\n[새 회원 추가]");
        System.out.print("이름: ");
        String name = br.readLine();
        System.out.print("연락처 (고유): ");
        String contactInfo = br.readLine();

        try {
            memberService.addMember(new Member(name, 0, contactInfo));
            System.out.println("새 회원이 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            System.err.println("회원 추가 실패: " + e.getMessage());
        }
    }

    private void removeMember() throws IOException {
        System.out.println("\n[회원 삭제]");
        System.out.print("삭제할 회원의 ID를 입력하세요: ");
        try {
            int memberId = Integer.parseInt(br.readLine());
            memberService.deleteMember(memberId);
            System.out.println("회원 ID " + memberId + "가 성공적으로 삭제되었습니다.");
        } catch (NumberFormatException e) {
            System.err.println("잘못된 ID 형식입니다. 숫자를 입력해주세요.");
        } catch (SQLException e) {
            System.err.println("회원 삭제 실패: " + e.getMessage());
        }
    }

    private void listAllMembers() {
        System.out.println("\n[전체 회원 목록]");
        try {
            List<Member> members = memberService.getAllMembers();
            if (members.isEmpty()) {
                System.out.println("등록된 회원이 없습니다.");
            } else {
                members.forEach(member -> System.out.println(member.toString()));
            }
        } catch (SQLException e) {
            System.err.println("회원 목록 조회 실패: " + e.getMessage());
        }
    }

    private void handleLoanMenu() {
        try {
            while (true) {
                System.out.println("\n===== 대출/반납 관리 =====");
                System.out.println("1. 도서 대출");
                System.out.println("2. 도서 반납");
                System.out.println("3. 전체 대출 목록 보기");
                System.out.println("9. 뒤로 가기");
                System.out.print("메뉴를 선택하세요: ");

                String input = br.readLine();

                switch (input) {
                    case "1":
                        borrowBook();
                        break;
                    case "2":
                        returnBook();
                        break;
                    case "3":
                        listAllCurrentLoans();
                        break;
                    case "9":
                        return;
                    default:
                        System.out.println("잘못된 입력입니다. 다시 시도하세요.");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("입력 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private void borrowBook() throws IOException {
        System.out.println("\n[도서 대출]");
        System.out.print("대출할 도서의 ID: ");
        try {
            int bookId = Integer.parseInt(br.readLine());
            System.out.print("회원 ID: ");
            int memberId = Integer.parseInt(br.readLine());
            loanService.borrowBook(bookId, memberId);
            System.out.println("대출이 성공적으로 처리되었습니다.");
        } catch (NumberFormatException e) {
            System.err.println("잘못된 ID 형식입니다. 숫자를 입력해주세요.");
        } catch (SQLException e) {
            System.err.println("도서 대출 실패: " + e.getMessage());
        }
    }

    private void returnBook() throws IOException {
        System.out.println("\n[도서 반납]");
        System.out.print("반납할 도서의 ID: ");
        try {
            int bookId = Integer.parseInt(br.readLine());
            System.out.print("회원 ID: ");
            int memberId = Integer.parseInt(br.readLine());
            loanService.returnBook(bookId, memberId);
            System.out.println("반납이 성공적으로 처리되었습니다.");
        } catch (NumberFormatException e) {
            System.err.println("잘못된 ID 형식입니다. 숫자를 입력해주세요.");
        } catch (SQLException e) {
            System.err.println("도서 반납 실패: " + e.getMessage());
        }
    }

    private void listAllCurrentLoans() {
        System.out.println("\n[현재 대출 목록]");
        try {
            List<Loan> loans = loanService.getActiveLoans();
            if (loans.isEmpty()) {
                System.out.println("현재 대출 중인 책이 없습니다.");
            } else {
                loans.forEach(loan -> System.out.println(loan.toString()));
            }
        } catch (SQLException e) {
            System.err.println("대출 목록 조회 실패: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection conn = null;
        try {

            ConnectionPoolManager.init();

            conn = ConnectionPoolManager.getConnection();
            Dbutil.initializeDatabase(conn);
            System.out.println("데이터베이스 초기화 성공!");

            MainApp app = new MainApp();
            app.start();

        } catch (SQLException | IOException e) {
            System.err.println("애플리케이션 시작 실패: " + e.getMessage());
            e.printStackTrace();
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