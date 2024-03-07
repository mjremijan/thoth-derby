import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Michael Remijan mjremijan@yahoo.com @mjremijan
 */
public class SelectForUpdateTest {

    public SelectForUpdateTest() {
    }

    protected static Connection conn;
    protected static String feedId = "feed_id-001";
    
    @BeforeAll
    public static void setUp() throws Exception {
        conn = DriverManager.getConnection(
            "jdbc:derby:memory:foo;create=true", "sa", "sa"
        );
        
        String create = "create table rss_entry_history (\n" +
            "      feed_id         varchar(200) not null\n" +
            "    , entry_id        varchar(200) not null\n" +
            "    , published_on    timestamp not null\n" +
            "    , last_found_on   date not null\n" +
            "    , primary key (feed_id, entry_id)\n" +
            ")"
        ;
        
        PreparedStatement stmt
            = conn.prepareStatement(create);
        
        stmt.execute();
        
        String insert = "insert into rss_entry_history (\n" +
            "      feed_id \n" +
            "    , entry_id \n" +
            "    , published_on \n" +
            "    , last_found_on \n" +            
            " ) values ( \n " +
            "      ? \n" +
            "    , ? \n" +
            "    , ? \n" +
            "    , ? \n" + 
            " )"
        ;
        
        stmt = conn.prepareStatement(insert);
        stmt.setString(1, feedId);
        stmt.setString(2, "entry_id-001");
        stmt.setTimestamp(3, Timestamp.valueOf("2024-03-06 21:28:16"));
        stmt.setDate(4, Date.valueOf("2024-03-06"));
        
        stmt.execute();
        stmt.close();        
        conn.commit();
    }

    @Test
    public void test_select_that_the_row_is_there() throws Exception {
        
        ResultSet rs
            = conn.createStatement().executeQuery(
                 "select feed_id, entry_id, published_on, "
                + "last_found_on from rss_entry_history ");
        rs.next();
        System.out.printf(
            "feed_id=\"%s\", entry_id=\"%s\", published_on=\"%s\", last_found_on=\"%s\"%n"
            , rs.getString(1)
            , rs.getString(2)
            , rs.getTimestamp(3).toString()
            , rs.getDate(4).toString()
        );      
        Assertions.assertEquals(feedId, rs.getString(1));
        rs.close();
    }
    
    @Test
    public void test_select_for_update() throws Exception {
        
        ResultSet rs
            = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE).executeQuery(
                  " select feed_id, entry_id, published_on, last_found_on "
                + " from rss_entry_history "                
                + " where "
                + " feed_id is not null "
                + " for update of last_found_on "
                + " ");
        
        rs.next();            
        Assertions.assertEquals("2024-03-06", rs.getDate(4).toString());
        rs.updateDate(4, Date.valueOf("2024-04-04"));
        rs.updateRow();
        rs.close();
        
        rs = conn.createStatement().executeQuery(
                 "select feed_id, entry_id, published_on, "
                + "last_found_on from rss_entry_history ");
        rs.next();
        Assertions.assertEquals("2024-04-04", rs.getDate(4).toString());;
        rs.close();
    }

}
