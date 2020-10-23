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
 * Script created by Jack, uniCenta 07/08/2016 08:00:00
 * Modified 10 Sept 2017
 * Called by Transfer for v4.4 after MySQL-create-transfer.sql
*/

set foreign_key_checks = 0;

alter table people drop foreign key people_fk_1;

alter table attributeinstance drop foreign key attinst_set;
alter table attributeinstance drop foreign key attinst_att;
alter table attributesetinstance drop foreign key attsetinst_set;
alter table attributeuse drop foreign key attuse_set;
alter table attributeuse drop foreign key attuse_att;
alter table attributevalue drop foreign key attval_att;

alter table categories drop foreign key categories_fk_1;

alter table customers drop foreign key customers_taxcat;

alter table leaves drop foreign key leaves_pplid;

alter table payments drop foreign key payments_fk_receipt;

alter table products drop foreign key products_attrset_fk;
alter table products drop foreign key products_taxcat_fk;
alter table products drop foreign key products_fk_1;
alter table products drop index products_inx_0; 
alter table products drop index products_inx_1; 
alter table products drop index products_name_inx; 

alter table products_bundle drop foreign key products_bundle_fk_1;
alter table products_bundle drop foreign key products_bundle_fk_2;

alter table products_cat drop foreign key products_cat_fk_1;

alter table products_com drop foreign key products_com_fk_1;
alter table products_com drop foreign key products_com_fk_2;

alter table receipts drop foreign key receipts_fk_money;

alter table reservation_customers drop foreign key res_cust_fk_1;
alter table reservation_customers drop foreign key res_cust_fk_2;

alter table shift_breaks drop foreign key shift_breaks_breakid;
alter table shift_breaks drop foreign key shift_breaks_shiftid;

alter table stockcurrent drop foreign key stockcurrent_attsetinst;
alter table stockcurrent drop foreign key stockcurrent_fk_1;
alter table stockcurrent drop foreign key stockcurrent_fk_2;

alter table stockdiary drop foreign key stockdiary_attsetinst;
alter table stockdiary drop foreign key stockdiary_fk_1;
alter table stockdiary drop foreign key stockdiary_fk_2;

alter table stocklevel drop foreign key stocklevel_product;
alter table stocklevel drop foreign key stocklevel_location;

alter table taxes drop foreign key taxes_cat_fk;
alter table taxes drop foreign key taxes_custcat_fk;
alter table taxes drop foreign key taxes_taxes_fk;

alter table taxlines drop foreign key taxlines_tax;
alter table taxlines drop foreign key taxlines_receipt;

alter table ticketlines drop foreign key ticketlines_attsetinst;
alter table ticketlines drop foreign key ticketlines_fk_ticket;
alter table ticketlines drop foreign key ticketlines_fk_2;
alter table ticketlines drop foreign key ticketlines_fk_3;

alter table tickets drop foreign key tickets_customers_fk;
alter table tickets drop foreign key tickets_fk_2;
alter table tickets drop foreign key tickets_fk_id;