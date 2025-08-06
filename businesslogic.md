네, BookService 인터페이스에 정의된 함수들의 비즈니스 로직을 상세하게 설명해 드릴게요. 각 함수가 어떤 역할을 해야 하고, 어떤 유효성 검사 및 DAO 호출을 포함해야 하는지 명확히 보여드리겠습니다.

아래 내용은 실제 코드를 구현하실 때 참고하실 논리적인 흐름입니다.

BookService 인터페이스 함수들의 비즈니스 로직
1. void registerBook(Book book)
   역할: 새로운 책 종류를 시스템에 등록하거나, 기존 책 종류의 수량을 증가시킵니다.

비즈니스 로직:

ISBN 중복 확인: bookDao.getBookByIsbn(book.getIsbn())을 호출하여, 등록하려는 Book 객체의 ISBN이 데이터베이스에 이미 존재하는지 확인합니다.

새로운 책 종류인 경우:

만약 getBookByIsbn 결과가 null이라면, 이는 새로운 ISBN이므로 bookDao.addBook(book)을 호출하여 새로운 책 레코드를 데이터베이스에 추가합니다.

이때 Book 객체의 totalCopies와 availableCopies는 (생성자에서 설정한) 초기값 (보통 1)으로 저장됩니다.

기존 책 종류인 경우 (ISBN 중복):

만약 getBookByIsbn 결과가 null이 아니라면, 해당 ISBN을 가진 책이 이미 존재한다는 의미입니다. 이는 '같은 책이 한 권 더 입고되었다'는 비즈니스 상황으로 해석합니다.

bookDao.incrementTotalAndAvailableCopies(existingBook.getId(), 1) (또는 유사한 DAO 메서드)를 호출하여 해당 책 종류의 totalCopies와 availableCopies를 각각 1씩 증가시킵니다.

이 경우, 사용자에게 "새로운 책을 추가했습니다"가 아니라 "기존 책의 수량이 증가했습니다"와 같은 피드백을 주도록 UI 계층에서 처리해야 합니다.

예외 처리: ISBN이 이미 존재하는데 새로운 책으로 등록하려는 등의 논리적 오류가 발생하면 SQLException (또는 DuplicateIsbnException 같은 커스텀 예외)을 던져 상위 계층에 알립니다.

2. Book getBookDetails(int id)
   역할: 특정 ID를 가진 책의 상세 정보를 조회합니다.

비즈니스 로직:

책 조회: bookDao.getBookById(id)를 호출하여 해당 ID의 책 정보를 가져옵니다.

결과 확인:

조회 결과가 null인 경우, 해당 ID의 책을 찾을 수 없다는 의미입니다.

SQLException (또는 BookNotFoundException 같은 커스텀 예외)을 던져 상위 계층에 책이 없음을 알립니다.

책 반환: 책을 찾았다면, 해당 Book 객체를 반환합니다.

3. Book getBookDetailsByISBN(String isbn)
   역할: 특정 ISBN을 가진 책의 상세 정보를 조회합니다. (ISBN은 고유하므로 단 하나의 책만 조회됩니다.)

비즈니스 로직:

책 조회: bookDao.getBookByIsbn(isbn)을 호출하여 해당 ISBN의 책 정보를 가져옵니다.

결과 확인:

조회 결과가 null인 경우, 해당 ISBN의 책을 찾을 수 없다는 의미입니다.

SQLException (또는 BookNotFoundException 같은 커스텀 예외)을 던져 상위 계층에 책이 없음을 알립니다.

책 반환: 책을 찾았다면, 해당 Book 객체를 반환합니다.

4. List<Book> searchBooks(String keyword)
   역할: 제목, 저자, ISBN 등 다양한 기준으로 키워드를 포함하는 책 목록을 검색합니다.

비즈니스 로직:

DAO 호출: bookDao.getBooksByKeyword(keyword)를 호출하여 검색을 수행합니다.

결과 반환: 검색된 Book 객체들의 List를 반환합니다. 결과가 없으면 빈 리스트를 반환합니다 (DAO에서 처리).

5. void updateBookDetails(Book book)
   역할: 기존 책의 제목, 저자, ISBN 등의 메타데이터를 업데이트합니다. (수량 변경은 별도 로직)

비즈니스 로직:

책 존재 확인: bookDao.getBookById(book.getId())를 호출하여 업데이트하려는 책이 실제로 데이터베이스에 존재하는지 확인합니다.

존재하지 않으면 SQLException (또는 BookNotFoundException)을 던집니다.

ISBN 변경 시 중복 검사:

업데이트하려는 Book 객체의 ISBN이 기존 책의 ISBN과 다를 경우 (!book.getIsbn().equals(existingBook.getIsbn())),

