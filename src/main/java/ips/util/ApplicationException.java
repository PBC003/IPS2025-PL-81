package ips.util;

@SuppressWarnings("serial")
public class ApplicationException extends RuntimeException {
    public ApplicationException(Throwable e) {
        super(e);
    }

    public ApplicationException(String s) {
        super(s);
    }
}