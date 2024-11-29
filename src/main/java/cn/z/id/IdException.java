package cn.z.id;

/**
 * <h1>高性能雪花ID生成器异常</h1>
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
     * @param message 详细信息
     */
    public IdException(String message) {
        super(message);
    }

    /**
     * 高性能雪花ID生成器异常
     *
     * @param message 详细信息
     * @param cause   原因
     */
    public IdException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 高性能雪花ID生成器异常
     *
     * @param cause 原因
     */
    public IdException(Throwable cause) {
        super(cause);
    }

    /**
     * 高性能雪花ID生成器异常
     *
     * @param message            详细信息
     * @param cause              原因
     * @param enableSuppression  是否启用抑制
     * @param writableStackTrace 堆栈跟踪是否为可写的
     */
    protected IdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