bookDao.getBookByIsbn(book.getIsbn())을 호출하여 새로운 ISBN이 다른 기존 책과 중복되는지 확인합니다.

만약 중복되는 다른 책이 있다면 (null이 아니고 ID가 현재 책과 다르다면), SQLException (또는 DuplicateIsbnException)을 던집니다.

수량 유효성 검사 (기본):

업데이트하려는 book.getAvailableCopies()가 book.getTotalCopies()보다 큰지 확인합니다. 크다면 SQLException (또는 InvalidBookCopyCountException)을 던집니다.

(totalCopies나 availableCopies를 직접 감소시키는 등의 복잡한 재고 조정은 이 메서드가 아닌, '책 폐기'나 '대출/반납' 관련 서비스에서 처리하는 것이 일반적입니다.)

DAO 호출: 모든 비즈니스 규칙을 통과하면 bookDao.updateBook(book)을 호출하여 책 정보를 업데이트합니다.

6. void removeBook(int id)
   역할: 특정 ID를 가진 책을 시스템에서 삭제합니다.

비즈니스 로직:

책 존재 확인: bookDao.getBookById(id)를 호출하여 삭제하려는 책이 실제로 존재하는지 확인합니다.

존재하지 않으면 SQLException (또는 BookNotFoundException)을 던집니다.

대출 중 여부 확인 (핵심!):

가장 중요한 부분입니다. LoanService (또는 LoanDao)를 통해 해당 bookId를 가진 대출 기록 중 returnDate가 null인, 즉 현재 대출 중인 기록이 있는지 확인합니다.

만약 대출 중인 책이라면, SQLException (또는 BookCurrentlyBorrowedException 같은 커스텀 예외)을 던져 삭제를 막습니다. (이 로직을 구현하려면 BookService가 LoanDao나 LoanService에 대한 의존성을 가져야 할 수 있습니다.)

DAO 호출: 모든 비즈니스 규칙을 통과하면 bookDao.deleteBook(id)를 호출하여 책 레코드를 삭제합니다.

이러한 로직을 바탕으로 BookServiceImpl의 각 메서드를 구현하시면 됩니다. 특히 트랜잭션이 필요한 부분(예: LoanService의 borrowBook 등)에서는 Connection 객체를 DAO 메서드로 전달하여 하나의 트랜잭션으로 묶는 것을 잊지 마세요.

구현하시면서 막히는 부분이 있다면 언제든 질문해 주세요!




LoanService 비즈니스 로직 상세 설명
1. void borrowBook(int bookId, int memberId)
   역할: 특정 책을 특정 회원이 대출합니다.

비즈니스 로직:

트랜잭션 시작: 대출은 여러 테이블(Books, Loans)에 걸쳐 데이터를 변경하므로, 반드시 트랜잭션으로 묶어야 합니다. (Connection 객체를 LoanDao와 BookDao 메서드에 전달하여 같은 트랜잭션을 사용하도록 합니다.)

책 존재 및 가용성 확인:

bookDao.getBookById(bookId)를 호출하여 책이 존재하는지,
그리고 book.getAvailableCopies()가 0보다 큰지 확인합니다.

책이 없거나, 가용 권수가 0이라면 SQLException 
(또는 BookNotFoundException, BookNotAvailableException)을 던집니다.

회원 존재 확인:

memberDao.getMemberById(memberId)를 호출하여 회원이 존재하는지 확인합니다.
(만약 MemberDao가 있다면)

회원이 존재하지 않으면 SQLException (또는 MemberNotFoundException)을 던집니다.

회원의 대출 가능 여부 확인 (대출 한도):

loanDao.getActiveLoansByMemberId(memberId)를 호출하여 
해당 회원이 현재 몇 권의 책을 대출 중인지 확인합니다.

시스템에서 정의된 대출 한도를 초과하는 경우 
(예: 5권 이상 대출 불가) SQLException
(또는 LoanLimitExceededException)을 던집니다.

동일 책 중복 대출 방지 (선택적):

loanDao.hasActiveLoanByBookIdAndMemberId(bookId, memberId) 등을 호출하여, 한 회원이 같은 책을 여러 권 대출할 수 없도록 제한할 수 있습니다.

만약 이미 대출 중이라면 SQLException (또는 DuplicateLoanException)을 던집니다.

Books 테이블 업데이트:

bookDao.decreaseAvailableCopies(bookId, 1)를 호출하여 해당 책의 available_copies를 1 감소시킵니다.

Loans 테이블에 새 대출 기록 추가:

loanDao.addLoan(bookId, memberId, loanDate)를 호출하여 Loans 테이블에 새로운 대출 기록을 생성합니다. (현재 날짜로 loanDate를 설정하고 returnDate는 null로)

