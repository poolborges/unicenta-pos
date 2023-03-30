--    KrOS POS - Open Source Point Of Sale
--    Copyright (c) 2009-2017 uniCenta
--    
--
--     .
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
--    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Script created by Jack, uniCenta 21/04/2017 08:00:00
 * Modified 21 Apr 2017 to lowercase
 * Called by Transfer for v4.2 after MySQL-create-transfer.sql
*/

set foreign_key_checks = 0;

-- drop default data
delete from people where `id` not in ('0');
delete from categories;
delete from taxcategories;
delete from taxcustcategories;
delete from taxes;
delete from locations;
delete from floors;
delete from places;
delete from shifts;
delete from breaks;
delete from shift_breaks;
delete from closedcash;
delete from csvimport;
-- delete from customers;
delete FROM draweropened;
delete FROM leaves;
delete FROM lineremoved;
delete FROM payments;
delete FROM products_cat;
delete FROM products_com;
delete FROM products;
delete FROM receipts;
delete FROM roles;
delete FROM stockcurrent;
delete FROM stockdiary;
delete FROM stocklevel;
-- delete FROM suppliers;

set foreign_key_checks = 0;