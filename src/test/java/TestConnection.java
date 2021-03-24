/*
 * Copyright 2021 The Ferris Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Michael Remijan mjremijan@yahoo.com @mjremijan
 */
public class TestConnection {

    public TestConnection() {
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void test_driver_version() throws Exception {
        Connection conn = DriverManager.getConnection(
            "jdbc:derby:memory:foo;create=true", "sa", "sa"
        );
        Assertions.assertEquals(
            "10.15.2.0 - (1873585)", conn.getMetaData().getDriverVersion()
        );
    }

    @Test
    public void test_embedded_10_14_2_0() throws Exception {
        // ij> connect 'jdbc:derby:D:\\Projects\\thoth-derby\\src\\test\\databases\\10.14.2.0.db;create=true' user 'sa' password 'sa';
        File f = new File("./src/test/databases/10.14.2.0.db").getCanonicalFile();
        Connection conn = DriverManager.getConnection(
            String.format("jdbc:derby:%s", f.getPath()), "sa", "sa"
        );
        ResultSet rs
            = conn.createStatement().executeQuery("values syscs_util.syscs_get_database_property('DataDictionaryVersion')");
        rs.next();
        Assertions.assertEquals(
            "10.14", rs.getString(1)
        );
    }

    @Test
    public void test_embedded_10_13_1_1() throws Exception {
        // ij> connect 'jdbc:derby:D:\\Projects\\thoth-derby\\src\\test\\databases\\10.13.1.1.db;create=true' user 'sa' password 'sa';
        File f = new File("./src/test/databases/10.13.1.1.db").getCanonicalFile();
        Connection conn = DriverManager.getConnection(
            String.format("jdbc:derby:%s", f.getPath()), "sa", "sa"
        );
        ResultSet rs
            = conn.createStatement().executeQuery("values syscs_util.syscs_get_database_property('DataDictionaryVersion')");
        rs.next();
        Assertions.assertEquals(
            "10.13", rs.getString(1)
        );
    }
}