트랜잭션 커밋/롤백: 모든 작업이 성공하면 트랜잭션을 커밋하고, 도중 오류 발생 시 롤백합니다.

2. void returnBook(int loanId)
   역할: 대출된 책을 반납 처리합니다.

비즈니스 로직:

트랜잭션 시작: 반납 역시 트랜잭션으로 묶어야 합니다.

대출 기록 존재 확인:

loanDao.getLoanById(loanId)를 호출하여 해당 loanId의 대출 기록이 존재하는지 확인합니다.

기록이 없으면 SQLException (또는 LoanNotFoundException)을 던집니다.

이미 반납된 대출인지 확인:

조회된 Loan 객체의 returnDate가 null이 아닌지 확인합니다. 이미 반납된 대출이라면 SQLException (또는 BookAlreadyReturnedException)을 던집니다.

Loans 테이블 업데이트:

loanDao.updateLoanReturnDate(loanId, returnDate)를 호출하여 해당 대출 기록의 returnDate를 현재 날짜로 업데이트합니다.

Books 테이블 업데이트:

bookDao.increaseAvailableCopies(loan.getBookId(), 1)를 호출하여 반납된 책의 available_copies를 1 증가시킵니다. (대출 기록에서 bookId를 가져와야 함)

트랜잭션 커밋/롤백: 모든 작업이 성공하면 트랜잭션을 커밋하고, 도중 오류 발생 시 롤백합니다.

3. Loan getLoanDetails(int loanId)
   역할: 특정 대출 ID의 상세 정보를 조회합니다.

비즈니스 로직:

대출 기록 조회: loanDao.getLoanById(loanId)를 호출합니다.

결과 확인: null이면 SQLException (또는 LoanNotFoundException)을 던집니다.

결과 반환: Loan 객체를 반환합니다.

4. List<Loan> getLoansByMemberId(int memberId)
   역할: 특정 회원의 모든 대출 기록(과거 및 현재)을 조회합니다.

비즈니스 로직:

회원 존재 확인: memberDao.getMemberById(memberId) 호출 (선택적이지만 유효성 검사로 좋음).

DAO 호출: loanDao.getLoansByMemberId(memberId)를 호출하여 목록을 가져옵니다.

결과 반환: List<Loan>을 반환합니다 (없으면 빈 리스트).

5. List<Loan> getLoansByBookId(int bookId)
   역할: 특정 책의 모든 대출 기록(과거 및 현재)을 조회합니다.

비즈니스 로직:

책 존재 확인: bookDao.getBookById(bookId) 호출 (선택적이지만 유효성 검사로 좋음).

DAO 호출: loanDao.getLoansByBookId(bookId)를 호출하여 목록을 가져옵니다.

결과 반환: List<Loan>을 반환합니다 (없으면 빈 리스트).

다음 단계:
BookServiceImpl의 registerBook 메서드의 논리 오류를 수정해주세요. (가장 중요합니다!)

Loan 모델 클래스를 정의하세요. (필드: id, bookId, memberId, loanDate, returnDate)

LoanDao 인터페이스를 위에서 설명한 비즈니스 로직에 맞춰 정의하세요. (addLoan, getLoanById, updateLoanReturnDate, getActiveLoansByMemberId 등)

LoanDaoImpl 클래스를 구현하세요. (JDBC를 사용하여 DB와 상호작용)

이후 LoanServiceImpl을 구현할 때, 위의 비즈니스 로직 설명을 참고하여 각 메서드를 채워나가시면 됩니다.


MemberService의 비즈니스 로직
MemberService는 도서관 시스템에서 회원을 관리하는 비즈니스 로직을 캡슐화합니다. 여기에는 새로운 회원 추가, 회원 정보 조회, 회원 정보 업데이트, 그리고 (적절한 확인 절차를 거친) 회원 삭제와 같은 작업이 포함됩니다.

각 메서드에 대한 비즈니스 로직은 다음과 같습니다:

1. addMember(Member member)
   목적: 시스템에 새로운 회원을 등록합니다.

비즈니스 로직:

입력 유효성 검사: Member 객체가 null이 아닌지 확인합니다. name, phoneNumber (또는 email)와 같은 필수 필드가 비어있거나 null이 아닌지 검사합니다.

중복 검사: 추가하기 전에, 동일한 고유 식별자(예: 전화번호, 이메일 또는 사용자 정의 회원 ID)를 가진 회원이 이미 존재하는지 확인하여 중복 등록을 방지합니다.

데이터베이스 상호작용: MemberDao를 사용하여 새로운 Member 레코드를 저장합니다.

