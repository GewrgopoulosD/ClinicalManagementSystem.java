CREATE DATABASE  IF NOT EXISTS `clinic` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `clinic`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: clinic
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appointment`
--

DROP TABLE IF EXISTS `appointment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointment` (
  `idAppointment` int NOT NULL AUTO_INCREMENT,
  `idCustomer` int NOT NULL,
  `idClinic` int NOT NULL,
  `idEmployee` int NOT NULL COMMENT 'Ο γιατρός που θα εξυπηρετήσει τον πελάτη',
  `appointmentDatetime` datetime NOT NULL,
  `appointmentType` varchar(50) DEFAULT NULL COMMENT 'π.χ. check-up, vaccination',
  PRIMARY KEY (`idAppointment`),
  KEY `idx_customer` (`idCustomer`),
  KEY `idx_clinic` (`idClinic`),
  KEY `idx_employee` (`idEmployee`),
  CONSTRAINT `fk_appointment_clinic` FOREIGN KEY (`idClinic`) REFERENCES `clinic` (`idClinic`) ON DELETE CASCADE,
  CONSTRAINT `fk_appointment_customer` FOREIGN KEY (`idCustomer`) REFERENCES `customer` (`idCustomer`) ON DELETE CASCADE,
  CONSTRAINT `fk_appointment_employee` FOREIGN KEY (`idEmployee`) REFERENCES `employee` (`idEmployee`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointment`
--

