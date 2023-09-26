package cn.z.id;

/**
 * <h1>高性能雪花ID生成器异常类</h1>
 *
 * <p>
 * createDate 2023/07/27 09:53:07
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 2.7.0
 **/
public class IdException extends RuntimeException {

    /**
     * 高性能雪花ID生成器异常
     */
    public IdException() {
        super();
    }

    /**
     * 高性能雪花ID生成器异常
     *
     * @param message 信息
     */
    public IdException(String message) {
        super(message);
    }

}
