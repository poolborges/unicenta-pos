--    uniCenta oPOS - Touch Friendly Point Of Sale
--    Copyright (c) 2009-2017 uniCenta
--    http://sourceforge.net/projects/unicentaopos
--
--    This file is part of uniCenta oPOS.
--
--    uniCenta oPOS is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    uniCenta oPOS is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with uniCenta oPOS.  If not, see <http://www.gnu.org/licenses/>.

-- Database create script for MySQL
-- Copyright (c) 2009-2017 uniCenta
-- v4.00

CREATE TABLE `applications` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `version` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `roles` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `permissions` mediumblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `roles_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO ROLES(ID, NAME, PERMISSIONS) VALUES('0', 'Administrator role', $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} );
INSERT INTO ROLES(ID, NAME, PERMISSIONS) VALUES('1', 'Manager role', $FILE{/com/openbravo/pos/templates/Role.Manager.xml} );
INSERT INTO ROLES(ID, NAME, PERMISSIONS) VALUES('2', 'Employee role', $FILE{/com/openbravo/pos/templates/Role.Employee.xml} );
INSERT INTO ROLES(ID, NAME, PERMISSIONS) VALUES('3', 'Guest role', $FILE{/com/openbravo/pos/templates/Role.Guest.xml} );

CREATE TABLE `people` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `apppassword` varchar(255) DEFAULT NULL,
  `card` varchar(255) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  `visible` bit(1) NOT NULL,
  `image` mediumblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `people_name_inx` (`name`),
  KEY `people_fk_1` (`role`),
  KEY `people_card_inx` (`card`),
  CONSTRAINT `people_fk_1` FOREIGN KEY (`role`) REFERENCES `roles` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO PEOPLE(ID, NAME, APPPASSWORD, ROLE, VISIBLE, IMAGE) VALUES ('0', 'Administrator', NULL, '0', TRUE, NULL);
INSERT INTO PEOPLE(ID, NAME, APPPASSWORD, ROLE, VISIBLE, IMAGE) VALUES ('1', 'Manager', NULL, '1', TRUE, NULL);
INSERT INTO PEOPLE(ID, NAME, APPPASSWORD, ROLE, VISIBLE, IMAGE) VALUES ('2', 'Employee', NULL, '2', TRUE, NULL);
INSERT INTO PEOPLE(ID, NAME, APPPASSWORD, ROLE, VISIBLE, IMAGE) VALUES ('3', 'Guest', NULL, '3', TRUE, NULL);


CREATE TABLE `resources` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `restype` int(11) NOT NULL,
  `content` mediumblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resources_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('0', 'Menu.Root', 0, $FILE{/com/openbravo/pos/templates/Menu.Root.txt});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('1', 'coin.2', 1, $FILE{/com/openbravo/pos/templates/coin.2.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('2', 'coin.1', 1, $FILE{/com/openbravo/pos/templates/coin.1.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('3', 'coin.50', 1, $FILE{/com/openbravo/pos/templates/coin.50.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('4', 'coin.20', 1, $FILE{/com/openbravo/pos/templates/coin.20.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('5', 'coin.10', 1, $FILE{/com/openbravo/pos/templates/coin.10.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('6', 'coin.05', 1, $FILE{/com/openbravo/pos/templates/coin.05.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('7', 'coin.02', 1, $FILE{/com/openbravo/pos/templates/coin.02.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('8', 'coin.01', 1, $FILE{/com/openbravo/pos/templates/coin.01.png});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('9', 'img.cash', 1, $FILE{/com/openbravo/pos/templates/img.cash.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('10', 'img.cashdrawer', 1, $FILE{/com/openbravo/pos/templates/img.cashdrawer.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('11', 'img.discount', 1, $FILE{/com/openbravo/pos/templates/img.discount.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('12', 'img.empty', 1, $FILE{/com/openbravo/pos/templates/img.empty.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('13', 'img.heart', 1, $FILE{/com/openbravo/pos/templates/img.heart.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('14', 'img.no_photo', 1, $FILE{/com/openbravo/pos/templates/img.no_photo.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('15', 'img.kit_print', 1, $FILE{/com/openbravo/pos/templates/img.kit_print.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('16', 'img.refundit', 1, $FILE{/com/openbravo/pos/templates/img.refundit.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('17', 'img.run_script', 1, $FILE{/com/openbravo/pos/templates/img.run_script.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('18', 'img.ticket_print', 1, $FILE{/com/openbravo/pos/templates/img.ticket_print.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('19', 'img.user', 1, $FILE{/com/openbravo/pos/templates/img.user.png});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('20', 'note.50', 1, $FILE{/com/openbravo/pos/templates/note.50.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('21', 'note.20', 1, $FILE{/com/openbravo/pos/templates/note.20.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('22', 'note.10', 1, $FILE{/com/openbravo/pos/templates/note.10.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('23', 'note.5', 1, $FILE{/com/openbravo/pos/templates/note.5.png});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('24', 'payment.cash', 0, $FILE{/com/openbravo/pos/templates/payment.cash.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('25', 'Printer.CloseCash', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('26', 'Printer.CustomerPaid', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('27', 'Printer.CustomerPaid2', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid2.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('28', 'Printer.FiscalTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.FiscalTicket.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('29', 'Printer.Inventory', 0, $FILE{/com/openbravo/pos/templates/Printer.Inventory.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('30', 'Printer.OpenDrawer', 0, $FILE{/com/openbravo/pos/templates/Printer.OpenDrawer.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('31', 'Printer.PartialCash', 0, $FILE{/com/openbravo/pos/templates/Printer.PartialCash.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('32', 'Printer.Product', 0, $FILE{/com/openbravo/pos/templates/Printer.Product.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('33', 'Printer.Start', 0, $FILE{/com/openbravo/pos/templates/Printer.Start.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('34', 'Printer.Ticket', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('35', 'Printer.Ticket2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket2.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('36', 'Printer.TicketClose', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketClose.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('37', 'Printer.TicketRemote', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketRemote.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('38', 'Printer.TicketLine', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('39', 'Printer.TicketNew', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('40', 'Printer.TicketPreview', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketPreview.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('41', 'Printer.TicketTotal', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketTotal.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('42', 'Printer.Ticket.Logo', 1, $FILE{/com/openbravo/pos/templates/printer.ticket.logo.png});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('43', 'script.AddLineNote', 0, $FILE{/com/openbravo/pos/templates/script.AddLineNote.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('44', 'script.Event.Total', 0, $FILE{/com/openbravo/pos/templates/script.Event.Total.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('45', 'script.linediscount', 0, $FILE{/com/openbravo/pos/templates/script.linediscount.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('46', 'script.ReceiptConsolidate', 0, $FILE{/com/openbravo/pos/templates/script.ReceiptConsolidate.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('47', 'script.Refundit', 0, $FILE{/com/openbravo/pos/templates/script.Refundit.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('48', 'script.SendOrder', 0, $FILE{/com/openbravo/pos/templates/script.SendOrder.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('49', 'script.ServiceCharge', 0, $FILE{/com/openbravo/pos/templates/script.script.ServiceCharge.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('50', 'script.SetPerson', 0, $FILE{/com/openbravo/pos/templates/script.SetPerson.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('51', 'script.StockCurrentAdd', 0, $FILE{/com/openbravo/pos/templates/script.StockCurrentAdd.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('52', 'script.StockCurrentSet', 0, $FILE{/com/openbravo/pos/templates/script.StockCurrentSet.txt});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('53', 'script.totaldiscount', 0, $FILE{/com/openbravo/pos/templates/script.totaldiscount.txt});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('54', 'Ticket.Buttons', 0, $FILE{/com/openbravo/pos/templates/Ticket.Buttons.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('55', 'Ticket.Close', 0, $FILE{/com/openbravo/pos/templates/Ticket.Close.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('56', 'Ticket.Discount', 0, $FILE{/com/openbravo/pos/templates/Ticket.Discount.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('57', 'Ticket.Line', 0, $FILE{/com/openbravo/pos/templates/Ticket.Line.xml});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('58', 'Ticket.TicketLineTaxesIncluded', 0, $FILE{/com/openbravo/pos/templates/Ticket.TicketLineTaxesIncluded.xml});

INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('59', 'Window.Logo', 1, $FILE{/com/openbravo/pos/templates/window.logo.png});
INSERT INTO RESOURCES(ID, NAME, RESTYPE, CONTENT) VALUES('60', 'Window.Title', 0, $FILE{/com/openbravo/pos/templates/Window.Title.txt});

CREATE TABLE `taxcustcategories` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `taxcustcat_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `customers` (
  `id` varchar(255) NOT NULL,
  `searchkey` varchar(255) NOT NULL,
  `taxid` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `taxcategory` varchar(255) DEFAULT NULL,
  `card` varchar(255) DEFAULT NULL,
  `maxdebt` double NOT NULL DEFAULT '0',
  `address` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `postal` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `phone2` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `visible` bit(1) NOT NULL DEFAULT b'1',
  `curdate` datetime DEFAULT NULL,
  `curdebt` double DEFAULT '0',
  `image` mediumblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `customers_skey_inx` (`searchkey`),
  KEY `customers_taxcat` (`taxcategory`),
  KEY `customers_taxid_inx` (`taxid`),
  KEY `customers_name_inx` (`name`),
  KEY `customers_card_inx` (`card`),
  CONSTRAINT `customers_taxcat` FOREIGN KEY (`taxcategory`) REFERENCES `taxcustcategories` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `taxsuppcategories` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `taxsuppcat_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `suppliers` (
  `id` varchar(255) NOT NULL,
  `searchkey` varchar(255) NOT NULL,
  `taxid` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `taxcategory` varchar(255) DEFAULT NULL,
  `card` varchar(255) DEFAULT NULL,
  `maxdebt` double NOT NULL DEFAULT '0',
  `address` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `postal` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `phone2` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `visible` bit(1) NOT NULL DEFAULT b'1',
  `curdate` datetime DEFAULT NULL,
  `curdebt` double DEFAULT '0',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `suppliers_skey_inx` (`searchkey`),
  KEY `suppliers_taxcat` (`taxcategory`),
  KEY `suppliers_taxid_inx` (`taxid`),
  KEY `suppliers_name_inx` (`name`),
  CONSTRAINT `suppliers_taxcat` FOREIGN KEY (`taxcategory`) REFERENCES `taxsuppcategories` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `categories` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `parentid` varchar(255) DEFAULT NULL,
  `image` mediumblob,
  `texttip` varchar(255) DEFAULT NULL,
  `catshowname` smallint(6) NOT NULL DEFAULT '1',
  `catorder` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `categories_name_inx` (`name`),
  KEY `categories_fk_1` (`parentid`),
  CONSTRAINT `categories_fk_1` FOREIGN KEY (`parentid`) REFERENCES `categories` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO CATEGORIES(ID, NAME) VALUES ('000', 'Category Standard');

CREATE TABLE `taxcategories` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `taxcat_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO TAXCATEGORIES(ID, NAME) VALUES ('000', 'Tax Exempt');
INSERT INTO TAXCATEGORIES(ID, NAME) VALUES ('001', 'Tax Standard');

CREATE TABLE `taxes` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `custcategory` varchar(255) DEFAULT NULL,
  `parentid` varchar(255) DEFAULT NULL,
  `rate` double NOT NULL DEFAULT '0',
  `ratecascade` bit(1) NOT NULL DEFAULT b'0',
  `rateorder` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `taxes_name_inx` (`name`),
  KEY `taxes_cat_fk` (`category`),
  KEY `taxes_custcat_fk` (`custcategory`),
  KEY `taxes_taxes_fk` (`parentid`),
  CONSTRAINT `taxes_cat_fk` FOREIGN KEY (`category`) REFERENCES `taxcategories` (`id`),
  CONSTRAINT `taxes_custcat_fk` FOREIGN KEY (`custcategory`) REFERENCES `taxcustcategories` (`id`),
  CONSTRAINT `taxes_taxes_fk` FOREIGN KEY (`parentid`) REFERENCES `taxes` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO TAXES(ID, NAME, CATEGORY, CUSTCATEGORY, PARENTID, RATE, RATECASCADE, RATEORDER) VALUES ('000', 'Tax Exempt', '000', NULL, NULL, 0, FALSE, NULL);
INSERT INTO TAXES(ID, NAME, CATEGORY, CUSTCATEGORY, PARENTID, RATE, RATECASCADE, RATEORDER) VALUES ('001', 'Tax Standard', '001', NULL, NULL, 0.20, FALSE, NULL);

CREATE TABLE `attribute` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `attributevalue` (
  `id` varchar(255) NOT NULL,
  `attribute_id` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `attval_att` (`attribute_id`),
  CONSTRAINT `attval_att` FOREIGN KEY (`attribute_id`) REFERENCES `attribute` (`id`) ON DELETE CASCADE
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `attributeset` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `attributeuse` (
  `id` varchar(255) NOT NULL,
  `attributeset_id` varchar(255) NOT NULL,
  `attribute_id` varchar(255) NOT NULL,
  `lineno` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `attuse_line` (`attributeset_id`,`lineno`),
  KEY `attuse_att` (`attribute_id`),
  CONSTRAINT `attuse_att` FOREIGN KEY (`attribute_id`) REFERENCES `attribute` (`id`),
  CONSTRAINT `attuse_set` FOREIGN KEY (`attributeset_id`) REFERENCES `attributeset` (`id`) ON DELETE CASCADE
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `attributesetinstance` (
  `id` varchar(255) NOT NULL,
  `attributeset_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `attsetinst_set` (`attributeset_id`),
  CONSTRAINT `attsetinst_set` FOREIGN KEY (`attributeset_id`) REFERENCES `attributeset` (`id`) ON DELETE CASCADE
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `attributeinstance` (
  `id` varchar(255) NOT NULL,
  `attributesetinstance_id` varchar(255) NOT NULL,
  `attribute_id` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `attinst_set` (`attributesetinstance_id`),
  KEY `attinst_att` (`attribute_id`),
  CONSTRAINT `attinst_att` FOREIGN KEY (`attribute_id`) REFERENCES `attribute` (`id`),
  CONSTRAINT `attinst_set` FOREIGN KEY (`attributesetinstance_id`) REFERENCES `attributesetinstance` (`id`) ON DELETE CASCADE
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `products` (
  `id` varchar(255) NOT NULL,
  `reference` varchar(255) NOT NULL,
  `code` varchar(255) NOT NULL,
  `codetype` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `pricebuy` double NOT NULL DEFAULT '0',
  `pricesell` double NOT NULL DEFAULT '0',
  `category` varchar(255) NOT NULL,
  `taxcat` varchar(255) NOT NULL,
  `attributeset_id` varchar(255) DEFAULT NULL,
  `stockcost` double DEFAULT NULL,
  `stockvolume` double DEFAULT NULL,
  `image` mediumblob,
  `iscom` bit(1) NOT NULL DEFAULT b'0',
  `isscale` bit(1) NOT NULL DEFAULT b'0',
  `isconstant` bit(1) NOT NULL DEFAULT b'0',
  `printkb` bit(1) NOT NULL DEFAULT b'0',
  `sendstatus` bit(1) NOT NULL DEFAULT b'0',
  `isservice` bit(1) NOT NULL DEFAULT b'0',
  `attributes` mediumblob,
  `display` varchar(255) DEFAULT NULL,
  `isvprice` smallint(6) NOT NULL DEFAULT '0',
  `isverpatrib` smallint(6) NOT NULL DEFAULT '0',
  `texttip` varchar(255) DEFAULT NULL,
  `warranty` smallint(6) NOT NULL DEFAULT '0',
  `stockunits` double NOT NULL DEFAULT '0',
  `printto` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `products_inx_0` (`reference`),
  UNIQUE KEY `products_inx_1` (`code`),
  UNIQUE KEY `products_name_inx` (`name`),
  KEY `products_fk_1` (`category`),
  KEY `products_taxcat_fk` (`taxcat`),
  KEY `products_attrset_fk` (`attributeset_id`),
  CONSTRAINT `products_attrset_fk` FOREIGN KEY (`attributeset_id`) REFERENCES `attributeset` (`id`),
  CONSTRAINT `products_fk_1` FOREIGN KEY (`category`) REFERENCES `categories` (`id`),
  CONSTRAINT `products_taxcat_fk` FOREIGN KEY (`taxcat`) REFERENCES `taxcategories` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO PRODUCTS(ID, REFERENCE, CODE, NAME, CATEGORY, TAXCAT, ISSERVICE) 
VALUES ('xxx999_999xxx_x9x9x9', 'xxx999', 'xxx999', '***', '000', '001', 1);
INSERT INTO PRODUCTS(ID, REFERENCE, CODE, NAME, CATEGORY, TAXCAT, ISSERVICE) 
VALUES ('xxx998_998xxx_x8x8x8', 'xxx998', 'xxx998', '***', '000', '001', 1);

CREATE TABLE `products_cat` (
  `product` varchar(255) NOT NULL,
  `catorder` int(11) DEFAULT NULL,
  PRIMARY KEY (`product`),
  KEY `products_cat_inx_1` (`catorder`),
  CONSTRAINT `products_cat_fk_1` FOREIGN KEY (`product`) REFERENCES `products` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO PRODUCTS_CAT(PRODUCT) VALUES ('xxx999_999xxx_x9x9x9');
INSERT INTO PRODUCTS_CAT(PRODUCT) VALUES ('xxx998_998xxx_x8x8x8');

CREATE TABLE `products_com` (
  `id` varchar(255) NOT NULL,
  `product` varchar(255) NOT NULL,
  `product2` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pcom_inx_prod` (`product`,`product2`),
  KEY `products_com_fk_2` (`product2`),
  CONSTRAINT `products_com_fk_1` FOREIGN KEY (`product`) REFERENCES `products` (`id`),
  CONSTRAINT `products_com_fk_2` FOREIGN KEY (`product2`) REFERENCES `products` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `locations` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `locations_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `stockdiary` (
  `id` varchar(255) NOT NULL,
  `datenew` datetime NOT NULL,
  `reason` int(11) NOT NULL,
  `location` varchar(255) NOT NULL,
  `product` varchar(255) NOT NULL,
  `attributesetinstance_id` varchar(255) DEFAULT NULL,
  `units` double NOT NULL,
  `price` double NOT NULL,
  `appuser` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `stockdiary_fk_1` (`product`),
  KEY `stockdiary_attsetinst` (`attributesetinstance_id`),
  KEY `stockdiary_fk_2` (`location`),
  KEY `stockdiary_inx_1` (`datenew`),
  CONSTRAINT `stockdiary_attsetinst` FOREIGN KEY (`attributesetinstance_id`) REFERENCES `attributesetinstance` (`id`),
  CONSTRAINT `stockdiary_fk_1` FOREIGN KEY (`product`) REFERENCES `products` (`id`),
  CONSTRAINT `stockdiary_fk_2` FOREIGN KEY (`location`) REFERENCES `locations` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `stocklevel` (
  `id` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `product` varchar(255) NOT NULL,
  `stocksecurity` double DEFAULT NULL,
  `stockmaximum` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `stocklevel_product` (`product`),
  KEY `stocklevel_location` (`location`),
  CONSTRAINT `stocklevel_location` FOREIGN KEY (`location`) REFERENCES `locations` (`id`),
  CONSTRAINT `stocklevel_product` FOREIGN KEY (`product`) REFERENCES `products` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `stockcurrent` (
  `location` varchar(255) NOT NULL,
  `product` varchar(255) NOT NULL,
  `attributesetinstance_id` varchar(255) DEFAULT NULL,
  `units` double NOT NULL,
  UNIQUE KEY `stockcurrent_inx` (`location`,`product`,`attributesetinstance_id`),
  KEY `stockcurrent_fk_1` (`product`),
  KEY `stockcurrent_attsetinst` (`attributesetinstance_id`),
  CONSTRAINT `stockcurrent_attsetinst` FOREIGN KEY (`attributesetinstance_id`) REFERENCES `attributesetinstance` (`id`),
  CONSTRAINT `stockcurrent_fk_1` FOREIGN KEY (`product`) REFERENCES `products` (`id`),
  CONSTRAINT `stockcurrent_fk_2` FOREIGN KEY (`location`) REFERENCES `locations` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `closedcash` (
  `money` varchar(255) NOT NULL,
  `host` varchar(255) NOT NULL,
  `hostsequence` int(11) NOT NULL,
  `datestart` datetime NOT NULL,
  `dateend` datetime DEFAULT NULL,
  `nosales` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`money`),
  UNIQUE KEY `closedcash_inx_seq` (`host`,`hostsequence`),
  KEY `closedcash_inx_1` (`datestart`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `receipts` (
  `id` varchar(255) NOT NULL,
  `money` varchar(255) NOT NULL,
  `datenew` datetime NOT NULL,
  `attributes` mediumblob,
  `person` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `receipts_fk_money` (`money`),
  KEY `receipts_inx_1` (`datenew`),
  CONSTRAINT `receipts_fk_money` FOREIGN KEY (`money`) REFERENCES `closedcash` (`money`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `tickets` (
  `id` varchar(255) NOT NULL,
  `tickettype` int(11) NOT NULL DEFAULT '0',
  `ticketid` int(11) NOT NULL,
  `person` varchar(255) NOT NULL,
  `customer` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `tickets_fk_2` (`person`),
  KEY `tickets_customers_fk` (`customer`),
  KEY `tickets_ticketid` (`tickettype`,`ticketid`),
  CONSTRAINT `tickets_customers_fk` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`),
  CONSTRAINT `tickets_fk_2` FOREIGN KEY (`person`) REFERENCES `people` (`id`),
  CONSTRAINT `tickets_fk_id` FOREIGN KEY (`id`) REFERENCES `receipts` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `ticketsnum` (
  `id` int(11) NOT NULL
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO TICKETSNUM VALUES(1);


CREATE TABLE `ticketsnum_refund` (
  `id` int(11) NOT NULL
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO TICKETSNUM_REFUND VALUES(1);

CREATE TABLE `ticketsnum_payment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=innodb AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
INSERT INTO TICKETSNUM_PAYMENT VALUES (DEFAULT);

CREATE TABLE `ticketlines` (
  `ticket` varchar(255) NOT NULL,
  `line` int(11) NOT NULL,
  `product` varchar(255) DEFAULT NULL,
  `attributesetinstance_id` varchar(255) DEFAULT NULL,
  `units` double NOT NULL,
  `price` double NOT NULL,
  `taxid` varchar(255) NOT NULL,
  `attributes` mediumblob,
  PRIMARY KEY (`ticket`,`line`),
  KEY `ticketlines_fk_2` (`product`),
  KEY `ticketlines_attsetinst` (`attributesetinstance_id`),
  KEY `ticketlines_fk_3` (`taxid`),
  CONSTRAINT `ticketlines_attsetinst` FOREIGN KEY (`attributesetinstance_id`) REFERENCES `attributesetinstance` (`id`),
  CONSTRAINT `ticketlines_fk_2` FOREIGN KEY (`product`) REFERENCES `products` (`id`),
  CONSTRAINT `ticketlines_fk_3` FOREIGN KEY (`taxid`) REFERENCES `taxes` (`id`),
  CONSTRAINT `ticketlines_fk_ticket` FOREIGN KEY (`ticket`) REFERENCES `tickets` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `lineremoved` (
  `removeddate` timestamp NOT NULL DEFAULT current_timestamp,
  `name` varchar(255) DEFAULT NULL,
  `ticketid` varchar(255) DEFAULT NULL,
  `productid` varchar(255) DEFAULT NULL,
  `productname` varchar(255) DEFAULT NULL,
  `units` double NOT NULL
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `payments` (
  `id` varchar(255) NOT NULL,
  `receipt` varchar(255) NOT NULL,
  `payment` varchar(255) NOT NULL,
  `total` double NOT NULL DEFAULT '0',
  `tip` double DEFAULT '0',
  `transid` varchar(255) DEFAULT NULL,
  `isprocessed` bit(1) DEFAULT b'0',
  `returnmsg` mediumblob,
  `notes` varchar(255) DEFAULT NULL,
  `tendered` double DEFAULT NULL,
  `cardname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `payments_fk_receipt` (`receipt`),
  KEY `payments_inx_1` (`payment`),
  CONSTRAINT `payments_fk_receipt` FOREIGN KEY (`receipt`) REFERENCES `receipts` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `taxlines` (
  `id` varchar(255) NOT NULL,
  `receipt` varchar(255) NOT NULL,
  `taxid` varchar(255) NOT NULL,
  `base` double NOT NULL DEFAULT '0',
  `amount` double NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `taxlines_tax` (`taxid`),
  KEY `taxlines_receipt` (`receipt`),
  CONSTRAINT `taxlines_receipt` FOREIGN KEY (`receipt`) REFERENCES `receipts` (`id`),
  CONSTRAINT `taxlines_tax` FOREIGN KEY (`taxid`) REFERENCES `taxes` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `floors` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `image` mediumblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `floors_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO FLOORS(ID, NAME, IMAGE) VALUES ('0', 'Restaurant floor', $FILE{/com/openbravo/pos/templates/restaurant_floor.png});

CREATE TABLE `places` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `floor` varchar(255) NOT NULL,
  `customer` varchar(255) DEFAULT NULL,
  `waiter` varchar(255) DEFAULT NULL,
  `ticketid` varchar(255) DEFAULT NULL,
  `tablemoved` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `places_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('1', 'Table 1', 100, 50, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('2', 'Table 2', 250, 50, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('3', 'Table 3', 400, 50, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('4', 'Table 4', 550, 50, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('5', 'Table 5', 700, 50, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('6', 'Table 6', 850, 50, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('7', 'Table 7', 100, 150, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('8', 'Table 8', 250, 150, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('9', 'Table 9', 400, 150, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('10', 'Table 10', 550, 150, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('11', 'Table 11', 700, 150, '0');
INSERT INTO PLACES(ID, NAME, X, Y, FLOOR) VALUES ('12', 'Table 12', 850, 150, '0');

CREATE TABLE `reservations` (
  `id` varchar(255) NOT NULL,
  `created` datetime NOT NULL,
  `datenew` datetime NOT NULL DEFAULT '2013-01-01 00:00:00',
  `title` varchar(255) NOT NULL,
  `chairs` int(11) NOT NULL,
  `isdone` bit(1) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `reservations_inx_1` (`datenew`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `reservation_customers` (
  `id` varchar(255) NOT NULL,
  `customer` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `res_cust_fk_2` (`customer`),
  CONSTRAINT `res_cust_fk_1` FOREIGN KEY (`id`) REFERENCES `reservations` (`id`),
  CONSTRAINT `res_cust_fk_2` FOREIGN KEY (`customer`) REFERENCES `customers` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `thirdparties` (
  `id` varchar(255) NOT NULL,
  `cif` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `contactcomm` varchar(255) DEFAULT NULL,
  `contactfact` varchar(255) DEFAULT NULL,
  `payrule` varchar(255) DEFAULT NULL,
  `faxnumber` varchar(255) DEFAULT NULL,
  `phonenumber` varchar(255) DEFAULT NULL,
  `mobilenumber` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `webpage` varchar(255) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `thirdparties_cif_inx` (`cif`),
  UNIQUE KEY `thirdparties_name_inx` (`name`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `sharedtickets` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `content` mediumblob,
  `appuser` varchar(255) DEFAULT NULL,
  `pickupid` smallint(6) NOT NULL DEFAULT '0',
  `locked` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

-- Added for Employee Presence Management
CREATE TABLE `shifts` (
  `id` varchar(255) NOT NULL,
  `startshift` datetime NOT NULL,
  `endshift` datetime DEFAULT NULL,
  `pplid` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO SHIFTS(ID, STARTSHIFT, ENDSHIFT, PPLID) VALUES ('0', '2017-01-01 00:00:00.001', '2017-01-01 00:00:00.002','0');

CREATE TABLE `leaves` (
  `id` varchar(255) NOT NULL,
  `pplid` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `startdate` datetime NOT NULL,
  `enddate` datetime NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `leaves_pplid` (`pplid`),
  CONSTRAINT `leaves_pplid` FOREIGN KEY (`pplid`) REFERENCES `people` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `breaks` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `visible` tinyint(1) NOT NULL DEFAULT '1',
  `notes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO BREAKS(ID, NAME, VISIBLE, NOTES) VALUES ('0', 'Lunch Break', TRUE, NULL);
INSERT INTO BREAKS(ID, NAME, VISIBLE, NOTES) VALUES ('1', 'Tea Break', TRUE, NULL);
INSERT INTO BREAKS(ID, NAME, VISIBLE, NOTES) VALUES ('2', 'Mid Break', TRUE, NULL);

CREATE TABLE `shift_breaks` (
  `id` varchar(255) NOT NULL,
  `shiftid` varchar(255) NOT NULL,
  `breakid` varchar(255) NOT NULL,
  `starttime` timestamp NOT NULL DEFAULT current_timestamp ON UPDATE current_timestamp,
  `endtime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `shift_breaks_breakid` (`breakid`),
  KEY `shift_breaks_shiftid` (`shiftid`),
  CONSTRAINT `shift_breaks_breakid` FOREIGN KEY (`breakid`) REFERENCES `breaks` (`id`),
  CONSTRAINT `shift_breaks_shiftid` FOREIGN KEY (`shiftid`) REFERENCES `shifts` (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO SHIFT_BREAKS(ID, SHIFTID, BREAKID, STARTTIME, ENDTIME) VALUES ('0', '0', '0', '2017-01-01 00:00:00.003', '2017-01-01 00:00:00.004');

CREATE TABLE `moorers` (
  `vesselname` varchar(255) DEFAULT NULL,
  `size` int(11) DEFAULT NULL,
  `days` int(11) DEFAULT NULL,
  `power` bit(1) NOT NULL DEFAULT b'0'
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `csvimport` (
  `id` varchar(255) NOT NULL,
  `rownumber` varchar(255) DEFAULT NULL,
  `csverror` varchar(255) DEFAULT NULL,
  `reference` varchar(1024) DEFAULT NULL,
  `code` varchar(1024) DEFAULT NULL,
  `name` varchar(1024) DEFAULT NULL,
  `pricebuy` double DEFAULT NULL,
  `pricesell` double DEFAULT NULL,
  `previousbuy` double DEFAULT NULL,
  `previoussell` double DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=innodb DEFAULT CHARSET=utf8;

CREATE TABLE `pickup_number` (
  `id` int(11) NOT NULL DEFAULT '0'
) ENGINE=innodb DEFAULT CHARSET=utf8;
INSERT INTO PICKUP_NUMBER VALUES(1);

CREATE TABLE `draweropened` (
  `opendate` timestamp NOT NULL DEFAULT current_timestamp,
  `name` varchar(255) DEFAULT NULL,
  `ticketid` varchar(255) DEFAULT NULL
) ENGINE=innodb DEFAULT CHARSET=utf8;

INSERT INTO applications(id, name, version) VALUES($APP_ID{}, $APP_NAME{}, $APP_VERSION{});