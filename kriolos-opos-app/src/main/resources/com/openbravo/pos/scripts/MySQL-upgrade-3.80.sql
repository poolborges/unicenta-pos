--    KrOS POS - Open Source Point Of Sale
--    Copyright (c) 2009-2017 uniCenta
--    
--    This program is free software: you can redistribute it and/or modify
--    it under the terms of the GNU General Public License as published by
--    the Free Software Foundation, either version 3 of the License, or
--    (at your option) any later version.
--
--    This program is distributed in the hope that it will be useful,
--    but WITHOUT ANY WARRANTY; without even the implied warranty of
--    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--    GNU General Public License for more details.
--
--    You should have received a copy of the GNU General Public License
--    along with this program.  If not, see <http://www.gnu.org/licenses/>.

-- Database upgrade script for MySQL
-- v3.80 - v4.3.0

--
-- CLEAR THE DECKS
--
DELETE FROM sharedtickets;

/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

/* Foreign Keys must be dropped in the target to ensure that requires changes can be done*/

/* Alter table in target */
ALTER TABLE `applications` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `attribute` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `attributeinstance` 
	DROP KEY `ATTINST_ATT` , 
	ADD KEY `attinst_att`(`attribute_id`) , 
	DROP KEY `ATTINST_SET` , 
	ADD KEY `attinst_set`(`attributesetinstance_id`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `attributeset` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `attributesetinstance` 
	DROP KEY `ATTSETINST_SET` , 
	ADD KEY `attsetinst_set`(`attributeset_id`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `attributeuse` 
	DROP KEY `ATTUSE_ATT` , 
	ADD KEY `attuse_att`(`attribute_id`) , 
	ADD UNIQUE KEY `attuse_line`(`attributeset_id`,`lineno`) , 
	DROP KEY `ATTUSE_LINE` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `attributevalue` 
	DROP KEY `ATTVAL_ATT` , 
	ADD KEY `attval_att`(`attribute_id`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `breaks` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `categories` 
	ADD COLUMN `catorder` varchar(255)  COLLATE utf8_general_ci NULL after `catshowname` , 
	DROP KEY `categories_fk_1` , 
	ADD KEY `categories_fk_1`(`parentid`) , 
	ADD UNIQUE KEY `categories_name_inx`(`name`) , 
	DROP KEY `CATEGORIES_NAME_INX` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `closedcash` 
	DROP KEY `CLOSEDCASH_INX_1` , 
	ADD KEY `closedcash_inx_1`(`datestart`) , 
	ADD UNIQUE KEY `closedcash_inx_seq`(`host`,`hostsequence`) , 
	DROP KEY `CLOSEDCASH_INX_SEQ` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`money`) ;

/* Alter table in target */
ALTER TABLE `csvimport` 
	CHANGE `id` `id` varchar(255)  COLLATE utf8_general_ci NOT NULL first , 
	CHANGE `rownumber` `rownumber` varchar(255)  COLLATE utf8_general_ci NULL after `id` , 
	CHANGE `csverror` `csverror` varchar(255)  COLLATE utf8_general_ci NULL after `rownumber` , 
	CHANGE `reference` `reference` varchar(1024)  COLLATE utf8_general_ci NULL after `csverror` , 
	CHANGE `code` `code` varchar(1024)  COLLATE utf8_general_ci NULL after `reference` , 
	CHANGE `name` `name` varchar(1024)  COLLATE utf8_general_ci NULL after `code` , 
	CHANGE `category` `category` varchar(255)  COLLATE utf8_general_ci NULL after `previoussell` , 
	ADD COLUMN `tax` varchar(255)  COLLATE utf8_general_ci NULL after `category` , 
	ADD COLUMN `searchkey` varchar(255)  COLLATE utf8_general_ci NULL after `tax` ,
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , DEFAULT CHARSET='utf8', COLLATE ='utf8_general_ci' ;

/* Alter table in target */
ALTER TABLE `customers` 
	ADD COLUMN `isvip` bit(1)   NOT NULL DEFAULT b'0' after `image` , 
	ADD COLUMN `discount` double   NULL DEFAULT 0 after `isvip` , 
	ADD COLUMN `memodate` datetime default '1900-01-01 00:00:01' after `discount` ,
	ADD KEY `customers_card_inx`(`card`) , 
	DROP KEY `CUSTOMERS_CARD_INX` , 
	ADD KEY `customers_name_inx`(`name`) , 
	DROP KEY `CUSTOMERS_NAME_INX` , 
	ADD UNIQUE KEY `customers_skey_inx`(`searchkey`) , 
	DROP KEY `CUSTOMERS_SKEY_INX` , 
	ADD KEY `customers_taxcat`(`taxcategory`) , 
	DROP KEY `CUSTOMERS_TAXCAT` , 
	DROP KEY `CUSTOMERS_TAXID_INX` , 
	ADD KEY `customers_taxid_inx`(`taxid`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `draweropened` 
	CHANGE `opendate` `opendate` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP first , 
	CHANGE `name` `name` varchar(255)  COLLATE utf8_general_ci NULL after `opendate` , 
	CHANGE `ticketid` `ticketid` varchar(255)  COLLATE utf8_general_ci NULL after `name` , DEFAULT CHARSET='utf8', COLLATE ='utf8_general_ci' ;

/* Alter table in target */
ALTER TABLE `floors` 
	DROP KEY `FLOORS_NAME_INX` , 
	ADD UNIQUE KEY `floors_name_inx`(`name`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `leaves` 
	DROP KEY `lEAVES_PPLID` , 
	ADD KEY `leaves_pplid`(`pplid`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `lineremoved` 
	CHANGE `removeddate` `removeddate` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP first ;

/* Alter table in target */
ALTER TABLE `locations` 
	DROP KEY `LOCATIONS_NAME_INX` , 
	ADD UNIQUE KEY `locations_name_inx`(`name`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `moorers` 
	CHANGE `vesselname` `vesselname` varchar(255)  COLLATE utf8_general_ci NULL first , DEFAULT CHARSET='utf8', COLLATE ='utf8_general_ci' ;

/* Alter table in target */
ALTER TABLE `payments` 
	ADD COLUMN `tip` double   NULL DEFAULT 0 after `total` , 
	CHANGE `transid` `transid` varchar(255)  COLLATE utf8_general_ci NULL after `tip` , 
	ADD COLUMN `isprocessed` bit(1)   NULL DEFAULT b'0' after `transid` , 
	CHANGE `returnmsg` `returnmsg` mediumblob   NULL after `isprocessed` , 
	ADD COLUMN `voucher` varchar(255)  COLLATE utf8_general_ci NULL after `cardname` , 
	DROP KEY `PAYMENTS_FK_RECEIPT` , 
	ADD KEY `payments_fk_receipt`(`receipt`) , 
	DROP KEY `PAYMENTS_INX_1` , 
	ADD KEY `payments_inx_1`(`payment`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `people` 
	ADD KEY `people_card_inx`(`card`) , 
	DROP KEY `PEOPLE_CARD_INX` , 
	DROP KEY `PEOPLE_FK_1` , 
	ADD KEY `people_fk_1`(`role`) , 
	ADD UNIQUE KEY `people_name_inx`(`name`) , 
	DROP KEY `PEOPLE_NAME_INX` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `places` 
	DROP KEY `PLACES_FK_1` , 
	DROP KEY `PLACES_NAME_INX` , 
	ADD UNIQUE KEY `places_name_inx`(`name`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	DROP FOREIGN KEY `PLACES_FK_1`  ;

/* Alter table in target */
ALTER TABLE `products` 
	ADD COLUMN `isconstant` bit(1)   NOT NULL DEFAULT b'0' after `isscale` , 
--	ADD COLUMN `stockunits` double  NOT NULL DEFAULT '0' after `warranty` , 
	CHANGE COLUMN `printkb` `printkb` bit(1)   NOT NULL DEFAULT b'0' after `isconstant` , 
	CHANGE COLUMN `texttip` `texttip` varchar(255)  COLLATE utf8_general_ci NULL after `isverpatrib` , 

	ADD COLUMN `printto` varchar(255)  COLLATE utf8_general_ci NULL DEFAULT '1' after `stockunits` , 
	ADD COLUMN `supplier` varchar(255)  COLLATE utf8_general_ci NULL after `printto` , 
	ADD COLUMN `uom` varchar(255)  COLLATE utf8_general_ci NULL DEFAULT '0' after `supplier` , 
	ADD COLUMN `memodate` datetime default '1900-01-01 00:00:01' after `uom` ,
	DROP COLUMN `ISKITCHEN` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	DROP KEY `PRODUCTS_ATTRSET_FK` , 
	ADD KEY `products_attrset_fx`(`attributeset_id`) , 
	DROP KEY `PRODUCTS_FK_1` , 
	ADD KEY `products_fk_1`(`category`) , 
	DROP KEY `PRODUCTS_INX_0` , 
	ADD UNIQUE KEY `products_inx_0`(`reference`) , 
	DROP KEY `PRODUCTS_INX_1` , 
	ADD UNIQUE KEY `products_inx_1`(`code`) , 
	DROP KEY `PRODUCTS_NAME_INX` , 
--	ADD UNIQUE KEY `products_name_inx`(`name`) , 
	ADD KEY `products_name_inx`(`name`) , 
	ADD KEY `products_taxcat_fk`(`taxcat`) , 
	DROP KEY `PRODUCTS_TAXCAT_FK` ;

/* Alter table in target */
ALTER TABLE `products_cat` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`product`) , 
	ADD KEY `products_cat_inx_1`(`catorder`) , 
	DROP KEY `PRODUCTS_CAT_INX_1` ;

/* Alter table in target */
ALTER TABLE `products_com` 
	DROP KEY `PCOM_INX_PROD` , 
	ADD UNIQUE KEY `pcom_inx_prod`(`product`,`product2`) , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD KEY `products_com_fk_2`(`product2`) , 
	DROP KEY `PRODUCTS_COM_FK_2` ;

/* Alter table in target */
ALTER TABLE `receipts` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD KEY `receipts_fk_money`(`money`) , 
	DROP KEY `RECEIPTS_FK_MONEY` , 
	ADD KEY `receipts_inx_1`(`datenew`) , 
	DROP KEY `RECEIPTS_INX_1` ;

/* Alter table in target */
ALTER TABLE `reservation_customers` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD KEY `res_cust_fk_2`(`customer`) , 
	DROP KEY `RES_CUST_FK_2` ;

/* Alter table in target */
ALTER TABLE `reservations` 
	CHANGE `datenew` `datenew` datetime   NOT NULL DEFAULT '2017-01-01 00:00:00' after `created` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD KEY `reservations_inx_1`(`datenew`) , 
	DROP KEY `RESERVATIONS_INX_1` ;

/* Alter table in target */
ALTER TABLE `resources` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD UNIQUE KEY `resources_name_inx`(`name`) , 
	DROP KEY `RESOURCES_NAME_INX` ;

/* Alter table in target */
ALTER TABLE `roles` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD UNIQUE KEY `roles_name_inx`(`name`) , 
	DROP KEY `ROLES_NAME_INX` ;

/* Alter table in target */
ALTER TABLE `sharedtickets` 
	ADD COLUMN `locked` varchar(20)  COLLATE utf8_general_ci NULL after `pickupid` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `shift_breaks` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD KEY `shift_breaks_breakid`(`breakid`) , 
	DROP KEY `SHIFT_BREAKS_BREAKID` , 
	ADD KEY `shift_breaks_shiftid`(`shiftid`) , 
	DROP KEY `SHIFT_BREAKS_SHIFTID` ;

/* Alter table in target */
ALTER TABLE `shifts` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE `stockcurrent` 
	DROP KEY `STOCKCURRENT_ATTSETINST` , 
	ADD KEY `stockcurrent_attsetinst`(`attributesetinstance_id`) , 
	DROP KEY `STOCKCURRENT_FK_1` , 
	ADD KEY `stockcurrent_fk_1`(`product`) , 
	ADD UNIQUE KEY `stockcurrent_inx`(`location`,`product`,`attributesetinstance_id`) , 
	DROP KEY `STOCKCURRENT_INX` ;

/* Alter table in target */
ALTER TABLE `stockdiary` 
	ADD COLUMN `supplier` varchar(255)  COLLATE utf8_general_ci NULL after `appuser` , 
	ADD COLUMN `supplierdoc` varchar(255)  COLLATE utf8_general_ci NULL after `supplier` , 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD KEY `stockdiary_attsetinst`(`attributesetinstance_id`) , 
	DROP KEY `STOCKDIARY_ATTSETINST` , 
	DROP KEY `STOCKDIARY_FK_1` , 
	ADD KEY `stockdiary_fk_1`(`product`) , 
	ADD KEY `stockdiary_fk_2`(`location`) , 
	DROP KEY `STOCKDIARY_FK_2` , 
	ADD KEY `stockdiary_inx_1`(`datenew`) , 
	DROP KEY `STOCKDIARY_INX_1` ;

/* Alter table in target */
ALTER TABLE `stocklevel` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	DROP KEY `STOCKLEVEL_LOCATION` , 
	ADD KEY `stocklevel_location`(`location`) , 
	ADD KEY `stocklevel_product`(`product`) , 
	DROP KEY `STOCKLEVEL_PRODUCT` ;

/* Create table in target */
CREATE TABLE `suppliers`(
	`id` varchar(255) COLLATE utf8_general_ci NOT NULL  , 
	`searchkey` varchar(255) COLLATE utf8_general_ci NOT NULL  , 
	`taxid` varchar(255) COLLATE utf8_general_ci NULL  , 
	`name` varchar(255) COLLATE utf8_general_ci NOT NULL  , 
	`maxdebt` double NOT NULL  DEFAULT 0 , 
	`address` varchar(255) COLLATE utf8_general_ci NULL  , 
	`address2` varchar(255) COLLATE utf8_general_ci NULL  , 
	`postal` varchar(255) COLLATE utf8_general_ci NULL  , 
	`city` varchar(255) COLLATE utf8_general_ci NULL  , 
	`region` varchar(255) COLLATE utf8_general_ci NULL  , 
	`country` varchar(255) COLLATE utf8_general_ci NULL  , 
	`firstname` varchar(255) COLLATE utf8_general_ci NULL  , 
	`lastname` varchar(255) COLLATE utf8_general_ci NULL  , 
	`email` varchar(255) COLLATE utf8_general_ci NULL  , 
	`phone` varchar(255) COLLATE utf8_general_ci NULL  , 
	`phone2` varchar(255) COLLATE utf8_general_ci NULL  , 
	`fax` varchar(255) COLLATE utf8_general_ci NULL  , 
	`notes` varchar(255) COLLATE utf8_general_ci NULL  , 
	`visible` bit(1) NOT NULL  DEFAULT b'1' , 
	`curdate` datetime NULL  , 
	`curdebt` double NULL  DEFAULT 0 , 
	`vatid` varchar(255) COLLATE utf8_general_ci NULL  , 
	PRIMARY KEY (`id`) , 
	UNIQUE KEY `suppliers_skey_inx`(`searchkey`) , 
	KEY `suppliers_name_inx`(`name`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';


/* Alter table in target */
ALTER TABLE `taxcategories` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD UNIQUE KEY `taxcat_name_inx`(`name`) , 
	DROP KEY `TAXCAT_NAME_INX` ;

/* Alter table in target */
ALTER TABLE `taxcustcategories` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD UNIQUE KEY `taxcustcat_name_inx`(`name`) , 
	DROP KEY `TAXCUSTCAT_NAME_INX` ;

/* Alter table in target */
ALTER TABLE `taxes` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD KEY `taxes_cat_fk`(`category`) , 
	DROP KEY `TAXES_CAT_FK` , 
	ADD KEY `taxes_custcat_fk`(`custcategory`) , 
	DROP KEY `TAXES_CUSTCAT_FK` , 
	ADD UNIQUE KEY `taxes_name_inx`(`name`) , 
	DROP KEY `TAXES_NAME_INX` , 
	ADD KEY `taxes_taxes_fk`(`parentid`) , 
	DROP KEY `TAXES_TAXES_FK` ;

/* Alter table in target */
ALTER TABLE `taxlines` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	DROP KEY `TAXLINES_RECEIPT` , 
	ADD KEY `taxlines_receipt`(`receipt`) , 
	ADD KEY `taxlines_tax`(`taxid`) , 
	DROP KEY `TAXLINES_TAX` ;

/* Alter table in target */
ALTER TABLE `thirdparties` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	ADD UNIQUE KEY `thirdparties_cif_inx`(`cif`) , 
	DROP KEY `THIRDPARTIES_CIF_INX` , 
	ADD UNIQUE KEY `thirdparties_name_inx`(`name`) , 
	DROP KEY `THIRDPARTIES_NAME_INX` ;

/* Alter table in target */
ALTER TABLE `ticketlines` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`ticket`,`line`) , 
	DROP KEY `TICKETLINES_ATTSETINST` , 
	ADD KEY `ticketlines_attsetinst`(`attributesetinstance_id`) , 
	ADD KEY `ticketlines_fk_2`(`product`) , 
	DROP KEY `TICKETLINES_FK_2` , 
	ADD KEY `ticketlines_fk_3`(`taxid`) , 
	DROP KEY `TICKETLINES_FK_3` ;

/* Alter table in target */
ALTER TABLE `tickets` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) , 
	DROP KEY `tickets_CUSTOMERS_FK` , 
	ADD KEY `tickets_customers_fk`(`customer`) , 
	ADD KEY `tickets_fk_2`(`person`) , 
	DROP KEY `tickets_FK_2` , 
	ADD KEY `tickets_ticketid`(`tickettype`,`ticketid`) , 
	DROP KEY `tickets_TICKETID` ;

/* Alter table in target */
ALTER TABLE `ticketsnum_payment` 
	DROP KEY `PRIMARY`, ADD PRIMARY KEY(`id`) ;

--
-- CREATE NEW TABLES
-- 

CREATE TABLE IF NOT EXISTS `uom`(
	`id` varchar(255) COLLATE utf8_general_ci NOT NULL  , 
	`name` varchar(255) COLLATE utf8_general_ci NOT NULL  , 
	PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';

CREATE TABLE IF NOT EXISTS `vouchers`(
	`id` varchar(100) COLLATE utf8_general_ci NOT NULL  , 
	`voucher_number` varchar(100) COLLATE utf8_general_ci NULL  , 
	`customer` varchar(100) COLLATE utf8_general_ci NULL  , 
	`amount` double NULL  , 
	`status` char(1) COLLATE utf8_general_ci NULL  DEFAULT 'A' , 
	PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';

CREATE TABLE IF NOT EXISTS `products_bundle` (
    `id` varchar(255) NOT NULL,
    `product` VARCHAR(255) NOT NULL,
    `product_bundle` VARCHAR(255) NOT NULL,
    `quantity` DOUBLE NOT NULL,
    PRIMARY KEY ( `id` ),
    UNIQUE INDEX `pbundle_inx_prod` ( `product` , `product_bundle` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Create table in target */
CREATE TABLE IF NOT EXISTS `taxsuppcategories`(
	`id` varchar(255) COLLATE utf8_general_ci NOT NULL  , 
	`name` varchar(255) COLLATE utf8_general_ci NOT NULL  , 
	PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8' COLLATE='utf8_general_ci';


CREATE TABLE IF NOT EXISTS `orders` (
    `id` MEDIUMINT NOT NULL AUTO_INCREMENT,
    `orderid` varchar(50) DEFAULT NULL,
    `qty` int(11) DEFAULT '1',
    `details` varchar(255) DEFAULT NULL,
    `attributes` varchar(255) DEFAULT NULL,
    `notes` varchar(255) DEFAULT NULL,
    `ticketid` varchar(50) DEFAULT NULL,
    `ordertime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `displayid` int(11) DEFAULT '1',
    `auxiliary` int(11) DEFAULT NULL,
    `completetime` timestamp,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;
 

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

-- UPDATE resources --
-- MENU
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Menu.Root.txt} WHERE `id` = '0';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/img.discount.png} WHERE `name` = 'img.discount';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/img.kit_print.png} WHERE `name` = 'img.kit_print';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/img.run_script.png} WHERE `name` = 'img.run_script';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/img.user.png} WHERE `name` = 'img.user';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Printer.CloseCash.xml} WHERE `name` = 'Printer.CloseCash';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Printer.PartialCash.xml} WHERE `name` = 'Printer.PartialCash';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml} WHERE `name` = 'Printer.Ticket';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Printer.TicketPreview.xml} WHERE `name` = 'Printer.TicketPreview';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/script.linediscount.txt} WHERE `name` = 'script.linediscount';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/script.totaldiscount.txt} WHERE `name` = 'script.totaldiscount';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Ticket.Buttons.xml} WHERE `name` = 'Ticket.Buttons';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Ticket.Close.xml} WHERE `name` = 'Ticket.Close';
UPDATE `resources` SET `content` = $FILE{/com/openbravo/pos/templates/Ticket.Discount.xml} WHERE `name` = 'Ticket.Discount';

INSERT INTO resources(id, name, restype, content) VALUES('100', 'img.discount_b', 1, $FILE{/com/openbravo/pos/templates/img.discount_b.png});
INSERT INTO resources(id, name, restype, content) VALUES('101', 'img.keyboard_32', 1, $FILE{/com/openbravo/pos/templates/img.keyboard_32.png});
INSERT INTO resources(id, name, restype, content) VALUES('102', 'Printer.CloseCash.Preview', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.Preview.xml});
INSERT INTO resources(id, name, restype, content) VALUES('103', 'Printer.PrintLastTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.PrintLastTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('104', 'Printer.ReprintTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.ReprintTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('105', 'Printer.Ticket.P1', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P1.xml});
INSERT INTO resources(id, name, restype, content) VALUES('106', 'Printer.Ticket.P2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('107', 'Printer.Ticket.P3', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P3.xml});
INSERT INTO resources(id, name, restype, content) VALUES('108', 'Printer.Ticket.P4', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P4.xml});
INSERT INTO resources(id, name, restype, content) VALUES('109', 'Printer.Ticket.P5', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P5.xml});
INSERT INTO resources(id, name, restype, content) VALUES('110', 'Printer.Ticket.P6', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P6.xml});
INSERT INTO resources(id, name, restype, content) VALUES('111', 'Printer.TicketRemote', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketRemote.xml});
INSERT INTO resources(id, name, restype, content) VALUES('112', 'script.Keyboard', 0, $FILE{/com/openbravo/pos/templates/script.Keyboard.txt});
INSERT INTO resources(id, name, restype, content) VALUES('113', 'ticket.change', 0, $FILE{/com/openbravo/pos/templates/ticket.change.txt});

-- ADD PRODUCTS
INSERT INTO products(id, reference, code, name, category, taxcat, isservice, display, printto) 
VALUES ('xxx998_998xxx_x8x8x8', 'xxx998', 'xxx998', '****', '000', '001', 1, '<html><center>****', '1');

-- ADD PRODUCTS_CAT
INSERT INTO products_cat(product) VALUES ('xxx998_998xxx_x8x8x8');

-- ADD supplier
INSERT INTO suppliers(id, searchkey, name) VALUES ('0','uniCenta', 'uniCenta');

-- ADD TAXCATEGORIES + TAXES
/* 002 added 31/01/2017 00:00:00. */
INSERT INTO taxcategories(id, name) VALUES ('002', 'Tax Other');
INSERT INTO taxes(id, name, category, custcategory, parentid, rate, ratecascade, rateorder) VALUES ('002', 'Tax Other', '002', NULL, NULL, 0, FALSE, NULL);

-- ADD UOM
INSERT INTO uom(id, name) VALUES ('0','Each');

--
-- UPDATE EXISTING data
-- products
UPDATE `products` SET `stockcost` = '0' WHERE `stockcost` IS NULL;
UPDATE `products` SET `stockvolume` = '0' WHERE `stockvolume` IS NULL;
UPDATE `products` SET `printto` = '1' WHERE `printto` IS NULL;
UPDATE `products` SET `supplier` = '0' WHERE `supplier` IS NULL;

UPDATE `roles` SET `permissions` = $FILE{/com/openbravo/pos/templates/Role.Administrator.xml} WHERE `id` = '0';
UPDATE `roles` SET `permissions` = $FILE{/com/openbravo/pos/templates/Role.Manager.xml} WHERE `id` = '1';

--
-- posApps
--
DELETE FROM resources WHERE  name = 'Printer.Ticket';
DELETE FROM resources WHERE  name = 'Ticket.Close';
INSERT INTO resources(id, name, restype, content) VALUES('44', 'Printer.Ticket', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('69', 'Ticket.Close', 0, $FILE{/com/openbravo/pos/templates/Ticket.Close.xml});

INSERT INTO resources(id, name, restype, content) VALUES('77', 'script.posapps', 0, $FILE{/com/openbravo/pos/templates/script.posapps.txt});
INSERT INTO resources(id, name, restype, content) VALUES('78', 'img.posapps', 1, $FILE{/com/openbravo/pos/templates/img.posapps.png});

-- UPDATE App' version
UPDATE applications SET NAME = $APP_NAME{}, VERSION = $APP_VERSION{} WHERE ID = $APP_ID{};