LOCK TABLES `appointment` WRITE;
/*!40000 ALTER TABLE `appointment` DISABLE KEYS */;
/*!40000 ALTER TABLE `appointment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clinic`
--

DROP TABLE IF EXISTS `clinic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clinic` (
  `idClinic` int NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `address` varchar(80) NOT NULL,
  PRIMARY KEY (`idClinic`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clinic`
--

LOCK TABLES `clinic` WRITE;
/*!40000 ALTER TABLE `clinic` DISABLE KEYS */;
INSERT INTO `clinic` VALUES (1,'Central Clinic','Alfeiou');
/*!40000 ALTER TABLE `clinic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `idCustomer` int NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `lastName` varchar(80) NOT NULL,
  `tel` varchar(20) NOT NULL,
  `email` varchar(120) NOT NULL,
  `password` varchar(255) NOT NULL,
  `amka` varchar(11) NOT NULL,
  PRIMARY KEY (`idCustomer`),
  UNIQUE KEY `tel_UNIQUE` (`tel`),
  UNIQUE KEY `amka` (`amka`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'Dimitris','Georgopoulos','6983274790','dim.georgopoulos1@gmail.com','$2a$10$5hDY20BksbWq55hc3.kPIOv7QMyfb9s8QqvjOoV04wwDvCV8RRbwq','02119801575'),(8,'Νίκος','Κορόμπος','6912345678','test@test.gr','$2a$10$jYVCSmu7yq.UmQNs/HK2s.MCMHDiN4oqOLA2gVM3L2fuMcB1z1F3C','12345678910'),(9,'Κώστας','Σπασοχέρης','6987654321','spasoxeris@test.com','$2a$10$Lr5FVDkheOw7FM2scIfLoOE/AuG5l1F4KyycfwT/o9CaMVgikGyGC','12345678911'),(10,'Ελένη','Κακόκεφη','6987654320','kakokefi@test.gr','$2a$10$NALJhYncd81B3nAdUFuMYuA7veqCADrTfox5QBwhbjtnovkyHOIfO','12345678912');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctorspecialization`
--

DROP TABLE IF EXISTS `doctorspecialization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctorspecialization` (
  `idSpecialization` int NOT NULL,
  `idEmployee` int NOT NULL,
  PRIMARY KEY (`idSpecialization`,`idEmployee`),
  KEY `idEmployee_fk_idx` (`idEmployee`) /*!80000 INVISIBLE */,
  KEY `idSpecialization_fk_idx` (`idSpecialization`),
  CONSTRAINT `idEmployee_fk` FOREIGN KEY (`idEmployee`) REFERENCES `employee` (`idEmployee`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idSpecialization_fk` FOREIGN KEY (`idSpecialization`) REFERENCES `specialization` (`idSpecialization`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctorspecialization`
--

LOCK TABLES `doctorspecialization` WRITE;
/*!40000 ALTER TABLE `doctorspecialization` DISABLE KEYS */;
/*!40000 ALTER TABLE `doctorspecialization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `idEmployee` int NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `lastName` varchar(80) NOT NULL,
  `tel` varchar(20) NOT NULL,
  `email` varchar(120) NOT NULL,
  `password` varchar(255) NOT NULL,
  `idClinic` int NOT NULL,
  `idRole` int NOT NULL,
  PRIMARY KEY (`idEmployee`),
  UNIQUE KEY `tel_UNIQUE` (`tel`),
  UNIQUE KEY `email` (`email`),
  KEY `idClinic_idx` (`idClinic`),
  KEY `idRole_idx` (`idRole`),
  CONSTRAINT `idClinic_fk` FOREIGN KEY (`idClinic`) REFERENCES `clinic` (`idClinic`) ON DELETE CASCADE,
  CONSTRAINT `idRole_fk` FOREIGN KEY (`idRole`) REFERENCES `role` (`idRole`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (2,'Παναγιώτης','Μονόφθαλμος','12345678911','monofthalmos@test.gr','$2a$10$NMb0aohgsYqZUtSFw54IwOwUhus1SpFmJ327xAaqK4aZMGtzYna0S',1,1),(3,'Στέλλα','Κοιμίσογλου','7895123640','koimisiglou@test.gr','$2a$10$YowIPBMyF5PLMj.vVjnq6emWjaXCpYnIOWs3NvRuQ.qj1VLMHx6VW',1,1),(4,'Γιώργος','Μαχαίρας','4562579340','maxairas@test.gr','$2a$10$FvSbRlXP17Tchq9GDwN.4.iq1RS5.rXNzpRpGpuT0mfsi1oMkKAz.',1,1),(5,'Μάριος','Φτιαξοπόδης','4723948016','ftiaxopodis@test.gr','$2a$10$vi4jo5dNDNnGvPcSeCtCU.C44/18Ko2acGDrl7zr09SN.dBxkDZyK',1,1),(6,'Νίκος','Γύφτος','1002003001','giftos@test.gr','$2a$10$zRPHocJZ5sq14StNv/M/6.8dom24rpw8MuX4nnBozV7JbMGMaIDm6',1,1);
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employeeshift`
--

DROP TABLE IF EXISTS `employeeshift`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employeeshift` (
  `idShift` int NOT NULL,
  `idEmployee` int NOT NULL,
  PRIMARY KEY (`idShift`,`idEmployee`),
  KEY `idEmployee` (`idEmployee`),
  CONSTRAINT `employeeshift_ibfk_1` FOREIGN KEY (`idShift`) REFERENCES `shifts` (`idShift`) ON DELETE CASCADE,
  CONSTRAINT `employeeshift_ibfk_2` FOREIGN KEY (`idEmployee`) REFERENCES `employee` (`idEmployee`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employeeshift`
--

LOCK TABLES `employeeshift` WRITE;
/*!40000 ALTER TABLE `employeeshift` DISABLE KEYS */;
/*!40000 ALTER TABLE `employeeshift` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `idRole` int NOT NULL AUTO_INCREMENT,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`idRole`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'Doctor');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shifts`
--

DROP TABLE IF EXISTS `shifts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shifts` (
  `idShift` int NOT NULL AUTO_INCREMENT,
  `shiftDate` date NOT NULL,
  `startTime` time NOT NULL,
  `endTime` time NOT NULL,
  PRIMARY KEY (`idShift`),
  KEY `idx_shift_date` (`shiftDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shifts`
--

LOCK TABLES `shifts` WRITE;
/*!40000 ALTER TABLE `shifts` DISABLE KEYS */;
/*!40000 ALTER TABLE `shifts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specialization`
--

DROP TABLE IF EXISTS `specialization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialization` (
  `idSpecialization` int NOT NULL AUTO_INCREMENT,
  `specialization` varchar(120) NOT NULL,
  PRIMARY KEY (`idSpecialization`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specialization`
--

LOCK TABLES `specialization` WRITE;
/*!40000 ALTER TABLE `specialization` DISABLE KEYS */;
/*!40000 ALTER TABLE `specialization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verificationcodes`
--

DROP TABLE IF EXISTS `verificationcodes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verificationcodes` (
  `verificationCode` varchar(11) NOT NULL,
  PRIMARY KEY (`verificationCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verificationcodes`
--

LOCK TABLES `verificationcodes` WRITE;
/*!40000 ALTER TABLE `verificationcodes` DISABLE KEYS */;
INSERT INTO `verificationcodes` VALUES ('12345678910');
/*!40000 ALTER TABLE `verificationcodes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-20 17:19:29
