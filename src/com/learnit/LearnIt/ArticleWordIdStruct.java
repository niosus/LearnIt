package com.learnit.LearnIt;

/**
 * Created with IntelliJ IDEA.
 * User: igor
 * Date: 1/10/13
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArticleWordIdStruct {

    public String article;
    public String word;
    public long id;

    ArticleWordIdStruct(String article, String word, long id)
        {
            this.article = article;
            this.word = word;
            this.id = id;
        }
}
