//    KrOS POS
//    Copyright (c) 2019-2023 KriolOS
//    
//
//     
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.ticket;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.DataRead;
import com.openbravo.data.loader.IKeyed;
import com.openbravo.data.loader.ImageUtils;
import com.openbravo.data.loader.SerializerRead;
import java.awt.image.*;

/**
 *
 * @author  Adrian
 * @version 
 */
public class CategoryInfo implements IKeyed {

    private static final long serialVersionUID = 8612449444103L;
    private String m_sID;
    private String m_sName;
    private String m_sTextTip;
    private BufferedImage m_Image;
    private Boolean m_bCatShowName;

    public CategoryInfo(String id, String name, BufferedImage image, String texttip, Boolean catshowname) {
        m_sID = id;
        m_sName = name;
        m_Image = image;
        m_sTextTip = texttip;
        m_bCatShowName = catshowname;
    }

    @Override
    public Object getKey() {
        return m_sID;
    }

    public void setID(String sID) {
        m_sID = sID;
    }

    public String getID() {
        return m_sID;
    }

    public String getName() {
        return m_sName;
    }

    public void setName(String sName) {
        m_sName = sName;
    }

    public String getTextTip() {
        return m_sTextTip;
    }

    public void setTextTip(String sName) {
        m_sTextTip = sName;
    }

    public Boolean getCatShowName() {
        return m_bCatShowName;
    }

    public void setCatShowName(Boolean bcatshowname) {
        m_bCatShowName = bcatshowname;
    }

    public BufferedImage getImage() {
        return m_Image;
    }

    public void setImage(BufferedImage img) {
        m_Image = img;
    }

    @Override
    public String toString() {
        return m_sName;
    }

    public static SerializerRead<CategoryInfo> getSerializerRead() {
        return new SerializerRead<CategoryInfo>() {
            @Override
            public CategoryInfo readValues(DataRead dr) throws BasicException {
            return new CategoryInfo(dr.getString(1), 
                dr.getString(2), ImageUtils.readImage(dr.getBytes(3)),
                dr.getString(4),dr.getBoolean(5));
        }};
    }
}
