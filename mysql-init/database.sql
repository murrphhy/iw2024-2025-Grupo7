CREATE DATABASE IF NOT EXISTS project_backlog;
USE project_backlog;

CREATE TABLE `Users` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `isAdmin` BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE `Project` (
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
    FOREIGN KEY (applicant_id) REFERENCES Users(id),
    FOREIGN KEY (promoter_id) REFERENCES Users(id)
);

CREATE TABLE `Stakeholder_Project` (
    `user_id` BIGINT UNSIGNED NOT NULL,
    `project_id` BIGINT UNSIGNED NOT NULL,
    `financing` DOUBLE NOT NULL,
    PRIMARY KEY (user_id, project_id), 
    FOREIGN KEY (user_id) REFERENCES Users(id), 
    FOREIGN KEY (project_id) REFERENCES Project(id)
);

CREATE TABLE `Technician_Project` (
    `user_id` BIGINT UNSIGNED NOT NULL,
    `project_id` BIGINT UNSIGNED NOT NULL,
    `project_appraisal` BIGINT NOT NULL,
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES Users(id), 
    FOREIGN KEY (project_id) REFERENCES Project(id) 
);

CREATE TABLE `CIO_Project` (
    `user_id` BIGINT UNSIGNED NOT NULL,
    `project_id` BIGINT UNSIGNED NOT NULL,
    `strategic_alignment` BIGINT NOT NULL,
    PRIMARY KEY (user_id, project_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (project_id) REFERENCES Project(id)
);

CREATE TABLE `Technician` (
    `id` BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    `technical_area` VARCHAR(255) NOT NULL,
    FOREIGN KEY (`id`) REFERENCES `Users`(`id`) ON DELETE CASCADE
);

CREATE TABLE `Promoter` (
    `id` BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    `importance` BIGINT NOT NULL,
    FOREIGN KEY (`id`) REFERENCES `Users`(`id`) ON DELETE CASCADE
);

CREATE TABLE `Applicant` (
    `id` BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    `unit` VARCHAR(255) NOT NULL,
    FOREIGN KEY (`id`) REFERENCES `Users`(`id`) ON DELETE CASCADE
);

CREATE TABLE `CIO` (
    `id` BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    `position` VARCHAR(255) NOT NULL,
    FOREIGN KEY (`id`) REFERENCES `Users`(`id`) ON DELETE CASCADE
);

