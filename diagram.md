온라인 도서 관리 시스템 프로그램 구조도 🏛️
이 문서는 온라인 도서 관리 시스템의 주요 클래스, 변수 및 메서드 이름을 정의합니다.

1. Model 계층
   Book 클래스 (bookmanager.model)
   역할: 도서 정보를 나타내는 객체 (POJO)

변수:

id: int

title: String

author: String

isbn: String

isAvailable: boolean

함수 (메서드) 이름:

Book(int id, String title, String author, String isbn, boolean isAvailable) (생성자)

Book(String title, String author, String isbn, boolean isAvailable) (id 없는 생성자 - add용)

getId(): int

setId(int id): void

getTitle(): String

setTitle(String title): void

getAuthor(): String

setAuthor(String author): void

getIsbn(): String

setIsbn(String isbn): void

isAvailable(): boolean

setAvailable(boolean available): void

toString(): String

equals(Object o): boolean

hashCode(): int

Member 클래스 (bookmanager.model)
역할: 회원 정보를 나타내는 객체 (POJO)

변수:

id: int

name: String

contactInfo: String

함수 (메서드) 이름:

Member(int id, String name, String contactInfo) (생성자)

Member(String name, String contactInfo) (id 없는 생성자 - add용)

getId(): int

setId(int id): void

getName(): String

setName(String name): void

getContactInfo(): String

setContactInfo(String contactInfo): void

toString(): String

equals(Object o): boolean

hashCode(): int

Loan 클래스 (bookmanager.model)
역할: 대출 기록 정보를 나타내는 객체 (POJO)

변수:

id: int

bookId: int

memberId: int

loanDate: java.time.LocalDateTime

returnDate: java.time.LocalDateTime

함수 (메서드) 이름:

Loan(int id, int bookId, int memberId, LocalDateTime loanDate, LocalDateTime returnDate) (생성자)

Loan(int bookId, int memberId, LocalDateTime loanDate, LocalDateTime returnDate) (id 없는 생성자 - add용)

getId(): int

setId(int id): void

getBookId(): int

setBookId(int bookId): void

getMemberId(): int

setMemberId(int memberId): void

getLoanDate(): java.time.LocalDateTime

setLoanDate(LocalDateTime loanDate): void

getReturnDate(): java.time.LocalDateTime

setReturnDate(LocalDateTime returnDate): void

toString(): String

equals(Object o): boolean

hashCode(): int

2. DAO (Data Access Object) 계층
   BookDao 인터페이스 (bookmanager.dao)
   역할: Book 데이터를 데이터베이스에서 CRUD 작업을 수행하기 위한 계약 (인터페이스)

함수 (메서드) 이름:

void addBook(Book book) throws SQLException

Book getBookById(int id) throws SQLException

Book getBookByIsbn(String isbn) throws SQLException

List<Book> getAllBooks() throws SQLException

void updateBook(Book book) throws SQLException

void updateBookAvailability(int bookId, boolean isAvailable, Connection conn) throws SQLException (트랜잭션용)

void deleteBook(int id) throws SQLException

BookDaoImpl 클래스 (bookmanager.dao)
역할: BookDao 인터페이스 구현체. 실제 JDBC 코드를 통해 DB와 통신

변수:

(필요시 ConnectionPoolManager 객체 참조, 하지만 대부분 메서드 내에서 getConnection() 호출)

함수 (메서드) 이름: (인터페이스의 모든 메서드를 구현하며, 추가 헬퍼 메서드 포함)

void addBook(Book book) throws SQLException

Book getBookById(int id) throws SQLException

Book getBookByIsbn(String isbn) throws SQLException

List<Book> getAllBooks() throws SQLException

void updateBook(Book book) throws SQLException

void updateBookAvailability(int bookId, boolean isAvailable, Connection conn) throws SQLException

void deleteBook(int id) throws SQLException

