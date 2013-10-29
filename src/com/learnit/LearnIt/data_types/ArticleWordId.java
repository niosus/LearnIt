/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt.data_types;


public class ArticleWordId {
    public String article;
    public String prefix;
    public String word;
    public String translation;
    public int id;

    public ArticleWordId(String article, String prefix, String word, String translation, int id) {
        this.article = article;
        this.prefix = prefix;
        this.word = word;
        this.translation = translation;
        this.id = id;
    }
}
