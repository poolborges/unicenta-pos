--    uniCenta oPOS - Touch Friendly Point Of Sale
--    Copyright (c) 2009-2017 uniCenta
--    https://unicenta.com
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

/*
 * Script created by Jack, uniCenta 28/07/2016 08:00:00
 *
 * Database upgrade script for MySQL
 * Normalise TABLENAME to tablename
 * lowercase is common to all platforms 
 * Linux default is lowercase whereas windows and Mac accepts UPPER OR lowercase 
*/

--
-- CLEAR THE DECKS
-- Need to clear any unsettled sales as properties is serialized and so may not
-- match the formats

DELETE FROM sharedtickets;

/* Alter table in target */
-- ALTER TABLE APPLICATIONS RENAME `applications` , 
--	DROP KEY PRIMARY, ADD PRIMARY KEY(`id`) ;
ALTER TABLE APPLICATIONS RENAME `applications_tmp` , 
    DROP KEY PRIMARY
ALTER TABLE `applications_tmp` RENAME `applications`, 
    ADD PRIMARY KEY(`id`);

/* Alter table in target */
ALTER TABLE ATTRIBUTE RENAME `attribute_tmp` , 
    DROP KEY `PRIMARY`;
ALTER TABLE `attribute_tmp` RENAME `attribute`, 
    ADD PRIMARY KEY (`id`);

/* Alter table in target */
ALTER TABLE ATTRIBUTEINSTANCE RENAME `attributeinstance_tmp` , 
    DROP KEY `PRIMARY`,
    DROP KEY `ATTINST_ATT` ,  
    DROP KEY `ATTINST_SET` ;
ALTER TABLE `attributeinstance_tmp` RENAME `attributeinstance`, 
    ADD PRIMARY KEY (`id`),
    ADD KEY `attinst_att`(`attribute_id`),
    ADD KEY `attinst_set`(`attributesetinstance_id`);

/* Alter table in target */
ALTER TABLE ATTRIBUTESET RENAME `attributeset_tmp` ,
    DROP KEY `PRIMARY`;
ALTER TABLE `attributeset_tmp` RENAME `attributeset` ,
    ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE ATTRIBUTESETINSTANCE RENAME `attributesetinstance_tmp` , 
    DROP KEY `PRIMARY`,
    DROP KEY `ATTSETINST_SET` ;
ALTER TABLE `attributesetinstance_tmp` RENAME `attributesetinstance` ,  
    ADD PRIMARY KEY(`id`),
    ADD KEY `attsetinst_set`(`attributeset_id`) ;

/* Alter table in target */
ALTER TABLE ATTRIBUTEUSE RENAME `attributeuse_tmp` , 
    DROP KEY `PRIMARY` ,
    DROP KEY `ATTUSE_ATT` ,
    DROP KEY `ATTUSE_LINE` ; 
ALTER TABLE `attributeuse_tmp` RENAME `attributeuse` , 
    ADD PRIMARY KEY(`id`),
    ADD KEY `attuse_att`(`attribute_id`) , 
    ADD UNIQUE KEY `attuse_line`(`attributeset_id`,`lineno`) ;

/* Alter table in target */
ALTER TABLE ATTRIBUTEVALUE RENAME `attributevalue_tmp` , 
    DROP KEY `PRIMARY` ,
    DROP KEY `ATTVAL_ATT` ;  
ALTER TABLE `attributevalue_tmp` RENAME `attributevalue` ,
    ADD PRIMARY KEY(`id`) ,
    ADD KEY `attval_att`(`attribute_id`) ;

/* Alter table in target */
ALTER TABLE BREAKS RENAME `breaks_tmp` , 
    DROP KEY `PRIMARY` ;
ALTER TABLE `breaks_tmp` RENAME `breaks` , 
    ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE CATEGORIES RENAME `categories_tmp` ,  
    DROP KEY `PRIMARY`,	
    DROP KEY `categories_fk_1` , 
    DROP KEY `CATEGORIES_NAME_INX` ;
