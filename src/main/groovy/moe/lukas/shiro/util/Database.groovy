package moe.lukas.shiro.util

import groovy.transform.CompileStatic

import java.sql.*

@CompileStatic
class Database {
    /**
     * Singleton cache
     */
    private static volatile Database instance

    /**
     * Connection cache
     */
    protected volatile Connection connection

    /**
     * Constructor
     *
     * @param config
     */
    private Database(HashMap config) {
        connection = DriverManager.getConnection(
                "jdbc:mysql://${config.host}/${config.db}?user=${config.user}&password=${config.pass}"
        )
    }

    /**
     * Instance lazy-creator
     *
     * @param config
     */
    static void createInstance(HashMap config) {
        instance = new Database(config)
    }

    /**
     * Singleton getter
     *
     * @return
     */
    static Database getInstance() {
        return instance
    }

    /**
     * Execute a query
     * Provide $data in this format:
     *
     * [myCoolVariable: MysqlType.TYPE, ...]
     *
     * @param sql
     * @param data
     * @return
     */
    List<Map<String, Object>> query(String sql, List data = []) {
        PreparedStatement preparedStatement = connection.prepareStatement(sql)

        if (data.size() > 0) {
            int i = 1
            data.each {
                preparedStatement.setObject(i, it)
                i++
            }
        }

        preparedStatement.execute()
        ResultSet resultSet = preparedStatement.resultSet

        return convertResultSet(resultSet)
    }

    /**
     * Converts a resultset to List<HashMap<RowName, Value>>
     *
     * @param resultSet
     * @return
     */
    @SuppressWarnings(["ChangeToOperator", "GrMethodMayBeStatic"])
    List<Map<String, Object>> convertResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            return null
        }

        ResultSetMetaData metaData = resultSet.getMetaData()
        int cols = metaData.getColumnCount()

        ArrayList list = new ArrayList()

        while (resultSet.next()) {
            HashMap row = new HashMap(cols)

            (1..cols).each { int i ->
                row[metaData.getColumnName(i)] = resultSet.getObject(i)
            }

            list << row
        }

        resultSet.close()

        return list
    }

    def get(String table, String key = null, fallback = null) {
        try {
            if (fallback != null) {
                this.set(table, key, fallback)
                return fallback
            }

            List<Map<String, Object>> result = this.query(
                    "SELECT `key`, `value` FROM `shiro`.`$table`" +
                            (key == null ? "" : " WHERE `key` = '$key'") +
                            ";"
            )

            if (result.size() > 0) {
                return result[0]["value"]
            } else {
                return null
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    void set(String table, String key, value) {
        try {
            this.query("INSERT INTO `shiro`.`$table` (`key`, `value`) VALUES ('$key', '$value');")
        } catch (SQLException e) {
            switch (e.SQLState) {
                case "1062":
                case "23000":
                    this.query("UPDATE `shiro`.`$table` SET `value`='$value' WHERE `key`='$key';")
                    break

                default:
                    e.printStackTrace()
                    break

            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}