private Book createBookFromResultSet(ResultSet rs) throws SQLException (헬퍼)

MemberDao 인터페이스 (bookmanager.dao)
역할: 회원 데이터 CRUD 작업을 수행하기 위한 계약 (인터페이스)

함수 (메서드) 이름: (BookDao와 유사한 패턴)

void addMember(Member member) throws SQLException

Member getMemberById(int id) throws SQLException

Member getMemberByName(String name) throws SQLException

List<Member> getAllMembers() throws SQLException

void updateMember(Member member) throws SQLException

void deleteMember(int id) throws SQLException

MemberDaoImpl 클래스 (bookmanager.dao)
역할: MemberDao 인터페이스 구현체. 실제 JDBC 코드를 통해 DB와 통신

변수:

(필요시 ConnectionPoolManager 객체 참조)

함수 (메서드) 이름: (인터페이스의 모든 메서드를 구현하며, 추가 헬퍼 메서드 포함)

void addMember(Member member) throws SQLException

Member getMemberById(int id) throws SQLException

Member getMemberByName(String name) throws SQLException

List<Member> getAllMembers() throws SQLException

void updateMember(Member member) throws SQLException

void deleteMember(int id) throws SQLException

private Member createMemberFromResultSet(ResultSet rs) throws SQLException (헬퍼)

LoanDao 인터페이스 (bookmanager.dao)
역할: 대출 기록 데이터 CRUD 작업을 수행하기 위한 계약 (인터페이스)

함수 (메서드) 이름:

void addLoan(Loan loan) throws SQLException

void addLoan(int bookId, int memberId, Connection conn) throws SQLException (트랜잭션용)

Loan getLoanById(int id) throws SQLException

List<Loan> getLoansByBookId(int bookId) throws SQLException (책 ID로 여러 대출 기록 가능)

List<Loan> getLoansByMemberId(int memberId) throws SQLException (회원 ID로 여러 대출 기록 가능)

List<Loan> getAllLoans() throws SQLException

void updateLoan(Loan loan) throws SQLException

void updateLoanReturnDate(int loanId, LocalDateTime returnDate, Connection conn) throws SQLException (트랜잭션용)

void deleteLoan(int id) throws SQLException

LoanDaoImpl 클래스 (bookmanager.dao)
역할: LoanDao 인터페이스 구현체. 실제 JDBC 코드를 통해 DB와 통신

변수:

(필요시 ConnectionPoolManager 객체 참조)

함수 (메서드) 이름: (인터페이스의 모든 메서드를 구현하며, 추가 헬퍼 메서드 포함)

void addLoan(Loan loan) throws SQLException

void addLoan(int bookId, int memberId, Connection conn) throws SQLException

Loan getLoanById(int id) throws SQLException

List<Loan> getLoansByBookId(int bookId) throws SQLException

List<Loan> getLoansByMemberId(int memberId) throws SQLException

List<Loan> getAllLoans() throws SQLException

void updateLoan(Loan loan) throws SQLException

void updateLoanReturnDate(int loanId, LocalDateTime returnDate, Connection conn) throws SQLException

void deleteLoan(int id) throws SQLException

private Loan createLoanFromResultSet(ResultSet rs) throws SQLException (헬퍼, LocalDateTime null 처리 포함)

private void setTimestampOrNull(PreparedStatement pstmt, int index, LocalDateTime dateTime) throws SQLException (헬퍼)

3. Service 계층
   BookService 인터페이스 (bookmanager.service)
   역할: Book 관련 비즈니스 로직 처리 및 트랜잭션 관리의 계약

함수 (메서드) 이름:

void registerBook(Book book) throws SQLException, DuplicateIsbnException (ISBN 중복 예외 추가 고려)

Book getBookDetails(int id) throws SQLException, BookNotFoundException (책 없음 예외 추가 고려)

Book getBookDetailsByIsbn(String isbn) throws SQLException, BookNotFoundException