ALTER TABLE `categories_tmp` RENAME `categories` ,
    ADD PRIMARY KEY(`id`) ,	
    ADD KEY `categories_fk_1`(`parentid`) , 
    ADD UNIQUE KEY `categories_name_inx`(`name`) ;

/* Alter table in target */
ALTER TABLE CLOSEDCASH RENAME `closedcash_tmp` , 
    DROP KEY `PRIMARY`,
    DROP KEY `CLOSEDCASH_INX_1` , 
    DROP KEY `CLOSEDCASH_INX_SEQ` ; 
ALTER TABLE `closedcash_tmp` RENAME `closedcash` ,  
    ADD PRIMARY KEY(`money`) ,
    ADD KEY `closedcash_inx_1`(`datestart`) , 
    ADD UNIQUE KEY `closedcash_inx_seq`(`host`,`hostsequence`) ;

/* Alter table in target */
ALTER TABLE CSVIMPORT RENAME `csvimport_tmp` , 
    DROP KEY `PRIMARY` ;
ALTER TABLE `csvimport_tmp` RENAME `csvimport` , 
    ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE CUSTOMERS RENAME `customers_tmp` , 
    DROP KEY `PRIMARY`,
    DROP KEY `CUSTOMERS_CARD_INX` ,
    DROP KEY `CUSTOMERS_NAME_INX` ,
    DROP KEY `CUSTOMERS_SKEY_INX` ,
    DROP KEY `CUSTOMERS_TAXCAT` ,
    DROP KEY `CUSTOMERS_TAXID_INX` ;
ALTER TABLE `customers_tmp` RENAME `customers` ,    
    ADD PRIMARY KEY(`id`) ,
    ADD KEY `customers_card_inx`(`card`) , 
    ADD KEY `customers_name_inx`(`name`) , 
    ADD UNIQUE KEY `customers_skey_inx`(`searchkey`) , 
    ADD KEY `customers_taxcat`(`taxcategory`) , 
    ADD KEY `customers_taxid_inx`(`taxid`) ;

/* Alter table in target */
ALTER TABLE DRAWEROPENED RENAME `draweropened_tmp` ;
ALTER TABLE `draweropened_tmp` RENAME `draweropened` ;

/* Alter table in target */
ALTER TABLE FLOORS RENAME `floors_tmp` , 
    DROP KEY `PRIMARY` ,
    DROP KEY `FLOORS_NAME_INX` ;
ALTER TABLE `floors_tmp` RENAME `floors` , 
    ADD PRIMARY KEY(`id`) ,
    ADD UNIQUE KEY `floors_name_inx`(`name`) ;

/* Alter table in target */
ALTER TABLE LEAVES RENAME `leaves_tmp` , 
    DROP KEY `PRIMARY` , 
    DROP KEY `lEAVES_PPLID` ;
ALTER TABLE `leaves_tmp` RENAME `leaves` ,
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `leaves_pplid`(`pplid`) ; 


/* Alter table in target */
ALTER TABLE LINEREMOVED RENAME `lineremoved_tmp` ;
ALTER TABLE `lineremoved_tmp` RENAME `lineremoved` ;

/* Alter table in target */
ALTER TABLE LOCATIONS RENAME `locations_tmp` , 
    DROP KEY `PRIMARY`,
    DROP KEY `LOCATIONS_NAME_INX` ;
ALTER TABLE `locations_tmp` RENAME `locations` , 
    ADD PRIMARY KEY(`id`) ,
    ADD UNIQUE KEY `locations_name_inx`(`name`)  ;

/* Alter table in target */
ALTER TABLE MOORERS RENAME `moorers_tmp` ;
ALTER TABLE `moorers_tmp` RENAME `moorers` ; 

/* Alter table in target */
ALTER TABLE PAYMENTS RENAME `payments_tmp` , 
    DROP KEY `PRIMARY`,
    DROP KEY `PAYMENTS_FK_RECEIPT` , 
    DROP KEY `PAYMENTS_INX_1` ; 