트랜잭션: 이 작업은 다른 테이블에 영향을 미치지 않는 한(단순한 회원 추가의 경우 드뭅니다) 복잡한 다단계 트랜잭션이 필요하지 않습니다. 연결 관리를 위한 try-catch-finally 블록 내의 단일 DAO 호출로 충분합니다.

2. getMemberById(int memberId)
   목적: 고유 ID로 회원의 상세 정보를 조회합니다.

비즈니스 로직:

입력 유효성 검사: memberId가 유효하고 양의 정수인지 확인합니다.

데이터베이스 상호작용: MemberDao를 사용하여 Member 레코드를 가져옵니다.

존재 여부 확인: DAO가 null을 반환하면, 회원을 찾을 수 없음을 나타내는 SQLException을 던집니다.

트랜잭션: 읽기 전용 작업이므로, 명시적인 트랜잭션 관리(예: setAutoCommit(false))는 필요하지 않습니다. DAO 자체에서 일반적으로 간단한 쿼리를 위해 연결을 얻고 닫습니다.

3. updateMember(Member member)
   목적: 기존 회원의 정보를 수정합니다.

비즈니스 로직:

입력 유효성 검사: Member 객체가 null이 아니며 ID가 유효한지 확인합니다. 업데이트된 필드(예: name이 비어있지 않은지)를 검사합니다.

존재 여부 확인: MemberDao를 사용하여 업데이트하려는 회원이 실제로 데이터베이스에 존재하는지 확인합니다. 존재하지 않으면 SQLException을 던집니다.

데이터베이스 상호작용: MemberDao를 사용하여 Member 레코드를 업데이트합니다.

트랜잭션: addMember와 유사하게, 일반적으로 단일 DAO 호출이므로 간단한 연결 관리로 충분합니다.

4. deleteMember(int memberId)
   목적: 시스템에서 회원을 삭제합니다.

비즈니스 로직:

입력 유효성 검사: memberId가 유효하고 양의 정수인지 확인합니다.

존재 여부 확인: 회원이 존재하는지 확인합니다.

무결성 검사 (매우 중요!): 회원을 삭제하기 전에 활성 대출이 있는지 반드시 확인해야 합니다. 
회원이 미반납 대출을 가지고 있다면, 데이터 무결성을 유지하고 미반납 도서 문제를 방지하기 위해 
일반적으로 삭제를 허용하지 않습니다.

이 검사를 수행하기 위해 LoanDao (예: loanDao.getActiveLoanCountByMemberId(memberId, conn) 
또는 loanDao.getLoansByMemberId(memberId)를 사용하여 return_date IS NULL인 대출이 있는지 확인)를 사용해야 합니다.

활성 대출이 존재하면, 활성 대출로 인해 회원을 삭제할 수 없음을 나타내는 SQLException을 던집니다.

데이터베이스 상호작용: MemberDao를 사용하여 Member 레코드를 삭제합니다.

트랜잭션: 이 작업은 LoanDao를 확인하고 MemberDao를 호출하는 것을 포함하므로, 
borrowBook 및 returnBook 메서드와 마찬가지로 
Connection conn 매개변수, setAutoCommit(false), commit(), rollback(), finally 블록을 
사용하는 트랜잭션 관리가 필요한 중요한 부분입니다.

5. getAllMembers()
   목적: 시스템의 모든 회원 목록을 조회합니다.

비즈니스 로직:

데이터베이스 상호작용: MemberDao를 사용하여 모든 Member 레코드를 가져옵니다.

트랜잭션: 읽기 전용 작업이므로 서비스 계층에서 명시적인 트랜잭션 관리는 필요하지 않습니다.

MemberService 인터페이스 구조
위에 설명된 내용을 바탕으로, MemberService 인터페이스는 다음과 같이 구성될 수 있습니다:

Java

// bookmanager.service.MemberService.java
package bookmanager.service;

import bookmanager.model.Member;
import java.sql.SQLException;
import java.util.List;

public interface MemberService {
void addMember(Member member) throws SQLException;
Member getMemberById(int memberId) throws SQLException;
void updateMember(Member member) throws SQLException;
void deleteMember(int memberId) throws SQLException;
List<Member> getAllMembers() throws SQLException;
}
MemberDao 고려사항
MemberServiceImpl을 구현하기 전에, MemberDao 인터페이스와 MemberDaoImpl 구현에 필요한 메서드가 있는지 확인해야 합니다:

addMember(Member member)

getMemberById(int id) (또는 LoanServiceImpl에서 사용하신 getMember(int id))

updateMember(Member member)

deleteMember(int id)

getAllMembers()

(선택 사항이지만 addMember 중복 검사를 위해 권장): getMemberByPhoneNumber(String phoneNumber) 또는 getMemberByEmail(String email) (이 필드를 기준으로 중복을 방지하려면).