package com.noobug.nooblog.service;

import com.noobug.nooblog.consts.ArticleConst;
import com.noobug.nooblog.consts.UserConst;
import com.noobug.nooblog.consts.error.ArticleError;
import com.noobug.nooblog.consts.error.UserError;
import com.noobug.nooblog.domain.*;
import com.noobug.nooblog.repository.UserColumnRepository;
import com.noobug.nooblog.repository.UserLogRepository;
import com.noobug.nooblog.repository.UserRepository;
import com.noobug.nooblog.repository.UserRoleRepository;
import com.noobug.nooblog.security.TokenProvider;
import com.noobug.nooblog.tools.entity.Result;
import com.noobug.nooblog.tools.utils.CommonUtil;
import com.noobug.nooblog.tools.utils.SecurityUtil;
import com.noobug.nooblog.tools.utils.ValidateUtil;
import com.noobug.nooblog.web.dto.UserFixInfoDTO;
import com.noobug.nooblog.web.dto.UserInfoDTO;
import com.noobug.nooblog.web.dto.UserLoginDTO;
import com.noobug.nooblog.web.dto.UserRegDTO;
import com.noobug.nooblog.web.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLogRepository userLogRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserColumnRepository userColumnRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private SecurityUtil securityUtil;

    /**
     * 用户注册
     *
     * @param regDTO 注册DTO
     * @return 结果
     */
    public Mono<Result> reg(UserRegDTO regDTO) {

        // 注册参数合法性判断
        if (!ValidateUtil.lengthBetween(regDTO.getAccount(), UserConst.Limit.LEN_ACCOUNT_MIN, UserConst.Limit.LEN_ACCOUNT_MAX)) {
            return Mono.just(Result.error(UserError.Reg.ACCOUNT_LENGTH));
        } else if (!ValidateUtil.lengthBetween(regDTO.getPassword(), UserConst.Limit.LEN_PASSWORD_MIN, UserConst.Limit.LEN_PASSWORD_MAX)) {
            return Mono.just(Result.error(UserError.Reg.PASSWORD_LENGTH));
        } else if (!ValidateUtil.lengthBetween(regDTO.getNickName(), UserConst.Limit.LEN_NICKNAME_MIN, UserConst.Limit.LEN_NICKNAME_MAX)) {
            return Mono.just(Result.error(UserError.Reg.NICKNAME_LENGTH));
        } else if (ValidateUtil.existChinese(regDTO.getAccount())) {
            return Mono.just(Result.error(UserError.Reg.ACCOUNT_EXIST_CHINESE));
        } else if (ValidateUtil.existSpace(regDTO.getAccount())) {
            return Mono.just(Result.error(UserError.Reg.ACCOUNT_EXIST_SPACE));
        } else if (ValidateUtil.allNumber(regDTO.getAccount())) {
            return Mono.just(Result.error(UserError.Reg.ACCOUNT_ALL_NUMBER));
        } else if (userRepository.findByAccountAndDeleted(regDTO.getAccount(), Boolean.FALSE).isPresent()) {
            return Mono.just(Result.error(UserError.Reg.EXISTED_ACCOUNT));
        } else if (ValidateUtil.existSpace(regDTO.getPassword())) {
            return Mono.just(Result.error(UserError.Reg.PASSWORD_EXIST_SPACE));
        } else if (ValidateUtil.allSpace(regDTO.getNickName())) {
            return Mono.just(Result.error(UserError.Reg.NICKNAME_ALL_SPACE));
        } else if (!ValidateUtil.isEmail(regDTO.getEmail())) {
            return Mono.just(Result.error(UserError.Reg.EMAIL_INVALID));
        }

        // 加密密码
        String md5 = securityUtil.md5(regDTO.getPassword());
        regDTO.setPassword(md5);

        User user = userMapper.regDTO2User(regDTO);
        user.setAuthenticated(Boolean.FALSE);
        user.setBanned(Boolean.FALSE);
        user.setDeleted(Boolean.FALSE);
        user.setIsPublic(Boolean.TRUE);
        user.setScore(0);
        user.setSex(UserConst.Sex.UNKNOWN);
        user = userRepository.save(user);

        // 设置为用户角色
        try {
            setDefaultRole(user);

        } catch (Exception e) {
            log.error("[新用户注册] 设置默认角色失败", e);
        }

        log.info("[新用户注册] {}", regDTO);
        return Mono.just(Result.ok());
    }

    /**
     * 设置默认角色
     *
     * @param user 用户实体
     */
    private void setDefaultRole(User user) throws Exception {
        Assert.notNull(user, "[设置默认用户角色] NPE");
        Assert.notNull(user.getId(), "[设置默认用户角色] ID为空");
        roleService.findOneByCode(UserConst.DEFAULT_ROLE)
                .map(role -> {
                    UserRole userRole = new UserRole(null, user.getId(), role.getId());
                    return userRoleRepository.save(userRole);
                })
                .orElseThrow(() -> new Exception("[设置默认用户角色] 数据库无该角色code"));
    }

    /**
     * 用户登录日志
     *
     * @return
     */
    private Mono<Void> addUserLog(User user, String ip) {
        userLogRepository.save(new UserLog(null, user, ip));
        return Mono.empty();
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录DTO
     * @return 结果
     */
    public Mono<Result<Object>> login(UserLoginDTO loginDTO, String ip) {
        // 加密密码
        String md5 = securityUtil.md5(loginDTO.getPassword());
        String account = loginDTO.getAccount();

        return userRepository.findByAccountAndDeleted(account, Boolean.FALSE)
                .map(user -> {
                    if (user.getPassword().equals(md5)) {
                        List<Role> roles = roleService.findAllByUserId(user.getId());

                        String token = tokenProvider.generateToken(account, md5, roles);

                        return addUserLog(user, ip)
                                .thenReturn(Result.ok((Object) token));
                    } else {
                        return Mono.just(Result.error(UserError.Login.INCORRECT_PASSWORD));
                    }
                })
                .orElse(Mono.just(Result.error(UserError.Login.NOT_EXIST_ACCOUNT)));
    }

    /**
     * 上传头像
     *
     * @param multiValueMap 表单信息
     * @return 上传文件url
     */
    public Mono<Result<String>> uploadIcon(MultiValueMap<String, Part> multiValueMap) {
        Map<String, Part> parts = multiValueMap.toSingleValueMap();
        if (parts.containsKey("file")) {
            FilePart part = (FilePart) parts.get("file");
            String ext = StringUtils.getFilenameExtension(part.filename());

            if (ext != null) {
                ext = ext.toLowerCase();
            }

            if (!"jpg".equals(ext) && !"gif".equals(ext) && !"png".equals(ext) && !"bmp".equals(ext)) {
                return Mono.just(Result.error(3, "不允许上传该格式的文件"));
            }

            String fileName = ZonedDateTime.now().toEpochSecond()
                    + "_" + CommonUtil.randomString(6) + "." + ext;

            String filePath = "upload" + File.separator + fileName;

            // 目录
            File dir = new File("upload");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return part.transferTo(file).thenReturn(Result.ok(filePath));
        }

        return Mono.just(Result.error(1, "上传文件异常"));
    }

    /**
     * 修改资料
     *
     * @param account        用户账号
     * @param userFixInfoDTO 修改的字段DTO
     * @return 处理结果
     */
    public Mono<Result<Object>> fixUserInfo(String account, UserFixInfoDTO userFixInfoDTO) {
        Integer sex = userFixInfoDTO.getSex();
        String nickName = userFixInfoDTO.getNickName();
        String signature = userFixInfoDTO.getSignature();
        String iconPath = userFixInfoDTO.getIconPath();

        Optional<User> user = userRepository.findByAccountAndDeleted(account, Boolean.FALSE);

        // 判断传入参数的有效性
        if (!user.isPresent()) {
            return Mono.just(Result.error(UserError.NON_EXIST_ID));
        } else if (!ValidateUtil.lengthBetween(nickName, UserConst.Limit.LEN_NICKNAME_MIN, UserConst.Limit.LEN_NICKNAME_MAX)) {
            return Mono.just(Result.error(UserError.Reg.NICKNAME_LENGTH));
        } else if (signature != null && signature.length() > UserConst.Limit.LEN_SIGNATURE_MAX) {
            return Mono.just(Result.error(UserError.Info.SIGNATURE_TOO_LONG));
        } else if (!UserConst.Sex.ALL.contains(sex)) {
            return Mono.just(Result.error(UserError.Info.UNKNOWN_SEX_TYPE));
        } else {
            File file = new File(iconPath);
            if (!file.exists()) {
                return Mono.just(Result.error(UserError.Info.UNKNOWN_ICON_PATH));
            } else {
                User dbUser = user.get();
                dbUser.setIsPublic(userFixInfoDTO.getIsPublic());
                dbUser.setSex(sex);
                dbUser.setSignature(signature);
                dbUser.setNickName(nickName);
                dbUser.setIconPath(iconPath);
                userRepository.save(dbUser);
            }
        }

        return Mono.just(Result.ok());
    }

    /**
     * 获取用户资料信息
     *
     * @param id 用户ID
     * @return 用户资料DTO
     */
    @Transactional(readOnly = true)
    public Mono<Result<UserInfoDTO>> getUserInfoById(Long id) {
        return userRepository.findByIdAndDeleted(id, Boolean.FALSE)
                .map(user -> {
                    UserInfoDTO dto = userMapper.user2InfoDTO(user);
                    return Mono.just(Result.ok(dto));
                })
                .orElse(Mono.just(Result.error(UserError.NON_EXIST_ID)));
    }

    /**
     * 根据ID获取用户栏目
     *
     * @param columnId 栏目ID
     * @return 用户栏目
     */
    @Transactional(readOnly = true)
    public Optional<UserColumn> getColumnById(Long columnId) {
        return userColumnRepository.findById(columnId);
    }

    /**
     * 获取默认用户栏目，没有则新增
     *
     * @param account 用户账号
     * @return 用户栏目
     */
    public UserColumn getOrAddDefaultColumnByAccount(String account) {

        return userRepository.findByAccountAndDeleted(account, Boolean.FALSE)
                .map(user -> {
                    UserColumn column = userColumnRepository.findOneByUserAccountAndIsDefault(account, Boolean.TRUE);

                    if (column == null) {
                        column = new UserColumn(null, user, null, "默认栏目", 0, Boolean.TRUE);
                        column = userColumnRepository.save(column);
                    }

                    return column;
                }).orElse(null);
    }

    public Mono<List<UserLog>> getLogPage(Pageable pageable) {

        return Mono.empty();
    }

    /**
     * 用户给文章差评
     *
     * @param account   用户账号
     * @param articleId 文章ID
     * @return 结果
     */
    public Mono<Result<Object>> unlikeArticle(String account, Long articleId, String ip) {

        // 查用户，无则返回error
        return userRepository.findByAccountAndDeleted(account, Boolean.FALSE)
                // 查文章 无则error
                .map(u -> articleService.getById(articleId)
                        .map(article -> {
                            // 找用户点赞该文章的记录
                            Optional<ArticleLike> articleLike = articleService.getUserArticleLike(u.getId(), articleId);

                            // 有记录则更新成差评  否则新增记录
                            if(articleLike.isPresent()){
                                ArticleLike dbLike = articleLike.get();
                                if(ArticleConst.Like.GOOD == dbLike.getStatus()){
                                    dbLike.setStatus(ArticleConst.Like.BAD);
                                    articleService.saveArticleLike(dbLike);
                                    // 文章点赞数-1 差评数+1
                                    article.setGoodNumber(article.getGoodNumber() - 1);
                                    article.setBadNumber(article.getBadNumber() + 1);
                                }
                            }else{
                                ArticleLike newLike = new ArticleLike(null, u, article, ArticleConst.Like.BAD);
                                articleService.saveArticleLike(newLike);
                                // 文章差评数+1
                                article.setBadNumber(article.getBadNumber() + 1);
                            }

                            // 更新文章信息

                            article = articleService.saveArticle(article);
                            return Mono.just(Result.ok());
                        })
                        .orElse(Mono.just(Result.error(ArticleError.NON_EXIST_ID)))
                ).orElse(Mono.just(Result.error(UserError.NON_EXIST_ID)));
    }

    /**
     * 好评操作
     * @param account 账号
     * @param arrticleId 文章id
     *
     * @return
     */
    public Mono<Result<Object>> likeArticle(String account, Long arrticleId){

        //查找用户，不存在返回error
        return userRepository.findByAccountAndDeleted(account, Boolean.FALSE)
                //查找文章,不存在返回error
                .map(user -> articleService.getById(arrticleId)
                            .map(article -> {
                                //用户点赞该文章记录
                                Optional<ArticleLike> articleLike = articleService.getUserArticleLike(user.getId(),arrticleId);

                                //有记录则更新为好评
                                if(articleLike.isPresent()){
                                    ArticleLike dbLike = articleLike.get();
                                    if ((dbLike.getStatus() == ArticleConst.Like.BAD)){
                                        dbLike.setStatus(ArticleConst.Like.BAD);
                                        articleService.saveArticleLike(dbLike);

                                        //点赞数+1 差评-1
                                        article.setGoodNumber(article.getGoodNumber() + 1);
                                        article.setBadNumber(article.getBadNumber() - 1);
                                    }
                                }else{
                                    ArticleLike newLike = new ArticleLike(null, user, article, ArticleConst.Like.GOOD);
                                    articleService.saveArticleLike(newLike);
                                    // 点赞数+1
                                    article.setGoodNumber(article.getGoodNumber() + 1);
                                }
                                return  Mono.just(Result.ok());
                            })
                        .orElse(Mono.just(Result.error(ArticleError.NON_EXIST_ID)))
                ).orElse(Mono.just(Result.error(UserError.NON_EXIST_ID)));
    }
}