ALTER TABLE `payments_tmp` RENAME `payments` , 
    ADD PRIMARY KEY(`id`) ,
    ADD KEY `payments_fk_receipt`(`receipt`) , 
    ADD KEY `payments_inx_1`(`payment`) ;

/* Alter table in target */
ALTER TABLE PEOPLE RENAME `people_tmp` , 
    DROP KEY `PRIMARY` ,
    DROP KEY `PEOPLE_CARD_INX` , 
    DROP KEY `PEOPLE_FK_1` , 
    DROP KEY `PEOPLE_NAME_INX` ; 
ALTER TABLE `people_tmp` RENAME `people` ,    
    ADD PRIMARY KEY(`id`) ,
    ADD KEY `people_card_inx`(`card`) ,
    ADD KEY `people_fk_1`(`role`) , 
    ADD UNIQUE KEY `people_name_inx`(`name`) ; 

/* Alter table in target */
ALTER TABLE PLACES RENAME `places_tmp` , 
    DROP KEY `PRIMARY` , 
    DROP KEY `PLACES_NAME_INX` ;
ALTER TABLE `places_tmp` RENAME `places` ,
    ADD PRIMARY KEY(`id`) ,
    ADD UNIQUE KEY `places_name_inx`(`name`) ;


/* Alter table in target */
ALTER TABLE PRODUCTS RENAME `products_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `PRODUCTS_ATTRSET_FK` , 
    DROP KEY `PRODUCTS_FK_1` , 
    DROP KEY `PRODUCTS_INX_0` , 
    DROP KEY `PRODUCTS_INX_1` , 
    DROP KEY `PRODUCTS_NAME_INX` , 
    DROP KEY `PRODUCTS_TAXCAT_FK` ;
ALTER TABLE `products_tmp` RENAME `products` , 
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `products_attrset_fx`(`attributeset_id`) , 
    ADD KEY `products_fk_1`(`category`) , 
    ADD UNIQUE KEY `products_inx_0`(`reference`) , 
    ADD UNIQUE KEY `products_inx_1`(`code`) , 
    ADD KEY `products_name_inx`(`name`) , 
    ADD KEY `products_taxcat_fk`(`taxcat`) ;

/* Alter table in target */
ALTER TABLE PRODUCTS_CAT RENAME `products_cat_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `PRODUCTS_CAT_INX_1` ;
ALTER TABLE `products_cat_tmp` RENAME `products_cat` , 
    ADD PRIMARY KEY(`product`) , 
    ADD KEY `products_cat_inx_1`(`catorder`) ;

/* Alter table in target */
ALTER TABLE PRODUCTS_COM RENAME `products_com_tmp` ,
    DROP KEY `PRIMARY`,
    DROP KEY `PCOM_INX_PROD` ,
    DROP KEY `PRODUCTS_COM_FK_2` ;
ALTER TABLE `products_com_tmp` RENAME `products_com` , 
    ADD PRIMARY KEY(`id`) , 
    ADD UNIQUE KEY `pcom_inx_prod`(`product`,`product2`) , 
    ADD KEY `products_com_fk_2`(`product2`) ;

/* Alter table in target */
ALTER TABLE RECEIPTS RENAME `receipts_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `RECEIPTS_FK_MONEY` , 
    DROP KEY `RECEIPTS_INX_1` ;
ALTER TABLE `receipts_tmp` RENAME `receipts` ,
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `receipts_fk_money`(`money`) , 
    ADD KEY `receipts_inx_1`(`datenew`) ; 


/* Alter table in target */
ALTER TABLE RESERVATION_CUSTOMERS RENAME `reservation_customers_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `RES_CUST_FK_2` ;
ALTER TABLE `reservation_customers_tmp` RENAME `reservation_customers` , 
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `res_cust_fk_2`(`customer`) ; 


