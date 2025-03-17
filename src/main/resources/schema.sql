DROP TABLE IF EXISTS departments CASCADE;
DROP TABLE IF EXISTS files CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS backups CASCADE;
DROP TABLE IF EXISTS employee_change_logs CASCADE;

-- DEPARTMENT
CREATE TABLE departments
(
    department_id    BIGINT GENERATED ALWAYS AS IDENTITY,
    name             VARCHAR   NOT NULL UNIQUE,
    description      TEXT      NOT NULL,
    established_date DATE      NOT NULL,
    created_at       TIMESTAMP NOT NULL
);

ALTER TABLE departments
    ADD CONSTRAINT departments_department_id_pk PRIMARY KEY (department_id);
-- TODO 설명(Description)으로 조회하는 경우가 존재함. 인덱스를 설정할 것인지?
-- TODO 설립일로 정렬을 하는 경우가 많은데 어떻게 성능을 향상시킬 수 있는지?
-- TODO VARCHAR 에 길이 제한을 걸어주는것이 좋은지 ?

--  FILE
CREATE TABLE files
(
    file_id      BIGINT GENERATED ALWAYS AS IDENTITY,
    file_name    VARCHAR   NOT NULL,
    content_type VARCHAR   NOT NULL,
    size         BIGINT    NOT NULL,
    file_path    VARCHAR   NOT NULL,
    created_at   TIMESTAMP NOT NULL
);

ALTER TABLE files
    ADD CONSTRAINT files_file_id_pk PRIMARY KEY (file_id);

-- EMPLOYEE
CREATE TABLE employees
(
    employee_id      BIGINT GENERATED ALWAYS AS IDENTITY,
    name             VARCHAR   NOT NULL,
    email            VARCHAR   NOT NULL UNIQUE,
    employee_number  VARCHAR   NOT NULL,
    position         VARCHAR   NOT NULL,
    hire_date        DATE      NOT NULL,
    status           VARCHAR   NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    department_id    BIGINT    NOT NULL,
    profile_image_id BIGINT
);
-- TODO status 제약조건을 걸어둘것인가, 자바에서만 enum 타입을 이용해서 처리할 것인가? 정답은 없고! 우리팀에서 선택한 방법의 이유만 잘 이야기 해봅시다.
ALTER TABLE employees
    ADD CONSTRAINT pk_employees_employee_id PRIMARY KEY (employee_id);

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_department_id
        FOREIGN KEY (department_id)
            REFERENCES departments (department_id);

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_file_id
        FOREIGN KEY (profile_image_id)
            REFERENCES files (file_id) ON DELETE CASCADE;

-- BACKUP
CREATE TABLE backups
(
    backup_id  BIGINT GENERATED ALWAYS AS IDENTITY,
    worker     VARCHAR   NOT NULL,
    started_at TIMESTAMP NOT NULL,
    ended_at   TIMESTAMP NOT NULL,
    status     VARCHAR   NOT NULL,
    created_at TIMESTAMP NOT NULL,
    file_id    BIGINT
);

ALTER TABLE backups
    ADD CONSTRAINT pk_backups_backup_id PRIMARY KEY (backup_id);

ALTER TABLE backups
    ADD CONSTRAINT fk_backups_file_id FOREIGN KEY (file_id)
        REFERENCES files (file_id);

CREATE TABLE employee_change_logs
(
    log_id          BIGINT GENERATED ALWAYS AS IDENTITY,
    type            VARCHAR   NOT NULL,
    memo            VARCHAR,
    ip              VARCHAR   NOT NULL,
    changed_at      TIMESTAMP NOT NULL,
    changed_value   jsonb,
    employee_number VARCHAR
);

ALTER TABLE employee_change_logs
    ADD CONSTRAINT pk_employee_change_logs_log_id PRIMARY KEY (log_id);
