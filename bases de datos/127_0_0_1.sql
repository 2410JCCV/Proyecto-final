-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 03-06-2025 a las 05:30:29
-- Versión del servidor: 9.1.0
-- Versión de PHP: 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `base_datos_escuela1`
--
CREATE DATABASE IF NOT EXISTS `base_datos_escuela1` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `base_datos_escuela1`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alumnos`
--

DROP TABLE IF EXISTS `alumnos`;
CREATE TABLE IF NOT EXISTS `alumnos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `matricula` varchar(50) DEFAULT NULL,
  `materia` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `alumnos`
--

INSERT INTO `alumnos` (`id`, `nombre`, `telefono`, `matricula`, `materia`) VALUES
(1, 'Lucía Arevalo', '876-475-9382', 'A32816', 'Biología'),
(2, 'Clara Barbara Henríquez Tejada', '1-924-115-7815x65938', 'A79085', 'Matemáticas'),
(3, 'Margarita Soliz', '080-160-9753', 'A65434', 'Matemáticas'),
(4, 'Andrea Elias Vanegas Garibay', '287.115.8714', 'A65197', 'Inteligencia Artificial'),
(5, 'Mtro. Carlota Trujillo', '1-839-894-7196', 'A77322', 'Física'),
(6, 'René Fabiola López Enríquez', '1-209-471-1220', 'A62225', 'Historia'),
(7, 'Paola Ornelas Sosa', '833-969-4775', 'A32465', 'Redes'),
(8, 'Irma Carrillo Ruiz', '1-533-041-3525x6012', 'A86417', 'Programación'),
(9, 'Amalia Renato Téllez', '101.399.1615x1090', 'A91158', 'Inteligencia Artificial'),
(10, 'Dulce María Carlota Roque', '1-008-691-4131', 'A74505', 'Inteligencia Artificial'),
(11, 'Jerónimo Marco Antonio Dueñas Ballesteros', '709-163-4579x2302', 'A78684', 'Programación'),
(12, 'Jacobo Olga Henríquez', '09720769845', 'A97924', 'Química'),
(13, 'Víctor Henríquez', '807-150-8423', 'A55355', 'Literatura'),
(14, 'Jesús Loera', '(599)246-6109x35233', 'A77005', 'Programación'),
(15, 'José María Vanegas', '(069)602-7142x787', 'A25924', 'Inteligencia Artificial'),
(16, 'Ing. Abel Arellano', '547.063.8120x665', 'A81829', 'Inteligencia Artificial'),
(17, 'Ernesto Alcaraz Acevedo', '(891)319-3442x17610', 'A83282', 'Física'),
(18, 'María Cristina Camilo Hidalgo Cepeda', '851-240-0034x85590', 'A78770', 'Redes'),
(19, 'Lic. Trinidad Reynoso', '658-236-9402x24555', 'A46316', 'Biología'),
(20, 'Ivonne Aranda Arguello', '229-456-8241', 'A94787', 'Matemáticas'),
(21, 'Evelio Arriaga', '(281)465-4611', 'A59246', 'Matemáticas'),
(22, 'Lic. Mario Miranda', '1-171-760-4522', 'A72151', 'Matemáticas'),
(23, 'Lic. Timoteo Brito', '01330601688', 'A75927', 'Química'),
(24, 'Marisol Minerva Viera Gamboa', '(153)492-6351x108', 'A46697', 'Matemáticas'),
(25, 'Zoé Ferrer', '07643039213', 'A53624', 'Literatura'),
(26, 'José María Nájera', '(219)729-6687x5773', 'A78667', 'Física'),
(27, 'Ing. Estefanía Aguirre', '1-550-824-9269', 'A24233', 'Inteligencia Artificial'),
(28, 'Mariano Andrés Bravo Sevilla', '+13(2)0407522758', 'A98688', 'Matemáticas'),
(29, 'Olga Sandoval', '+91(8)9163489676', 'A82288', 'Bases de Datos'),
(30, 'Ing. Eugenia Almonte', '(024)894-5174x46660', 'A21746', 'Biología'),
(31, 'Tomás Carolina Griego', '(500)762-7912', 'A24569', 'Física'),
(32, 'Lilia Alejandro Robledo Olvera', '1-017-200-9925x185', 'A89512', 'Literatura'),
(33, 'Juan Carlos Mitzy Carranza', '+97(9)5194264183', 'A12581', 'Física'),
(34, 'Julio César Rael Nieto', '1-751-007-4089', 'A76411', 'Literatura'),
(35, 'Dr. Estela Cabán', '886-841-2696x11611', 'A71592', 'Historia'),
(36, 'Cristal Angulo', '1-660-754-1511x505', 'A24881', 'Bases de Datos'),
(37, 'Diego Adalberto Godoy Navarrete', '09230310450', 'A74769', 'Literatura'),
(38, 'Dr. Claudia Escobar', '1-175-420-3557x448', 'A72964', 'Matemáticas'),
(39, 'Dulce María Rosalia Blanco Candelaria', '(942)622-3583x3245', 'A31794', 'Literatura'),
(40, 'Wilfrido Arredondo', '906-112-6486', 'A78333', 'Biología'),
(41, 'Rosa Luisa Lugo', '513-758-0660x65735', 'A20215', 'Bases de Datos'),
(42, 'Mariana Aurora Escalante Cardona', '189-276-2662', 'A14262', 'Programación'),
(43, 'María Elena Jacinto Solorzano', '571-734-0797', 'A38865', 'Redes'),
(44, 'Esteban Lugo Carrero', '489-267-1738x64014', 'A37547', 'Literatura'),
(45, 'Abel Hidalgo Otero', '967-145-4391x0144', 'A47670', 'Bases de Datos'),
(46, 'Mtro. Caridad Tamayo', '216.448.2983', 'A33013', 'Literatura'),
(47, 'Sr(a). Lilia Tórrez', '447-592-2116x697', 'A59636', 'Biología'),
(48, 'Rafaél Yolanda Luna', '763.778.5707', 'A39271', 'Matemáticas'),
(49, 'Claudio Miguel Ángel Ávila Galván', '+57(6)0811605019', 'A26956', 'Literatura'),
(50, 'Genaro Loya Gaytán', '943-167-5625', 'A74789', 'Redes'),
(51, 'Vicente Puga', '728-523-2756', 'A67931', 'Literatura'),
(52, 'Mitzy Ordóñez', '337.390.6031x25023', 'A46024', 'Química'),
(53, 'Mtro. Sara Briseño', '1-845-670-8869x77473', 'A86323', 'Historia'),
(54, 'Gabriela Alma Armas Ávila', '(504)021-6396', 'A58029', 'Matemáticas'),
(55, 'Dr. María Elena Espinoza', '1-919-155-8754', 'A45098', 'Matemáticas'),
(56, 'Octavio Armendáriz Espinal', '138.533.4448', 'A55902', 'Bases de Datos'),
(57, 'Florencia Rosario', '304.810.7770', 'A44505', 'Inteligencia Artificial'),
(58, 'Miguel Rincón', '1-111-312-6379x168', 'A39238', 'Matemáticas'),
(59, 'Alfredo Dueñas', '732.455.6184', 'A65794', 'Redes'),
(60, 'Ing. Eloy Lucio', '1-897-844-3019x126', 'A11739', 'Literatura'),
(61, 'Esteban Gloria Aguayo', '886.016.4195x38433', 'A40262', 'Programación'),
(62, 'Norma Maldonado Menchaca', '574.922.0885', 'A72212', 'Química'),
(63, 'Rocío Teresa Alvarado Ceballos', '228-123-7933x236', 'A51907', 'Programación'),
(64, 'Sandra Rosa Centeno Samaniego', '09089577403', 'A70928', 'Historia'),
(65, 'Dr. Virginia Saldaña', '785.142.9635x46030', 'A91067', 'Física'),
(66, 'Felix Jacobo Quintana Gastélum', '524-059-8025', 'A41870', 'Matemáticas'),
(67, 'Mitzy Ballesteros Alvarado', '003.510.5623', 'A91525', 'Redes'),
(68, 'Manuel Collado', '425.660.6488', 'A71315', 'Física'),
(69, 'Alma Vallejo', '06620829821', 'A44037', 'Historia'),
(70, 'Eva Diana Gurule Alonzo', '(821)691-9729', 'A87607', 'Química'),
(71, 'Sr(a). Flavio Molina', '600.715.4273x85265', 'A97240', 'Física'),
(72, 'Miguel Laura Alejandro Márquez', '487-089-8407x6165', 'A34632', 'Programación'),
(73, 'Amelia Alonso', '049-438-8564', 'A52600', 'Biología'),
(74, 'Camilo Raúl Mendoza', '985.225.0909', 'A61851', 'Física'),
(75, 'Jaime Jos Lovato', '457-696-2029x07250', 'A50689', 'Biología'),
(76, 'Wendolin Hinojosa', '931.864.2821x29188', 'A20880', 'Historia'),
(77, 'Lic. Manuel Jáquez', '(406)448-8855', 'A97671', 'Física'),
(78, 'Magdalena Claudia Aguayo', '296-570-8693x0582', 'A30491', 'Biología'),
(79, 'Susana Joaquín Salgado', '+39(3)4261973781', 'A42141', 'Historia'),
(80, 'Daniel Marcos Barragán', '(644)659-5140x704', 'A86525', 'Literatura'),
(81, 'Leonel Judith Pulido', '609-759-2945x06782', 'A90687', 'Bases de Datos'),
(82, 'Ariadna Valles Montenegro', '013.187.0505', 'A48482', 'Historia'),
(83, 'Carmen Laureano', '(292)648-0222x708', 'A57363', 'Química'),
(84, 'Pilar Ontiveros Dueñas', '911.824.3454', 'A22770', 'Literatura'),
(85, 'Noemí María Eugenia Correa Rangel', '(209)280-5137x9378', 'A32799', 'Física'),
(86, 'Isabela Vanesa Cervántez', '808.185.0116x959', 'A40404', 'Química'),
(87, 'Israel Ochoa', '512-502-2059x3060', 'A88385', 'Redes'),
(88, 'Julio Amelia Zapata de la Garza', '160.759.9449', 'A72425', 'Matemáticas'),
(89, 'Liliana Durán', '+74(3)6151105026', 'A45600', 'Matemáticas'),
(90, 'Sr(a). Irene Reyes', '(771)086-4081x1551', 'A80360', 'Física'),
(91, 'Alonso Corrales', '400-652-8222x2395', 'A42843', 'Historia'),
(92, 'Maximiliano Otero Armendáriz', '345.235.3267', 'A73210', 'Literatura'),
(93, 'Raúl Citlali Olvera Vallejo', '+29(0)6220058000', 'A89835', 'Bases de Datos'),
(94, 'Renato Cordero Corral', '(606)695-3258x38612', 'A43370', 'Física'),
(95, 'Raúl Montoya', '06637636376', 'A68093', 'Química'),
(96, 'Sr(a). Flavio Leal', '580-997-3409x5618', 'A66371', 'Programación'),
(97, 'Claudio Orosco Amador', '(661)797-2257x629', 'A20557', 'Bases de Datos'),
(98, 'Nadia Bruno Negrete Montaño', '578-034-2954', 'A94121', 'Programación'),
(99, 'Tania Arguello', '(860)646-3522', 'A58025', 'Química'),
(100, 'Sara Munguía de Jesús', '+69(6)7116882223', 'A16745', 'Física');
--
-- Base de datos: `escuela`
--
CREATE DATABASE IF NOT EXISTS `escuela` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `escuela`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estudiantes`
--

DROP TABLE IF EXISTS `estudiantes`;
CREATE TABLE IF NOT EXISTS `estudiantes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) DEFAULT NULL,
  `edad` int DEFAULT NULL,
  `carrera` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `estudiantes`
