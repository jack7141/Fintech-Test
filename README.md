# 프로젝트 이름

## 소개
이 프로젝트는 최신 기술 스택을 사용하여 개발된 Spring Boot 애플리케이션입니다. Java 또는 Kotlin 언어로 작성되었으며, 내장된 H2 데이터베이스와 JUnit을 사용하여 단위 테스트를 수행합니다. 빌드는 Gradle을 통해 관리됩니다.

## 기술 스택
- **개발 언어**: Java 17 이상
- **프레임워크**: Spring Boot 3.0 이상
- **데이터베이스**: H2 Embedded DB
- **단위 테스트**: JUnit
- **빌드 도구**: Gradle
- **IDE**: IntelliJ IDEA

## 설치 및 설정

### 필수 조건
다음 소프트웨어가 설치되어 있어야 합니다:
- [Java 17 이상](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
- [Gradle](https://gradle.org/install/)

### H2 데이터베이스 설정
이 프로젝트는 개발 및 테스트를 위한 내장형 H2 데이터베이스를 사용합니다. 추가 설정 없이 사용할 수 있습니다.

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 단위 테스트 실행
```sh
./gradlew test
```
이 명령어를 통해 모든 단위 테스트가 실행됩니다.

## 주요 기능
- **사용자 관리**: 사용자 생성, 조회, 수정, 삭제 기능
- **상품 관리**: 상품 등록, 조회, 수정, 삭제 기능
- **주문 관리**: 주문 생성, 조회, 수정, 삭제 기능
- **보고서 생성**: 다양한 조건에 따른 정렬 및 필터링 기능

## 프로젝트 구조
```plaintext
src/
│── main/
│   ├── java/
│   │   └── com/
│   │       └── moin/
│   │           └── remittance/
│   │               ├── controller/
│   │                       └── v1/
│   │               ├── core/
│   │               ├── domain/
│   │                       ├── dto/
│   │                       └── entity/
│   │               ├── exception/
│   │               ├── repository/
│   │               └── service/
│   └── resources/
│       ├── application.properties
└── test/
    ├── java/
    │   └── com/
    │       └── moin/
    │           └── remittance/
    │               ├── controller/
    │               ├── model/
    │               ├── repository/
    │               └── service/
    └── resources/
        └── application-test.properties
```


### 요청

# 회원 가입 API

## 소개

이 프로젝트는 이메일 형식의 사용자 ID로 회원 가입을 처리하는 RESTful API를 제공합니다. 사용자 비밀번호와 식별 ID는 안전하게 암호화되어 저장됩니다.

## API 개요

### 요청

회원 가입을 위해 다음과 같은 JSON 형식의 데이터를 서버로 보냅니다:

```json
{
    "userId": "user@example.com",
    "password": "UserPassword123!",
    "idType": "REG_NO",
    "idValue": "123456-1234567"
}
```

- **`userId`** (String): 이메일 형식의 사용자 ID (고유값)
- **`password`** (String): 사용자의 비밀번호 (암호화 됨)
- **`idType`** (String): 식별을 위한 ID 타입으로 `REG_NO`(개인회원) 또는 `BUSINESS_NO`(법인회원) 중 하나
- **`idValue`** (String): 식별 ID 값으로, `REG_NO`의 경우 주민등록번호, `BUSINESS_NO`의 경우 사업자 등록번호 (암호화 됨)

### 응답

회원 가입이 성공하면 다음과 같은 JSON 형식의 데이터를 반환합니다:

```json
{
    "message": "User registered successfully.",
    "userId": "user@example.com"
}
```

- **`message`** (String): 처리 결과 메시지
- **`userId`** (String): 등록된 사용자 ID

## 요구사항

- `userId`는 이메일 형식이어야 하며, 고유해야 합니다.
- 사용자의 비밀번호는 안전하게 암호화하여 저장됩니다.
- `idType`는 `REG_NO`(개인회원)와 `BUSINESS_NO`(법인회원) 중 하나의 값이어야 합니다.
- `idValue`는 `REG_NO`일 경우, 주민등록번호를 암호화하여 저장합니다.
- `idValue`는 `BUSINESS_NO`일 경우, 사업자 등록번호를 암호화하여 저장합니다.

### API 호출 예시

다음 명령어를 사용하여 회원 가입 API를 호출할 수 있습니다:

```sh
curl -X POST "http://localhost:8080/user/signup" \
-H "Content-Type: application/json" \
-d '{
    "userId": "user@example.com",
    "password": "UserPassword123!",
    "idType": "REG_NO",
    "idValue": "123456-1234567"
}'
```

# 구현 방법
> ### 레이어드 아키텍처
> - 계층간에 역할 분담
> - DB 테이블과 일치하는 데이터 형태는 Entity
> - 계층간 전송 객체 형태는 DTO로 전달


# DB 테이블
### Table
> - member: 회원
> - trade: 송금 내역, 견적서

<img src="./Images/ERD.png" style="width: 100%"><br><br>

# 회고
이번 과제를 진행하면서 java와 springBoot 개념을 리마인드 할 수 있었습니다.
또한 테스트 코드를 작성하는 과정에서 시간을 많이 할애하여, 완성도가 높은 코드를
만들려하였지만, 테스트 코드 자체를 작성하는데 많이 미숙하여 시간을 많이 할애한것 
같습니다. 그로 인해 요구 사항의 사이사이에 에러를 놓친것이 아쉽습니다.
