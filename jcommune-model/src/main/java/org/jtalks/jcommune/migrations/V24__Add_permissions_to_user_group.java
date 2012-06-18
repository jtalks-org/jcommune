package org.jtalks.jcommune.migrations;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public class V24__Add_permissions_to_user_group  /*implements JavaMigration */{
    private static String selectAllBranches="";
    private static String selectAllFromAclClass="select * from acl_class";
    private static String insertToAclClass="insert into acl_class(id, class) values(?,?)";
    private static String s="";

    //@Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
          jdbcTemplate.query("",new ResultSetExtractor<Object>() {
              @Override
              public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                  return null;
              }
          });
    }
}
