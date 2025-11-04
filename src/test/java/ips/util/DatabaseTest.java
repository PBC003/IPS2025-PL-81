package ips.util;

import org.junit.Before;

public abstract class DatabaseTest {
    protected Database db;

    @Before
    public void setUp() {
        db = new Database();
        db.createDatabase(true);
    }
}
