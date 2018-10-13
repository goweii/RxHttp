package per.goweii.android.rxhttp.bean;

import per.goweii.rxhttp.base.BaseBean;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/13
 */
public class SinglePoetryBean extends BaseBean {
    /**
     * author : 苏辙
     * origin : 水调歌头·徐州中秋
     * category : 古诗文-节日-中秋节
     * content : 离别一何久，七度过中秋。
     */

    private String author;
    private String origin;
    private String category;
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
