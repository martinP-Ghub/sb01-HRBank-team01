# 🚀 HRBank - Employee Management System (1조)

> *HRBank*는 기업의 부서, 직원 변경 이력을 효율적으로 관리하는 **Spring Boot 기반의 직원 관리 시스템**입니다.
>
> 부서의 추가, 수정, 삭제 관리
>
> 직원의 추가, 수정, 삭제가 가능하며 변동사항 기록을 남기고 검색
>
> 데이터들의 시간 단위 백업, 수동 백업을 할 수 있도록 설계되었습니다.
> 


## 🛠️ 기술 스택

- **Backend**: Spring Boot, Spring Data JPA
- **Database**: PostgreSQL
- **Deployment**: Railway
- **Build Tool**: Gradle
- **Logging**: SLF4J, Logback


## 🌟 주요 기능

✅ **부서 관리**

- 부서 등록, 수정, 삭제
- 부서명, 설명 기반 검색
- 부서, 설립일 기반 정렬

✅ **직원 관리**

- 직원 등록, 수정, 삭제
- 이름, 이메일, 부서명, 직함, 상태 기반 검색
- 이름, 사번, 입사일 기반 정렬

✅ **직원 변경 로그 관리**

- 직원 추가 / 수정 / 삭제 이력을 기록
- 사번, 내용(메모), IP주소, 유형, 특정 기간 기반 검색
- 등록일, IP주소 기반 정렬

✅ **데이터 백업**

- 데이터 백업 및 다운로드
- 작업자, 상태, 특정 기간 기반 검색
- 백업 시작시간, 종료시간 기반 정렬

✅ **파일 업로드/다운로드**

- image, csv 파일 업로드 및 다운로드

✅ **페이징 & 커서 기반 조회**

- 커서 기반 페이징을 활용하여 대량의 데이터를 효율적으로 조회

✅ **자동 IP 저장**

- 변경 기록 생성 시, 요청한 사용자의 IP 자동 저장

✅ **RESTful API 제공**

- JSON 형식의 REST API 지원
- Swagger UI 문서화

## 📂 프로젝트 구조

```
HRBank
│── src/main/java/com/project/hrbank
│   ├── config         # 설정 파일
│   ├── controller     # API 컨트롤러
│   ├── dto            # DTO 클래스
│   ├── entity         # JPA 엔티티
│   ├── repository     # JPA 리포지토리
│   ├── mapper         # DTO <-> Entity 매핑
│   ├── service        # 비즈니스 로직
│   ├── util           # 유틸리티 클래스
│── src/main/resources
│   ├── application.yml  # 환경설정
│── README.md          # 프로젝트 소개
│── build.gradle       # Gradle 빌드 파일
```

## 🧑‍💻 [**프로젝트 관리 문서**](https://www.notion.so/1-1b586243d999805da7fbc6b1c3c01128?pvs=4)


## 🚀 로컬 실행 방법

1️⃣ **환경 변수 설정** (`application.yml`)

```
spring:
  datasource:
	  driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}
    username: ${PGUSER}
    password: ${PGPASSWORD}
```

2️⃣ **빌드 및 실행**

```
./gradlew clean build
java -jar build/libs/hrbank-0.0.1-SNAPSHOT.jar
```

3️⃣ **API 테스트**

- Postman 또는 Swagger UI 활용

## 📌 API 명세서

<img width="1446" alt="image" src="https://github.com/user-attachments/assets/6d115ad1-5415-44d8-9710-37ed1eddee80" />
