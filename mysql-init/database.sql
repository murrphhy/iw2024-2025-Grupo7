CREATE DATABASE IF NOT EXISTS project_backlog;
USE project_backlog;

CREATE TABLE `users` (

    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `academic_position` VARCHAR(255) NULL,
    `center` VARCHAR(255) NOT NULL,
    `technical_area` VARCHAR(255) NOT NULL,
    `role` enum ('APPLICANT', 'TECHNICIAN', 'CIO', 'ADMINISTRATOR') NOT NULL DEFAULT 'APPLICANT'
);

CREATE TABLE `project` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `applicant_id` BIGINT UNSIGNED NOT NULL,
    `promoter_id` BIGINT UNSIGNED NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `short_title` VARCHAR(255) NOT NULL,
    `memory` VARCHAR(255) NOT NULL,
    `state` VARCHAR(255) NOT NULL,
    `scope` VARCHAR(255) NOT NULL,
    `start_date` DATE,
    `project_regulations` VARCHAR(255),
    `technical_specification` VARCHAR(255),
    FOREIGN KEY (applicant_id) REFERENCES users(id)
);


CREATE TABLE `stakeholder_project` (
    `user_id` BIGINT UNSIGNED NOT NULL,
    `project_id` BIGINT UNSIGNED NOT NULL,
    `financing` DOUBLE NOT NULL,
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES Project(id)
);

CREATE TABLE `technician_project` (
    `user_id` BIGINT UNSIGNED NOT NULL,
    `project_id` BIGINT UNSIGNED NOT NULL,
    `project_appraisal` BIGINT NOT NULL,
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES Project(id)
);

CREATE TABLE `support` (
    `user_id` BIGINT UNSIGNED NOT NULL,
    `project_id` BIGINT UNSIGNED NOT NULL,
    `rating` INT NOT NULL,
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES Project(id)
);
