--
-- Table structure for table `vehicles`
--
CREATE TABLE `vehicles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brand` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `plate` varchar(8) NOT NULL,
  `type` enum('TRAILER','TRUCK') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_vehicles_plate` (`plate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Table structure for table `shipments`
--

CREATE TABLE `shipments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `notes` text DEFAULT NULL,
  `number` varchar(13) NOT NULL,
  `pickup` datetime(6) NOT NULL,
  `status` enum('DELIVERED','DISPATCHED','PENDING') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by_user_id` bigint(20) NOT NULL,
  `driver_id` bigint(20) DEFAULT NULL,
  `quote_id` bigint(20) NOT NULL,
  `trailer_id` bigint(20) DEFAULT NULL,
  `truck_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shipments_number` (`number`),
  UNIQUE KEY `uk_shipments_quote_id` (`quote_id`),
  KEY `idx_shipments_created_by_user_id` (`created_by_user_id`),
  KEY `idx_shipments_driver_id` (`driver_id`),
  KEY `idx_shipments_trailer_id` (`trailer_id`),
  KEY `idx_shipments_truck_id` (`truck_id`),
  CONSTRAINT `fk_shipments_quote` FOREIGN KEY (`quote_id`) REFERENCES `quotes` (`id`),
  CONSTRAINT `fk_shipments_created_by_user` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_shipments_vehicle_trailer` FOREIGN KEY (`trailer_id`) REFERENCES `vehicles` (`id`),
  CONSTRAINT `fk_shipments_vehicle_truck` FOREIGN KEY (`truck_id`) REFERENCES `vehicles` (`id`),
  CONSTRAINT `fk_shipments_driver_user` FOREIGN KEY (`driver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;