List<Book> searchBooks(String keyword) throws SQLException (제목/저자/ISBN 등으로 검색)

void updateBookDetails(Book book) throws SQLException, BookNotFoundException

void removeBook(int bookId) throws SQLException (책 삭제 시 비즈니스 규칙 추가 가능)

BookServiceImpl 클래스 (bookmanager.service)
역할: BookService 인터페이스 구현체. 비즈니스 로직 처리 및 DAO 조합

변수:

private final BookDao bookDao; (생성자를 통해 주입)

함수 (메서드) 이름: (인터페이스의 모든 메서드를 구현)

BookServiceImpl(BookDao bookDao) (생성자 - 의존성 주입)

void registerBook(Book book) throws SQLException, DuplicateIsbnException

Book getBookDetails(int id) throws SQLException, BookNotFoundException

Book getBookDetailsByIsbn(String isbn) throws SQLException, BookNotFoundException

List<Book> searchBooks(String keyword) throws SQLException

void updateBookDetails(Book book) throws SQLException, BookNotFoundException

void removeBook(int bookId) throws SQLException

MemberService 인터페이스 & MemberServiceImpl 클래스 (bookmanager.service)
역할: 회원 관련 비즈니스 로직 처리

변수:

private final MemberDao memberDao;

(필요시 LoanDao 등 다른 DAO 의존성)

함수 (메서드) 이름: (BookService와 유사한 패턴)

void registerMember(Member member) throws SQLException

Member getMemberDetails(int id) throws SQLException

void updateMemberInfo(Member member) throws SQLException

void removeMember(int memberId) throws SQLException

List<Member> getAllMembers() throws SQLException

LoanService 인터페이스 & LoanServiceImpl 클래스 (bookmanager.service)
역할: 대출/반납 비즈니스 로직 처리 및 트랜잭션 관리

변수:

private final LoanDao loanDao;

private final BookDao bookDao;

private final MemberDao memberDao;

함수 (메서드) 이름: (핵심 비즈니스 프로세스)

void borrowBook(int bookId, int memberId) throws SQLException, BookUnavailableException, MemberNotFoundException (트랜잭션 포함)

void returnBook(int bookId, int memberId) throws SQLException, LoanNotFoundException (트랜잭션 포함)

List<Loan> getActiveLoans() throws SQLException

List<Loan> getLoansByBookId(int bookId) throws SQLException

List<Loan> getLoansByMember(int memberId) throws SQLException

4. Utility 계층
   ConnectionPoolManager 클래스 (bookmanager.util)
   역할: 데이터베이스 커넥션 풀을 관리하고 제공

변수:

private static DataSource dataSource; (싱글톤 패턴 고려)

함수 (메서드) 이름:

init() : void (초기화)

getConnection() : Connection

close() : void (풀 종료)

CustomException 클래스 (추가 고려 - bookmanager.exception)
역할: 비즈니스 로직에서 발생하는 특정 예외들을 정의 (예: BookNotFoundException, DuplicateIsbnException, BookUnavailableException, MemberNotFoundException, LoanNotFoundException 등)

5. UI 계층 및 Main Entry Point
   MainApp 클래스 (bookmanager.ui 또는 루트 패키지)
   역할: 애플리케이션의 시작점 (main 메서드 포함), 서비스 계층 초기화 및 UI 구동

함수 (메서드) 이름:

main(String[] args) : void

MainFrame / BookPanel 등 UI 관련 클래스 (bookmanager.ui)
역할: 사용자에게 보여지는 화면 및 상호작용 처리

변수:

private BookService bookService;

private MemberService memberService;

private LoanService loanService;

함수 (메서드) 이름:

initUI() : void

loadBooks() : void

onAddBookButtonClick() : void

onBorrowBookButtonClick() : void

onReturnBookButtonClick() : void

displayErrorMessage(String message) : void

displaySuccessMessage(String message) : void