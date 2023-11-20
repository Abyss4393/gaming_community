package cn.abyss4393.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum BaseCode {
    SUCCESS(200, "请求成功"),
    REGISTER_SUCCESS(221, "注册成功"),
    LOGIN_SUCCESS(222, "登录成功"),
    DELETE(223, "删除成功"),
    MODIFY(224, "修改成功"),
    ADD(225, "添加成功"),
    SEARCH(226, "查询成功"),

    FILE_UPLOAD_SUCCESS(239, "上传文件成功"),
    FILE_DEL_SUCCESS(239, "删除文件成功"),

    UPVOTE(241, "点赞成功"),
    UPVOTE_FAIL(242, "点赞失败"),
    DISLIKE(243, "拉踩成功"),
    DISLIKE_FAIL(244, "拉踩失败"),

    POST(245, "发布成功"),
    POST_FAIL(246, "发布失败"),

    COLLECT(247, "收藏成功"),
    COLLECT_FAIL(248, "收藏失败"),

    UNKNOWN(500, "未知错误"),

    SYSTEM_ERROR(501, "系统错误,请查询运行日志"),

    VALUE_NULL(510, "空值错误"),

    SELECT_NULL_ARGS(511, "未查询到相关数据"),

    FILE_REPO_NOTFOUND(519, "服务器未找到存储仓库"),

    FILE_RESOURCE_NOTFOUND(520, "服务器未找到资源"),

    ERROR(230, "请求失败"),

    REGISTER_ERROR(231, "注册失败！请检查你的账号或密码是否正确"),

    LOGIN_ERROR(232, "登录失败！请检查你的账号密码是否正确"),

    MODIFY_ERROR(233, "修改失败"),

    DELETE_ERROR(234, "删除失败"),

    ADD_ERROR(235, "添加失败"),

    SEARCH_ERROR(236, "未查询到关键字的信息"),

    MISS_PARAMS(237, "缺少请求参数！"),

    FILE_URL_ERROR(238, "无法解析url路径"),

    FILE_UPLOAD_ERROR(239, "上传服务器失败"),

    FILE_DEL_ERROR(240, "删除文件失败"),

    IMAGE_URL_NULL(249, "空的图片url地址"),

    IMAGE_NOT_PURE(250, "未处理的图片url错误"),

    ARGS_ERROR(260, "请求参数错误，请检查参数"),

    REGISTER_REPETITION(261, "注册用户重复"),

    LOGIN_NO(262, "该用户未登录"),

    COMMENT_SUCCESS(263, "评论成功"),
    COMMENT_ERROR(264, "评论失败"),

    ERROR_REQUEST(400, "错误请求"),

    UNAUTHORIZED(401, "请求未授权，请登录后重试"),

    FORBIDDEN(403, "服务器拒绝请求"),

    NOT_FOUND(404, "请求的资源不存在"),

    TIMEOUT(408, "请求超时");

    final int code;
    final String msg;

    BaseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
