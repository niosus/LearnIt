/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License. To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */

/*
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 */


package com.learnit.LearnIt;


public class ArticleWordIdStruct {

    public String article;
    public String prefix;
    public String word;
    public long id;

    ArticleWordIdStruct(String article, String prefix, String word, long id)
        {
            this.article = article;
            this.prefix = prefix;
            this.word = word;
            this.id = id;
        }
}