--

INSERT INTO `estudiantes` (`id`, `nombre`, `edad`, `carrera`) VALUES
(1, 'Ana Torres', 21, 'Ingeniería'),
(2, 'Luis Gómez', 22, 'Contaduría'),
(3, 'Carlos Pérez', 23, 'Derecho'),
(4, 'Laura Ruiz', 20, 'Psicología'),
(5, 'Daniela López', 21, 'Medicina'),
(6, 'Pedro Sánchez', 24, 'Arquitectura'),
(7, 'Lucía Díaz', 22, 'Sociología'),
(8, 'María Jiménez', 23, 'Odontología'),
(9, 'Jorge Castro', 25, 'Informática'),
(10, 'Fernanda Mora', 22, 'Diseño'),
(11, 'Ricardo Vela', 21, 'Física'),
(12, 'Carmen León', 23, 'Química'),
(13, 'Sofía Marín', 24, 'Educación'),
(14, 'Mateo Silva', 20, 'Agronomía'),
(15, 'Valeria Núñez', 21, 'Economía'),
(16, 'David Ortiz', 22, 'Geografía'),
(17, 'Camila Ramos', 20, 'Turismo'),
(18, 'Andrés Vega', 23, 'Filosofía'),
(19, 'Paula Zúñiga', 22, 'Periodismo'),
(20, 'Esteban Navarro', 24, 'Veterinaria'),
(22, 'juan carlos', 21, 'Bioquimica');
--
-- Base de datos: `escuela2`
--
CREATE DATABASE IF NOT EXISTS `escuela2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `escuela2`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alumnos`
--