/* Alter table in target */
ALTER TABLE RESERVATIONS RENAME `reservations_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `RESERVATIONS_INX_1` ;
ALTER TABLE `reservations_tmp` RENAME `reservations` , 
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `reservations_inx_1`(`datenew`) ;

/* Alter table in target */
ALTER TABLE RESOURCES RENAME `resources_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `RESOURCES_NAME_INX` ;
ALTER TABLE `resources_tmp` RENAME `resources` ,     
    ADD PRIMARY KEY(`id`) , 
    ADD UNIQUE KEY `resources_name_inx`(`name`) ; 


/* Alter table in target */
ALTER TABLE ROLES RENAME `roles_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `ROLES_NAME_INX` ;
ALTER TABLE `roles_tmp` RENAME `roles` ,
    ADD PRIMARY KEY(`id`) , 
    ADD UNIQUE KEY `roles_name_inx`(`name`) ;

/* Alter table in target */
ALTER TABLE SHAREDTICKETS RENAME `sharedtickets_tmp` ,  
    DROP KEY `PRIMARY` ;
ALTER TABLE `sharedtickets_tmp` RENAME `sharedtickets` ,
    ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE SHIFT_BREAKS RENAME `shift_breaks_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `SHIFT_BREAKS_BREAKID` , 
    DROP KEY `SHIFT_BREAKS_SHIFTID` ;
ALTER TABLE `shift_breaks_tmp` RENAME `shift_breaks` , 
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `shift_breaks_breakid`(`breakid`) , 
    ADD KEY `shift_breaks_shiftid`(`shiftid`) ;

/* Alter table in target */
ALTER TABLE SHIFTS RENAME `shifts_tmp` , 
    DROP KEY `PRIMARY` ;
ALTER TABLE `shifts_tmp` RENAME `shifts` ,
    ADD PRIMARY KEY(`id`) ;

/* Alter table in target */
ALTER TABLE STOCKCURRENT RENAME `stockcurrent_tmp` , 
DROP KEY `STOCKCURRENT_ATTSETINST` ,
DROP KEY `STOCKCURRENT_FK_1` , 
DROP KEY `STOCKCURRENT_INX` ;
ALTER TABLE `stockcurrent_tmp` RENAME `stockcurrent` ,  
ADD KEY `stockcurrent_attsetinst`(`attributesetinstance_id`) , 
ADD KEY `stockcurrent_fk_1`(`product`) , 
ADD UNIQUE KEY `stockcurrent_inx`(`location`,`product`,`attributesetinstance_id`) ;

/* Alter table in target */
ALTER TABLE STOCKDIARY RENAME `stockdiary_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `STOCKDIARY_ATTSETINST` , 
    DROP KEY `STOCKDIARY_FK_1` , 
    DROP KEY `STOCKDIARY_FK_2` , 
    DROP KEY `STOCKDIARY_INX_1` ;
ALTER TABLE `stockdiary_tmp` RENAME `stockdiary` ,    
    ADD PRIMARY KEY(`id`) ,
    ADD KEY `stockdiary_fk_1`(`product`) , 
    ADD KEY `stockdiary_fk_2`(`location`) , 
    ADD KEY `stockdiary_attsetinst`(`attributesetinstance_id`) , 
    ADD KEY `stockdiary_inx_1`(`datenew`) ;


/* Alter table in target */
ALTER TABLE STOCKLEVEL RENAME `stocklevel_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `STOCKLEVEL_LOCATION` , 
    DROP KEY `STOCKLEVEL_PRODUCT` ;
ALTER TABLE `stocklevel_tmp` RENAME `stocklevel` , 
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `stocklevel_location`(`location`) , 
    ADD KEY `stocklevel_product`(`product`) ;

