package com.noobug.nooblog.consts;

public interface ArticleConst {

    /**
     * 文章状态
     */
    interface Status {
        // 正常
        int ARTICLE_STATUS_NORMAL = 1;

        // 封禁
        int ARTICLE_STATUS_BANNED = 2;

        // 删除
        int ARTICLE_STATUS_DELETED = 3;
    }

    /**
     * 文章类型
     */
    interface Type {
        // 原创
        int ARTICLE_TYPE_OWN = 1;

        // 转载
        int ARTICLE_TYPE_REPRINT = 2;

        // 翻译
        int ARTICLE_TYPE_TRANSLATE = 3;
    }

}
