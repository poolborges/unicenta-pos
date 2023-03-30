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

/*
 * Script created by Jack, uniCenta 23/07/2016 08:00:00
 *
 * Create tables IF NOT EXISTS tablename.
*/

-- DROP DATABASE IF EXISTS `unitest`;
-- CREATE DATABASE IF NOT EXISTS `unitest`;
-- USE `unitest`;                                        

/* Header line. Object: applications. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `applications` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`version` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

-- INSERT INTO applications(id, name, version) VALUES("unicentaopos", "KrOS POS", "4.2Beta");

/* Header line. Object: attribute. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `attribute` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributeinstance. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `attributeinstance` (
	`id` varchar(255) NOT NULL,
	`attributesetinstance_id` varchar(255) NOT NULL,
	`attribute_id` varchar(255) NOT NULL,
	`value` varchar(255) default NULL,
	KEY `attinst_att` ( `attribute_id` ),
	KEY `attinst_set` ( `attributesetinstance_id` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributeset. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `attributeset` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributesetinstance. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `attributesetinstance` (
	`id` varchar(255) NOT NULL,
	`attributeset_id` varchar(255) NOT NULL,
	`description` varchar(255) default NULL,
	KEY `attsetinst_set` ( `attributeset_id` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributeuse. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `attributeuse` (
	`id` varchar(255) NOT NULL,
	`attributeset_id` varchar(255) NOT NULL,
	`attribute_id` varchar(255) NOT NULL,
	`lineno` int(11) default NULL,
	KEY `attuse_att` ( `attribute_id` ),
	UNIQUE INDEX `attuse_line` ( `attributeset_id`, `lineno` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: attributevalue. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `attributevalue` (
	`id` varchar(255) NOT NULL,
	`attribute_id` varchar(255) NOT NULL,
	`value` varchar(255) default NULL,
	KEY `attval_att` ( `attribute_id` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: breaks. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `breaks` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`visible` tinyint(1) NOT NULL default '1',
	`notes` varchar(255) default NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: categories. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `categories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`parentid` varchar(255) default NULL,
	`image` mediumblob default NULL,
	`texttip` varchar(255) default NULL,
	`catshowname` smallint(6) NOT NULL default '1',
	`catorder` varchar(255) default NULL,
	KEY `categories_fk_1` ( `parentid` ),
	UNIQUE INDEX `categories_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: closedcash. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `closedcash` (
	`money` varchar(255) NOT NULL,
	`host` varchar(255) NOT NULL,
	`hostsequence` int(11) NOT NULL,
	`datestart` datetime NOT NULL,
	`dateend` datetime default NULL,
	`nosales` int(11) NOT NULL default '0',
	KEY `closedcash_inx_1` ( `datestart` ),
	UNIQUE INDEX `closedcash_inx_seq` ( `host`, `hostsequence` ),
	PRIMARY KEY  ( `money` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: csvimport. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `csvimport` (
	`id` varchar(255) NOT NULL,
	`rownumber` varchar(255) default NULL,
	`csverror` varchar(255) default NULL,
	`reference` varchar(255) default NULL,
	`code` varchar(255) default NULL,
	`name` varchar(255) default NULL,
	`pricebuy` double default NULL,
	`pricesell` double default NULL,
	`previousbuy` double default NULL,
	`previoussell` double default NULL,
	`category` varchar(255) default NULL,
	`tax` varchar(255) default NULL,
	`searchkey` varchar(255) default NULL
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: customers. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `customers` (
	`id` varchar(255) NOT NULL,
	`searchkey` varchar(255) NOT NULL,
	`taxid` varchar(255) default NULL,
	`name` varchar(255) NOT NULL,
	`taxcategory` varchar(255) default NULL,
	`card` varchar(255) default NULL,
	`maxdebt` double NOT NULL default '0',
	`address` varchar(255) default NULL,
	`address2` varchar(255) default NULL,
	`postal` varchar(255) default NULL,
	`city` varchar(255) default NULL,
	`region` varchar(255) default NULL,
	`country` varchar(255) default NULL,
	`firstname` varchar(255) default NULL,
	`lastname` varchar(255) default NULL,
	`email` varchar(255) default NULL,
	`phone` varchar(255) default NULL,
	`phone2` varchar(255) default NULL,
	`fax` varchar(255) default NULL,
	`notes` varchar(255) default NULL,
	`visible` bit(1) NOT NULL default b'1',
	`curdate` datetime default NULL,
	`curdebt` double default '0',
	`image` mediumblob default NULL,
	`isvip` bit(1) NOT NULL default b'0',
	`discount` double default '0',
        `memodate` datetime default NULL,
	KEY `customers_card_inx` ( `card` ),
	KEY `customers_name_inx` ( `name` ),
	UNIQUE INDEX `customers_skey_inx` ( `searchkey` ),
	KEY `customers_taxcat` ( `taxcategory` ),
	KEY `customers_taxid_inx` ( `taxid` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: draweropened. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `draweropened` (
	`opendate` timestamp NOT NULL,
	`name` varchar(255) default NULL,
	`ticketid` varchar(255) default NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: floors. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `floors` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`image` mediumblob default NULL,
	UNIQUE INDEX `floors_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: leaves. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `leaves` (
	`id` varchar(255) NOT NULL,
	`pplid` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`startdate` datetime NOT NULL,
	`enddate` datetime NOT NULL,
	`notes` varchar(255) default NULL,
	KEY `leaves_pplid` ( `pplid` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: lineremoved. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `lineremoved` (
	`removeddate` timestamp NOT NULL,
	`name` varchar(255) default NULL,
	`ticketid` varchar(255) default NULL,
	`productid` varchar(255) default NULL,
	`productname` varchar(255) default NULL,
	`units` double NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: locations. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `locations` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`address` varchar(255) default NULL,
	UNIQUE INDEX `locations_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: moorers. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `moorers` (
	`vesselname` varchar(255) default NULL,
	`size` int(11) default NULL,
	`days` int(11) default NULL,
	`power` bit(1) NOT NULL default b'0'
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: orders. Script date: 01/01/2017 00:00:01. */
CREATE TABLE `orders` (
    `id` int(11) NOT NULL,
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

/* Header line. Object: payments. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `payments` (
	`id` varchar(255) NOT NULL,
	`receipt` varchar(255) NOT NULL,
	`payment` varchar(255) NOT NULL,
	`total` double NOT NULL default '0',
	`tip` double default '0',
	`transid` varchar(255) default NULL,
	`isprocessed` bit(1) default b'0',
	`returnmsg` mediumblob default NULL,
	`notes` varchar(255) default NULL,
	`tendered` double default NULL,
	`cardname` varchar(255) default NULL,
        `voucher` varchar(255) default NULL,
	KEY `payments_fk_receipt` ( `receipt` ),
	KEY `payments_inx_1` ( `payment` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: people. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `people` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`apppassword` varchar(255) default NULL,
	`card` varchar(255) default NULL,
	`role` varchar(255) NOT NULL,
	`visible` bit(1) NOT NULL,
	`image` mediumblob default NULL,
	KEY `people_card_inx` ( `card` ),
	KEY `people_fk_1` ( `role` ),
	UNIQUE INDEX `people_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: pickup_number. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `pickup_number` (
	`id` int(11) NOT NULL default '0'
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: places. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `places` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`x` int(11) NOT NULL,
	`y` int(11) NOT NULL,
	`floor` varchar(255) NOT NULL,
	`customer` varchar(255) default NULL,
	`waiter` varchar(255) default NULL,
	`ticketid` varchar(255) default NULL,
	`tablemoved` smallint(6) NOT NULL default '0',
	UNIQUE INDEX `places_name_inx` ( `name` ),
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products. Script date: 02/04/2016 10:53:00. */
CREATE TABLE IF NOT EXISTS `products` (
	`id` varchar(255) NOT NULL,
	`reference` varchar(255) NOT NULL,
	`code` varchar(255) NOT NULL,
	`codetype` varchar(255) default NULL,
	`name` varchar(255) NOT NULL,
	`pricebuy` double NOT NULL default '0',
	`pricesell` double NOT NULL default '0',
	`category` varchar(255) NOT NULL,
	`taxcat` varchar(255) NOT NULL,
	`attributeset_id` varchar(255) default NULL,
	`stockcost` double NOT NULL default '0',
	`stockvolume` double NOT NULL default '0',
	`image` mediumblob default NULL,
	`iscom` bit(1) NOT NULL default b'0',
	`isscale` bit(1) NOT NULL default b'0',
	`isconstant` bit(1) NOT NULL default b'0',
	`printkb` bit(1) NOT NULL default b'0',
	`sendstatus` bit(1) NOT NULL default b'0',
	`isservice` bit(1) NOT NULL default b'0',
	`attributes` mediumblob default NULL,
	`display` varchar(255) default NULL,
	`isvprice` smallint(6) NOT NULL default '0',
	`isverpatrib` smallint(6) NOT NULL default '0',
	`texttip` varchar(255) default NULL,
	`warranty` smallint(6) NOT NULL default '0',
	`stockunits` double NOT NULL default '0',
	`printto` varchar(255) default '1',
	`supplier` varchar(255) default NULL,
        `uom` varchar(255) default '0',
        `memodate` datetime default NULL,

	PRIMARY KEY  ( `id` ),
	KEY `products_attrset_fx` ( `attributeset_id` ),
	KEY `products_fk_1` ( `category` ),
	UNIQUE INDEX `products_inx_0` ( `reference` ),
	UNIQUE INDEX `products_inx_1` ( `code` ),
	INDEX `products_name_inx` ( `name` ),
	KEY `products_taxcat_fk` ( `taxcat` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products_cat. Script date: 11/05/2016 05:25:00. */
CREATE TABLE IF NOT EXISTS `products_bundle` (
    `id` varchar(255) NOT NULL,
    `product` VARCHAR(255) NOT NULL,
    `product_bundle` VARCHAR(255) NOT NULL,
    `quantity` DOUBLE NOT NULL,
    PRIMARY KEY ( `id` ),
    UNIQUE INDEX `pbundle_inx_prod` ( `product` , `product_bundle` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products_cat. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `products_cat` (
	`product` varchar(255) NOT NULL,
	`catorder` int(11) default NULL,
	PRIMARY KEY  ( `product` ),
	KEY `products_cat_inx_1` ( `catorder` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: products_com. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `products_com` (
	`id` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`product2` varchar(255) NOT NULL,
	UNIQUE INDEX `pcom_inx_prod` ( `product`, `product2` ),
	PRIMARY KEY  ( `id` ),
	KEY `products_com_fk_2` ( `product2` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: receipts. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `receipts` (
	`id` varchar(255) NOT NULL,
	`money` varchar(255) NOT NULL,
	`datenew` datetime NOT NULL,
	`attributes` mediumblob default NULL,
	`person` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `receipts_fk_money` ( `money` ),
	KEY `receipts_inx_1` ( `datenew` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: reservation_customers. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `reservation_customers` (
	`id` varchar(255) NOT NULL,
	`customer` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` ),
	KEY `res_cust_fk_2` ( `customer` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: reservations. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `reservations` (
	`id` varchar(255) NOT NULL,
	`created` datetime NOT NULL,
	`datenew` datetime NOT NULL default '2015-01-01 00:00:00',
	`title` varchar(255) NOT NULL,
	`chairs` int(11) NOT NULL,
	`isdone` bit(1) NOT NULL,
	`description` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `reservations_inx_1` ( `datenew` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: resources. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `resources` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`restype` int(11) NOT NULL,
	`content` mediumblob default NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `resources_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: roles. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `roles` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`permissions` mediumblob default NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `roles_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: sharedtickets. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `sharedtickets` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`content` mediumblob default NULL,
	`appuser` varchar(255) default NULL,
	`pickupid` smallint(6) NOT NULL default '0',
	`locked` varchar(20) default NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: shift_breaks. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `shift_breaks` (
	`id` varchar(255) NOT NULL,
	`shiftid` varchar(255) NOT NULL,
	`breakid` varchar(255) NOT NULL,
	`starttime` timestamp NOT NULL,
	`endtime` timestamp NOT NULL,
	PRIMARY KEY  ( `id` ),
	KEY `shift_breaks_breakid` ( `breakid` ),
	KEY `shift_breaks_shiftid` ( `shiftid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: shifts. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `shifts` (
	`id` varchar(255) NOT NULL,
	`startshift` datetime NOT NULL,
	`endshift` datetime default NULL,
	`pplid` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: stockcurrent. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `stockcurrent` (
	`location` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`attributesetinstance_id` varchar(255) default NULL,
	`units` double NOT NULL,
	KEY `stockcurrent_attsetinst` ( `attributesetinstance_id` ),
	KEY `stockcurrent_fk_1` ( `product` ),
	UNIQUE INDEX `stockcurrent_inx` ( `location`, `product`, `attributesetinstance_id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: stockdiary. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `stockdiary` (
	`id` varchar(255) NOT NULL,
	`datenew` datetime NOT NULL,
	`reason` int(11) NOT NULL,
	`location` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`attributesetinstance_id` varchar(255) default NULL,
	`units` double NOT NULL,
	`price` double NOT NULL,
	`appuser` varchar(255) default NULL,
	`supplier` varchar(255) default NULL,
	`supplierdoc` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `stockdiary_attsetinst` ( `attributesetinstance_id` ),
	KEY `stockdiary_fk_1` ( `product` ),
	KEY `stockdiary_fk_2` ( `location` ),
	KEY `stockdiary_inx_1` ( `datenew` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: stocklevel. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `stocklevel` (
	`id` varchar(255) NOT NULL,
	`location` varchar(255) NOT NULL,
	`product` varchar(255) NOT NULL,
	`stocksecurity` double default NULL,
	`stockmaximum` double default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `stocklevel_location` ( `location` ),
	KEY `stocklevel_product` ( `product` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: suppliers. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `suppliers` (
	`id` varchar(255) NOT NULL,
	`searchkey` varchar(255) NOT NULL,
	`taxid` varchar(255) default NULL,
	`name` varchar(255) NOT NULL,
	`maxdebt` double NOT NULL default '0',
	`address` varchar(255) default NULL,
	`address2` varchar(255) default NULL,
	`postal` varchar(255) default NULL,
	`city` varchar(255) default NULL,
	`region` varchar(255) default NULL,
	`country` varchar(255) default NULL,
	`firstname` varchar(255) default NULL,
	`lastname` varchar(255) default NULL,
	`email` varchar(255) default NULL,
	`phone` varchar(255) default NULL,
	`phone2` varchar(255) default NULL,
	`fax` varchar(255) default NULL,
	`notes` varchar(255) default NULL,
	`visible` bit(1) NOT NULL default b'1',
	`curdate` datetime default NULL,
	`curdebt` double default '0',
	`vatid` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `suppliers_name_inx` ( `name` ),
	UNIQUE INDEX `suppliers_skey_inx` ( `searchkey` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxcategories. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `taxcategories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `taxcat_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxcustcategories. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `taxcustcategories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `taxcustcat_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxes. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `taxes` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`category` varchar(255) NOT NULL,
	`custcategory` varchar(255) default NULL,
	`parentid` varchar(255) default NULL,
	`rate` double NOT NULL default '0',
	`ratecascade` bit(1) NOT NULL default b'0',
	`rateorder` int(11) default NULL,
	PRIMARY KEY  ( `id` ),
	KEY `taxes_cat_fk` ( `category` ),
	KEY `taxes_custcat_fk` ( `custcategory` ),
	UNIQUE INDEX `taxes_name_inx` ( `name` ),
	KEY `taxes_taxes_fk` ( `parentid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxlines. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `taxlines` (
	`id` varchar(255) NOT NULL,
	`receipt` varchar(255) NOT NULL,
	`taxid` varchar(255) NOT NULL,
	`base` double NOT NULL default '0',
	`amount` double NOT NULL default '0',
	PRIMARY KEY  ( `id` ),
	KEY `taxlines_receipt` ( `receipt` ),
	KEY `taxlines_tax` ( `taxid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: taxsuppcategories. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `taxsuppcategories` (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: thirdparties. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `thirdparties` (
	`id` varchar(255) NOT NULL,
	`cif` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`address` varchar(255) default NULL,
	`contactcomm` varchar(255) default NULL,
	`contactfact` varchar(255) default NULL,
	`payrule` varchar(255) default NULL,
	`faxnumber` varchar(255) default NULL,
	`phonenumber` varchar(255) default NULL,
	`mobilenumber` varchar(255) default NULL,
	`email` varchar(255) default NULL,
	`webpage` varchar(255) default NULL,
	`notes` varchar(255) default NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `thirdparties_cif_inx` ( `cif` ),
	UNIQUE INDEX `thirdparties_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketlines. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `ticketlines` (
	`ticket` varchar(255) NOT NULL,
	`line` int(11) NOT NULL,
	`product` varchar(255) default NULL,
	`attributesetinstance_id` varchar(255) default NULL,
	`units` double NOT NULL,
	`price` double NOT NULL,
	`taxid` varchar(255) NOT NULL,
	`attributes` mediumblob default NULL,
	PRIMARY KEY  ( `ticket`, `line` ),
	KEY `ticketlines_attsetinst` ( `attributesetinstance_id` ),
	KEY `ticketlines_fk_2` ( `product` ),
	KEY `ticketlines_fk_3` ( `taxid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: tickets. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `tickets` (
	`id` varchar(255) NOT NULL,
	`tickettype` int(11) NOT NULL default '0',
	`ticketid` int(11) NOT NULL,
	`person` varchar(255) NOT NULL,
	`customer` varchar(255) default NULL,
	`status` int(11) NOT NULL default '0',
	PRIMARY KEY  ( `id` ),
	KEY `tickets_customers_fk` ( `customer` ),
	KEY `tickets_fk_2` ( `person` ),
	KEY `tickets_ticketid` ( `tickettype`, `ticketid` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketsnum. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `ticketsnum` (
	`id` int(11) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketsnum_payment. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `ticketsnum_payment` (
--	`id` int(11) NOT NULL auto_increment,
	`id` int(11) NOT NULL,
	PRIMARY KEY  ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: ticketsnum_refund. Script date: 23/07/2016 08:00:00 */
CREATE TABLE IF NOT EXISTS `ticketsnum_refund` (
	`id` int(11) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: uom. Script date: 30/09/2015 13:07:00. */
CREATE TABLE IF NOT EXISTS `uom` (
    `id` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    PRIMARY KEY ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

/* Header line. Object: vouchers. Script date: 30/09/2015 09:33:33. */
CREATE TABLE IF NOT EXISTS `vouchers` (
   `id` VARCHAR(100) NOT NULL,
   `voucher_number` VARCHAR(100) DEFAULT NULL,
   `customer` VARCHAR(100) DEFAULT NULL,
   `amount` DOUBLE DEFAULT NULL,
   `status` CHAR(1) DEFAULT 'A',
  PRIMARY KEY ( `id` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;