/* Alter table in target */
ALTER TABLE TAXCATEGORIES RENAME `taxcategories_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `TAXCAT_NAME_INX` ;
ALTER TABLE `taxcategories_tmp` RENAME `taxcategories` ,
    ADD PRIMARY KEY(`id`) , 
    ADD UNIQUE KEY `taxcat_name_inx`(`name`) ;

/* Alter table in target */
ALTER TABLE TAXCUSTCATEGORIES RENAME `taxcustcategories_tmp` , 
    DROP KEY `PRIMARY`,
DROP KEY `TAXCUSTCAT_NAME_INX` ; 
ALTER TABLE `taxcustcategories_tmp` RENAME `taxcustcategories` , 
    ADD PRIMARY KEY(`id`) , 
    ADD UNIQUE KEY `taxcustcat_name_inx`(`name`) ;

/* Alter table in target */
ALTER TABLE TAXES RENAME `taxes_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `TAXES_CAT_FK` , 
    DROP KEY `TAXES_CUSTCAT_FK` , 
    DROP KEY `TAXES_NAME_INX` , 
    DROP KEY `TAXES_TAXES_FK` ;
ALTER TABLE `taxes_tmp` RENAME `taxes` , 
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `taxes_cat_fk`(`category`) , 
    ADD KEY `taxes_custcat_fk`(`custcategory`) , 
    ADD UNIQUE KEY `taxes_name_inx`(`name`) , 
    ADD KEY `taxes_taxes_fk`(`parentid`) ; 


/* Alter table in target */
ALTER TABLE TAXLINES RENAME `taxlines_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `TAXLINES_RECEIPT` ,
    DROP KEY `TAXLINES_TAX` ;
ALTER TABLE `taxlines_tmp` RENAME `taxlines` ,  
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `taxlines_receipt`(`receipt`) , 
    ADD KEY `taxlines_tax`(`taxid`) ; 


/* Alter table in target */
ALTER TABLE THIRDPARTIES RENAME `thirdparties_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `THIRDPARTIES_CIF_INX` , 
    DROP KEY `THIRDPARTIES_NAME_INX` ;
ALTER TABLE `thirdparties_tmp` RENAME `thirdparties` ,    
    ADD PRIMARY KEY(`id`) , 
    ADD UNIQUE KEY `thirdparties_cif_inx`(`cif`) , 
    ADD UNIQUE KEY `thirdparties_name_inx`(`name`) ;


/* Alter table in target */
ALTER TABLE TICKETLINES RENAME `ticketlines_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `TICKETLINES_ATTSETINST` , 
    DROP KEY `TICKETLINES_FK_2` , 
    DROP KEY `TICKETLINES_FK_3` ;
ALTER TABLE `ticketlines_tmp` RENAME `ticketlines` ,
    ADD PRIMARY KEY(`ticket`,`line`) , 
    ADD KEY `ticketlines_attsetinst`(`attributesetinstance_id`) , 
    ADD KEY `ticketlines_fk_2`(`product`) , 
    ADD KEY `ticketlines_fk_3`(`taxid`) ;

/* Alter table in target */
ALTER TABLE TICKETS RENAME `tickets_tmp` , 
    DROP KEY `PRIMARY`, 
    DROP KEY `tickets_CUSTOMERS_FK` , 
    DROP KEY `tickets_FK_2` , 
    DROP KEY `tickets_TICKETID` ;
ALTER TABLE `tickets_tmp` RENAME `tickets` ,
    ADD PRIMARY KEY(`id`) , 
    ADD KEY `tickets_customers_fk`(`customer`) , 
    ADD KEY `tickets_fk_2`(`person`) , 
    ADD KEY `tickets_ticketid`(`tickettype`,`ticketid`) ; 


/* Alter table in target */
ALTER TABLE TICKETSNUM RENAME `ticketsnum_tmp` ;
ALTER TABLE `ticketsnum_tmp` RENAME `ticketsnum` ;

ALTER TABLE TICKETSNUM_PAYMENT RENAME `ticketsnum_payment_tmp` ,
    DROP KEY `PRIMARY` ;
ALTER TABLE `ticketsnum_payment_tmp` RENAME `ticketsnum_payment` ,
    ADD PRIMARY KEY(`id`) ;

ALTER TABLE TICKETSNUM_REFUND RENAME `ticketsnum_refund_tmp` ;
ALTER TABLE `ticketsnum_refund_tmp` RENAME `ticketsnum_refund` ;