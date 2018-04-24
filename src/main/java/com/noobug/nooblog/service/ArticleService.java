package com.noobug.nooblog.service;

import com.noobug.nooblog.consts.ArticleConst;
import com.noobug.nooblog.consts.error.ArticleError;
import com.noobug.nooblog.domain.Article;
import com.noobug.nooblog.domain.ArticleLike;
import com.noobug.nooblog.domain.UserColumn;
import com.noobug.nooblog.repository.ArticleLikeRepository;
import com.noobug.nooblog.repository.ArticleRepository;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.tools.utils.ValidateUtil;
import com.noobug.nooblog.web.dto.AddNewArticleDTO;
import com.noobug.nooblog.web.dto.ArticleListDTO;
import com.noobug.nooblog.web.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 文章相关逻辑
 *
 * @author noobug.com
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleService {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleLikeRepository articleLikeRepository;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 逻辑删除指定ID的文章
     *
     * @param id 文章ID
     * @return 结果
     */
    public Mono<Result<Object>> delById(Long id) {
        return articleRepository.findById(id)
                .map(article -> {
                    if (ArticleConst.Status.DELETED != article.getStatus()) {
                        article.setStatus(ArticleConst.Status.DELETED);
                        articleRepository.save(article);
                    }

                    log.info("[删除文章] title:{}", article.getTitle());
                    return Mono.just(Result.ok());
                }).orElse(Mono.just(Result.error(ArticleError.NON_EXIST_ID)));
    }

    /**
     * 获取文章列表分页
     *
     * @param pageable 分页参数
     * @return 文章分页
     */
    @Transactional(readOnly = true)
    public Mono<Page<ArticleListDTO>> getAllByPage(Pageable pageable) {
        Page<Article> articles = articleRepository.findAllByStatusNot(ArticleConst.Status.DELETED, pageable);

        return Mono.just(articleMapper.toListDTOPage(articles));
    }

    /**
     * 封禁指定ID的文章
     *
     * @param id 文章ID
     * @return 结果
     */
    public Mono<Result<Object>> banById(Long id) {
        return articleRepository.findById(id)
                .map(article -> {
                    // 已删除文章不可封禁
                    if (ArticleConst.Status.DELETED == article.getStatus()) {
                        return Mono.just(Result.error(ArticleError.ALREADY_DELETED));
                    }

                    // 未封禁就封禁
                    if (ArticleConst.Status.BANNED != article.getStatus()) {
                        article.setStatus(ArticleConst.Status.BANNED);
                        articleRepository.save(article);
                    }

                    log.info("[封禁文章] title:{}", article.getTitle());
                    return Mono.just(Result.ok());
                }).orElse(Mono.just(Result.error(ArticleError.NON_EXIST_ID)));
    }

    /**
     * 解封指定ID的文章
     *
     * @param id 文章ID
     * @return 结果
     */
    public Mono<Result<Object>> unbanById(Long id) {
        return articleRepository.findById(id)
                .map(article -> {
                    // 已删除文章不可解封
                    if (ArticleConst.Status.DELETED == article.getStatus()) {
                        return Mono.just(Result.error(ArticleError.ALREADY_DELETED));
                    }

                    // 已封禁就解封
                    if (ArticleConst.Status.BANNED == article.getStatus()) {
                        article.setStatus(ArticleConst.Status.NORMAL);
                        articleRepository.save(article);
                    }

                    log.info("[解封文章] title:{}", article.getTitle());
                    return Mono.just(Result.ok());
                }).orElse(Mono.just(Result.error(ArticleError.NON_EXIST_ID)));
    }

    /**
     * 新增文章
     *
     * @param account    用户账号
     * @param articleDTO 文章DTO
     * @return 结果
     */
    public Mono<Result<Object>> addNew(String account, AddNewArticleDTO articleDTO) {
        String title = articleDTO.getTitle().trim();
        Optional<Long> columnId = Optional.ofNullable(articleDTO.getColumnId());
        Integer typeFlag = articleDTO.getTypeFlag();
        String reprintUrl = articleDTO.getReprintUrl();
        String translateUrl = articleDTO.getTranslateUrl();
        String label = articleDTO.getLabel();

        // 验证参数有效性
        if (!ValidateUtil.lengthBetween(title, ArticleConst.Limit.LEN_TITLE_MIN, ArticleConst.Limit.LEN_TITLE_MAX)) {
            return Mono.just(Result.error(ArticleError.Add.TITLE_LENGTH));
        } else if (!ArticleConst.Type.ALL.contains(typeFlag)) {
            return Mono.just(Result.error(ArticleError.Add.NOT_EXIST_TYPEFLAG));
        } else if (ArticleConst.Type.REPRINT == typeFlag && (reprintUrl == null || reprintUrl.isEmpty())) {
            return Mono.just(Result.error(ArticleError.Add.REPRINT_NO_URL));
        } else if (ArticleConst.Type.TRANSLATE == typeFlag && (translateUrl == null || translateUrl.isEmpty())) {
            return Mono.just(Result.error(ArticleError.Add.TRANSLATE_NO_URL));
        } else if (label != null && !ValidateUtil.lengthBetween(label, 0, ArticleConst.Limit.LEN_LABEL_MAX)) {
            return Mono.just(Result.error(ArticleError.Add.LABEL_LENGTH));
        } else if (reprintUrl != null && !ValidateUtil.lengthBetween(reprintUrl, 0, ArticleConst.Limit.LEN_REPRINT_URL_MAX)) {
            return Mono.just(Result.error(ArticleError.Add.REPRINT_URL_LENGTH));
        } else if (translateUrl != null && !ValidateUtil.lengthBetween(translateUrl, 0, ArticleConst.Limit.LEN_TRANSLATE_URL_MAX)) {
            return Mono.just(Result.error(ArticleError.Add.TRANSLATE_URL_LENGTH));
        } else {
            Article article = articleMapper.addNewArticleDTO2Article(articleDTO);

            // 未指定所属栏目则添加到默认栏目
            UserColumn column = columnId
                    .map(o -> userService.getColumnById(o).orElse(null))
                    .orElse(userService.getOrAddDefaultColumnByAccount(account));

            if (null == column) {
                return Mono.just(Result.error(ArticleError.Add.NOT_EXIST_COLUMN));
            }

            article.setUserColumn(column);

            // 填充字段
            article.setBadNumber(0);
            article.setGoodNumber(0);
            article.setStatus(ArticleConst.Status.NORMAL);
            article.setClickNumber(1L);
            article.setCommentNumber(0);
            article.setFavoriteNumber(0);
            // 默认非私密文章
            article.setIsPrivate(Optional.ofNullable(articleDTO.getIsPrivate()).orElse(Boolean.FALSE));
            // 默认非置顶
            article.setIsTop(Optional.ofNullable(articleDTO.getIsTop()).orElse(Boolean.FALSE));
            // 默认Markdown
            article.setIsMarkdown(Optional.ofNullable(articleDTO.getIsMarkdown()).orElse(Boolean.TRUE));
            article = articleRepository.save(article);

            log.info("[新增文章] account:{} - title:{}", account, title);
            return Mono.just(Result.ok());
        }


    }

    /**
     * 根据ID查询文章（不包含已被删除文章）
     *
     * @param articleId
     * @return
     */
    public Optional<Article> getById(Long articleId) {
        return articleRepository.findByIdAndStatusNot(articleId, ArticleConst.Status.DELETED);
    }

    /**
     * 找出用户对指定文章的点赞/差评记录
     *
     * @param userId    用户ID
     * @param articleId 文章ID
     * @return
     */
    public Optional<ArticleLike> getUserArticleLike(Long userId, Long articleId) {
        return articleLikeRepository.findOneByUserIdAndArticleId(userId, articleId);
    }

    /**
     * save文章实体
     *
     * @param article 文章实体
     * @return
     */
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    /**
     * save 点赞记录
     *
     * @param articleLike 点赞记录实体
     * @return
     */
    public ArticleLike saveArticleLike(ArticleLike articleLike) {
        return articleLikeRepository.save(articleLike);
    }
}
