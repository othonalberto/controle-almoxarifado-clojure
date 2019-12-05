create database almoxarifado;

use almoxarifado;

CREATE TABLE `Peca` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL DEFAULT '',
  `qtdDisponivel` int(11) NOT NULL,
  `qtdMinima` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `Pessoa` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cpf` varchar(11) NOT NULL DEFAULT '',
  `email` varchar(255) NOT NULL DEFAULT '',
  `cargo` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Retirada` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `datahoraRetirada` datetime NOT NULL,
  `idAlmoxarife` int(11) unsigned DEFAULT NULL,
  `idFuncionario` int(11) unsigned NOT NULL,
  `idPeca` int(11) unsigned NOT NULL,
  `quantidade` int(11) NOT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'aberta',
  PRIMARY KEY (`id`),
  KEY `IdAlmoxarife` (`IdAlmoxarife`),
  KEY `IdFuncionario` (`IdFuncionario`),
  KEY `IdPeca` (`IdPeca`),
  CONSTRAINT `retirada_ibfk_1` FOREIGN KEY (`IdAlmoxarife`) REFERENCES `Pessoa` (`id`),
  CONSTRAINT `retirada_ibfk_2` FOREIGN KEY (`IdFuncionario`) REFERENCES `Pessoa` (`id`),
  CONSTRAINT `retirada_ibfk_3` FOREIGN KEY (`IdPeca`) REFERENCES `Peca` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `AvisoQuantidadeMinima` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `datahoraAcontecimento` datetime NOT NULL,
  `idRetiradaVinculada` int(11) unsigned NOT NULL,
  `dataHoraEnvio` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idRetiradaVinculada` (`idRetiradaVinculada`),
  CONSTRAINT `avisoquantidademinima_ibfk_1` FOREIGN KEY (`idRetiradaVinculada`) REFERENCES `Retirada` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `Reposicao` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `datahoraChegada` datetime NOT NULL,
  `quantidadeReposta` int(11) NOT NULL,
  `idPeca` int(11) unsigned NOT NULL,
  `idAviso` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idPeca` (`idPeca`),
  KEY `idAviso` (`idAviso`),
  CONSTRAINT `idPeca` FOREIGN KEY (`idPeca`) REFERENCES `Peca` (`id`),
  CONSTRAINT `idAviso` FOREIGN KEY (`idAviso`) REFERENCES `AvisoQuantidadeMinima` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;