package per.goweii.android.rxhttp.bean;

import per.goweii.rxhttp.base.BaseBean;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/13
 */
public class RecommendPoetryBean extends BaseBean {
    /**
     * title : 饯郑安阳入蜀
     * content : 彭山折坂外，井络少城隈。|地是三巴俗，人非百里材。|畏途君怅望，岐路我裴徊。|心赏风烟隔，容华岁月催。|遥遥分凤野，去去转龙媒。|遗锦非前邑，鸣琴即旧台。|剑门千仞起，石路五丁开。|海客乘槎渡，仙童驭竹回。|魂将离鹤远，思逐断猿哀。|唯有双凫舄，飞去复飞来。
     * authors : 骆宾王
     */

    private String title;
    private String content;
    private String authors;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }
}
