-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: db    Database: logistics_platform
-- ------------------------------------------------------
-- Server version	8.4.6

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_name` varchar(80) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `customer_type` enum('COMPANY','INDIVIDUAL') NOT NULL,
  `email` varchar(320) DEFAULT NULL,
  `first_name` varchar(80) NOT NULL,
  `last_name` varchar(80) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `tin` varchar(9) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4c2qg4bqms7fw0wrn2a7luqkp` (`company_name`),
  UNIQUE KEY `UKpvdtk8brnvukorsnpj1bbqpk5` (`tin`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quote_items`
--

DROP TABLE IF EXISTS `quote_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quote_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `name` varchar(80) NOT NULL,
  `price` decimal(19,2) NOT NULL,
  `quantity` int NOT NULL,
  `unit` enum('HOUR','PALLET','PIECE') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `quote_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrvsmoef7yontnlu1lwxrb0g3g` (`quote_id`),
  CONSTRAINT `FKrvsmoef7yontnlu1lwxrb0g3g` FOREIGN KEY (`quote_id`) REFERENCES `quotes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quotes`
--

DROP TABLE IF EXISTS `quotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quotes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `destination` varchar(255) NOT NULL,
  `expiration_date` date NOT NULL,
  `gross_price` decimal(19,2) NOT NULL,
  `issue_date` date NOT NULL,
  `net_price` decimal(19,2) NOT NULL,
  `notes` text,
  `number` varchar(255) NOT NULL,
  `origin` varchar(255) NOT NULL,
  `quote_status` enum('ACCEPTED','DRAFT','EXPIRED','SENT') NOT NULL,
  `special_terms` text,
  `tax_rate_percentage` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `validity_days` int NOT NULL,
  `vat_amount` decimal(19,2) NOT NULL,
  `customer_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6xnt1yuqlpv2irqg6dj9mvbnp` (`number`),
  KEY `FK20tdeugogrqtgo2wvlt4v7fq2` (`customer_id`),
  KEY `FKbwr7u11tqw8jekeijdbngy3ku` (`user_id`),
  CONSTRAINT `FK20tdeugogrqtgo2wvlt4v7fq2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
  CONSTRAINT `FKbwr7u11tqw8jekeijdbngy3ku` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(30) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_editable` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_tokens`
--

DROP TABLE IF EXISTS `user_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `token` varchar(256) NOT NULL,
  `type` enum('CREATE_PASSWORD','RESET_PASSWORD') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK61iiu6gfevpvo2v3yl76sar7r` (`user_id`),
  CONSTRAINT `FK61iiu6gfevpvo2v3yl76sar7r` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(320) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `password` varchar(100) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `status` enum('ACTIVE','INVITED','SUSPENDED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'logistics_platform'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-14  4:21:37