DROP TABLE IF EXISTS `alumnos`;
CREATE TABLE IF NOT EXISTS `alumnos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `matricula` varchar(20) DEFAULT NULL,
  `materia` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `alumnos`
--

INSERT INTO `alumnos` (`id`, `nombre`, `telefono`, `matricula`, `materia`) VALUES
(1, 'Lic. Nadia Bonilla', '1919675858411', 'B2001', 'Biología'),
(2, 'Isabel Ayala Castellanos', '(741)5789870', 'B2002', 'Biología'),
(3, 'Catalina Virginia Ballesteros', '(241)9673396x54', 'B2003', 'Matemáticas'),
(4, 'Dr. Lucía Trujill', '7909637207x659', 'B2004', 'Química'),
(5, 'Teresa Celia Ayala Cantú', '240.140.2743x31', 'B2005', 'Historia'),
(6, 'Alonso Asunción Baeza Manzanares', '(645)4364355x58', 'B2006', 'Programación'),
(7, 'Clara Mora', '+89(2)588010040', 'B2007', 'Historia'),
(8, 'Laura Rubio Salinas', '01353727117', 'B2008', 'Literatura'),
(9, 'Adalberto Julio César Maya', '3692007104x880', 'B2009', 'Física'),
(10, 'Dr. Alfonso Romero', '11943873267', 'B2010', 'Filosofía'),
(11, 'Serafín Tapia', '06213125754', 'B2011', 'Programación'),
(12, 'Mtro. Micaela Mayorga', '1155575702x3161', 'B2012', 'Matemáticas'),
(13, 'Eva Daniela Santiago', '6941946474x066', 'B2013', 'Biología'),
(14, 'José Manuél Borrego de la Torre', '(282)8721242x65', 'B2014', 'Química'),
(15, 'Dr. Dulce Zavala', '14791170856', 'B2015', 'Economía'),
(16, 'Cecilia Julio César Altamirano Puga', '568.398.6177x62', 'B2016', 'Física'),
(17, 'Aurelio Godoy', '14336909146x837', 'B2017', 'Matemáticas'),
(18, 'Jos Segura Díaz', '(285)1594190', 'B2018', 'Filosofía'),
(19, 'Olga Ozuna Lomeli', '12401018047x485', 'B2019', 'Biología'),
(21, 'Carolina Juan Archuleta de la Fuente', '18196303793x763', 'B2021', 'Literatura'),
(22, 'Nicolás Alfaro Jáquez', '10210748346x114', 'B2022', 'Biología'),
(23, 'Dalia Collado', '(589)4496783x52', 'B2023', 'Programación'),
(24, 'Miguel Francisca Arguello Escalante', '(535)9029136', 'B2024', 'Economía'),
(25, 'Andrea Darío Alonzo Linares', '(635)7203741x35', 'B2025', 'Biología'),
(26, 'Guillermo Antonio Maya Sauceda', '19544705235x640', 'B2026', 'Literatura'),
(27, 'Oswaldo Patricio Palomino Villanueva', '12831302047x994', 'B2027', 'Filosofía'),
(28, 'Hermelinda Eduardo Armendáriz', '14263679098x906', 'B2028', 'Biología'),
(29, 'Ing. Abelardo Gaona', '(090)0214786x93', 'B2029', 'Biología'),
(30, 'Miguel Reynoso', '701.112.5692x46', 'B2030', 'Economía'),
(31, 'María José Liliana Tovar Corrales', '16114522989', 'B2031', 'Programación'),
(32, 'José Luis Fabiola Mateo Aguilar', '(059)2027917x01', 'B2032', 'Geografía'),
(33, 'Alberto Nadia Prado Córdova', '(081)5180857x33', 'B2033', 'Literatura'),
(34, 'Carlos Preciado Toro', '(566)2193019', 'B2034', 'Geografía'),
(35, 'Natalia Rosario Rosado', '(150)3889924x59', 'B2035', 'Filosofía'),
(36, 'Dr. Guillermina Mena', '(077)9107293x13', 'B2036', 'Filosofía'),
(37, 'Mtro. Jaime Granado', '08505048374', 'B2037', 'Física'),
(38, 'Mtro. Raquel Cortez', '170.476.3048', 'B2038', 'Historia'),
(39, 'Dr. Dulce María Candelaria', '(178)1703454', 'B2039', 'Historia'),
(40, 'Yeni Dulce Salcedo', '228.316.6431x46', 'B2040', 'Química'),
(41, 'Yuridia Montemayor Pabón', '(050)9369236', 'B2041', 'Filosofía'),
(42, 'Alejandra Alfonso Urrutia', '+21(5)832105724', 'B2042', 'Economía'),
(43, 'María José Rivera', '15654829755', 'B2043', 'Física'),
(44, 'Frida Micaela Ávalos', '(643)0209458', 'B2044', 'Filosofía'),
(45, 'Karla Raya Aguilera', '054.965.6618x73', 'B2045', 'Historia'),
(46, 'Dr. Elvira Gurule', '5146672460x6403', 'B2046', 'Física'),
(47, 'Eduardo Baeza', '(304)4311614', 'B2047', 'Historia'),
(48, 'Sr(a). Emiliano Téllez', '15859375209', 'B2048', 'Matemáticas'),
(49, 'Sofía José Eduardo Arredondo', '13472506528', 'B2049', 'Historia'),
(50, 'Óscar Escobar', '173.447.1369', 'B2050', 'Geografía'),
(51, 'Jorge Luis Indira Duarte Sotelo', '(848)8774292x87', 'B2051', 'Programación'),
(52, 'Eloisa Pedro Sarabia Morales', '(385)3186254x67', 'B2052', 'Literatura'),
(53, 'Noelia Vanesa Carmona Tamayo', '(418)4015052x02', 'B2053', 'Matemáticas'),
(54, 'Timoteo Campos', '18905354229x453', 'B2054', 'Filosofía'),
(55, 'Gerónimo Lemus', '(694)6529257x35', 'B2055', 'Economía'),
(56, 'Timoteo Serrano Paredes', '878.222.0347', 'B2056', 'Geografía'),
(57, 'Luis Tomás Hinojosa Rojo', '+55(0)370079153', 'B2057', 'Física'),
(58, 'Graciela Esquivel', '(243)6269008', 'B2058', 'Programación'),
(59, 'Joaquín Francisco Javier Cadena', '17205003667x536', 'B2059', 'Matemáticas'),
(60, 'Minerva Ulloa Acosta', '19908174041', 'B2060', 'Física'),
(61, 'Martín Velasco', '05226970915', 'B2061', 'Literatura'),
(62, 'Natalia Violeta Longoria Sotelo', '817.339.8510x54', 'B2062', 'Programación'),
(63, 'Alvaro Guerra', '(498)9938338x46', 'B2063', 'Geografía'),
(64, 'Florencia Márquez Peres', '13366775225', 'B2064', 'Biología'),
(65, 'Dr. Felipe Cepeda', '1293863254x7987', 'B2065', 'Literatura'),
(66, 'Micaela Riojas Pichardo', '633.199.3977', 'B2066', 'Geografía'),
(67, 'Ing. Abelardo Navarrete', '+98(8)011031083', 'B2067', 'Literatura'),
(68, 'Verónica Acuña', '857.483.2688x28', 'B2068', 'Literatura'),
(69, 'Ángel Viera Sotelo', '316.452.3355', 'B2069', 'Química'),
(70, 'Isaac Alberto Grijalva', '505.201.5715', 'B2070', 'Historia'),
(71, 'Luis Manuel Norma Roybal', '(780)3202940x89', 'B2071', 'Economía'),
(72, 'Germán Esparta Rodrígez Montaño', '0396621003x436', 'B2072', 'Programación'),
(73, 'Claudio Sessa del Río Solorzano', '(177)7113243x94', 'B2073', 'Filosofía'),
(74, 'Jorge Luis Montaño Marrero', '16984671216x098', 'B2074', 'Química'),
(75, 'Fidel Jorge Luis Valles Tafoya', '(476)8912682x91', 'B2075', 'Biología'),
(76, 'Karina Julio Chacón Palomo', '+79(7)618631766', 'B2076', 'Matemáticas'),
(77, 'Eloy Santana', '02113400897', 'B2077', 'Química'),
(78, 'Arcelia Oquendo', '6764285404x3087', 'B2078', 'Química'),
(79, 'Lic. Enrique Feliciano', '09342803451', 'B2079', 'Geografía'),
(80, 'Pascual Salinas', '008.193.1658', 'B2080', 'Matemáticas'),
(81, 'Ana María Medina Padrón', '914.348.1464x50', 'B2081', 'Literatura'),
(82, 'Porfirio Moya Cadena', '2404001037', 'B2082', 'Física'),
(83, 'Pilar Trinidad Hernandes', '082.768.8611x43', 'B2083', 'Economía'),
(84, 'Flavio Laureano', '17384825721x203', 'B2084', 'Biología'),
(85, 'Alonso María Tapia Reynoso', '0226478796x4340', 'B2085', 'Literatura'),
(86, 'Miguel Ángel Mónica Hidalgo Malave', '2270581686', 'B2086', 'Química'),
(87, 'Leonardo Carmona', '9484799239x509', 'B2087', 'Historia'),
(88, 'Octavio Zacarías Romero', '13761265170x129', 'B2088', 'Programación'),
(89, 'Benito Dulce María Córdova', '16875684525x886', 'B2089', 'Economía'),
(90, 'Mtro. Jorge Rascón', '(593)2461463', 'B2090', 'Economía'),
(91, 'Tomás Reséndez', '+35(4)200300565', 'B2091', 'Historia'),
(92, 'Benito Clemente Irizarry', '+64(5)132759935', 'B2092', 'Física'),
(93, 'Esmeralda Miriam Venegas Espinosa', '828.501.4318x17', 'B2093', 'Biología'),
(94, 'Sr(a). Rufino Heredia', '(457)2381272', 'B2094', 'Historia'),
(95, 'Francisco Jacobo Maestas Zamora', '18167512011x367', 'B2095', 'Economía'),
(96, 'Renato Leal', '+40(1)087691957', 'B2096', 'Geografía'),
(97, 'María Luisa Yeni Rodarte Guerra', '01585821939', 'B2097', 'Biología'),
(98, 'Armando Roybal Bustos', '(667)8046089x54', 'B2098', 'Geografía'),
(99, 'Tomás Beatriz Soria Uribe', '19721055342x878', 'B2099', 'Biología');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(191) NOT NULL,
  `password` varchar(255) NOT NULL,
  `secret` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `username`, `password`, `secret`) VALUES
(1, 'luis', '123456', '4A6HP2KDX6R3K3GEAYESLYCV25NKNNYM');
--
-- Base de datos: `layaut`
--
CREATE DATABASE IF NOT EXISTS `layaut` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `layaut`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

DROP TABLE IF EXISTS `productos`;
CREATE TABLE IF NOT EXISTS `productos` (
  `producto_id` int NOT NULL AUTO_INCREMENT,
  `nombre_producto` varchar(100) NOT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`producto_id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`producto_id`, `nombre_producto`, `precio_unitario`) VALUES
