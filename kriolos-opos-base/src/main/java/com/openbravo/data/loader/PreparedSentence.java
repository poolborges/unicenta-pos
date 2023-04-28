//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
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
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.data.loader;

/**
 *
 * @author adrianromero
 * @param <W>
 * @param <T>
 */
public class PreparedSentence<W, T> extends PreparedSentenceExec<W, T> {

    /*
    public PreparedSentence(Session s, ISQLBuilderStatic sentence, SerializerWrite<W> serwrite, SerializerRead<T> serread) {
        super(s, sentence, serwrite, serread);
    }

    public PreparedSentence(Session s, ISQLBuilderStatic sentence) {
        this(s, sentence, null, null);
    }

    public PreparedSentence(Session s, ISQLBuilderStatic sentence, SerializerWrite<W> serwrite) {
        this(s, sentence, serwrite, null);
    }
 

    public PreparedSentence(Session s, String sentence, SerializerWrite<W> serwrite, SerializerRead<T> serread) {
        this(s, new NormalBuilder(sentence), serwrite, serread);
    }

    public PreparedSentence(Session s, String sentence, SerializerWrite<W> serwrite) {
        this(s, new NormalBuilder(sentence), serwrite, null);
    }

    public PreparedSentence(Session s, String sentence) {
        this(s, new NormalBuilder(sentence), null, null);
    }
   */
    /* */
    public PreparedSentence(Session session, String sqlSentence, Datas[] param, int[] index) {
        super( session,  sqlSentence, param, index);
    }

    public PreparedSentence(Session s, String sentence, SerializerWrite<W> serwrite) {
        super(s, sentence, serwrite);
    }

    public PreparedSentence(Session s, String sentence, SerializerWrite<W> serwrite, SerializerRead<T> serread) {
        super(s, sentence, serwrite, serread);
    }

}
