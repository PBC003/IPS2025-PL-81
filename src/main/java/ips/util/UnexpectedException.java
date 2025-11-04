package ips.util;

@SuppressWarnings("serial")
public class UnexpectedException extends RuntimeException {
    public UnexpectedException(Throwable e) {
        super(e);
    }

    public UnexpectedException(String s) {
        super(s);
    }
}