(1, 'Laptop', 1200.00),
(2, 'Mouse', 25.00),
(3, 'Teclado Mecánico', 80.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `correo` varchar(100) NOT NULL,
  `secret` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
--
-- Base de datos: `libreria_online`
--
CREATE DATABASE IF NOT EXISTS `libreria_online` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `libreria_online`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `autores`
--

DROP TABLE IF EXISTS `autores`;
CREATE TABLE IF NOT EXISTS `autores` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `nacionalidad` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `autores`
--

INSERT INTO `autores` (`id`, `nombre`, `nacionalidad`) VALUES
(1, 'Gabriel García Márquez', 'Colombiano'),
(2, 'Jane Austen', 'Británica'),
(3, 'George Orwell', 'Británico');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `libros`
--

DROP TABLE IF EXISTS `libros`;
CREATE TABLE IF NOT EXISTS `libros` (
  `id` int NOT NULL AUTO_INCREMENT,
  `titulo` varchar(255) NOT NULL,
  `id_autor` int DEFAULT NULL,
  `año_publicacion` int DEFAULT NULL,
  `precio` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_autor` (`id_autor`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `libros`
--

INSERT INTO `libros` (`id`, `titulo`, `id_autor`, `año_publicacion`, `precio`) VALUES
(1, 'Cien años de soledad', 1, 1967, 25.50),
(2, 'Orgullo y prejuicio', 2, 1813, 18.00),
(3, '1984', 3, 1949, 22.75),
(4, 'El amor en los tiempos del cólera', 1, 1985, 30.00);
--
-- Base de datos: `login_usuarios`
--
CREATE DATABASE IF NOT EXISTS `login_usuarios` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `login_usuarios`;
--
-- Base de datos: `prueba`
--
CREATE DATABASE IF NOT EXISTS `prueba` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `prueba`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
CREATE TABLE IF NOT EXISTS `pedidos` (
  `id_pedido` int NOT NULL AUTO_INCREMENT,
  `id_producto` int DEFAULT NULL,
  `cantidad` int DEFAULT NULL,
  `fecha_pedido` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_pedido`),
  KEY `id_producto` (`id_producto`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

DROP TABLE IF EXISTS `productos`;
CREATE TABLE IF NOT EXISTS `productos` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `nombre_producto` varchar(150) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `stock` int DEFAULT '0',
  PRIMARY KEY (`id_producto`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id_producto`, `nombre_producto`, `precio`, `stock`) VALUES
(1, 'Laptop Gamer', 1200.00, 8),
(2, 'Monitor Curvo', 350.50, 25);
--
-- Base de datos: `pruebas8`
--
CREATE DATABASE IF NOT EXISTS `pruebas8` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `pruebas8`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alumnos`
--

DROP TABLE IF EXISTS `alumnos`;
CREATE TABLE IF NOT EXISTS `alumnos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `matricula` varchar(50) DEFAULT NULL,
  `materia` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `alumnos`
--

INSERT INTO `alumnos` (`id`, `nombre`, `telefono`, `matricula`, `materia`) VALUES
(1, 'Juan Pérez', '555-1234', 'A001', 'Matemáticas'),
(2, 'Ana López', '555-5678', 'A002', 'Física'),
(3, 'Carlos Ruiz', '555-9999', 'A003', 'Química'),
(5, 'Luis Torres', '555-7890', 'A005', 'Historia');
--
-- Base de datos: `regis`
--
CREATE DATABASE IF NOT EXISTS `regis` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `regis`;
--
-- Base de datos: `sistemaveterinario`
--
CREATE DATABASE IF NOT EXISTS `sistemaveterinario` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE `sistemaveterinario`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tb_usuarios`
--

DROP TABLE IF EXISTS `tb_usuarios`;
CREATE TABLE IF NOT EXISTS `tb_usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
  `password` text COLLATE utf8mb4_spanish_ci NOT NULL,
  `token` varchar(11) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `cargo` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `fyh_creacion` datetime NOT NULL,
  `fyh_actualizacion` datetime DEFAULT NULL,
  PRIMARY KEY (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `tb_usuarios`
--

INSERT INTO `tb_usuarios` (`id_usuario`, `nombre_completo`, `email`, `password`, `token`, `cargo`, `fyh_creacion`, `fyh_actualizacion`) VALUES
(1, 'juan carlos', 'licjuanccv@gmail.com', '12345678', NULL, 'ADMINISTRADOR', '2025-05-10 06:05:52', NULL);
--
-- Base de datos: `tienda`
--
CREATE DATABASE IF NOT EXISTS `tienda` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `tienda`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `articulos`
--

DROP TABLE IF EXISTS `articulos`;
CREATE TABLE IF NOT EXISTS `articulos` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `existencias` int DEFAULT '0',
  `costo_compra` decimal(10,2) DEFAULT '0.00',
  `porc_ganancia` decimal(4,2) DEFAULT '10.00',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `articulos`
--

INSERT INTO `articulos` (`id`, `nombre`, `existencias`, `costo_compra`, `porc_ganancia`) VALUES
(1, 'Mouse inalámbrico', 100, 150.00, 20.00),
(2, 'Teclado mecánico RGB', 50, 350.00, 25.00),
(3, 'Monitor LED 24\"', 30, 1800.00, 30.00),
(4, 'Disco duro externo 1TB', 40, 950.00, 15.00),
(5, 'Memoria USB 64GB', 200, 120.00, 35.00),
(6, 'Audífonos Bluetooth', 75, 250.00, 20.00),
(7, 'Laptop Core i5', 10, 15000.00, 18.00),
(8, 'Tablet 10 pulgadas', 15, 3500.00, 22.00),
(9, 'Cámara web HD', 60, 400.00, 25.00),
(10, 'Impresora multifuncional', 20, 2200.00, 28.00),
(12, 'caca', 2, 167.00, 29.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ventas`
--

DROP TABLE IF EXISTS `ventas`;
CREATE TABLE IF NOT EXISTS `ventas` (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_articulo` bigint UNSIGNED NOT NULL,
  `fecha` date DEFAULT NULL,
  `cantidad` int DEFAULT NULL,
  `costo` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_articulo` (`id_articulo`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `ventas`
--

INSERT INTO `ventas` (`id`, `id_articulo`, `fecha`, `cantidad`, `costo`) VALUES
(1, 1, '2025-05-12', 2, 360.00),
(2, 2, '2025-05-11', 1, 437.50),
(3, 3, '2025-05-10', 1, 2340.00),
(4, 4, '2025-05-09', 3, 3277.50),
(5, 5, '2025-05-08', 5, 810.00),
(6, 6, '2025-05-07', 2, 600.00),
(7, 7, '2025-05-06', 1, 17700.00),
(8, 8, '2025-05-05', 1, 4270.00),
(9, 9, '2025-05-04', 3, 1500.00),
(10, 10, '2025-05-03', 2, 5632.00);
--
-- Base de datos: `visualizador_db`
--
CREATE DATABASE IF NOT EXISTS `visualizador_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `visualizador_db`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `login`
--

DROP TABLE IF EXISTS `login`;
CREATE TABLE IF NOT EXISTS `login` (
  `id` int NOT NULL AUTO_INCREMENT,
  `usuario` varchar(50) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `login`
--

INSERT INTO `login` (`id`, `usuario`, `contrasena`) VALUES
(1, 'testuser', '12345'),
(2, 'luis', '123456'),
(3, 'maria', '1234');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

DROP TABLE IF EXISTS `productos`;
CREATE TABLE IF NOT EXISTS `productos` (
  `producto_id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `precio_unitario` decimal(10,2) DEFAULT NULL,
  `stock` int DEFAULT NULL,
  PRIMARY KEY (`producto_id`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`producto_id`, `nombre`, `precio_unitario`, `stock`) VALUES
(1, 'Laptop', 1200.50, 50),
(2, 'Mouse', 29.99, 200),
(3, 'Teclado', 75.20, 100),
(4, 'Monitor', 350.00, 75),
(5, 'Auriculares', 50.00, 150),
(6, 'pantalla', 100.00, 20),
(7, 'telefono', 20000.00, 20),
(11, 'Nuevo Producto', 19.99, 100);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
