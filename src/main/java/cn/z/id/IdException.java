package cn.z.id;

/**
 * <h1>ID异常类</h1>
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
     * ID异常
     */
    public IdException() {
        super();
    }

    /**
     * ID异常
     *
     * @param message 信息
     */
    public IdException(String message) {
        super(message);
    }

}
