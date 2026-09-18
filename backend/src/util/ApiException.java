package util;

public class ApiException extends RuntimeException {

    private final int statusHttp;

    public ApiException(int statusHttp, String mensagem) {
        super(mensagem);
        this.statusHttp = statusHttp;
    }

    public int getStatusHttp() {
        return statusHttp;
    }
}
