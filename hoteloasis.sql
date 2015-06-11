DROP DATABASE IF EXISTS `hoteloasis`;
CREATE DATABASE IF NOT EXISTS `hoteloasis` /*!40100 DEFAULT CHARACTER SET utf8 */;
GRANT ALL PRIVILEGES ON `hoteloasis`.* TO 'hoteloasis'@'localhost';
USE `hoteloasis`;

-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 11-06-2015 a las 13:13:02
-- Versión del servidor: 5.6.17
-- Versión de PHP: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `hoteloasis`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bebida`
--

CREATE TABLE IF NOT EXISTS `bebida` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `coste` double NOT NULL,
  `cantidad_minibar` int(11) NOT NULL,
  `categoria` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fd72rxgl57dtcgto544c9eo11` (`categoria`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Volcado de datos para la tabla `bebida`
--

INSERT INTO `bebida` (`id`, `nombre`, `coste`, `cantidad_minibar`, `categoria`, `version`) VALUES
(1, 'Cerveza Alhambra', 1.2, 4, 1, 0),
(2, 'Cerveza Alhambra', 1.6, 4, 2, 0),
(3, 'Cerveza Alhambra', 2, 4, 3, 0),
(4, 'Coca-cola', 1.2, 4, 1, 0),
(5, 'Coca-cola', 1.6, 4, 2, 0),
(6, 'Coca-cola', 2, 4, 3, 0),
(7, 'Vino tinto Marqués de Riscal (1L)', 8.5, 2, 1, 0),
(8, 'Vino tinto Marqués de Riscal (1L)', 12.5, 2, 2, 0),
(9, 'Vino tinto Marqués de Riscal (1L)', 18.5, 2, 3, 0),
(10, 'Agua Mineral', 1, 4, 1, 0),
(11, 'Agua Mineral', 1.2, 1, 2, 0),
(12, 'Agua Mineral', 1.4, 4, 3, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bebida_consumo`
--

CREATE TABLE IF NOT EXISTS `bebida_consumo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bebida` bigint(20) NOT NULL,
  `cantidad_consumida` int(11) NOT NULL,
  `estancia` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_7xekh86ho9e8mhksh708wom13` (`bebida`),
  KEY `FK_lseuhbnvrhlhe75yehw8ien9t` (`estancia`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Volcado de datos para la tabla `bebida_consumo`
--

INSERT INTO `bebida_consumo` (`id`, `bebida`, `cantidad_consumida`, `estancia`, `version`) VALUES
(9, 4, 3, 2, 2),
(10, 1, 3, 2, 2),
(11, 10, 4, 2, 0),
(12, 1, 1, 3, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria`
--

CREATE TABLE IF NOT EXISTS `categoria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `precio_categoria` double NOT NULL,
  `hotel` bigint(20) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_sefj36p9cylj4elqr9g05cgmf` (`hotel`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Volcado de datos para la tabla `categoria`
--

INSERT INTO `categoria` (`id`, `nombre`, `descripcion`, `precio_categoria`, `hotel`, `version`) VALUES
(1, 'Normal', 'Categoría básica', 1, 1, 0),
(2, 'Business', 'Categoría media (terraza)', 1.3, 1, 0),
(3, 'Alta', 'Categoría de lujo (terraza y jacuzzi)', 2, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estancia`
--

CREATE TABLE IF NOT EXISTS `estancia` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reserva` bigint(20) DEFAULT NULL,
  `habitacion` bigint(20) DEFAULT NULL,
  `fecha_check_in` datetime DEFAULT NULL,
  `fecha_check_out` datetime DEFAULT NULL,
  `usuario` bigint(20) DEFAULT NULL,
  `llamadas` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_idcl4apqgm76iw92exbd8qqm0` (`habitacion`),
  KEY `FK_d38qyhodyxqxnw8sagvtn21kw` (`llamadas`),
  KEY `FK_ckfjibcqu9yrqyky3cc93j7xu` (`reserva`),
  KEY `FK_rra9vnp57hv64x3q9nwlichju` (`usuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Volcado de datos para la tabla `estancia`
--

INSERT INTO `estancia` (`id`, `reserva`, `habitacion`, `fecha_check_in`, `fecha_check_out`, `usuario`, `llamadas`, `version`) VALUES
(2, 1, 1, '2015-06-11 02:24:59', NULL, 2, 1, 12),
(3, 2, 2, '2015-06-11 12:05:24', NULL, 3, 2, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estancia_bebida_consumo`
--

CREATE TABLE IF NOT EXISTS `estancia_bebida_consumo` (
  `estancia` bigint(20) NOT NULL,
  `bebida_consumo` bigint(20) NOT NULL,
  PRIMARY KEY (`estancia`,`bebida_consumo`),
  KEY `FK_h5qkw9sipiraphk98b37ti82w` (`bebida_consumo`),
  KEY `FK_3qaf0woeri5nvmefxd70kmiai` (`estancia`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `estancia_bebida_consumo`
--

INSERT INTO `estancia_bebida_consumo` (`estancia`, `bebida_consumo`) VALUES
(2, 9),
(2, 10),
(2, 11),
(3, 12);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habitacion`
--

CREATE TABLE IF NOT EXISTS `habitacion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `numero` int(11) NOT NULL,
  `estado` int(11) DEFAULT NULL,
  `tipo` int(11) NOT NULL,
  `categoria` bigint(20) NOT NULL,
  `hotel` bigint(20) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fqyr2c64epuk4iwbyolxtwyk8` (`categoria`),
  KEY `FK_t1shnt51btupr612580ori1aj` (`hotel`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

--
-- Volcado de datos para la tabla `habitacion`
--

INSERT INTO `habitacion` (`id`, `numero`, `estado`, `tipo`, `categoria`, `hotel`, `version`) VALUES
(1, 1, 1, 0, 1, 1, 3),
(2, 2, 1, 0, 1, 1, 1),
(3, 3, 0, 0, 1, 1, 0),
(4, 4, 0, 0, 1, 1, 4),
(5, 5, 0, 0, 2, 1, 4),
(6, 6, 0, 0, 3, 1, 1),
(7, 7, 0, 1, 1, 1, 1),
(8, 8, 0, 1, 1, 1, 1),
(9, 9, 0, 1, 2, 1, 0),
(10, 10, 0, 1, 3, 1, 0),
(11, 11, 0, 0, 1, 1, 0),
(12, 12, 0, 0, 1, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `hotel`
--

CREATE TABLE IF NOT EXISTS `hotel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `provincia` varchar(30) NOT NULL,
  `poblacion` varchar(30) NOT NULL,
  `direccion` varchar(30) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `estrellas` int(11) NOT NULL,
  `precio_hab_simple` double NOT NULL,
  `precio_hab_doble` double NOT NULL,
  `precio_cama_sup` double NOT NULL,
  `dias_antelacion` int(11) NOT NULL,
  `dias_maximos` int(11) NOT NULL,
  `tarifa` bigint(20) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_46racfjmdwto1suyecrlgges3` (`tarifa`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Volcado de datos para la tabla `hotel`
--

INSERT INTO `hotel` (`id`, `nombre`, `provincia`, `poblacion`, `direccion`, `telefono`, `estrellas`, `precio_hab_simple`, `precio_hab_doble`, `precio_cama_sup`, `dias_antelacion`, `dias_maximos`, `tarifa`, `version`) VALUES
(1, 'Hotel Oasis Costa Ballena', 'Cádiz', 'Costa Ballena', 'Calle Reina Sofía s/n', '956010203', 5, 30, 70, 10, 60, 30, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `llamada`
--

CREATE TABLE IF NOT EXISTS `llamada` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `estancia` bigint(20) DEFAULT NULL,
  `minutos_nacionales` double NOT NULL,
  `minutos_internacionales` double NOT NULL,
  `minutos_internet` double NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ec3kfyafdymx3vi4x9oprvo49` (`estancia`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Volcado de datos para la tabla `llamada`
--

INSERT INTO `llamada` (`id`, `estancia`, `minutos_nacionales`, `minutos_internacionales`, `minutos_internet`, `version`) VALUES
(1, 2, 2, 2, 2, 15),
(2, 3, 1, 0, 0, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reserva`
--

CREATE TABLE IF NOT EXISTS `reserva` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cama_supletoria` tinyint(1) DEFAULT NULL,
  `coste_reserva` double NOT NULL,
  `fecha_cancelacion` datetime DEFAULT NULL,
  `fecha_entrada` datetime NOT NULL,
  `fecha_reserva` datetime NOT NULL,
  `fecha_salida` datetime NOT NULL,
  `tipo` int(11) NOT NULL,
  `version` int(11) DEFAULT NULL,
  `categoria` bigint(20) NOT NULL,
  `hotel` bigint(20) NOT NULL,
  `usuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_jn05yaaainuc33m0exmmbag13` (`categoria`),
  KEY `FK_r5amy587nfobqlx0r0lslueqp` (`hotel`),
  KEY `FK_komn6oxdb49a7x85plh7wqm18` (`usuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Volcado de datos para la tabla `reserva`
--

INSERT INTO `reserva` (`id`, `cama_supletoria`, `coste_reserva`, `fecha_cancelacion`, `fecha_entrada`, `fecha_reserva`, `fecha_salida`, `tipo`, `version`, `categoria`, `hotel`, `usuario`) VALUES
(1, 1, 120, NULL, '2015-06-11 00:00:00', '2015-06-11 02:08:17', '2015-06-14 00:00:00', 0, 0, 1, 1, 2),
(2, NULL, 60, NULL, '2015-06-11 00:00:00', '2015-06-11 12:04:57', '2015-06-13 00:00:00', 0, 0, 1, 1, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE IF NOT EXISTS `rol` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(20) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id`, `nombre`, `version`) VALUES
(1, 'Administrador', 0),
(2, 'Recepcionista', 0),
(3, 'Usuario', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarifa`
--

CREATE TABLE IF NOT EXISTS `tarifa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `llamada_nacional` double NOT NULL,
  `llamada_internacional` double NOT NULL,
  `internet` double NOT NULL,
  `cancel_mas_cinco_dias` double NOT NULL,
  `cancel_dos_cinco_dias` double NOT NULL,
  `cancel_uno_dos_dias` double NOT NULL,
  `cancel_mismo_dia` double NOT NULL,
  `version` int(11) DEFAULT NULL,
  `hotel` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cqcycganu12ywiidg20wvym09` (`hotel`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Volcado de datos para la tabla `tarifa`
--

INSERT INTO `tarifa` (`id`, `llamada_nacional`, `llamada_internacional`, `internet`, `cancel_mas_cinco_dias`, `cancel_dos_cinco_dias`, `cancel_uno_dos_dias`, `cancel_mismo_dia`, `version`, `hotel`) VALUES
(1, 0.02, 0.1, 0.04, 0, 10, 30, 100, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE IF NOT EXISTS `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `primer_apellido` varchar(30) NOT NULL,
  `segundo_apellido` varchar(30) NOT NULL,
  `email` varchar(50) NOT NULL,
  `nombre_usuario` varchar(30) NOT NULL,
  `clave` varchar(300) NOT NULL,
  `rol` bigint(20) DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_aevanv0397v7gs2a47sb43sxs` (`rol`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `nombre`, `primer_apellido`, `segundo_apellido`, `email`, `nombre_usuario`, `clave`, `rol`, `enabled`, `version`) VALUES
(1, 'Administrador', 'Hotel Oasis', 'Hotel Oasis', 'admin@hoteloasis.com', 'admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 1, 1, 0),
(2, 'María', 'Díaz', 'Prueba', 'maria-df@hotmail.es', 'maria', '94aec9fbed989ece189a7e172c9cf41669050495152bc4c1dbf2a38d7fd85627', 3, 1, 0),
(3, 'Pepe', 'Fuentes', 'Pérez', 'pepe@yahoo.es', 'pepe', '7c9e7c1494b2684ab7c19d6aff737e460fa9e98d5a234da1310c97ddf5691834', 3, 1, 0);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `bebida`
--
ALTER TABLE `bebida`
  ADD CONSTRAINT `FK_fd72rxgl57dtcgto544c9eo11` FOREIGN KEY (`categoria`) REFERENCES `categoria` (`id`);

--
-- Filtros para la tabla `bebida_consumo`
--
ALTER TABLE `bebida_consumo`
  ADD CONSTRAINT `FK_7xekh86ho9e8mhksh708wom13` FOREIGN KEY (`bebida`) REFERENCES `bebida` (`id`),
  ADD CONSTRAINT `FK_lseuhbnvrhlhe75yehw8ien9t` FOREIGN KEY (`estancia`) REFERENCES `estancia` (`id`);

--
-- Filtros para la tabla `categoria`
--
ALTER TABLE `categoria`
  ADD CONSTRAINT `FK_sefj36p9cylj4elqr9g05cgmf` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`);

--
-- Filtros para la tabla `estancia`
--
ALTER TABLE `estancia`
  ADD CONSTRAINT `FK_ckfjibcqu9yrqyky3cc93j7xu` FOREIGN KEY (`reserva`) REFERENCES `reserva` (`id`),
  ADD CONSTRAINT `FK_d38qyhodyxqxnw8sagvtn21kw` FOREIGN KEY (`llamadas`) REFERENCES `llamada` (`id`),
  ADD CONSTRAINT `FK_idcl4apqgm76iw92exbd8qqm0` FOREIGN KEY (`habitacion`) REFERENCES `habitacion` (`id`),
  ADD CONSTRAINT `FK_rra9vnp57hv64x3q9nwlichju` FOREIGN KEY (`usuario`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `estancia_bebida_consumo`
--
ALTER TABLE `estancia_bebida_consumo`
  ADD CONSTRAINT `FK_3qaf0woeri5nvmefxd70kmiai` FOREIGN KEY (`estancia`) REFERENCES `estancia` (`id`),
  ADD CONSTRAINT `FK_h5qkw9sipiraphk98b37ti82w` FOREIGN KEY (`bebida_consumo`) REFERENCES `bebida_consumo` (`id`);

--
-- Filtros para la tabla `habitacion`
--
ALTER TABLE `habitacion`
  ADD CONSTRAINT `FK_fqyr2c64epuk4iwbyolxtwyk8` FOREIGN KEY (`categoria`) REFERENCES `categoria` (`id`),
  ADD CONSTRAINT `FK_t1shnt51btupr612580ori1aj` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`);

--
-- Filtros para la tabla `hotel`
--
ALTER TABLE `hotel`
  ADD CONSTRAINT `FK_46racfjmdwto1suyecrlgges3` FOREIGN KEY (`tarifa`) REFERENCES `tarifa` (`id`);

--
-- Filtros para la tabla `llamada`
--
ALTER TABLE `llamada`
  ADD CONSTRAINT `FK_ec3kfyafdymx3vi4x9oprvo49` FOREIGN KEY (`estancia`) REFERENCES `estancia` (`id`);

--
-- Filtros para la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD CONSTRAINT `FK_jn05yaaainuc33m0exmmbag13` FOREIGN KEY (`categoria`) REFERENCES `categoria` (`id`),
  ADD CONSTRAINT `FK_komn6oxdb49a7x85plh7wqm18` FOREIGN KEY (`usuario`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `FK_r5amy587nfobqlx0r0lslueqp` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`);

--
-- Filtros para la tabla `tarifa`
--
ALTER TABLE `tarifa`
  ADD CONSTRAINT `FK_cqcycganu12ywiidg20wvym09` FOREIGN KEY (`hotel`) REFERENCES `hotel` (`id`);

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `FK_aevanv0397v7gs2a47sb43sxs` FOREIGN KEY (`rol`) REFERENCES `rol` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
