-- phpMyAdmin SQL Dump
-- version 5.0.4deb2+deb11u1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Feb 08, 2023 at 03:26 PM
-- Server version: 10.5.15-MariaDB-0+deb11u1
-- PHP Version: 7.4.33

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `logico`
--

-- --------------------------------------------------------

--
-- Table structure for table `animali`
--

CREATE TABLE `animali` (
  `Nome` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `animali`
--

INSERT INTO `animali` (`Nome`) VALUES
('Ctenoforo'),
('Delfino'),
('Granchio'),
('Medusa'),
('Tartaruga');

-- --------------------------------------------------------

--
-- Table structure for table `avvistamenti`
--

CREATE TABLE `avvistamenti` (
  `ID` int(11) NOT NULL,
  `Data` datetime NOT NULL,
  `Numero_Esemplari` int(11) NOT NULL,
  `Vento` text DEFAULT '',
  `Mare` text DEFAULT '',
  `Note` text DEFAULT '',
  `Latid` varchar(50) NOT NULL,
  `Long` varchar(50) NOT NULL,
  `Utente_ID` int(11) NOT NULL,
  `Anima_Nome` varchar(50) DEFAULT NULL,
  `Specie_Anima_Nome` varchar(50) DEFAULT NULL,
  `Specie_Nome` varchar(50) DEFAULT NULL,
  `Eliminato` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `avvistamenti`
--

INSERT INTO `avvistamenti` (`ID`, `Data`, `Numero_Esemplari`, `Vento`, `Mare`, `Note`, `Latid`, `Long`, `Utente_ID`, `Anima_Nome`, `Specie_Anima_Nome`, `Specie_Nome`, `Eliminato`) VALUES
(2, '2023-01-02 10:36:30', 2, '22', '11', 'Due delfini un po\' strani', '44.191801', '12.419601', 2, 'Delfino', NULL, NULL, 0),
(7, '2022-11-05 11:51:29', 1, '22', '', '', '44.223071', '12.388925', 2, 'Granchio', 'Granchio', 'Blu', 0),
(8, '2022-08-01 11:52:24', 1, '', '', '', '44.229698', '12.384493', 2, 'Medusa', 'Medusa', 'Medusa cristallo', 0),
(21, '2023-02-06 20:56:00', 5, '30', '12', 'Tartarughe ninja', '44.186', '12.4385', 4, 'Tartaruga', 'Tartaruga', 'Verde', 0);

-- --------------------------------------------------------

--
-- Table structure for table `descrizioni`
--

CREATE TABLE `descrizioni` (
  `Nomenclatura_Binomiale` varchar(50) NOT NULL,
  `Descrizione` text NOT NULL,
  `Dimensione` varchar(50) DEFAULT NULL,
  `Curiosita` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `descrizioni`
--

INSERT INTO `descrizioni` (`Nomenclatura_Binomiale`, `Descrizione`, `Dimensione`, `Curiosita`) VALUES
('Mnemiopsis leidyi', 'La noce di mare (Mnemiopsis leidyi A. Agassiz, 1865) è uno ctenoforo appartenente alla famiglia Bolinopsidae. Al genere Mnemiopsis Agassiz, 1860 appartengono altre due specie: la M. gardeni e la M. mccradyi, con un\'area di distribuzione diversa; l\'opinione più accreditata è però che le tre specie siano forme zoologiche diverse della M. leidyi anche se mostrano un certo polimorfismo dovuto agli adattamenti ambientali', 'Fino 10cm', 'Specie dannosa'),
('Tursiops truncatus', 'Il tursìope (Tursiops truncatus, Montagu, 1821) è un cetaceo odontoceto appartenente alla famiglia dei Delfinidi. È una delle rare specie di delfini che sopportano la cattività; anche a causa di ciò è il più studiato e il più comune nei delfinari. È diffuso in tutti i mari del mondo, ad eccezione delle zone artiche ed antartiche e ne esistono due popolazioni distinte, una costiera ed una di mare aperto. Utilizza per cacciare la tecnica dell\'ecolocalizzazione e si nutre principalmente di pesci. Raggiunge la maturità sessuale intorno ai 12 anni e le femmine partoriscono un solo piccolo. Vive generalmente in branchi formati dalle femmine ed i piccoli, mentre i maschi possono formare delle associazioni chiamate \"alleanze\". A causa dell\'influenza dei media (il famoso delfino della serie televisiva Flipper era un tursiope), è diventato il delfino per antonomasia.', 'Circa 3m', 'Essi comunicano tra loro modulando dei fischi.');

-- --------------------------------------------------------

--
-- Table structure for table `esemplari`
--

CREATE TABLE `esemplari` (
  `ID` int(11) NOT NULL,
  `Nome` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `esemplari`
--

INSERT INTO `esemplari` (`ID`, `Nome`) VALUES
(0, 'Sconosciuto'),
(1, 'Pochi'),
(2, 'Mura'),
(3, 'Giulio');

-- --------------------------------------------------------

--
-- Table structure for table `ferite`
--

CREATE TABLE `ferite` (
  `ID` int(11) NOT NULL,
  `Descrizione_Ferita` text NOT NULL,
  `Posizione` text NOT NULL,
  `Img_rif` int(11) NOT NULL,
  `Sottoi_ID` int(11) NOT NULL,
  `Gravi_Nome` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `ferite`
--

INSERT INTO `ferite` (`ID`, `Descrizione_Ferita`, `Posizione`, `Img_rif`, `Sottoi_ID`, `Gravi_Nome`) VALUES
(19, 'Mura gli ha spezzato il ❤️  più volte', 'Cuore', 21, 1, 'Gravissima'),
(20, 'Un po\' di acidità ', 'Stomaco', 22, 1, 'Lieve');

-- --------------------------------------------------------

--
-- Table structure for table `gravita`
--

CREATE TABLE `gravita` (
  `Nome` varchar(50) NOT NULL,
  `Gravita` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `gravita`
--

INSERT INTO `gravita` (`Nome`, `Gravita`) VALUES
('Grave', 2),
('Gravissima', 3),
('Lieve', 0),
('Media', 1);

-- --------------------------------------------------------

--
-- Table structure for table `immagini`
--

CREATE TABLE `immagini` (
  `ID` int(11) NOT NULL,
  `Img` text NOT NULL,
  `Avvis_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `immagini`
--

INSERT INTO `immagini` (`ID`, `Img`, `Avvis_ID`) VALUES
(21, '63e14950b8e4f.jpg', 2),
(22, '63e15c63ea8e7.jpg', 21);

-- --------------------------------------------------------

--
-- Table structure for table `sottoimmagini`
--

CREATE TABLE `sottoimmagini` (
  `ID` int(11) NOT NULL,
  `tl_x` decimal(20,3) NOT NULL,
  `tl_y` decimal(20,3) NOT NULL,
  `br_x` decimal(20,3) NOT NULL,
  `br_y` decimal(20,3) NOT NULL,
  `Immag_ID` int(11) NOT NULL,
  `Esemp_ID` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `sottoimmagini`
--

INSERT INTO `sottoimmagini` (`ID`, `tl_x`, `tl_y`, `br_x`, `br_y`, `Immag_ID`, `Esemp_ID`) VALUES
(1, '0.845', '0.402', '0.538', '1.021', 21, 1),
(1, '0.534', '0.399', '0.624', '0.555', 22, 3),
(2, '0.220', '0.356', '0.461', '0.784', 21, 2);

-- --------------------------------------------------------

--
-- Table structure for table `specie`
--

CREATE TABLE `specie` (
  `Anima_Nome` varchar(50) NOT NULL,
  `Nome` varchar(50) NOT NULL,
  `Nomenclatura_Binomiale` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `specie`
--

INSERT INTO `specie` (`Anima_Nome`, `Nome`, `Nomenclatura_Binomiale`) VALUES
('Delfino', 'Comune', NULL),
('Delfino', 'Stenella striata', NULL),
('Granchio', 'Blu', NULL),
('Granchio', 'Comune', NULL),
('Medusa', 'Medusa cristallo', NULL),
('Medusa', 'Medusa luminosa', NULL),
('Medusa', 'Medusa quadrifoglio', NULL),
('Medusa', 'Polmone di mare', NULL),
('Tartaruga', 'Comune', NULL),
('Tartaruga', 'Liuto', NULL),
('Tartaruga', 'Verde', NULL),
('Ctenoforo', 'Noce di mare', 'Mnemiopsis leidyi'),
('Delfino', 'Tursiope', 'Tursiops truncatus');

-- --------------------------------------------------------

--
-- Table structure for table `utenti`
--

CREATE TABLE `utenti` (
  `ID` int(11) NOT NULL,
  `Nome` varchar(50) DEFAULT NULL,
  `Cognome` varchar(50) DEFAULT NULL,
  `Email` varchar(50) NOT NULL,
  `Password` text NOT NULL,
  `Key` text NOT NULL,
  `Active` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `utenti`
--

INSERT INTO `utenti` (`ID`, `Nome`, `Cognome`, `Email`, `Password`, `Key`, `Active`) VALUES
(2, 'Andrea', 'Bedei', 'andreabedei@libero.it', 'a7125bd854f4bb729b1ed478b43b51857da166699b94015b0cedd1003be7e86285527a6f464911abad9e16b58e30c0b5812aec55e6ddfc4215deb8e1237450ee', '81a0f544108f8e3e4f646e62d10f218dad865947', 1),
(3, ' ', ' ', 'a@gmail.com', 'fe2bb224e68e6c4e5c81e19874c426c7b8acdf44b6e3396b725ebf9d714a8f9bf5df951d947a743a46f30fc5a77fd98dfdd7c3841a8fa6d445255b5c9db905c3', '492a0f2849f9a1b4b70676c562630861130ce43b', 1),
(4, 'Matteo', 'Muratori', 'matmurat01@gmail.com', '9af50d141e5f9e55b7dd593dadb1de13938661ac98873e3ae43572fa3c5c41954da614272f82c259e6179b8bb1754cba6ddf9e52653041d12454279490a64669', 'c5747e2085a40ac64ee156aa1674bc20f689ef56', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `animali`
--
ALTER TABLE `animali`
  ADD PRIMARY KEY (`Nome`);

--
-- Indexes for table `avvistamenti`
--
ALTER TABLE `avvistamenti`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `FKAggiunge` (`Utente_ID`),
  ADD KEY `FKRiferisce` (`Anima_Nome`),
  ADD KEY `FKRiferimento_FK` (`Specie_Anima_Nome`,`Specie_Nome`);

--
-- Indexes for table `descrizioni`
--
ALTER TABLE `descrizioni`
  ADD PRIMARY KEY (`Nomenclatura_Binomiale`);

--
-- Indexes for table `esemplari`
--
ALTER TABLE `esemplari`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `ferite`
--
ALTER TABLE `ferite`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `FKRilevata` (`Gravi_Nome`),
  ADD KEY `FKGlob` (`Sottoi_ID`,`Img_rif`);

--
-- Indexes for table `gravita`
--
ALTER TABLE `gravita`
  ADD PRIMARY KEY (`Nome`);

--
-- Indexes for table `immagini`
--
ALTER TABLE `immagini`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `FKInclude` (`Avvis_ID`);

--
-- Indexes for table `sottoimmagini`
--
ALTER TABLE `sottoimmagini`
  ADD PRIMARY KEY (`ID`,`Immag_ID`),
  ADD KEY `FKContiene` (`Immag_ID`),
  ADD KEY `FKRiconosciuto` (`Esemp_ID`);

--
-- Indexes for table `specie`
--
ALTER TABLE `specie`
  ADD PRIMARY KEY (`Anima_Nome`,`Nome`),
  ADD UNIQUE KEY `FKAttribuita_ID` (`Nomenclatura_Binomiale`);

--
-- Indexes for table `utenti`
--
ALTER TABLE `utenti`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `avvistamenti`
--
ALTER TABLE `avvistamenti`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `esemplari`
--
ALTER TABLE `esemplari`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `ferite`
--
ALTER TABLE `ferite`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `immagini`
--
ALTER TABLE `immagini`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `utenti`
--
ALTER TABLE `utenti`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `avvistamenti`
--
ALTER TABLE `avvistamenti`
  ADD CONSTRAINT `FKAggiunge` FOREIGN KEY (`Utente_ID`) REFERENCES `utenti` (`ID`),
  ADD CONSTRAINT `FKRiferimento_FK` FOREIGN KEY (`Specie_Anima_Nome`,`Specie_Nome`) REFERENCES `specie` (`Anima_Nome`, `Nome`),
  ADD CONSTRAINT `FKRiferisce` FOREIGN KEY (`Anima_Nome`) REFERENCES `animali` (`Nome`);

--
-- Constraints for table `ferite`
--
ALTER TABLE `ferite`
  ADD CONSTRAINT `FKGlob` FOREIGN KEY (`Sottoi_ID`,`Img_rif`) REFERENCES `sottoimmagini` (`ID`, `Immag_ID`),
  ADD CONSTRAINT `FKRilevata` FOREIGN KEY (`Gravi_Nome`) REFERENCES `gravita` (`Nome`);

--
-- Constraints for table `immagini`
--
ALTER TABLE `immagini`
  ADD CONSTRAINT `FKInclude` FOREIGN KEY (`Avvis_ID`) REFERENCES `avvistamenti` (`ID`);

--
-- Constraints for table `sottoimmagini`
--
ALTER TABLE `sottoimmagini`
  ADD CONSTRAINT `FKContiene` FOREIGN KEY (`Immag_ID`) REFERENCES `immagini` (`ID`),
  ADD CONSTRAINT `FKRiconosciuto` FOREIGN KEY (`Esemp_ID`) REFERENCES `esemplari` (`ID`);

--
-- Constraints for table `specie`
--
ALTER TABLE `specie`
  ADD CONSTRAINT `FKAttribuita_FK` FOREIGN KEY (`Nomenclatura_Binomiale`) REFERENCES `descrizioni` (`Nomenclatura_Binomiale`),
  ADD CONSTRAINT `FKIndividuata` FOREIGN KEY (`Anima_Nome`) REFERENCES `animali` (`Nome`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
